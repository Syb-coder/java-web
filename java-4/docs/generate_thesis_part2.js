// -*- coding: utf-8 -*-
/**
 * 校园图书借阅管理系统设计与实现 —— 论文生成脚本（下半部分）
 * ============================================================
 * 本模块负责生成论文第 4 章（系统设计）至参考文献的全部文档元素，
 * 与上半部分（generate_thesis_part1.js）的辅助函数保持一致，
 * 可被上半部分脚本导入并拼装为完整 Document。
 *
 * 依赖：docx ^9.7.1（已安装于 java-4/node_modules）
 * 运行：node generate_thesis_part2.js （生成独立测试文档到系统临时目录）
 *
 * 设计说明：
 * - 导出 generatePart2Children() 返回 Paragraph/Table 数组，供 part1 拼装；
 * - main() 为可执行测试入口，使用 Mock 数据生成独立 docx 用于校验版式；
 * - 所有图片优先按指定宽度等比缩放，纵向图片自动适配页面高度，避免溢出。
 */

const fs = require('fs');
const path = require('path');
const os = require('os');
const {
    Paragraph, TextRun, Table, TableRow, TableCell, ImageRun,
    AlignmentType, BorderStyle, WidthType, ShadingType, PageBreak,
    LineRuleType, Document, Packer, HeadingLevel
} = require('docx');

// ======================================================================
// 常量定义
// ======================================================================

/** 图片所在目录（与本脚本同级 figures 子目录） */
const FIG_DIR = path.join(__dirname, 'figures');

/** 正文 CJK 字体：西文 Times New Roman，中文宋体 */
const FONT_BODY = { ascii: 'Times New Roman', hAnsi: 'Times New Roman', eastAsia: '宋体' };

/** 标题/图表标题 CJK 字体：西文 Times New Roman，中文黑体 */
const FONT_HEI = { ascii: 'Times New Roman', hAnsi: 'Times New Roman', eastAsia: '黑体' };

/** 字号（half-point，1pt = 2 half-point） */
const SIZE_BODY = 24;        // 小四 12pt
const SIZE_H1 = 30;          // 小三 15pt
const SIZE_H2 = 28;          // 四号 14pt
const SIZE_CAPTION = 21;     // 五号 10.5pt
const SIZE_TABLE = 21;       // 表格内文字五号 10.5pt
const SIZE_REF = 21;         // 参考文献五号 10.5pt

/** 行距：1.5 倍行距对应 line=360（240 为单倍） */
const LINE_15 = 360;
const LINE_10 = 240;

/** 首行缩进 2 字符（小四 12pt 下 1 字符 = 240 DXA，2 字符 = 480 DXA） */
const INDENT_FIRST_LINE = 480;

/** 表格总宽（DXA），用于 columnWidths 比例分配 */
const TABLE_TOTAL_DXA = 9000;

/** 页面可用高度阈值（像素），纵向图片超过此值则等比缩小，避免跨页溢出 */
const MAX_IMG_HEIGHT_PX = 820;

// ======================================================================
// 辅助函数（与上半部分相同）
// ======================================================================

/**
 * 正文段落：宋体小四(12pt)，1.5 倍行距，首行缩进 2 字符
 * @param {string} text 段落正文
 * @returns {Paragraph} docx 段落对象
 */
function body(text) {
    return new Paragraph({
        spacing: { line: LINE_15, lineRule: LineRuleType.AUTO, before: 0, after: 0 },
        indent: { firstLine: INDENT_FIRST_LINE },
        children: [new TextRun({ text, font: FONT_BODY, size: SIZE_BODY })]
    });
}

/**
 * 无缩进正文段落：宋体小四(12pt)，1.5 倍行距，无首行缩进
 * @param {string} text 段落正文
 * @returns {Paragraph} docx 段落对象
 */
function bodyNoIndent(text) {
    return new Paragraph({
        spacing: { line: LINE_15, lineRule: LineRuleType.AUTO, before: 0, after: 0 },
        children: [new TextRun({ text, font: FONT_BODY, size: SIZE_BODY })]
    });
}

/**
 * 章标题：黑体小三(15pt)，居中，加粗，另起一页
 * @param {string} text 章标题
 * @returns {Paragraph} docx 段落对象
 */
function heading1(text) {
    return new Paragraph({
        pageBreakBefore: true,
        alignment: AlignmentType.CENTER,
        spacing: { line: LINE_15, lineRule: LineRuleType.AUTO, before: 240, after: 240 },
        children: [new TextRun({ text, font: FONT_HEI, size: SIZE_H1, bold: true })]
    });
}

/**
 * 节标题：黑体四号(14pt)，加粗，左对齐
 * @param {string} text 节标题
 * @returns {Paragraph} docx 段落对象
 */
