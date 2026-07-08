package com.example.java3.service;

import com.example.java3.dto.ProductRequest;
import com.example.java3.dto.ProductResponse;
import com.example.java3.model.*;
import com.example.java3.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务
 * <p>
 * 负责商品发布、查询、审核、点赞收藏等核心业务。
 * 商品发布后默认 PENDING 状态，需管理员审核通过后才在前台展示。
 * </p>
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductLikeRepository likeRepository;

    public ProductService(ProductRepository productRepository,
                          ProductCategoryRepository categoryRepository,
                          UserRepository userRepository,
                          FavoriteRepository favoriteRepository,
                          ProductLikeRepository likeRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.likeRepository = likeRepository;
    }

    /**
     * 发布商品（学生）
     *
     * @param sellerId 卖家 ID
     * @param req      商品请求
     * @return 商品响应
     */
    public ProductResponse publish(Long sellerId, ProductRequest req) {
        // 校验分类存在
        categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        Product p = new Product(req.getTitle(), req.getDescription(), req.getPrice(),
                req.getOriginalPrice(), req.getImages(), req.getConditionLevel(),
                req.getCategoryId(), sellerId);
        productRepository.save(p);
        return toResponse(p);
    }

    /**
     * 分页查询已审核通过且未售出的商品（前台展示）
     *
     * @param page    页码（0 起）
     * @param size    每页数量
     * @param categoryId 分类 ID（可为 null）
     * @param keyword  关键词（可为 null）
     * @return 商品分页
     */
    public Page<ProductResponse> listApproved(int page, int size, Long categoryId, String keyword) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            // 关键词搜索
            products = productRepository.searchByKeyword(keyword, ProductAuditStatus.APPROVED, false, pageable);
        } else if (categoryId != null) {
            // 按分类筛选
            products = productRepository.findByAuditStatusAndSoldAndCategoryId(
                    ProductAuditStatus.APPROVED, false, categoryId, pageable);
        } else {
            // 全部
            products = productRepository.findByAuditStatusAndSold(ProductAuditStatus.APPROVED, false, pageable);
        }
        return products.map(this::toResponse);
    }

    /**
     * 查询商品详情
     *
     * @param id 商品 ID
     * @return 商品响应
     */
    public ProductResponse detail(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        return toResponse(p);
    }

    /**
     * 查询我的发布
     *
     * @param sellerId 卖家 ID
     * @return 商品列表
     */
    public List<ProductResponse> myProducts(Long sellerId) {
        return productRepository.findBySellerIdOrderByCreatedAtDesc(sellerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 管理员审核商品
     *
     * @param id     商品 ID
     * @param status 审核结果
     * @param remark 备注
     */
    public void audit(Long id, ProductAuditStatus status, String remark) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        p.setAuditStatus(status);
        p.setAuditRemark(remark);
        productRepository.save(p);
    }

    /**
     * 管理员下架商品（设为 REJECTED）
     *
     * @param id 商品 ID
     */
    public void takeDown(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        p.setAuditStatus(ProductAuditStatus.REJECTED);
        p.setAuditRemark("管理员强制下架");
        productRepository.save(p);
    }

    /**
     * 管理员查询待审核商品
     *
     * @return 商品列表
     */
    public List<ProductResponse> pendingList() {
        return productRepository.findByAuditStatusOrderByCreatedAtDesc(ProductAuditStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 查询全部商品（后台）
     *
     * @return 商品列表
     */
    public List<ProductResponse> all() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 删除商品（仅卖家本人或管理员可调用，权限由 Controller 控制）
     *
     * @param id 商品 ID
     */
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 点赞商品（已点赞则取消）
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 当前是否已点赞
     */
    public boolean toggleLike(Long userId, Long productId) {
        if (likeRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            // 已点赞则取消
            likeRepository.findByUserIdAndProductId(userId, productId)
                    .ifPresent(likeRepository::delete);
            return false;
        }
        likeRepository.save(new ProductLike(userId, productId));
        return true;
    }

    /**
     * 收藏商品（已收藏则取消）
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 当前是否已收藏
     */
    public boolean toggleFavorite(Long userId, Long productId) {
        if (favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            favoriteRepository.findByUserIdAndProductId(userId, productId)
                    .ifPresent(favoriteRepository::delete);
            return false;
        }
        favoriteRepository.save(new Favorite(userId, productId));
        return true;
    }

    /**
     * 查询用户是否已点赞/收藏某商品
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     * @return 状态 map：liked / favorited
     */
    public Map<String, Boolean> userInteractions(Long userId, Long productId) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("liked", likeRepository.findByUserIdAndProductId(userId, productId).isPresent());
        map.put("favorited", favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent());
        return map;
    }

    /**
     * 实体转响应 DTO（补充分类名、卖家名、点赞/收藏数）
     *
     * @param p 商品实体
     * @return 响应 DTO
     */
    public ProductResponse toResponse(Product p) {
        ProductResponse resp = new ProductResponse(p);
        // 补充分类名
        categoryRepository.findById(p.getCategoryId())
                .ifPresent(c -> resp.setCategoryName(c.getName()));
        // 补充卖家昵称
        userRepository.findById(p.getSellerId())
                .ifPresent(u -> resp.setSellerName(u.getNickname()));
        // 补充点赞数、收藏数
        resp.setLikeCount(likeRepository.countByProductId(p.getId()));
        resp.setFavoriteCount(favoriteRepository.countByProductId(p.getId()));
        return resp;
    }

    /**
     * 根据订单完成时标记商品已售出
     *
     * @param productId 商品 ID
     */
    public void markSold(Long productId) {
        productRepository.findById(productId).ifPresent(p -> {
            p.setSold(true);
            productRepository.save(p);
        });
    }
}
