# -*- coding: utf-8 -*-
"""
校园图书借阅管理系统 - 系统设计图片生成脚本
==========================================
生成 6 张系统设计图片（SVG + PNG 双格式），用于系统设计文档配图。

技术栈：Spring Boot 4.1.0 + JPA + H2 数据库
输出目录：./figures/

依赖：matplotlib
运行：python gen_figures.py
"""

import os
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import (FancyBboxPatch, FancyArrowPatch, Polygon,
                                Rectangle)

# ----------------------------------------------------------------------
# 全局配置
# ----------------------------------------------------------------------
plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'SimSun']
plt.rcParams['axes.unicode_minus'] = False

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
OUTPUT_DIR = os.path.join(BASE_DIR, 'figures')
os.makedirs(OUTPUT_DIR, exist_ok=True)
DPI = 200


# ======================================================================
# 辅助绘制函数
# ======================================================================

def add_box(ax, cx, cy, w, h, text, facecolor, edgecolor,
            fontsize=8, text_color='black', fontweight='normal',
            rounding=0.06, zorder=3, alpha=1.0):
    """绘制圆角矩形框并居中放置文字。"""
    box = FancyBboxPatch(
        (cx - w / 2, cy - h / 2), w, h,
        boxstyle="round,pad=0,rounding_size={:.2f}".format(rounding),
        facecolor=facecolor, edgecolor=edgecolor,
        linewidth=1.5, zorder=zorder, alpha=alpha
    )
    ax.add_patch(box)
    ax.text(cx, cy, text, ha='center', va='center',
            fontsize=fontsize, color=text_color,
            fontweight=fontweight, zorder=zorder + 1)


def add_arrow(ax, start, end, color='#333333', lw=1.5,
              style='->', ls='solid', mutation=15, zorder=2):
    """绘制箭头。"""
    arrow = FancyArrowPatch(
        start, end,
        arrowstyle=style, mutation_scale=mutation,
        color=color, linewidth=lw, linestyle=ls,
        zorder=zorder, connectionstyle="arc3,rad=0"
    )
    ax.add_patch(arrow)


def add_diamond(ax, cx, cy, w, h, text, facecolor, edgecolor,
                fontsize=7, zorder=3):
    """绘制菱形（判断节点）。"""
    diamond = Polygon(
        [(cx, cy + h / 2), (cx + w / 2, cy),
         (cx, cy - h / 2), (cx - w / 2, cy)],
        closed=True, facecolor=facecolor, edgecolor=edgecolor,
        linewidth=1.5, zorder=zorder
    )
    ax.add_patch(diamond)
    ax.text(cx, cy, text, ha='center', va='center',
            fontsize=fontsize, zorder=zorder + 1)


def save_fig(fig, name):
    """同时保存 SVG 和 PNG 格式。"""
    svg_path = os.path.join(OUTPUT_DIR, name + '.svg')
    png_path = os.path.join(OUTPUT_DIR, name + '.png')
    fig.savefig(svg_path, format='svg', bbox_inches='tight')
    fig.savefig(png_path, format='png', dpi=DPI, bbox_inches='tight')
    plt.close(fig)
    print(f'  [OK] {name}.svg / {name}.png')


# ======================================================================
# 图 4-1：系统架构图（五层架构）
# ======================================================================