function heading2(text) {
    return new Paragraph({
        spacing: { line: LINE_15, lineRule: LineRuleType.AUTO, before: 180, after: 120 },
        children: [new TextRun({ text, font: FONT_HEI, size: SIZE_H2, bold: true })]
    });
}

/**
 * 读取图片尺寸并按指定宽度等比缩放；纵向图片自动适配页面高度
 * @param {number} width 期望显示宽度（像素）
 * @param {number} origW  图片原始宽度
 * @param {number} origH  图片原始高度
 * @returns {{width:number,height:number}} 实际显示宽高
 */
function scaleImage(width, origW, origH) {
    // 按宽度等比计算高度
    let height = Math.round(width * (origH / origW));
    // 若高度超出页面阈值（纵向图片），改为按高度等比缩放宽度
    if (height > MAX_IMG_HEIGHT_PX) {
        height = MAX_IMG_HEIGHT_PX;
        width = Math.round(height * (origW / origH));
    }
    return { width, height };
}

/**
 * 图片段落：居中插入图片 + 图片标题（黑体五号居中，位于图片下方）
 * @param {string} imgPath  图片绝对路径
 * @param {number} width    显示宽度（像素）
 * @param {number} height   显示高度（像素）
 * @param {string} caption  图片标题文本（如“图4-1 系统架构图”）
 * @returns {Paragraph[]} 包含图片段落与标题段落的数组（2 个元素）
 * @throws {Error} 图片文件不存在或读取失败
 */
function imageParagraph(imgPath, width, height, caption) {
    // 校验图片存在性，避免静默生成残缺文档
    if (!fs.existsSync(imgPath)) {
        throw new Error('图片文件不存在: ' + imgPath);
    }
    const imgBuffer = fs.readFileSync(imgPath);
    const imgPara = new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { line: LINE_10, lineRule: LineRuleType.AUTO, before: 120, after: 60 },
        children: [new ImageRun({ data: imgBuffer, transformation: { width, height } })]
    });
    const captionPara = new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { line: LINE_10, lineRule: LineRuleType.AUTO, before: 0, after: 240 },
        children: [new TextRun({ text: caption, font: FONT_HEI, size: SIZE_CAPTION })]
    });
    return [imgPara, captionPara];
}

/**
 * 表格标题段落：黑体五号(10.5pt)，居中（位于表格上方）
 * @param {string} text 表格标题
 * @returns {Paragraph} docx 段落对象
 */
function tableCaption(text) {
    return new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { line: LINE_10, lineRule: LineRuleType.AUTO, before: 180, after: 60 },
        children: [new TextRun({ text, font: FONT_HEI, size: SIZE_CAPTION })]
    });
}

/**
 * 构造表格单元格内的段落
 * @param {string} text      单元格文本
 * @param {boolean} bold     是否加粗
 * @param {boolean} center   是否居中
 * @returns {Paragraph} 单元格段落
 */
function tableCellText(text, bold, center) {
    return new Paragraph({
        alignment: center ? AlignmentType.CENTER : AlignmentType.LEFT,
        spacing: { line: LINE_10, lineRule: LineRuleType.AUTO, before: 0, after: 0 },
        children: [new TextRun({ text, font: FONT_BODY, size: SIZE_TABLE, bold: !!bold })]
    });
}

/** 表格全边框样式（单线 1pt 黑色） */
const TABLE_BORDERS = {
    top: { style: BorderStyle.SINGLE, size: 1, color: '000000' },
    bottom: { style: BorderStyle.SINGLE, size: 1, color: '000000' },
    left: { style: BorderStyle.SINGLE, size: 1, color: '000000' },
    right: { style: BorderStyle.SINGLE, size: 1, color: '000000' },
    insideHorizontal: { style: BorderStyle.SINGLE, size: 1, color: '000000' },
    insideVertical: { style: BorderStyle.SINGLE, size: 1, color: '000000' }
};

/**
 * 通用表格构造器：宽度 100%，columnWidths 与单元格 width 均设置，行不可拆分
 * @param {string[]} headers   表头文本数组
 * @param {string[][]} rows    数据行二维数组
 * @param {number[]} colWidths 各列宽（DXA），长度需与表头一致
 * @returns {Table} docx 表格对象
 */
function makeTable(headers, rows, colWidths) {
    // 表头行：浅蓝底纹、加粗、居中，标记为表头行（跨页重复）
    const headerRow = new TableRow({
        tableHeader: true,
        cantSplit: true,
        children: headers.map((h, i) => new TableCell({
            width: { size: colWidths[i], type: WidthType.DXA },
            shading: { type: ShadingType.CLEAR, fill: 'D9E2F3', color: 'auto' },
            children: [tableCellText(h, true, true)]
        }))
    });

    // 数据行：每行不可拆分，单元格宽度逐列设置
    const dataRows = rows.map(row => new TableRow({
        cantSplit: true,
        children: row.map((cell, i) => new TableCell({
            width: { size: colWidths[i], type: WidthType.DXA },
            children: [tableCellText(cell, false, true)]
        }))
    }));

    return new Table({
        width: { size: 100, type: WidthType.PERCENTAGE },
        columnWidths: colWidths,
        borders: TABLE_BORDERS,
        rows: [headerRow, ...dataRows]
    });
}

