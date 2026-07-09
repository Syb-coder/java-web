/**
 * 课程设计论文生成脚本
 * 使用 docx 库生成《大学生闲置二手物品交易网站设计与实现》论文
 *
 * 运行方式：node generate_thesis.js
 * 输出路径：../大学生闲置二手物品交易网站设计与实现.docx
 */

const docx = require("c:/000/code/java-web/java-8/node_modules/docx");
const fs = require("fs");
const path = require("path");

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
const OUTPUT_PATH = path.join(__dirname, "..", "大学生闲置二手物品交易网站设计与实现.docx");

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
    "fig_4_4_order_flow.png": { w: 1500, h: 750 },
    "fig_4_5_trade_process.png": { w: 1500, h: 1500 },
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
  centerText("大学生闲置二手物品", SIZE.XIAOYI, true, FONT_HEADING, 0),
  centerText("交易网站设计与实现", SIZE.XIAOYI, true, FONT_HEADING, 0),
  new Paragraph({ spacing: { before: 1600 }, children: [new TextRun({ text: "" })] }),
];

// 封面信息表（使用段落模拟）
const coverInfo = [
  ["学　　生　姓　名", "王明哲"],
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
    { text: "大学生闲置二手物品交易网站设计与实现" },
  ]),
  pRich([{ text: "学　　生：", bold: true }, { text: "王明哲（240701240303）" }]),
  pRich([{ text: "专　　业：", bold: true }, { text: "网络空间安全" }]),
  pRich([{ text: "指导教师：", bold: true }, { text: "赵娅 副教授" }]),
  emptyLine(),
  h2("一、课程设计目的"),
  p("本课程设计旨在综合运用Java Web开发技术，完成一个具有实际应用价值的大学生闲置二手物品交易网站系统。通过本项目的开发，学生需要掌握Spring Boot框架的核心原理与使用方法，熟练运用JPA/Hibernate进行对象关系映射与数据库操作，理解B/S架构的设计思想，掌握前端HTML/CSS/JavaScript基础技术，并能够运用Maven进行项目构建与依赖管理。同时，通过完整的项目实践，培养学生的系统分析与设计能力、代码编写与调试能力、以及软件工程文档撰写能力。"),
  h2("二、课程设计内容与要求"),
  p("1. 需求分析：分析大学生二手物品交易业务流程，明确系统功能性需求与非功能性需求，包括用户认证与管理、商品发布与审核、订单交易、消息互动、公告反馈和后台管理等核心功能模块。"),
  p("2. 系统设计：完成系统架构设计、功能模块设计、数据库设计（E-R图设计与数据表结构设计），确定技术选型方案，遵循分层解耦、单一职责、安全优先等设计原则。"),
  p("3. 功能实现：基于Spring Boot 4.1.0框架与Java 25语言，使用JPA/Hibernate ORM框架与H2文件数据库，实现学号实名认证注册登录、商品发布审核、订单状态流转、私信沟通、线下预约自提、1至5星交易评价、公告发布与意见反馈等全部功能。前端采用HTML/CSS/JavaScript技术，通过RESTful API与后端通信。"),
  p("4. 安全实现：采用BCrypt密码加密算法保障用户密码安全，使用Session机制进行用户认证，通过LoginInterceptor拦截器实现URL级别的权限控制，防止未授权访问与越权操作。"),
  p("5. 系统测试：编写测试用例，对系统的功能正确性、安全性、兼容性进行全面测试，确保系统稳定可靠运行。"),
  p("6. 文档撰写：按照学校规定的格式要求，撰写完整的课程设计论文，包括摘要、目录、正文各章节、结论与参考文献等。"),
  h2("三、时间安排"),
  p("第1周：需求分析与系统设计，完成需求分析文档与系统设计文档。"),
  p("第2周：数据库设计与后端核心功能开发，完成13个实体类、13个Repository接口、12个Service类与11个Controller类代码编写。"),
  p("第3周：前端页面开发与系统集成，完成HTML/CSS/JavaScript前端页面，实现前后端联调。"),
  p("第4周：系统测试、优化与论文撰写，完成测试报告与课程设计论文。"),
  h2("四、预期成果"),
  p("1. 可运行的大学生闲置二手物品交易网站系统源代码（含Maven构建脚本）。"),
  p("2. 课程设计论文一份（不少于45页），包含完整的系统分析与设计文档。"),
  p("3. 系统测试报告，覆盖功能测试、安全测试与兼容性测试。"),
  pageBreak(),
];

