# -*- coding: utf-8 -*-
"""
图书封面图片生成脚本
====================
使用 Pillow 本地生成 15 本书的封面图片（PNG 格式，300x450 像素）。

设计要点：
1. 每本书按分类使用不同主色调，形成视觉分组；
2. 顶部色块标识分类，中部大字标题（自动换行），底部作者名；
3. 侧边装饰条与底部细线增强设计感；
4. 中文字体优先使用 Windows 系统自带的微软雅黑/黑体。

运行方式：
    python generate_covers.py

输出目录：
    ../src/main/resources/static/images/covers/
"""

import os  # 操作系统接口，用于路径拼接与目录创建
from PIL import Image, ImageDraw, ImageFont  # Pillow 图像处理核心组件

# ===== 路径配置 =====
# 脚本所在目录
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
# 封面图输出目录（相对于脚本的上级 src/main/resources/static/images/covers）
OUTPUT_DIR = os.path.join(SCRIPT_DIR, "..", "src", "main", "resources", "static", "images", "covers")
# 中文字体候选路径（按优先级尝试）
FONT_CANDIDATES = [
    r"C:\Windows\Fonts\msyhbd.ttc",   # 微软雅黑粗体（标题优先）
    r"C:\Windows\Fonts\msyh.ttc",     # 微软雅黑常规
    r"C:\Windows\Fonts\simhei.ttf",   # 黑体
    r"C:\Windows\Fonts\simsun.ttc",   # 宋体（兜底）
]

# ===== 封面尺寸 =====
COVER_WIDTH = 300   # 封面宽度（像素）
COVER_HEIGHT = 450  # 封面高度（像素），比例 2:3 为标准书封面

# ===== 分类配色方案 =====
# 每个分类对应主色（深）与辅色（浅），用于背景渐变与装饰
CATEGORY_COLORS = {
    "计算机科学": {"primary": (30, 64, 175), "secondary": (59, 130, 246)},   # 蓝色系
    "文学":       {"primary": (185, 28, 28), "secondary": (220, 38, 38)},    # 红色系
    "历史":       {"primary": (146, 64, 14), "secondary": (217, 119, 6)},    # 棕色系
    "哲学":       {"primary": (107, 33, 168), "secondary": (147, 51, 234)},  # 紫色系
    "经济学":     {"primary": (21, 128, 61), "secondary": (22, 163, 74)},    # 绿色系
}

# ===== 15 本书的元数据列表 =====
# 顺序与 DataInitializer.initBooks() 中的创建顺序保持一致
BOOKS = [
    # 计算机科学（4 本）
    {"id": 1,  "title": "Java核心技术 卷I",      "author": "凯 S. 霍斯特曼",     "category": "计算机科学"},
    {"id": 2,  "title": "深入理解Java虚拟机",     "author": "周志明",            "category": "计算机科学"},
    {"id": 3,  "title": "Spring Boot实战",       "author": "克雷格 沃斯",        "category": "计算机科学"},
    {"id": 4,  "title": "数据结构与算法分析",     "author": "马克 艾伦 韦斯",     "category": "计算机科学"},
    # 文学（3 本）
    {"id": 5,  "title": "红楼梦",                "author": "曹雪芹",            "category": "文学"},
    {"id": 6,  "title": "百年孤独",              "author": "加西亚 马尔克斯",     "category": "文学"},
    {"id": 7,  "title": "活着",                  "author": "余华",              "category": "文学"},
    # 历史（3 本）
    {"id": 8,  "title": "史记",                  "author": "司马迁",            "category": "历史"},
    {"id": 9,  "title": "全球通史",              "author": "斯塔夫里阿诺斯",     "category": "历史"},
    {"id": 10, "title": "明朝那些事儿",          "author": "当年明月",          "category": "历史"},
    # 哲学（2 本）
    {"id": 11, "title": "中国哲学简史",          "author": "冯友兰",            "category": "哲学"},
    {"id": 12, "title": "西方哲学史",            "author": "伯特兰 罗素",       "category": "哲学"},
    # 经济学（3 本）
    {"id": 13, "title": "经济学原理",            "author": "曼昆",              "category": "经济学"},
    {"id": 14, "title": "国富论",                "author": "亚当 斯密",         "category": "经济学"},
    {"id": 15, "title": "穷查理宝典",            "author": "查理 芒格",         "category": "经济学"},
]


def load_font(size):
    """
    加载中文字体
    按 FONT_CANDIDATES 顺序尝试加载，第一个可用即返回。
    :param size: 字号
    :return: ImageFont 对象
    :raises RuntimeError: 所有候选字体均不可用
    """
    for path in FONT_CANDIDATES:
        if os.path.exists(path):
            return ImageFont.truetype(path, size)
    raise RuntimeError("未找到可用中文字体，请检查 C:\\Windows\\Fonts 目录")


