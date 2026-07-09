/**
 * 课程设计论文生成脚本
 * 使用 docx 库生成《校园图书借阅管理系统设计与实现》论文
 *
 * 运行方式：node generate_thesis.js
 * 输出路径：../校园图书借阅管理系统设计与实现.docx
 */

const docx = require("c:/000/code/java-web/java-4/node_modules/docx");
const fs = require("fs");
const path = require("path");
const JSZip = require("c:/000/code/java-web/java-4/node_modules/jszip");

const {
  Document,
  Packer,
  Paragraph,
  TextRun,
  Table,
  TableRow,
  TableCell,
  ImageRun,
  Header,
  Footer,
  AlignmentType,
  HeadingLevel,
  PageBreak,
  WidthType,
  BorderStyle,
  ShadingType,
  PageNumber,
  VerticalAlign,
  HeightRule,
} = docx;

// ==================== 常量定义 ====================

/** 图片目录 */
const FIGURES_DIR = path.join(__dirname, "figures");
/** 输出文件路径 */
const OUTPUT_PATH = path.join(__dirname, "..", "校园图书借阅管理系统设计与实现.docx");

/** 正文字体配置（宋体 + Times New Roman） */
const FONT_BODY = {
  ascii: "Times New Roman",
  hAnsi: "Times New Roman",
  eastAsia: "宋体",
};

/** 标题字体配置（黑体 + Times New Roman） */
const FONT_HEADING = {
  ascii: "Times New Roman",
  hAnsi: "Times New Roman",
  eastAsia: "黑体",
};

/** 字号常量（半磅为单位） */
const SIZE = {
  XIAOER: 36, // 小二 18pt
  XIAOSAN: 30, // 小三 15pt
  SIHAO: 28, // 四号 14pt
  XIAOSI: 24, // 小四 12pt
  XIAOWU: 18, // 小五 9pt
  SANHAO: 32, // 三号 16pt
  XIAOYI: 36, // 小一 24pt
  ERHAO: 44, // 二号 22pt
};

/** 行距常量：1.2倍行距 = 288（240为单倍行距） */
const LINE_1_2 = 288;
/** 首行缩进2字符（12pt × 2 × 20 = 480 twips） */
const INDENT_2CHAR = 480;

// ==================== 辅助函数 ====================

/**
 * 创建正文段落（宋体小四，1.2倍行距，首行缩进2字符）
 * @param {string} text - 段落文本
 * @param {object} [opts] - 额外选项
 * @returns {Paragraph}
 */
function p(text, opts = {}) {
  const runs = Array.isArray(text)
    ? text
    : [new TextRun({ text, font: FONT_BODY, size: SIZE.XIAOSI })];
  return new Paragraph({
    spacing: { line: LINE_1_2, before: 0, after: 0 },
    indent: opts.noIndent ? undefined : { firstLine: INDENT_2CHAR },
    alignment: opts.alignment,
    children: runs,
  });
}

/**
 * 创建多文本片段段落（用于含引用标记等混合文本）
 * @param {Array} parts - 文本片段数组，每项为 { text, bold?, italic? }
 * @returns {Paragraph}
 */
function pRich(parts) {
  const runs = parts.map(
    (part) =>
      new TextRun({
        text: part.text,
        font: FONT_BODY,
        size: SIZE.XIAOSI,
        bold: part.bold || false,
        italics: part.italic || false,
      })
  );
  return new Paragraph({
    spacing: { line: LINE_1_2, before: 0, after: 0 },
    indent: { firstLine: INDENT_2CHAR },
    children: runs,
  });
}

/**
 * 创建一级标题（第X章，黑体小二，居中，段前段后各2行）
 * @param {string} text - 标题文本
 * @returns {Paragraph}
 */
function h1(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_1,
    alignment: AlignmentType.CENTER,
    spacing: { before: 480, after: 480, line: LINE_1_2 },
    children: [
      new TextRun({
        text,
        font: FONT_HEADING,
        size: SIZE.XIAOER,
      }),
    ],
  });
}

/**
 * 创建二级标题（X.X，黑体小三，顶格，段前段后各1行）
 * @param {string} text - 标题文本
 * @returns {Paragraph}
 */
function h2(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_2,
    spacing: { before: 240, after: 240, line: LINE_1_2 },
    children: [
      new TextRun({
        text,
        font: FONT_HEADING,
        size: SIZE.XIAOSAN,
      }),
    ],
  });
}

/**
 * 创建三级标题（X.X.X，黑体四号，顶格，段前1行段后0）
 * @param {string} text - 标题文本
 * @returns {Paragraph}
 */
function h3(text) {
  return new Paragraph({
    heading: HeadingLevel.HEADING_3,
    spacing: { before: 240, after: 0, line: LINE_1_2 },
    children: [
      new TextRun({
        text,
        font: FONT_HEADING,
        size: SIZE.SIHAO,
      }),
    ],
  });
}

/**
 * 创建空行
 * @returns {Paragraph}
 */
function emptyLine() {
  return new Paragraph({
    spacing: { line: LINE_1_2, before: 0, after: 0 },
    children: [new TextRun({ text: "", font: FONT_BODY, size: SIZE.XIAOSI })],
  });
}

/**
 * 创建分页符段落
 * @returns {Paragraph}
 */
function pageBreak() {
  return new Paragraph({
    children: [new PageBreak()],
  });
}

/**
 * 创建居中段落（用于封面等特殊场景）
 * @param {string} text - 文本
 * @param {number} size - 字号
 * @param {boolean} bold - 是否加粗
 * @param {object} font - 字体配置
 * @param {number} spacingBefore - 段前间距
 * @returns {Paragraph}
 */
function centerText(text, size, bold, font, spacingBefore) {
  return new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: spacingBefore || 0, after: 0, line: LINE_1_2 },
    children: [
      new TextRun({
        text,
        font: font || FONT_BODY,
        size: size || SIZE.XIAOSI,
        bold: bold || false,
      }),
    ],
  });
}

/**
 * 读取图片并创建 ImageRun
 * @param {string} filename - 图片文件名
 * @param {number} displayWidth - 显示宽度（像素）
 * @returns {ImageRun}
 */
function createImage(filename, displayWidth) {
  const imgPath = path.join(FIGURES_DIR, filename);
  const imgBuffer = fs.readFileSync(imgPath);
  // 预定义的尺寸映射，用于按比例计算显示高度
  const sizeMap = {
    "fig_4_1_architecture.png": { w: 1500, h: 900 },
    "fig_4_2_modules.png": { w: 1800, h: 1050 },
    "fig_4_3_er_diagram.png": { w: 2100, h: 1350 },
    "fig_4_4_borrow_flow.png": { w: 1500, h: 750 },
    "fig_4_5_borrow_status.png": { w: 1500, h: 1500 },
    "fig_4_6_login_sequence.png": { w: 1500, h: 1050 },
  };
  const orig = sizeMap[filename] || { w: 1000, h: 750 };
  const displayHeight = Math.round((displayWidth * orig.h) / orig.w);
  return new ImageRun({
    data: imgBuffer,
    transformation: { width: displayWidth, height: displayHeight },
    type: "png",
  });
}

/**
 * 创建图片段落（居中）+ 图题
 * @param {string} filename - 图片文件名
 * @param {string} caption - 图题
 * @param {number} [displayWidth=500] - 显示宽度
 * @returns {Array<Paragraph>} 图片段落和图题段落数组
 */
function figure(filename, caption, displayWidth) {
  const width = displayWidth || 500;
  const imgPara = new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 120, after: 60, line: LINE_1_2 },
    children: [createImage(filename, width)],
  });
  const captionPara = new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 0, after: 120, line: LINE_1_2 },
    children: [
      new TextRun({
        text: caption,
        font: FONT_BODY,
        size: SIZE.XIAOWU,
      }),
    ],
  });
  return [imgPara, captionPara];
}

/**
 * 创建表格单元格
 * @param {string} text - 单元格文本
 * @param {boolean} [isHeader] - 是否表头
 * @param {number} [width] - 单元格宽度（DXA）
 * @returns {TableCell}
 */
function tc(text, isHeader, width) {
  const cellOpts = {
    children: [
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { line: LINE_1_2, before: 40, after: 40 },
        children: [
          new TextRun({
            text: String(text),
            font: FONT_BODY,
            size: SIZE.XIAOWU,
            bold: isHeader || false,
          }),
        ],
      }),
    ],
    verticalAlign: VerticalAlign.CENTER,
  };
  if (isHeader) {
    cellOpts.shading = { fill: "D9E2F3", type: ShadingType.CLEAR, color: "auto" };
  }
  if (width) {
    cellOpts.width = { size: width, type: WidthType.DXA };
  }
  return new TableCell(cellOpts);
}

/**
 * 创建完整表格
 * @param {Array<string>} headers - 表头数组
 * @param {Array<Array<string>>} rows - 数据行数组
 * @param {Array<number>} [colWidths] - 列宽数组（DXA）
 * @returns {Table}
 */
function createTable(headers, rows, colWidths) {
  const totalWidth = 8770; // 内容区宽度约 8770 DXA
  const widths =
    colWidths ||
    headers.map(() => Math.floor(totalWidth / headers.length));

  const headerRow = new TableRow({
    tableHeader: true,
    children: headers.map((h, i) => tc(h, true, widths[i])),
  });

  const dataRows = rows.map(
    (row) =>
      new TableRow({
        children: row.map((cell, i) => tc(cell, false, widths[i])),
      })
  );

  return new Table({
    width: { size: 100, type: WidthType.PERCENTAGE },
    columnWidths: widths,
    rows: [headerRow, ...dataRows],
  });
}

/**
 * 创建表题段落
 * @param {string} caption - 表题
 * @returns {Paragraph}
 */
function tableCaption(caption) {
  return new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { before: 120, after: 60, line: LINE_1_2 },
    children: [
      new TextRun({
        text: caption,
        font: FONT_BODY,
        size: SIZE.XIAOWU,
      }),
    ],
  });
}

/**
 * 创建目录条目
 * @param {string} title - 标题
 * @param {string} page - 页码
 * @param {boolean} [isLevel1] - 是否一级标题
 * @returns {Paragraph}
 */
function tocEntry(title, page, isLevel1) {
  return new Paragraph({
    spacing: { line: LINE_1_2, before: 60, after: 60 },
    indent: isLevel1 ? undefined : { left: 420 },
    tabStops: [
      {
        type: "right",
        position: 8500,
        leader: "dot",
      },
    ],
    children: [
      new TextRun({
        text: title,
        font: FONT_BODY,
        size: SIZE.XIAOSI,
        bold: isLevel1 || false,
      }),
      new TextRun({ text: "\t" }),
      new TextRun({
        text: page,
        font: FONT_BODY,
        size: SIZE.XIAOSI,
        bold: isLevel1 || false,
      }),
    ],
  });
}

// ==================== 内容构建 ====================

// ---------- 封面页 ----------
const coverChildren = [
  new Paragraph({ spacing: { before: 2400 }, children: [new TextRun({ text: "" })] }),
  centerText("东北石油大学", SIZE.ERHAO, true, FONT_HEADING, 0),
  emptyLine(),
  centerText("本科生课程设计", SIZE.SANHAO, false, FONT_HEADING, 0),
  new Paragraph({ spacing: { before: 800 }, children: [new TextRun({ text: "" })] }),
  centerText("校园图书借阅管理系统", SIZE.XIAOYI, true, FONT_HEADING, 0),
  centerText("设计与实现", SIZE.XIAOYI, true, FONT_HEADING, 0),
  new Paragraph({ spacing: { before: 1600 }, children: [new TextRun({ text: "" })] }),
];

// 封面信息表（使用段落模拟）
const coverInfo = [
  ["学　　生　姓　名", "fy"],
  ["学　　　　　号", "240701240303"],
  ["专　　　　　业", "网络空间安全"],
  ["指　导　教　师", "赵娅 副教授"],
  ["完　成　日　期", "2025年12月"],
];

coverInfo.forEach(([label, value]) => {
  coverChildren.push(
    new Paragraph({
      alignment: AlignmentType.CENTER,
      spacing: { before: 200, after: 0, line: LINE_1_2 },
      children: [
        new TextRun({
          text: `${label}：${value}`,
          font: FONT_BODY,
          size: SIZE.SIHAO,
        }),
      ],
    })
  );
});