def draw_fig_4_1_architecture():
    """绘制系统架构图：表现层→控制层→业务逻辑层→数据访问层→数据层 + 横切标注。"""
    fig, ax = plt.subplots(figsize=(16, 11))
    ax.set_xlim(0, 16)
    ax.set_ylim(0, 11)
    ax.axis('off')
    fig.suptitle('图 4-1  系统架构图', fontsize=16, fontweight='bold', y=0.97)

    # 层级配色：填充色, 边框色
    layers = [
        {'label': '表现层', 'y': 9.0, 'h': 1.2, 'fc': '#D6E4F0', 'ec': '#2E6FA7'},
        {'label': '控制层', 'y': 7.2, 'h': 1.2, 'fc': '#D5F0DC', 'ec': '#2E8B57'},
        {'label': '业务逻辑层', 'y': 5.4, 'h': 1.2, 'fc': '#FCE8C8', 'ec': '#C77F1A'},
        {'label': '数据访问层', 'y': 3.6, 'h': 1.2, 'fc': '#E6D6F0', 'ec': '#7B3FA0'},
        {'label': '数据层', 'y': 1.8, 'h': 1.2, 'fc': '#F5D6D6', 'ec': '#B33A3A'},
    ]

    # 左侧留出空间给层级标签
    layer_x = 3.0
    layer_w = 10.0

    for layer in layers:
        # 绘制层级背景
        bg = FancyBboxPatch(
            (layer_x, layer['y'] - layer['h'] / 2), layer_w, layer['h'],
            boxstyle="round,pad=0,rounding_size=0.15",
            facecolor=layer['fc'], edgecolor=layer['ec'],
            linewidth=2, zorder=1, alpha=0.30
        )
        ax.add_patch(bg)
        # 层级标签
        ax.text(layer_x - 0.4, layer['y'], layer['label'],
                ha='right', va='center', fontsize=12,
                fontweight='bold', color=layer['ec'])

    # 各层内部元素
    # 表现层
    add_box(ax, 6.5, 9.0, 2.6, 0.7, 'index.html\n（用户端）',
            '#D6E4F0', '#2E6FA7', fontsize=9, fontweight='bold')
    add_box(ax, 10.5, 9.0, 2.6, 0.7, 'admin.html\n（管理后台）',
            '#D6E4F0', '#2E6FA7', fontsize=9, fontweight='bold')

    # 控制层
    controllers = ['AuthController', 'BookController', 'BorrowController',
                   'CartController', 'CategoryController', 'ReaderController',
                   'AdminController', 'StatsController']
    cx_start = 3.8
    cx_gap = 1.25
    for i, name in enumerate(controllers):
        add_box(ax, cx_start + i * cx_gap, 7.2, 1.15, 0.55, name,
                '#D5F0DC', '#2E8B57', fontsize=6.5)

    # 业务逻辑层
    services = ['AuthService', 'BookService', 'BorrowService',
                'CartService', 'CategoryService', 'ReaderService', 'StatsService']
    sx_start = 4.1
    sx_gap = 1.35
    for i, name in enumerate(services):
        add_box(ax, sx_start + i * sx_gap, 5.4, 1.2, 0.55, name,
                '#FCE8C8', '#C77F1A', fontsize=7)

    # 数据访问层
    repos = ['AdminUser\nRepository', 'Book\nRepository', 'BookCategory\nRepository',
             'BorrowRecord\nRepository', 'CartItem\nRepository', 'Reader\nRepository']
    rx_start = 4.3
    rx_gap = 1.55
    for i, name in enumerate(repos):
        add_box(ax, rx_start + i * rx_gap, 3.6, 1.35, 0.65, name,
                '#E6D6F0', '#7B3FA0', fontsize=6.5)

    # 数据层
    add_box(ax, 6.5, 1.8, 2.8, 0.7, 'H2 文件数据库\n（librarydb）',
            '#F5D6D6', '#B33A3A', fontsize=9, fontweight='bold')
    add_box(ax, 10.5, 1.8, 2.8, 0.7, 'JPA / Hibernate\nORM',
            '#F5D6D6', '#B33A3A', fontsize=9, fontweight='bold')

    # 层间箭头
    for y_from, y_to in [(9.6, 7.8), (7.8, 6.0), (6.0, 4.2), (4.2, 2.4)]:
        add_arrow(ax, (8.0, y_from), (8.0, y_to),
                  color='#555555', lw=2, mutation=18)

    # 横切标注：LoginInterceptor
    lx = 14.2
    ly_top = 9.6
    ly_bot = 1.2
    add_box(ax, lx, (ly_top + ly_bot) / 2, 1.2, ly_top - ly_bot,
            'L\no\ng\ni\nn\nI\nn\nt\ne\nc\ne\np\nt\no\nr',
            '#E8E8E8', '#555555', fontsize=7, rounding=0.1, alpha=0.8)
    ax.text(lx, ly_top + 0.3, '请求拦截与权限校验',
            ha='center', va='bottom', fontsize=8, color='#555555',
            fontstyle='italic')
    # 横切虚线箭头
    add_arrow(ax, (lx - 0.6, 7.2), (13.1, 7.2),
              color='#888888', lw=1.2, style='->', ls='dashed', mutation=12)
    add_arrow(ax, (lx - 0.6, 5.4), (13.1, 5.4),
              color='#888888', lw=1.2, style='->', ls='dashed', mutation=12)
    add_arrow(ax, (lx - 0.6, 3.6), (13.1, 3.6),
              color='#888888', lw=1.2, style='->', ls='dashed', mutation=12)

    save_fig(fig, 'fig_4_1_architecture')