def wrap_text(text, font, draw, max_width):
    """
    中文文本自动换行
    逐字符累加宽度，超过 max_width 时断行，支持中文（无空格分词）。
    :param text: 待换行文本
    :param font: 字体对象
    :param draw: ImageDraw 对象，用于测量文本宽度
    :param max_width: 单行最大宽度（像素）
    :return: 换行后的字符串（含 \n）
    """
    lines = []          # 已完成的行列表
    current = ""        # 当前正在累加的行
    for char in text:
        candidate = current + char                    # 尝试追加一个字符
        bbox = draw.textbbox((0, 0), candidate, font=font)
        width = bbox[2] - bbox[0]                     # 计算候选行宽度
        if width <= max_width or not current:         # 未超宽或当前行空时接受
            current = candidate
        else:                                          # 超宽则断行
            lines.append(current)
            current = char
    if current:                                        # 追加最后一行
        lines.append(current)
    return "\n".join(lines)


def draw_vertical_gradient(draw, width, height, top_color, bottom_color):
    """
    绘制垂直渐变背景
    通过逐行插值实现从上到下的颜色过渡。
    :param draw: ImageDraw 对象
    :param width: 画布宽度
    :param height: 画布高度
    :param top_color: 顶部颜色 (R, G, B)
    :param bottom_color: 底部颜色 (R, G, B)
    """
    for y in range(height):
        ratio = y / max(height - 1, 1)                # 当前行的插值比例 [0,1]
        r = int(top_color[0] + (bottom_color[0] - top_color[0]) * ratio)
        g = int(top_color[1] + (bottom_color[1] - top_color[1]) * ratio)
        b = int(top_color[2] + (bottom_color[2] - top_color[2]) * ratio)
        draw.line([(0, y), (width, y)], fill=(r, g, b))


def generate_cover(book, output_path):
    """
    为单本书生成封面图
    布局：顶部分类条 -> 渐变背景 -> 居中标题 -> 底部作者 -> 侧边装饰
    :param book: 图书元数据字典
    :param output_path: 输出文件路径
    """
    # 1. 创建画布与绘图对象
    img = Image.new("RGB", (COVER_WIDTH, COVER_HEIGHT), (255, 255, 255))
    draw = ImageDraw.Draw(img)

    # 2. 获取分类配色
    colors = CATEGORY_COLORS.get(book["category"], {"primary": (50, 50, 50), "secondary": (120, 120, 120)})
    primary = colors["primary"]
    secondary = colors["secondary"]

    # 3. 绘制垂直渐变背景
    draw_vertical_gradient(draw, COVER_WIDTH, COVER_HEIGHT, secondary, primary)

    # 4. 顶部分类标识条（深色矩形 + 白色分类名）
    draw.rectangle([(0, 0), (COVER_WIDTH, 50)], fill=primary)
    category_font = load_font(20)
    cat_bbox = draw.textbbox((0, 0), book["category"], font=category_font)
    cat_w = cat_bbox[2] - cat_bbox[0]
    cat_x = (COVER_WIDTH - cat_w) // 2               # 水平居中
    draw.text((cat_x, 14), book["category"], fill=(255, 255, 255), font=category_font)

    # 5. 侧边装饰条（左侧细竖线，增强设计感）
    draw.rectangle([(15, 60), (20, COVER_HEIGHT - 60)], fill=(255, 255, 255))

    # 6. 居中标题（自动换行，最多 4 行）
    title_font = load_font(32)
    wrapped_title = wrap_text(book["title"], title_font, draw, COVER_WIDTH - 60)
    title_lines = wrapped_title.split("\n")[:4]       # 限制最多 4 行，避免溢出
    line_height = 42
    total_h = len(title_lines) * line_height
    start_y = (COVER_HEIGHT - total_h) // 2 - 20      # 垂直居中偏上
    for i, line in enumerate(title_lines):
        bbox = draw.textbbox((0, 0), line, font=title_font)
        line_w = bbox[2] - bbox[0]
        x = (COVER_WIDTH - line_w) // 2               # 每行水平居中
        y = start_y + i * line_height
        # 文字阴影（轻微偏移的深色副本，增加可读性）
        draw.text((x + 2, y + 2), line, fill=(0, 0, 0), font=title_font)
        draw.text((x, y), line, fill=(255, 255, 255), font=title_font)

    # 7. 底部分隔线
    draw.line([(40, COVER_HEIGHT - 80), (COVER_WIDTH - 40, COVER_HEIGHT - 80)],
              fill=(255, 255, 255), width=2)

    # 8. 底部作者名（居中）
    author_font = load_font(22)
    author_text = "— " + book["author"] + " —"
    au_bbox = draw.textbbox((0, 0), author_text, font=author_font)
    au_w = au_bbox[2] - au_bbox[0]
    au_x = (COVER_WIDTH - au_w) // 2
    draw.text((au_x, COVER_HEIGHT - 60), author_text, fill=(255, 255, 255), font=author_font)

    # 9. 保存为 PNG（优化压缩）
    img.save(output_path, "PNG", optimize=True)


def main():
    """
    主入口：创建输出目录并为 15 本书逐一生成封面
    """
    # 确保输出目录存在
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    print(f"[INFO] 输出目录: {OUTPUT_DIR}")

    # 逐本生成封面
    for book in BOOKS:
        # 文件名格式：book_01.png ~ book_15.png，编号与图书 ID 一致
        filename = f"book_{book['id']:02d}.png"
        output_path = os.path.join(OUTPUT_DIR, filename)
        generate_cover(book, output_path)
        print(f"[OK] 已生成: {filename}  《{book['title']}》")

    print(f"\n[DONE] 共生成 {len(BOOKS)} 张封面图")


if __name__ == "__main__":
    main()
