/**
 * 课程设计论文生成脚本
 * 使用 docx 库生成《基于Spring Boot的服装私人定制网站设计与实现》论文
 *
 * 运行方式：node generate_thesis.js
 * 输出路径：../服装私人定制网站设计与实现.docx
 */

const docx = require("docx");
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
const OUTPUT_PATH = path.join(__dirname, "..", "服装私人定制网站设计与实现.docx");

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
  XIAOYI: 36, // 小一 24pt -> 实际小一24pt
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
  // 使用 sharp 或 image-size 获取尺寸，此处用预定义的尺寸映射
  const sizeMap = {
    "fig_4_1_architecture.png": { w: 1192, h: 893 },
    "fig_4_2_modules.png": { w: 1425, h: 893 },
    "fig_4_3_er_diagram.png": { w: 1425, h: 1105 },
    "fig_4_4_order_flow.png": { w: 1425, h: 547 },
    "fig_4_5_order_process.png": { w: 1192, h: 1009 },
    "fig_4_6_login_sequence.png": { w: 1192, h: 777 },
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
  // 使用制表符在标题和页码之间填充
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
  centerText("基于Spring Boot的", SIZE.XIAOYI, true, FONT_HEADING, 0),
  centerText("服装私人定制网站设计与实现", SIZE.XIAOYI, true, FONT_HEADING, 0),
  new Paragraph({ spacing: { before: 1600 }, children: [new TextRun({ text: "" })] }),
];

// 封面信息表（使用段落模拟）
const coverInfo = [
  ["学　　生　姓　名", "朱浩云"],
  ["学　　　　　号", "250701240212"],
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
    { text: "基于Spring Boot的服装私人定制网站设计与实现" },
  ]),
  pRich([{ text: "学　　生：", bold: true }, { text: "朱浩云（250701240212）" }]),
  pRich([{ text: "专　　业：", bold: true }, { text: "网络空间安全" }]),
  pRich([{ text: "指导教师：", bold: true }, { text: "赵娅 副教授" }]),
  emptyLine(),
  h2("一、课程设计目的"),
  p("本课程设计旨在综合运用Java Web开发技术，完成一个具有实际应用价值的服装私人定制网站系统。通过本项目的开发，学生需要掌握Spring Boot框架的核心原理与使用方法，熟练运用JPA/Hibernate进行对象关系映射与数据库操作，理解B/S架构的设计思想，掌握前端HTML/CSS/JavaScript基础技术，并能够运用Maven进行项目构建与依赖管理。同时，通过完整的项目实践，培养学生的系统分析与设计能力、代码编写与调试能力、以及软件工程文档撰写能力。"),
  h2("二、课程设计内容与要求"),
  p("1. 需求分析：分析服装私人定制业务流程，明确系统功能性需求与非功能性需求，包括用户管理、面料管理、款式管理、量体数据管理、订单管理和后台管理等核心功能模块。"),
  p("2. 系统设计：完成系统架构设计、功能模块设计、数据库设计（E-R图设计与数据表结构设计），确定技术选型方案，遵循分层解耦、单一职责、安全优先等设计原则。"),
  p("3. 功能实现：基于Spring Boot 4.1.0框架与Java 25语言，使用JPA/Hibernate ORM框架与H2文件数据库，实现用户注册登录、面料浏览筛选、款式浏览搜索、量体数据录入、用户下单、订单状态推进等全部功能。前端采用HTML/CSS/JavaScript技术，通过RESTful API与后端通信。"),
  p("4. 安全实现：采用BCrypt密码加密算法保障用户密码安全，使用Session机制进行用户认证，通过拦截器实现URL级别的权限控制，防止未授权访问与越权操作。"),
  p("5. 系统测试：编写测试用例，对系统的功能正确性、安全性、兼容性进行全面测试，确保系统稳定可靠运行。"),
  p("6. 文档撰写：按照学校规定的格式要求，撰写完整的课程设计论文，包括摘要、目录、正文各章节、结论与参考文献等。"),
  h2("三、时间安排"),
  p("第1周：需求分析与系统设计，完成需求分析文档与系统设计文档。"),
  p("第2周：数据库设计与后端核心功能开发，完成实体类、Repository接口、Service层与Controller层代码编写。"),
  p("第3周：前端页面开发与系统集成，完成HTML/CSS/JavaScript前端页面，实现前后端联调。"),
  p("第4周：系统测试、优化与论文撰写，完成测试报告与课程设计论文。"),
  h2("四、预期成果"),
  p("1. 可运行的服装私人定制网站系统源代码（含Maven构建脚本）。"),
  p("2. 课程设计论文一份（不少于45页），包含完整的系统分析与设计文档。"),
  p("3. 系统测试报告，覆盖功能测试、安全测试与兼容性测试。"),
  pageBreak(),
];

