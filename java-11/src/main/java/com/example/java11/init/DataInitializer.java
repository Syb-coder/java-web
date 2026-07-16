package com.example.java11.init;  // 初始化层包，存放启动时的种子数据逻辑

import com.example.java11.model.AdminRole;  // 管理员角色枚举
import com.example.java11.model.AdminUser;  // 管理员实体
import com.example.java11.model.News;  // 新闻实体
import com.example.java11.model.NewsCategory;  // 新闻分类枚举
import com.example.java11.model.Post;  // 帖子实体
import com.example.java11.model.PostStatus;  // 帖子状态枚举
import com.example.java11.model.Section;  // 板块实体
import com.example.java11.model.SensitiveWord;  // 敏感词实体
import com.example.java11.model.SystemConfig;  // 系统配置实体
import com.example.java11.model.User;  // 普通用户实体
import com.example.java11.model.UserStatus;  // 用户状态枚举
import com.example.java11.model.Work;  // 作品实体
import com.example.java11.model.WorkType;  // 作品类型枚举
import com.example.java11.repository.AdminUserRepository;  // 管理员仓储
import com.example.java11.repository.NewsRepository;  // 新闻仓储
import com.example.java11.repository.PostRepository;  // 帖子仓储
import com.example.java11.repository.SectionRepository;  // 板块仓储
import com.example.java11.repository.SensitiveWordRepository;  // 敏感词仓储
import com.example.java11.repository.SystemConfigRepository;  // 系统配置仓储
import com.example.java11.repository.UserRepository;  // 用户仓储
import com.example.java11.repository.WorkRepository;  // 作品仓储
import org.springframework.boot.CommandLineRunner;  // 启动回调接口
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  // 密码编码器
import org.springframework.stereotype.Component;  // Spring 组件注解

import java.time.LocalDateTime;  // 时间类型

/**
 * 种子数据初始化器
 * <p>
 * 应用启动时自动初始化演示数据，仅在对应表为空时执行，避免重复插入。
 * 初始化内容包括：管理员账号、测试用户、板块分区、新闻资讯、作品库、示例帖子、系统配置、敏感词。
 * </p>
 * <p>
 * 默认账号：
 * <ul>
 *   <li>管理员：admin / admin123</li>
 *   <li>版主：moderator / mod123</li>
 *   <li>用户：user1 / user123</li>
 *   <li>用户：user2 / user123</li>
 * </ul>
 * </p>
 */
@Component  // 注册为 Spring Bean，启动后自动执行 run 方法
public class DataInitializer implements CommandLineRunner {  // 启动回调

    /** BCrypt 密码编码器实例 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;
    private final SectionRepository sectionRepository;
    private final NewsRepository newsRepository;
    private final WorkRepository workRepository;
    private final PostRepository postRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final SensitiveWordRepository sensitiveWordRepository;

    /**
     * 构造器注入所有仓储依赖
     */
    public DataInitializer(AdminUserRepository adminUserRepository,
                           UserRepository userRepository,
                           SectionRepository sectionRepository,
                           NewsRepository newsRepository,
                           WorkRepository workRepository,
                           PostRepository postRepository,
                           SystemConfigRepository systemConfigRepository,
                           SensitiveWordRepository sensitiveWordRepository) {
        this.adminUserRepository = adminUserRepository;
        this.userRepository = userRepository;
        this.sectionRepository = sectionRepository;
        this.newsRepository = newsRepository;
        this.workRepository = workRepository;
        this.postRepository = postRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.sensitiveWordRepository = sensitiveWordRepository;
    }

    @Override  // 重写启动回调
    public void run(String... args) {  // 应用启动后自动执行
        initAdmins();  // 初始化管理员
        initUsers();  // 初始化测试用户
        initSections();  // 初始化板块
        initNews();  // 初始化新闻资讯
        initWorks();  // 初始化作品库
        initPosts();  // 初始化示例帖子
        initSystemConfigs();  // 初始化系统配置
        initSensitiveWords();  // 初始化敏感词
    }