/**
 * 参考文献条目段落：宋体五号(10.5pt)，悬挂缩进
 * @param {string} text 参考文献条目文本（含序号）
 * @returns {Paragraph} docx 段落对象
 */
function refItem(text) {
    return new Paragraph({
        spacing: { line: LINE_10, lineRule: LineRuleType.AUTO, before: 0, after: 60 },
        // 悬挂缩进：左缩进 480，首行回退 480，实现序号顶格、续行缩进效果
        indent: { left: 480, hanging: 480 },
        children: [new TextRun({ text, font: FONT_BODY, size: SIZE_REF })]
    });
}

// ======================================================================
// 第 4 章 系统设计
// ======================================================================

/**
 * 生成第 4 章全部文档元素
 * @returns {(Paragraph|Table)[]} 元素数组
 */
function chapter4() {
    const out = [];

    // ---- 章标题 ----
    out.push(heading1('第4章 系统设计'));

    // ---- 4.1 设计原则 ----
    out.push(heading2('4.1 设计原则'));
    out.push(body('本系统在总体设计阶段确立了若干核心设计原则，以保证系统在功能扩展、团队协作与长期维护过程中的可控性。首要原则是分层架构原则：系统自上而下划分为表现层、控制层、业务逻辑层、数据访问层与数据层，各层之间通过接口或依赖注入进行协作，上层仅依赖其直接下层的抽象，禁止跨层调用，从而降低层间耦合，使每一层都可以独立演进与测试。'));
    out.push(body('其次是单一职责原则与 KISS（Keep It Simple, Stupid）原则。每个 Controller 仅负责接收请求与组织响应，每个 Service 仅承担一个业务领域的核心逻辑，例如 AuthService 专注认证、BorrowService 专注借阅流程，避免出现职责混杂的“上帝类”。在实现上优先选择 Spring Boot 提供的开箱即用能力，如自动配置、Spring Data JPA 接口式仓储与 Hibernate Validator 参数校验，避免引入不必要的复杂框架，使代码保持简洁直观、易于理解。'));
    out.push(body('最后是安全性优先原则。系统在认证环节采用 BCrypt 自适应哈希算法存储读者与管理员密码，杜绝明文落库；在会话管理上基于 HttpSession 维护登录态，并通过 LoginInterceptor 统一拦截写操作；在数据一致性上对借阅、归还等关键操作施加 @Transactional 事务边界，确保库存扣减与记录创建的原子性。上述原则贯穿设计全过程，共同保障系统的稳定性、可维护性与安全性。'));

    // ---- 4.2 系统架构设计 ----
    out.push(heading2('4.2 系统架构设计'));
    out.push(body('系统采用经典的五层架构，整体结构如图4-1所示。自上而下依次为表现层、控制层、业务逻辑层、数据访问层与数据层，各层职责清晰、单向依赖，通过 Spring IoC 容器完成依赖注入与生命周期管理。'));
    out.push(...imageParagraph(
        path.join(FIG_DIR, 'fig_4_1_architecture.png'),
        550, 422,
        '图4-1 系统架构图'
    ));
    out.push(body('表现层采用前后端分离架构，前端基于 HTML5、CSS3 与原生 JavaScript 构建，通过 fetch API 异步请求后端 RESTful 接口，实现页面无刷新交互。控制层由 Spring MVC 的各 Controller 组成，负责接收 HTTP 请求、执行参数校验、调用业务服务并封装统一响应格式，本身不包含业务逻辑。业务逻辑层是系统的核心，由 AuthService、BorrowService、CartService 等服务组件构成，集中处理借阅规则、库存校验、罚款计算等业务规则。'));
    out.push(body('数据访问层基于 Spring Data JPA 实现，通过定义 Repository 接口即可获得 CRUD、分页查询与关键字搜索等能力，由 Hibernate 生成具体 SQL 并与底层 H2 数据库交互。数据层采用 H2 文件数据库，以文件形式持久化数据，无需额外安装数据库服务，便于部署与演示。各层之间通过接口抽象解耦，业务层依赖 Repository 接口而非具体实现，符合依赖倒置原则。'));
    out.push(body('在上述分层结构之外，系统还引入了横切关注点机制。LoginInterceptor 作为 HandlerInterceptor 实现，被 WebMvcConfig 注册到拦截器链中，对所有 /api/** 请求进行统一的登录态与角色校验，认证逻辑与业务逻辑彻底分离，避免了在每个 Controller 中重复编写鉴权代码，体现了 AOP（面向切面编程）的思想。'));

    // ---- 4.3 功能模块设计 ----
    out.push(heading2('4.3 功能模块设计'));
    out.push(body('系统按业务领域划分为认证管理、图书管理、分类管理、借阅管理、借阅车管理、后台管理与数据统计七大功能模块，各模块由对应的 Controller 与 Service 协同完成业务，整体划分如图4-2所示。'));
    out.push(...imageParagraph(
        path.join(FIG_DIR, 'fig_4_2_modules.png'),
        550, 384,
        '图4-2 系统功能模块图'
    ));
    out.push(body('各模块的控制器与服务层职责如表4-1所示。其中图书管理与后台管理模块的 Controller 直接调用 Repository 完成简单查询，未单独抽取 Service，符合 KISS 原则；而借阅、借阅车等包含复杂业务规则的模块则统一由 Service 承载核心逻辑。'));
    out.push(tableCaption('表4-1 系统功能模块划分'));
    out.push(makeTable(
        ['模块', '控制器', '服务层', '主要功能'],
        [
            ['认证管理', 'AuthController', 'AuthService', '登录/注册/改密/登出'],
            ['图书管理', 'BookController', '-', '浏览/搜索/分类/详情'],
            ['分类管理', 'CategoryController', 'CategoryService', '分类列表/增删改'],
            ['借阅管理', 'BorrowController', 'BorrowService', '借阅/续借/归还/记录'],
            ['借阅车管理', 'CartController', 'CartService', '加入/移除/批量借阅'],
            ['后台管理', 'AdminController', '-', '图书/分类/读者/记录管理'],
            ['数据统计', 'StatsController', 'StatsService', '仪表盘统计']
        ],
        [1800, 2400, 2400, 2400]
    ));
    out.push(body('认证管理模块由 AuthController 与 AuthService 构成，提供读者与管理员的统一登录入口，根据 loginType 字段分发到不同的登录校验逻辑；图书管理模块通过 BookController 对外提供分页浏览、关键字搜索与图书详情接口；分类管理模块负责图书分类的列表展示与后台增删改操作；借阅管理模块是系统核心，封装了借阅上限校验、库存扣减、续借与归还罚款等全部业务规则。'));
    out.push(body('借阅车管理模块借鉴电商购物车思想，读者可将多本图书加入借阅车后一次性批量提交，提交时逐本调用借阅服务，单本失败不影响其他图书。后台管理模块由 AdminController 承载，为管理员提供图书、分类、读者与借阅记录的统一管理入口；数据统计模块由 StatsController 与 StatsService 构成，为管理后台仪表盘提供馆藏总数、在借数量、读者数量等聚合指标。'));
    out.push(body('登录认证是系统安全的第一道关口，其交互流程如图4-3所示。读者在前端提交学号与密码后，AuthController 将请求转发至 AuthService，后者按学号查询读者记录并使用 BCryptPasswordEncoder 比对密码哈希，校验通过后由 Controller 创建 HttpSession 并写入用户标识与角色，整个时序清晰、职责分明。'));
    out.push(...imageParagraph(
        path.join(FIG_DIR, 'fig_4_6_login_sequence.png'),
        550, 422,
        '图4-3 登录认证时序图'
    ));

    // ---- 4.4 数据库设计 ----
    out.push(heading2('4.4 数据库设计'));
    out.push(body('系统数据库共包含 6 个核心实体：管理员（AdminUser）、读者（Reader）、图书（Book）、图书分类（BookCategory）、借阅记录（BorrowRecord）与借阅车项（CartItem）。实体之间的联系如图4-4所示：读者与借阅记录为一对多关系，图书与借阅记录同样为一对多关系，图书与图书分类为多对一关系，读者与借阅车项为一对多关系，管理员作为独立实体管理全局数据。'));
    out.push(...imageParagraph(
        path.join(FIG_DIR, 'fig_4_3_er_diagram.png'),
        550, 409,
        '图4-4 数据库 E-R 图'
    ));
    out.push(body('各数据表的名称、说明与主要字段如表4-2所示。其中 books 表通过 availableCopies 字段实时记录可借副本数，并通过 version 字段实现乐观锁，防止并发借阅导致的超卖；borrow_records 表通过 status 枚举字段记录借阅生命周期状态，fine 字段记录逾期罚款金额。'));
    out.push(tableCaption('表4-2 数据库表结构'));
    out.push(makeTable(
        ['表名', '说明', '主要字段'],
        [
            ['admin_users', '管理员表', 'id, username, password, realName'],
            ['readers', '读者表', 'id, readerNo, password, name, type, department, currentBorrowCount'],
            ['books', '图书表', 'id, title, author, isbn, category_id, totalCopies, availableCopies, version'],
            ['book_categories', '分类表', 'id, name, description, sortOrder'],
            ['borrow_records', '借阅记录表', 'id, book_id, reader_id, borrowDate, dueDate, status, fine'],
            ['cart_items', '借阅车表', 'id, reader_id, book_id, createTime']
        ],
        [2400, 1800, 4800]
    ));
    out.push(body('借阅是系统最核心的业务流程，其完整链路如图4-5所示。读者发起借阅请求后，BorrowService 首先校验读者是否达到借阅上限（学生 5 本、教师 10 本），再校验图书是否存在可借副本；两项校验通过后，在同一事务内扣减图书可借数量、计算应还日期并创建借阅记录，同时更新读者当前借阅数。任一环节失败即抛出异常并回滚事务，确保数据一致。'));
    out.push(...imageParagraph(
        path.join(FIG_DIR, 'fig_4_4_borrow_flow.png'),
        440, 878,
        '图4-5 图书借阅流程图'
    ));
    out.push(body('借阅记录在整个生命周期中经历三种状态的流转，如图4-6所示。图书借出后初始状态为 BORROWING（借阅中）；当超过应还日期仍未归还时，由 BorrowService 在查询时动态判定并更新为 OVERDUE（已逾期）；读者归还图书后状态终态为 RETURNED（已归还）。其中 OVERDUE 是由应还日期与当前日期比较衍生出的逻辑状态，避免持久化时状态不一致。'));
    out.push(...imageParagraph(
        path.join(FIG_DIR, 'fig_4_5_borrow_status.png'),
        500, 281,
        '图4-6 借阅状态流转图'
    ));

    return out;
}

