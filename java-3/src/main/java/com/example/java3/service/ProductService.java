// 声明当前类所在包路径
package com.example.java3.service;

// 导入商品请求 DTO
import com.example.java3.dto.ProductRequest;
// 导入商品响应 DTO
import com.example.java3.dto.ProductResponse;
// 导入 model 包下全部实体类
import com.example.java3.model.*;
// 导入 repository 包下全部仓储接口
import com.example.java3.repository.*;
// 导入 Spring Data 分页 Page
import org.springframework.data.domain.Page;
// 导入分页请求对象
import org.springframework.data.domain.PageRequest;
// 导入排序对象
import org.springframework.data.domain.Sort;
// 导入 @Service 注解
import org.springframework.stereotype.Service;

// 导入 HashMap
import java.util.HashMap;
// 导入 List 集合
import java.util.List;
// 导入 Map 接口
import java.util.Map;

/**
 * 商品服务
 * <p>
 * 负责商品发布、查询、审核、点赞收藏等核心业务。
 * 商品发布后默认 PENDING 状态，需管理员审核通过后才在前台展示。
 * </p>
 */
// 标识为业务层组件，由 Spring 容器管理为单例
@Service
public class ProductService {

    // 商品仓储，处理商品表 CRUD
    private final ProductRepository productRepository;
    // 分类仓储，用于校验分类存在和补充分类名
    private final ProductCategoryRepository categoryRepository;
    // 用户仓储，用于补充卖家昵称
    private final UserRepository userRepository;
    // 收藏仓储，处理商品收藏记录
    private final FavoriteRepository favoriteRepository;
    // 点赞仓储，处理商品点赞记录
    private final ProductLikeRepository likeRepository;