# ======================================================================
# 图 4-2：功能模块图
# ======================================================================

def draw_fig_4_2_modules():
    """绘制功能模块图：六大模块。"""
    fig, ax = plt.subplots(figsize=(16, 10))
    ax.set_xlim(0, 16)
    ax.set_ylim(0, 10)
    ax.axis('off')
    fig.suptitle('图 4-2  功能模块图', fontsize=16, fontweight='bold', y=0.97)

    # 中心系统名
    add_box(ax, 8.0, 5.0, 3.5, 1.0,
            '校园图书借阅管理系统',
            '#2E6FA7', '#1A4A7A', fontsize=13, fontweight='bold',
            text_color='white', rounding=0.15)

    # 六大模块
    modules = [
        {'name': '认证管理', 'items': '登录 / 注册 / 修改密码',
         'cx': 3.0, 'cy': 8.0, 'fc': '#D6E4F0', 'ec': '#2E6FA7'},
        {'name': '图书管理', 'items': '浏览 / 搜索 / 增删改',
         'cx': 8.0, 'cy': 8.0, 'fc': '#D5F0DC', 'ec': '#2E8B57'},
        {'name': '分类管理', 'items': '浏览 / 增删改',
         'cx': 13.0, 'cy': 8.0, 'fc': '#FCE8C8', 'ec': '#C77F1A'},
        {'name': '借阅管理', 'items': '借阅 / 续借 / 归还',
         'cx': 3.0, 'cy': 2.0, 'fc': '#E6D6F0', 'ec': '#7B3FA0'},
        {'name': '借阅车管理', 'items': '加入 / 移除 / 提交批量借阅',
         'cx': 8.0, 'cy': 2.0, 'fc': '#F5D6D6', 'ec': '#B33A3A'},
        {'name': '读者管理', 'items': '个人信息 / 修改密码',
         'cx': 13.0, 'cy': 2.0, 'fc': '#D6E8E8', 'ec': '#2A7A7A'},
    ]

    for m in modules:
        # 模块框
        add_box(ax, m['cx'], m['cy'], 3.2, 1.4,
                f"{m['name']}\n─────────\n{m['items']}",
                m['fc'], m['ec'], fontsize=10, rounding=0.12)
        # 连接线到中心
        add_arrow(ax, (m['cx'], m['cy'] - 0.7 if m['cy'] > 5 else m['cy'] + 0.7),
                  (8.0, 5.5 if m['cy'] > 5 else 4.5),
                  color=m['ec'], lw=1.8, style='->', mutation=15)

    save_fig(fig, 'fig_4_2_modules')


# ======================================================================
# 图 4-3：E-R 图
# ======================================================================