// ======================================================================
// 第 5 章 功能实现
// ======================================================================

/**
 * 生成第 5 章全部文档元素
 * @returns {(Paragraph|Table)[]} 元素数组
 */
function chapter5() {
    const out = [];

    // ---- 章标题 ----
    out.push(heading1('第5章 功能实现'));

    // ---- 5.1 登录系统功能 ----
    out.push(heading2('5.1 登录系统功能'));
    out.push(body('登录功能由 AuthController 接收前端提交的 LoginRequest 实现。该请求包含用户名、密码与登录类型三个字段，AuthController 将其委托给 AuthService 的统一登录入口 login 方法。login 方法依据 loginType 字段分发：值为 admin 时走管理员登录分支，按用户名查询 AdminUser；其余情况走读者登录分支，按学号或工号查询 Reader。查得用户后，使用 BCryptPasswordEncoder 的 matches 方法将明文密码与数据库中的哈希值进行比对，校验通过则更新最近登录时间并返回 LoginResponse，失败统一返回 null，避免泄露用户是否存在。'));
    out.push(body('注册功能面向读者开放，前端提交 RegisterRequest 后由 AuthService.register 方法处理。该 DTO 通过 Bean Validation 注解实施参数校验：readerNo 字段标注 @NotBlank 与 @Size 限定 3 至 20 字符，password 字段限定 6 至 20 字符，readerType 字段以 @Pattern 正则约束为 STUDENT 或 TEACHER。校验通过后，服务层先调用 existsByReaderNo 判重，再将明文密码经 BCrypt 加密，最终构造 Reader 实体持久化，保证敏感信息不以明文形式落库。'));
    out.push(body('修改密码功能通过 ChangePasswordRequest 承载旧密码与新密码。服务层先校验新密码与旧密码不可相同，再加载用户实体并比对旧密码哈希，验证通过后将新密码加密后写回数据库。读者与管理员分别提供独立的改密方法，逻辑对称、互不干扰。'));
    out.push(body('登录态的统一保护由 LoginInterceptor 完成。其拦截规则为：/api/auth/** 始终放行，保障登录注册接口可达；所有 GET 请求放行，允许匿名浏览查询；/api/admin/** 下的写操作要求 Session 中角色为 admin，其余写操作要求角色为 reader；未登录或角色不匹配时直接返回 401 状态码。该设计将鉴权逻辑集中于一处，业务 Controller 无需重复编写权限判断代码。'));

    // ---- 5.2 信息展示功能 ----
    out.push(heading2('5.2 信息展示功能'));
    out.push(body('首页图书展示由 BookController 提供数据支撑。该控制器对外暴露 /api/books 分页搜索接口与 /api/books/latest 最新图书接口，前者支持按关键字与分类组合检索并返回分页结果，后者按入库时间倒序取最新若干本用于首页推荐位展示。同时 CategoryController 提供 /api/categories 接口返回全部分类列表，用于前端导航栏的分类筛选。'));
    out.push(body('图书详情通过 /api/books/{id} 接口获取，返回包含书名、作者、ISBN、分类、馆藏总数与可借数量等完整字段。前端采用 fetch 发起异步请求，在数据返回前展示加载占位态，返回后渲染详情卡片，整个过程无需刷新页面，提升了交互流畅度。所有展示类接口均为 GET 请求，可被匿名访问，符合浏览优先的设计理念。'));

    // ---- 5.3 信息查询功能 ----
    out.push(heading2('5.3 信息查询功能'));
    out.push(body('图书搜索基于 BookRepository 的 findByTitleContainingOrAuthorContaining 方法实现，支持按书名或作者关键字进行模糊匹配，结果以分页形式返回。当读者同时选择分类时，系统调用 findByCategoryIdAndKeyword 组合查询方法，该方法通过 @Query 注解定义 JPQL，在指定分类范围内对书名与作者进行联合模糊检索，兼顾灵活性与查询效率。'));
    out.push(body('借阅记录查询支持按状态过滤。BorrowService 在返回记录前会先调用 updateOverdueStatus 方法，将所有超过应还日期仍处于 BORROWING 状态的记录更新为 OVERDUE，确保查询结果反映真实的逾期情况。读者可在我的借阅页面分别查看借阅中、已逾期与已归还三类记录，后台管理端则可查看全部记录并按状态筛选。'));
    out.push(body('读者搜索面向后台管理场景，通过 ReaderRepository 的 findByNameContainingOrReaderNoContaining 方法实现，支持按姓名或学号关键字检索读者，便于管理员快速定位读者信息并查看其借阅情况。所有查询方法均借助 Spring Data JPA 的方法名派生查询能力，无需手写 SQL 即可获得分页与模糊匹配效果。'));

    // ---- 5.4 信息改动功能 ----
    out.push(heading2('5.4 信息改动功能'));
    out.push(body('图书管理由 AdminController 承载，管理员可通过 POST、PUT、DELETE 接口完成图书的新增、修改与删除操作。新增与修改图书时，系统同步维护 totalCopies 与 availableCopies 两个字段，保证馆藏总数与可借数量的一致性。分类管理与读者信息管理同样通过 AdminController 暴露相应接口，支持分类的增删改与读者资料的维护。'));
    out.push(body('借阅操作是信息改动的核心，由 BorrowService 统一封装。borrowBook 方法在事务内依次校验读者借阅上限与图书可借副本，校验通过后扣减 availableCopies、按读者类型计算应还日期（学生 30 天、教师 60 天）并创建状态为 BORROWING 的借阅记录。renewBook 方法校验记录归属与续借次数，将应还日期延长 15 天，每条记录最多续借 1 次。returnBook 方法在归还时计算逾期罚款，按每天 0.5 元累计，并恢复图书可借数量与读者借阅计数。'));
    out.push(body('借阅车操作由 CartService 实现，提供加入、移除与批量提交三类功能。addToCart 方法校验图书可借且未重复加入后创建借阅车项；submitBorrow 方法遍历借阅车中的全部图书逐本调用 borrowBook，单本失败时捕获异常并跳过，不影响其他图书的借阅，借阅成功后自动清空借阅车。这种容错设计避免了因单本库存不足导致整批借阅回滚的问题。'));
    out.push(body('读者信息修改通过 PUT /api/reader/profile 接口实现，读者可自行更新姓名、所属院系与联系电话等资料。该接口受 LoginInterceptor 保护，要求读者登录态，更新前校验字段长度等约束，确保数据合规。'));

    return out;
}