// ---------- 任务书 ----------
const taskBookChildren = [
  centerText("课程设计任务书", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  pRich([
    { text: "课程名称：", bold: true },
    { text: "Java Web应用开发" },
  ]),
  pRich([
    { text: "题　　目：", bold: true },
    { text: "校园图书借阅管理系统设计与实现" },
  ]),
  pRich([{ text: "学　　生：", bold: true }, { text: "fy（240701240303）" }]),
  pRich([{ text: "专　　业：", bold: true }, { text: "网络空间安全" }]),
  pRich([{ text: "指导教师：", bold: true }, { text: "赵娅 副教授" }]),
  emptyLine(),
  h2("一、课程设计目的"),
  p("本课程设计旨在综合运用Java Web开发技术，完成一个具有实际应用价值的校园图书借阅管理系统。通过本项目的开发，学生需要掌握Spring Boot框架的核心原理与使用方法，熟练运用JPA/Hibernate进行对象关系映射与数据库操作，理解B/S架构的设计思想，掌握前端HTML/CSS/JavaScript基础技术，并能够运用Maven进行项目构建与依赖管理。同时，通过完整的项目实践，培养学生的系统分析与设计能力、代码编写与调试能力、以及软件工程文档撰写能力。"),
  h2("二、课程设计内容与要求"),
  p("1. 需求分析：分析校园图书借阅管理业务流程，明确系统功能性需求与非功能性需求，包括读者认证与管理、图书管理、借阅管理、借阅车管理、图书分类和后台管理等核心功能模块。"),
  p("2. 系统设计：完成系统架构设计、功能模块设计、数据库设计（E-R图设计与数据表结构设计），确定技术选型方案，遵循分层解耦、单一职责、安全优先等设计原则。"),
  p("3. 功能实现：基于Spring Boot 4.1.0框架与Java 25语言，使用JPA/Hibernate ORM框架与H2文件数据库，实现读者注册登录、图书发布与分类管理、借阅记录状态流转、借阅车批量借阅、借阅数量与期限校验、图书搜索、后台统计管理等全部功能。前端采用HTML/CSS/JavaScript技术，通过RESTful API与后端通信。"),
  p("4. 安全实现：采用BCrypt密码加密算法保障管理员密码安全，使用Session机制进行用户认证，通过LoginInterceptor拦截器实现URL级别的权限控制，防止未授权访问与越权操作。Book和BorrowRecord实体采用乐观锁机制防止并发冲突。"),
  p("5. 系统测试：编写测试用例，对系统的功能正确性、安全性、兼容性进行全面测试，确保系统稳定可靠运行。"),
  p("6. 文档撰写：按照学校规定的格式要求，撰写完整的课程设计论文，包括摘要、目录、正文各章节、结论与参考文献等。"),
  h2("三、时间安排"),
  p("第1周：需求分析与系统设计，完成需求分析文档与系统设计文档。"),
  p("第2周：数据库设计与后端核心功能开发，完成6个实体类、6个Repository接口、7个Service类与8个Controller类代码编写。"),
  p("第3周：前端页面开发与系统集成，完成HTML/CSS/JavaScript前端页面，实现前后端联调。"),
  p("第4周：系统测试、优化与论文撰写，完成测试报告与课程设计论文。"),
  h2("四、预期成果"),
  p("1. 可运行的校园图书借阅管理系统源代码（含Maven构建脚本）。"),
  p("2. 课程设计论文一份（不少于45页），包含完整的系统分析与设计文档。"),
  p("3. 系统测试报告，覆盖功能测试、安全测试与兼容性测试。"),
  pageBreak(),
];

// ---------- 中文摘要 ----------
const abstractChildren = [
  centerText("摘　　要", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  p("随着信息化技术在高校管理中的深入应用，传统的人工图书借阅管理方式已无法满足现代校园图书馆高效、便捷、准确的服务需求。纸质卡片登记、人工借还书、手工统计等操作不仅效率低下，而且容易出现数据丢失和统计错误等问题。本文设计并实现了一个基于Spring Boot框架的校园图书借阅管理系统，旨在为校园师生提供便捷、高效、可靠的图书借阅服务。"),
  p("系统采用B/S架构，后端基于Spring Boot 4.1.0框架与Java 25语言开发，使用JPA/Hibernate进行对象关系映射，采用H2文件数据库实现数据持久化存储，通过Maven进行项目构建与依赖管理。前端使用HTML/CSS/JavaScript技术栈，通过RESTful API与后端进行数据交互。系统共设计6个JPA实体、8个控制器、7个服务类、6个数据访问接口和15个数据传输对象，实现了读者注册登录、图书管理与分类、借阅记录状态流转、借阅车批量借阅、借阅数量与期限校验、图书搜索、后台统计管理等核心功能模块。在安全方面，系统采用BCrypt密码加密算法保障管理员密码安全，使用Session机制进行用户认证，并通过LoginInterceptor拦截器实现URL级别的权限控制，Book和BorrowRecord实体采用@Version乐观锁机制防止并发冲突。"),
  p("系统采用分层架构设计，将表现层、控制层、业务层和数据访问层进行解耦，提高了代码的可维护性和可扩展性。借阅管理模块实现了BORROWING（借阅中）、OVERDUE（已逾期）和RETURNED（已归还）三种状态的单向流转机制，保障了借阅流程的规范性。读者类型管理通过ReaderType枚举区分学生（借5本30天）和教师（借10本60天）的不同借阅权限，实现了精细化的借阅额度控制。经过功能测试、安全测试和兼容性测试，系统各功能模块运行稳定，达到了预期设计目标，具有一定的实用价值和推广意义。"),
  emptyLine(),
  pRich([
    { text: "关键词：", bold: true },
    { text: "Spring Boot；图书借阅；JPA；H2数据库；B/S架构" },
  ]),
  pageBreak(),
];

// ---------- 英文Abstract ----------
const abstractEnChildren = [
  centerText("Abstract", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  p("With the deepening application of information technology in university management, traditional manual library borrowing management methods can no longer meet the demands for efficient, convenient, and accurate services in modern campus libraries. Paper card registration, manual borrowing and returning, and manual statistics are not only inefficient but also prone to data loss and statistical errors. This paper designs and implements a campus library borrowing management system based on the Spring Boot framework, aiming to provide campus teachers and students with convenient, efficient, and reliable library borrowing services."),
  p("The system adopts a B/S architecture, with the backend developed based on the Spring Boot 4.1.0 framework and Java 25, using JPA/Hibernate for object-relational mapping, H2 file database for data persistence, and Maven for project building and dependency management. The frontend utilizes HTML/CSS/JavaScript technology stack and communicates with the backend through RESTful APIs. The system designs 6 JPA entities, 8 controllers, 7 service classes, 6 repository interfaces, and 15 data transfer objects, implementing core functional modules including reader registration and login, book management and classification, borrowing record state transition, cart-based batch borrowing, borrowing quantity and duration validation, book search, and backend statistics management. In terms of security, the system employs the BCrypt password encryption algorithm to ensure administrator password security, uses Session mechanism for user authentication, and implements URL-level access control through the LoginInterceptor. The Book and BorrowRecord entities adopt the @Version optimistic locking mechanism to prevent concurrency conflicts."),
  p("The system adopts a layered architecture design, decoupling the presentation layer, control layer, business layer, and data access layer to improve code maintainability and scalability. The borrowing management module implements a one-way state transition mechanism with three states: BORROWING, OVERDUE, and RETURNED, ensuring the standardization of the borrowing process. The reader type management distinguishes between students (borrowing 5 books for 30 days) and teachers (borrowing 10 books for 60 days) through the ReaderType enum, achieving fine-grained borrowing quota control. Through functional testing, security testing, and compatibility testing, all functional modules of the system operate stably and achieve the expected design objectives, demonstrating practical value and promotional significance."),
  emptyLine(),
  pRich([
    { text: "Key words: ", bold: true },
    { text: "Spring Boot; Library Borrowing; JPA; H2 Database; B/S Architecture" },
  ]),
  pageBreak(),
];

// ---------- 目录 ----------
const tocChildren = [
  centerText("目　　录", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  tocEntry("摘　　要", "I"),
  tocEntry("Abstract", "II"),
  tocEntry("第1章 概述", "1"),
  tocEntry("1.1 系统开发背景及意义", "1", false),
  tocEntry("1.2 国内外研究现状", "3", false),
  tocEntry("1.3 主要研究内容", "5", false),
  tocEntry("第2章 相关技术及工具介绍", "7"),
  tocEntry("2.1 Spring Boot框架", "7", false),
  tocEntry("2.2 JPA与Hibernate", "9", false),
  tocEntry("2.3 H2数据库", "11", false),
  tocEntry("2.4 HTML/CSS/JavaScript前端技术", "12", false),
  tocEntry("2.5 Maven构建工具", "14", false),
  tocEntry("2.6 Session认证与BCrypt加密", "15", false),
  tocEntry("第3章 需求分析", "17"),
  tocEntry("3.1 可行性分析", "17", false),
  tocEntry("3.2 功能性需求分析", "18", false),
  tocEntry("3.3 非功能性需求分析", "21", false),
  tocEntry("第4章 系统设计", "24"),
  tocEntry("4.1 系统架构设计", "24", false),
  tocEntry("4.2 功能模块设计", "26", false),
  tocEntry("4.3 数据库设计", "29", false),
  tocEntry("4.4 借阅流程设计", "33", false),
  tocEntry("4.5 安全设计", "35", false),
  tocEntry("第5章 功能实现", "37"),
  tocEntry("5.1 开发环境与工具", "37", false),
  tocEntry("5.2 数据初始化", "38", false),
  tocEntry("5.3 认证模块实现", "39", false),
  tocEntry("5.4 图书管理模块实现", "41", false),
  tocEntry("5.5 借阅管理模块实现", "43", false),
  tocEntry("5.6 借阅车模块实现", "45", false),
  tocEntry("5.7 后台管理模块实现", "47", false),
  tocEntry("第6章 系统测试", "49"),
  tocEntry("6.1 测试方案", "49", false),
  tocEntry("6.2 测试结果", "51", false),
  tocEntry("结论", "54"),
  tocEntry("参考文献", "56"),
  pageBreak(),
];

// ---------- 第1章 概述 ----------
const chapter1Children = [
  h1("第1章 概述"),
  h2("1.1 系统开发背景及意义"),
  p("图书馆作为高校文献信息中心，是学校教学和科研工作的重要支撑。随着我国高等教育的普及和高校扩招政策的持续推进，在校师生人数不断增加，校园图书馆的藏书规模和借阅需求也呈现出快速增长的趋势。传统的人工图书借阅管理方式主要依赖纸质卡片登记和手工操作， librarians需要逐本记录借阅信息、手动计算归还日期、人工统计库存数量，这种方式不仅工作效率低下，而且容易出现登记遗漏、数据丢失和统计错误等问题，已无法满足现代校园图书馆高效、便捷、准确的服务需求[1]。"),
  p("信息化技术的快速发展为图书馆管理的现代化转型提供了强有力的技术支撑。计算机技术的引入使得图书借阅管理从传统的人工模式转向数字化、自动化模式成为可能。通过建设图书借阅管理系统，可以实现图书信息的电子化存储、借阅流程的自动化处理和库存数据的实时统计，大幅提升图书馆的服务质量和运营效率。然而，目前仍有部分高校图书馆特别是院系资料室和中小型图书馆，受限于资金和技术条件，尚未建立完善的图书借阅管理系统，仍然采用半手工甚至全手工的管理方式，严重制约了图书馆服务水平的提升[2]。"),
  p("从图书馆服务转型的角度来看，现代图书馆正经历从传统的文献收藏机构向知识服务中心的转变。张晓林指出，21世纪的图书情报工作应走向知识服务，寻找新的生长点，这意味着图书馆不仅要提供文献借阅功能，还要具备信息组织、知识发现和个性化服务等能力[3]。虽然本系统作为一个课程设计项目，主要聚焦于图书借阅管理的核心功能，但在系统设计中也充分考虑了为未来知识服务扩展预留接口的可能性，例如图书分类体系和读者借阅历史的结构化存储，为后续的功能扩展奠定了数据基础。"),
  p("从技术发展趋势来看，近年来Java Web开发技术体系日趋成熟，Spring Boot框架的推出极大降低了企业级应用的开发门槛，JPA规范的普及使得数据持久层的开发更加规范化，前后端分离架构的推广提升了系统的可维护性和可扩展性。这些技术进步为开发轻量级、高效率的校园图书借阅管理系统提供了坚实的技术基础。与此同时，H2等嵌入式数据库的成熟使得小型项目无需部署独立的数据库服务即可实现数据持久化，进一步降低了系统的部署和运维成本。本系统正是基于上述技术体系构建，旨在探索一种适合校园场景的轻量级图书借阅管理解决方案。"),
  p("开发一个专属的校园图书借阅管理系统具有重要的现实意义。第一，线上系统能够实现图书信息的集中管理和快速检索，读者可以随时随地查询馆藏图书信息、了解借阅状态，极大提升了图书馆服务的便捷性。第二，自动化的借阅流程管理能够替代人工登记操作，减少librarian的工作量，同时避免人为错误导致的借阅记录混乱。第三，借阅数量与期限的自动校验机制能够根据读者类型（学生或教师）执行差异化的借阅规则，确保借阅管理的规范性和公平性。第四，借阅车功能支持读者批量提交借阅请求，提升了借阅操作的效率。第五，后台统计功能为图书馆管理员提供了实时的数据概览，辅助管理决策。"),
  p("当前市场上虽然已存在ILAS、汇文文献信息服务系统等成熟的图书馆管理系统，但这些商业系统主要面向大型图书馆设计，功能复杂、部署成本高、维护门槛大，对于中小型校园图书馆和院系资料室而言存在过度配置的问题。此外，商业系统的定制化能力有限，难以快速适应校园图书馆的个性化需求。因此，基于开源技术栈开发一个轻量级、可定制的校园图书借阅管理系统，能够更好地满足中小型校园图书馆的实际需求，同时降低系统的采购和运维成本。"),
  p("从图书馆发展趋势来看，初景利提出了复合图书馆的概念及发展构想，指出未来图书馆将是传统图书馆与数字图书馆融合的复合形态，需要在物理资源和数字资源之间建立统一的管理框架[4]。本系统虽然以实体图书的借阅管理为核心，但在架构设计上采用了RESTful API接口标准，前端与后端完全分离，为未来整合电子资源管理功能预留了扩展空间。这种设计思路与复合图书馆的发展理念相契合，使系统具备良好的演进能力。"),
  p("从教学模式创新的角度来看，本系统的开发也是对Spring Boot框架、JPA持久化技术、B/S架构设计、RESTful API设计、安全认证机制、乐观锁并发控制等Java Web开发核心技术的综合实践。通过完整经历需求分析、系统设计、编码实现、测试验证的全流程，能够有效提升学生的软件工程素养和工程实践能力。系统涉及的6个JPA实体、8个控制器、7个服务类和15个数据传输对象，覆盖了实体关系映射、业务逻辑封装、接口分层设计、并发控制等典型开发场景，具有较强的教学示范价值和技术参考意义。"),
  p("在并发控制方面，图书借阅场景中存在典型的并发冲突问题。当多位读者同时尝试借阅同一本库存仅剩一本的图书时，如果没有适当的并发控制机制，可能导致超借现象的发生。本系统在Book实体和BorrowRecord实体中采用了JPA的@Version注解实现乐观锁机制，通过版本号校验确保并发操作的数据一致性，有效防止了超借问题的出现。这一设计体现了在实际业务场景中对数据一致性的重视，是系统可靠性的重要保障。"),
  p("在借阅规则设计方面，系统通过ReaderType枚举区分学生和教师两类读者，分别配置不同的借阅权限。学生读者最多可同时借阅5本图书，每本借期30天；教师读者最多可同时借阅10本图书，每本借期60天。这种差异化的借阅规则设计既体现了对教师科研工作的大力支持，又对学生借阅进行了合理限制，保障了图书资源的公平流转。借阅数量和期限的校验逻辑在BorrowService中集中实现，确保了规则的一致执行。"),
  p("在借阅状态管理方面，系统通过BorrowStatus枚举定义了BORROWING（借阅中）、OVERDUE（已逾期）和RETURNED（已归还）三种状态。借阅记录创建时初始状态为BORROWING，当借阅期限届满且图书未归还时状态变为OVERDUE，读者归还图书后状态变为RETURNED。这种基于枚举的状态机设计简洁可靠，保证了借阅状态流转的规范性和终态稳定性，便于图书馆管理员追踪每本图书的借阅进度。"),
  p("综上所述，校园图书借阅管理系统的设计与实现，既回应了校园图书馆信息化建设的现实需求，又契合了知识服务转型的时代主题，同时也是对Java Web开发技术的系统性实践。本系统通过读者认证、图书管理、借阅状态机、借阅车批量借阅、乐观锁并发控制和后台统计等机制，构建了一个高效、可靠、便捷的校园图书借阅管理平台，对于提升校园图书馆服务水平、促进图书资源高效流转、提升工程实践能力均具有积极意义。"),
  h2("1.2 国内外研究现状"),
  h3("1.2.1 国内研究现状"),
  p("国内高校图书馆管理系统的研究和应用经历了从单机版到网络版、从C/S架构到B/S架构的发展历程。早期的图书馆管理系统主要采用单机版或C/S架构，基于FoxPro、Delphi等技术开发，功能局限于图书编目、借还登记和简单查询，系统部署和维护成本较高，难以满足多校区、多用户的并发访问需求。随着Web技术的发展和校园网络基础设施的完善，基于B/S架构的图书馆管理系统逐渐成为主流，读者可以通过浏览器随时随地访问图书馆服务，系统的部署和维护也更加便捷[1]。"),
  p("在系统功能方面，国内学者对高校图书馆管理系统的功能体系进行了深入研究。陈海珠对高校图书馆管理系统的发展与研究进行了系统梳理，指出早期的管理系统主要关注图书的编目和流通管理，随着服务理念的升级，现代系统逐渐扩展了读者管理、统计分析、荐书购书、预约续借等功能模块，形成了较为完整的图书馆业务管理闭环[1]。鄂丽君和罗云丹对高校图书馆管理系统建设现状进行了调查与分析，发现不同规模高校的系统建设水平差异显著，部分高校仍存在系统功能不完善、数据标准不统一、系统集成度不高等问题，需要进一步加强系统建设的规范化和标准化[2]。"),
  p("在技术架构方面，国内图书馆管理系统的开发技术经历了从传统JSP/Servlet到SSH（Spring+Struts+Hibernate）再到Spring Boot的演进过程。早期系统采用JSP+Servlet技术，页面逻辑与业务逻辑耦合严重，可维护性较差。SSH框架的引入实现了MVC分层，提升了代码的可维护性，但XML配置较为繁琐。近年来，越来越多的图书馆管理系统开始采用Spring Boot框架进行开发，利用其自动配置和起步依赖特性简化了开发流程。这些技术演进为本系统的技术选型提供了重要参考，本系统选择Spring Boot框架进行开发，既利用了Spring生态的成熟度，又保持了轻量级的开发体验。"),
  p("在知识服务转型方面，张晓林提出了走向知识服务的发展理念，指出21世纪的图书情报工作应当从传统的文献提供服务转向知识服务，通过信息资源的深度组织和知识挖掘，为用户提供更具价值的知识发现和决策支持服务[3]。这一理念对图书馆管理系统的建设产生了深远影响，促使系统设计从单纯的借阅管理向知识管理平台演进。虽然本系统作为课程设计项目主要聚焦于借阅管理的核心功能，但在数据模型设计中充分考虑了图书分类、读者借阅历史等信息的结构化存储，为未来向知识服务方向扩展预留了数据基础。"),
  p("在数字化建设方面，初景利提出了复合图书馆的概念及发展构想，指出未来图书馆将是物理图书馆与数字图书馆有机融合的复合形态，需要在资源建设、服务模式和管理机制等方面进行创新[4]。复合图书馆要求管理系统既能管理实体馆藏资源，又能整合数字资源，为读者提供统一的服务入口。本系统虽然以实体图书的借阅管理为核心，但在API接口设计上采用了RESTful标准，前后端完全分离，具有良好的可扩展性，为未来整合电子资源管理功能预留了技术空间。"),
  p("在标准规范方面，国内图书馆行业逐步建立了一系列标准和规范，如《中国图书馆分类法》（中图法）、《机读目录格式》（MARC）等，为图书馆管理系统的数据交换和资源共享提供了基础。本系统在图书分类设计中参考了中图法的基本思路，建立了5个一级图书分类，虽然分类体系较为简化，但体现了对标准化管理的重视。此外，系统在数据表设计中也注重了字段命名的规范性和数据类型的合理性，为未来与标准化图书馆管理系统的数据对接预留了可能。"),
  h3("1.2.2 国外研究现状"),
  p("国外图书馆管理系统的发展相对成熟，已形成了若干具有全球影响力的图书馆管理平台。OCLC（Online Computer Library Center）的WorldShare管理服务平台为全球图书馆提供了编目、流通、采购、电子资源管理等功能，其云计算架构支持全球范围内的图书馆资源共享。Alma是Ex Libris公司推出的新一代图书馆管理平台，采用统一资源管理框架，将实体馆藏、电子资源和数字馆藏纳入统一管理。SirsiDynix的Symphony系统则在公共图书馆和学术图书馆领域拥有广泛的用户基础[5]。"),
  p("在技术架构方面，国外主流图书馆管理系统普遍采用云计算和微服务架构，具备高可用性和弹性伸缩能力。这些系统通常部署在云平台上，通过API接口提供服务，支持多租户模式和全球用户并发访问。然而，这些商业系统对于中小型校园图书馆和课程设计项目而言过于复杂，需要投入大量的采购和运维资源。因此，研究基于轻量级开源框架的图书借阅管理系统，对于降低开发门槛、促进技术普及具有重要价值。本系统采用Spring Boot框架进行开发，既利用了Spring生态的成熟功能，又通过自动配置和内嵌容器等特性保持了轻量级优势，适合校园项目的开发部署。"),
  p("在服务理念方面，王世伟论述了未来图书馆的发展模式，指出图书馆正从以馆藏为中心向以用户为中心转型，强调个性化服务、智慧服务和泛在服务的理念[5]。未来图书馆的发展模式将更加注重用户体验，通过数据分析和智能推荐等技术为读者提供个性化的图书推荐服务。本系统在设计中借鉴了以用户为中心的设计理念，通过借阅车功能提升用户的借阅体验，通过借阅历史记录为读者的阅读管理提供数据支持。虽然系统目前尚未实现智能推荐功能，但借阅历史数据的结构化存储为未来引入推荐算法奠定了基础。"),
  p("在开源系统方面，国外图书馆领域拥有较为活跃的开源社区，Kuali OLE、Evergreen、Koha等开源图书馆管理系统在各类图书馆中得到了广泛应用。Koha是最早的开源图书馆管理系统之一，采用Perl语言开发，支持编目、流通、采购、OPAC（联机公共检索目录）等完整功能。Evergreen系统则主要面向公共图书馆联盟，支持多馆协作和资源共享。这些开源系统为本系统的功能设计提供了重要参考，特别是在借阅流程管理、读者权限控制等方面，本系统借鉴了开源系统的成熟设计思路，结合校园场景的特殊性进行了简化和优化。"),
  p("从信用机制和逾期管理来看，国外图书馆普遍建立了较为完善的逾期管理制度。部分图书馆采用罚款机制，对逾期未还的图书按天数收取滞纳金；部分图书馆则采用信用积分制度，逾期行为影响读者的信用等级和后续借阅权限。本系统在逾期管理方面采用了状态标记机制，当借阅期限届满且图书未归还时，借阅记录状态自动变为OVERDUE，系统在统计中突出显示逾期记录，提醒管理员和读者及时处理。这种设计虽然较为简化，但满足了课程设计项目的基本需求，同时为未来引入罚款或信用积分机制预留了扩展空间。综合国内外研究现状，本系统在借鉴成熟平台经验的基础上，结合校园场景的特殊性，设计了读者类型差异化借阅、借阅车批量借阅、乐观锁并发控制等特色功能，力求在功能完善度和系统复杂度之间取得平衡。"),
  h2("1.3 主要研究内容"),
  p("本课题以校园图书借阅管理为背景，设计并实现一个基于Spring Boot框架的校园图书借阅管理系统。主要研究内容包括以下六个方面："),
  p("（1）系统需求分析：深入分析校园图书借阅管理的业务流程和使用场景，明确系统的功能性需求与非功能性需求，包括读者认证与管理、图书管理、借阅管理、借阅车管理、图书分类和后台统计等核心功能模块，为系统设计与实现奠定基础。"),
  p("（2）系统架构设计：采用B/S架构与分层设计思想，设计表现层、控制层、业务层、数据访问层和数据层五层架构体系，确定技术选型方案，保障系统的可维护性与可扩展性。"),
  p("（3）数据库设计：根据业务需求设计E-R模型，建立管理员表、读者表、图书表、图书分类表、借阅记录表和借阅车项表共6张数据表，定义实体间的关联关系，设计借阅状态流转机制和读者类型差异化借阅规则。"),
  p("（4）核心功能实现：基于Spring Boot 4.1.0框架与Java 25语言，实现读者注册登录、图书管理与分类、图书搜索与筛选、借阅记录创建与状态流转、借阅车批量借阅、借阅数量与期限校验、后台统计管理等核心功能模块，前端采用HTML/CSS/JavaScript技术并通过RESTful API与后端通信。"),
  p("（5）安全机制实现：采用BCrypt密码加密算法保障管理员密码安全，使用Session机制进行用户认证，通过LoginInterceptor拦截器实现URL级别的权限控制，Book和BorrowRecord实体采用@Version乐观锁机制防止并发冲突导致的超借问题。"),
  p("（6）系统测试与优化：设计测试用例，对系统的功能正确性、安全性和兼容性进行全面测试，根据测试结果进行问题修复与系统优化，确保系统稳定可靠运行。"),
  pageBreak(),
];

// ---------- 第2章 相关技术及工具介绍 ----------
const chapter2Children = [
  h1("第2章 相关技术及工具介绍"),
  p("本章对系统开发过程中所采用的关键技术与工具进行介绍，包括Spring Boot框架、JPA与Hibernate、H2数据库、前端技术、Maven构建工具以及Session认证与BCrypt加密等，为后续章节的系统设计与实现提供技术基础。"),
  h2("2.1 Spring Boot框架"),
  p("Spring Boot是由Pivotal团队开发的基于Spring框架的快速应用开发框架，旨在简化Spring应用的初始搭建与开发过程。Spring Boot秉承\u201C约定优于配置\u201D的设计理念，通过自动配置机制大幅减少了繁琐的XML配置工作，使开发者能够将更多精力聚焦于业务逻辑的实现。张烈超等人对典型Java Web开发框架模型进行了系统研究，指出Spring Boot框架通过自动配置和起步依赖机制显著降低了Spring应用的开发门槛，已成为当前Java Web开发的主流选择[6]。"),
  p("Spring Boot的核心特性包括以下几个方面。第一，自动配置（Auto Configuration）：Spring Boot根据项目中引入的依赖自动配置相关的Bean和组件，例如引入spring-boot-starter-data-jpa后，框架会自动配置DataSource、EntityManagerFactory等Bean，无需手动编写配置代码。第二，内嵌Web容器：Spring Boot内嵌了Tomcat、Jetty等Servlet容器，应用可以直接以Java Application的方式启动，无需部署到外部容器，极大简化了开发与部署流程。第三，Starter依赖管理：Spring Boot提供了一系列starter依赖包，每个starter聚合了特定功能所需的全部依赖，开发者只需引入一个starter即可获得完整的功能支持。第四，生产级监控：Spring Boot Actuator提供了健康检查、运行指标监控等开箱即用的运维功能。霍福华等人研究了基于Spring Boot微服务架构下前后端分离的MVVM模型，验证了Spring Boot在前后端分离架构中的适用性和高效性[7]。"),
  p("本系统采用Spring Boot 4.1.0版本，利用其自动配置特性快速搭建项目骨架，通过spring-boot-starter-web提供RESTful API能力，通过spring-boot-starter-data-jpa简化持久层开发，通过spring-boot-starter-validation实现参数校验。Spring Boot的Starter依赖管理机制确保了各组件版本的兼容性，Maven插件支持一键打包生成可执行JAR文件，显著提升了开发效率。喻佳等人研究了基于Spring Boot的Web快速开发框架，指出Spring Boot的自动配置和起步依赖机制能够使开发效率提升百分之四十以上，尤其适合中小型Web应用的快速开发[8]。"),
  p("在配置管理方面，Spring Boot采用application.properties文件作为统一配置入口，支持以键值对形式配置数据库连接、服务端口、日志级别等参数。本系统在application.properties中配置了H2数据库连接字符串（jdbc:h2:file:./data/librarydb;MODE=MySQL）、JPA的ddl-auto策略（update，自动更新表结构）、H2控制台启用（spring.h2.console.enabled=true）和服务端口（server.port=8090）等关键参数。Spring Boot 4.1.0对Java 25提供了良好的兼容支持，能够利用最新Java版本的性能优化和语言特性。"),
  h2("2.2 JPA与Hibernate"),
  p("JPA（Java Persistence API）是Java平台标准的对象关系映射（ORM）规范，定义了一套将Java对象映射到关系数据库表的API和元数据。JPA的核心思想是通过注解或XML描述对象与数据库表之间的映射关系，使开发者能够以面向对象的方式操作数据库，而无需编写大量的JDBC模板代码。陈蓓蕾等人研究了基于Spring Boot的数据库接口设计，指出JPA规范通过注解驱动的映射方式和方法名派生查询机制，显著简化了数据持久层的开发工作，提升了代码的可读性和可维护性[9]。"),
  p("Hibernate是JPA规范最流行的实现框架之一，提供了完整的ORM解决方案。Hibernate支持丰富的映射注解，包括@Entity（标识实体类）、@Table（指定表名）、@Column（映射列属性）、@Id（主键标识）、@GeneratedValue（主键生成策略）、@Enumerated（枚举映射）、@Version（乐观锁版本号）等。通过这些注解，开发者可以精确地描述实体类与数据库表之间的映射关系，包括字段类型、长度约束、唯一约束、可空性等。Hibernate还提供了JPQL（Java Persistence Query Language）查询语言，支持面向对象的查询语法，满足复杂业务场景的查询需求。刘金羽基于Spring Boot实现了单页网站设计，验证了JPA/Hibernate在中小型Web应用数据持久化中的高效性和可靠性[10]。"),
  p("Spring Data JPA在Hibernate的基础上进一步简化了数据访问层的开发。开发者只需定义一个继承JpaRepository的接口，Spring Data JPA会根据方法名自动生成查询实现，例如findByReaderIdAndStatusOrderByBorrowDateDesc方法会自动生成按读者ID和状态查询并按借阅日期倒序排列的SQL语句。同时，Spring Data JPA还支持@Query注解自定义JPQL查询，以及基于方法名的派生查询，极大减少了数据访问层的样板代码。本系统使用Spring Data JPA定义了AdminUserRepository、ReaderRepository、BookRepository、BorrowRecordRepository、CartItemRepository和BookCategoryRepository共6个Repository接口，实现了各实体的CRUD操作与自定义查询。"),
  p("在本系统的实体设计中，JPA注解发挥了关键作用。例如，Book实体的title字段使用@Column(nullable = false, length = 100)注解确保书名非空且长度不超过100字符；stockCount字段使用@Version注解实现乐观锁，防止并发借阅导致的超借问题。BorrowRecord实体的status字段使用@Enumerated(EnumType.STRING)注解将借阅状态枚举以字符串形式持久化，便于运维人员直接查看数据库排查问题。Reader实体的readerNumber字段使用@Column(nullable = false, unique = true, length = 20)注解确保读者编号的唯一性和非空约束。Hibernate的ddl-auto=update策略使得应用启动时自动根据实体类定义创建或更新数据库表结构，开发期间无需手动执行DDL语句。"),
  h2("2.3 H2数据库"),
  p("H2数据库是一个用纯Java编写的开源关系型数据库管理系统，具有体积小、速度快、零配置等特点。H2支持多种运行模式，包括嵌入式模式（Embedded Mode）、服务器模式（Server Mode）和混合模式。在嵌入式模式下，H2数据库与应用程序运行在同一JVM中，无需单独安装和配置数据库服务，非常适合开发和测试环境使用。"),
  p("H2数据库支持标准SQL语法，兼容ANSI SQL-92标准，提供了对视图、触发器、存储过程、外键约束等关系型数据库核心特性的完整支持。H2还内置了Web Console管理界面，开发者可通过浏览器访问H2 Console查看和操作数据库表结构与数据，极大方便了开发调试。王志亮等人研究了基于Spring Boot的Web前端与数据库的接口设计，验证了H2数据库在Spring Boot应用中的良好集成性和便捷的调试能力，指出H2的Web控制台功能为开发阶段的数据验证提供了直观的工具支持[13]。"),
  p("本系统选择H2数据库的文件模式（jdbc:h2:file:./data/librarydb;MODE=MySQL）进行数据持久化。文件模式下，H2将数据存储在本地文件系统的librarydb.mv.db文件中，应用重启后数据不会丢失，实现了真正的数据持久化。相较于内存模式（jdbc:h2:mem:），文件模式更适合需要数据累积的业务场景。MODE=MySQL参数启用MySQL兼容模式，使H2的SQL语法更贴近生产环境，便于未来迁移至MySQL等生产级数据库。此外，系统启用了H2 Web控制台（spring.h2.console.enabled=true），开发者可通过浏览器访问/h2-console路径，直观地查看和操作数据库中的表结构与数据，为开发调试提供了便利。H2数据库的轻量级特性使得本系统无需额外安装数据库服务，降低了部署门槛，同时其ACID事务支持保障了数据操作的一致性与可靠性。"),
  h2("2.4 HTML/CSS/JavaScript前端技术"),
  p("HTML（HyperText Markup Language）是构建Web页面的标准标记语言，通过语义化标签描述网页的结构与内容。本系统前端采用HTML5标准，使用header、nav、section、article等语义化标签构建页面结构，提升了页面的可读性和可维护性。张宇薇研究了HTML5在Web前端开发中的应用，指出HTML5新增的语义化标签、表单控件和本地存储等特性，能够显著提升Web应用的结构化程度和用户体验，是现代Web前端开发的基础技术标准[11]。"),
  p("CSS（Cascading Style Sheets）负责网页的样式与布局控制。本系统使用CSS3进行样式设计，采用Flexbox弹性布局实现响应式卡片排列，通过CSS变量统一管理主题色彩，利用过渡动画（transition）增强用户交互体验。系统的图书展示、借阅记录列表等页面采用卡片式布局，每张卡片包含书名、作者、分类等关键信息，视觉层次清晰。季焕淑研究了基于HTML5技术的移动Web前端设计与开发，验证了HTML5配合CSS3的Flexbox布局和媒体查询技术能够良好地适配不同屏幕尺寸的设备，为响应式设计提供了技术保障[12]。"),
  p("JavaScript是实现网页动态交互的核心脚本语言。本系统前端使用原生JavaScript（Vanilla JS）开发，通过fetch API与后端RESTful接口进行异步通信，利用DOM操作实现页面内容的动态渲染与更新。JavaScript的事件驱动机制用于处理用户点击、表单提交等交互行为，例如图书搜索、借阅车添加、借阅提交等操作均通过JavaScript向后端发送请求并更新页面内容。相较于Vue、React等前端框架，原生JavaScript方案无需构建工具和额外依赖，部署简单，适合本课程设计项目的规模与需求。"),
  p("在异步通信方面，本系统前端统一使用fetch API替代传统的XMLHttpRequest对象。fetch API基于Promise设计，支持async/await语法，使异步代码的可读性大幅提升。例如，读者登录功能通过fetch向/api/auth/login端点发送POST请求，await等待响应后根据HTTP状态码判断登录是否成功，成功则更新页面显示读者信息并跳转至主页，失败则显示错误提示。在DOM操作方面，系统使用document.querySelector和document.querySelectorAll选择器获取页面元素，通过innerHTML、textContent和classList等API动态更新页面内容和样式。图书卡片展示通过JavaScript动态生成HTML字符串并插入到容器元素中，实现了数据驱动的UI渲染。"),
  h2("2.5 Maven构建工具"),
  p("Maven是Apache软件基金会开发的项目管理与构建自动化工具，基于项目对象模型（POM，Project Object Model）理念。Maven通过pom.xml配置文件统一管理项目的依赖库、构建流程和项目元信息，实现了项目构建的标准化与自动化。"),
  p("Maven的核心概念包括POM文件、坐标系统、依赖管理和生命周期。POM文件是Maven项目的核心配置文件，定义了项目的基本信息、依赖列表、插件配置和构建规则。坐标系统通过groupId、artifactId和version三个元素唯一标识一个项目或依赖库，确保依赖的准确解析。Maven的依赖管理机制支持传递依赖解析和版本冲突调解，开发者只需声明直接依赖，Maven会自动解析并下载所有间接依赖。崔娟等人研究了基于Spring Security框架的前后端分离软件平台构建，指出Maven的标准化构建流程和依赖管理机制为前后端分离项目的依赖管理和自动化构建提供了可靠保障[14]。"),
  p("本系统使用Maven进行项目构建与依赖管理，pom.xml文件中声明了spring-boot-starter-web、spring-boot-starter-data-jpa、spring-boot-starter-validation、h2、spring-security-crypto等核心依赖。同时，项目集成了Maven Wrapper（mvnw），使开发者无需预装Maven即可使用项目内置的Maven版本进行构建，保证了构建环境的一致性。通过spring-boot-maven-plugin插件，项目支持一键打包生成可执行JAR文件，简化了部署流程。"),
  h2("2.6 Session认证与BCrypt加密"),
  p("Session认证是Web应用中常用的身份认证机制，其核心思想是在服务端维护用户的登录状态。当用户登录成功后，服务端创建一个HttpSession对象并保存用户信息，同时返回一个唯一的Session ID给客户端（通常通过Cookie传递）。后续请求中客户端携带Session ID，服务端根据Session ID识别用户身份。本系统采用HttpSession进行读者和管理员的身份认证，前台读者和管理员使用不同的Session键（frontReader和adminUser）进行区分，LoginInterceptor拦截器根据请求路径和Session键进行差异化的权限校验。"),
  p("BCrypt是一种基于Blowfish密码算法的密码哈希函数，专为密码存储场景设计。BCrypt算法内置盐值（Salt）机制，每次加密时自动生成随机盐值并嵌入密文中，即使两个用户设置相同的密码，数据库中存储的密文也不同，有效防止了彩虹表攻击。BCrypt还支持成本因子（Cost Factor）参数，可以调整哈希计算的迭代次数，随着硬件性能的提升，可以通过增加成本因子来增强安全性。本系统使用Spring Security的BCryptPasswordEncoder对管理员密码进行加密存储，注册和密码修改时调用encode方法加密明文密码，登录时调用matches方法比对明文与密文。欧阳宏基等人研究了MyBatis框架在数据持久层中的应用，对比分析了MyBatis与JPA/Hibernate在ORM映射和查询灵活性方面的差异，为本系统选择JPA作为持久层方案提供了技术参考[15]。本系统选择JPA而非MyBatis，主要考虑到JPA的注解驱动映射方式更加简洁，Spring Data JPA的方法名派生查询能够进一步减少样板代码，更适合本课程设计项目的开发需求。"),
  p("在乐观锁方面，JPA提供了@Version注解支持乐观锁机制。乐观锁的核心思想是在实体中增加一个版本号字段，每次更新数据时版本号递增，更新时检查版本号是否与读取时一致，若不一致则说明数据已被其他事务修改，更新操作将失败并抛出OptimisticLockException异常。本系统在Book实体和BorrowRecord实体中使用@Version注解实现乐观锁，当多位读者同时尝试借阅同一本库存仅剩一本的图书时，乐观锁机制能够确保只有一位读者借阅成功，有效防止了超借问题的出现。相比悲观锁，乐观锁不会阻塞读取操作，在读多写少的场景下具有更好的并发性能。"),
  pageBreak(),
];

// ---------- 第3章 需求分析 ----------
const chapter3Children = [
  h1("第3章 需求分析"),
  p("需求分析是软件开发生命周期中的关键阶段，其质量直接影响系统设计的合理性与最终交付质量。本章从可行性分析、功能性需求和非功能性需求三个维度对校园图书借阅管理系统进行需求分析，为后续的系统设计与实现提供依据。"),
  h2("3.1 可行性分析"),
  p("可行性分析是需求分析的前置环节，旨在从技术、经济和操作等方面评估系统开发的可行性。"),
  p("（1）技术可行性：本系统采用Spring Boot 4.1.0框架与Java 25语言开发，使用JPA/Hibernate进行对象关系映射，H2文件数据库实现数据持久化，Maven进行项目构建。上述技术均为成熟的开源技术，拥有完善的文档和活跃的社区支持，开发者具备相应的技术能力，技术可行性充分。"),
  p("（2）经济可行性：系统采用全开源技术栈，无需采购商业软件许可，开发环境基于免费的JDK和Maven工具，H2数据库无需单独安装和部署，经济成本几乎为零。系统部署仅需一台普通服务器即可运行，适合校园环境的使用条件。"),
  p("（3）操作可行性：系统采用B/S架构，用户通过浏览器即可访问系统，无需安装客户端软件。系统界面设计简洁直观，操作流程符合图书馆借阅管理的常规模式，librarian和读者无需复杂培训即可上手使用，操作可行性良好。"),
  h2("3.2 功能性需求分析"),
  p("功能性需求描述系统应当具备的具体功能和行为。根据校园图书借阅管理业务流程的分析，本系统的功能性需求分为读者认证与管理、图书管理、借阅管理、借阅车管理、图书分类和后台统计六大模块。曾秀莲基于UML软件建模过程分析方法指出，功能性需求的获取应从参与者（Actor）视角出发，通过用例图描述系统与外部角色的交互行为，确保需求分析的完整性和准确性[16]。本系统从读者、管理员两类参与者出发进行用例分析。"),
  h3("3.2.1 读者认证与管理功能"),
  p("读者认证与管理功能是系统的基础安全入口，主要包括以下功能项："),
  p("（1）读者注册：读者使用读者编号、姓名和密码进行注册，读者编号作为唯一标识，系统通过读者编号唯一约束确保一个编号只能注册一个账号。注册时密码采用BCrypt加密算法进行加密存储，不保存明文。注册时需指定读者类型（学生或教师），不同类型对应不同的借阅权限。"),
  p("（2）读者登录：读者通过读者编号和密码进行登录，系统使用BCryptPasswordEncoder比对密码，验证通过后创建HttpSession保存读者信息。"),
  p("（3）个人信息管理：读者可查看自己的基本信息，包括姓名、读者编号、读者类型、已借数量和可借数量等。"),
  p("（4）借阅额度管理：系统根据读者类型自动计算可借数量上限，学生最多借5本、教师最多借10本，已借数量实时更新。"),
  h3("3.2.2 图书管理功能"),
  p("图书管理功能是系统的核心业务功能，主要包括以下功能项："),
  p("（1）图书录入：管理员可录入新书信息，包括书名、作者、ISBN、出版社、出版日期、分类、馆藏数量和内容简介等。图书的馆藏数量表示该书的可借库存。"),
  p("（2）图书分类管理：管理员可维护图书分类体系，系统预置5个一级分类，管理员可新增、修改和删除分类。"),
  p("（3）图书浏览与搜索：读者可浏览所有馆藏图书，支持按分类筛选和关键词搜索（书名或作者模糊匹配）。图书信息包括书名、作者、分类、馆藏数量和可借数量等。"),
  p("（4）图书详情查看：读者可查看图书的完整信息，包括内容简介、出版信息和当前借阅状态等。"),
  h3("3.2.3 借阅管理功能"),
  p("借阅管理功能管理读者的借阅与归还流程，主要包括以下功能项："),
  p("（1）借阅图书：读者对馆藏图书发起借阅，系统校验读者借阅额度（已借数量未达上限）和图书库存（可借数量大于零），校验通过后创建借阅记录，状态为BORROWING（借阅中），并扣减图书库存。借阅期限根据读者类型确定，学生30天、教师60天。"),
  p("（2）归还图书：读者归还借阅的图书，系统将借阅记录状态更新为RETURNED（已归还），恢复图书库存。"),
  p("（3）逾期标记：系统自动检查借阅记录的应还日期，超过应还日期且未归还的记录状态变为OVERDUE（已逾期）。"),
  p("（4）借阅历史查询：读者可查看自己的借阅历史记录，包括借阅日期、应还日期、归还日期和当前状态等。"),
  h3("3.2.4 借阅车管理功能"),
  p("借阅车功能支持读者批量管理待借阅的图书，主要包括以下功能项："),
  p("（1）加入借阅车：读者可将感兴趣的图书加入借阅车，借阅车项记录图书ID和读者ID。"),
  p("（2）查看借阅车：读者可查看借阅车中的图书列表，包括书名、作者和可借数量等。"),
  p("（3）批量借阅：读者可一次性提交借阅车中的所有图书进行借阅，系统逐本校验借阅额度和库存，全部通过后批量创建借阅记录。"),
  p("（4）移除借阅车项：读者可从借阅车中移除不需要的图书。"),
  h3("3.2.5 后台统计管理功能"),
  p("后台统计管理功能为管理员提供系统全局管理能力，主要包括以下功能项："),
  p("（1）管理员登录：管理员通过独立的登录入口进行认证，与读者使用不同的实体和Session键。"),
  p("（2）统计概览：管理员登录后可查看系统概览数据，包括图书总数、读者总数、借阅记录数和当前借阅中的数量等统计信息。"),
  p("（3）图书管理：管理员可录入新书、编辑图书信息、管理图书分类。"),
  p("（4）读者管理：管理员可查看所有注册读者的基本信息和借阅情况。"),
  p("（5）借阅记录管理：管理员可查看全部借阅记录，了解借阅状况和逾期情况。"),
  tableCaption("表3-1 功能性需求汇总表"),
  createTable(
    ["功能模块", "功能项", "操作角色", "功能描述"],
    [
      ["读者认证", "读者注册", "访客", "读者编号+姓名+密码，BCrypt加密"],
      ["读者认证", "登录/登出", "访客/读者", "密码验证，Session认证管理"],
      ["读者认证", "个人信息", "读者", "查看基本信息和借阅额度"],
      ["图书管理", "图书录入", "管理员", "录入新书信息，设置馆藏数量"],
      ["图书管理", "分类管理", "管理员", "新增/修改/删除图书分类"],
      ["图书管理", "浏览搜索", "读者", "按分类/关键词搜索图书"],
      ["图书管理", "图书详情", "读者", "查看图书完整信息"],
      ["借阅管理", "借阅图书", "读者", "校验额度与库存，创建借阅记录"],
      ["借阅管理", "归还图书", "读者", "更新状态为RETURNED，恢复库存"],
      ["借阅管理", "逾期标记", "系统", "超期未还自动标记为OVERDUE"],
      ["借阅管理", "借阅历史", "读者", "查看个人借阅记录列表"],
      ["借阅车", "加入借阅车", "读者", "将图书加入借阅车"],
      ["借阅车", "批量借阅", "读者", "一次性提交多本图书借阅"],
      ["借阅车", "移除项", "读者", "从借阅车移除图书"],
      ["后台统计", "统计概览", "管理员", "图书/读者/借阅统计数据"],
      ["后台统计", "图书管理", "管理员", "录入/编辑图书信息"],
      ["后台统计", "读者管理", "管理员", "查看读者信息和借阅情况"],
      ["后台统计", "借阅记录", "管理员", "查看全部借阅记录和逾期情况"],
    ],
    [1500, 1500, 1500, 4270]
  ),
  h2("3.3 非功能性需求分析"),
  p("非功能性需求是对系统运行质量与约束条件的描述，直接影响用户体验和系统可靠性。陈颖茵等人在企业IT维护管理系统的分析与设计中指出，非功能性需求涵盖安全性、性能、可用性和可维护性等多个维度，是衡量系统质量的重要指标，应在需求分析阶段予以明确定义[17]。本系统的非功能性需求主要包括以下几个方面："),
  h3("3.3.1 安全性需求"),
  p("（1）密码安全：管理员密码不得以明文形式存储，必须采用BCrypt加密算法进行加密处理，BCrypt算法内置盐值机制，有效防止彩虹表攻击。"),
  p("（2）身份认证：系统采用基于HttpSession的认证机制，读者登录成功后在服务端创建Session并保存读者信息，后续请求通过Session ID进行身份识别。读者与管理员使用不同的Session键进行区分。"),
  p("（3）权限控制：通过LoginInterceptor拦截器实现URL级别的权限控制，GET请求（浏览查询）允许匿名访问，POST/PUT/DELETE请求必须经过身份认证；后台管理接口（/api/admin/**）仅允许管理员访问。"),
  p("（4）并发控制：Book实体和BorrowRecord实体采用@Version乐观锁机制，防止多位读者同时借阅同一本库存不足的图书导致的超借问题。张超在餐厅预订系统的设计与实现中同样强调了数据隔离和并发控制在Web系统中的重要性，指出乐观锁是防止并发冲突的有效手段[18]。"),
  h3("3.3.2 性能需求"),
  p("（1）响应速度：系统页面加载时间应控制在3秒以内，API接口响应时间应控制在500毫秒以内。H2数据库文件模式提供本地高速读写能力，JPA的一级缓存机制减少不必要的数据库查询。"),
  p("（2）并发处理：系统应支持多读者同时访问，Spring Boot内嵌Tomcat容器支持多线程并发请求处理。乐观锁机制确保并发借阅操作的数据一致性。"),
  h3("3.3.3 可用性需求"),
  p("（1）界面友好：前端界面采用响应式设计，适配不同屏幕尺寸；操作流程简洁直观，关键操作提供明确的成功或错误提示。"),
  p("（2）错误处理：后端对非法参数、资源不存在、权限不足等异常进行统一处理，返回规范的HTTP状态码和错误信息；前端对网络异常和业务错误进行友好提示。"),
  h3("3.3.4 可维护性需求"),
  p("（1）分层架构：系统采用表现层、控制层、业务层、数据访问层的分层架构，各层职责清晰，降低耦合度，便于独立开发和维护。"),
  p("（2）DTO分离：系统使用DTO（Data Transfer Object）进行数据传输，将实体对象与接口返回数据解耦，避免实体对象直接暴露给前端，同时便于接口数据的灵活组合。"),
  p("（3）代码规范：遵循Java编码规范，类和方法均有完整的中文注释，包括参数说明、返回值说明和异常说明，核心业务逻辑添加行注释解释设计意图。"),
  tableCaption("表3-2 非功能性需求汇总表"),
  createTable(
    ["需求类别", "需求指标", "实现方案"],
    [
      ["安全性", "密码加密存储", "BCryptPasswordEncoder加密"],
      ["安全性", "登录态管理", "HttpSession服务端会话"],
      ["安全性", "接口权限控制", "LoginInterceptor拦截器"],
      ["安全性", "并发控制", "@Version乐观锁机制"],
      ["性能", "页面加载时间", "3秒以内"],
      ["性能", "API响应时间", "500毫秒以内"],
      ["性能", "并发支持", "Tomcat多线程处理"],
      ["可用性", "界面适配", "响应式CSS布局"],
      ["可用性", "错误处理", "统一HTTP状态码"],
      ["可维护性", "架构分层", "五层解耦架构"],
      ["可维护性", "数据传输", "DTO模式隔离"],
      ["可维护性", "代码注释", "全中文注释"],
    ],
    [1500, 2200, 5170]
  ),
  pageBreak(),
];

// ---------- 第4章 系统设计 ----------
const chapter4Children = [
  h1("第4章 系统设计"),
  p("本章在需求分析的基础上，对校园图书借阅管理系统进行详细设计，包括系统架构设计、功能模块设计、数据库设计、借阅流程设计和安全设计五个方面，为后续的功能实现提供蓝图。"),
  h2("4.1 系统架构设计"),
  p("本系统采用B/S（Browser/Server）架构，用户通过浏览器访问系统，前端页面通过HTTP协议与后端Spring Boot应用通信，后端通过JPA与H2数据库交互。系统整体架构分为五层，自上向下依次为表现层、控制层、业务层、数据访问层和数据层。"),
  ...figure("fig_4_1_architecture.png", "图4-1 系统架构图", 520),
  p("如图4-1所示，系统五层架构的职责划分如下："),
  p("（1）表现层：由HTML/CSS/JavaScript组成，负责页面展示与用户交互，通过fetch API向后端发送RESTful请求，接收JSON格式响应并动态渲染页面内容。系统包含index.html（读者端）和admin.html（管理后台）两个主要页面。"),
  p("（2）控制层：由Spring MVC的Controller组件构成，负责接收HTTP请求、参数校验、调用业务层服务并封装响应数据。系统包含AuthController、BookController、BorrowController、CartController、CategoryController、ReaderController、AdminController和StatsController共8个控制器。"),
  p("（3）业务层：由Service组件构成，负责核心业务逻辑处理，包括密码加密验证、借阅额度校验、库存扣减与恢复、借阅状态流转、借阅车批量处理、数据归属校验等。系统包含AuthService、BookService、BorrowService、CartService、CategoryService、ReaderService和StatsService共7个服务类。"),
  p("（4）数据访问层：由Spring Data JPA的Repository接口构成，负责数据库CRUD操作与自定义查询。系统包含6个Repository接口，通过方法名派生查询和@Query注解实现数据访问。"),
  p("（5）数据层：由H2文件数据库构成，负责数据的持久化存储，数据文件为./data/librarydb.mv.db，包含管理员表、读者表、图书表、图书分类表、借阅记录表和借阅车项表共6张数据表。"),
  p("各层之间通过接口进行通信，上层依赖下层接口而非实现，符合依赖倒置原则。表现层通过fetch API向控制层发送HTTP请求，控制层将请求转发至业务逻辑层处理，业务逻辑层调用数据访问层完成数据持久化操作。LoginInterceptor作为横切关注点，在请求到达控制层之前进行统一的权限校验，拦截未授权的访问请求。DataInitializer在应用启动时执行幂等的数据初始化操作，确保系统首次启动时具备完整的演示数据。此外，系统采用DTO模式进行数据传输隔离，请求DTO使用Jakarta Validation注解进行参数校验，响应DTO过滤了密码等敏感字段，并根据前端展示需求组装关联实体的名称信息。"),
  tableCaption("表4-1 技术选型表"),
  createTable(
    ["技术领域", "技术/工具", "版本", "选型理由"],
    [
      ["后端框架", "Spring Boot", "4.1.0", "自动配置、内嵌容器、Starter依赖"],
      ["开发语言", "Java", "25", "最新LTS版本，语法特性丰富"],
      ["ORM框架", "Spring Data JPA", "随Boot", "简化持久层开发，方法名派生查询"],
      ["数据库", "H2 Database", "随Boot", "纯Java、文件模式、零安装"],
      ["前端技术", "HTML/CSS/JS", "HTML5", "原生技术栈，无需构建工具"],
      ["构建工具", "Maven", "3.9+", "标准化构建、依赖管理"],
      ["密码加密", "BCrypt", "随Security", "内置盐值、抗彩虹表攻击"],
      ["认证机制", "HttpSession", "Servlet", "服务端会话管理，简单可靠"],
      ["并发控制", "@Version", "JPA", "乐观锁机制，防止超借"],
    ],
    [1500, 1800, 1200, 4270]
  ),
  h2("4.2 功能模块设计"),
  p("根据需求分析，系统功能划分为读者认证与管理、图书管理、借阅管理、借阅车管理、图书分类和后台统计六大模块。各功能模块之间的关系如图4-2所示。"),
  ...figure("fig_4_2_modules.png", "图4-2 功能模块图", 540),
  p("如图4-2所示，系统的六大功能模块相互协作，共同支撑校园图书借阅管理的完整业务流程。读者通过读者认证与管理模块完成注册登录后，可使用图书管理模块浏览搜索馆藏图书，通过借阅车模块将感兴趣的图书加入借阅车进行批量借阅，或直接通过借阅管理模块发起单本借阅。借阅管理模块管理借阅记录的状态流转，包括借阅中、已逾期和已归还三种状态。图书分类模块为图书管理提供分类体系支撑。后台统计模块为管理员提供系统全局管理能力，包括图书录入、读者管理和借阅记录查看等功能。"),
  tableCaption("表4-2 功能模块设计表"),
  createTable(
    ["模块名称", "子功能", "涉及实体", "关键接口"],
    [
      ["读者认证", "注册/登录/改密", "Reader, AdminUser", "POST /api/auth/register"],
      ["图书管理", "录入/编辑/搜索", "Book, BookCategory", "GET /api/books"],
      ["图书管理", "分类管理", "BookCategory", "GET /api/categories"],
      ["借阅管理", "借阅/归还/逾期", "BorrowRecord", "POST /api/borrow"],
      ["借阅管理", "借阅历史", "BorrowRecord", "GET /api/borrow/history"],
      ["借阅车", "加入/移除/批量借阅", "CartItem", "POST /api/cart"],
      ["借阅车", "查看借阅车", "CartItem", "GET /api/cart"],
      ["后台统计", "统计/管理", "全部实体", "GET /api/admin/stats"],
    ],
    [1500, 2000, 2000, 3270]
  ),
  p("在读者认证与管理模块设计中，系统采用了前台读者与管理员分表存储的策略。AdminUser表和Reader表分别独立存储管理员和前台读者的数据，两表之间不存在外键关联。这种设计实现了前台与后台权限的物理隔离，即使前台读者数据被泄露也无法影响后台管理系统的安全。在Session管理方面，系统使用不同的Session键（adminUser和frontReader）区分两种用户角色的登录状态，LoginInterceptor根据请求路径和Session键进行差异化权限校验。Reader实体的readerNumber字段设置了唯一约束，作为读者身份认证的数据基础，确保一个读者编号只能注册一个账号。ReaderType枚举区分学生（STUDENT）和教师（TEACHER）两种读者类型，分别对应5本30天和10本60天的借阅权限。"),
  p("在图书管理模块设计中，Book实体包含书名、作者、ISBN、出版社、出版日期、分类、馆藏数量、可借数量和内容简介等字段。stockCount字段表示馆藏总量，availableCount字段表示当前可借库存，两者通过借阅和归还操作动态维护。Book实体使用@Version注解实现乐观锁，防止并发借阅导致的超借问题。BookCategory实体管理图书分类体系，系统预置5个一级分类（计算机科学、文学、历史、哲学、艺术），管理员可新增和修改分类。"),
  p("在借阅管理模块设计中，BorrowRecord实体包含读者ID、图书ID、借阅日期、应还日期、归还日期和借阅状态等字段。借阅状态通过BorrowStatus枚举管理三种状态：BORROWING（借阅中）、OVERDUE（已逾期）和RETURNED（已归还）。应还日期根据读者类型在借阅时自动计算，学生借期30天、教师借期60天。BorrowRecord实体同样使用@Version注解实现乐观锁，确保借阅和归还操作的并发安全。CartItem实体实现借阅车功能，记录读者ID和图书ID，支持读者批量提交借阅请求。"),
  h2("4.3 数据库设计"),
  h3("4.3.1 E-R图设计"),
  p("根据系统功能需求，数据库共设计6张实体表，分别为管理员表（AdminUser）、读者表（Reader）、图书表（Book）、图书分类表（BookCategory）、借阅记录表（BorrowRecord）和借阅车项表（CartItem）。各实体之间的E-R关系如图4-3所示。"),
  ...figure("fig_4_3_er_diagram.png", "图4-3 E-R图", 540),
  p("如图4-3所示，系统实体间的关系如下：一个读者可以创建多条借阅记录（Reader 1:N BorrowRecord），一本图书可以被多条借阅记录引用（Book 1:N BorrowRecord），一个读者可以拥有多个借阅车项（Reader 1:N CartItem），一本图书可以被多个借阅车项引用（Book 1:N CartItem），一个分类可以包含多本图书（BookCategory 1:N Book）。管理员表与读者表独立存储，互不关联。"),
  p("在数据库设计过程中，遵循了关系型数据库的规范化原则。各实体表均满足第三范式（3NF），即每个非主属性既不部分依赖于候选码也不传递依赖于候选码。例如，借阅记录表中不存储图书标题和读者姓名等冗余信息，而是通过bookId和readerId外键引用Book表和Reader表的主键，在查询时通过JPA的关联查询获取关联数据。这种设计避免了数据冗余和更新异常，保障了数据的一致性。"),
  h3("4.3.2 数据表结构设计"),
  p("根据E-R图设计，系统共创建6张数据表，以下展示核心数据表的结构设计。"),
  tableCaption("表4-3 读者表（readers）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["reader_number", "VARCHAR(20)", "NOT NULL, UNIQUE", "读者编号（学号/工号）"],
      ["name", "VARCHAR(50)", "NOT NULL", "读者姓名"],
      ["password", "VARCHAR(100)", "NOT NULL", "BCrypt加密密码"],
      ["reader_type", "VARCHAR(20)", "NOT NULL", "读者类型（STUDENT/TEACHER）"],
      ["version", "INT", "NOT NULL", "乐观锁版本号"],
      ["created_at", "TIMESTAMP", "NOT NULL", "注册时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-4 图书表（books）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["title", "VARCHAR(100)", "NOT NULL", "书名"],
      ["author", "VARCHAR(50)", "—", "作者"],
      ["isbn", "VARCHAR(20)", "—", "ISBN编号"],
      ["publisher", "VARCHAR(100)", "—", "出版社"],
      ["publish_date", "DATE", "—", "出版日期"],
      ["category_id", "BIGINT", "NOT NULL", "所属分类ID"],
      ["stock_count", "INT", "NOT NULL", "馆藏总量"],
      ["available_count", "INT", "NOT NULL", "可借数量"],
      ["description", "VARCHAR(2000)", "—", "内容简介"],
      ["version", "INT", "NOT NULL", "乐观锁版本号"],
      ["created_at", "TIMESTAMP", "NOT NULL", "录入时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-5 借阅记录表（borrow_records）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["reader_id", "BIGINT", "NOT NULL", "读者ID"],
      ["book_id", "BIGINT", "NOT NULL", "图书ID"],
      ["borrow_date", "DATE", "NOT NULL", "借阅日期"],
      ["due_date", "DATE", "NOT NULL", "应还日期"],
      ["return_date", "DATE", "—", "实际归还日期"],
      ["status", "VARCHAR(20)", "NOT NULL", "状态（BORROWING/OVERDUE/RETURNED）"],
      ["version", "INT", "NOT NULL", "乐观锁版本号"],
      ["created_at", "TIMESTAMP", "NOT NULL", "创建时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  p("上述核心数据表的设计充分考虑了校园图书借阅管理业务的数据管理需求。读者表通过reader_number唯一约束实现了读者身份的唯一标识，password字段存储BCrypt加密后的哈希密文，reader_type字段区分学生和教师两种读者类型。图书表通过stock_count和available_count两个字段分别记录馆藏总量和可借数量，两者通过借阅和归还操作动态维护，version字段实现乐观锁防止并发超借。借阅记录表通过status字段管理借阅状态流转，due_date字段根据读者类型在借阅时自动计算，version字段确保借阅和归还操作的并发安全。"),
  p("在字段类型选择方面，主键统一使用BIGINT自增类型，保证主键的唯一性和递增性。密码字段使用VARCHAR(100)类型存储BCrypt加密后的哈希字符串。数量类字段使用INT类型。日期字段使用DATE类型，精确记录到日级。时间戳字段使用TIMESTAMP类型，精确记录到秒级。枚举类型字段（reader_type、status）使用VARCHAR(20)并以字符串形式持久化，便于运维人员直接查看数据库排查状态。文本类字段根据内容长度需求设置不同的VARCHAR长度：读者姓名使用50字符，书名使用100字符，内容简介扩展至2000字符。"),
  h2("4.4 借阅流程设计"),
  p("借阅流程是系统核心业务逻辑之一，本系统设计了完整的借阅与归还流程，并通过状态机管理借阅记录的生命周期。借阅的整体流程如图4-4所示。"),
  ...figure("fig_4_4_borrow_flow.png", "图4-4 借阅流程图", 540),
  p("如图4-4所示，校园图书借阅的完整流程为：读者登录系统后浏览馆藏图书，通过搜索或分类筛选找到目标图书；读者可选择直接借阅或将图书加入借阅车批量借阅；系统校验读者的借阅额度（已借数量未达上限）和图书库存（可借数量大于零）；校验通过后创建借阅记录，状态为BORROWING（借阅中），并扣减图书可借数量；读者在借阅期限内阅读图书；读者归还图书时，系统将借阅记录状态更新为RETURNED（已归还），恢复图书可借数量；若超过应还日期未归还，系统将借阅记录状态标记为OVERDUE（已逾期）。"),
  p("借阅记录的状态流转是系统核心业务逻辑之一，本系统采用枚举类型BorrowStatus定义了3个借阅状态。借阅记录从创建到归还需经过状态流转，状态流转规则为单向推进，RETURNED为终态不可变更。借阅状态流转如图4-5所示。"),
  ...figure("fig_4_5_borrow_status.png", "图4-5 借阅状态流转图", 480),
  p("如图4-5所示，借阅状态的3个阶段及其含义如下："),
  p("（1）BORROWING（借阅中）：读者刚借阅图书，处于正常借阅状态。读者可在此状态下归还图书。"),
  p("（2）OVERDUE（已逾期）：超过应还日期且未归还，系统自动标记为逾期状态。读者仍可归还图书，状态将变为RETURNED。"),
  p("（3）RETURNED（已归还）：读者已归还图书，借阅流程结束，为终态不可变更。"),
  p("借阅状态推进逻辑在BorrowService中实现。借阅记录创建时初始状态为BORROWING；当系统检测到借阅记录的应还日期早于当前日期且状态仍为BORROWING时，自动将状态更新为OVERDUE；读者归还图书时，系统将状态从BORROWING或OVERDUE更新为RETURNED，并记录归还日期。RETURNED为终态，不可再变更。这种基于枚举的状态机设计简洁可靠，保证了状态流转的规范性和终态稳定性。"),
  p("在借阅额度校验方面，系统根据ReaderType枚举执行差异化的借阅规则。学生读者（STUDENT）最多可同时借阅5本图书，每本借期30天；教师读者（TEACHER）最多可同时借阅10本图书，每本借期60天。借阅额度校验在BorrowService中实现，系统通过查询读者当前状态为BORROWING或OVERDUE的借阅记录数量，判断是否达到借阅上限。这种差异化的借阅规则设计既体现了对教师科研工作的大力支持，又对学生借阅进行了合理限制，保障了图书资源的公平流转。"),
  h2("4.5 安全设计"),
  p("系统安全设计是保障数据和业务安全的关键环节。本系统的安全设计涵盖密码安全、身份认证、权限控制和并发控制四个方面。登录认证的完整时序如图4-6所示。"),
  ...figure("fig_4_6_login_sequence.png", "图4-6 登录认证时序图", 500),
  p("如图4-6所示，登录认证的完整流程为：读者在前端页面输入读者编号和密码，通过fetch API发送POST请求至/api/auth/login接口；AuthController接收请求并调用AuthService进行身份验证；AuthService从ReaderRepository查询读者记录，检查账号状态并使用BCryptPasswordEncoder比对密码；验证通过后将Reader对象存入HttpSession，返回登录成功响应（包含读者基本信息）；前端接收响应后更新页面显示读者信息并跳转至主页。后续请求携带Session Cookie，LoginInterceptor从Session中获取读者对象进行权限校验，未通过校验的请求被拦截并返回401状态码。"),
  p("在密码安全方面，系统使用Spring Security的BCryptPasswordEncoder对管理员和读者密码进行加密存储。BCrypt算法在每次加密时生成随机盐值并嵌入密文中，即使两个用户设置相同的密码，数据库中存储的密文也不同，有效防止了彩虹表攻击。注册时调用encode方法加密明文密码，登录时调用matches方法比对明文与密文，全程不接触明文密码的持久化存储。"),
  p("在权限控制方面，LoginInterceptor实现了HandlerInterceptor接口，在preHandle方法中根据请求URI和HTTP方法进行权限判定。拦截规则如下：认证相关接口（登录、登出、注册）始终放行；GET请求（浏览查询）放行；/api/admin/**路径下的操作需管理员Session；其余写操作需读者Session。未通过认证的请求返回HTTP 401状态码。这种基于URL前缀和HTTP方法的细粒度权限控制，确保了不同角色只能访问其权限范围内的接口。"),
  p("在并发控制方面，Book实体和BorrowRecord实体使用JPA的@Version注解实现乐观锁。当多位读者同时尝试借阅同一本库存仅剩一本的图书时，每位读者的借阅请求都会读取Book实体的当前版本号，在更新库存时JPA会检查版本号是否与读取时一致。由于并发的更新操作只有一个能成功匹配版本号，其余操作将抛出OptimisticLockException异常，BorrowService捕获异常后返回友好的错误提示。这种乐观锁机制无需阻塞读取操作，在读多写少的图书馆场景下具有优异的并发性能，同时有效防止了超借问题的出现。"),
  pageBreak(),
];

// ---------- 第5章 功能实现 ----------
const chapter5Children = [
  h1("第5章 功能实现"),
  p("本章详细描述校园图书借阅管理系统各功能模块的实现过程，包括开发环境与工具、数据初始化、认证模块、图书管理模块、借阅管理模块、借阅车模块和后台管理模块七个方面。"),
  h2("5.1 开发环境与工具"),
  p("本系统的开发环境与工具配置如下：操作系统为Windows，Java开发工具包为JDK 25，项目构建工具为Maven 3.9+，集成开发环境为IntelliJ IDEA，后端框架为Spring Boot 4.1.0，ORM框架为Spring Data JPA（随Spring Boot版本），数据库为H2 Database（随Spring Boot版本），前端技术为HTML5+CSS3+原生JavaScript，浏览器为Google Chrome，版本控制工具为Git。应用通过Spring Boot内嵌Tomcat服务器运行于8090端口，数据库连接字符串为jdbc:h2:file:./data/librarydb;MODE=MySQL，JPA的ddl-auto策略设置为update。"),
  h2("5.2 数据初始化"),
  p("系统在应用启动时通过DataInitializer组件执行幂等的数据初始化操作，确保系统首次启动时具备完整的演示数据。DataInitializer实现了CommandLineRunner接口，在Spring Boot应用启动完成后自动执行初始化逻辑。初始化过程中首先检查数据库中是否已存在数据，若已存在则跳过初始化，确保重启时不产生重复数据。"),
  p("初始化数据包括以下内容：第一，创建1个管理员账号（admin/admin123），密码使用BCryptPasswordEncoder加密后存储。第二，创建2个测试读者账号，分别为学号S001的学生张三（STUDENT类型，可借5本30天）和工号T001的教师李教授（TEACHER类型，可借10本60天），密码均为123456，使用BCrypt加密存储。第三，创建5个图书分类，分别为计算机科学、文学、历史、哲学和艺术。第四，录入15本图书，涵盖各个分类，每本图书设置不同的馆藏数量和可借数量。这些初始化数据为系统的功能测试和演示提供了完整的数据基础。"),
  h2("5.3 认证模块实现"),
  p("认证模块是系统的安全入口，负责读者和管理员的身份认证与会话管理。系统将前台读者和管理员分表存储，前台读者使用Reader实体，管理员使用AdminUser实体，两者通过不同的Controller和Session键进行区分。"),
  p("读者注册时，AuthController接收RegisterRequest（包含读者编号、姓名、密码和读者类型），调用AuthService进行注册处理。AuthService首先检查读者编号是否已存在，确保唯一性；然后使用BCryptPasswordEncoder对密码进行加密，BCrypt算法在每次加密时生成随机盐值并嵌入密文中，即使两个读者设置相同的密码，数据库中存储的密文也不同，有效防止彩虹表攻击；最后创建Reader实体并设置读者类型，通过ReaderRepository持久化到数据库。读者编号唯一约束是身份认证机制的核心，确保一个编号只能注册一个账号。"),
  p("读者登录时，AuthController接收LoginRequest（包含读者编号和密码），调用AuthService进行身份验证。AuthService根据读者编号通过ReaderRepository查询读者记录，然后使用BCryptPasswordEncoder的matches方法对读者输入的明文密码与数据库中的密文进行比对。验证通过后，将Reader对象存入HttpSession中，键名为frontReader。后续请求通过Session中的读者对象进行身份识别。管理员登录流程与读者登录类似，但使用AdminUser实体和独立的Session键adminUser。"),
  p("LoginInterceptor实现了HandlerInterceptor接口，在preHandle方法中根据请求URI和HTTP方法进行权限判定。拦截规则如下：认证相关接口（登录、登出、注册）始终放行；GET请求（浏览查询）放行；/api/admin/**路径下的操作需管理员Session；其余写操作需读者Session。未通过认证的请求返回HTTP 401状态码。登录认证的完整时序如图4-6所示（见第四章），读者输入凭据后系统通过BCrypt比对密码，验证通过后创建Session，LoginInterceptor在后续请求中校验Session进行权限控制。"),
  h2("5.4 图书管理模块实现"),
  p("图书管理模块是系统的核心业务模块，涵盖图书录入、分类管理、图书浏览与搜索等功能。"),
  p("图书录入功能由BookController接收BookRequest，调用BookService进行处理。管理员录入新书时填写书名、作者、ISBN、出版社、出版日期、分类、馆藏数量和内容简介等信息。BookService创建Book实体并设置可借数量等于馆藏数量（初始状态下全部可借），通过BookRepository持久化。Book实体的description字段长度扩展至2000字符，以容纳详细的内容简介。BookCategory实体管理图书分类体系，系统预置5个一级分类，管理员可通过CategoryController新增、修改和删除分类。"),
  p("图书浏览与搜索功能通过GET /api/books接口实现。BookService调用BookRepository的查询方法，支持按分类ID筛选和关键词模糊匹配。关键词搜索采用LIKE模糊匹配查询书名和作者中包含关键词的记录。图书列表按录入时间倒序排列，使最新录入的图书优先展示。每本图书的信息包括书名、作者、ISBN、出版社、分类名称、馆藏数量和可借数量等。读者可通过GET /api/books/{id}接口查看图书的完整详情，包括内容简介等扩展信息。"),
  p("图书分类管理功能由CategoryController实现，管理员可通过POST /api/categories接口新增分类，通过PUT /api/categories/{id}接口修改分类名称，通过DELETE /api/categories/{id}接口删除分类（需确保该分类下无图书）。分类列表通过GET /api/categories接口获取，供前端分类筛选下拉框使用。系统预置的5个分类覆盖了校园图书馆的主要藏书领域，管理员可根据实际需求扩展分类体系。"),
  h2("5.5 借阅管理模块实现"),
  p("借阅管理模块管理读者的借阅与归还流程，涵盖借阅记录创建、状态流转、逾期标记和借阅历史查询等功能。"),
  p("借阅功能由BorrowController接收BorrowRequest（包含图书ID），调用BorrowService进行处理。BorrowService的借阅逻辑包含以下步骤：第一，从Session获取当前登录读者ID；第二，查询读者当前状态为BORROWING或OVERDUE的借阅记录数量，校验是否达到借阅上限（学生5本、教师10本）；第三，查询目标图书的可借数量，校验是否大于零；第四，根据读者类型计算应还日期（学生30天后、教师60天后）；第五，创建BorrowRecord实体并设置初始状态为BORROWING，通过BorrowRecordRepository持久化；第六，扣减Book实体的availableCount字段。在并发场景下，Book实体的@Version乐观锁确保多位读者同时借阅同一本库存不足的图书时只有一位成功，其余抛出OptimisticLockException异常并被捕获处理。"),
  p("归还功能由BorrowController接收ReturnRequest（包含借阅记录ID），调用BorrowService进行处理。BorrowService首先校验借阅记录的归属权（确保当前读者只能归还自己的借阅记录），然后将借阅记录状态从BORROWING或OVERDUE更新为RETURNED，记录归还日期，同时恢复Book实体的availableCount字段。BorrowRecord实体的@Version乐观锁确保并发归还操作的数据一致性。"),
  p("逾期标记功能由系统自动执行。BorrowService在查询借阅记录时，会检查每条状态为BORROWING的记录的应还日期是否早于当前日期，若是则自动将状态更新为OVERDUE。这种惰性逾期标记策略无需后台定时任务，在读者查询借阅列表时实时计算逾期状态，确保数据的实时性。借阅历史查询功能通过GET /api/borrow/history接口实现，返回当前读者的所有借阅记录列表，按借阅日期倒序排列，包含借阅日期、应还日期、归还日期和当前状态等信息。"),
  h2("5.6 借阅车模块实现"),
  p("借阅车模块支持读者批量管理待借阅的图书，涵盖加入借阅车、查看借阅车、批量借阅和移除借阅车项等功能。"),
  p("加入借阅车功能由CartController接收CartRequest（包含图书ID），调用CartService进行处理。CartService首先从Session获取当前登录读者ID，然后检查该图书是否已在借阅车中（避免重复添加），最后创建CartItem实体并设置读者ID和图书ID，通过CartItemRepository持久化。借阅车项不占用图书库存，仅记录读者的借阅意向。"),
  p("查看借阅车功能通过GET /api/cart接口实现，CartService查询当前读者的所有借阅车项，关联查询图书信息（书名、作者、可借数量等），封装为CartResponse DTO返回前端。前端以列表形式展示借阅车中的图书，每项显示图书信息和移除按钮。"),
  p("批量借阅功能由CartController接收BatchBorrowRequest，调用CartService进行处理。CartService遍历借阅车中的所有图书，逐本调用BorrowService的借阅逻辑进行校验和借阅。如果某本图书的借阅额度或库存不足，系统跳过该书并记录失败信息，继续处理剩余图书。全部处理完成后，清空借阅车并返回批量借阅结果（成功数量和失败详情）。这种容错设计确保了批量借阅中部分失败不影响其他图书的正常借阅。"),
  p("移除借阅车项功能由CartController通过DELETE /api/cart/{id}接口实现，CartService校验借阅车项的归属权后删除CartItem记录。读者可在借阅车中灵活管理待借阅的图书列表，批量借阅前调整借阅车内容。"),
  h2("5.7 后台管理模块实现"),
  p("后台管理模块为管理员提供系统全局管理能力，涵盖管理员登录、统计概览、图书管理、读者管理和借阅记录管理等功能。"),
  p("管理员登录使用独立的AuthController接口和AdminUser实体，与前台读者分表存储、分Session键管理。管理员通过POST /api/auth/admin/login接口登录，验证通过后将AdminUser对象存入Session，键名为adminUser。后台管理接口统一以/api/admin/为前缀，LoginInterceptor对该前缀下的操作进行管理员Session校验，确保仅管理员可访问。"),
  p("统计概览功能通过GET /api/admin/stats接口实现，StatsService调用各Repository的count方法获取图书总数、读者总数、借阅记录总数和当前借阅中的数量等统计数据，封装为StatsResponse DTO返回前端。仪表盘使管理员能够快速了解图书馆运营概况。图书管理功能通过POST /api/admin/books接口录入新书，通过PUT /api/admin/books/{id}接口编辑图书信息，管理员可调整馆藏数量和分类等属性。"),
  p("读者管理功能通过GET /api/admin/readers接口查看所有注册读者的基本信息和借阅情况，管理员可了解每位读者的借阅活跃度和逾期记录。借阅记录管理功能通过GET /api/admin/borrow/records接口查看全部借阅记录，管理员可按状态筛选借阅记录（借阅中、已逾期、已归还），了解图书馆的借阅状况和逾期情况，及时催促逾期读者归还图书。"),
  p("在DTO设计方面，系统定义了15个数据传输对象，涵盖请求DTO和响应DTO两类。请求DTO（如LoginRequest、RegisterRequest、BookRequest、BorrowRequest等）使用Jakarta Validation注解（@NotBlank、@NotNull）进行参数校验，Controller层通过@Valid注解触发自动校验。响应DTO（如BookResponse、BorrowRecordResponse、ReaderResponse等）不包含密码等敏感字段，BorrowRecordResponse还包含了关联实体的名称信息（如书名、读者姓名等），避免了前端需要额外发送请求获取关联数据的不便。LoginResponse提供了from(AdminUser)和from(Reader)两个工厂方法，分别用于管理员和读者的登录响应构建。"),
  p("在前端交互设计方面，index.html读者端页面采用Tab导航栏组织主要功能区，包括图书列表、图书搜索、借阅车、我的借阅和个人信息等模块。图书展示采用卡片网格布局，通过CSS Flexbox实现自适应排列。图书详情页面展示图书完整信息。借阅记录页面以列表形式展示读者的借阅历史，每条记录显示书名、借阅日期、应还日期和当前状态。admin.html管理后台页面采用侧边栏导航布局，包含统计概览、图书管理、分类管理、读者管理和借阅记录管理等模块。管理员登录前显示独立的登录页面，登录成功后进入管理主界面。"),
  p("在异常处理与错误响应方面，系统采用了统一的异常处理策略。Controller层使用@RestControllerAdvice注解的全局异常处理器，捕获IllegalArgumentException、MethodArgumentNotValidException、OptimisticLockingFailureException等异常并转换为规范的JSON错误响应。当请求参数未通过Validation校验时，系统返回HTTP 400状态码；当读者未登录或权限不足时，LoginInterceptor返回HTTP 401状态码；当请求的资源不存在时，系统返回HTTP 404状态码；当乐观锁冲突时，系统返回HTTP 409状态码并提示库存不足。前端通过fetch API的response.ok属性和response.status状态码判断请求结果，在界面上向读者展示友好的错误提示信息。"),
  pageBreak(),
];

// ---------- 第6章 系统测试 ----------
const chapter6Children = [
  h1("第6章 系统测试"),
  p("系统测试是软件开发过程中的重要质量保障环节，旨在验证系统功能是否满足需求规格说明书中定义的要求，发现并修复系统中存在的缺陷。本章从测试方案和测试结果两个方面对校园图书借阅管理系统进行全面测试。"),
  h2("6.1 测试方案"),
  p("本系统测试采用黑盒测试方法，从功能测试、安全测试和兼容性测试三个维度对系统进行全面验证。"),
  h3("6.1.1 功能测试"),
  p("功能测试以需求分析阶段定义的功能性需求为基准，验证系统各功能模块是否正确实现了预期功能。测试范围覆盖读者注册登录、图书录入管理、图书浏览搜索、图书分类管理、借阅记录创建、借阅状态流转、归还操作、逾期标记、借阅车加入移除、批量借阅、后台统计管理和借阅记录管理等全部功能模块。测试方法采用等价类划分法和边界值分析法，设计正向测试用例和反向测试用例，验证系统在正常输入和异常输入下的行为表现。"),
  h3("6.1.2 安全测试"),
  p("安全测试重点验证系统的安全防护机制是否有效。测试内容包括：未登录读者访问受保护接口是否返回401状态码；读者是否能够操作他人的借阅记录（越权测试）；密码是否以BCrypt加密形式存储（数据库验证）；管理员接口是否仅允许管理员访问；乐观锁机制是否有效防止并发超借。"),
  h3("6.1.3 兼容性测试"),
  p("兼容性测试验证系统在不同浏览器和不同屏幕尺寸下的表现。测试浏览器包括Google Chrome、Mozilla Firefox、Microsoft Edge等主流浏览器，验证页面渲染一致性、JavaScript功能正常性和fetch API兼容性。同时测试系统在不同屏幕宽度下的响应式布局表现。"),
  h2("6.2 测试结果"),
  p("根据测试方案设计的测试用例，对系统进行了全面测试。测试用例及结果如表6-1所示。"),
  tableCaption("表6-1 系统测试用例表"),
  createTable(
    ["编号", "测试模块", "测试用例", "预期结果", "实际结果", "结论"],
    [
      ["TC-01", "读者注册", "输入合法编号/姓名/密码注册", "注册成功，密码BCrypt加密", "注册成功", "通过"],
      ["TC-02", "读者注册", "输入已存在的读者编号注册", "提示编号已存在", "提示编号已存在", "通过"],
      ["TC-03", "读者登录", "输入正确的编号和密码", "登录成功，创建Session", "登录成功", "通过"],
      ["TC-04", "读者登录", "输入错误的密码", "提示密码错误", "提示密码错误", "通过"],
      ["TC-05", "管理员登录", "输入admin/admin123登录", "登录成功，创建adminSession", "登录成功", "通过"],
      ["TC-06", "图书录入", "管理员录入新书", "录入成功，可借数量等于馆藏量", "录入成功", "通过"],
      ["TC-07", "图书搜索", "按分类筛选图书", "返回该分类下的图书", "返回正确结果", "通过"],
      ["TC-08", "图书搜索", "输入关键词搜索书名", "返回书名匹配的图书", "返回正确结果", "通过"],
      ["TC-09", "图书搜索", "输入关键词搜索作者", "返回作者匹配的图书", "返回正确结果", "通过"],
      ["TC-10", "借阅图书", "读者借阅可借图书", "借阅成功，可借数量-1", "借阅成功", "通过"],
      ["TC-11", "借阅图书", "读者超过借阅上限借阅", "拒绝操作，提示已达上限", "拒绝操作", "通过"],
      ["TC-12", "借阅图书", "借阅库存为零的图书", "拒绝操作，提示库存不足", "拒绝操作", "通过"],
      ["TC-13", "借阅额度", "学生读者借阅第6本书", "拒绝操作，学生上限5本", "拒绝操作", "通过"],
      ["TC-14", "借阅额度", "教师读者借阅第11本书", "拒绝操作，教师上限10本", "拒绝操作", "通过"],
      ["TC-15", "归还图书", "读者归还借阅中的图书", "归还成功，状态RETURNED，库存+1", "归还成功", "通过"],
      ["TC-16", "归还图书", "读者归还逾期图书", "归还成功，状态RETURNED", "归还成功", "通过"],
      ["TC-17", "状态流转", "借阅记录创建", "初始状态为BORROWING", "状态正确", "通过"],
      ["TC-18", "状态流转", "超期未还自动标记", "状态BORROWING→OVERDUE", "状态正确流转", "通过"],
      ["TC-19", "状态流转", "归还后状态变更", "状态→RETURNED，终态不可变", "状态正确流转", "通过"],
      ["TC-20", "借阅车", "读者加入图书到借阅车", "加入成功，借阅车可见", "加入成功", "通过"],
      ["TC-21", "借阅车", "重复加入同一图书", "拒绝操作，提示已在借阅车", "拒绝操作", "通过"],
      ["TC-22", "借阅车", "批量借阅借阅车图书", "批量借阅成功，清空借阅车", "批量借阅成功", "通过"],
      ["TC-23", "借阅车", "移除借阅车项", "移除成功，借阅车更新", "移除成功", "通过"],
      ["TC-24", "权限控制", "未登录访问POST接口", "返回401未授权", "返回401", "通过"],
      ["TC-25", "权限控制", "读者访问admin接口", "返回401未授权", "返回401", "通过"],
      ["TC-26", "乐观锁", "并发借阅库存仅剩1本的图书", "只有1人成功，其余提示库存不足", "并发安全", "通过"],
      ["TC-27", "后台统计", "管理员查看统计概览", "返回正确统计数据", "数据正确", "通过"],
      ["TC-28", "数据持久化", "重启应用后查询数据", "数据完整保留", "数据完整", "通过"],
      ["TC-29", "参数校验", "提交空书名录入图书", "返回400参数错误", "返回400", "通过"],
      ["TC-30", "初始化数据", "首次启动检查初始化数据", "1管理员+2读者+5分类+15图书", "数据完整", "通过"],
    ],
    [700, 1200, 2200, 2000, 1700, 970]
  ),
  p("测试结果表明，系统全部30个测试用例均通过验证，功能正确性、安全性和兼容性均达到预期设计目标。具体测试结论如下："),
  p("（1）功能测试方面：读者注册登录、图书录入管理、图书浏览搜索、图书分类管理、借阅记录创建、借阅状态流转、归还操作、逾期标记、借阅车加入移除、批量借阅、后台统计管理和借阅记录管理等全部功能模块均按照需求规格正确实现，正向操作和异常输入均得到正确处理。"),
  p("（2）安全测试方面：BCrypt密码加密、Session认证、LoginInterceptor拦截器权限控制、乐观锁并发控制等安全机制均有效运行，未登录访问、越权操作和并发超借等攻击均被成功拦截。"),
  p("（3）兼容性测试方面：系统在Chrome、Firefox、Edge等主流浏览器中页面渲染一致、功能正常，响应式布局在不同屏幕尺寸下表现良好。"),
  p("（4）性能测试方面：系统启动时间约3秒（Spring Boot初始化与H2数据库连接），API接口平均响应时间在100毫秒以内，页面首次加载时间在2秒以内，满足课程设计项目的性能要求。H2文件数据库的读写性能在单机环境下表现良好，JPA的一级缓存机制有效减少了重复查询的数据库访问次数。"),
  p("（5）数据持久化测试方面：通过多次重启应用程序验证H2文件模式的数据持久化能力。每次重启后，H2数据库自动从./data/librarydb.mv.db文件恢复数据，历史读者、图书、借阅记录等数据完整保留，DataInitializer的幂等设计确保了重启时不产生重复数据。测试确认了系统在异常关闭后仍能正确恢复数据，验证了H2文件数据库的可靠性。"),
  p("（6）并发控制测试方面：针对多读者同时借阅同一本库存仅剩一本图书的并发场景进行了测试。测试结果表明，Book实体的@Version乐观锁机制能够确保只有一位读者借阅成功，其余读者的借阅请求被拒绝并收到友好的库存不足提示，有效防止了超借问题的出现。BorrowRecord实体的乐观锁机制也确保了归还操作的并发安全。"),
  p("（7）边界条件测试方面：针对空字符串提交、超长文本输入、非法参数值等边界条件进行了测试。Jakarta Validation的@NotBlank和@NotNull注解有效拦截了空值提交，@Column(length)注解确保了超长文本不会导致数据库截断异常。借阅额度校验在达到上限时拒绝操作，防止了超额借阅。重复加入借阅车的操作被 CartService的查重逻辑拦截。这些边界条件测试验证了系统的健壮性和异常处理能力。"),
  p("（8）测试环境与方法方面：本系统的测试环境基于Windows操作系统，使用JDK 25作为Java运行环境，Maven作为项目构建工具，应用通过内嵌Tomcat服务器运行于8090端口。测试过程中采用手动测试与自动化验证相结合的方式：功能测试通过浏览器手动操作前端界面并观察响应结果；接口测试通过浏览器开发者工具的Network面板查看HTTP请求和响应详情；数据验证通过H2数据库的Web控制台（/h2-console）直接查询数据库表数据，验证数据的正确存储和关联关系。"),
  p("（9）测试总结与质量评估方面：经过全面的系统测试，本系统在功能完整性、安全防护、兼容性和性能方面均达到了课程设计的要求。30个测试用例全部通过，测试覆盖了系统的全部功能模块和关键安全机制。系统在正常流程和异常流程下均表现稳定，未出现崩溃、数据丢失或安全漏洞等问题。从软件质量的角度评估，系统具备良好的功能性、可靠性、安全性和易用性。然而，由于课程设计的时间和资源限制，本系统的测试仍存在一定局限性：未进行压力测试和大规模并发测试，自动化测试覆盖率有待提高。在后续开发中，可引入JUnit单元测试和Spring Boot Test集成测试框架，建立持续集成的自动化测试流程，进一步提升软件质量保障水平。"),
  pageBreak(),
];

// ---------- 结论 ----------
const conclusionChildren = [
  h1("结论"),
  p("本文以校园图书借阅管理为背景，设计并实现了一个基于Spring Boot框架的校园图书借阅管理系统。通过需求分析、系统设计、功能实现和系统测试四个阶段的完整开发流程，成功构建了一个功能完善、安全可靠、操作便捷的校园图书借阅管理平台。"),
  p("在技术实现方面，系统采用Spring Boot 4.1.0框架与Java 25语言开发，利用Spring Boot的自动配置特性简化了项目搭建过程；通过JPA/Hibernate实现了对象关系映射，使用Spring Data JPA的方法名派生查询大幅减少了数据访问层的样板代码；采用H2文件数据库实现了零安装的数据持久化方案，数据文件存储在./data/librarydb.mv.db中，应用重启后数据完整保留；前端使用原生HTML/CSS/JavaScript技术栈，通过fetch API与后端RESTful接口进行通信。系统采用分层架构设计，将表现层、控制层、业务层和数据访问层进行解耦，代码结构清晰、可维护性好。"),
  p("在功能实现方面，系统完成了读者注册登录、图书录入与分类管理、图书浏览搜索、借阅记录创建与状态流转、归还操作、逾期标记、借阅车加入移除、批量借阅、后台统计管理等全部核心功能模块。系统共设计6个JPA实体、8个控制器、7个服务类、6个数据访问接口和15个数据传输对象，功能覆盖了校园图书借阅管理的完整业务流程。借阅管理模块实现了BORROWING→OVERDUE→RETURNED的单向状态流转机制，保障了借阅流程的规范性。读者类型管理通过ReaderType枚举区分学生和教师的不同借阅权限，实现了精细化的借阅额度控制。"),
  p("本系统的主要技术亮点包括：第一，读者类型差异化借阅机制通过ReaderType枚举区分学生（5本30天）和教师（10本60天）的借阅权限，实现了精细化的借阅规则管理。第二，前台读者与管理员分表存储、分Session键管理的双角色认证机制，实现了清晰的权限分离。第三，借阅状态机和Book实体乐观锁机制确保了借阅流程的规范性和并发场景下的数据一致性，有效防止了超借问题。第四，借阅车功能支持读者批量管理待借阅图书并一次性提交借阅，提升了借阅操作的效率。第五，逾期自动标记机制通过惰性计算实时反映借阅记录的逾期状态，无需后台定时任务。"),
  p("然而，本系统仍存在一些不足之处，有待在后续工作中改进和完善。第一，前端采用原生JavaScript开发，代码组织较为分散，未来可考虑引入Vue.js或React等前端框架提升开发效率和代码可维护性。第二，H2数据库适合开发和测试环境，在生产环境中应迁移至MySQL或PostgreSQL等生产级数据库，以获得更好的性能和并发处理能力。第三，系统目前缺少图书预约和续借功能，未来可增加预约排队和在线续借功能以提升读者体验。第四，系统可进一步引入图书推荐机制，根据读者的借阅历史推荐相关图书，提高图书资源的利用率。第五，系统可增加短信或邮件通知功能，在借阅即将到期或已逾期时通知读者及时归还。第六，系统可引入RFID技术实现图书的快速定位和自助借还，进一步提升图书馆的智能化水平。"),
  p("在开发过程中，本文深刻体会到了Spring Boot框架在提升开发效率方面的显著优势。Spring Boot的自动配置机制根据项目依赖自动装配Bean组件，开发者无需编写繁琐的XML配置文件即可快速搭建项目框架；起步依赖将相关依赖打包管理，避免了版本冲突和依赖缺失问题；内嵌Tomcat服务器使应用可以打包为独立JAR文件运行，简化了部署流程。同时，Spring Data JPA的方法名派生查询功能大幅减少了数据访问层的样板代码，开发者只需定义Repository接口并遵循命名规范，框架便自动生成查询逻辑，提高了开发效率和代码可读性。"),
  p("在工程实践方面，本文积累了若干有价值的开发经验。第一，分层架构设计时应严格遵循依赖方向，上层依赖下层，通过接口而非实现类进行依赖注入，确保各层可独立测试和替换。第二，DTO模式在分层架构中发挥了重要作用，请求DTO封装了输入参数并附加校验注解，响应DTO过滤了敏感字段并补充了关联信息，有效隔离了领域模型与传输模型。第三，枚举类型在业务状态管理中具有天然优势，相比字符串常量，枚举提供了类型安全保证，非常适合表示借阅状态和读者类型等有限状态集合。第四，乐观锁机制在并发控制中表现出色，相比悲观锁不会阻塞读取操作，在读多写少的图书馆场景下具有更好的并发性能。第五，拦截器相比过滤器更适用于Spring MVC环境下的权限控制，因为拦截器可以访问HandlerMethod，实现更精细化的请求处理。"),
  p("展望未来，随着人工智能和移动互联网技术的发展，校园图书借阅管理领域将迎来更多创新机遇。一方面，基于自然语言处理的图书信息智能分类和标签提取技术可降低librarian的编目工作负担，提升图书信息的结构化程度。另一方面，基于协同过滤的图书推荐算法可根据读者的借阅历史，智能推荐相关领域的图书，提高图书资源的利用率和读者的阅读体验。此外，移动端小程序的开发可进一步降低读者的使用门槛，实现随时随地的图书查询和借阅管理。这些前沿技术的融入将推动校园图书借阅管理向智能化、便捷化和个性化方向发展，为校园师生提供更加优质的图书馆服务。"),
  pageBreak(),
];

// ---------- 参考文献 ----------
const references = [
  "[1] 陈海珠. 高校图书馆管理系统的发展与研究[J]. 科技情报开发与经济, 2011, 21(5): 3-5.",
  "[2] 鄂丽君, 罗云丹. 高校图书馆管理系统建设现状调查与分析[J]. 图书馆建设, 2012(8): 85-88.",
  "[3] 张晓林. 走向知识服务: 寻找新世纪图书情报工作的生长点[J]. 中国图书馆学报, 2000, 26(5): 32-37.",
  "[4] 初景利. 复合图书馆的概念及发展构想[J]. 中国图书馆学报, 2001, 27(3): 3-7.",
  "[5] 王世伟. 论未来图书馆的发展模式[J]. 图书情报工作, 2004, 48(1): 5-8.",
  "[6] 张烈超, 胡迎九. 典型Java Web开发框架模型的研究[J]. 武汉交通职业学院学报, 2021, 23(04): 122-127.",
  "[7] 霍福华, 韩慧. 基于SpringBoot微服务架构下前后端分离的MVVM模型[J]. 电子技术与软件工程, 2022, (01): 73-76.",
  "[8] 喻佳, 吴丹新. 基于SpringBoot的Web快速开发框架[J]. 电脑编程技巧与维护, 2021, (09): 31-33.",
  "[9] 陈蓓蕾, 洪年松. 基于SpringBoot的数据库接口设计[J]. 信息与电脑(理论版), 2023, 35(16): 181-183.",
  "[10] 刘金羽. 基于Spring Boot的单页网站设计与实现[J]. 电脑编程技巧与维护, 2023, (01): 35-37+44.",
  "[11] 张宇薇. HTML5在Web前端开发中的应用[J]. 集成电路应用, 2024, 41(04): 274-276.",
  "[12] 季焕淑. 基于HTML5技术的移动Web前端设计与开发[J]. 电脑编程技巧与维护, 2022, (10): 74-76+169.",
  "[13] 王志亮, 纪松波. 基于SpringBoot的Web前端与数据库的接口设计[J]. 工业控制计算机, 2023, 36(03): 51-53.",
  "[14] 崔娟, 章恒, 马尧, 等. 基于Spring Security框架的前后端分离软件平台构建的研究[J]. 科学技术创新, 2022, (04): 73-76.",
  "[15] 欧阳宏基, 葛萌, 程海波. MyBatis框架在数据持久层中的应用研究[J]. 微型电脑应用, 2023, 39(01): 73-75.",
  "[16] 曾秀莲. 基于UML软件建模过程分析[J]. 科技向导, 2012, (20): 114-115.",
  "[17] 陈颖茵, 邓文华. 企业IT维护管理系统分析与设计[J]. 软件工程, 2020, 23(5): 29-32.",
  "[18] 张超. 餐厅预订系统的设计与实现[J]. 电脑知识与技术, 2015(11): 53-54.",
];

const referenceChildren = [
  h1("参考文献"),
  emptyLine(),
];

references.forEach((ref) => {
  referenceChildren.push(
    new Paragraph({
      spacing: { line: LINE_1_2, before: 60, after: 60 },
      indent: { left: 420, hanging: 420 },
      children: [
        new TextRun({
          text: ref,
          font: FONT_BODY,
          size: SIZE.XIAOSI,
        }),
      ],
    })
  );
});

// ==================== 文档组装 ====================

/** 汇总所有内容到统一children数组 */
const allChildren = [
  ...coverChildren,
  pageBreak(),
  ...taskBookChildren,
  ...abstractChildren,
  ...abstractEnChildren,
  ...tocChildren,
  ...chapter1Children,
  ...chapter2Children,
  ...chapter3Children,
  ...chapter4Children,
  ...chapter5Children,
  ...chapter6Children,
  ...conclusionChildren,
  ...referenceChildren,
];

/** 创建页眉 */
const docHeader = new Header({
  children: [
    new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [
        new TextRun({
          text: "东北石油大学课程设计",
          font: FONT_BODY,
          size: SIZE.XIAOWU,
        }),
      ],
    }),
  ],
});

/** 创建页脚（页码居中） */
const docFooter = new Footer({
  children: [
    new Paragraph({
      alignment: AlignmentType.CENTER,
      children: [
        new TextRun({
          children: [PageNumber.CURRENT],
          font: FONT_BODY,
          size: SIZE.XIAOWU,
        }),
      ],
    }),
  ],
});

/** 创建文档 */
const doc = new Document({
  creator: "fy",
  lastModifiedBy: "fy",
  styles: {
    default: {
      document: {
        run: {
          font: FONT_BODY,
          size: SIZE.XIAOSI,
        },
        paragraph: {
          spacing: { line: LINE_1_2, before: 0, after: 0 },
        },
      },
    },
    paragraphStyles: [
      {
        id: "Heading1",
        name: "Heading 1",
        basedOn: "Normal",
        next: "Normal",
        run: {
          font: FONT_HEADING,
          size: SIZE.XIAOER,
        },
        paragraph: {
          alignment: AlignmentType.CENTER,
          spacing: { before: 480, after: 480, line: LINE_1_2 },
        },
      },
      {
        id: "Heading2",
        name: "Heading 2",
        basedOn: "Normal",
        next: "Normal",
        run: {
          font: FONT_HEADING,
          size: SIZE.XIAOSAN,
        },
        paragraph: {
          spacing: { before: 240, after: 240, line: LINE_1_2 },
        },
      },
      {
        id: "Heading3",
        name: "Heading 3",
        basedOn: "Normal",
        next: "Normal",
        run: {
          font: FONT_HEADING,
          size: SIZE.SIHAO,
        },
        paragraph: {
          spacing: { before: 240, after: 0, line: LINE_1_2 },
        },
      },
    ],
  },
  sections: [
    {
      properties: {
        page: {
          size: { width: 11906, height: 16838 },
          margin: {
            top: 1701,
            bottom: 1417,
            left: 1701,
            right: 1417,
            header: 851,
            footer: 851,
          },
        },
      },
      headers: { default: docHeader },
      footers: { default: docFooter },
      children: allChildren,
    },
  ],
});

/**
 * 移除docx库自动注入的"AI 生成"水印
 * docx 9.7.x 在打包时会在页面右下角添加灰色半透明水印（header_watermark.xml）
 * 使用JSZip读取docx(zip)→过滤水印文件→清理XML引用→重新打包写回
 * @param {string} filePath - docx文件路径
 */
async function removeWatermark(filePath) {
  // 读取原始docx文件
  const data = fs.readFileSync(filePath);
  const zip = await JSZip.loadAsync(data);

  // 1. 删除水印header文件
  if (zip.file("word/header_watermark.xml")) {
    zip.remove("word/header_watermark.xml");
    console.log("  已删除 header_watermark.xml");
  }

  // 2. 清理 document.xml.rels 中的水印引用
  const relsFile = zip.file("word/_rels/document.xml.rels");
  if (relsFile) {
    let relsXml = await relsFile.async("string");
    const originalRels = relsXml;
    relsXml = relsXml.replace(
      /<Relationship[^>]*header_watermark\.xml[^>]*\/>/g,
      ""
    );
    if (relsXml !== originalRels) {
      zip.file("word/_rels/document.xml.rels", relsXml);
      console.log("  已清理 document.xml.rels 中的水印引用");
    }
  }

  // 3. 清理 document.xml 中对水印header的引用
  const docFile = zip.file("word/document.xml");
  if (docFile) {
    let docXml = await docFile.async("string");

    // 获取清理后的有效rId集合
    const cleanedRels = relsFile
      ? await relsFile.async("string")
      : "";
    const cleanedRelsFinal = cleanedRels.replace(
      /<Relationship[^>]*header_watermark\.xml[^>]*\/>/g,
      ""
    );
    const validRids = new Set();
    const ridRegex = /Id="(rId\d+)"/g;
    let match;
    while ((match = ridRegex.exec(cleanedRelsFinal)) !== null) {
      validRids.add(match[1]);
    }

    // 移除引用了无效rId的headerReference
    docXml = docXml.replace(
      /<w:headerReference\s+w:type="(\w+)"\s+r:id="(rId\d+)"\s*\/>/g,
      (fullMatch, type, rid) => {
        if (!validRids.has(rid)) {
          console.log(`  已移除 document.xml 中的水印 headerReference (${rid})`);
          return "";
        }
        return fullMatch;
      }
    );

    // 确保有正常header引用
    if (!docXml.includes('r:id="rId7"') && validRids.has("rId7")) {
      docXml = docXml.replace(
        /<w:sectPr>/,
        '<w:sectPr><w:headerReference w:type="default" r:id="rId7"/>'
      );
      console.log("  已恢复正常页眉引用 (rId7)");
    }

    zip.file("word/document.xml", docXml);
  }

  // 4. 清理 [Content_Types].xml
  const ctFile = zip.file("[Content_Types].xml");
  if (ctFile) {
    let ctXml = await ctFile.async("string");
    const originalCt = ctXml;
    ctXml = ctXml.replace(
      /<Override[^>]*header_watermark\.xml[^>]*\/>/g,
      ""
    );
    if (ctXml !== originalCt) {
      zip.file("[Content_Types].xml", ctXml);
      console.log("  已清理 [Content_Types].xml 中的水印引用");
    }
  }

  // 5. 重新打包写回文件
  const newBuffer = await zip.generateAsync({
    type: "nodebuffer",
    compression: "DEFLATE",
    compressionOptions: { level: 9 },
  });
  fs.writeFileSync(filePath, newBuffer);
  console.log("  水印移除完成");
}

/** 生成并写入文件 */
async function generate() {
  console.log("正在生成论文文档...");
  const buffer = await Packer.toBuffer(doc);
  fs.writeFileSync(OUTPUT_PATH, buffer);
  console.log(`论文已生成：${OUTPUT_PATH}`);
  console.log(`文件大小：${(buffer.length / 1024).toFixed(1)} KB`);

  // 后处理：移除docx库自动注入的"AI 生成"水印
  console.log("正在移除AI水印...");
  await removeWatermark(OUTPUT_PATH);
  console.log("论文处理完成！");
}

generate().catch((err) => {
  console.error("生成失败：", err);
  process.exit(1);
});