def draw_fig_4_3_er_diagram():
    """绘制 E-R 图。"""
    fig, ax = plt.subplots(figsize=(18, 12))
    ax.set_xlim(0, 18)
    ax.set_ylim(0, 12)
    ax.axis('off')
    fig.suptitle('图 4-3  E-R 图', fontsize=16, fontweight='bold', y=0.97)

    # 实体定义：(cx, cy, 名称, 字段列表, 填充色, 边框色)
    entities = [
        (3.5, 9.0, 'BookCategory', [
            'id: Long (PK)',
            'name: String',
            'description: String',
            'sortOrder: Integer',
        ], '#D5F0DC', '#2E8B57'),

        (3.5, 4.5, 'Book', [
            'id: Long (PK)',
            'title: String',
            'author: String',
            'isbn: String',
            'category_id: Long (FK)',
            'publisher: String',
            'publishYear: Integer',
            'totalCopies: Integer',
            'availableCopies: Integer',
            'version: Integer',
        ], '#D6E4F0', '#2E6FA7'),

        (10.0, 7.5, 'Reader', [
            'id: Long (PK)',
            'readerNo: String',
            'password: String',
            'name: String',
            'type: ReaderType',
            'department: String',
            'phone: String',
            'currentBorrowCount: Integer',
        ], '#FCE8C8', '#C77F1A'),

        (10.0, 3.5, 'BorrowRecord', [
            'id: Long (PK)',
            'book_id: Long (FK)',
            'reader_id: Long (FK)',
            'borrowDate: LocalDate',
            'dueDate: LocalDate',
            'returnDate: LocalDate',
            'status: BorrowStatus',
            'renewCount: Integer',
            'fine: BigDecimal',
        ], '#E6D6F0', '#7B3FA0'),

        (15.5, 3.5, 'CartItem', [
            'id: Long (PK)',
            'reader_id: Long (FK)',
            'book_id: Long (FK)',
            'createTime: LocalDateTime',
        ], '#F5D6D6', '#B33A3A'),

        (15.5, 9.0, 'AdminUser', [
            'id: Long (PK)',
            'username: String',
            'password: String',
            'realName: String',
        ], '#D6E8E8', '#2A7A7A'),
    ]

    for cx, cy, name, fields, fc, ec in entities:
        # 标题栏
        header_h = 0.5
        body_h = len(fields) * 0.38
        total_h = header_h + body_h
        box_w = 3.2

        # 整体框背景
        bg = FancyBboxPatch(
            (cx - box_w / 2, cy - total_h / 2), box_w, total_h,
            boxstyle="round,pad=0,rounding_size=0.1",
            facecolor=fc, edgecolor=ec,
            linewidth=2, zorder=2, alpha=0.4
        )
        ax.add_patch(bg)

        # 标题栏
        header = FancyBboxPatch(
            (cx - box_w / 2, cy + total_h / 2 - header_h), box_w, header_h,
            boxstyle="round,pad=0,rounding_size=0.1",
            facecolor=ec, edgecolor=ec,
            linewidth=2, zorder=3, alpha=0.85
        )
        ax.add_patch(header)
        ax.text(cx, cy + total_h / 2 - header_h / 2, name,
                ha='center', va='center', fontsize=9,
                fontweight='bold', color='white', zorder=4)

        # 字段列表
        for i, field in enumerate(fields):
            fy = cy + total_h / 2 - header_h - (i + 0.5) * 0.38
            ax.text(cx - box_w / 2 + 0.15, fy, field,
                    ha='left', va='center', fontsize=6.5,
                    color='#333333', zorder=4)

    # 关系连线
    # BookCategory (1) ── (N) Book
    ax.annotate('', xy=(3.5, 7.1), xytext=(3.5, 6.3),
                arrowprops=dict(arrowstyle='->', color='#2E8B57', lw=1.8))
    ax.text(4.2, 6.7, '1 : N', fontsize=9, color='#2E8B57', fontweight='bold')

    # Reader (1) ── (N) BorrowRecord
    ax.annotate('', xy=(10.0, 5.5), xytext=(10.0, 4.9),
                arrowprops=dict(arrowstyle='->', color='#C77F1A', lw=1.8))
    ax.text(10.7, 5.2, '1 : N', fontsize=9, color='#C77F1A', fontweight='bold')

    # BorrowRecord (N) ── (1) Book
    ax.annotate('', xy=(5.1, 4.5), xytext=(6.9, 3.8),
                arrowprops=dict(arrowstyle='->', color='#7B3FA0', lw=1.8))
    ax.text(5.8, 4.5, 'N : 1', fontsize=9, color='#7B3FA0', fontweight='bold')

    # Reader (1) ── (N) CartItem
    ax.plot([10.0, 10.0, 15.5, 15.5], [3.5, 1.5, 1.5, 2.2],
            color='#B33A3A', lw=1.8, ls='--', zorder=2)
    ax.annotate('', xy=(15.5, 2.2), xytext=(15.5, 1.5),
                arrowprops=dict(arrowstyle='->', color='#B33A3A', lw=1.8))
    ax.text(12.5, 1.2, '1 : N', fontsize=9, color='#B33A3A', fontweight='bold')

    # CartItem (N) ── (1) Book
    ax.annotate('', xy=(5.1, 4.2), xytext=(13.9, 3.5),
                arrowprops=dict(arrowstyle='->', color='#B33A3A', lw=1.8, ls='dashed'))
    ax.text(9.0, 2.8, 'N : 1', fontsize=9, color='#B33A3A', fontweight='bold')

    save_fig(fig, 'fig_4_3_er_diagram')