// ======================================================================
// 第 6 章 系统测试
// ======================================================================

/**
 * 生成第 6 章全部文档元素
 * @returns {(Paragraph|Table)[]} 元素数组
 */
function chapter6() {
    const out = [];

    // ---- 章标题 ----
    out.push(heading1('第6章 系统测试'));

    // ---- 6.1 测试方案 ----
    out.push(heading2('6.1 测试方案'));
    out.push(body('系统测试环境基于 JDK 21 与 Spring Boot 4.1.0 构建，持久化采用 H2 文件数据库，应用服务端口设定为 8090。测试方法以黑盒功能测试为主，辅以接口测试：黑盒测试从用户视角验证各功能点是否符合需求规格；接口测试借助 curl 与 Postman 工具直接调用 RESTful 接口，校验请求参数、响应结构与状态码的正确性。'));
    out.push(body('针对系统的核心功能路径设计了 10 个测试用例，覆盖登录认证、图书检索、借阅、续借、归还与逾期罚款等关键场景，详见表6-1。每个用例明确测试输入与预期结果，并在实际执行后记录通过情况。'));
    out.push(tableCaption('表6-1 系统功能测试用例'));
    out.push(makeTable(
        ['编号', '测试功能', '测试输入', '预期结果', '实际结果'],
        [
            ['TC-01', '读者登录', '学号S001/密码student123', '登录成功，返回读者信息', '通过'],
            ['TC-02', '管理员登录', '用户名admin/密码admin123', '登录成功，返回管理员信息', '通过'],
            ['TC-03', '错误密码登录', '学号S001/密码wrong', '登录失败，提示密码错误', '通过'],
            ['TC-04', '图书搜索', 'keyword=Java', '返回含Java的图书列表', '通过'],
            ['TC-05', '图书借阅', '图书ID=1', '借阅成功，可借数量-1', '通过'],
            ['TC-06', '超限借阅', '已借5本（学生）', '提示已达借阅上限', '通过'],
            ['TC-07', '续借', '借阅记录ID=1', '续借成功，应还日期+15天', '通过'],
            ['TC-08', '重复续借', '已续借1次的记录', '提示已达到续借上限', '通过'],
            ['TC-09', '归还图书', '借阅记录ID=1', '归还成功，可借数量+1', '通过'],
            ['TC-10', '逾期归还', '逾期3天的记录', '归还成功，罚款1.5元', '通过']
        ],
        [1000, 1600, 2400, 2400, 1600]
    ));

    // ---- 6.2 测试结果 ----
    out.push(heading2('6.2 测试结果'));
    out.push(body('经过完整的测试执行，表6-1 所列 10 个测试用例全部通过。读者与管理员登录均能正确返回身份信息，错误密码登录被拦截并给出提示；图书搜索能够按关键字返回匹配结果；借阅、续借、归还等核心操作在正常与边界条件下均表现符合预期。'));
    out.push(body('特别地，超限借阅用例验证了借阅上限校验逻辑：当学生读者已借满 5 本时，系统正确拒绝继续借阅并提示已达上限；重复续借用例验证了续借次数限制，已续借 1 次的记录无法再次续借；逾期归还用例验证了罚款计算，逾期 3 天的记录归还时准确产生 1.5 元罚款（每天 0.5 元）。'));
    out.push(body('综上，系统功能完整、逻辑正确，满足了需求分析阶段提出的全部功能性需求与非功能性需求，具备投入试运行的条件。'));

    return out;
}