    /**
     * 初始化管理员账号
     */
    private void initAdmins() {
        if (adminUserRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        AdminUser admin = new AdminUser();  // 超级管理员
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(AdminRole.ADMIN);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        adminUserRepository.save(admin);

        AdminUser moderator = new AdminUser();  // 版主
        moderator.setUsername("moderator");
        moderator.setPassword(passwordEncoder.encode("mod123"));
        moderator.setRole(AdminRole.MODERATOR);
        moderator.setCreatedAt(LocalDateTime.now());
        moderator.setUpdateTime(LocalDateTime.now());
        adminUserRepository.save(moderator);
    }

    /**
     * 初始化测试用户
     */
    private void initUsers() {
        if (userRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        User user1 = new User("user1", passwordEncoder.encode("user123"));
        user1.setEmail("user1@acg.com");
        user1.setNickname("二次元爱好者");
        user1.setSignature("宅舞拯救世界");
        user1.setBio("资深ACG爱好者，喜欢看番、玩游戏、写同人文。");
        userRepository.save(user1);

        User user2 = new User("user2", passwordEncoder.encode("user123"));
        user2.setEmail("user2@acg.com");
        user2.setNickname("新番追番者");
        user2.setSignature("每一季都不落下");
        user2.setBio("专注新番资讯，喜欢分享观后感。");
        userRepository.save(user2);
    }

    /**
     * 初始化板块分区
     */
    private void initSections() {
        if (sectionRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        String[][] sections = {  // 板块名、描述、图标标识
                {"动画", "讨论各类动画、新番、经典老番", "tv"},
                {"漫画", "漫画推荐、连载讨论、漫画资讯", "book"},
                {"游戏", "各类游戏讨论、攻略分享、游戏推荐", "gamepad"},
                {"轻小说", "轻小说讨论、推荐与创作", "book-open"},
                {"同人创作", "同人图文创作分享、创作交流", "palette"}
        };
        for (int i = 0; i < sections.length; i++) {  // 逐个创建板块
            Section section = new Section();
            section.setName(sections[i][0]);
            section.setDescription(sections[i][1]);
            section.setIcon(sections[i][2]);
            section.setSortOrder(i);  // 按数组顺序排序
            section.setPostCount(0);
            section.setCreatedAt(LocalDateTime.now());
            section.setUpdateTime(LocalDateTime.now());
            sectionRepository.save(section);
        }
    }

    /**
     * 初始化新闻资讯
     */
    private void initNews() {
        if (newsRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        String[][] newsData = {
                {"2026夏季新番导览：30部值得关注的动画", "本季新番涵盖热血、恋爱、科幻等多种题材...",
                 "夏季新番季正式开启，本季共30余部新番即将播出。", "NEW_ANIME"},
                {"《鬼灭之刃》最终章剧场版定档", "官方宣布最终章剧场版将于明年春季上映...",
                 "官方正式宣布最终章剧场版上映日期。", "WORK_INFO"},
                {"二次元产业2026上半年报告发布", "报告显示上半年市场规模持续增长...",
                 "行业研究报告发布，二次元市场持续增长。", "INDUSTRY"},
                {"Comic Market 104展会圆满落幕", "本届展会参展人数再创新高...",
                 "同人展会活动回顾，参展人数创新高。", "EVENT"},
                {"《葬送的芙莉莲》第二季制作决定", "官方宣布第二季动画制作决定...",
                 "人气动画第二季制作正式宣布。", "WORK_INFO"}
        };
        for (String[] data : newsData) {  // 逐条创建新闻
            News news = new News();
            news.setTitle(data[0]);
            news.setContent(data[1]);
            news.setSummary(data[2]);
            news.setCategory(NewsCategory.valueOf(data[3]));
            news.setViewCount((int) (Math.random() * 500) + 100);  // 随机浏览量
            news.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
            news.setUpdateTime(LocalDateTime.now());
            newsRepository.save(news);
        }
    }

    /**
     * 初始化作品库
     */
    private void initWorks() {
        if (workRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        Object[][] worksData = {
                {"葬送的芙莉莲", "奇幻冒险动画，讲述精灵魔法使芙莉莲的旅途", WorkType.ANIME, 9.3, "奇幻,冒险,治愈"},
                {"鬼灭之刃", "大正时代猎鬼人的热血故事", WorkType.ANIME, 9.1, "热血,战斗,友情"},
                {"咒术回战", "现代背景下咒术师对抗诅咒的战斗故事", WorkType.ANIME, 8.9, "热血,战斗,奇幻"},
                {"进击的巨人", "人类与巨人的终极对抗史诗", WorkType.ANIME, 9.5, "热血,黑暗,史诗"},
                {"间谍过家家", "间谍一家人的温馨日常喜剧", WorkType.ANIME, 8.8, "喜剧,日常,温馨"},
                {"海贼王", "传奇海盗冒险漫画，连载二十余年", WorkType.MANGA, 9.4, "冒险,热血,友情"},
                {"原神", "开放世界冒险RPG游戏", WorkType.GAME, 8.7, "RPG,开放世界,冒险"},
                {"塞尔达传说：王国之泪", "任天堂开放世界冒险游戏续作", WorkType.GAME, 9.6, "冒险,解谜,开放世界"},
                {"86-不存在的战区-", "科幻轻小说，讲述被遗忘的少年兵们", WorkType.NOVEL, 8.6, "科幻,战争,悲剧"}
        };
        for (Object[] data : worksData) {  // 逐个创建作品
            Work work = new Work();
            work.setTitle((String) data[0]);
            work.setDescription((String) data[1]);
            work.setType((WorkType) data[2]);
            work.setRating((Double) data[3]);
            work.setTags((String) data[4]);
            work.setCreatedAt(LocalDateTime.now());
            work.setUpdateTime(LocalDateTime.now());
            workRepository.save(work);
        }
    }

    /**
     * 初始化示例帖子
     */
    private void initPosts() {
        if (postRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        // 获取用户和板块
        var users = userRepository.findAll();
        var sections = sectionRepository.findAll();
        if (users.isEmpty() || sections.isEmpty()) {  // 依赖数据不存在则跳过
            return;
        }

        Long user1Id = users.get(0).getId();
        Long user2Id = users.size() > 1 ? users.get(1).getId() : user1Id;
        Long animeSectionId = sections.get(0).getId();  // 动画板块
        Long gameSectionId = sections.size() > 2 ? sections.get(2).getId() : animeSectionId;  // 游戏板块

        Object[][] postsData = {
                {user1Id, animeSectionId, "《葬送的芙莉莲》观后感讨论",
                 "刚看完芙莉莲最新一集，真的是每一集都被治愈到。大家怎么看这部作品对生命和时间的探讨？",
                 156, 23, 18},
                {user2Id, animeSectionId, "2026夏季新番追番清单分享",
                 "整理了下这季要追的新番，大概有10部左右。大家都在追哪些？一起交流一下观感吧。",
                 203, 45, 32},
                {user1Id, gameSectionId, "塞尔达王国之泪通关感受",
                 "终于通关了王国之泪，整个游戏体验太震撼了。大家觉得跟旷野之息比怎么样？",
                 289, 67, 41},
                {user2Id, animeSectionId, "《进击的巨人》最终季讨论（含剧透）",
                 "看完最终季了，结局真的是让人感慨万千。大家怎么看待艾伦的选择？",
                 412, 89, 56}
        };

        // 统计各板块与各用户的帖子数，用于同步 postCount 字段
        java.util.Map<Long, Integer> sectionPostCount = new java.util.HashMap<>();
        java.util.Map<Long, Integer> userPostCount = new java.util.HashMap<>();

        for (Object[] data : postsData) {  // 逐条创建帖子
            Post post = new Post();
            // 数组结构：[0]userId [1]sectionId [2]title [3]content [4]viewCount [5]likeCount [6]favoriteCount
            post.setUserId((Long) data[0]);
            post.setSectionId((Long) data[1]);
            post.setTitle((String) data[2]);
            post.setContent((String) data[3]);
            post.setViewCount((Integer) data[4]);
            post.setLikeCount((Integer) data[5]);
            post.setCommentCount(0);
            post.setFavoriteCount((Integer) data[6]);
            post.setStatus(PostStatus.APPROVED);  // 示例帖子直接通过
            post.setIsTop(false);
            post.setIsEssence(false);
            post.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 15)));
            post.setUpdateTime(LocalDateTime.now());
            postRepository.save(post);

            // 累加板块与用户的帖子计数
            Long sectionId = (Long) data[1];
            Long userId = (Long) data[0];
            sectionPostCount.merge(sectionId, 1, Integer::sum);
            userPostCount.merge(userId, 1, Integer::sum);
        }

        // 同步更新板块 postCount，避免首页显示"0 帖子"
        for (Section section : sections) {
            int count = sectionPostCount.getOrDefault(section.getId(), 0);
            if (count > 0) {
                section.setPostCount(count);
                section.setUpdateTime(LocalDateTime.now());
                sectionRepository.save(section);
            }
        }

        // 同步更新用户 postCount，避免管理后台显示发帖数为 0
        for (User user : users) {
            int count = userPostCount.getOrDefault(user.getId(), 0);
            if (count > 0) {
                user.setPostCount(count);
                userRepository.save(user);
            }
        }
    }

    /**
     * 初始化系统配置
     */
    private void initSystemConfigs() {
        if (systemConfigRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        String[][] configs = {
                {"post.auto_approve", "false", "帖子是否自动审核通过（true/false）"},
                {"site.name", "二次元讨论社区", "站点名称"},
                {"site.description", "面向ACG爱好者的垂直化讨论社区平台", "站点描述"},
                {"post.max_tags", "5", "每个帖子最大标签数"},
                {"comment.max_length", "2000", "评论最大字符数"}
        };
        for (String[] config : configs) {  // 逐条创建配置
            SystemConfig sc = new SystemConfig();
            sc.setConfigKey(config[0]);
            sc.setConfigValue(config[1]);
            sc.setDescription(config[2]);
            sc.setCreatedAt(LocalDateTime.now());  // createdAt 为 nullable=false，必须显式设置
            sc.setUpdateTime(LocalDateTime.now());
            systemConfigRepository.save(sc);
        }
    }

    /**
     * 初始化敏感词库
     */
    private void initSensitiveWords() {
        if (sensitiveWordRepository.count() > 0) {  // 表非空则跳过
            return;
        }
        String[][] words = {
                {"广告推广", "广告"},
                {"违规内容", "色情"},
                {"违规内容", "赌博"},
                {"违规内容", "毒品"},
                {"侮辱言论", "脑残"}
        };
        for (String[] word : words) {  // 逐条创建敏感词
            SensitiveWord sw = new SensitiveWord();
            sw.setWord(word[1]);
            sw.setCategory(word[0]);
            sw.setCreatedAt(LocalDateTime.now());
            sensitiveWordRepository.save(sw);
        }
    }
}
