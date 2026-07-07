// 声明包路径
package com.example.java2.service;

// 导入 DTO 与实体类
import com.example.java2.dto.ArticleListItem;
import com.example.java2.dto.ArticleRequest;
import com.example.java2.dto.ArticleResponse;
import com.example.java2.model.Article;
import com.example.java2.model.ArticleLike;
import com.example.java2.model.Favorite;
import com.example.java2.repository.ArticleLikeRepository;
import com.example.java2.repository.ArticleRepository;
import com.example.java2.repository.FavoriteRepository;

// 导入 Spring 分页与异常
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// 导入集合类
import java.util.List;

/**
 * 文章业务服务
 * <p>
 * 负责文章 CRUD、列表分页、关键词搜索、详情查询（含阅读量自增、收藏/点赞状态填充）、
 * 用户收藏/点赞/取消收藏/取消点赞等业务。
 * </p>
 */
@Service
public class ArticleService {

    // final 修饰：所有依赖在构造完成后不可变，满足线程安全发布与不可变约束
    private final ArticleRepository repository;
    private final FavoriteRepository favoriteRepository;
    private final ArticleLikeRepository likeRepository;
    private final CategoryService categoryService;

    /**
     * 构造方法注入（Spring 4.3+ 单构造器自动注入，无需 @Autowired）
     * 为何不用字段注入：构造注入可声明 final、便于单元测试、能在启动期暴露循环依赖
     */
    public ArticleService(ArticleRepository repository,
                          FavoriteRepository favoriteRepository,
                          ArticleLikeRepository likeRepository,
                          CategoryService categoryService) {
        this.repository = repository;
        this.favoriteRepository = favoriteRepository;
        this.likeRepository = likeRepository;
        this.categoryService = categoryService;
    }

    /**
     * 前台文章列表分页查询
     *
     * @param categoryId 分类 ID（null 表示不限分类）
     * @param keyword    关键词（null 或空表示不限）
     * @param page       页码（0 起）
     * @param size       每页条数
     * @return 文章列表项分页
     */
    public Page<ArticleListItem> listForUser(Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles;
        // 查询条件优先级：关键词 > 分类 > 全量；关键词优先保证搜索体验，避免被分类过滤干扰
        if (keyword != null && !keyword.isBlank()) {
            // trim 防止前后空格导致 LIKE 匹配不到
            articles = repository.searchByKeyword(keyword.trim(), pageable);
        } else if (categoryId != null) {
            // 仅返回已发布文章，未发布草稿不对外暴露
            articles = repository.findByCategoryIdAndPublishedTrue(categoryId, pageable);
        } else {
            articles = repository.findByPublishedTrue(pageable);
        }
        // 转换为列表项 DTO，并填充分类名（避免前端二次请求分类接口）
        return articles.map(a -> ArticleListItem.from(a, categoryService.getNameById(a.getCategoryId())));
    }

    /**
     * 文章详情查询（前台）
     * <p>每次访问详情页自动累加阅读量，并填充当前用户的收藏/点赞状态。</p>
     *
     * @param id     文章 ID
     * @param userId 当前用户 ID（未登录为 null）
     * @return 文章详情响应
     * @throws ResponseStatusException 文章不存在或未发布
     */
    public ArticleResponse getDetail(Long id, Long userId) {
        Article article = repository.findById(id).orElse(null);
        // 未发布文章前台不可见，避免草稿泄露；用 404 而非 403 防止探测文章存在性
        if (article == null || !article.isPublished()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在");
        }
        // 阅读量 +1：每次访问详情即累加，用于热度排序与统计（可接受少量并发误差）
        article.setViewCount(article.getViewCount() + 1);
        repository.save(article);
        // 填充当前用户的收藏/点赞状态：未登录用户统一为 false，避免 NPE
        boolean favorited = false;
        boolean liked = false;
        if (userId != null) {
            favorited = favoriteRepository.findByUserIdAndArticleId(userId, id).isPresent();
            liked = likeRepository.findByUserIdAndArticleId(userId, id).isPresent();
        }
        return ArticleResponse.from(article, categoryService.getNameById(article.getCategoryId()), favorited, liked);
    }