    // 构造方法注入五个仓储 Bean，便于单元测试 mock
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
        // 校验分类存在，避免脏数据
        categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        // 构造商品实体，填入标题、描述、价格、原价、图片、成色、分类 ID、卖家 ID
        Product p = new Product(req.getTitle(), req.getDescription(), req.getPrice(),
                req.getOriginalPrice(), req.getImages(), req.getConditionLevel(),
                req.getCategoryId(), sellerId);
        // 持久化到数据库，默认状态为 PENDING 等待审核
        productRepository.save(p);
        // 转换为响应 DTO 返回
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
        // 构造分页请求：按创建时间降序，保证最新商品展示在前
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            // 关键词搜索：调用仓储的模糊查询方法
            products = productRepository.searchByKeyword(keyword, ProductAuditStatus.APPROVED, false, pageable);
        } else if (categoryId != null) {
            // 按分类筛选：只查指定分类下已审核通过且未售出的商品
            products = productRepository.findByAuditStatusAndSoldAndCategoryId(
                    ProductAuditStatus.APPROVED, false, categoryId, pageable);
        } else {
            // 无筛选条件：查询全部已审核通过且未售出的商品
            products = productRepository.findByAuditStatusAndSold(ProductAuditStatus.APPROVED, false, pageable);
        }
        // 将商品实体分页转换为响应 DTO 分页
        return products.map(this::toResponse);
    }

    /**
     * 查询商品详情
     *
     * @param id 商品 ID
     * @return 商品响应
     */
    public ProductResponse detail(Long id) {
        // 根据 ID 查询商品
        Product p = productRepository.findById(id)
                // 商品不存在则抛异常
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        // 转换为响应 DTO
        return toResponse(p);
    }

    /**
     * 查询我的发布
     *
     * @param sellerId 卖家 ID
     * @return 商品列表
     */
    public List<ProductResponse> myProducts(Long sellerId) {
        // 按卖家 ID 查询并按创建时间降序，最近发布在前
        return productRepository.findBySellerIdOrderByCreatedAtDesc(sellerId)
                // 转 Stream
                .stream()
                // 逐个映射为响应 DTO
                .map(this::toResponse)
                // 收集为 List
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
        // 根据 ID 查询商品
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        // 设置审核状态（APPROVED / REJECTED）
        p.setAuditStatus(status);
        // 设置审核备注，便于卖家了解原因
        p.setAuditRemark(remark);
        // 持久化
        productRepository.save(p);
    }

    /**
     * 管理员下架商品（设为 REJECTED）
     *
     * @param id 商品 ID
     */
    public void takeDown(Long id) {
        // 根据 ID 查询商品
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        // 强制设为 REJECTED 状态，前台不再展示
        p.setAuditStatus(ProductAuditStatus.REJECTED);
        // 设置下架备注
        p.setAuditRemark("管理员强制下架");
        // 持久化
        productRepository.save(p);
    }

    /**
     * 管理员查询待审核商品
     *
     * @return 商品列表
     */
    public List<ProductResponse> pendingList() {
        // 查询所有 PENDING 状态商品，按创建时间降序
        return productRepository.findByAuditStatusOrderByCreatedAtDesc(ProductAuditStatus.PENDING)
                // 转 Stream
                .stream()
                // 映射为响应 DTO
                .map(this::toResponse)
                // 收集为 List
                .toList();
    }

    /**
     * 查询全部商品（后台）
     *
     * @return 商品列表
     */
    public List<ProductResponse> all() {
        // 查询全部商品，按创建时间降序
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                // 转 Stream
                .stream()
                // 映射为响应 DTO
                .map(this::toResponse)
                // 收集为 List
                .toList();
    }

    /**
     * 删除商品（仅卖家本人或管理员可调用，权限由 Controller 控制）
     *
     * @param id 商品 ID
     */
    public void delete(Long id) {
        // 调用 JPA 默认方法按 ID 删除商品
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
        // 查询是否已存在点赞记录
        if (likeRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            // 已点赞则取消，删除点赞记录
            likeRepository.findByUserIdAndProductId(userId, productId)
                    .ifPresent(likeRepository::delete);
            // 返回 false 表示当前未点赞
            return false;
        }
        // 未点赞则新增点赞记录
        likeRepository.save(new ProductLike(userId, productId));
        // 返回 true 表示当前已点赞
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
        // 查询是否已存在收藏记录
        if (favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            // 已收藏则取消，删除收藏记录
            favoriteRepository.findByUserIdAndProductId(userId, productId)
                    .ifPresent(favoriteRepository::delete);
            // 返回 false 表示当前未收藏
            return false;
        }
        // 未收藏则新增收藏记录
        favoriteRepository.save(new Favorite(userId, productId));
        // 返回 true 表示当前已收藏
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
        // 构造结果 Map
        Map<String, Boolean> map = new HashMap<>();
        // 查询点赞记录是否存在，转为 boolean
        map.put("liked", likeRepository.findByUserIdAndProductId(userId, productId).isPresent());
        // 查询收藏记录是否存在，转为 boolean
        map.put("favorited", favoriteRepository.findByUserIdAndProductId(userId, productId).isPresent());
        // 返回状态 Map
        return map;
    }

    /**
     * 实体转响应 DTO（补充分类名、卖家名、点赞/收藏数）
     *
     * @param p 商品实体
     * @return 响应 DTO
     */
    public ProductResponse toResponse(Product p) {
        // 先基于商品实体构造响应 DTO
        ProductResponse resp = new ProductResponse(p);
        // 补充分类名：存在则设置，避免分类被删除时崩溃
        categoryRepository.findById(p.getCategoryId())
                .ifPresent(c -> resp.setCategoryName(c.getName()));
        // 补充卖家昵称：存在则设置
        userRepository.findById(p.getSellerId())
                .ifPresent(u -> resp.setSellerName(u.getNickname()));
        // 补充点赞数：统计该商品的点赞总数
        resp.setLikeCount(likeRepository.countByProductId(p.getId()));
        // 补充收藏数：统计该商品的收藏总数
        resp.setFavoriteCount(favoriteRepository.countByProductId(p.getId()));
        // 返回完整响应 DTO
        return resp;
    }

    /**
     * 根据订单完成时标记商品已售出
     *
     * @param productId 商品 ID
     */
    public void markSold(Long productId) {
        // 商品存在时设置 sold 标志为 true，前台不再展示
        productRepository.findById(productId).ifPresent(p -> {
            // 标记为已售出
            p.setSold(true);
            // 持久化
            productRepository.save(p);
        });
    }
}
