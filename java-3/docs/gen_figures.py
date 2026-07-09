# -*- coding: utf-8 -*-
"""
课程设计论文第四章图表生成脚本
使用 matplotlib 绘制 6 张 SVG + PNG 图表
运行方式: python gen_figures.py
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch, Polygon
import numpy as np
import os

# 中文字体配置
plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'SimSun']
plt.rcParams['axes.unicode_minus'] = False

# 输出目录
OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'figures')
os.makedirs(OUTPUT_DIR, exist_ok=True)

# 颜色常量
COLOR_BROWSER = '#E8F5E9'
COLOR_FRONTEND = '#E3F2FD'
COLOR_CONTROLLER = '#FFF3E0'
COLOR_SERVICE = '#F3E5F5'
COLOR_REPO = '#ECEFF1'
COLOR_DB = '#FFF9C4'
COLOR_BORDER = '#455A64'
COLOR_ARROW = '#37474F'
COLOR_DIAMOND = '#FFF9C4'
COLOR_ERROR = '#FFCDD2'
COLOR_START = '#C8E6C9'
COLOR_END = '#C8E6C9'


def save_figure(fig, name):
    """同时保存 SVG 和 PNG 格式"""
    svg_path = os.path.join(OUTPUT_DIR, f'{name}.svg')
    png_path = os.path.join(OUTPUT_DIR, f'{name}.png')
    fig.savefig(svg_path, format='svg', bbox_inches='tight', pad_inches=0.1)
    fig.savefig(png_path, format='png', dpi=150, bbox_inches='tight', pad_inches=0.1)
    plt.close(fig)
    print(f'已生成: {name}.svg + {name}.png')


def draw_box(ax, x, y, w, h, text, color, fontsize=9, bold=False):
    """绘制圆角矩形节点"""
    box = FancyBboxPatch((x - w/2, y - h/2), w, h,
                          boxstyle='round,pad=0.1',
                          facecolor=color, edgecolor=COLOR_BORDER, linewidth=1.2)
    ax.add_patch(box)
    weight = 'bold' if bold else 'normal'
    ax.text(x, y, text, ha='center', va='center', fontsize=fontsize, fontweight=weight, wrap=True)


def draw_arrow(ax, x1, y1, x2, y2, text='', color=COLOR_ARROW, style='->'):
    """绘制箭头"""
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle=style, color=color, lw=1.5))
    if text:
        mx, my = (x1 + x2) / 2, (y1 + y2) / 2
        ax.text(mx + 0.15, my, text, fontsize=8, color=color, ha='left', va='center')


def draw_diamond(ax, x, y, w, h, text, color=COLOR_DIAMOND, fontsize=8):
    """绘制菱形判断框"""
    diamond = Polygon([(x, y + h/2), (x + w/2, y), (x, y - h/2), (x - w/2, y)],
                       facecolor=color, edgecolor=COLOR_BORDER, linewidth=1.2)
    ax.add_patch(diamond)
    ax.text(x, y, text, ha='center', va='center', fontsize=fontsize)


# ==================== 图4-1 系统架构图 ====================
def draw_architecture():
    fig, ax = plt.subplots(1, 1, figsize=(10, 6))
    ax.set_xlim(-0.5, 10.5)
    ax.set_ylim(-0.5, 11)
    ax.axis('off')
    ax.set_title('图4-1 系统架构图', fontsize=14, fontweight='bold', pad=10)

    layers = [
        (5.0, 10.0, 8.0, 1.0, '浏览器（Chrome / Firefox / Edge）', COLOR_BROWSER),
        (5.0, 8.5, 8.0, 1.0, '前端 SPA（index.html / admin.html）\n原生 HTML + CSS + JavaScript + fetch API', COLOR_FRONTEND),
        (5.0, 7.0, 8.0, 1.0, 'Controller 层（10 个 REST 控制器）\nAuthController / ProductController / OrderController 等', COLOR_CONTROLLER),
        (5.0, 5.5, 8.0, 1.0, 'Service 层（12 个业务服务）\nAuthService / ProductService / OrderService 等', COLOR_SERVICE),
        (5.0, 4.0, 8.0, 1.0, 'Repository 层（13 个 JPA 仓储接口）\nSpring Data JPA 方法名派生查询', COLOR_REPO),
        (5.0, 2.5, 8.0, 1.0, 'H2 文件数据库（data/campusdb.mv.db）\n13 张数据表 + 3 个枚举类型', COLOR_DB),
    ]

    for x, y, w, h, text, color in layers:
        draw_box(ax, x, y, w, h, text, color, fontsize=9, bold=False)

    # 层间箭头
    for i in range(len(layers) - 1):
        y_top = layers[i][1] - layers[i][3] / 2
        y_bot = layers[i+1][1] + layers[i+1][3] / 2
        draw_arrow(ax, 5.0, y_top, 5.0, y_bot)

    # 横切关注点标注
    draw_box(ax, 1.0, 6.25, 1.6, 0.7, 'LoginInterceptor\n登录拦截器', '#FFEBEE', fontsize=7)
    draw_box(ax, 9.0, 6.25, 1.6, 0.7, 'DTO\n数据传输对象', '#E8EAF6', fontsize=7)
    ax.annotate('', xy=(2.0, 6.25), xytext=(3.5, 6.25),
                arrowprops=dict(arrowstyle='->', color='#E53935', lw=1, ls='--'))
    ax.annotate('', xy=(8.0, 6.25), xytext=(6.5, 6.25),
                arrowprops=dict(arrowstyle='->', color='#3949AB', lw=1, ls='--'))

    # HTTP 标注
    ax.text(5.6, 9.25, 'HTTP/HTTPS', fontsize=7, color='#2E7D32', ha='left')
    ax.text(5.6, 7.75, 'RESTful API (JSON)', fontsize=7, color='#1565C0', ha='left')

    save_figure(fig, 'fig_4_1_architecture')


# ==================== 图4-2 功能模块图 ====================
def draw_modules():
    fig, ax = plt.subplots(1, 1, figsize=(12, 7))
    ax.set_xlim(-0.5, 12)
    ax.set_ylim(-0.5, 8)
    ax.axis('off')
    ax.set_title('图4-2 系统功能模块图', fontsize=14, fontweight='bold', pad=10)

    # 顶层
    draw_box(ax, 6.0, 7.3, 5.0, 0.8, '大学生闲置二手物品交易网站', '#C8E6C9', fontsize=11, bold=True)

    # 一级模块
    modules = [
        (1.2, 5.8, '用户认证\n模块', '#E3F2FD'),
        (3.2, 5.8, '商品管理\n模块', '#FFF3E0'),
        (5.2, 5.8, '订单交易\n模块', '#F3E5F5'),
        (7.2, 5.8, '消息互动\n模块', '#E8F5E9'),
        (9.2, 5.8, '公告反馈\n模块', '#FFF9C4'),
        (11.0, 5.8, '后台管理\n模块', '#FFEBEE'),
    ]

    for x, y, text, color in modules:
        draw_box(ax, x, y, 1.6, 0.9, text, color, fontsize=9, bold=True)

    # 顶层到一级模块连线
    for x, _, _, _ in modules:
        ax.plot([6.0, x], [6.9, 6.25], color=COLOR_BORDER, lw=1, ls='-')

    # 子功能
    sub_modules = [
        # 用户认证
        (1.2, 4.3, '注册/登录\n学号实名\n修改密码\n资料管理', '#E3F2FD'),
        # 商品管理
        (3.2, 4.3, '发布商品\n分类浏览\n关键词搜索\n点赞收藏', '#FFF3E0'),
        # 订单交易
        (5.2, 4.3, '创建订单\n确认付款\n确认完成\n取消订单', '#F3E5F5'),
        # 消息互动
        (7.2, 4.3, '私信沟通\n商品评论\n自提预约\n交易评价', '#E8F5E9'),
        # 公告反馈
        (9.2, 4.3, '平台公告\n意见反馈\n反馈记录', '#FFF9C4'),
        # 后台管理
        (11.0, 4.3, '用户管理\n商品审核\n订单管理\n公告/分类\n数据统计', '#FFEBEE'),
    ]

    for x, y, text, color in sub_modules:
        draw_box(ax, x, y, 1.6, 1.6, text, color, fontsize=7.5)

    # 一级模块到子功能连线
    for x, _, _, _ in modules:
        ax.plot([x, x], [5.35, 5.1], color=COLOR_BORDER, lw=1, ls='-')

    save_figure(fig, 'fig_4_2_modules')


# ==================== 图4-3 E-R图 ====================
def draw_er_diagram():
    fig, ax = plt.subplots(1, 1, figsize=(14, 9))
    ax.set_xlim(-0.5, 14)
    ax.set_ylim(-0.5, 10)
    ax.axis('off')
    ax.set_title('图4-3 数据库E-R图', fontsize=14, fontweight='bold', pad=10)

    entities = [
        # (x, y, name, fields, color)
        (2.0, 8.5, 'User (users)', 'studentId, username, password\nnickname, phone, avatar\nstatus, createdAt', '#E3F2FD'),
        (7.0, 8.5, 'AdminUser\n(admin_users)', 'username, password\ndisplayName, createdAt', '#FFEBEE'),
        (12.0, 8.5, 'ProductCategory\n(categories)', 'name, icon, sortOrder\ncreatedAt', '#FFF9C4'),

        (2.0, 5.5, 'Product\n(products)', 'title, description, price\nconditionLevel, images\nsellerId, categoryId\nauditStatus, sold, createdAt', '#FFF3E0'),
        (7.0, 5.5, 'Order\n(orders)', 'orderNo, productId\nbuyerId, sellerId, price\nstatus, buyerRemark\ndisputeRemark, createdAt', '#F3E5F5'),
        (12.0, 5.5, 'Review\n(reviews)', 'orderId, reviewerId\nrevieweeId, rating\ncontent, createdAt', '#E8F5E9'),

        (2.0, 2.5, 'Favorite\n(favorites)', 'userId, productId\ncreatedAt', '#E0F7FA'),
        (5.0, 2.5, 'ProductLike\n(product_likes)', 'userId, productId\ncreatedAt', '#E0F7FA'),
        (8.0, 2.5, 'Message\n(messages)', 'senderId, receiverId\nproductId, content\nread, createdAt', '#E8EAF6'),
        (10.5, 2.5, 'Comment\n(comments)', 'productId, userId\ncontent, parentId\ncreatedAt', '#E8EAF6'),
        (12.5, 2.5, 'Appointment\n(appointments)', 'orderId, buyerId, sellerId\nappointmentTime, location\nremark, createdAt', '#FFF3E0'),
    ]

    for x, y, name, fields, color in entities:
        # 实体标题框
        box = FancyBboxPatch((x - 1.2, y - 0.9), 2.4, 1.8,
                              boxstyle='round,pad=0.05',
                              facecolor=color, edgecolor=COLOR_BORDER, linewidth=1.2)
        ax.add_patch(box)
        # 分隔线
        ax.plot([x - 1.2, x + 1.2], [y + 0.5, y + 0.5], color=COLOR_BORDER, lw=0.8)
        # 标题
        ax.text(x, y + 0.72, name, ha='center', va='center', fontsize=7.5, fontweight='bold')
        # 属性
        ax.text(x, y - 0.2, fields, ha='center', va='center', fontsize=6)

    # 关系连线
    # User 1:N Product (卖家发布)
    ax.annotate('', xy=(2.0, 6.4), xytext=(2.0, 7.6),
                arrowprops=dict(arrowstyle='->', color='#1565C0', lw=1.2))
    ax.text(2.5, 7.0, '1:N\n发布', fontsize=6, color='#1565C0')

    # User 1:N Order (买家)
    ax.annotate('', xy=(5.8, 6.4), xytext=(2.8, 7.6),
                arrowprops=dict(arrowstyle='->', color='#2E7D32', lw=1.2))
    ax.text(3.5, 7.2, '1:N\n购买', fontsize=6, color='#2E7D32')

    # Product 1:N Order
    ax.annotate('', xy=(5.8, 5.5), xytext=(3.2, 5.5),
                arrowprops=dict(arrowstyle='->', color='#E65100', lw=1.2))
    ax.text(4.5, 5.8, '1:N', fontsize=6, color='#E65100')

    # ProductCategory 1:N Product
    ax.annotate('', xy=(3.2, 5.8), xytext=(10.8, 8.2),
                arrowprops=dict(arrowstyle='->', color='#F57F17', lw=1.0, ls='--'))
    ax.text(7.0, 7.2, '1:N 分类', fontsize=6, color='#F57F17')

    # Order 1:1 Review
    ax.annotate('', xy=(10.8, 5.5), xytext=(8.2, 5.5),
                arrowprops=dict(arrowstyle='->', color='#6A1B9A', lw=1.2))
    ax.text(9.5, 5.8, '1:1 评价', fontsize=6, color='#6A1B9A')

    # Product 1:N Favorite
    ax.annotate('', xy=(2.0, 3.4), xytext=(2.0, 4.6),
                arrowprops=dict(arrowstyle='->', color='#00838F', lw=1.0))
    ax.text(2.5, 4.0, '1:N\n收藏', fontsize=6, color='#00838F')

    # Product 1:N ProductLike
    ax.annotate('', xy=(4.5, 3.4), xytext=(3.0, 4.6),
                arrowprops=dict(arrowstyle='->', color='#00838F', lw=1.0))
    ax.text(4.0, 4.0, '1:N\n点赞', fontsize=6, color='#00838F')

    # Product 1:N Comment
    ax.annotate('', xy=(9.8, 3.4), xytext=(3.0, 4.6),
                arrowprops=dict(arrowstyle='->', color='#283593', lw=1.0, ls='--'))
    ax.text(6.5, 3.8, '1:N 评论', fontsize=6, color='#283593')

    # Order 1:1 Appointment
    ax.annotate('', xy=(11.8, 3.4), xytext=(7.5, 4.6),
                arrowprops=dict(arrowstyle='->', color='#BF360C', lw=1.0, ls='--'))
    ax.text(10.0, 4.2, '1:1 预约', fontsize=6, color='#BF360C')

    save_figure(fig, 'fig_4_3_er_diagram')


# ==================== 图4-4 订单状态流转图 ====================
def draw_order_flow():
    fig, ax = plt.subplots(1, 1, figsize=(10, 5))
    ax.set_xlim(-0.5, 10.5)
    ax.set_ylim(-0.5, 5)
    ax.axis('off')
    ax.set_title('图4-4 订单状态流转图', fontsize=14, fontweight='bold', pad=10)

    # 状态节点
    draw_box(ax, 1.5, 3.0, 2.0, 0.9, 'PENDING\n待付款', COLOR_START, fontsize=10, bold=True)
    draw_box(ax, 5.0, 3.0, 2.0, 0.9, 'PAID\n已付款', '#FFF3E0', fontsize=10, bold=True)
    draw_box(ax, 8.5, 3.0, 2.0, 0.9, 'COMPLETED\n已完成', COLOR_END, fontsize=10, bold=True)
    draw_box(ax, 5.0, 0.8, 2.0, 0.9, 'CANCELLED\n已取消', COLOR_ERROR, fontsize=10, bold=True)

    # 正向流转箭头
    draw_arrow(ax, 2.5, 3.0, 4.0, 3.0, '买家确认付款')
    draw_arrow(ax, 6.0, 3.0, 7.5, 3.0, '确认完成\n(买卖双方)')

    # 取消流转箭头
    draw_arrow(ax, 1.5, 2.55, 4.2, 1.25, '取消订单', color='#E53935')
    draw_arrow(ax, 5.8, 2.55, 5.8, 1.25, '取消订单', color='#E53935')

    # 标注终态
    ax.text(8.5, 2.2, '终态', fontsize=8, color='#2E7D32', ha='center', fontweight='bold')
    ax.text(5.0, 0.05, '终态', fontsize=8, color='#C62828', ha='center', fontweight='bold')

    # 商品状态标注
    ax.text(9.5, 3.9, '商品标记\n为已售出', fontsize=7, color='#E65100', ha='center',
            bbox=dict(boxstyle='round,pad=0.2', facecolor='#FFF3E0', edgecolor='#E65100', alpha=0.8))

    save_figure(fig, 'fig_4_4_order_flow')


# ==================== 图4-5 交易流程图 ====================
def draw_trade_process():
    fig, ax = plt.subplots(1, 1, figsize=(10, 10))
    ax.set_xlim(-0.5, 10.5)
    ax.set_ylim(-0.5, 11)
    ax.axis('off')
    ax.set_title('图4-5 二手物品交易流程图', fontsize=14, fontweight='bold', pad=10)

    steps = [
        (5.0, 10.2, 3.0, 0.7, '开始', COLOR_START, 'oval'),
        (5.0, 9.0, 3.5, 0.7, '学生注册（学号实名认证）', '#E3F2FD', 'rect'),
        (5.0, 7.8, 3.5, 0.7, '登录系统', '#E3F2FD', 'rect'),
        (5.0, 6.6, 3.5, 0.7, '发布闲置商品（PENDING）', '#FFF3E0', 'rect'),
        (5.0, 5.2, 2.5, 1.0, '管理员\n审核？', COLOR_DIAMOND, 'diamond'),
        (1.8, 5.2, 2.0, 0.7, 'REJECTED\n驳回', COLOR_ERROR, 'rect'),
        (5.0, 3.8, 3.5, 0.7, '买家浏览/搜索商品', '#FFF3E0', 'rect'),
        (5.0, 2.6, 3.5, 0.7, '创建订单（PENDING）', '#F3E5F5', 'rect'),
        (5.0, 1.4, 3.5, 0.7, '确认付款 → 确认完成\n→ 评价卖家', '#F3E5F5', 'rect'),
        (5.0, 0.3, 3.0, 0.7, '结束', COLOR_END, 'oval'),
    ]

    for x, y, w, h, text, color, shape in steps:
        if shape == 'oval':
            from matplotlib.patches import Ellipse
            ell = Ellipse((x, y), w, h, facecolor=color, edgecolor=COLOR_BORDER, linewidth=1.2)
            ax.add_patch(ell)
            ax.text(x, y, text, ha='center', va='center', fontsize=9, fontweight='bold')
        elif shape == 'diamond':
            draw_diamond(ax, x, y, w, h, text, color, fontsize=8)
        else:
            draw_box(ax, x, y, w, h, text, color, fontsize=9)

    # 箭头连接
    draw_arrow(ax, 5.0, 9.85, 5.0, 9.35)
    draw_arrow(ax, 5.0, 8.65, 5.0, 8.15)
    draw_arrow(ax, 5.0, 7.45, 5.0, 6.95)
    draw_arrow(ax, 5.0, 6.25, 5.0, 5.7)
    # 审核通过
    draw_arrow(ax, 5.0, 4.7, 5.0, 4.15, '通过', color='#2E7D32')
    # 审核驳回
    draw_arrow(ax, 3.75, 5.2, 2.8, 5.2, '驳回', color='#C62828')
    # 驳回到结束
    ax.plot([1.8, 1.8], [4.85, 0.3], color='#C62828', lw=1, ls='--')
    draw_arrow(ax, 1.8, 0.3, 3.5, 0.3, color='#C62828')
    # 继续
    draw_arrow(ax, 5.0, 3.45, 5.0, 2.95)
    draw_arrow(ax, 5.0, 2.25, 5.0, 1.75)
    draw_arrow(ax, 5.0, 1.05, 5.0, 0.65)

    save_figure(fig, 'fig_4_5_trade_process')


# ==================== 图4-6 登录认证时序图 ====================
def draw_login_sequence():
    fig, ax = plt.subplots(1, 1, figsize=(10, 7))
    ax.set_xlim(-0.5, 10.5)
    ax.set_ylim(-0.5, 8)
    ax.axis('off')
    ax.set_title('图4-6 用户登录认证时序图', fontsize=14, fontweight='bold', pad=10)

    # 参与者
    actors = [
        (1.5, '用户/浏览器'),
        (4.5, 'AuthController'),
        (7.5, 'AuthService'),
        (10.0, 'UserRepository\n/H2数据库'),
    ]

    for x, name in actors:
        draw_box(ax, x, 7.3, 2.0, 0.6, name, '#E3F2FD', fontsize=8, bold=True)
        # 生命线
        ax.plot([x, x], [7.0, 0.3], color='#90A4AE', lw=1, ls='--')

    # 消息
    messages = [
        (1.5, 4.5, 6.3, '1. POST /api/auth/login (学号+密码)', '#1565C0'),
        (4.5, 7.5, 5.5, '2. login(studentId, password)', '#2E7D32'),
        (7.5, 10.0, 4.7, '3. findByStudentId(studentId)', '#E65100'),
        (10.0, 7.5, 3.9, '4. User实体 (含BCrypt密码哈希)', '#6A1B9A'),
        (7.5, 4.5, 3.1, '5. passwordEncoder.matches()\n+ 验证通过', '#2E7D32'),
        (4.5, 1.5, 2.3, '6. Session.setAttribute(SESSION_USER_KEY)\n+ 返回LoginResponse', '#1565C0'),
    ]

    for x1, x2, y, text, color in messages:
        ax.annotate('', xy=(x2, y), xytext=(x1, y),
                     arrowprops=dict(arrowstyle='->', color=color, lw=1.5))
        mx = (x1 + x2) / 2
        ax.text(mx, y + 0.2, text, fontsize=7.5, ha='center', va='bottom', color=color)

    # 激活条
    for x, name in actors:
        ax.add_patch(mpatches.Rectangle((x - 0.1, 2.3), 0.2, 4.0,
                                         facecolor='#FFF9C4', edgecolor='#F57F17', alpha=0.3))

    save_figure(fig, 'fig_4_6_login_sequence')


# ==================== 主函数 ====================
if __name__ == '__main__':
    print('开始生成图表...')
    draw_architecture()
    draw_modules()
    draw_er_diagram()
    draw_order_flow()
    draw_trade_process()
    draw_login_sequence()
    print(f'\n全部 6 张图表已生成至: {OUTPUT_DIR}')