# ======================================================================
# 图 4-4：借阅流程图
# ======================================================================

def draw_fig_4_4_borrow_flow():
    """绘制借阅流程图。"""
    fig, ax = plt.subplots(figsize=(10, 18))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 18)
    ax.axis('off')
    fig.suptitle('图 4-4  借阅流程图', fontsize=16, fontweight='bold', y=0.98)

    # 流程节点：从上到下
    x_center = 5.0
    y_step = 1.3

    nodes = [
        ('start',  '读者发起借阅',         None),
        ('proc',   '校验读者存在',         None),
        ('proc',   '校验图书存在',         None),
        ('dec',    'availableCopies > 0?', None),
        ('proc',   '统计当前借阅中数量',    None),
        ('dec',    '是否达到借阅上限?',     None),
        ('proc',   '扣减可借数量 (-1)',     None),
        ('proc',   '计算应还日期\n(学生+30天/教师+60天)', None),
        ('proc',   '创建借阅记录\n(status=BORROWING)', None),
        ('proc',   '读者借阅数 +1',        None),
        ('end',    '返回借阅记录',          None),
    ]

    y_positions = []
    y = 16.5
    for i, (ntype, text, _) in enumerate(nodes):
        y_positions.append(y)
        y -= y_step

    # 绘制节点
    for i, (ntype, text, _) in enumerate(nodes):
        cy = y_positions[i]
        if ntype == 'start' or ntype == 'end':
            add_box(ax, x_center, cy, 2.8, 0.7, text,
                    '#F5D6D6', '#B33A3A', fontsize=9, fontweight='bold',
                    rounding=0.35)
        elif ntype == 'proc':
            add_box(ax, x_center, cy, 3.5, 0.8, text,
                    '#D6E4F0', '#2E6FA7', fontsize=8.5, rounding=0.06)
        elif ntype == 'dec':
            add_diamond(ax, x_center, cy, 3.0, 1.0, text,
                        '#FCE8C8', '#C77F1A', fontsize=7.5)

    # 主流程箭头（纵向）
    for i in range(len(nodes) - 1):
        y_from = y_positions[i]
        y_to = y_positions[i + 1]
        ntype_from = nodes[i][0]
        ntype_to = nodes[i + 1][0]

        offset_from = -0.4 if ntype_from != 'dec' else -0.5
        offset_to = 0.4 if ntype_to != 'dec' else 0.5

        add_arrow(ax, (x_center, y_from + offset_from),
                  (x_center, y_to + offset_to),
                  color='#333333', lw=1.5, mutation=14)

    # 判断节点 "否" 分支（横向箭头 + 错误框）
    # availableCopies > 0? -> 否
    dec1_y = y_positions[3]
    add_box(ax, 8.5, dec1_y, 2.0, 0.6, '返回错误\n无可借副本',
            '#F0D0D0', '#A04040', fontsize=7.5, rounding=0.06)
    add_arrow(ax, (x_center + 1.5, dec1_y),
              (7.5, dec1_y),
              color='#A04040', lw=1.3, mutation=12)
    ax.text(x_center + 1.6, dec1_y + 0.15, '否', fontsize=8,
            color='#A04040', fontweight='bold')

    # 是否达到借阅上限? -> 是
    dec2_y = y_positions[5]
    add_box(ax, 8.5, dec2_y, 2.0, 0.6, '返回错误\n已达上限',
            '#F0D0D0', '#A04040', fontsize=7.5, rounding=0.06)
    add_arrow(ax, (x_center + 1.5, dec2_y),
              (7.5, dec2_y),
              color='#A04040', lw=1.3, mutation=12)
    ax.text(x_center + 1.6, dec2_y + 0.15, '是', fontsize=8,
            color='#A04040', fontweight='bold')

    # "是" / "否" 标注在主流程旁
    ax.text(x_center + 0.15, (dec1_y + y_positions[4]) / 2, '是',
            fontsize=8, color='#2E8B57', fontweight='bold')
    ax.text(x_center + 0.15, (dec2_y + y_positions[6]) / 2, '否',
            fontsize=8, color='#2E8B57', fontweight='bold')

    save_fig(fig, 'fig_4_4_borrow_flow')