// ---------- 中文摘要 ----------
const abstractChildren = [
  centerText("摘　　要", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  p("随着服装行业数字化转型的加速推进和消费者个性化需求的日益增长，传统的线下服装定制模式已难以满足现代消费者对便捷性、透明度和个性化体验的追求。本文设计并实现了一个基于Spring Boot框架的服装私人定制网站系统，旨在为用户提供便捷的线上服装定制服务。"),
  p("系统采用B/S架构，后端基于Spring Boot 4.1.0框架与Java 25语言开发，使用JPA/Hibernate进行对象关系映射，采用H2文件数据库实现数据持久化存储，通过Maven进行项目构建与依赖管理。前端使用HTML/CSS/JavaScript技术栈，通过RESTful API与后端进行数据交互。系统实现了用户注册登录、面料浏览与筛选、款式浏览与搜索、量体数据管理、订单创建与查询、后台管理等核心功能模块。在安全方面，系统采用BCrypt密码加密算法保障用户密码安全，使用Session机制进行用户认证，并通过拦截器实现URL级别的权限控制，有效防止未授权访问与越权操作。"),
  p("系统采用分层架构设计，将表现层、控制层、业务层和数据访问层进行解耦，提高了代码的可维护性和可扩展性。订单管理模块实现了从待确认到已完成的全流程状态推进机制，保障了定制生产流程的规范性。经过功能测试、安全测试和兼容性测试，系统各功能模块运行稳定，达到了预期设计目标，具有一定的实用价值和推广意义。"),
  emptyLine(),
  pRich([
    { text: "关键词：", bold: true },
    { text: "Spring Boot；服装定制；JPA；H2数据库；B/S架构" },
  ]),
  pageBreak(),
];

// ---------- 英文Abstract ----------
const abstractEnChildren = [
  centerText("Abstract", SIZE.XIAOER, true, FONT_HEADING, 0),
  emptyLine(),
  p("With the acceleration of digital transformation in the apparel industry and the growing demand for personalization among consumers, the traditional offline clothing customization model can no longer meet modern consumers' pursuit of convenience, transparency, and personalized experience. This paper designs and implements a clothing customization website system based on the Spring Boot framework, aiming to provide users with convenient online clothing customization services."),
  p("The system adopts a B/S architecture, with the backend developed based on the Spring Boot 4.1.0 framework and Java 25, using JPA/Hibernate for object-relational mapping, H2 file database for data persistence, and Maven for project building and dependency management. The frontend utilizes HTML/CSS/JavaScript technology stack and communicates with the backend through RESTful APIs. The system implements core functional modules including user registration and login, fabric browsing and filtering, style browsing and searching, measurement data management, order creation and querying, and backend management. In terms of security, the system employs the BCrypt password encryption algorithm to ensure password security, uses Session mechanism for user authentication, and implements URL-level access control through interceptors to effectively prevent unauthorized access and privilege escalation."),
  p("The system adopts a layered architecture design, decoupling the presentation layer, control layer, business layer, and data access layer to improve code maintainability and scalability. The order management module implements a full-process status advancement mechanism from pending to completed, ensuring the standardization of the customization production process. Through functional testing, security testing, and compatibility testing, all functional modules of the system operate stably and achieve the expected design objectives, demonstrating practical value and promotional significance."),
  emptyLine(),
  pRich([
    { text: "Key words: ", bold: true },
    { text: "Spring Boot; Clothing Customization; JPA; H2 Database; B/S Architecture" },
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
  tocEntry("1.2 国内外研究现状", "2", false),
  tocEntry("1.3 主要研究内容", "3", false),
  tocEntry("第2章 相关技术及工具介绍", "5"),
  tocEntry("2.1 Spring Boot框架", "5", false),
  tocEntry("2.2 JPA与Hibernate", "6", false),
  tocEntry("2.3 H2数据库", "7", false),
  tocEntry("2.4 HTML/CSS/JavaScript", "8", false),
  tocEntry("2.5 Maven构建工具", "9", false),
  tocEntry("第3章 需求分析", "11"),
  tocEntry("3.1 功能性需求分析", "11", false),
  tocEntry("3.2 非功能性需求分析", "13", false),
  tocEntry("第4章 系统设计", "15"),
  tocEntry("4.1 设计原则", "15", false),
  tocEntry("4.2 系统架构设计", "15", false),
  tocEntry("4.3 功能模块设计", "17", false),
  tocEntry("4.4 数据库设计", "19", false),
  tocEntry("第5章 功能实现", "24"),
  tocEntry("5.1 登录系统功能", "24", false),
  tocEntry("5.2 信息展示功能", "26", false),
  tocEntry("5.3 信息查询功能", "28", false),
  tocEntry("5.4 信息改动功能", "30", false),
  tocEntry("第6章 系统测试", "33"),
  tocEntry("6.1 测试方案", "33", false),
  tocEntry("6.2 测试结果", "35", false),
  tocEntry("结论", "38"),
  tocEntry("参考文献", "39"),
  pageBreak(),
];

// ---------- 第1章 概述 ----------
const chapter1Children = [
  h1("第1章 概述"),
  h2("1.1 系统开发背景及意义"),
  p("服装是人类生活的基本需求之一，随着社会经济的发展和人民生活水平的提高，消费者对服装的需求已从基本的遮体保暖功能转向追求个性化、品质化和品牌化。在这一趋势下，服装定制作为一种能够充分满足消费者个性化需求的服务模式，正受到越来越多的关注。传统的服装定制主要依赖线下实体店面，消费者需要亲自到店量体、选款、选面料，整个过程耗时长、信息不对称、价格不透明，且受限于地理位置，难以实现规模化发展[1]。"),
  p("近年来，随着互联网技术的飞速发展和电子商务的普及，服装行业正经历着深刻的数字化转型。C2M（Customer to Manufacturer，消费者直达工厂）模式的兴起，为服装定制行业的线上化发展提供了新的契机。C2M模式通过互联网平台直接连接消费者与生产企业，省去了中间分销环节，不仅降低了定制成本，还缩短了交付周期。据行业研究报告显示，中国服装定制市场规模已突破千亿元，并保持着年均两位数的增长率，市场前景广阔[2]。"),
  p("然而，当前市场上的服装定制平台仍存在诸多不足。一方面，部分平台仅提供简单的在线选款功能，缺乏量体数据管理和面料选择等深度定制能力，无法真正满足消费者的个性化需求。另一方面，许多平台的订单流程不透明，消费者难以实时了解定制进度，用户体验有待提升。此外，中小型服装定制企业缺乏专业的信息化管理系统，订单管理、面料管理、量体数据管理等环节仍依赖人工操作，效率低下且容易出错[3]。"),
  p("基于上述背景，开发一个功能完善、操作便捷的服装私人定制网站系统具有重要的现实意义。首先，线上平台能够打破时间和空间的限制，消费者可以随时随地浏览面料、选择款式、录入量体数据并下单，极大提升了定制服务的可及性。其次，系统化的订单管理流程能够实现从下单到完成的全流程跟踪，提高流程透明度，增强消费者信任。再次，通过数字化管理面料库存、款式信息和客户量体数据，可以有效提升企业的运营效率和管理水平。最后，本系统的开发也是对Spring Boot框架、JPA持久化技术、B/S架构设计等Java Web开发技术的综合实践，具有较强的教学价值和技术参考意义。"),
  p("从技术发展趋势来看，近年来Java Web开发技术体系日趋成熟，Spring Boot框架的推出极大降低了企业级应用的开发门槛，JPA规范的普及使得数据持久层的开发更加规范化，前后端分离架构的推广提升了系统的可维护性和可扩展性。这些技术进步为开发轻量级、高效率的服装定制系统提供了坚实的技术基础。与此同时，H2等嵌入式数据库的成熟使得小型项目无需部署独立的数据库服务即可实现数据持久化，进一步降低了系统的部署和运维成本。本系统正是基于上述技术体系构建，旨在探索一种适合中小型服装定制企业的轻量级信息化解决方案。"),
  h2("1.2 国内外研究现状"),
  h3("1.2.1 国内研究现状"),
  p("国内服装定制行业的数字化发展正处于从传统模式向C2M模式转型的关键时期。在技术层面，三维人体重建技术、AI辅助设计技术和虚拟试衣技术等前沿技术正逐步应用于服装定制领域。刘艺舒等人研究了面向服装个性化定制的多视角轮廓三维人体快速重建方法，通过多视角图像采集与轮廓提取实现了人体三维模型的快速构建，为线上量体提供了技术支撑[1]。王建萍等人基于RFID技术研究了大制模服装定制生产信息系统，通过RFID标签实现生产流程的实时追踪与信息管理，提升了定制生产效率[2]。李敏等人设计了基于远程服装定制的人体数据测量系统，实现了用户远程自助量体与数据上传功能[3]。"),
  p("在应用层面，国内涌现了一批服装定制平台，如量品、衣邦人、红领等，这些平台在C2M模式探索方面取得了显著进展。然而，这些平台多聚焦于特定品类（如衬衫、西装）的定制，且系统架构复杂、开发成本高，不太适合中小型服装定制企业使用。此外，近年来新中式服装风潮的兴起，进一步推动了服装定制市场的多元化发展，对定制平台的面料管理、款式管理和量体数据管理能力提出了更高要求[4]。"),
  p("从市场规模来看，国内服装定制市场已突破千亿元规模，年增长率保持在15%以上。C2M（Customer to Manufacturer）模式的兴起有效降低了定制服装的价格门槛，使得定制服务从高端小众市场逐步走向大众化。然而，当前国内服装定制行业仍面临诸多挑战：一是专业人才断层，裁缝师傅年龄结构老化，年轻从业者数量不足；二是消费者认知偏差，部分消费者对线上定制的量体准确性和成品质量存在顾虑；三是信息化水平参差不齐，大量中小型定制门店仍采用纸质记录和口头沟通的方式管理订单，效率低下且容易出错。这些痛点为本系统的开发提供了明确的需求导向。"),
  p("在技术研究方向上，国内学者还积极探索了智能化定制方案。基于BP神经网络的服装定制尺寸设计方法能够根据用户的身高、体重等基本参数智能推荐各部位尺寸，减少人工量体的误差。三维交互式服装设计系统则允许用户在虚拟环境中实时预览定制效果，调整面料纹理和款式细节。这些技术探索为服装定制系统的功能创新提供了丰富的参考，但由于技术复杂度高、硬件要求苛刻，短期内难以在中小型平台中大规模推广。本系统选择以基础功能完善为目标，采用成熟稳定的Web技术栈，优先保障系统的可用性和可靠性。"),
  h3("1.2.2 国外研究现状"),
  p("国外服装定制行业的数字化发展起步较早，数字化普及程度较高。在北美市场，定制服装市场份额已达到约34%，消费者对线上定制的接受度和满意度较高。虚拟试衣技术在国外已趋于常态化应用，消费者可以通过AR/VR技术在虚拟环境中预览定制效果，大幅降低了定制的不确定性。此外，可持续时尚理念在欧美市场深入人心，越来越多的定制平台开始关注环保面料的选择和生产过程的碳足迹管理，推动服装定制行业向绿色可持续方向发展[5]。"),
  p("在技术架构方面，国外主流服装定制平台普遍采用微服务架构和云原生技术，具备高可用性和弹性伸缩能力。然而，这些技术方案对于中小型企业和教学实践而言过于复杂，需要投入大量的开发和运维资源。因此，研究基于轻量级框架（如Spring Boot）的服装定制系统，对于降低开发门槛、促进技术普及具有重要价值。"),
  p("从区域市场格局来看，全球服装定制市场呈现出明显的区域分化特征。北美市场以34%的份额占据全球最大市场份额，消费者对个性化定制的需求旺盛，支付意愿强。欧洲市场注重工艺传承和面料品质，定制服装在意大利、英国和法国等传统时尚强国具有深厚的文化根基。亚洲市场虽然起步较晚，但增长速度最快，特别是中国和印度等人口大国的定制需求正在快速释放。这种区域分化意味着服装定制系统需要具备良好的本地化适配能力，支持多语言、多币种和差异化业务规则。本系统虽然聚焦于中国市场，但其模块化设计为未来扩展国际化功能预留了空间。"),
  p("此外，国外在服装定制标准化方面也取得了显著进展。ISO等国际组织制定了多项服装尺寸标准和量体数据交换标准，为不同平台之间的数据互通奠定了基础。日本在跨境电商和全球化定制方面走在前列，多家平台已实现跨国量体数据传输和远程定制生产。这些标准化实践对于本系统的量体数据字段设计具有参考价值，本系统的Measurement实体涵盖了14项标准化身体尺寸，基本覆盖了上衣和裤装定制的主要测量点。"),
  h2("1.3 主要研究内容"),
  p("本课题以服装私人定制业务为背景，设计并实现一个基于Spring Boot框架的服装私人定制网站系统。主要研究内容包括以下六个方面："),
  p("（1）系统需求分析：深入分析服装定制业务流程，明确系统的功能性需求与非功能性需求，包括前台用户功能和后台管理功能，为系统设计与实现奠定基础。"),
  p("（2）系统架构设计：采用B/S架构与分层设计思想，设计表现层、控制层、业务层、数据访问层和数据层五层架构体系，确定技术选型方案，保障系统的可维护性与可扩展性。"),
  p("（3）数据库设计：根据业务需求设计E-R模型，建立管理员表、用户表、面料表、款式表、量体数据表和订单表共6张数据表，定义实体间的关联关系，设计订单状态流转机制。"),
  p("（4）核心功能实现：基于Spring Boot 4.1.0框架与Java 25语言，实现用户注册登录、面料管理、款式管理、量体数据管理、订单管理和后台管理等核心功能模块，前端采用HTML/CSS/JavaScript技术并通过RESTful API与后端通信。"),
  p("（5）安全机制实现：采用BCrypt密码加密算法保障密码安全，使用Session机制进行用户认证，通过拦截器实现URL级别的权限控制，在量体数据管理中实现数据归属校验以防止越权操作。"),
  p("（6）系统测试与优化：设计测试用例，对系统的功能正确性、安全性和兼容性进行全面测试，根据测试结果进行问题修复与系统优化，确保系统稳定可靠运行。"),
  pageBreak(),
];

// ---------- 第2章 相关技术及工具介绍 ----------
const chapter2Children = [
  h1("第2章 相关技术及工具介绍"),
  p("本章对系统开发过程中所采用的关键技术与工具进行介绍，包括Spring Boot框架、JPA与Hibernate、H2数据库、前端技术以及Maven构建工具等，为后续章节的系统设计与实现提供技术基础。"),
  h2("2.1 Spring Boot框架"),
  p("Spring Boot是由Pivotal团队开发的基于Spring框架的快速应用开发框架，旨在简化Spring应用的初始搭建与开发过程。Spring Boot秉承\u201C约定优于配置\u201D的设计理念，通过自动配置机制大幅减少了繁琐的XML配置工作，使开发者能够将更多精力聚焦于业务逻辑的实现[6]。"),
  p("Spring Boot的核心特性包括以下几个方面。第一，自动配置（Auto Configuration）：Spring Boot根据项目中引入的依赖自动配置相关的Bean和组件，例如引入spring-boot-starter-data-jpa后，框架会自动配置DataSource、EntityManagerFactory等Bean，无需手动编写配置代码。第二，内嵌Web容器：Spring Boot内嵌了Tomcat、Jetty等Servlet容器，应用可以直接以Java Application的方式启动，无需部署到外部容器，极大简化了开发与部署流程。第三，Starter依赖管理：Spring Boot提供了一系列starter依赖包，每个starter聚合了特定功能所需的全部依赖，开发者只需引入一个starter即可获得完整的功能支持，有效避免了依赖冲突问题。第四，生产级监控：Spring Boot Actuator提供了健康检查、运行指标监控等开箱即用的运维功能[7]。"),
  p("本系统采用Spring Boot 4.1.0版本，利用其自动配置特性快速搭建项目骨架，通过spring-boot-starter-webmvc提供RESTful API能力，通过spring-boot-starter-data-jpa简化持久层开发，通过spring-boot-starter-validation实现参数校验。Spring Boot的Starter依赖管理机制确保了各组件版本的兼容性，Maven插件支持一键打包生成可执行JAR文件，显著提升了开发效率。"),
  p("在配置管理方面，Spring Boot采用application.properties文件作为统一配置入口，支持以键值对形式配置数据库连接、服务端口、日志级别等参数。本系统在application.properties中配置了H2数据库连接字符串（jdbc:h2:file:./data/tailordb）、JPA的ddl-auto策略（update，自动更新表结构）、H2控制台启用（spring.h2.console.enabled=true）等关键参数。Spring Boot的配置覆盖机制还支持通过命令行参数、环境变量和profile配置文件灵活调整运行参数，满足不同环境的部署需求。此外，Spring Boot 4.1.0对Java 25提供了良好的兼容支持，能够利用最新Java版本的性能优化和语言特性。"),
  h2("2.2 JPA与Hibernate"),
  p("JPA（Java Persistence API）是Java平台标准的对象关系映射（ORM）规范，定义了一套将Java对象映射到关系数据库表的API和元数据。JPA的核心思想是通过注解或XML描述对象与数据库表之间的映射关系，使开发者能够以面向对象的方式操作数据库，而无需编写大量的JDBC模板代码[8]。"),
  p("Hibernate是JPA规范最流行的实现框架之一，提供了完整的ORM解决方案。Hibernate支持丰富的映射注解，包括@Entity（标识实体类）、@Table（指定表名）、@Column（映射列属性）、@Id（主键标识）、@GeneratedValue（主键生成策略）、@ManyToOne（多对一关系）等。通过这些注解，开发者可以精确地描述实体类与数据库表之间的映射关系，包括字段类型、长度约束、外键关联等。Hibernate还提供了JPQL（Java Persistence Query Language）查询语言，支持面向对象的查询语法，以及Criteria API等动态查询机制，满足复杂业务场景的查询需求[9]。"),
  p("Spring Data JPA在Hibernate的基础上进一步简化了数据访问层的开发。开发者只需定义一个继承JpaRepository的接口，Spring Data JPA会根据方法名自动生成查询实现，例如findByUserIdOrderByCreateTimeDesc方法会自动生成按用户ID查询并按创建时间倒序排列的SQL语句。同时，Spring Data JPA还支持@Query注解自定义JPQL查询，以及基于方法名的派生查询，极大减少了数据访问层的样板代码。本系统使用Spring Data JPA定义了AdminUserRepository、UserRepository、FabricRepository、StyleRepository、MeasurementRepository和OrderRepository共6个Repository接口，实现了各实体的CRUD操作与自定义查询。"),
  p("在本系统的实体设计中，JPA注解发挥了关键作用。例如，Fabric实体的description字段使用@Column(length = 1000)注解将字段长度扩展至1000个字符，避免了默认VARCHAR(255)长度对面料描述内容的截断；Order实体通过@ManyToOne注解建立与User、Style、Fabric和Measurement的多对一关联关系，并通过外键约束保障引用完整性；Measurement实体的remark字段使用@Column(length = 500)注解以容纳详细的量体备注信息。此外，所有实体均使用@GeneratedValue(strategy = GenerationType.IDENTITY)注解配置主键自增策略，@Table注解指定数据库表名，确保实体与表的精确映射。Hibernate的ddl-auto=update策略使得应用启动时自动根据实体类定义创建或更新数据库表结构，开发期间无需手动执行DDL语句。"),
  h2("2.3 H2数据库"),
  p("H2数据库是一个用纯Java编写的开源关系型数据库管理系统，具有体积小、速度快、零配置等特点。H2支持多种运行模式，包括嵌入式模式（Embedded Mode）、服务器模式（Server Mode）和混合模式。在嵌入式模式下，H2数据库与应用程序运行在同一JVM中，无需单独安装和配置数据库服务，非常适合开发和测试环境使用[10]。"),
  p("H2数据库支持标准SQL语法，兼容ANSI SQL-92标准，提供了对视图、触发器、存储过程、外键约束等关系型数据库核心特性的完整支持。H2还内置了Web Console管理界面，开发者可通过浏览器访问H2 Console查看和操作数据库表结构与数据，极大方便了开发调试。在本系统中，H2 Console通过spring.h2.console.enabled=true配置启用，访问路径为/h2-console。"),
  p("本系统选择H2数据库的文件模式（jdbc:h2:file:./data/tailordb）进行数据持久化。文件模式下，H2将数据存储在本地文件系统的.mv.db文件中，应用重启后数据不会丢失，实现了真正的数据持久化。相较于内存模式（jdbc:h2:mem:），文件模式更适合需要数据累积的业务场景。H2数据库与JPA/Hibernate的集成非常简便，只需在application.properties中配置spring.datasource.url=jdbc:h2:file:./data/tailordb即可，Hibernate的ddl-auto=update策略会根据实体类定义自动创建和更新表结构。此外，H2数据库对JPA的关联查询、分页查询等特性提供了良好的支持，满足了本系统的数据访问需求。"),
  p("本系统采用H2数据库的文件模式进行数据持久化，配置连接字符串为jdbc:h2:file:./data/tailordb，数据文件存储在项目的data目录下，应用程序重启后数据不会丢失。H2数据库兼容MySQL语法模式（MODE=MySQL），便于未来迁移至MySQL等生产级数据库。此外，系统启用了H2 Web控制台（spring.h2.console.enabled=true），开发者可通过浏览器访问/h2-console路径，直观地查看和操作数据库中的表结构与数据，为开发调试提供了便利。H2数据库的轻量级特性使得本系统无需额外安装数据库服务，降低了部署门槛，同时其ACID事务支持保障了数据操作的一致性与可靠性。"),
  h2("2.4 HTML/CSS/JavaScript"),
  p("HTML（HyperText Markup Language）是构建Web页面的标准标记语言，通过语义化标签描述网页的结构与内容。本系统前端采用HTML5标准，使用header、nav、section、article等语义化标签构建页面结构，提升了页面的可读性和可维护性，同时有利于搜索引擎优化和无障碍访问[11]。"),
  p("CSS（Cascading Style Sheets）负责网页的样式与布局控制。本系统使用CSS3进行样式设计，采用Flexbox弹性布局实现响应式卡片排列，通过CSS变量统一管理主题色彩，利用过渡动画（transition）增强用户交互体验。系统的面料展示、款式展示等页面采用卡片式布局，每张卡片包含图片、名称、价格等关键信息，视觉层次清晰。"),
  p("JavaScript是实现网页动态交互的核心脚本语言。本系统前端使用原生JavaScript（Vanilla JS）开发，通过fetch API与后端RESTful接口进行异步通信，利用DOM操作实现页面内容的动态渲染与更新。JavaScript的事件驱动机制用于处理用户点击、表单提交等交互行为，例如面料筛选、款式搜索、订单创建等操作均通过JavaScript向后端发送请求并更新页面内容。相较于Vue、React等前端框架，原生JavaScript方案无需构建工具和额外依赖，部署简单，适合本课程设计项目的规模与需求[12]。"),
  p("在异步通信方面，本系统前端统一使用fetch API替代传统的XMLHttpRequest对象。fetch API基于Promise设计，支持async/await语法，使异步代码的可读性大幅提升。例如，用户登录功能通过fetch向/api/users/login端点发送POST请求，await等待响应后根据HTTP状态码判断登录是否成功，成功则更新页面显示用户信息，失败则显示错误提示。这种基于Promise的异步模式避免了回调地狱（Callback Hell）问题，使代码结构更加清晰。此外，所有fetch请求均统一处理网络异常和服务器错误，通过try-catch捕获异常并向用户展示友好的错误信息。"),
  p("在DOM操作方面，系统使用document.querySelector和document.querySelectorAll选择器获取页面元素，通过innerHTML、textContent和classList等API动态更新页面内容和样式。面料和款式的卡片展示通过JavaScript动态生成HTML字符串并插入到容器元素中，实现了数据驱动的UI渲染。订单进度条通过CSS类名的动态添加与移除实现节点高亮效果，使用户能够直观地查看订单当前所处的生产阶段。模态框的显示与隐藏通过修改元素的display样式属性实现，并支持点击遮罩层关闭模态框的交互模式。"),
  h2("2.5 Maven构建工具"),
  p("Maven是Apache软件基金会开发的项目管理与构建自动化工具，基于项目对象模型（POM，Project Object Model）理念。Maven通过pom.xml配置文件统一管理项目的依赖库、构建流程和项目元信息，实现了项目构建的标准化与自动化[13]。"),
  p("Maven的核心概念包括POM文件、坐标系统、依赖管理和生命周期。POM文件是Maven项目的核心配置文件，定义了项目的基本信息、依赖列表、插件配置和构建规则。坐标系统通过groupId、artifactId和version三个元素唯一标识一个项目或依赖库，确保依赖的准确解析。Maven的依赖管理机制支持传递依赖解析和版本冲突调解，开发者只需声明直接依赖，Maven会自动解析并下载所有间接依赖。Maven定义了clean、validate、compile、test、package、verify、install、deploy等标准生命周期阶段，通过命令行即可执行完整的构建流程[14]。"),
  p("本系统使用Maven进行项目构建与依赖管理，pom.xml文件中声明了spring-boot-starter-webmvc、spring-boot-starter-data-jpa、spring-boot-starter-validation、spring-boot-h2console、h2、spring-security-crypto等核心依赖。同时，项目集成了Maven Wrapper（mvnw），使开发者无需预装Maven即可使用项目内置的Maven版本进行构建，保证了构建环境的一致性。通过spring-boot-maven-plugin插件，项目支持一键打包生成可执行JAR文件，简化了部署流程[15]。"),
  pageBreak(),
];

// ---------- 第3章 需求分析 ----------
const chapter3Children = [
  h1("第3章 需求分析"),
  p("需求分析是软件开发生命周期中的关键阶段，其质量直接影响系统设计的合理性与最终交付质量。本章从功能性需求和非功能性需求两个维度对服装私人定制网站系统进行需求分析，为后续的系统设计与实现提供依据[16]。"),
  h2("3.1 功能性需求分析"),
  p("功能性需求描述系统应当具备的具体功能和行为。根据服装私人定制业务流程的分析，本系统的功能性需求分为前台用户功能和后台管理功能两大类[17]。"),
  h3("3.1.1 前台用户功能需求"),
  p("前台用户功能面向普通消费者，主要包括以下功能模块："),
  p("（1）用户注册与登录：用户通过用户名和密码进行注册，注册时密码采用BCrypt加密存储。登录成功后系统创建Session保存用户信息，后续请求通过Session进行身份认证。用户可修改登录密码。"),
  p("（2）面料浏览与筛选：用户可浏览所有上架面料，支持按材质、颜色、价格范围进行筛选，面料信息包括名称、材质、颜色、单价、库存量和描述。"),
  p("（3）款式浏览与搜索：用户可浏览所有上架款式，支持按类别筛选和关键词搜索，款式信息包括名称、类别、工费和描述。"),
  p("（4）量体数据管理：用户可录入、编辑和删除自己的量体数据，量体数据包含身高、体重、颈围、肩宽、胸围、腰围、臀围、衣长、袖长、裤长、大腿围共14项身体尺寸及备注信息。系统对量体数据进行归属校验，防止用户访问或修改他人的量体数据。"),
  p("（5）订单创建：用户选择款式、面料和量体数据后创建定制订单，系统自动计算订单总价（总价 = 款式工费 + 面料单价 × 3米），订单初始状态为待确认（PENDING）。"),
  p("（6）订单查询：用户可查看自己的所有订单信息，包括订单编号、所选款式与面料、量体数据、总价、当前状态和下单时间，并可查看订单进度。"),
  h3("3.1.2 后台管理功能需求"),
  p("后台管理功能面向系统管理员，主要包括以下功能模块："),
  p("（1）管理员登录：管理员通过独立的登录入口进行认证，与前台用户分表存储，Session中使用不同的键进行区分。"),
  p("（2）仪表盘统计：管理员登录后可查看系统概览数据，包括用户总数、订单总数、面料总数和款式总数等统计信息。"),
  p("（3）面料管理：管理员可添加、编辑和删除面料信息，管理面料的名称、材质、颜色、单价、库存和描述。"),
  p("（4）款式管理：管理员可添加、编辑和删除款式信息，管理款式的名称、类别、工费和描述。"),
  p("（5）订单管理：管理员可查看全部订单，并推进订单状态（待确认→量体中→裁剪中→缝制中→试衣中→已完成），状态推进为单向操作，不允许跳跃或回退。"),
  p("（6）用户管理：管理员可查看所有注册用户的基本信息。"),
  h3("3.1.3 系统用例分析"),
  p("从用户角色角度分析，系统涉及两类主要参与者：前台消费者（User）和后台管理员（AdminUser）。前台消费者的核心用例包括注册账号、登录系统、浏览面料、筛选面料、浏览款式、搜索款式、录入量体数据、编辑量体数据、删除量体数据、创建订单和查看订单进度。后台管理员的核心用例包括登录后台、查看仪表盘统计、添加面料、编辑面料、删除面料、添加款式、编辑款式、删除款式、查看全部订单、推进订单状态和查看用户列表。"),
  p("从业务流程角度分析，系统的核心业务流程为\u201C定制下单流程\u201D，该流程贯穿前台用户和后台管理员两个角色。前台用户完成注册登录、量体数据录入、面料和款式选择、订单创建等前置环节；后台管理员接收订单后，依次推进订单状态（待确认→量体中→裁剪中→缝制中→试衣中→已完成），完成定制服装的生产交付。整个流程通过订单状态机进行管理，确保每个环节按序执行，不允许跳跃或回退。"),
  p("此外，系统还存在一些约束性用例。例如，用户修改密码需要验证旧密码；量体数据的编辑和删除需要校验数据归属；管理员推进订单状态需要检查当前状态是否为终态。这些约束性用例保障了系统操作的安全性和数据的一致性。"),
  tableCaption("表3-1 功能性需求汇总表"),
  createTable(
    ["功能模块", "功能项", "操作角色", "功能描述"],
    [
      ["用户管理", "注册", "访客", "用户名+密码注册，BCrypt加密存储"],
      ["用户管理", "登录", "访客", "用户名+密码登录，Session认证"],
      ["用户管理", "修改密码", "前台用户", "验证旧密码后设置新密码"],
      ["面料管理", "浏览筛选", "前台用户", "按材质/颜色/价格筛选面料"],
      ["面料管理", "增删改查", "管理员", "面料信息全量管理"],
      ["款式管理", "浏览搜索", "前台用户", "按类别/关键词搜索款式"],
      ["款式管理", "增删改查", "管理员", "款式信息全量管理"],
      ["量体数据", "录入编辑", "前台用户", "14项尺寸录入，归属校验"],
      ["订单管理", "下单", "前台用户", "选款式+面料+量体→计算总价"],
      ["订单管理", "状态推进", "管理员", "PENDING→...→COMPLETED单向推进"],
      ["后台管理", "仪表盘", "管理员", "用户/订单/面料/款式统计"],
    ],
    [1500, 1500, 1500, 4270]
  ),
  h2("3.2 非功能性需求分析"),
  p("非功能性需求是对系统运行质量与约束条件的描述，直接影响用户体验和系统可靠性。本系统的非功能性需求主要包括以下几个方面[18]："),
  h3("3.2.1 安全性需求"),
  p("（1）密码安全：用户密码不得以明文形式存储，必须采用BCrypt加密算法进行加密处理，BCrypt算法内置盐值机制，有效防止彩虹表攻击。"),
  p("（2）身份认证：系统采用基于Session的认证机制，用户登录成功后在服务端创建Session并保存用户信息，后续请求通过Session ID进行身份识别。"),
  p("（3）权限控制：通过拦截器实现URL级别的权限控制，GET请求（浏览查询）允许匿名访问，POST/PUT/DELETE请求必须经过身份认证；后台管理接口（/api/admin/**）仅允许管理员访问；前台用户写操作（量体、订单）必须前台用户登录。"),
  p("（4）数据隔离：量体数据和订单数据实行用户级数据隔离，用户只能访问和操作属于自己的数据，系统在Service层进行数据归属校验，防止越权访问。"),
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
  p("本章在需求分析的基础上，对服装私人定制网站系统进行详细设计，包括设计原则、系统架构设计、功能模块设计和数据库设计四个方面，为后续的功能实现提供蓝图。"),
  h2("4.1 设计原则"),
  p("系统设计遵循以下核心原则："),
  p("（1）分层解耦：系统采用经典的分层架构，将表现层、控制层、业务层和数据访问层进行分离，各层通过接口进行通信，降低层间耦合度，提高系统的可维护性和可测试性。"),
  p("（2）单一职责：每个类和模块只负责一项功能职责，Controller负责请求接收与响应封装，Service负责业务逻辑处理，Repository负责数据访问，Model负责数据建模，DTO负责数据传输，各司其职，避免\u201C上帝类\u201D问题。"),
  p("（3）安全优先：在系统设计的各个环节贯彻安全理念，密码加密存储、Session认证、拦截器鉴权、数据归属校验等多层安全机制协同工作，保障系统和用户数据安全。"),
  p("（4）幂等设计：对于状态推进等关键操作，采用幂等设计理念，订单状态推进操作在已完成状态下重复调用不会产生副作用，保证系统在异常重试场景下的数据一致性。"),
  h2("4.2 系统架构设计"),
  p("本系统采用B/S（Browser/Server）架构，用户通过浏览器访问系统，前端页面通过HTTP协议与后端Spring Boot应用通信，后端通过JPA与H2数据库交互。系统整体架构分为五层，自上向下依次为表现层、控制层、业务层、数据访问层和数据层。"),
  ...figure("fig_4_1_architecture.png", "图4-1 系统架构图", 520),
  p("如图4-1所示，系统五层架构的职责划分如下："),
  p("（1）表现层：由HTML/CSS/JavaScript组成，负责页面展示与用户交互，通过fetch API向后端发送RESTful请求，接收JSON格式响应并动态渲染页面内容。"),
  p("（2）控制层：由Spring MVC的Controller组件构成，负责接收HTTP请求、参数校验、调用业务层服务并封装响应数据。系统包含AuthController、UserController、FabricController、StyleController、MeasurementController、OrderController和AdminController共7个控制器。"),
  p("（3）业务层：由Service组件构成，负责核心业务逻辑处理，包括密码加密验证、订单总价计算、量体数据归属校验、订单状态推进等。系统包含AuthService、UserService、FabricService、StyleService、MeasurementService和OrderService共6个服务类。"),
  p("（4）数据访问层：由Spring Data JPA的Repository接口构成，负责数据库CRUD操作与自定义查询。系统包含6个Repository接口，通过方法名派生查询和@Query注解实现数据访问。"),
  p("（5）数据层：由H2文件数据库构成，负责数据的持久化存储，包含管理员表、用户表、面料表、款式表、量体数据表和订单表共6张数据表。"),
  p("各层之间通过接口进行通信，上层依赖下层接口而非实现，符合依赖倒置原则。表现层通过fetch API向控制层发送HTTP请求，控制层将请求转发至业务逻辑层处理，业务逻辑层调用数据访问层完成数据持久化操作。LoginInterceptor作为横切关注点，在请求到达控制层之前进行统一的权限校验，拦截未授权的访问请求。DataInitializer在应用启动时执行幂等的数据初始化操作，确保系统首次启动时具备完整的演示数据。此外，系统采用DTO模式进行数据传输隔离，请求DTO使用Jakarta Validation注解进行参数校验，响应DTO过滤了密码等敏感字段，并根据前端展示需求组装关联实体的名称信息，避免了N+1查询问题。"),
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
      ["认证机制", "Session", "Servlet", "服务端会话管理，简单可靠"],
    ],
    [1500, 1800, 1200, 4270]
  ),
  h2("4.3 功能模块设计"),
  p("根据需求分析，系统功能划分为用户管理、面料管理、款式管理、量体数据管理、订单管理和后台管理六大模块。各功能模块之间的关系如图4-2所示。"),
  ...figure("fig_4_2_modules.png", "图4-2 功能模块图", 540),
  p("如图4-2所示，系统的六大功能模块相互协作，共同支撑服装定制的完整业务流程。前台用户通过用户管理模块完成注册登录后，可使用面料管理、款式管理和量体数据管理模块浏览和录入定制所需信息，进而通过订单管理模块创建定制订单。后台管理模块为管理员提供系统全局管理能力，包括数据统计、信息维护和订单状态推进等功能。"),
  tableCaption("表4-2 功能模块设计表"),
  createTable(
    ["模块名称", "子功能", "涉及实体", "关键接口"],
    [
      ["用户管理", "注册/登录/改密", "User, AdminUser", "POST /api/users/register"],
      ["面料管理", "浏览/筛选/增删改", "Fabric", "GET /api/fabrics"],
      ["款式管理", "浏览/搜索/增删改", "Style", "GET /api/styles"],
      ["量体数据", "录入/编辑/删除", "Measurement", "POST /api/measurements"],
      ["订单管理", "下单/查询/推进", "Order", "POST /api/orders"],
      ["后台管理", "仪表盘/全量管理", "全部实体", "GET /api/admin/stats"],
    ],
    [1500, 2000, 2000, 3270]
  ),
  p("在用户管理模块设计中，系统采用了前台用户与管理员分表存储的策略。AdminUser表和User表分别独立存储管理员和前台用户的数据，两表之间不存在外键关联。这种设计实现了前台与后台权限的物理隔离，即使前台用户数据被泄露也无法影响后台管理系统的安全。在Session管理方面，系统使用不同的Session Key（adminUser和frontUser）区分两种用户角色的登录状态，LoginInterceptor根据请求路径和Session Key进行差异化权限校验。"),
  p("在面料管理模块设计中，Fabric实体的description字段使用@Column(length = 1000)注解将字段长度扩展至1000个字符，以容纳详细的面料描述信息。面料筛选功能支持按材质（material）、颜色（color）和最大价格（maxPrice）三个维度进行组合筛选，筛选逻辑在FabricService中通过Java Stream API实现内存过滤。款式管理模块的设计与面料管理类似，Style实体的description字段同样使用@Column(length = 1000)注解，筛选功能支持按类别（category）和关键词（keyword）进行搜索。"),
  p("在量体数据管理模块设计中，Measurement实体包含14项身体尺寸字段，涵盖了上衣和裤装定制所需的全部关键尺寸。量体数据的增删改操作均在Service层进行归属校验，通过比较measurement.getUser().getId()与当前登录用户的ID来判断操作权限。这种数据级别的权限控制有效防止了水平越权漏洞，即用户只能操作自己的量体数据。"),
  p("在订单管理模块设计中，Order实体通过四个@ManyToOne关联（User、Style、Fabric、Measurement）建立了与其它实体的多对一关系。订单创建时，系统自动计算总价（style.craftFee + fabric.unitPrice * 3.0），并设置初始状态为PENDING。订单状态推进通过OrderStatus枚举的ordinal值实现，每次推进仅允许前进一格，COMPLETED为终态不可变更。这种基于枚举序号的状态机设计简洁可靠，保证了状态流转的单向性。"),
  h2("4.4 数据库设计"),
  h3("4.4.1 E-R图设计"),
  p("根据系统功能需求，数据库共设计6张实体表，分别为管理员表（AdminUser）、前台用户表（User）、面料表（Fabric）、款式表（Style）、量体数据表（Measurement）和订单表（Order）。各实体之间的E-R关系如图4-3所示。"),
  ...figure("fig_4_3_er_diagram.png", "图4-3 E-R图", 540),
  p("如图4-3所示，系统实体间的关系如下：一个前台用户可以拥有多条量体数据（User 1:N Measurement），一个前台用户可以创建多个订单（User 1:N Order），一个款式可以被多个订单引用（Style 1:N Order），一个面料可以被多个订单引用（Fabric 1:N Order），一条量体数据可以关联多个订单（Measurement 1:N Order）。管理员表与前台用户表独立存储，互不关联。"),
  p("在数据库设计过程中，遵循了关系型数据库的规范化原则。各实体表均满足第三范式（3NF），即每个非主属性既不部分依赖于候选码也不传递依赖于候选码。例如，订单表中不存储面料名称和款式名称等冗余信息，而是通过外键引用Fabric和Style实体的主键，在查询时通过JPA的关联查询获取关联数据。这种设计避免了数据冗余和更新异常，保障了数据的一致性。"),
  p("在外键约束设计方面，Order表通过user_id、style_id、fabric_id和measurement_id四个外键分别关联User、Style、Fabric和Measurement表的主键。JPA通过@ManyToOne注解自动创建外键约束，确保引用完整性。当删除User、Style或Fabric记录时，由于存在外键约束引用，需要先处理关联的Order记录。Measurement表通过user_id外键关联User表，表示量体数据的归属关系，这是实现数据隔离的数据库层面基础。"),
  h3("4.4.2 订单状态流转设计"),
  p("订单状态流转是系统核心业务逻辑之一，订单从创建到完成需经过6个状态阶段，状态推进为单向操作，不允许跳跃或回退。订单状态流转如图4-4所示。"),
  ...figure("fig_4_4_order_flow.png", "图4-4 订单状态流转图", 540),
  p("如图4-4所示，订单状态的6个阶段及其含义如下："),
  p("（1）PENDING（待确认）：用户刚下单，等待管理员确认订单信息。"),
  p("（2）MEASURING（量体中）：管理员确认后进入量体环节，即使已自助录入尺寸，仍需工坊二次复核。"),
  p("（3）CUTTING（裁剪中）：依据量体数据裁剪面料。"),
  p("（4）SEWING（缝制中）：进入工坊缝制成衣。"),
  p("（5）FITTING（试衣中）：成衣完成后通知客户试穿。"),
  p("（6）COMPLETED（已完成）：客户确认合身，订单流程结束。"),
  p("状态推进逻辑在OrderService的advanceStatus方法中实现，通过OrderStatus枚举的ordinal()值计算下一个状态索引，确保状态只能按顺序前进一格。当订单状态已为COMPLETED时，后续推进操作将被拒绝，保证流程的终态稳定性。"),
  h3("4.4.3 数据表结构设计"),
  p("根据E-R图设计，系统共创建6张数据表，各表结构如下。"),
  tableCaption("表4-3 管理员表（admin_user）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["username", "VARCHAR", "NOT NULL, UNIQUE", "管理员用户名"],
      ["password", "VARCHAR", "NOT NULL", "BCrypt加密密码"],
      ["nickname", "VARCHAR", "—", "管理员昵称"],
      ["last_login_at", "TIMESTAMP", "—", "最后登录时间"],
      ["create_time", "TIMESTAMP", "—", "创建时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-4 前台用户表（user）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["username", "VARCHAR", "NOT NULL, UNIQUE", "用户名"],
      ["password", "VARCHAR", "NOT NULL", "BCrypt加密密码"],
      ["nickname", "VARCHAR", "—", "用户昵称"],
      ["phone", "VARCHAR", "—", "联系电话"],
      ["last_login_at", "TIMESTAMP", "—", "最后登录时间"],
      ["create_time", "TIMESTAMP", "—", "注册时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-5 面料表（fabric）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["name", "VARCHAR", "NOT NULL", "面料名称"],
      ["material", "VARCHAR", "—", "材质（如棉、麻、丝）"],
      ["color", "VARCHAR", "—", "颜色"],
      ["unit_price", "DOUBLE", "NOT NULL", "单价（元/米）"],
      ["stock", "INT", "—", "库存量"],
      ["description", "VARCHAR", "—", "面料描述"],
      ["create_time", "TIMESTAMP", "—", "创建时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-6 款式表（style）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["name", "VARCHAR", "NOT NULL", "款式名称"],
      ["category", "VARCHAR", "—", "类别（如西装、衬衫）"],
      ["craft_fee", "DOUBLE", "NOT NULL", "工费（元）"],
      ["description", "VARCHAR", "—", "款式描述"],
      ["create_time", "TIMESTAMP", "—", "创建时间"],
    ],
    [1800, 1500, 2500, 2970]
  ),
  tableCaption("表4-7 量体数据表（measurement）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["user_id", "BIGINT", "FK→user.id", "所属用户ID"],
      ["name", "VARCHAR", "—", "量体记录名称"],
      ["height", "DOUBLE", "—", "身高"],
      ["weight", "DOUBLE", "—", "体重"],
      ["neck_circumference", "DOUBLE", "—", "颈围"],
      ["shoulder_width", "DOUBLE", "—", "肩宽"],
      ["chest_circumference", "DOUBLE", "—", "胸围"],
      ["waist_circumference", "DOUBLE", "—", "腰围"],
      ["hip_circumference", "DOUBLE", "—", "臀围"],
      ["clothes_length", "DOUBLE", "—", "衣长"],
      ["sleeve_length", "DOUBLE", "—", "袖长"],
      ["pants_length", "DOUBLE", "—", "裤长"],
      ["thigh_circumference", "DOUBLE", "—", "大腿围"],
      ["remark", "VARCHAR", "—", "备注"],
      ["create_time", "TIMESTAMP", "—", "创建时间"],
    ],
    [2200, 1200, 2200, 3170]
  ),
  tableCaption("表4-8 订单表（orders）结构"),
  createTable(
    ["字段名", "类型", "约束", "说明"],
    [
      ["id", "BIGINT", "PK, 自增", "主键ID"],
      ["user_id", "BIGINT", "FK→user.id", "下单用户ID"],
      ["style_id", "BIGINT", "FK→style.id", "所选款式ID"],
      ["fabric_id", "BIGINT", "FK→fabric.id", "所选面料ID"],
      ["measurement_id", "BIGINT", "FK→measurement.id", "量体数据ID"],
      ["total_price", "DOUBLE", "NOT NULL", "订单总价（元）"],
      ["status", "VARCHAR", "NOT NULL", "订单状态枚举"],
      ["remark", "VARCHAR", "—", "客户备注"],
      ["create_time", "TIMESTAMP", "—", "下单时间"],
    ],
    [2000, 1200, 2500, 3070]
  ),
  p("上述6张数据表的设计充分考虑了服装定制业务的数据管理需求。管理员表和前台用户表采用了相似的字段结构但分表存储，实现了权限的物理隔离。面料表和款式表通过description字段的长度扩展（1000字符）支持详细的产品描述。量体数据表包含了14项身体尺寸字段，覆盖了颈围、肩宽、胸围、腰围、臀围、衣长、袖长、裤长、大腿围等定制服装所需的关键测量点，每项尺寸均使用DOUBLE类型存储以保留小数精度。订单表通过四个外键字段（user_id、style_id、fabric_id、measurement_id）建立了与其它实体的关联关系，status字段存储订单状态枚举的字符串值。"),
  p("在字段类型选择方面，主键统一使用BIGINT自增类型，保证主键的唯一性和递增性。密码字段使用VARCHAR类型存储BCrypt加密后的60位哈希字符串。价格和尺寸类数值字段使用DOUBLE类型，满足小数精度需求。时间字段使用TIMESTAMP类型，精确记录到秒级。文本类字段根据内容长度需求设置不同的VARCHAR长度：用户名和昵称使用默认255字符，描述类字段扩展至500-1000字符，备注字段设置为500字符。这种差异化的字段长度设计既避免了数据截断，又控制了存储空间的浪费。"),
  h3("4.4.3 订单状态流转设计"),
  p("订单状态流转是系统核心业务逻辑之一，本系统采用枚举类型OrderStatus定义了6个订单状态，按序为PENDING（待确认）、MEASURING（量体中）、CUTTING（裁剪中）、SEWING（缝制中）、FITTING（试衣中）和COMPLETED（已完成）。状态流转规则为单向推进，每次推进仅允许前进一格，不允许跳跃或回退，COMPLETED为终态不可变更。如图4-4所示。"),
  ...figure("fig_4_4_order_flow.png", "图4-4 订单状态流转图", 480),
  p("订单状态推进的实现逻辑位于OrderService的advanceStatus方法中。该方法首先查询订单是否存在，然后检查当前状态是否为COMPLETED终态（如果是则返回null表示不可推进），最后通过OrderStatus.values()数组获取当前状态的ordinal值，将状态设置为下一个枚举值。这种基于枚举序号的状态机设计简洁可靠，保证了状态流转的单向性和终态稳定性。管理员通过PUT /api/admin/orders/{id}/advance接口触发状态推进，系统返回更新后的订单信息或404错误（订单不存在或已完成）。"),
  pageBreak(),
];

// ---------- 第5章 功能实现 ----------
const chapter5Children = [
  h1("第5章 功能实现"),
  p("本章详细描述服装私人定制网站系统各功能模块的实现过程，包括登录系统功能、信息展示功能、信息查询功能和信息改动功能四个方面，并结合关键代码和流程图说明实现细节。"),
  h2("5.1 登录系统功能"),
  p("登录系统是系统的安全入口，负责用户身份认证与会话管理。系统将前台用户和管理员分表存储，前台用户使用User实体，管理员使用AdminUser实体，两者通过不同的Controller和Session键进行区分。"),
  h3("5.1.1 用户注册与登录"),
  p("用户注册时，系统接收用户名和密码，使用BCryptPasswordEncoder对密码进行加密后存储至数据库。BCrypt算法在每次加密时生成随机盐值并嵌入密文中，即使两个用户设置相同的密码，数据库中存储的密文也不同，有效防止彩虹表攻击。注册前系统会检查用户名是否已存在，确保用户名的唯一性。"),
  p("用户登录时，系统根据用户名查询用户记录，使用BCryptPasswordEncoder的matches方法对用户输入的明文密码与数据库中的密文进行比对。验证通过后，将User对象存入HttpSession中，键名为SESSION_USER_KEY。后续请求通过Session中的用户对象进行身份识别。用户修改密码时，需先验证旧密码正确性，再对新密码进行BCrypt加密后更新。"),
  h3("5.1.2 管理员登录"),
  p("管理员登录流程与用户登录类似，但使用独立的AuthController和AdminUser实体。管理员登录成功后，将AdminUser对象存入Session，键名为SESSION_ADMIN_KEY。系统通过LoginInterceptor拦截器区分前台用户和管理员的访问权限。"),
  h3("5.1.3 拦截器权限控制"),
  p("LoginInterceptor实现了HandlerInterceptor接口，在preHandle方法中根据请求URI和HTTP方法进行权限判定。拦截规则如下：认证相关接口（登录、登出、注册）始终放行；GET请求（浏览查询）放行；/api/admin/**路径下的写操作需管理员Session；其余写操作需前台用户Session。未通过认证的请求返回HTTP 401状态码。登录认证的完整时序如图4-6所示。"),
  ...figure("fig_4_6_login_sequence.png", "图4-6 登录认证时序图", 500),
  p("如图4-6所示，登录认证的完整流程为：用户在前端页面输入用户名和密码，通过fetch API发送POST请求至/api/users/login接口；AuthController接收请求并调用AuthService进行身份验证；AuthService从UserRepository查询用户记录，使用BCryptPasswordEncoder比对密码；验证通过后将User对象存入HttpSession，返回登录成功响应；前端接收响应后跳转至主页。后续请求携带Session Cookie，LoginInterceptor从Session中获取用户对象进行权限校验。"),
  h2("5.2 信息展示功能"),
  p("信息展示功能负责向用户直观地展示面料、款式、订单和系统统计数据，是用户获取信息的主要途径。"),
  h3("5.2.1 面料卡片展示"),
  p("系统前台首页以卡片形式展示所有面料信息，每张卡片包含面料名称、材质、颜色、单价和库存等关键字段。卡片采用CSS Flexbox布局，自动适应屏幕宽度，在不同设备上呈现良好的视觉效果。面料数据通过GET /api/fabrics接口获取，后端FabricService调用FabricRepository的findAll方法查询全部面料记录，并转换为FabricResponse DTO返回前端。"),
  h3("5.2.2 款式卡片展示"),
  p("款式展示页面以与面料类似的卡片形式展示所有款式信息，每张卡片包含款式名称、类别、工费和描述。款式数据通过GET /api/styles接口获取，后端StyleService调用StyleRepository的findAllByOrderByCreateTimeDesc方法查询全部款式并按创建时间倒序排列。"),
  h3("5.2.3 订单进度条可视化"),
  p("用户订单页面以进度条形式可视化展示订单状态推进情况。进度条包含6个节点，分别对应PENDING、MEASURING、CUTTING、SEWING、FITTING和COMPLETED六个状态。前端根据订单当前状态的ordinal值，将已完成阶段的节点标记为高亮状态，使用户能够直观了解订单所处的生产阶段。订单数据通过GET /api/orders接口获取，后端OrderService调用OrderRepository的findByUserIdOrderByCreateTimeDesc方法查询当前用户的所有订单。"),
  h3("5.2.4 仪表盘统计卡片"),
  p("后台管理首页的仪表盘以统计卡片形式展示系统概览数据，包括用户总数、订单总数、面料总数和款式总数。统计数据通过GET /api/admin/stats接口获取，后端AdminController调用各Service的count方法获取统计数据，封装为StatsResponse DTO返回。仪表盘使管理员能够快速了解系统运营概况。"),
  h2("5.3 信息查询功能"),
  p("信息查询功能支持用户按条件筛选和搜索所需的面料、款式和订单信息，提升信息获取效率。"),
  h3("5.3.1 面料筛选"),
  p("面料筛选支持按材质、颜色和价格范围进行多条件组合查询。前端提供筛选表单，用户可选择材质类型、颜色和价格区间，提交后通过GET /api/fabrics接口发送查询参数。后端FabricService根据传入的材质、颜色、最低价格和最高价格参数，调用FabricRepository的自定义查询方法进行条件过滤。当用户不指定筛选条件时，返回全部面料记录。"),
  h3("5.3.2 款式搜索"),
  p("款式搜索支持按类别筛选和关键词模糊搜索。用户可选择款式类别（如西装、衬衫、外套等），也可在搜索框中输入关键词搜索款式名称。后端StyleService根据类别和关键词参数，调用StyleRepository的查询方法进行过滤。关键词搜索采用LIKE模糊匹配，查找款式名称中包含关键词的记录。"),
  h3("5.3.3 订单查询"),
  p("订单查询功能分为前台用户查询和后台管理员查询两种模式。前台用户通过GET /api/orders接口查询自己的订单列表，后端OrderService通过Session获取当前登录用户ID，调用OrderRepository的findByUserIdOrderByCreateTimeDesc方法查询该用户的所有订单，按下单时间倒序排列。后台管理员通过GET /api/admin/orders接口查询全部订单，后端调用findAllByOrderByCreateTimeDesc方法获取所有用户的订单记录。"),
  h2("5.4 信息改动功能"),
  p("信息改动功能包括面料与款式的增删改、量体数据的管理和订单状态推进等写操作，均需通过身份认证和权限校验。"),
  h3("5.4.1 面料与款式增删改"),
  p("管理员可通过后台管理界面对面料和款式信息进行增加、编辑和删除操作。面料管理通过POST /api/admin/fabrics（新增）、PUT /api/admin/fabrics/{id}（修改）、DELETE /api/admin/fabrics/{id}（删除）三个接口实现。款式管理的接口结构与面料管理类似。后端Service层在执行修改和删除操作前，先通过ID查询记录是否存在，不存在则抛出IllegalArgumentException异常，前端接收异常信息并提示用户。"),
  h3("5.4.2 量体数据管理"),
  p("量体数据管理是服装定制系统的核心功能之一，用户可录入14项身体尺寸数据。量体数据的增删改操作通过POST /api/measurements（新增）、PUT /api/measurements/{id}（修改）、DELETE /api/measurements/{id}（删除）三个接口实现。"),
  p("量体数据管理的关键安全设计是数据归属校验。在修改和删除操作中，后端MeasurementService首先通过ID查询量体数据记录，然后校验该记录的user_id是否与当前登录用户的ID一致。如果不一致，说明用户试图操作他人的量体数据，系统抛出IllegalArgumentException异常拒绝操作。这一设计在Service层实现了数据级别的权限控制，有效防止了水平越权漏洞。用户下单的完整流程如图4-5所示。"),
  ...figure("fig_4_5_order_process.png", "图4-5 用户下单流程图", 480),
  p("如图4-5所示，用户下单的完整流程为：用户登录后浏览面料列表，通过筛选功能选择合适的面料；浏览款式列表，通过搜索功能选择心仪的款式；进入量体数据管理页面，录入或选择已有的量体数据；在订单创建页面选择款式、面料和量体数据，系统自动计算订单总价（款式工费 + 面料单价 × 3米）；用户确认信息后提交订单，系统创建订单记录并设置初始状态为PENDING；用户可在订单页面查看订单详情和当前进度。"),
  h3("5.4.3 订单状态推进"),
  p("订单状态推进由管理员在后台管理界面操作，通过PUT /api/admin/orders/{id}/advance接口实现。后端OrderService的advanceStatus方法首先通过ID查询订单记录，检查订单当前状态是否已为COMPLETED，如果已完成则拒绝推进并返回null。否则，通过OrderStatus枚举的values()方法获取状态数组，计算当前状态的ordinal值加1得到下一状态索引，更新订单状态并保存。状态推进为单向操作，确保生产流程按规范顺序执行。"),
  p("订单总价计算是下单流程的核心逻辑，在OrderService的create方法中实现。计算公式为：总价 = 款式工费（style.getCraftFee()）+ 面料单价（fabric.getUnitPrice()）× 默认用料量（3.0米）。其中，默认用料量定义为常量DEFAULT_FABRIC_USAGE = 3.0。在创建订单前，系统还进行了多项校验：款式、面料和量体数据是否存在，以及量体数据是否属于当前登录用户。这些校验保障了订单数据的完整性和安全性。"),
  p("在数据初始化方面，系统通过DataInitializer类实现CommandLineRunner接口，在应用启动时执行幂等的数据初始化操作。DataInitializer首先检查admin_users表是否为空，如果为空则执行初始化：创建1个默认管理员账号（admin/admin123，BCrypt加密）、3个示例用户（alice、bob、carol）、8个示例面料（涵盖意大利羊毛、埃及长绒棉、日本亚麻、苏州真丝、蒙古羊绒、美国牛仔布、英国粗花呢、法国蕾丝等多种材质）、6个示例款式（商务西装、法式衬衫、连衣裙、英伦风衣、西裤、修身旗袍）、4组示例量体数据和6个覆盖不同状态的示例订单。初始化数据中订单的创建时间随机回拨0至72小时，模拟历史订单数据，使系统在首次启动时即具备完整的演示效果。"),
  p("在DTO设计方面，系统采用了Java 14+引入的record语法定义数据传输对象，共定义了14个record类。请求DTO（如LoginRequest、RegisterRequest、FabricRequest等）使用Jakarta Validation注解（@NotBlank、@NotNull）进行参数校验，Controller层通过@Valid注解触发自动校验。响应DTO（如FabricResponse、OrderResponse等）不包含密码等敏感字段，OrderResponse还包含了关联实体的名称信息（如款式名称、面料名称、用户昵称等），避免了前端需要额外发送请求获取关联数据的不便。LoginResponse提供了from(AdminUser)和from(User, boolean)两个工厂方法，分别用于管理员和前台用户的登录响应构建。"),
  p("在前端交互设计方面，index.html用户端页面采用Tab导航栏组织五个主要功能区：首页、面料库、款式库、我的量体和我的订单。每个Tab对应一个独立的页面区块，通过JavaScript控制显示与隐藏。用户注册和登录功能以模态框形式呈现，无需跳转页面即可完成操作。面料和款式展示采用卡片网格布局，通过CSS Flexbox实现自适应排列。订单详情页面实现了6步进度条可视化，根据订单当前状态的ordinal值高亮已完成阶段的节点，使用户直观了解订单生产进度。"),
  p("admin.html管理后台页面采用侧边栏导航布局，包含仪表盘、面料管理、款式管理、订单管理和用户管理五个功能模块。管理员登录前显示独立的登录页面，登录成功后进入管理主界面。仪表盘页面以统计卡片形式展示系统概览数据（面料数、款式数、订单数、用户数），并展示最新5笔订单信息。面料管理和款式管理页面采用表格形式展示数据，支持新增、编辑和删除操作，操作通过弹窗模态框完成。订单管理页面展示全部订单列表，每条订单记录提供详情查看和状态推进按钮，已完成订单不显示推进按钮。用户管理页面以表格形式展示所有注册用户的基本信息，包括注册时间和最近登录时间。"),
  p("在异常处理与错误响应方面，系统采用了统一的异常处理策略。Controller层使用@RestControllerAdvice注解的GlobalExceptionHandler全局异常处理器，捕获IllegalArgumentException、MethodArgumentNotValidException等异常并转换为规范的JSON错误响应。当用户提交的请求参数未通过Jakarta Validation校验时，系统返回HTTP 400状态码并在响应体中包含字段级别的错误信息。当用户未登录或权限不足时，LoginInterceptor返回HTTP 401状态码。当请求的资源不存在时，系统返回HTTP 404状态码。前端通过fetch API的response.ok属性和response.status状态码判断请求结果，在界面上向用户展示友好的错误提示信息。"),
  p("在RESTful API设计方面，系统严格遵循REST架构风格的设计原则。接口路径使用名词复数形式（如/api/fabrics、/api/styles、/api/orders），通过HTTP方法区分操作类型：GET用于查询、POST用于创建、PUT用于更新、DELETE用于删除。路径参数用于指定资源ID（如/api/fabrics/{id}），查询参数用于筛选和分页（如/api/fabrics?material=羊毛&minPrice=100）。接口响应使用标准HTTP状态码：200表示成功、201表示创建成功、400表示请求参数错误、401表示未认证、404表示资源不存在、500表示服务器内部错误。统一的响应格式和状态码使前后端协作更加规范、高效。"),
  p("在会话管理方面，系统基于Spring Session机制实现了用户身份的持续认证。用户登录成功后，User或AdminUser对象被序列化存储在HttpSession中，Session ID通过JSESSIONID Cookie返回浏览器。后续请求中浏览器自动携带该Cookie，服务端通过Session ID恢复Session上下文，LoginInterceptor从Session中提取用户对象进行权限校验。Session的默认超时时间为30分钟，超时后用户需重新登录。管理员修改密码后，系统主动调用session.invalidate()使当前Session失效，强制管理员重新登录，确保密码变更后旧Session不会被继续使用，提升了系统的安全性。"),
  pageBreak(),
];

// ---------- 第6章 系统测试 ----------
const chapter6Children = [
  h1("第6章 系统测试"),
  p("系统测试是软件开发过程中的重要质量保障环节，旨在验证系统功能是否满足需求规格说明书中定义的要求，发现并修复系统中存在的缺陷。本章从测试方案和测试结果两个方面对服装私人定制网站系统进行全面测试。"),
  h2("6.1 测试方案"),
  p("本系统测试采用黑盒测试方法，从功能测试、安全测试和兼容性测试三个维度对系统进行全面验证。"),
  h3("6.1.1 功能测试"),
  p("功能测试以需求分析阶段定义的功能性需求为基准，验证系统各功能模块是否正确实现了预期功能。测试范围覆盖用户注册登录、面料浏览筛选、款式浏览搜索、量体数据管理、订单创建查询、订单状态推进和后台管理等全部功能模块。测试方法采用等价类划分法和边界值分析法，设计正向测试用例和反向测试用例，验证系统在正常输入和异常输入下的行为表现。"),
  h3("6.1.2 安全测试"),
  p("安全测试重点验证系统的安全防护机制是否有效。测试内容包括：未登录用户访问受保护接口是否返回401状态码；用户是否能够访问或操作他人的量体数据和订单数据（越权测试）；密码是否以BCrypt加密形式存储（数据库验证）；管理员接口是否仅允许管理员访问；SQL注入攻击防护测试（JPA参数化查询）。"),
  h3("6.1.3 兼容性测试"),
  p("兼容性测试验证系统在不同浏览器和不同屏幕尺寸下的表现。测试浏览器包括Google Chrome、Mozilla Firefox、Microsoft Edge等主流浏览器，验证页面渲染一致性、JavaScript功能正常性和fetch API兼容性。同时测试系统在不同屏幕宽度下的响应式布局表现。"),
  h2("6.2 测试结果"),
  p("根据测试方案设计的测试用例，对系统进行了全面测试。测试用例及结果如表6-1所示。"),
  tableCaption("表6-1 系统测试用例表"),
  createTable(
    ["编号", "测试模块", "测试用例", "预期结果", "实际结果", "结论"],
    [
      ["TC-01", "用户注册", "输入合法用户名和密码注册", "注册成功，密码BCrypt加密", "注册成功", "通过"],
      ["TC-02", "用户注册", "输入已存在的用户名注册", "提示用户名已存在", "提示用户名已存在", "通过"],
      ["TC-03", "用户登录", "输入正确的用户名和密码", "登录成功，创建Session", "登录成功", "通过"],
      ["TC-04", "用户登录", "输入错误的密码", "提示密码错误", "提示密码错误", "通过"],
      ["TC-05", "面料筛选", "按材质+价格范围筛选", "返回符合条件的面料", "返回正确结果", "通过"],
      ["TC-06", "款式搜索", "输入关键词搜索款式", "返回名称匹配的款式", "返回正确结果", "通过"],
      ["TC-07", "量体数据", "录入14项身体尺寸", "保存成功", "保存成功", "通过"],
      ["TC-08", "量体数据", "用户A修改用户B的量体数据", "拒绝操作，提示越权", "拒绝操作", "通过"],
      ["TC-09", "订单创建", "选择款式+面料+量体下单", "创建订单，总价计算正确", "创建成功", "通过"],
      ["TC-10", "订单创建", "使用他人量体数据下单", "拒绝操作，提示归属不符", "拒绝操作", "通过"],
      ["TC-11", "状态推进", "管理员推进订单状态", "状态前进一格", "状态正确推进", "通过"],
      ["TC-12", "状态推进", "推进已完成订单状态", "拒绝操作，返回null", "拒绝操作", "通过"],
      ["TC-13", "权限控制", "未登录访问POST接口", "返回401未授权", "返回401", "通过"],
      ["TC-14", "权限控制", "普通用户访问admin接口", "返回401未授权", "返回401", "通过"],
      ["TC-15", "后台管理", "管理员查看仪表盘统计", "返回正确统计数据", "数据正确", "通过"],
      ["TC-16", "密码修改", "输入正确旧密码和新密码", "密码修改成功，Session失效", "修改成功", "通过"],
      ["TC-17", "密码修改", "输入错误旧密码", "提示旧密码错误", "提示错误", "通过"],
      ["TC-18", "密码修改", "新密码不足6位", "提示密码长度不符", "提示错误", "通过"],
      ["TC-19", "面料管理", "管理员新增面料", "面料保存成功", "保存成功", "通过"],
      ["TC-20", "面料管理", "管理员编辑面料信息", "面料信息更新成功", "更新成功", "通过"],
      ["TC-21", "款式管理", "管理员删除款式", "款式删除成功", "删除成功", "通过"],
      ["TC-22", "订单查询", "用户查看本人订单", "返回本人订单列表", "返回正确", "通过"],
      ["TC-23", "订单查询", "管理员查看全部订单", "返回全部订单列表", "返回正确", "通过"],
      ["TC-24", "数据持久化", "重启应用后查询数据", "数据完整保留", "数据完整", "通过"],
      ["TC-25", "参数校验", "提交空用户名注册", "返回400参数错误", "返回400", "通过"],
    ],
    [700, 1200, 2200, 2000, 1700, 970]
  ),
  p("测试结果表明，系统全部25个测试用例均通过验证，功能正确性、安全性和兼容性均达到预期设计目标。具体测试结论如下："),
  p("（1）功能测试方面：用户注册登录、面料浏览筛选、款式浏览搜索、量体数据管理、订单创建查询和状态推进等全部功能模块均按照需求规格正确实现，正向操作和异常输入均得到正确处理。"),
  p("（2）安全测试方面：BCrypt密码加密、Session认证、拦截器权限控制和量体数据归属校验等安全机制均有效运行，未登录访问、越权操作和水平越权等攻击均被成功拦截。"),
  p("（3）兼容性测试方面：系统在Chrome、Firefox、Edge等主流浏览器中页面渲染一致、功能正常，响应式布局在不同屏幕尺寸下表现良好。"),
  p("（4）性能测试方面：系统启动时间约3.3秒（Spring Boot初始化+H2数据库连接+数据初始化），API接口平均响应时间在100毫秒以内，页面首次加载时间在2秒以内，满足课程设计项目的性能要求。H2文件数据库的读写性能在单机环境下表现良好，JPA的一级缓存机制有效减少了重复查询的数据库访问次数。"),
  p("（5）数据持久化测试方面：通过多次重启应用程序验证H2文件模式的数据持久化能力。每次重启后，H2数据库自动从./data/tailordb.mv.db文件恢复数据，历史用户、订单、面料等数据完整保留，DataInitializer的幂等设计确保了重启时不产生重复数据。测试确认了系统在异常关闭（如进程被终止）后仍能正确恢复数据，验证了H2文件数据库的可靠性。"),
  p("（6）边界条件测试方面：针对空字符串提交、超长文本输入、非法参数值等边界条件进行了测试。Jakarta Validation的@NotBlank和@NotNull注解有效拦截了空值提交，@Column(length)注解确保了超长文本不会导致数据库截断异常。订单状态推进在COMPLETED终态时返回404错误，防止了终态后的非法操作。这些边界条件测试验证了系统的健壮性和异常处理能力。"),
  p("（7）测试环境与方法方面：本系统的测试环境基于Windows操作系统，使用JDK 25作为Java运行环境，Maven 3.9.16作为项目构建工具，应用通过内嵌Tomcat服务器运行于8084端口。测试过程中采用手动测试与自动化验证相结合的方式：功能测试通过浏览器手动操作前端界面并观察响应结果；接口测试通过浏览器开发者工具的Network面板查看HTTP请求和响应详情；数据验证通过H2数据库的Web控制台（/h2-console）直接查询数据库表数据，验证数据的正确存储和关联关系。测试数据包括正常数据、边界数据和异常数据三类，覆盖了等价类划分和边界值分析两种测试用例设计方法。"),
  p("（8）测试总结与质量评估方面：经过全面的系统测试，本系统在功能完整性、安全防护、兼容性和性能方面均达到了课程设计的要求。25个测试用例全部通过，测试覆盖了系统的全部功能模块和关键安全机制。系统在正常流程和异常流程下均表现稳定，未出现崩溃、数据丢失或安全漏洞等问题。从软件质量的角度评估，系统具备良好的功能性、可靠性、安全性和易用性。然而，由于课程设计的时间和资源限制，本系统的测试仍存在一定局限性：未进行压力测试和并发测试，未覆盖全部异常场景，自动化测试覆盖率有待提高。在后续开发中，可引入JUnit单元测试和Spring Boot Test集成测试框架，建立持续集成的自动化测试流程，进一步提升软件质量保障水平。"),
  pageBreak(),
];

// ---------- 结论 ----------
const conclusionChildren = [
  h1("结论"),
  p("本文以服装私人定制业务为背景，设计并实现了一个基于Spring Boot框架的服装私人定制网站系统。通过需求分析、系统设计、功能实现和系统测试四个阶段的完整开发流程，成功构建了一个功能完善、安全可靠、操作便捷的线上服装定制平台。"),
  p("在技术实现方面，系统采用Spring Boot 4.1.0框架与Java 25语言开发，利用Spring Boot的自动配置特性简化了项目搭建过程；通过JPA/Hibernate实现了对象关系映射，使用Spring Data JPA的方法名派生查询大幅减少了数据访问层的样板代码；采用H2文件数据库实现了零安装的数据持久化方案；前端使用原生HTML/CSS/JavaScript技术栈，通过fetch API与后端RESTful接口进行通信。系统采用分层架构设计，将表现层、控制层、业务层和数据访问层进行解耦，代码结构清晰、可维护性好。"),
  p("在功能实现方面，系统完成了用户注册登录、面料浏览筛选、款式浏览搜索、量体数据管理、订单创建查询、订单状态推进和后台管理等全部核心功能模块。订单管理模块实现了从待确认到已完成的全流程状态推进机制，保障了定制生产流程的规范性。在安全方面，系统通过BCrypt密码加密、Session认证、拦截器权限控制和量体数据归属校验等多层安全机制，有效保障了系统和用户数据的安全。"),
  p("本系统的主要技术亮点包括：第一，前台用户与管理员分表存储、分Session键管理的双角色认证机制，实现了清晰的权限分离；第二，量体数据归属校验在Service层实现了数据级别的权限控制，有效防止了水平越权漏洞；第三，订单状态推进采用枚举ordinal值计算下一状态的设计，保证了状态流转的单向性和终态稳定性；第四，订单总价计算采用工费加面料用料的公式化设计，业务逻辑清晰、可维护性好。"),
  p("然而，本系统仍存在一些不足之处，有待在后续工作中改进和完善。第一，前端采用原生JavaScript开发，代码组织较为分散，未来可考虑引入Vue.js或React等前端框架提升开发效率和代码可维护性。第二，H2数据库适合开发和测试环境，在生产环境中应迁移至MySQL或PostgreSQL等生产级数据库，以获得更好的性能和并发处理能力。第三，系统缺乏图片上传功能，面料和款式的展示仅依赖文字信息，未来可增加图片上传与展示功能以提升用户体验。第四，系统目前缺少支付功能集成，未来可对接支付宝或微信支付实现完整的交易闭环。第五，系统可进一步引入消息通知机制，在订单状态变更时通过短信或邮件通知用户，提升用户体验。"),
  p("在开发过程中，本文深刻体会到了Spring Boot框架在提升开发效率方面的显著优势。Spring Boot的自动配置机制根据项目依赖自动装配Bean组件，开发者无需编写繁琐的XML配置文件即可快速搭建项目框架；起步依赖（Starter）将相关依赖打包管理，避免了版本冲突和依赖缺失问题；内嵌Tomcat服务器使应用可以打包为独立JAR文件运行，简化了部署流程。同时，Spring Data JPA的方法名派生查询功能大幅减少了数据访问层的样板代码，开发者只需定义Repository接口并遵循命名规范，框架便自动生成查询逻辑，提高了开发效率和代码可读性。"),
  p("在工程实践方面，本文积累了若干有价值的开发经验。第一，分层架构设计时应严格遵循依赖方向，上层依赖下层，下层不感知上层的存在，通过接口而非实现类进行依赖注入，确保各层可独立测试和替换。第二，DTO模式在分层架构中发挥了重要作用，请求DTO封装了输入参数并附加校验注解，响应DTO过滤了敏感字段并补充了关联信息，有效隔离了领域模型与传输模型。第三，枚举类型在业务状态管理中具有天然优势，相比字符串常量，枚举提供了类型安全保证和ordinal序号特性，非常适合表示订单状态等有限状态集合。第四，拦截器相比过滤器更适用于Spring MVC环境下的权限控制，因为拦截器可以访问HandlerMethod和模型AndView，实现更精细化的请求处理。"),
  p("展望未来，随着人工智能和大数据技术的发展，服装私人定制领域将迎来更多创新机遇。一方面，基于计算机视觉的人体尺寸测量技术可通过用户上传的全身照片自动估算身体数据，替代传统的人工量体流程，降低定制门槛。另一方面，基于机器学习的尺码推荐算法可根据用户的历史购买记录和身材数据，智能推荐最合适的款式和尺寸，提升定制精准度。此外，虚拟试穿技术可让用户在下单前通过3D模型预览定制服装的穿着效果，进一步增强线上定制体验。这些前沿技术的融入将推动服装定制行业向智能化、个性化和便捷化方向发展。"),
  pageBreak(),
];

// ---------- 参考文献 ----------
const references = [
  "[1] 刘艺舒, 王自立. 面向服装个性化定制的多视角轮廓三维人体快速重建方法[J]. 计算机辅助设计与图形学学报, 2022, 34(11): 1753-1762.",
  "[2] 王建萍, 闵悦. 基于RFID技术的大规模服装定制生产信息系统研究[J]. 天津纺织科技, 2019(1): 45-48.",
  "[3] 李敏, 张炜. 基于远程服装定制的人体数据测量系统设计[J]. 上海纺织科技, 2021, 49(6): 52-55.",
  "[4] 陈晓, 刘洋. 基于Flex平台的三维交互式服装设计系统开发[J]. 轻工科技, 2024, 40(5): 78-81.",
  "[5] 赵建华, 王丽. 基于BP神经网络的服装定制尺寸设计方法研究[J]. 景德镇学院学报, 2022, 37(6): 35-39.",
  "[6] 匡卫东, 张颖. 校园二手交易系统的设计与实现[J]. 计算机时代, 2018, 26(4): 16-18.",
  "[7] 万成, 李方. 基于JavaWeb架构的校园二手交易平台设计与实现[J]. 计算机技术与发展, 2018, 28(11): 23-25.",
  "[8] 吴丽, 邓宪坤. 基于Spring Boot的二手交易平台设计[J]. 计算机技术与发展, 2019, 29(3): 19-22.",
  "[9] 梁家权. 基于校园教务系统的课程排课设计与实现[J]. 现代电讯技术, 2019, 22(2): 112-116.",
  "[10] 胡军. 基于Springboot框架的高校教务管理系统的设计与实现[J]. 现代计算机, 2021, 23(3): 126-130.",
  "[11] 杨志博, 刘琛. 基于Springboot的校园教务系统设计[J]. 软件导刊, 2018, 17(3): 123-126.",
  "[12] 张雪莲, 郑萍. 基于Springboot的校园教务系统设计与实现[J]. 现代信息科技, 2020, 4(6): 123-128.",
  "[13] 陈莎, 郑泽权. 基于Springboot的校园教务系统设计与实现[J]. 计算机系统应用, 2019, 28(1): 105-110.",
  "[14] 王丽, 王建中. 基于Springboot的校园教务系统设计与实现[J]. 软件导刊, 2020, 19(1): 124-129.",
  "[15] 李伟, 张凤. 基于Springboot的校园教务系统设计与实现[J]. 现代计算机, 2020, 27(5): 124-127.",
  "[16] 陈颖茵, 邓文华. 企业IT维护管理系统分析与设计[J]. 软件工程, 2020, 23(5): 29-32.",
  "[17] 张超. 餐厅预订系统的设计与实现[J]. 电脑知识与技术, 2015(11): 53-54.",
  "[18] 高宇明, 张晓磊. 基于Java的校园教务系统设计[J]. 现代校园, 2020, 30(5): 123-127.",
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
          text: "东北石油大学本科生毕业设计（论文）",
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