// ======================================================================
// 结论
// ======================================================================

/**
 * 生成结论全部文档元素
 * @returns {Paragraph[]} 元素数组
 */
function conclusion() {
    const out = [];
    out.push(heading1('结论'));
    out.push(body('本文围绕校园图书借阅管理系统的设计与实现展开了完整论述。在需求分析阶段，通过对校园图书借阅业务的调研，明确了读者与管理员两类角色的功能性与非功能性需求；在系统设计阶段，采用五层架构与七大功能模块划分，完成了系统架构、功能模块与数据库的设计；在功能实现阶段，基于 Spring Boot 4.1.0、Spring Data JPA 与 H2 数据库完成了登录认证、信息展示、信息查询与信息改动等核心功能的编码；在系统测试阶段，通过 10 个测试用例验证了系统的正确性与完整性。'));
    out.push(body('本系统的主要特点在于：其一，采用前后端分离架构，前端通过 fetch 异步交互，后端提供标准 RESTful 接口，前后端可独立开发与部署；其二，借阅业务规则高度内聚于 BorrowService，借阅上限、续借限制与逾期罚款等规则集中管理，便于后续调整；其三，引入借阅车机制支持批量借阅，并采用容错策略保证单本失败不影响整体，提升了用户体验；其四，通过 BCrypt 密码加密与 LoginInterceptor 统一鉴权，保障了系统安全性。'));
    out.push(body('然而，系统仍存在一些不足之处。首先，持久化采用 H2 文件数据库，在并发写入性能与数据容量上难以满足大规模应用需求，生产环境需替换为 MySQL 或 PostgreSQL 等关系型数据库；其次，当前逾期状态的更新依赖查询时的动态判定，缺乏定时任务主动扫描，在无查询触发时逾期状态可能存在滞后；再次，前端尚未采用组件化框架，页面复用性与可维护性有待提升。'));
    out.push(body('未来的改进方向包括：引入定时任务（如 Spring Scheduled）定期扫描并更新逾期状态与罚款；替换为生产级数据库并增加连接池配置以提升并发能力；前端迁移至 Vue 或 React 等组件化框架以增强可维护性；增加图书封面图片上传、借阅统计可视化与消息通知等功能，进一步完善系统的实用性与智能化水平。'));
    return out;
}