    /**
     * 后台文章列表分页查询（含未发布）
     */
    public Page<ArticleListItem> listForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // 后台需看到草稿，故 findAll 不加 published 过滤
        return repository.findAll(pageable)
                .map(a -> ArticleListItem.from(a, categoryService.getNameById(a.getCategoryId())));
    }

    /**
     * 后台根据 ID 查询文章实体（编辑回显用）
     */
    public Article getById(Long id) {
        // 编辑回显需要草稿也能查到，故不做 published 过滤
        return repository.findById(id).orElse(null);
    }

    /**
     * 新增文章（后台）
     *
     * @param req 文章请求
     * @return 新建文章实体
     * @throws IllegalArgumentException 分类不存在
     */
    public Article create(ArticleRequest req) {
        // 分类存在性校验：避免文章挂到不存在的分类下导致外键孤儿
        if (categoryService.getById(req.categoryId()) == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        Article article = new Article(req.categoryId(), req.title(), req.summary(), req.content());
        // published 为 null 表示使用实体默认值（未发布草稿），显式传值则覆盖
        if (req.published() != null) {
            article.setPublished(req.published());
        }
        return repository.save(article);
    }

    /**
     * 更新文章（后台）
     *
     * @param id  文章 ID
     * @param req 文章请求
     * @return 更新后的文章实体
     * @throws IllegalArgumentException 文章或分类不存在
     */
    public Article update(Long id, ArticleRequest req) {
        Article article = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        // 改分类时也要校验新分类存在，避免迁移到无效分类
        if (categoryService.getById(req.categoryId()) == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        article.setCategoryId(req.categoryId());
        article.setTitle(req.title());
        article.setSummary(req.summary());
        article.setContent(req.content());
        // published 为 null 表示不修改发布状态（保留原值），避免误把草稿发布或下线
        if (req.published() != null) {
            article.setPublished(req.published());
        }
        return repository.save(article);
    }

    /**
     * 删除文章（后台）
     * <p>同时清理该文章的收藏与点赞记录，避免孤儿数据。</p>
     *
     * @param id 文章 ID
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("文章不存在");
        }
        // 关联数据清理：删除文章前必须先清理 favorite 与 like 表
        // 为何要清理：这两张表通过 articleId 软关联（无外键约束），不清理会留下指向已删文章的孤儿记录
        //   孤儿记录会导致收藏列表查询时 findById 返回 null，且冗余计数无法收回
        // 注意：收藏/点赞表通过 articleId 关联，逐条删除
        // 先全量过滤再批量删，因 Repository 无按 articleId 直接删除方法，用内存过滤兜底
        List<Favorite> favorites = favoriteRepository.findAll().stream()
                .filter(f -> f.getArticleId().equals(id)).toList();
        favoriteRepository.deleteAll(favorites);
        List<ArticleLike> likes = likeRepository.findAll().stream()
                .filter(l -> l.getArticleId().equals(id)).toList();
        likeRepository.deleteAll(likes);
        repository.deleteById(id);
    }

    /**
     * 收藏文章（前台用户）
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     * @return true 收藏成功，false 已收藏过（幂等）
     * @throws IllegalArgumentException 文章不存在
     */
    public boolean favorite(Long userId, Long articleId) {
        Article article = repository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        // 幂等：已收藏则直接返回，避免重复插入违反唯一约束
        // 为何要幂等：前端可能重复点击、网络重试，若不校验会插入重复记录导致 count 失真
        if (favoriteRepository.findByUserIdAndArticleId(userId, articleId).isPresent()) {
            return false;
        }
        favoriteRepository.save(new Favorite(userId, articleId));
        // 同步冗余计数：避免列表页 N+1 查询收藏数，用字段冗余换取查询性能
        // 为何在 Article 表维护 favoriteCount：列表页每页 N 条都需展示收藏数，
        //   若每次都 count(*) 会有 N+1 查询问题；冗余字段以写入时多一次 update 换取读取时 O(1)
        article.setFavoriteCount(article.getFavoriteCount() + 1);
        repository.save(article);
        return true;
    }

    /**
     * 取消收藏
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     * @return true 取消成功，false 原本未收藏
     */
    public boolean unfavorite(Long userId, Long articleId) {
        Article article = repository.findById(articleId).orElse(null);
        if (article == null) {
            return false;
        }
        if (favoriteRepository.findByUserIdAndArticleId(userId, articleId).isEmpty()) {
            return false;
        }
        favoriteRepository.deleteByUserIdAndArticleId(userId, articleId);
        // 同步冗余计数（避免减为负数）：并发场景下 count 可能已偏移，Math.max 兜底
        article.setFavoriteCount(Math.max(0, article.getFavoriteCount() - 1));
        repository.save(article);
        return true;
    }

    /**
     * 点赞文章
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     * @return true 点赞成功，false 已点赞过（幂等）
     */
    public boolean like(Long userId, Long articleId) {
        Article article = repository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        // 幂等校验：防止刷点赞，一人一赞
        // 为何要幂等：与收藏同理，防止前端重复提交或脚本刷量导致点赞数虚高
        if (likeRepository.findByUserIdAndArticleId(userId, articleId).isPresent()) {
            return false;
        }
        likeRepository.save(new ArticleLike(userId, articleId));
        // 同步冗余计数，便于列表页直接展示热度
        // 为何在 Article 表维护 likeCount：与 favoriteCount 同理，避免列表页 N+1 count 查询
        article.setLikeCount(article.getLikeCount() + 1);
        repository.save(article);
        return true;
    }

    /**
     * 取消点赞
     *
     * @param userId    用户 ID
     * @param articleId 文章 ID
     * @return true 取消成功，false 原本未点赞
     */
    public boolean unlike(Long userId, Long articleId) {
        Article article = repository.findById(articleId).orElse(null);
        if (article == null) {
            return false;
        }
        if (likeRepository.findByUserIdAndArticleId(userId, articleId).isEmpty()) {
            return false;
        }
        likeRepository.deleteByUserIdAndArticleId(userId, articleId);
        // 同步冗余计数（避免减为负数）：并发兜底
        article.setLikeCount(Math.max(0, article.getLikeCount() - 1));
        repository.save(article);
        return true;
    }

    /**
     * 查询用户收藏的文章列表（个人中心用）
     */
    public List<ArticleListItem> listFavorites(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                // 文章可能已被删除，orElse(null) 后需 filter 过滤掉 null
                .map(f -> repository.findById(f.getArticleId()).orElse(null))
                // 仅展示已发布文章：草稿被作者收藏后下线不应再对收藏者可见
                .filter(a -> a != null && a.isPublished())
                .map(a -> ArticleListItem.from(a, categoryService.getNameById(a.getCategoryId())))
                .toList();
    }

    /**
     * 统计已发布文章数（仪表板用）
     */
    public long countPublished() {
        // 仅统计已发布，草稿不计入内容产出指标
        return repository.countByPublishedTrue();
    }

    /**
     * 全平台阅读量总和（仪表板用）
     */
    public long totalViewCount() {
        // 走 DB sum 聚合，避免全表加载到内存累加
        return repository.sumViewCount();
    }
}