// ---------- 中文摘要 ----------
const abstractChildren = [
  centerText("摘　　要", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  p("随着高校扩招和大学生消费水平的提升，校园内闲置物品逐年累积，资源浪费现象日益严重。传统的校园二手交易方式主要依赖线下跳蚤市场和微信QQ群等社交渠道，存在信息不对称、交易效率低、信任机制缺失等问题。本文设计并实现了一个基于Spring Boot框架的大学生闲置二手物品交易网站系统，旨在为校园师生提供便捷、安全、可信赖的二手物品交易平台。"),
  p("系统采用B/S架构，后端基于Spring Boot 4.1.0框架与Java 25语言开发，使用JPA/Hibernate进行对象关系映射，采用H2文件数据库实现数据持久化存储，通过Maven进行项目构建与依赖管理。前端使用HTML/CSS/JavaScript技术栈，通过RESTful API与后端进行数据交互。系统共设计13个JPA实体、11个控制器、12个服务类、13个数据访问接口和26个数据传输对象，实现了学号实名认证、商品发布与审核、订单状态机流转、私信沟通、线下预约自提、1至5星交易评价、公告发布与意见反馈等核心功能模块。在安全方面，系统采用BCrypt密码加密算法保障用户密码安全，使用Session机制进行用户认证，并通过LoginInterceptor拦截器实现URL级别的权限控制，有效防止未授权访问与越权操作。"),
  p("系统采用分层架构设计，将表现层、控制层、业务层和数据访问层进行解耦，提高了代码的可维护性和可扩展性。订单管理模块实现了待付款、已付款、已完成和已取消四种状态的单向流转机制，保障了交易流程的规范性。商品审核模块通过待审核、已通过和已驳回三种状态管理商品上架流程，有效过滤违规内容。经过功能测试、安全测试和兼容性测试，系统各功能模块运行稳定，达到了预期设计目标，具有一定的实用价值和推广意义。"),
  emptyLine(),
  pRich([
    { text: "关键词：", bold: true },
    { text: "Spring Boot；二手交易；JPA；H2数据库；B/S架构" },
  ]),
  pageBreak(),
];

// ---------- 英文Abstract ----------
const abstractEnChildren = [
  centerText("Abstract", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  p("With the expansion of college enrollment and the improvement of college students' consumption levels, idle items accumulate year by year on campus, and the waste of resources has become increasingly serious. Traditional campus second-hand trading methods mainly rely on offline flea markets and social channels such as WeChat and QQ groups, which suffer from information asymmetry, low transaction efficiency, and lack of trust mechanisms. This paper designs and implements a campus second-hand trading website system based on the Spring Boot framework, aiming to provide a convenient, safe, and trustworthy second-hand trading platform for campus students and teachers."),
  p("The system adopts a B/S architecture, with the backend developed based on the Spring Boot 4.1.0 framework and Java 25, using JPA/Hibernate for object-relational mapping, H2 file database for data persistence, and Maven for project building and dependency management. The frontend utilizes HTML/CSS/JavaScript technology stack and communicates with the backend through RESTful APIs. The system designs 13 JPA entities, 11 controllers, 12 service classes, 13 repository interfaces, and 26 data transfer objects, implementing core functional modules including student ID real-name authentication, product publishing and auditing, order state machine transition, private messaging, offline pickup appointments, 1-to-5 star transaction reviews, announcement publishing, and feedback submission. In terms of security, the system employs the BCrypt password encryption algorithm to ensure password security, uses Session mechanism for user authentication, and implements URL-level access control through the LoginInterceptor to effectively prevent unauthorized access and privilege escalation."),
  p("The system adopts a layered architecture design, decoupling the presentation layer, control layer, business layer, and data access layer to improve code maintainability and scalability. The order management module implements a one-way state transition mechanism with four states: pending, paid, completed, and cancelled, ensuring the standardization of the trading process. The product audit module manages the product listing process through three states: pending, approved, and rejected, effectively filtering out non-compliant content. Through functional testing, security testing, and compatibility testing, all functional modules of the system operate stably and achieve the expected design objectives, demonstrating practical value and promotional significance."),
  emptyLine(),
  pRich([
    { text: "Key words: ", bold: true },
    { text: "Spring Boot; Second-hand Trading; JPA; H2 Database; B/S Architecture" },
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
  tocEntry("第3章 需求分析", "16"),
  tocEntry("3.1 功能性需求分析", "16", false),
  tocEntry("3.2 非功能性需求分析", "20", false),
  tocEntry("第4章 系统设计", "23"),
  tocEntry("4.1 设计原则", "23", false),
  tocEntry("4.2 系统架构设计", "23", false),
  tocEntry("4.3 功能模块设计", "25", false),
  tocEntry("4.4 数据库设计", "28", false),
  tocEntry("第5章 功能实现", "34"),
  tocEntry("5.1 用户认证模块实现", "34", false),
  tocEntry("5.2 商品管理模块实现", "36", false),
  tocEntry("5.3 订单交易模块实现", "38", false),
  tocEntry("5.4 消息互动模块实现", "40", false),
  tocEntry("5.5 后台管理模块实现", "42", false),
  tocEntry("第6章 系统测试", "44"),
  tocEntry("6.1 测试方案", "44", false),
  tocEntry("6.2 测试结果", "46", false),
  tocEntry("结论", "49"),
  tocEntry("参考文献", "51"),
  pageBreak(),
];

// ---------- 第1章 概述 ----------
const chapter1Children = [
  h1("第1章 概述"),
  h2("1.1 系统开发背景及意义"),
  p("随着我国高等教育的普及和高校扩招政策的持续推进，在校大学生人数已突破四千万规模。大学生群体作为年轻一代的消费主力，在日常学习和生活中购买了大量的教材参考书、电子产品、生活用品和文体器材等物资。然而，随着学期的更替、年级的升高以及毕业离校等节点的到来，大量物品因课程结束、个人需求变化或搬迁不便等原因沦为闲置状态。据相关调研数据显示，每名大学生在校期间平均产生价值约两千元的闲置物品，全国高校每年产生的闲置物品总价值高达数百亿元，资源浪费现象十分惊人[1]。"),
  p("传统的校园二手交易方式主要依赖以下几种渠道：一是线下跳蚤市场，通常在毕业季或特定节假日由学校社团组织，具有时间地点固定、覆盖面有限的缺陷；二是微信群、QQ群等社交媒体渠道，信息以聊天消息形式呈现，缺乏结构化的商品展示和检索能力，历史消息难以回溯；三是校园公告栏张贴海报，信息传播效率低且维护成本高。这些传统方式普遍存在信息不对称、交易匹配效率低、缺乏信用保障和售后维权机制等问题，导致大量闲置物品最终被丢弃或废弃，造成了严重的资源浪费和环境负担[2]。"),
  p("从资源利用和环境保护的角度来看，闲置物品的循环利用具有显著的经济价值和生态意义。二手物品的流转不仅能够延长物品的使用寿命，减少新品生产所消耗的能源和原材料，还能降低废弃物处理带来的环境压力。在\u201C双碳\u201D目标和绿色低碳发展理念的指引下，推动校园闲置物品的高效流转，既是践行可持续发展战略的具体举措，也是培养大学生环保意识和节约习惯的有效途径。一个功能完善的校园二手交易平台，能够让闲置物品在校园内部实现就近流转，减少物流环节的碳排放，符合绿色消费的时代趋势。"),
  p("从技术发展趋势来看，近年来Java Web开发技术体系日趋成熟，Spring Boot框架的推出极大降低了企业级应用的开发门槛，JPA规范的普及使得数据持久层的开发更加规范化，前后端分离架构的推广提升了系统的可维护性和可扩展性。这些技术进步为开发轻量级、高效率的校园二手交易系统提供了坚实的技术基础。与此同时，H2等嵌入式数据库的成熟使得小型项目无需部署独立的数据库服务即可实现数据持久化，进一步降低了系统的部署和运维成本。本系统正是基于上述技术体系构建，旨在探索一种适合校园场景的轻量级二手交易解决方案。"),
  p("开发一个专属的校园二手交易网站具有重要的现实意义。第一，线上平台能够打破时间和空间的限制，学生可以随时随地发布闲置物品、浏览商品信息和进行交易沟通，极大提升了交易的便捷性和效率。第二，学号实名认证机制能够确保交易双方的真实身份，建立基于校园身份的信任体系，有效减少虚假信息和欺诈行为。第三，商品审核机制能够过滤违规和不良内容，维护平台的健康生态。第四，订单状态机管理能够规范交易流程，保障买卖双方的权益。第五，线下预约自提功能结合校园地理优势，实现了O2O（Online to Offline）模式的便捷交易体验，免去了物流配送的成本和等待时间。"),
  p("当前市场上虽然已存在闲鱼、转转等综合性二手交易平台，但这些平台面向全社会用户，存在以下局限性：一是用户身份无法验证，难以建立校园级别的信任关系；二是交易需要通过快递物流完成，增加了交易成本和时间；三是平台信息庞杂，校园相关的商品容易被淹没在海量信息中；四是缺乏针对校园场景的特色功能，如课程教材分类、宿舍楼栋自提等。因此，开发一个专注于校园场景、具备学号认证和线下自提等特色功能的二手交易平台，能够更好地满足大学生群体的实际需求，弥补综合性平台在校园细分市场的不足。"),
  p("从教学实践的角度来看，本系统的开发也是对Spring Boot框架、JPA持久化技术、B/S架构设计、RESTful API设计、安全认证机制等Java Web开发核心技术的综合实践。通过完整经历需求分析、系统设计、编码实现、测试验证的全流程，能够有效提升学生的软件工程素养和工程实践能力。系统涉及的13个JPA实体、11个控制器、12个服务类和26个数据传输对象，覆盖了实体关系映射、业务逻辑封装、接口分层设计等典型开发场景，具有较强的教学示范价值和技术参考意义。"),
  p("综上所述，大学生闲置二手物品交易网站的设计与实现，既回应了校园闲置物品流转的现实需求，又契合了绿色低碳发展的时代主题，同时也是对Java Web开发技术的系统性实践。本系统通过学号实名认证、商品审核、订单状态机、线下预约自提和交易评价等机制，构建了一个安全、便捷、可信赖的校园二手交易生态，对于促进校园资源循环利用、培养大学生节约环保意识、提升工程实践能力均具有积极意义。"),
  h2("1.2 国内外研究现状"),
  h3("1.2.1 国内研究现状"),
  p("国内校园二手交易领域的研究和实践起步较早，经历了从BBS论坛到独立交易平台的发展历程。早期的校园二手交易主要依托高校BBS论坛的二手版块进行，如清华大学的\u201C水木社区\u201D、北京大学的\u201C未名空间\u201D等，这些论坛虽然具备基本的信息发布功能，但缺乏商品结构化展示、交易流程管理和信用评价体系，交易效率和用户体验均较为有限。随着Web 2.0技术和移动互联网的发展，越来越多的高校开始建设独立的二手交易平台，研究者们也从不同角度对校园二手交易系统进行了深入探索[3]。"),
  p("在技术架构方面，国内学者普遍采用Java Web技术栈进行校园二手交易系统的开发。匡卫东等人设计了基于传统MVC模式的校园二手交易系统，使用Servlet和JSP技术实现了商品发布、搜索和交易等基本功能，为校园二手交易系统的早期建设提供了参考。万成等人基于JavaWeb架构设计了校园二手交易平台，引入了前后端分离的设计思想，通过Ajax技术实现了页面的局部刷新，提升了用户体验。吴丽等人基于Spring Boot框架设计了二手交易平台，利用Spring Boot的自动配置特性简化了项目搭建过程，提高了开发效率。这些研究为本系统的技术选型和架构设计提供了重要参考。"),
  p("在市场分析方面，张宝才等人对二手交易市场的现状与发展趋势进行了系统分析，指出随着循环经济理念的深入和年轻一代消费观念的转变，二手交易市场正迎来快速发展期，其中校园市场作为高密度、高频率的交易场景，具有巨大的发展潜力[4]。研究还指出，当前二手交易平台面临的主要挑战包括信任机制缺失、交易纠纷频发和售后服务不足等问题，建立完善的信用评价体系和交易保障机制是平台可持续发展的关键。这些研究结论为本系统的功能设计提供了重要指引，特别是学号实名认证和交易评价机制的设计，正是针对信任机制缺失问题的有效应对。"),
  p("在功能创新方面，国内研究者们也在不断探索校园二手交易系统的新特性。部分研究引入了推荐算法，根据用户的浏览和购买历史推荐感兴趣的商品，提高交易匹配效率。部分研究关注线上线下融合，设计了基于地理位置的附近商品推荐和线下自提点导航功能。还有研究引入了社交元素，允许用户关注其他用户、分享商品到社交平台等。然而，这些创新功能往往增加了系统的复杂度，对于课程设计项目而言，应优先保障核心功能的完整性和稳定性，在此基础上再考虑扩展功能的引入。本系统选择以学号认证、商品审核、订单状态机和线下预约自提等核心功能为重点，确保系统的实用性和可靠性。"),
  h3("1.2.2 国外研究现状"),
  p("国外二手交易市场的发展相对成熟，已形成了若干具有全球影响力的二手交易平台。Craigslist作为美国最早的分类信息网站之一，提供了包括二手物品在内的多种分类信息服务，其简洁的界面和地区化的信息组织方式对后来的二手交易平台产生了深远影响。eBay作为全球最大的在线拍卖和购物网站之一，其二手物品交易板块为用户提供了拍卖和一口价两种交易模式，建立了较为完善的支付保障和纠纷处理机制。日本的Mercari则以其移动端优先的设计和简洁的用户体验，成为移动二手交易领域的标杆产品[5]。"),
  p("在校园场景方面，国外的大学普遍重视二手物品的循环利用，许多高校设有专门的二手交易网站或应用程序。例如，斯坦福大学的\u201CStanford Marketplace\u201D、MIT的\u201CMIT Marketplace\u201D等校园二手交易平台，为师生提供了一个安全、便捷的校内交易渠道。这些平台通常要求使用者使用校园邮箱进行注册，实现了基于校园邮箱的身份认证，与本系统的学号实名认证机制在思路上具有相似性。此外，国外高校还注重二手物品交易的环保教育意义，将平台运营与可持续发展教育相结合，培养学生的环保意识。"),
  p("在技术架构方面，国外主流二手交易平台普遍采用微服务架构和云原生技术，具备高可用性和弹性伸缩能力。然而，这些技术方案对于中小型项目和教学实践而言过于复杂，需要投入大量的开发和运维资源。因此，研究基于轻量级框架的二手交易系统，对于降低开发门槛、促进技术普及具有重要价值。本系统采用Spring Boot框架进行开发，既利用了Spring生态的成熟度和丰富功能，又通过自动配置和内嵌容器等特性保持了轻量级优势，适合校园项目的开发部署。"),
  p("从信用机制来看，国外二手交易平台普遍建立了较为完善的信用评价体系。eBay的\u201C反馈评分\u201D机制允许交易双方互相评价，累计评分直接影响用户的信誉等级和搜索排名。Mercari引入了评价星级和文字评价相结合的方式，为后续交易者提供参考。这些信用评价机制的设计思路对本系统的1至5星评价功能具有启发意义。本系统在订单完成后允许买家对卖家进行1至5星评分和文字评价，评价结果与用户信息关联展示，为校园交易建立了基于历史行为的信任参考。综合国内外研究现状，本系统在借鉴成熟平台经验的基础上，结合校园场景的特殊性，设计了学号实名认证、商品审核、线下预约自提等特色功能，力求在功能完善度和系统复杂度之间取得平衡。"),
  h2("1.3 主要研究内容"),
  p("本课题以大学生闲置二手物品交易为背景，设计并实现一个基于Spring Boot框架的校园二手交易网站系统。主要研究内容包括以下六个方面："),
  p("（1）系统需求分析：深入分析校园二手物品交易的业务流程和使用场景，明确系统的功能性需求与非功能性需求，包括用户认证与管理、商品管理、订单交易、消息互动、公告反馈和后台管理等核心功能模块，为系统设计与实现奠定基础。"),
  p("（2）系统架构设计：采用B/S架构与分层设计思想，设计表现层、控制层、业务层、数据访问层和数据层五层架构体系，确定技术选型方案，保障系统的可维护性与可扩展性。"),
  p("（3）数据库设计：根据业务需求设计E-R模型，建立用户表、管理员表、商品表、商品分类表、订单表、消息表、评论表、评价表、公告表、预约表、收藏表、点赞表和反馈表共13张数据表，定义实体间的关联关系，设计订单状态流转机制和商品审核状态流转机制。"),
  p("（4）核心功能实现：基于Spring Boot 4.1.0框架与Java 25语言，实现学号实名认证注册登录、商品发布与审核、商品搜索与筛选、订单创建与状态流转、私信沟通、线下预约自提、1至5星交易评价、公告发布与意见反馈等核心功能模块，前端采用HTML/CSS/JavaScript技术并通过RESTful API与后端通信。"),
  p("（5）安全机制实现：采用BCrypt密码加密算法保障密码安全，使用Session机制进行用户认证，通过LoginInterceptor拦截器实现URL级别的权限控制，在订单和消息等操作中实现数据归属校验以防止越权操作，商品审核机制过滤违规内容。"),
  p("（6）系统测试与优化：设计测试用例，对系统的功能正确性、安全性和兼容性进行全面测试，根据测试结果进行问题修复与系统优化，确保系统稳定可靠运行。"),
  pageBreak(),
];

// ---------- 第2章 相关技术及工具介绍 ----------
const chapter2Children = [
  h1("第2章 相关技术及工具介绍"),
  p("本章对系统开发过程中所采用的关键技术与工具进行介绍，包括Spring Boot框架、JPA与Hibernate、H2数据库、前端技术以及Maven构建工具等，为后续章节的系统设计与实现提供技术基础。"),
  h2("2.1 Spring Boot框架"),
  p("Spring Boot是由Pivotal团队开发的基于Spring框架的快速应用开发框架，旨在简化Spring应用的初始搭建与开发过程。Spring Boot秉承\u201C约定优于配置\u201D的设计理念，通过自动配置机制大幅减少了繁琐的XML配置工作，使开发者能够将更多精力聚焦于业务逻辑的实现。张烈超等人对典型Java Web开发框架模型进行了系统研究，指出Spring Boot框架通过自动配置和起步依赖机制显著降低了Spring应用的开发门槛，已成为当前Java Web开发的主流选择[6]。"),
  p("Spring Boot的核心特性包括以下几个方面。第一，自动配置（Auto Configuration）：Spring Boot根据项目中引入的依赖自动配置相关的Bean和组件，例如引入spring-boot-starter-data-jpa后，框架会自动配置DataSource、EntityManagerFactory等Bean，无需手动编写配置代码。第二，内嵌Web容器：Spring Boot内嵌了Tomcat、Jetty等Servlet容器，应用可以直接以Java Application的方式启动，无需部署到外部容器，极大简化了开发与部署流程。第三，Starter依赖管理：Spring Boot提供了一系列starter依赖包，每个starter聚合了特定功能所需的全部依赖，开发者只需引入一个starter即可获得完整的功能支持。第四，生产级监控：Spring Boot Actuator提供了健康检查、运行指标监控等开箱即用的运维功能。霍福华等人研究了基于Spring Boot微服务架构下前后端分离的MVVM模型，验证了Spring Boot在前后端分离架构中的适用性和高效性[7]。"),
  p("本系统采用Spring Boot 4.1.0版本，利用其自动配置特性快速搭建项目骨架，通过spring-boot-starter-webmvc提供RESTful API能力，通过spring-boot-starter-data-jpa简化持久层开发，通过spring-boot-starter-validation实现参数校验。Spring Boot的Starter依赖管理机制确保了各组件版本的兼容性，Maven插件支持一键打包生成可执行JAR文件，显著提升了开发效率。喻佳等人研究了基于Spring Boot的Web快速开发框架，指出Spring Boot的自动配置和起步依赖机制能够使开发效率提升百分之四十以上，尤其适合中小型Web应用的快速开发[8]。"),
  p("在配置管理方面，Spring Boot采用application.properties文件作为统一配置入口，支持以键值对形式配置数据库连接、服务端口、日志级别等参数。本系统在application.properties中配置了H2数据库连接字符串（jdbc:h2:file:./data/campusdb;DB_CLOSE_DELAY=-1;MODE=MySQL）、JPA的ddl-auto策略（update，自动更新表结构）、H2控制台启用（spring.h2.console.enabled=true）和服务端口（server.port=8083）等关键参数。Spring Boot 4.1.0对Java 25提供了良好的兼容支持，能够利用最新Java版本的性能优化和语言特性。"),
  h2("2.2 JPA与Hibernate"),
  p("JPA（Java Persistence API）是Java平台标准的对象关系映射（ORM）规范，定义了一套将Java对象映射到关系数据库表的API和元数据。JPA的核心思想是通过注解或XML描述对象与数据库表之间的映射关系，使开发者能够以面向对象的方式操作数据库，而无需编写大量的JDBC模板代码。陈蓓蕾等人研究了基于Spring Boot的数据库接口设计，指出JPA规范通过注解驱动的映射方式和方法名派生查询机制，显著简化了数据持久层的开发工作，提升了代码的可读性和可维护性[9]。"),
  p("Hibernate是JPA规范最流行的实现框架之一，提供了完整的ORM解决方案。Hibernate支持丰富的映射注解，包括@Entity（标识实体类）、@Table（指定表名）、@Column（映射列属性）、@Id（主键标识）、@GeneratedValue（主键生成策略）、@Enumerated（枚举映射）等。通过这些注解，开发者可以精确地描述实体类与数据库表之间的映射关系，包括字段类型、长度约束、唯一约束、可空性等。Hibernate还提供了JPQL（Java Persistence Query Language）查询语言，支持面向对象的查询语法，满足复杂业务场景的查询需求。刘金羽基于Spring Boot实现了单页网站设计，验证了JPA/Hibernate在中小型Web应用数据持久化中的高效性和可靠性[10]。"),
  p("Spring Data JPA在Hibernate的基础上进一步简化了数据访问层的开发。开发者只需定义一个继承JpaRepository的接口，Spring Data JPA会根据方法名自动生成查询实现，例如findBySellerIdOrderByCreatedAtDesc方法会自动生成按卖家ID查询并按创建时间倒序排列的SQL语句。同时，Spring Data JPA还支持@Query注解自定义JPQL查询，以及基于方法名的派生查询，极大减少了数据访问层的样板代码。本系统使用Spring Data JPA定义了AdminUserRepository、UserRepository、ProductRepository、ProductCategoryRepository、OrderRepository、MessageRepository、CommentRepository、ReviewRepository、AnnouncementRepository、AppointmentRepository、FavoriteRepository、ProductLikeRepository和FeedbackRepository共13个Repository接口，实现了各实体的CRUD操作与自定义查询。"),
  p("在本系统的实体设计中，JPA注解发挥了关键作用。例如，Product实体的description字段使用@Column(length = 2000)注解将字段长度扩展至2000个字符，以容纳详细的商品描述信息；images字段使用@Column(length = 1000)注解存储多张图片URL（以分号分隔）。Order实体的status字段使用@Enumerated(EnumType.STRING)注解将订单状态枚举以字符串形式持久化，便于运维人员直接查看数据库排查问题。User实体的studentId字段使用@Column(nullable = false, unique = true, length = 20)注解确保学号的唯一性和非空约束，这是学号实名认证机制在数据层面的基础保障。Review实体使用@Table的uniqueConstraints属性声明orderId字段的唯一约束，从数据库层面保证一个订单只能被评价一次。Hibernate的ddl-auto=update策略使得应用启动时自动根据实体类定义创建或更新数据库表结构，开发期间无需手动执行DDL语句。"),
  h2("2.3 H2数据库"),
  p("H2数据库是一个用纯Java编写的开源关系型数据库管理系统，具有体积小、速度快、零配置等特点。H2支持多种运行模式，包括嵌入式模式（Embedded Mode）、服务器模式（Server Mode）和混合模式。在嵌入式模式下，H2数据库与应用程序运行在同一JVM中，无需单独安装和配置数据库服务，非常适合开发和测试环境使用。"),
  p("H2数据库支持标准SQL语法，兼容ANSI SQL-92标准，提供了对视图、触发器、存储过程、外键约束等关系型数据库核心特性的完整支持。H2还内置了Web Console管理界面，开发者可通过浏览器访问H2 Console查看和操作数据库表结构与数据，极大方便了开发调试。王志亮等人研究了基于Spring Boot的Web前端与数据库的接口设计，验证了H2数据库在Spring Boot应用中的良好集成性和便捷的调试能力，指出H2的Web控制台功能为开发阶段的数据验证提供了直观的工具支持[13]。"),
  p("本系统选择H2数据库的文件模式（jdbc:h2:file:./data/campusdb;DB_CLOSE_DELAY=-1;MODE=MySQL）进行数据持久化。文件模式下，H2将数据存储在本地文件系统的campusdb.mv.db文件中，应用重启后数据不会丢失，实现了真正的数据持久化。相较于内存模式（jdbc:h2:mem:），文件模式更适合需要数据累积的业务场景。DB_CLOSE_DELAY=-1参数确保JVM退出时不立即关闭数据库连接，避免开发期频繁启停导致锁文件残留。MODE=MySQL参数启用MySQL兼容模式，使H2的SQL语法更贴近生产环境，便于未来迁移至MySQL等生产级数据库。此外，系统启用了H2 Web控制台（spring.h2.console.enabled=true），开发者可通过浏览器访问/h2-console路径，直观地查看和操作数据库中的表结构与数据，为开发调试提供了便利。H2数据库的轻量级特性使得本系统无需额外安装数据库服务，降低了部署门槛，同时其ACID事务支持保障了数据操作的一致性与可靠性。"),
  h2("2.4 HTML/CSS/JavaScript前端技术"),
  p("HTML（HyperText Markup Language）是构建Web页面的标准标记语言，通过语义化标签描述网页的结构与内容。本系统前端采用HTML5标准，使用header、nav、section、article等语义化标签构建页面结构，提升了页面的可读性和可维护性。张宇薇研究了HTML5在Web前端开发中的应用，指出HTML5新增的语义化标签、表单控件和本地存储等特性，能够显著提升Web应用的结构化程度和用户体验，是现代Web前端开发的基础技术标准[11]。"),
  p("CSS（Cascading Style Sheets）负责网页的样式与布局控制。本系统使用CSS3进行样式设计，采用Flexbox弹性布局实现响应式卡片排列，通过CSS变量统一管理主题色彩，利用过渡动画（transition）增强用户交互体验。系统的商品展示、公告列表等页面采用卡片式布局，每张卡片包含图片、标题、价格等关键信息，视觉层次清晰。季焕淑研究了基于HTML5技术的移动Web前端设计与开发，验证了HTML5配合CSS3的Flexbox布局和媒体查询技术能够良好地适配不同屏幕尺寸的设备，为响应式设计提供了技术保障[12]。"),
  p("JavaScript是实现网页动态交互的核心脚本语言。本系统前端使用原生JavaScript（Vanilla JS）开发，通过fetch API与后端RESTful接口进行异步通信，利用DOM操作实现页面内容的动态渲染与更新。JavaScript的事件驱动机制用于处理用户点击、表单提交等交互行为，例如商品发布、订单创建、私信发送等操作均通过JavaScript向后端发送请求并更新页面内容。相较于Vue、React等前端框架，原生JavaScript方案无需构建工具和额外依赖，部署简单，适合本课程设计项目的规模与需求。"),
  p("在异步通信方面，本系统前端统一使用fetch API替代传统的XMLHttpRequest对象。fetch API基于Promise设计，支持async/await语法，使异步代码的可读性大幅提升。例如，用户登录功能通过fetch向/api/auth/login端点发送POST请求，await等待响应后根据HTTP状态码判断登录是否成功，成功则更新页面显示用户信息并跳转至主页，失败则显示错误提示。这种基于Promise的异步模式避免了回调地狱（Callback Hell）问题，使代码结构更加清晰。在DOM操作方面，系统使用document.querySelector和document.querySelectorAll选择器获取页面元素，通过innerHTML、textContent和classList等API动态更新页面内容和样式。商品卡片展示通过JavaScript动态生成HTML字符串并插入到容器元素中，实现了数据驱动的UI渲染。"),
  h2("2.5 Maven构建工具"),
  p("Maven是Apache软件基金会开发的项目管理与构建自动化工具，基于项目对象模型（POM，Project Object Model）理念。Maven通过pom.xml配置文件统一管理项目的依赖库、构建流程和项目元信息，实现了项目构建的标准化与自动化。"),
  p("Maven的核心概念包括POM文件、坐标系统、依赖管理和生命周期。POM文件是Maven项目的核心配置文件，定义了项目的基本信息、依赖列表、插件配置和构建规则。坐标系统通过groupId、artifactId和version三个元素唯一标识一个项目或依赖库，确保依赖的准确解析。Maven的依赖管理机制支持传递依赖解析和版本冲突调解，开发者只需声明直接依赖，Maven会自动解析并下载所有间接依赖。崔娟等人研究了基于Spring Security框架的前后端分离软件平台构建，指出Maven的标准化构建流程和依赖管理机制为前后端分离项目的依赖管理和自动化构建提供了可靠保障[14]。"),
  p("本系统使用Maven进行项目构建与依赖管理，pom.xml文件中声明了spring-boot-starter-webmvc、spring-boot-starter-data-jpa、spring-boot-starter-validation、spring-boot-starter-h2console、h2、spring-security-crypto等核心依赖。同时，项目集成了Maven Wrapper（mvnw），使开发者无需预装Maven即可使用项目内置的Maven版本进行构建，保证了构建环境的一致性。通过spring-boot-maven-plugin插件，项目支持一键打包生成可执行JAR文件，简化了部署流程。欧阳宏基等人研究了MyBatis框架在数据持久层中的应用，对比分析了MyBatis与JPA/Hibernate在ORM映射和查询灵活性方面的差异，为本系统选择JPA作为持久层方案提供了技术参考[15]。本系统选择JPA而非MyBatis，主要考虑到JPA的注解驱动映射方式更加简洁，Spring Data JPA的方法名派生查询能够进一步减少样板代码，更适合本课程设计项目的开发需求。"),
  pageBreak(),
];

// ---------- 第3章 需求分析 ----------
const chapter3Children = [
  h1("第3章 需求分析"),
  p("需求分析是软件开发生命周期中的关键阶段，其质量直接影响系统设计的合理性与最终交付质量。本章从功能性需求和非功能性需求两个维度对大学生闲置二手物品交易网站系统进行需求分析，为后续的系统设计与实现提供依据。"),
  h2("3.1 功能性需求分析"),
  p("功能性需求描述系统应当具备的具体功能和行为。根据校园二手物品交易业务流程的分析，本系统的功能性需求分为用户认证与管理、商品管理、订单交易、消息互动、公告反馈和后台管理六大模块。曾秀莲基于UML软件建模过程分析方法指出，功能性需求的获取应从参与者（Actor）视角出发，通过用例图描述系统与外部角色的交互行为，确保需求分析的完整性和准确性[16]。本系统从学生用户、管理员两类参与者出发进行用例分析。"),
  h3("3.1.1 用户认证与管理功能"),
  p("用户认证与管理功能是系统的基础安全入口，主要包括以下功能项："),
  p("（1）学号实名注册：学生使用学号、用户名和密码进行注册，学号作为实名认证的唯一标识，系统通过学号唯一约束确保一个学号只能注册一个账号。注册时密码采用BCrypt加密算法进行加密存储，不保存明文。"),
  p("（2）用户登录：用户通过用户名和密码进行登录，系统使用BCryptPasswordEncoder比对密码，验证通过后创建HttpSession保存用户信息。"),
  p("（3）个人信息管理：用户可查看和修改自己的昵称、联系电话等个人信息，可修改登录密码（需验证旧密码）。"),
  p("（4）账号状态管理：管理员可对违规用户执行封禁操作，被封禁用户（status=BANNED）无法登录系统。"),
  h3("3.1.2 商品管理功能"),
  p("商品管理功能是系统的核心业务功能，主要包括以下功能项："),
  p("（1）商品发布：学生用户可发布闲置物品，填写商品标题、描述、期望价格、原价、图片URL、新旧程度和所属分类等信息。新发布的商品默认审核状态为PENDING（待审核），需经管理员审核通过后方可在前台展示。"),
  p("（2）商品审核：管理员对已发布的商品进行审核，审核通过则商品状态变为APPROVED（已通过），前台可展示；审核驳回则商品状态变为REJECTED（已驳回），并填写驳回原因。"),
  p("（3）商品浏览与搜索：用户可浏览所有已通过审核且未售出的商品，支持按分类筛选和关键词搜索。商品信息包括标题、描述、价格、原价、图片、新旧程度和发布者信息等。"),
  p("（4）商品点赞与收藏：用户可对感兴趣的商品进行点赞和收藏，方便后续查看。"),
  p("（5）商品评论：用户可对商品发表评论，其他用户可查看评论列表，促进商品信息的交流与互动。"),
  h3("3.1.3 订单交易功能"),
  p("订单交易功能管理买卖双方的交易流程，主要包括以下功能项："),
  p("（1）订单创建：买家对商品下单后系统生成订单，订单包含订单号、商品ID、买家ID、卖家ID、成交价格（下单时锁定商品价格）和买家留言等信息，订单初始状态为PENDING（待付款）。"),
  p("（2）订单状态流转：订单状态按PENDING（待付款）→PAID（已付款）→COMPLETED（已完成）的路径单向流转，买家也可在PENDING或PAID阶段取消订单（CANCELLED）。"),
  p("（3）订单查询：买家可查看自己的购买订单列表，卖家可查看自己收到的销售订单列表，管理员可查看全部订单。"),
  p("（4）交易评价：订单状态为COMPLETED后，买家可对卖家进行1至5星评分和文字评价，一个订单只能评价一次。"),
  p("（5）线下预约自提：买卖双方达成交易意向后，可通过预约功能约定线下自提的时间和地点，实现O2O模式的便捷交易。"),
  h3("3.1.4 消息互动功能"),
  p("消息互动功能支持买卖双方基于商品进行沟通，主要包括以下功能项："),
  p("（1）私信沟通：用户可向其他用户发送私信消息，消息可关联特定商品（商品咨询场景），也可为纯用户间私信。系统记录消息的已读状态，支持未读消息计数和红点提示。"),
  p("（2）消息列表：用户可查看自己收到的消息列表和发送的消息列表，按时间倒序排列。"),
  p("（3）消息标记已读：用户查看消息后，系统自动将消息标记为已读状态。"),
  p("（4）商品评论互动：用户可对商品发表评论，评论列表按时间排列，促进商品信息的交流。"),
  h3("3.1.5 公告反馈功能"),
  p("公告反馈功能为管理员与用户之间提供信息传达和意见收集渠道，主要包括以下功能项："),
  p("（1）公告发布：管理员可发布系统公告，公告内容在前台首页展示，向全体用户传达平台通知、活动信息等。"),
  p("（2）公告管理：管理员可编辑和删除已发布的公告。"),
  p("（3）意见反馈：用户可向管理员提交意见反馈，反馈内容可包含文字描述。"),
  p("（4）反馈回复：管理员可查看用户提交的反馈并进行回复，形成双向沟通闭环。"),
  h3("3.1.6 后台管理功能"),
  p("后台管理功能为管理员提供系统全局管理能力，主要包括以下功能项："),
  p("（1）管理员登录：管理员通过独立的登录入口进行认证，与前台用户分表存储（AdminUser表与User表），Session中使用不同的键进行区分。"),
  p("（2）仪表盘统计：管理员登录后可查看系统概览数据，包括用户总数、商品总数、订单总数等统计信息，快速了解平台运营状况。"),
  p("（3）用户管理：管理员可查看所有注册用户的基本信息，并可对违规用户执行封禁或解封操作。"),
  p("（4）商品审核管理：管理员可查看待审核商品列表，对商品进行审核通过或审核驳回操作。"),
  p("（5）订单管理：管理员可查看全部订单信息，了解平台交易状况。"),
  p("（6）纠纷处理：管理员可对交易纠纷进行介入处理，填写纠纷处理备注。"),
  tableCaption("表3-1 功能性需求汇总表"),
  createTable(
    ["功能模块", "功能项", "操作角色", "功能描述"],
    [
      ["用户认证", "学号注册", "访客", "学号+用户名+密码，BCrypt加密"],
      ["用户认证", "登录/登出", "访客/用户", "密码验证，Session认证管理"],
      ["用户认证", "个人信息", "前台用户", "查看/修改昵称、电话、密码"],
      ["商品管理", "发布商品", "前台用户", "填写商品信息，默认待审核"],
      ["商品管理", "商品审核", "管理员", "通过/驳回，驳回填原因"],
      ["商品管理", "浏览搜索", "前台用户", "按分类/关键词搜索商品"],
      ["商品管理", "点赞收藏", "前台用户", "点赞/收藏感兴趣的商品"],
      ["商品管理", "商品评论", "前台用户", "发表/查看商品评论"],
      ["订单交易", "下单", "前台用户", "生成订单，锁定成交价格"],
      ["订单交易", "状态流转", "前台用户", "PENDING→PAID→COMPLETED/CANCELLED"],
      ["订单交易", "交易评价", "前台用户", "1-5星评分+文字评价"],
      ["订单交易", "线下预约", "前台用户", "约定自提时间和地点"],
      ["消息互动", "私信沟通", "前台用户", "一对一私信，关联商品"],
      ["公告反馈", "公告发布", "管理员", "发布/编辑/删除系统公告"],
      ["公告反馈", "意见反馈", "前台用户", "提交反馈，管理员回复"],
      ["后台管理", "仪表盘", "管理员", "用户/商品/订单统计数据"],
      ["后台管理", "用户管理", "管理员", "查看用户/封禁/解封"],
      ["后台管理", "纠纷处理", "管理员", "介入交易纠纷，填写备注"],
    ],
    [1500, 1500, 1500, 4270]
  ),
  h2("3.2 非功能性需求分析"),
  p("非功能性需求是对系统运行质量与约束条件的描述，直接影响用户体验和系统可靠性。陈颖茵等人在企业IT维护管理系统的分析与设计中指出，非功能性需求涵盖安全性、性能、可用性和可维护性等多个维度，是衡量系统质量的重要指标，应在需求分析阶段予以明确定义[17]。本系统的非功能性需求主要包括以下几个方面："),
  h3("3.2.1 安全性需求"),
  p("（1）密码安全：用户密码不得以明文形式存储，必须采用BCrypt加密算法进行加密处理，BCrypt算法内置盐值机制，有效防止彩虹表攻击。"),
  p("（2）身份认证：系统采用基于HttpSession的认证机制，用户登录成功后在服务端创建Session并保存用户信息，后续请求通过Session ID进行身份识别。前台用户与管理员使用不同的Session键进行区分。"),
  p("（3）权限控制：通过LoginInterceptor拦截器实现URL级别的权限控制，GET请求（浏览查询）允许匿名访问，POST/PUT/DELETE请求必须经过身份认证；后台管理接口（/api/admin/**）仅允许管理员访问。"),
  p("（4）数据隔离：订单数据和消息数据实行用户级数据隔离，用户只能查看和操作与自己相关的数据，系统在Service层进行数据归属校验，防止越权访问。张超在餐厅预订系统的设计与实现中同样强调了数据隔离和权限控制在Web系统中的重要性，指出Service层的归属校验是防止水平越权漏洞的有效手段[18]。"),
  h3("3.2.2 性能需求"),
  p("（1）响应速度：系统页面加载时间应控制在3秒以内，API接口响应时间应控制在500毫秒以内。H2数据库文件模式提供本地高速读写能力，JPA的一级缓存机制减少不必要的数据库查询。"),
  p("（2）并发处理：系统应支持多用户同时访问，Spring Boot内嵌Tomcat容器支持多线程并发请求处理。"),
  h3("3.2.3 可用性需求"),
  p("（1）界面友好：前端界面采用响应式设计，适配不同屏幕尺寸；操作流程简洁直观，关键操作提供明确的成功或错误提示。"),
  p("（2）错误处理：后端对非法参数、资源不存在、权限不足等异常进行统一处理，返回规范的HTTP状态码和错误信息；前端对网络异常和业务错误进行友好提示。"),
  h3("3.2.4 可维护性需求"),
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
      ["安全性", "数据隔离", "Service层归属校验"],
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
  p("本章在需求分析的基础上，对大学生闲置二手物品交易网站系统进行详细设计，包括设计原则、系统架构设计、功能模块设计和数据库设计四个方面，为后续的功能实现提供蓝图。"),
  h2("4.1 设计原则"),
  p("系统设计遵循以下核心原则："),
  p("（1）分层解耦：系统采用经典的分层架构，将表现层、控制层、业务层和数据访问层进行分离，各层通过接口进行通信，降低层间耦合度，提高系统的可维护性和可测试性。"),
  p("（2）单一职责：每个类和模块只负责一项功能职责，Controller负责请求接收与响应封装，Service负责业务逻辑处理，Repository负责数据访问，Model负责数据建模，DTO负责数据传输，各司其职，避免\u201C上帝类\u201D问题。"),
  p("（3）安全优先：在系统设计的各个环节贯彻安全理念，密码加密存储、Session认证、拦截器鉴权、数据归属校验、商品审核等多层安全机制协同工作，保障系统和用户数据安全。"),
  p("（4）状态机驱动：订单状态和商品审核状态均采用枚举类型定义有限状态集合，通过状态机规则约束状态流转路径，确保业务流程的规范性和数据的一致性。"),
  p("（5）幂等设计：对于状态推进等关键操作，采用幂等设计理念，已完成或已取消的订单重复操作不会产生副作用，保证系统在异常重试场景下的数据一致性。"),
  h2("4.2 系统架构设计"),
  p("本系统采用B/S（Browser/Server）架构，用户通过浏览器访问系统，前端页面通过HTTP协议与后端Spring Boot应用通信，后端通过JPA与H2数据库交互。系统整体架构分为五层，自上向下依次为表现层、控制层、业务层、数据访问层和数据层。"),
  ...figure("fig_4_1_architecture.png", "图4-1 系统架构图", 520),
  p("如图4-1所示，系统五层架构的职责划分如下："),
  p("（1）表现层：由HTML/CSS/JavaScript组成，负责页面展示与用户交互，通过fetch API向后端发送RESTful请求，接收JSON格式响应并动态渲染页面内容。系统包含index.html（用户端）和admin.html（管理后台）两个主要页面。"),
  p("（2）控制层：由Spring MVC的Controller组件构成，负责接收HTTP请求、参数校验、调用业务层服务并封装响应数据。系统包含AuthController、UserController、ProductController、OrderController、MessageController、CommentController、AppointmentController、ReviewController、AnnouncementController、FeedbackController和AdminController共11个控制器。"),
  p("（3）业务层：由Service组件构成，负责核心业务逻辑处理，包括密码加密验证、商品审核、订单状态流转、交易评价、私信管理、数据归属校验等。系统包含AuthService、UserService、ProductService、CategoryService、OrderService、MessageService、CommentService、ReviewService、AppointmentService、AnnouncementService、FeedbackService和StatsService共12个服务类。"),
  p("（4）数据访问层：由Spring Data JPA的Repository接口构成，负责数据库CRUD操作与自定义查询。系统包含13个Repository接口，通过方法名派生查询和@Query注解实现数据访问。"),
  p("（5）数据层：由H2文件数据库构成，负责数据的持久化存储，数据文件为./data/campusdb.mv.db，包含用户表、管理员表、商品表、商品分类表、订单表、消息表、评论表、评价表、公告表、预约表、收藏表、点赞表和反馈表共13张数据表。"),
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
    ],
    [1500, 1800, 1200, 4270]
  ),
  h2("4.3 功能模块设计"),
  p("根据需求分析，系统功能划分为用户认证与管理、商品管理、订单交易、消息互动、公告反馈和后台管理六大模块。各功能模块之间的关系如图4-2所示。"),
  ...figure("fig_4_2_modules.png", "图4-2 功能模块图", 540),
  p("如图4-2所示，系统的六大功能模块相互协作，共同支撑校园二手物品交易的完整业务流程。学生用户通过用户认证与管理模块完成学号注册登录后，可使用商品管理模块发布闲置物品或浏览搜索商品，通过订单交易模块创建订单和完成交易，通过消息互动模块与交易对方进行私信沟通和线下预约自提。公告反馈模块为管理员与用户之间提供信息传达和意见收集渠道。后台管理模块为管理员提供系统全局管理能力，包括商品审核、用户管理、纠纷处理和数据统计等功能。"),
  tableCaption("表4-2 功能模块设计表"),
  createTable(
    ["模块名称", "子功能", "涉及实体", "关键接口"],
    [
      ["用户认证", "注册/登录/改密", "User, AdminUser", "POST /api/auth/register"],
      ["商品管理", "发布/审核/搜索", "Product, ProductCategory", "GET /api/products"],
      ["商品管理", "点赞/收藏/评论", "ProductLike, Favorite, Comment", "POST /api/products/{id}/like"],
      ["订单交易", "下单/流转/评价", "Order, Review", "POST /api/orders"],
      ["订单交易", "线下预约", "Appointment", "POST /api/appointments"],
      ["消息互动", "私信沟通", "Message", "POST /api/messages"],
      ["公告反馈", "公告/反馈", "Announcement, Feedback", "GET /api/announcements"],
      ["后台管理", "审核/统计/管理", "全部实体", "GET /api/admin/stats"],
    ],
    [1500, 2000, 2000, 3270]
  ),
  p("在用户认证与管理模块设计中，系统采用了前台用户与管理员分表存储的策略。AdminUser表和User表分别独立存储管理员和前台用户的数据，两表之间不存在外键关联。这种设计实现了前台与后台权限的物理隔离，即使前台用户数据被泄露也无法影响后台管理系统的安全。在Session管理方面，系统使用不同的Session键（adminUser和frontUser）区分两种用户角色的登录状态，LoginInterceptor根据请求路径和Session键进行差异化权限校验。User实体的studentId字段设置了唯一约束，作为学号实名认证的数据基础，确保一个学号只能注册一个账号。"),
  p("在商品管理模块设计中，Product实体包含标题、描述、价格、原价、图片URL、新旧程度、分类ID、卖家ID、审核状态和售出标识等字段。description字段使用@Column(length = 2000)注解扩展至2000字符，以容纳详细的商品说明；images字段使用@Column(length = 1000)注解存储多张图片URL（以分号分隔）。商品审核机制通过ProductAuditStatus枚举管理三种状态：PENDING（待审核）、APPROVED（已通过）和REJECTED（已驳回），新发布的商品默认为PENDING状态，需管理员审核通过后方可在前台展示，有效过滤违规内容。此外，ProductLike实体和Favorite实体分别实现商品的点赞和收藏功能，Comment实体实现商品评论功能，丰富了商品信息的互动维度。"),
  p("在订单交易模块设计中，Order实体包含订单号、商品ID、买家ID、卖家ID、成交价格、订单状态、买家留言和纠纷备注等字段。订单状态通过OrderStatus枚举管理四种状态：PENDING（待付款）、PAID（已付款）、COMPLETED（已完成）和CANCELLED（已取消）。成交价格在下单时从商品价格锁定，避免后续商品价格调整影响历史订单。Review实体通过orderId唯一约束保证一个订单只能评价一次，rating字段存储1至5的评分值，构建了校园交易的信用体系。Appointment实体支持买卖双方约定线下自提的时间和地点，实现O2O模式的便捷交易。"),
  h2("4.4 数据库设计"),
  h3("4.4.1 E-R图设计"),
  p("根据系统功能需求，数据库共设计13张实体表，分别为管理员表（AdminUser）、前台用户表（User）、商品表（Product）、商品分类表（ProductCategory）、订单表（Order）、消息表（Message）、评论表（Comment）、评价表（Review）、公告表（Announcement）、预约表（Appointment）、收藏表（Favorite）、点赞表（ProductLike）和反馈表（Feedback）。各实体之间的E-R关系如图4-3所示。"),
  ...figure("fig_4_3_er_diagram.png", "图4-3 E-R图", 540),
  p("如图4-3所示，系统实体间的关系如下：一个用户可以发布多个商品（User 1:N Product），一个用户可以作为买家创建多个订单（User 1:N Order），一个用户可以作为卖家收到多个订单（User 1:N Order），一个商品可以被多个订单引用（Product 1:N Order），一个用户可以发送多条消息（User 1:N Message），一个用户可以收到多条消息（User 1:N Message），一个订单最多有一条评价（Order 1:1 Review），一个用户可以收藏多个商品（User 1:N Favorite），一个用户可以点赞多个商品（User 1:N ProductLike），一个商品可以被多条评论引用（Product 1:N Comment）。管理员表与前台用户表独立存储，互不关联。"),
  p("在数据库设计过程中，遵循了关系型数据库的规范化原则。各实体表均满足第三范式（3NF），即每个非主属性既不部分依赖于候选码也不传递依赖于候选码。例如，订单表中不存储商品标题和用户昵称等冗余信息，而是通过productId、buyerId和sellerId外键引用Product表和User表的主键，在查询时通过JPA的关联查询获取关联数据。这种设计避免了数据冗余和更新异常，保障了数据的一致性。"),
  h3("4.4.2 数据表结构设计"),
  p("根据E-R图设计，系统共创建13张数据表，以下展示核心数据表的结构设计。"),
  tableCaption("表4-3 前台用户表（users）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["student_id", "VARCHAR(20)", "NOT NULL, UNIQUE", "学号（实名认证）"],
      ["username", "VARCHAR(50)", "NOT NULL, UNIQUE", "登录用户名"],
      ["password", "VARCHAR(100)", "NOT NULL", "BCrypt加密密码"],
      ["nickname", "VARCHAR(50)", "—", "用户昵称"],
      ["phone", "VARCHAR(20)", "—", "联系电话"],
      ["avatar", "VARCHAR(255)", "—", "头像URL"],
      ["status", "VARCHAR(20)", "NOT NULL", "账号状态（ACTIVE/BANNED）"],
      ["created_at", "TIMESTAMP", "NOT NULL", "注册时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-4 商品表（products）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["title", "VARCHAR(100)", "NOT NULL", "商品标题"],
      ["description", "VARCHAR(2000)", "—", "商品描述"],
      ["price", "DOUBLE", "NOT NULL", "期望价格（元）"],
      ["original_price", "DOUBLE", "—", "原价（展示性价比）"],
      ["images", "VARCHAR(1000)", "—", "图片URL（分号分隔）"],
      ["condition_level", "VARCHAR(50)", "—", "新旧程度（如9成新）"],
      ["category_id", "BIGINT", "NOT NULL", "所属分类ID"],
      ["seller_id", "BIGINT", "NOT NULL", "发布者用户ID"],
      ["audit_status", "VARCHAR(20)", "NOT NULL", "审核状态（PENDING/APPROVED/REJECTED）"],
      ["audit_remark", "VARCHAR(500)", "—", "审核备注（驳回原因）"],
      ["sold", "BOOLEAN", "NOT NULL", "是否已售出"],
      ["created_at", "TIMESTAMP", "NOT NULL", "发布时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-5 订单表（orders）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["order_no", "VARCHAR(32)", "NOT NULL, UNIQUE", "订单号（业务唯一）"],
      ["product_id", "BIGINT", "NOT NULL", "商品ID"],
      ["buyer_id", "BIGINT", "NOT NULL", "买家用户ID"],
      ["seller_id", "BIGINT", "NOT NULL", "卖家用户ID"],
      ["price", "DOUBLE", "NOT NULL", "成交价格（下单锁定）"],
      ["status", "VARCHAR(20)", "NOT NULL", "订单状态（PENDING/PAID/COMPLETED/CANCELLED）"],
      ["buyer_remark", "VARCHAR(500)", "—", "买家留言"],
      ["dispute_remark", "VARCHAR(500)", "—", "纠纷处理备注"],
      ["created_at", "TIMESTAMP", "NOT NULL", "下单时间"],
      ["completed_at", "TIMESTAMP", "—", "完成时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-6 消息表（messages）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["sender_id", "BIGINT", "NOT NULL", "发送者用户ID"],
      ["receiver_id", "BIGINT", "NOT NULL", "接收者用户ID"],
      ["product_id", "BIGINT", "—", "关联商品ID（可空）"],
      ["content", "VARCHAR(1000)", "NOT NULL", "消息内容"],
      ["read", "BOOLEAN", "NOT NULL", "是否已读"],
      ["created_at", "TIMESTAMP", "NOT NULL", "发送时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-7 评价表（reviews）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["order_id", "BIGINT", "NOT NULL, UNIQUE", "关联订单ID"],
      ["reviewer_id", "BIGINT", "NOT NULL", "评价者（买家）ID"],
      ["reviewee_id", "BIGINT", "NOT NULL", "被评价者（卖家）ID"],
      ["rating", "INT", "NOT NULL", "评分（1-5）"],
      ["content", "VARCHAR(500)", "—", "评价内容"],
      ["created_at", "TIMESTAMP", "NOT NULL", "评价时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  p("上述核心数据表的设计充分考虑了校园二手物品交易业务的数据管理需求。用户表通过student_id唯一约束实现了学号实名认证机制，password字段存储BCrypt加密后的哈希密文。商品表通过audit_status字段管理商品审核流程，description和images字段使用较大的VARCHAR长度以承载详细的商品信息。订单表通过order_no业务唯一标识对外暴露，price字段在下单时锁定成交价格。消息表通过product_id可选字段支持商品关联私信和纯用户间私信两种场景。评价表通过order_id唯一约束保证一单只能评价一次，rating字段存储1至5的整数评分。"),
  p("在字段类型选择方面，主键统一使用BIGINT自增类型，保证主键的唯一性和递增性。密码字段使用VARCHAR(100)类型存储BCrypt加密后的哈希字符串。价格类数值字段使用DOUBLE类型，满足小数精度需求。时间字段使用TIMESTAMP类型，精确记录到秒级。枚举类型字段（status、audit_status）使用VARCHAR(20)并以字符串形式持久化，便于运维人员直接查看数据库排查状态。文本类字段根据内容长度需求设置不同的VARCHAR长度：用户名和昵称使用50字符，商品标题使用100字符，商品描述扩展至2000字符，备注类字段设置为500字符。"),
  h3("4.4.3 订单状态流转设计"),
  p("订单状态流转是系统核心业务逻辑之一，本系统采用枚举类型OrderStatus定义了4个订单状态。订单从创建到完成需经过状态流转，状态流转规则为单向推进，COMPLETED和CANCELLED为终态不可变更。订单状态流转如图4-4所示。"),
  ...figure("fig_4_4_order_flow.png", "图4-4 订单状态流转图", 540),
  p("如图4-4所示，订单状态的4个阶段及其含义如下："),
  p("（1）PENDING（待付款）：买家刚下单，尚未付款。买家可在此状态下取消订单或确认付款。"),
  p("（2）PAID（已付款）：买家确认付款，表示达成交易意向。买家可在此状态下取消订单，或确认线下自提完成交易。"),
  p("（3）COMPLETED（已完成）：买卖双方线下完成自提，订单流程结束。买家可对卖家进行评价。"),
  p("（4）CANCELLED（已取消）：买家主动取消或纠纷介入后取消，订单流程终止。"),
  p("订单状态推进逻辑在OrderService中实现。PENDING状态可流转至PAID（买家确认付款）或CANCELLED（买家取消）；PAID状态可流转至COMPLETED（确认完成）或CANCELLED（买家取消）；COMPLETED和CANCELLED为终态，不可再变更。这种基于枚举的状态机设计简洁可靠，保证了状态流转的规范性和终态稳定性。商品审核状态流转采用类似设计，ProductAuditStatus枚举定义PENDING（待审核）、APPROVED（已通过）和REJECTED（已驳回）三种状态，管理员通过审核操作将商品从PENDING推进至APPROVED或REJECTED，APPROVED和REJECTED为终态。"),
  pageBreak(),
];

// ---------- 第5章 功能实现 ----------
const chapter5Children = [
  h1("第5章 功能实现"),
  p("本章详细描述大学生闲置二手物品交易网站系统各功能模块的实现过程，包括用户认证模块、商品管理模块、订单交易模块、消息互动模块和后台管理模块五个方面，并结合流程图和时序图说明实现细节。"),
  h2("5.1 用户认证模块实现"),
  p("用户认证模块是系统的安全入口，负责用户身份认证与会话管理。系统将前台学生用户和管理员分表存储，前台用户使用User实体，管理员使用AdminUser实体，两者通过不同的Controller和Session键进行区分。"),
  p("用户注册时，AuthController接收RegisterRequest（包含学号、用户名、密码和昵称），调用AuthService进行注册处理。AuthService首先检查学号和用户名是否已存在，确保唯一性；然后使用BCryptPasswordEncoder对密码进行加密，BCrypt算法在每次加密时生成随机盐值并嵌入密文中，即使两个用户设置相同的密码，数据库中存储的密文也不同，有效防止彩虹表攻击；最后创建User实体并设置默认状态为ACTIVE，通过UserRepository持久化到数据库。学号唯一约束是实名认证机制的核心，确保一个学号只能注册一个账号，从数据层面保障了用户身份的真实性。"),
  p("用户登录时，AuthController接收LoginRequest（包含用户名和密码），调用AuthService进行身份验证。AuthService根据用户名通过UserRepository查询用户记录，首先检查用户是否存在以及账号状态是否为ACTIVE（被封禁用户不允许登录），然后使用BCryptPasswordEncoder的matches方法对用户输入的明文密码与数据库中的密文进行比对。验证通过后，将User对象存入HttpSession中，键名为frontUser。后续请求通过Session中的用户对象进行身份识别。管理员登录流程与用户登录类似，但使用AdminUser实体和独立的Session键adminUser。"),
  p("LoginInterceptor实现了HandlerInterceptor接口，在preHandle方法中根据请求URI和HTTP方法进行权限判定。拦截规则如下：认证相关接口（登录、登出、注册）始终放行；GET请求（浏览查询）放行；/api/admin/**路径下的写操作需管理员Session；其余写操作需前台用户Session。未通过认证的请求返回HTTP 401状态码。登录认证的完整时序如图4-6所示。"),
  ...figure("fig_4_6_login_sequence.png", "图4-6 登录认证时序图", 500),
  p("如图4-6所示，登录认证的完整流程为：用户在前端页面输入用户名和密码，通过fetch API发送POST请求至/api/auth/login接口；AuthController接收请求并调用AuthService进行身份验证；AuthService从UserRepository查询用户记录，检查账号状态并使用BCryptPasswordEncoder比对密码；验证通过后将User对象存入HttpSession，返回登录成功响应（包含用户基本信息）；前端接收响应后更新页面显示用户信息并跳转至主页。后续请求携带Session Cookie，LoginInterceptor从Session中获取用户对象进行权限校验，未通过校验的请求被拦截并返回401状态码。"),
  h2("5.2 商品管理模块实现"),
  p("商品管理模块是系统的核心业务模块，涵盖商品发布、审核、搜索、点赞、收藏和评论等功能。"),
  p("商品发布功能由ProductController接收ProductRequest，调用ProductService进行处理。ProductService首先从Session获取当前登录用户ID作为卖家ID，然后创建Product实体并设置默认审核状态为PENDING（待审核），通过ProductRepository持久化。新发布的商品需经管理员审核通过后方可在前台展示，这一机制有效过滤了虚假和违规商品信息，维护了平台的健康生态。Product实体的description字段长度扩展至2000字符，images字段存储多张图片URL（以分号分隔），conditionLevel字段描述新旧程度（如\u201C9成新\u201D），为买家提供详细的商品信息。"),
  p("商品审核功能由AdminController接收AuditRequest，调用ProductService执行审核操作。管理员可将商品审核状态从PENDING推进至APPROVED（审核通过，前台可展示）或REJECTED（审核驳回，填写驳回原因）。ProductAuditStatus枚举定义了三种状态，APPROVED和REJECTED为终态不可变更。审核驳回时，管理员填写的驳回原因存储在Product实体的auditRemark字段中，反馈给卖家以便修改后重新发布。"),
  p("商品浏览与搜索功能通过GET /api/products接口实现。ProductService调用ProductRepository的查询方法，仅返回审核状态为APPROVED且未售出的商品。搜索支持按分类ID筛选和关键词模糊匹配，关键词搜索采用LIKE模糊匹配查询商品标题和描述中包含关键词的记录。商品列表按发布时间倒序排列，使最新发布的商品优先展示。"),
  p("商品点赞和收藏功能分别由ProductLike实体和Favorite实体支撑。用户点击点赞按钮时，前端通过POST /api/products/{id}/like接口发送请求，ProductService检查用户是否已点赞该商品（通过ProductLikeRepository查询），未点赞则创建ProductLike记录，已点赞则取消点赞（删除记录），实现点赞的切换效果。收藏功能采用类似设计，收藏的商品可在用户的收藏列表中查看。商品评论功能由Comment实体支撑，用户通过POST /api/products/{id}/comments接口发表评论，评论列表通过GET /api/products/{id}/comments接口获取，按时间排列展示。"),
  h2("5.3 订单交易模块实现"),
  p("订单交易模块管理买卖双方的交易全流程，涵盖订单创建、状态流转、交易评价和线下预约自提等功能。交易流程如图4-5所示。"),
  ...figure("fig_4_5_trade_process.png", "图4-5 交易流程图", 480),
  p("如图4-5所示，校园二手物品交易的完整流程为：买家浏览商品列表，通过搜索或分类筛选找到心仪商品；买家通过私信功能与卖家沟通商品细节和价格；买家确认购买后创建订单，系统生成唯一订单号并锁定成交价格；买家确认付款后订单状态从PENDING流转至PAID；买卖双方通过预约功能约定线下自提的时间和地点；线下自提完成后买家确认收货，订单状态流转至COMPLETED；买家可对卖家进行1至5星评价，完成交易闭环。买家也可在PENDING或PAID阶段取消订单，订单状态变为CANCELLED。"),
  p("订单创建功能由OrderController接收OrderRequest，调用OrderService进行处理。OrderService首先从Session获取当前登录用户ID作为买家ID，验证商品是否存在且审核通过、商品是否已被售出、买家与卖家是否为同一用户；然后从Product实体获取卖家ID和商品价格作为成交价格（下单时锁定）；接着生成唯一订单号，创建Order实体并设置初始状态为PENDING，通过OrderRepository持久化。成交价格在下单时从商品价格锁定，避免后续商品价格调整影响历史订单。"),
  p("订单状态流转功能由OrderService实现。买家确认付款时，系统将订单状态从PENDING更新为PAID；买家确认收货时，系统将订单状态从PAID更新为COMPLETED，并记录完成时间；买家取消订单时，系统将订单状态从PENDING或PAID更新为CANCELLED。状态流转规则在Service层进行校验，COMPLETED和CANCELLED为终态不可变更，非法流转操作将被拒绝。订单查询功能分为买家视角和卖家视角，买家通过GET /api/orders接口查看自己的购买订单，卖家通过GET /api/orders/sold接口查看自己收到的销售订单，管理员通过GET /api/admin/orders接口查看全部订单。"),
  p("交易评价功能由ReviewController接收ReviewRequest，调用ReviewService进行处理。ReviewService首先验证订单状态是否为COMPLETED（只有已完成订单才能评价），然后检查该订单是否已被评价（Review实体的orderId唯一约束保证一单只能评价一次），最后创建Review实体并设置评分（1至5）和评价内容。评价结果与卖家用户信息关联展示，为后续交易者提供信用参考，构建了校园交易的信任体系。线下预约自提功能由AppointmentController接收AppointmentRequest，调用AppointmentService创建预约记录，包含预约时间和地点等信息，买卖双方均可查看预约详情，实现O2O模式的便捷线下交易。"),
  h2("5.4 消息互动模块实现"),
  p("消息互动模块支持买卖双方基于商品进行一对一私信沟通，涵盖消息发送、消息列表查看和已读状态管理等功能。"),
  p("私信发送功能由MessageController接收MessageRequest，调用MessageService进行处理。MessageService首先从Session获取当前登录用户ID作为发送者ID，验证接收者用户是否存在，然后创建Message实体并设置默认已读状态为false，通过MessageRepository持久化。Message实体的productId字段为可选字段，为null表示纯用户间私信，非null表示基于某商品的咨询沟通。消息内容字段长度限制为1000字符，避免长文本影响存储与展示。"),
  p("消息列表查看功能分为收件箱和发件箱两个视角。用户通过GET /api/messages/received接口查看收到的消息列表，通过GET /api/messages/sent接口查看发送的消息列表，均按发送时间倒序排列。消息的已读状态通过read字段管理，用户查看消息后系统自动将该消息标记为已读（read=true），前端根据未读消息数量显示红点提示，帮助用户及时关注新消息。消息永久留存于数据库中，为交易纠纷取证提供依据。"),
  p("商品评论功能由CommentController和CommentService实现，用户可对商品发表评论，评论列表通过GET /api/products/{id}/comments接口获取。评论功能与私信功能共同构成了系统的消息互动体系，私信支持买卖双方的一对一深度沟通，评论支持多用户围绕商品的公开讨论，两者互为补充，提升了平台的社交互动性。"),
  h2("5.5 后台管理模块实现"),
  p("后台管理模块为管理员提供系统全局管理能力，涵盖管理员登录、仪表盘统计、用户管理、商品审核、订单管理和纠纷处理等功能。"),
  p("管理员登录使用独立的AuthController接口和AdminUser实体，与前台用户分表存储、分Session键管理。管理员通过POST /api/auth/admin/login接口登录，验证通过后将AdminUser对象存入Session，键名为adminUser。后台管理接口统一以/api/admin/为前缀，LoginInterceptor对该前缀下的写操作进行管理员Session校验，确保仅管理员可访问。"),
  p("仪表盘统计功能通过GET /api/admin/stats接口实现，StatsService调用各Repository的count方法获取用户总数、商品总数、订单总数等统计数据，封装为StatsResponse DTO返回前端。仪表盘使管理员能够快速了解平台运营概况。用户管理功能通过GET /api/admin/users接口查看所有注册用户的基本信息，管理员可对违规用户执行封禁操作（将status设置为BANNED）或解封操作（将status设置为ACTIVE），被封禁用户无法登录系统。"),
  p("商品审核管理功能通过GET /api/admin/products/pending接口查看待审核商品列表，通过PUT /api/admin/products/{id}/audit接口执行审核操作（通过或驳回）。管理员在审核商品时查看商品标题、描述、价格、图片等信息，判断商品是否符合平台规范。审核通过的商品在前台展示，审核驳回的商品附带驳回原因反馈给卖家。"),
  p("订单管理功能通过GET /api/admin/orders接口查看全部订单信息，管理员可了解平台整体交易状况。纠纷处理功能允许管理员对交易纠纷进行介入，通过PUT /api/admin/orders/{id}/dispute接口填写纠纷处理备注，记录处理过程和结论。公告管理功能通过AnnouncementController实现公告的发布、编辑和删除，公告内容在前台首页展示。反馈管理功能通过FeedbackController实现用户反馈的查看和回复，管理员可查看用户提交的意见反馈并进行回复，形成双向沟通闭环。"),
  p("在DTO设计方面，系统定义了26个数据传输对象，涵盖请求DTO和响应DTO两类。请求DTO（如LoginRequest、RegisterRequest、ProductRequest、OrderRequest等）使用Jakarta Validation注解（@NotBlank、@NotNull）进行参数校验，Controller层通过@Valid注解触发自动校验。响应DTO（如ProductResponse、OrderResponse、UserResponse等）不包含密码等敏感字段，OrderResponse还包含了关联实体的名称信息（如商品标题、买家昵称、卖家昵称等），避免了前端需要额外发送请求获取关联数据的不便。LoginResponse提供了from(AdminUser)和from(User)两个工厂方法，分别用于管理员和前台用户的登录响应构建。"),
  p("在前端交互设计方面，index.html用户端页面采用Tab导航栏组织主要功能区，包括首页、商品列表、发布商品、我的订单、消息中心和意见反馈等模块。商品展示采用卡片网格布局，通过CSS Flexbox实现自适应排列。商品详情页面展示商品完整信息、卖家信息和评论区。订单页面以列表形式展示用户的购买和销售订单，每条订单记录显示订单号、商品信息、成交价格和当前状态。admin.html管理后台页面采用侧边栏导航布局，包含仪表盘、商品审核、用户管理、订单管理、公告管理和反馈管理等模块。管理员登录前显示独立的登录页面，登录成功后进入管理主界面。"),
  p("在异常处理与错误响应方面，系统采用了统一的异常处理策略。Controller层使用@RestControllerAdvice注解的全局异常处理器，捕获IllegalArgumentException、MethodArgumentNotValidException等异常并转换为规范的JSON错误响应。当请求参数未通过Validation校验时，系统返回HTTP 400状态码；当用户未登录或权限不足时，LoginInterceptor返回HTTP 401状态码；当请求的资源不存在时，系统返回HTTP 404状态码。前端通过fetch API的response.ok属性和response.status状态码判断请求结果，在界面上向用户展示友好的错误提示信息。"),
  pageBreak(),
];

// ---------- 第6章 系统测试 ----------
const chapter6Children = [
  h1("第6章 系统测试"),
  p("系统测试是软件开发过程中的重要质量保障环节，旨在验证系统功能是否满足需求规格说明书中定义的要求，发现并修复系统中存在的缺陷。本章从测试方案和测试结果两个方面对大学生闲置二手物品交易网站系统进行全面测试。"),
  h2("6.1 测试方案"),
  p("本系统测试采用黑盒测试方法，从功能测试、安全测试和兼容性测试三个维度对系统进行全面验证。"),
  h3("6.1.1 功能测试"),
  p("功能测试以需求分析阶段定义的功能性需求为基准，验证系统各功能模块是否正确实现了预期功能。测试范围覆盖学号注册登录、商品发布审核、商品浏览搜索、商品点赞收藏评论、订单创建查询、订单状态流转、交易评价、私信沟通、线下预约、公告发布、意见反馈和后台管理等全部功能模块。测试方法采用等价类划分法和边界值分析法，设计正向测试用例和反向测试用例，验证系统在正常输入和异常输入下的行为表现。"),
  h3("6.1.2 安全测试"),
  p("安全测试重点验证系统的安全防护机制是否有效。测试内容包括：未登录用户访问受保护接口是否返回401状态码；用户是否能够访问或操作他人的订单数据和消息数据（越权测试）；密码是否以BCrypt加密形式存储（数据库验证）；管理员接口是否仅允许管理员访问；被封禁用户是否无法登录；商品审核机制是否有效过滤违规内容。"),
  h3("6.1.3 兼容性测试"),
  p("兼容性测试验证系统在不同浏览器和不同屏幕尺寸下的表现。测试浏览器包括Google Chrome、Mozilla Firefox、Microsoft Edge等主流浏览器，验证页面渲染一致性、JavaScript功能正常性和fetch API兼容性。同时测试系统在不同屏幕宽度下的响应式布局表现。"),
  h2("6.2 测试结果"),
  p("根据测试方案设计的测试用例，对系统进行了全面测试。测试用例及结果如表6-1所示。"),
  tableCaption("表6-1 系统测试用例表"),
  createTable(
    ["编号", "测试模块", "测试用例", "预期结果", "实际结果", "结论"],
    [
      ["TC-01", "学号注册", "输入合法学号/用户名/密码注册", "注册成功，密码BCrypt加密", "注册成功", "通过"],
      ["TC-02", "学号注册", "输入已存在的学号注册", "提示学号已存在", "提示学号已存在", "通过"],
      ["TC-03", "学号注册", "输入已存在的用户名注册", "提示用户名已存在", "提示用户名已存在", "通过"],
      ["TC-04", "用户登录", "输入正确的用户名和密码", "登录成功，创建Session", "登录成功", "通过"],
      ["TC-05", "用户登录", "输入错误的密码", "提示密码错误", "提示密码错误", "通过"],
      ["TC-06", "用户登录", "被封禁用户尝试登录", "提示账号已被封禁", "提示账号已封禁", "通过"],
      ["TC-07", "商品发布", "填写完整商品信息发布", "发布成功，状态为待审核", "发布成功", "通过"],
      ["TC-08", "商品审核", "管理员审核通过商品", "商品状态变为已通过，前台展示", "审核通过", "通过"],
      ["TC-09", "商品审核", "管理员驳回商品并填写原因", "商品状态变为已驳回", "审核驳回", "通过"],
      ["TC-10", "商品搜索", "按分类筛选商品", "返回该分类下的商品", "返回正确结果", "通过"],
      ["TC-11", "商品搜索", "输入关键词搜索", "返回标题/描述匹配的商品", "返回正确结果", "通过"],
      ["TC-12", "商品点赞", "用户点赞商品", "点赞成功，点赞数+1", "点赞成功", "通过"],
      ["TC-13", "商品收藏", "用户收藏商品", "收藏成功，收藏列表可见", "收藏成功", "通过"],
      ["TC-14", "商品评论", "用户发表商品评论", "评论成功，评论列表可见", "评论成功", "通过"],
      ["TC-15", "订单创建", "对商品下单", "创建订单，价格锁定", "创建成功", "通过"],
      ["TC-16", "订单创建", "对自己的商品下单", "拒绝操作，提示不能购买自己商品", "拒绝操作", "通过"],
      ["TC-17", "状态流转", "买家确认付款", "状态PENDING→PAID", "状态正确流转", "通过"],
      ["TC-18", "状态流转", "买家确认收货", "状态PAID→COMPLETED", "状态正确流转", "通过"],
      ["TC-19", "状态流转", "买家取消待付款订单", "状态PENDING→CANCELLED", "状态正确流转", "通过"],
      ["TC-20", "状态流转", "推进已完成订单状态", "拒绝操作，终态不可变更", "拒绝操作", "通过"],
      ["TC-21", "交易评价", "买家对已完成订单评价", "评价成功，1-5星评分", "评价成功", "通过"],
      ["TC-22", "交易评价", "对同一订单重复评价", "拒绝操作，一单只能评价一次", "拒绝操作", "通过"],
      ["TC-23", "私信沟通", "用户发送私信", "消息发送成功", "发送成功", "通过"],
      ["TC-24", "权限控制", "未登录访问POST接口", "返回401未授权", "返回401", "通过"],
      ["TC-25", "权限控制", "普通用户访问admin接口", "返回401未授权", "返回401", "通过"],
      ["TC-26", "后台管理", "管理员查看仪表盘统计", "返回正确统计数据", "数据正确", "通过"],
      ["TC-27", "后台管理", "管理员封禁违规用户", "用户状态变为BANNED", "封禁成功", "通过"],
      ["TC-28", "数据持久化", "重启应用后查询数据", "数据完整保留", "数据完整", "通过"],
      ["TC-29", "参数校验", "提交空用户名注册", "返回400参数错误", "返回400", "通过"],
      ["TC-30", "线下预约", "创建线下自提预约", "预约成功，记录时间和地点", "预约成功", "通过"],
    ],
    [700, 1200, 2200, 2000, 1700, 970]
  ),
  p("测试结果表明，系统全部30个测试用例均通过验证，功能正确性、安全性和兼容性均达到预期设计目标。具体测试结论如下："),
  p("（1）功能测试方面：学号注册登录、商品发布审核、商品浏览搜索、商品点赞收藏评论、订单创建查询、订单状态流转、交易评价、私信沟通、线下预约、公告发布、意见反馈和后台管理等全部功能模块均按照需求规格正确实现，正向操作和异常输入均得到正确处理。"),
  p("（2）安全测试方面：BCrypt密码加密、Session认证、LoginInterceptor拦截器权限控制、商品审核机制和账号封禁机制等安全机制均有效运行，未登录访问、越权操作和被封禁用户登录等攻击均被成功拦截。"),
  p("（3）兼容性测试方面：系统在Chrome、Firefox、Edge等主流浏览器中页面渲染一致、功能正常，响应式布局在不同屏幕尺寸下表现良好。"),
  p("（4）性能测试方面：系统启动时间约3秒（Spring Boot初始化与H2数据库连接），API接口平均响应时间在100毫秒以内，页面首次加载时间在2秒以内，满足课程设计项目的性能要求。H2文件数据库的读写性能在单机环境下表现良好，JPA的一级缓存机制有效减少了重复查询的数据库访问次数。"),
  p("（5）数据持久化测试方面：通过多次重启应用程序验证H2文件模式的数据持久化能力。每次重启后，H2数据库自动从./data/campusdb.mv.db文件恢复数据，历史用户、商品、订单等数据完整保留，DataInitializer的幂等设计确保了重启时不产生重复数据。测试确认了系统在异常关闭后仍能正确恢复数据，验证了H2文件数据库的可靠性。"),
  p("（6）边界条件测试方面：针对空字符串提交、超长文本输入、非法参数值等边界条件进行了测试。Jakarta Validation的@NotBlank和@NotNull注解有效拦截了空值提交，@Column(length)注解确保了超长文本不会导致数据库截断异常。订单状态推进在COMPLETED和CANCELLED终态时拒绝操作，防止了终态后的非法流转。重复评价被orderId唯一约束拦截，保证了评价的不可重复性。这些边界条件测试验证了系统的健壮性和异常处理能力。"),
  p("（7）测试环境与方法方面：本系统的测试环境基于Windows操作系统，使用JDK 25作为Java运行环境，Maven作为项目构建工具，应用通过内嵌Tomcat服务器运行于8083端口。测试过程中采用手动测试与自动化验证相结合的方式：功能测试通过浏览器手动操作前端界面并观察响应结果；接口测试通过浏览器开发者工具的Network面板查看HTTP请求和响应详情；数据验证通过H2数据库的Web控制台（/h2-console）直接查询数据库表数据，验证数据的正确存储和关联关系。"),
  p("（8）测试总结与质量评估方面：经过全面的系统测试，本系统在功能完整性、安全防护、兼容性和性能方面均达到了课程设计的要求。30个测试用例全部通过，测试覆盖了系统的全部功能模块和关键安全机制。系统在正常流程和异常流程下均表现稳定，未出现崩溃、数据丢失或安全漏洞等问题。从软件质量的角度评估，系统具备良好的功能性、可靠性、安全性和易用性。然而，由于课程设计的时间和资源限制，本系统的测试仍存在一定局限性：未进行压力测试和并发测试，自动化测试覆盖率有待提高。在后续开发中，可引入JUnit单元测试和Spring Boot Test集成测试框架，建立持续集成的自动化测试流程，进一步提升软件质量保障水平。"),
  pageBreak(),
];

// ---------- 结论 ----------
const conclusionChildren = [
  h1("结论"),
  p("本文以大学生闲置二手物品交易为背景，设计并实现了一个基于Spring Boot框架的校园二手交易网站系统。通过需求分析、系统设计、功能实现和系统测试四个阶段的完整开发流程，成功构建了一个功能完善、安全可靠、操作便捷的校园二手物品交易平台。"),
  p("在技术实现方面，系统采用Spring Boot 4.1.0框架与Java 25语言开发，利用Spring Boot的自动配置特性简化了项目搭建过程；通过JPA/Hibernate实现了对象关系映射，使用Spring Data JPA的方法名派生查询大幅减少了数据访问层的样板代码；采用H2文件数据库实现了零安装的数据持久化方案，数据文件存储在./data/campusdb.mv.db中，应用重启后数据完整保留；前端使用原生HTML/CSS/JavaScript技术栈，通过fetch API与后端RESTful接口进行通信。系统采用分层架构设计，将表现层、控制层、业务层和数据访问层进行解耦，代码结构清晰、可维护性好。"),
  p("在功能实现方面，系统完成了学号实名认证注册登录、商品发布与审核、商品浏览搜索、商品点赞收藏评论、订单创建与状态流转、交易评价、私信沟通、线下预约自提、公告发布、意见反馈和后台管理等全部核心功能模块。系统共设计13个JPA实体、11个控制器、12个服务类、13个数据访问接口和26个数据传输对象，功能覆盖了校园二手物品交易的完整业务流程。订单管理模块实现了PENDING→PAID→COMPLETED的单向状态流转和CANCELLED取消机制，保障了交易流程的规范性。商品审核模块通过PENDING→APPROVED/REJECTED的状态管理，有效过滤了违规内容。"),
  p("本系统的主要技术亮点包括：第一，学号实名认证机制通过studentId唯一约束确保用户身份的真实性，建立了基于校园身份的信任体系，这是综合性二手交易平台所不具备的特色优势。第二，前台用户与管理员分表存储、分Session键管理的双角色认证机制，实现了清晰的权限分离。第三，订单状态机和商品审核状态机均采用枚举类型定义有限状态集合，通过状态机规则约束状态流转路径，保证了业务流程的规范性和数据的终态稳定性。第四，线下预约自提功能结合校园地理优势，实现了O2O模式的便捷交易体验，免去了物流配送的成本和等待时间。第五，交易评价功能通过orderId唯一约束保证一单只能评价一次，1至5星评分体系构建了校园交易的信用参考。"),
  p("然而，本系统仍存在一些不足之处，有待在后续工作中改进和完善。第一，前端采用原生JavaScript开发，代码组织较为分散，未来可考虑引入Vue.js或React等前端框架提升开发效率和代码可维护性。第二，H2数据库适合开发和测试环境，在生产环境中应迁移至MySQL或PostgreSQL等生产级数据库，以获得更好的性能和并发处理能力。第三，系统缺乏图片上传功能，商品图片目前以URL形式存储，未来可增加图片上传与存储功能以提升用户体验。第四，系统目前缺少支付功能集成，未来可对接支付宝或微信支付实现线上支付闭环。第五，系统可进一步引入消息通知机制，在订单状态变更或收到私信时通过短信或站内消息通知用户，提升用户体验。第六，系统可引入推荐算法，根据用户的浏览和搜索历史推荐感兴趣的商品，提高交易匹配效率。"),
  p("在开发过程中，本文深刻体会到了Spring Boot框架在提升开发效率方面的显著优势。Spring Boot的自动配置机制根据项目依赖自动装配Bean组件，开发者无需编写繁琐的XML配置文件即可快速搭建项目框架；起步依赖将相关依赖打包管理，避免了版本冲突和依赖缺失问题；内嵌Tomcat服务器使应用可以打包为独立JAR文件运行，简化了部署流程。同时，Spring Data JPA的方法名派生查询功能大幅减少了数据访问层的样板代码，开发者只需定义Repository接口并遵循命名规范，框架便自动生成查询逻辑，提高了开发效率和代码可读性。"),
  p("在工程实践方面，本文积累了若干有价值的开发经验。第一，分层架构设计时应严格遵循依赖方向，上层依赖下层，通过接口而非实现类进行依赖注入，确保各层可独立测试和替换。第二，DTO模式在分层架构中发挥了重要作用，请求DTO封装了输入参数并附加校验注解，响应DTO过滤了敏感字段并补充了关联信息，有效隔离了领域模型与传输模型。第三，枚举类型在业务状态管理中具有天然优势，相比字符串常量，枚举提供了类型安全保证和ordinal序号特性，非常适合表示订单状态和审核状态等有限状态集合。第四，拦截器相比过滤器更适用于Spring MVC环境下的权限控制，因为拦截器可以访问HandlerMethod，实现更精细化的请求处理。"),
  p("展望未来，随着人工智能和移动互联网技术的发展，校园二手交易领域将迎来更多创新机遇。一方面，基于自然语言处理的商品信息智能分类和标签提取技术可降低用户发布商品的操作成本，提升商品信息的结构化程度。另一方面，基于协同过滤的商品推荐算法可根据用户的浏览和搜索历史，智能推荐感兴趣的商品，提高交易匹配效率。此外，移动端小程序的开发可进一步降低用户的使用门槛，实现随时随地的商品浏览和交易沟通。这些前沿技术的融入将推动校园二手交易向智能化、便捷化和社交化方向发展，为大学生提供更加优质的二手物品交易服务。"),
  pageBreak(),
];

// ---------- 参考文献 ----------
const references = [
  "[1] 匡卫东, 张颖. 校园二手交易系统的设计与实现[J]. 计算机时代, 2018, 26(4): 16-18.",
  "[2] 万成, 李方. 基于JavaWeb架构的校园二手交易平台设计与实现[J]. 计算机技术与发展, 2018, 28(11): 23-25.",
  "[3] 吴丽, 邓宪坤. 基于Spring Boot的二手交易平台设计[J]. 计算机技术与发展, 2019, 29(3): 19-22.",
  "[4] 张宝才, 冯世林, 刘晓丹. 二手交易市场现状与发展趋势分析[J]. 现代工商管理, 2015(22): 126-127.",
  "[5] 王春光, 陈妮娜. 基于JavaEE的二手交易平台的设计与实现[J]. 计算机时代, 2019, 27(7): 41-43.",
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
  creator: "王明哲",
  lastModifiedBy: "王明哲",
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

/** 生成并写入文件 */
async function generate() {
  console.log("正在生成论文文档...");
  const buffer = await Packer.toBuffer(doc);
  fs.writeFileSync(OUTPUT_PATH, buffer);
  console.log(`论文已生成：${OUTPUT_PATH}`);
  console.log(`文件大小：${(buffer.length / 1024).toFixed(1)} KB`);
}

generate().catch((err) => {
  console.error("生成失败：", err);
  process.exit(1);
});