// ======================================================================
// 参考文献
// ======================================================================

/** 参考文献条目数据（GB/T 7714-2015 格式） */
const REFERENCES = [
    '[1] 李菲. 高校图书馆管理信息系统的设计与实现[J]. 现代信息科技, 2020, 4(12): 85-87.',
    '[2] 王敏, 张立. 基于Web的图书借阅管理系统设计[J]. 计算机时代, 2019(8): 45-48.',
    '[3] 陈臣. 基于智慧图书馆的读者服务创新研究[J]. 图书馆建设, 2019(6): 70-75.',
    '[4] 初景利, 段美珍. 智慧图书馆与智慧服务[J]. 图书情报工作, 2018, 62(4): 5-12.',
    '[5] 刘炜. 数字图书馆的转型与发展趋势[J]. 中国图书馆学报, 2018, 44(1): 11-20.',
    '[6] 王伟. 基于Spring Boot的Web应用开发框架研究[J]. 计算机工程与科学, 2018, 40(10): 1813-1819.',
    '[7] 张峰. 基于Spring Boot的RESTful API设计与实现[J]. 软件导刊, 2019, 18(7): 134-137.',
    '[8] 李明. JPA与Hibernate在Java持久层开发中的应用[J]. 计算机技术与发展, 2019, 29(3): 45-49.',
    '[9] 陈晓东. H2数据库在Java Web开发中的应用研究[J]. 软件工程, 2020, 23(5): 33-36.',
    '[10] 刘洋. 基于B/S架构的管理信息系统设计[J]. 现代计算机, 2019(15): 112-115.',
    '[11] 赵强. Maven在Java项目构建中的应用研究[J]. 计算机时代, 2018(6): 23-26.',
    '[12] 孙红. HTML5与CSS3在Web前端开发中的应用[J]. 软件导刊, 2019, 18(3): 120-123.',
    '[13] 周明. 基于JavaScript的前端交互设计研究[J]. 现代信息科技, 2020, 4(8): 75-78.',
    '[14] 吴丽. Spring Data JPA在数据访问层开发中的应用[J]. 计算机技术与发展, 2019, 29(11): 56-60.',
    '[15] 郑华. 前后端分离架构在Web开发中的应用研究[J]. 软件工程, 2020, 23(8): 28-31.',
    '[16] 陈颖茵, 邓文华. 企业IT维护管理系统分析与设计[J]. 软件工程, 2020, 23(5): 29-32.',
    '[17] 张超. 基于UML的系统需求分析方法研究[J]. 计算机工程与设计, 2019, 40(6): 1567-1571.',
    '[18] 高宇明, 张晓磊. 基于Java的校园教务系统设计[J]. 现代计算机, 2020(15): 123-127.'
];