# ======================================================================
# 图 4-5：借阅状态流转图
# ======================================================================

def draw_fig_4_5_borrow_status():
    """绘制借阅状态流转图。"""
    fig, ax = plt.subplots(figsize=(14, 7))
    ax.set_xlim(0, 14)
    ax.set_ylim(0, 7)
    ax.axis('off')
    fig.suptitle('图 4-5  借阅状态流转图', fontsize=16, fontweight='bold', y=0.97)

    # 三个状态框
    states = [
        (2.5, 3.5, 'BORROWING\n（借阅中）', '#D6E4F0', '#2E6FA7'),
        (7.0, 3.5, 'OVERDUE\n（已逾期）', '#F5D6D6', '#B33A3A'),
        (11.5, 3.5, 'RETURNED\n（已归还）', '#D5F0DC', '#2E8B57'),
    ]

    for cx, cy, text, fc, ec in states:
        add_box(ax, cx, cy, 2.8, 1.4, text,
                fc, ec, fontsize=12, fontweight='bold', rounding=0.15)

    # BORROWING -> OVERDUE
    add_arrow(ax, (3.9, 3.5), (5.6, 3.5),
              color='#B33A3A', lw=2.5, mutation=20)
    ax.text(4.75, 4.0, '超期未还\n（查询时动态修正）',
            ha='center', va='bottom', fontsize=8, color='#B33A3A',
            fontstyle='italic')

    # OVERDUE -> RETURNED
    add_arrow(ax, (8.4, 3.5), (10.1, 3.5),
              color='#2E8B57', lw=2.5, mutation=20)
    ax.text(9.25, 4.0, '归还\n（计算逾期罚款 0.5元/天）',
            ha='center', va='bottom', fontsize=8, color='#2E8B57',
            fontstyle='italic')

    # BORROWING -> RETURNED（直接归还）
    add_arrow(ax, (3.5, 2.7), (11.0, 2.7),
              color='#2E8B57', lw=2, mutation=18, ls='dashed')
    ax.text(7.25, 2.3, '正常归还（未逾期）',
            ha='center', va='top', fontsize=8, color='#2E8B57')

    # BORROWING 自续借（弧形箭头）
    add_arrow(ax, (2.5, 4.3), (2.5, 5.5),
              color='#2E6FA7', lw=2, mutation=18)
    add_arrow(ax, (2.5, 5.5), (1.2, 5.5),
              color='#2E6FA7', lw=2, mutation=18)
    add_arrow(ax, (1.2, 5.5), (1.2, 3.5),
              color='#2E6FA7', lw=2, mutation=18)
    add_arrow(ax, (1.2, 3.5), (1.1, 3.5),
              color='#2E6FA7', lw=2, mutation=18)
    ax.text(1.5, 5.9, '续借（延长15天，最多1次）',
            ha='center', va='bottom', fontsize=8, color='#2E6FA7',
            fontweight='bold')

    # 说明文字
    ax.text(7.0, 0.8,
            '说明：查询借阅记录时，系统会动态检查 dueDate 并将超期记录状态修正为 OVERDUE；'
            '\nBORROWING 状态可续借（延长 15 天，最多 1 次）；'
            '归还时若逾期则计算罚款（0.5 元/天）',
            ha='center', va='center', fontsize=9, color='#555555',
            bbox=dict(boxstyle='round,pad=0.5', facecolor='#FFFFF0',
                      edgecolor='#CCCCCC', linewidth=1))

    save_fig(fig, 'fig_4_5_borrow_status')


# ======================================================================
# 图 4-6：登录认证时序图
# ======================================================================