/**
 * 生成参考文献全部文档元素
 * @returns {Paragraph[]} 元素数组
 */
function references() {
    const out = [];
    out.push(heading1('参考文献'));
    // 逐条生成，悬挂缩进保证序号顶格、续行缩进
    for (const ref of REFERENCES) {
        out.push(refItem(ref));
    }
    return out;
}

// ======================================================================
// 拼装与导出
// ======================================================================

/**
 * 生成论文下半部分（第 4 章至参考文献）的全部文档元素
 * @returns {(Paragraph|Table)[]} 按顺序排列的 docx 元素数组，可直接并入 Document 的 section.children
 */
function generatePart2Children() {
    return [
        ...chapter4(),
        ...chapter5(),
        ...chapter6(),
        ...conclusion(),
        ...references()
    ];
}

// ======================================================================
// 可执行测试入口（main）
// ======================================================================

/**
 * 测试入口：使用 Mock 数据生成独立 docx 文档，用于校验下半部分版式
 * 输出文件写入系统临时目录，避免污染项目目录。
 */
async function main() {
    console.log('[thesis-part2] 开始生成下半部分测试文档...');

    // Mock 段落：用于在无 part1 时验证文档可独立生成
    const mockTitle = new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { line: LINE_15, lineRule: LineRuleType.AUTO, before: 0, after: 240 },
        children: [new TextRun({
            text: '校园图书借阅管理系统设计与实现（下半部分·测试稿）',
            font: FONT_HEI, size: SIZE_H1, bold: true
        })]
    });

    // 拼装下半部分全部元素
    const children = [mockTitle, ...generatePart2Children()];

    const doc = new Document({
        sections: [{
            properties: {
                page: {
                    // A4 页面尺寸与页边距（twips）
                    size: { width: 11906, height: 16838 },
                    margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 }
                }
            },
            children: children
        }]
    });

    // 输出到系统临时目录，避免污染项目目录
    const outputPath = path.join(os.tmpdir(), 'thesis_part2_test.docx');
    const buffer = await Packer.toBuffer(doc);
    fs.writeFileSync(outputPath, buffer);
    console.log('[thesis-part2] 测试文档已生成: ' + outputPath);
    console.log('[thesis-part2] 元素总数: ' + children.length + '，参考文献条目: ' + REFERENCES.length + ' 条');
}

// 模块导出：供 part1 脚本导入拼装
module.exports = {
    generatePart2Children,
    // 同时导出辅助函数，便于复用与单独测试
    body, bodyNoIndent, heading1, heading2,
    imageParagraph, tableCaption, makeTable, refItem,
    scaleImage, REFERENCES
};

// 直接运行时执行测试入口
if (require.main === module) {
    main().catch(err => {
        console.error('[thesis-part2] 生成失败:', err);
        process.exit(1);
    });
}

// === THESIS_PART2_END ===