def draw_fig_4_6_login_sequence():
    """绘制登录认证时序图。"""
    fig, ax = plt.subplots(figsize=(16, 11))
    ax.set_xlim(0, 16)
    ax.set_ylim(0, 11)
    ax.axis('off')
    fig.suptitle('图 4-6  登录认证时序图', fontsize=16, fontweight='bold', y=0.97)

    # 参与者
    actors = [
        (1.5,  '用户浏览器'),
        (4.0,  '前端JS'),
        (6.5,  'AuthController'),
        (9.5,  'AuthService'),
        (12.0, 'ReaderRepository'),
        (14.5, 'HttpSession'),
    ]

    actor_y_top = 9.8
    actor_y_bot = 0.8

    for cx, name in actors:
        # 顶部参与者框
        add_box(ax, cx, actor_y_top, 2.0, 0.6, name,
                '#E6D6F0', '#7B3FA0', fontsize=8.5, fontweight='bold',
                rounding=0.1)
        # 生命线
        ax.plot([cx, cx], [actor_y_top - 0.3, actor_y_bot],
                color='#999999', lw=1.2, ls='--', zorder=1)
        # 底部参与者框
        add_box(ax, cx, actor_y_bot - 0.15, 2.0, 0.6, name,
                '#E6D6F0', '#7B3FA0', fontsize=8.5, fontweight='bold',
                rounding=0.1)

    # 时序消息
    messages = [
        (1.5,  4.0,  '1. 输入用户名密码',          8.8, True),
        (4.0,  6.5,  '2. POST /api/auth/login',    8.0, True),
        (6.5,  9.5,  '3. login(readerNo, pwd)',    7.2, True),
        (9.5,  12.0, '4. findByReaderNo()',         6.4, True),
        (12.0, 9.5,  '5. 返回 Reader 实体',         5.8, False),
        (9.5,  9.5,  '6. BCryptPasswordEncoder\n     .matches() 验证密码', 5.0, True),
        (9.5,  14.5, '7. setAttribute(userId, userRole)', 4.2, True),
        (9.5,  6.5,  '8. 返回 LoginResponse',       3.4, False),
        (6.5,  4.0,  '9. 200 JSON 响应',            2.6, False),
        (4.0,  1.5,  '10. 更新页面显示用户信息',     1.8, False),
    ]

    for (x_from, x_to, text, y, is_request) in messages:
        color = '#2E6FA7' if is_request else '#2E8B57'
        ls = 'solid' if is_request else 'dashed'
        style = '->' if is_request else '->'

        add_arrow(ax, (x_from, y), (x_to, y),
                  color=color, lw=1.5, style=style, ls=ls, mutation=14)

        # 消息文字
        mid_x = (x_from + x_to) / 2
        offset_y = 0.2
        ax.text(mid_x, y + offset_y, text,
                ha='center', va='bottom', fontsize=7,
                color=color, fontweight='bold')

    save_fig(fig, 'fig_4_6_login_sequence')


# ======================================================================
# 主函数
# ======================================================================

def main():
    """生成所有图片。"""
    print('=' * 60)
    print('校园图书借阅管理系统 - 系统设计图片生成')
    print('=' * 60)
    print(f'输出目录: {OUTPUT_DIR}')
    print()

    print('[1/6] 生成 系统架构图 ...')
    draw_fig_4_1_architecture()

    print('[2/6] 生成 功能模块图 ...')
    draw_fig_4_2_modules()

    print('[3/6] 生成 E-R 图 ...')
    draw_fig_4_3_er_diagram()

    print('[4/6] 生成 借阅流程图 ...')
    draw_fig_4_4_borrow_flow()

    print('[5/6] 生成 借阅状态流转图 ...')
    draw_fig_4_5_borrow_status()

    print('[6/6] 生成 登录认证时序图 ...')
    draw_fig_4_6_login_sequence()

    print()
    print('=' * 60)
    print('全部图片生成完毕！')
    print(f'输出目录: {OUTPUT_DIR}')
    # 列出生成的文件
    for f in sorted(os.listdir(OUTPUT_DIR)):
        fpath = os.path.join(OUTPUT_DIR, f)
        size_kb = os.path.getsize(fpath) / 1024
        print(f'  {f}  ({size_kb:.1f} KB)')
    print('=' * 60)


if __name__ == '__main__':
    main()
