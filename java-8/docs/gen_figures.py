# -*- coding: utf-8 -*-
"""
第四章图表生成脚本
生成SVG文件并转换为PNG格式
"""
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
import numpy as np
import os

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei', 'SimSun']
plt.rcParams['axes.unicode_minus'] = False

OUTPUT_DIR = os.path.dirname(os.path.abspath(__file__))
FIG_DIR = os.path.join(OUTPUT_DIR, 'figures')
os.makedirs(FIG_DIR, exist_ok=True)


def save_figure(fig, name):
    """保存为SVG和PNG两种格式"""
    svg_path = os.path.join(FIG_DIR, f'{name}.svg')
    png_path = os.path.join(FIG_DIR, f'{name}.png')
    fig.savefig(svg_path, format='svg', bbox_inches='tight', dpi=150)
    fig.savefig(png_path, format='png', bbox_inches='tight', dpi=150)
    plt.close(fig)
    print(f"已生成: {svg_path}")
    print(f"已生成: {png_path}")


def draw_architecture():
    """图4-1 系统架构图"""
    fig, ax = plt.subplots(1, 1, figsize=(10, 7))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 8)
    ax.axis('off')
    ax.set_title('图4-1 系统架构图', fontsize=14, fontweight='bold', pad=15)

    layers = [
        (6.5, '表现层（前端页面）', '#E8F5E9', 'index.html（用户端）  |  admin.html（管理后台）'),
        (5.2, '控制层（Controller）', '#E3F2FD', 'AuthController  UserController  FabricController  StyleController\nMeasurementController  OrderController  AdminController'),
        (3.9, '业务逻辑层（Service）', '#FFF3E0', 'AuthService  UserService  FabricService  StyleService\nMeasurementService  OrderService'),
        (2.6, '数据访问层（Repository）', '#F3E5F5', 'AdminUserRepository  UserRepository  FabricRepository  StyleRepository\nMeasurementRepository  OrderRepository'),
        (1.3, '数据层（H2 Database）', '#FFEBEE', 'H2文件数据库（tailordb）  |  JPA / Hibernate ORM'),
    ]

    for y, title, color, content in layers:
        box = FancyBboxPatch((1, y - 0.5), 8, 1.0,
                             boxstyle="round,pad=0.1",
                             facecolor=color, edgecolor='#333333', linewidth=1.5)
        ax.add_patch(box)
        ax.text(5, y + 0.15, title, ha='center', va='center',
                fontsize=11, fontweight='bold')
        ax.text(5, y - 0.2, content, ha='center', va='center',
                fontsize=8, color='#555555')

    # 添加层间箭头
    for y_start in [6.0, 4.7, 3.4, 2.1]:
        arrow = FancyArrowPatch((5, y_start), (5, y_start - 0.5),
                                arrowstyle='->', mutation_scale=15,
                                color='#666666', linewidth=1.5)
        ax.add_patch(arrow)

    # 横向拦截器标注
    ax.annotate('LoginInterceptor\n（请求拦截与权限校验）',
                xy=(9.2, 5.2), fontsize=7, ha='center', va='center',
                bbox=dict(boxstyle='round,pad=0.3', facecolor='#FFF9C4', edgecolor='#F9A825'))

    save_figure(fig, 'fig_4_1_architecture')


def draw_modules():
    """图4-2 功能模块图"""
    fig, ax = plt.subplots(1, 1, figsize=(12, 7))
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 7)
    ax.axis('off')
    ax.set_title('图4-2 系统功能模块图', fontsize=14, fontweight='bold', pad=15)

    # 顶层系统框
    top_box = FancyBboxPatch((4, 5.8), 4, 0.8,
                             boxstyle="round,pad=0.1",
                             facecolor='#1565C0', edgecolor='#0D47A1', linewidth=2)
    ax.add_patch(top_box)
    ax.text(6, 6.2, '服装私人定制网站', ha='center', va='center',
            fontsize=12, fontweight='bold', color='white')

    # 一级模块
    modules = [
        (1.0, '用户管理\n模块', '#E8F5E9', '注册/登录\n个人信息\n修改密码'),
        (3.2, '面料管理\n模块', '#E3F2FD', '面料浏览\n面料筛选\n面料增删改'),
        (5.4, '款式管理\n模块', '#FFF3E0', '款式浏览\n款式搜索\n款式增删改'),
        (7.6, '量体数据\n模块', '#F3E5F5', '量体录入\n量体编辑\n量体删除'),
        (9.8, '订单管理\n模块', '#FFEBEE', '用户下单\n订单查询\n状态推进'),
    ]

    for x, title, color, content in modules:
        # 一级模块框
        box = FancyBboxPatch((x, 4.2), 1.8, 1.0,
                             boxstyle="round,pad=0.1",
                             facecolor=color, edgecolor='#333333', linewidth=1.5)
        ax.add_patch(box)
        ax.text(x + 0.9, 4.7, title, ha='center', va='center',
                fontsize=9, fontweight='bold')

        # 连接线到顶层
        ax.plot([x + 0.9, 6], [5.2, 5.8], 'k-', linewidth=0.8)

        # 子功能框
        sub_box = FancyBboxPatch((x, 2.5), 1.8, 1.3,
                                 boxstyle="round,pad=0.05",
                                 facecolor='white', edgecolor='#999999', linewidth=1)
        ax.add_patch(sub_box)
        ax.text(x + 0.9, 3.15, content, ha='center', va='center',
                fontsize=7.5, color='#333333')

        # 连接线
        ax.plot([x + 0.9, x + 0.9], [4.2, 3.8], 'k-', linewidth=0.8)

    # 后台管理模块
    admin_box = FancyBboxPatch((4, 0.5), 4, 1.0,
                               boxstyle="round,pad=0.1",
                               facecolor='#FFF9C4', edgecolor='#F57F17', linewidth=1.5)
    ax.add_patch(admin_box)
    ax.text(6, 1.0, '后台管理模块（仪表盘统计 · 面料/款式/订单/用户管理）',
            ha='center', va='center', fontsize=9, fontweight='bold')

    # 连接线
    ax.plot([6, 6], [2.5, 1.5], 'k--', linewidth=0.8)

    save_figure(fig, 'fig_4_2_modules')


def draw_er_diagram():
    """图4-3 E-R图"""
    fig, ax = plt.subplots(1, 1, figsize=(12, 8))
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 8)
    ax.axis('off')
    ax.set_title('图4-3 数据库E-R图', fontsize=14, fontweight='bold', pad=15)

    entities = {
        'AdminUser': (1.5, 6.5, '#FFCDD2', ['id(PK)', 'username', 'password', 'nickname', 'lastLoginAt', 'createTime']),
        'User': (5.0, 6.5, '#C8E6C9', ['id(PK)', 'username', 'password', 'nickname', 'phone', 'lastLoginAt', 'createTime']),
        'Fabric': (9.0, 6.5, '#BBDEFB', ['id(PK)', 'name', 'material', 'color', 'unitPrice', 'stock', 'description', 'createTime']),
        'Style': (9.0, 3.5, '#FFE0B2', ['id(PK)', 'name', 'category', 'craftFee', 'description', 'createTime']),
        'Measurement': (5.0, 3.5, '#E1BEE7', ['id(PK)', 'user_id(FK)', 'name', 'height', 'weight', 'shoulderWidth', 'chestCircumference', '...(共14项)', 'createTime']),
        'Order': (2.0, 3.5, '#FFCDD2', ['id(PK)', 'user_id(FK)', 'style_id(FK)', 'fabric_id(FK)', 'measurement_id(FK)', 'totalPrice', 'status', 'remark', 'createTime']),
    }

    # 绘制实体框
    for name, (x, y, color, attrs) in entities.items():
        w, h = 2.2, 0.4 + len(attrs) * 0.22
        # 实体标题框
        header = FancyBboxPatch((x - w/2, y - h), w, 0.35,
                                boxstyle="round,pad=0.02",
                                facecolor=color, edgecolor='#333333', linewidth=1.5)
        ax.add_patch(header)
        ax.text(x, y - h + 0.17, name, ha='center', va='center',
                fontsize=9, fontweight='bold')

        # 属性框
        attr_box = FancyBboxPatch((x - w/2, y - h - 0.35 - (len(attrs)-1)*0.22), w, h - 0.35,
                                  boxstyle="round,pad=0.02",
                                  facecolor='white', edgecolor='#333333', linewidth=1)
        ax.add_patch(attr_box)

        for i, attr in enumerate(attrs):
            ax.text(x - w/2 + 0.1, y - h - 0.15 - i * 0.22,
                    attr, ha='left', va='center', fontsize=6.5)

    # 绘制关系连线
    relationships = [
        # (x1, y1, x2, y2, label, offset)
        (5.0, 6.5 - 0.4 - 7*0.22 - 0.35, 5.0, 3.5, '1:N', (5.3, 5.0)),  # User -> Measurement
        (5.0 - 2.2/2, 6.5 - 0.4 - 7*0.22 - 0.35 + 0.5, 2.0 + 2.2/2, 3.5, '1:N', (3.0, 5.0)),  # User -> Order
        (9.0 - 2.2/2, 6.5 - 0.4 - 8*0.22 - 0.35 + 1.0, 2.0 + 2.2/2, 3.5 - 0.4, '1:N', (5.5, 2.5)),  # Fabric -> Order
        (9.0 - 2.2/2, 3.5, 2.0 + 2.2/2, 3.5 - 0.4, '1:N', (5.5, 3.7)),  # Style -> Order (top)
        (5.0, 3.5 - 0.4 - 9*0.22 - 0.35, 2.0, 3.5 - 0.4, '1:N', (3.5, 1.0)),  # Measurement -> Order
    ]

    for x1, y1, x2, y2, label, (lx, ly) in relationships:
        ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                    arrowprops=dict(arrowstyle='->', color='#666666', linewidth=1))
        ax.text(lx, ly, label, fontsize=7, ha='center', va='center',
                bbox=dict(boxstyle='round,pad=0.1', facecolor='#FFF9C4', edgecolor='#F9A825'))

    save_figure(fig, 'fig_4_3_er_diagram')


def draw_order_flow():
    """图4-4 订单状态流转图"""
    fig, ax = plt.subplots(1, 1, figsize=(12, 4))
    ax.set_xlim(0, 12)
    ax.set_ylim(0, 4)
    ax.axis('off')
    ax.set_title('图4-4 订单状态流转图', fontsize=14, fontweight='bold', pad=15)

    states = [
        (1.0, 'PENDING\n待确认', '#FFCDD2'),
        (3.0, 'MEASURING\n量体中', '#FFE0B2'),
        (5.0, 'CUTTING\n裁剪中', '#FFF9C4'),
        (7.0, 'SEWING\n缝制中', '#C8E6C9'),
        (9.0, 'FITTING\n试衣中', '#BBDEFB'),
        (11.0, 'COMPLETED\n已完成', '#B2DFDB'),
    ]

    # 绘制状态节点
    for x, label, color in states:
        circle = FancyBboxPatch((x - 0.7, 1.5), 1.4, 1.0,
                                boxstyle="round,pad=0.15",
                                facecolor=color, edgecolor='#333333', linewidth=1.5)
        ax.add_patch(circle)
        ax.text(x, 2.0, label, ha='center', va='center',
                fontsize=8, fontweight='bold')

    # 绘制箭头
    for i in range(len(states) - 1):
        x1 = states[i][0] + 0.7
        x2 = states[i+1][0] - 0.7
        arrow = FancyArrowPatch((x1, 2.0), (x2, 2.0),
                                arrowstyle='->', mutation_scale=18,
                                color='#333333', linewidth=1.5)
        ax.add_patch(arrow)
        ax.text((x1 + x2) / 2, 2.35, '推进', ha='center', va='center',
                fontsize=7, color='#666666')

    # 终止标记
    ax.text(11.0, 0.8, '终态（不可变更）', ha='center', va='center',
            fontsize=8, color='#C62828', style='italic')

    save_figure(fig, 'fig_4_4_order_flow')


def draw_order_process():
    """图4-5 用户下单流程图"""
    fig, ax = plt.subplots(1, 1, figsize=(10, 8))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 10)
    ax.axis('off')
    ax.set_title('图4-5 用户下单流程图', fontsize=14, fontweight='bold', pad=15)

    steps = [
        (5, 9, '用户注册/登录', '#E8F5E9', 'ellipse'),
        (5, 7.8, '录入量体数据', '#E3F2FD', 'ellipse'),
        (5, 6.6, '浏览面料库', '#FFF3E0', 'ellipse'),
        (5, 5.4, '浏览款式库', '#F3E5F5', 'ellipse'),
        (5, 4.2, '选择款式+面料+量体', '#FFEBEE', 'box'),
        (5, 3.0, '系统计算总价\n（工费+面料单价×3米）', '#FFF9C4', 'box'),
        (5, 1.8, '生成PENDING订单', '#C8E6C9', 'box'),
        (5, 0.6, '查看订单进度', '#BBDEFB', 'ellipse'),
    ]

    for x, y, label, color, shape in steps:
        if shape == 'ellipse':
            box = FancyBboxPatch((x - 1.8, y - 0.35), 3.6, 0.7,
                                 boxstyle="round,pad=0.2",
                                 facecolor=color, edgecolor='#333333', linewidth=1.5)
        else:
            box = FancyBboxPatch((x - 1.8, y - 0.4), 3.6, 0.8,
                                 boxstyle="round,pad=0.05",
                                 facecolor=color, edgecolor='#333333', linewidth=1.5)
        ax.add_patch(box)
        ax.text(x, y, label, ha='center', va='center',
                fontsize=9, fontweight='bold')

    # 箭头
    for i in range(len(steps) - 1):
        y1 = steps[i][1] - 0.4
        y2 = steps[i+1][1] + 0.4
        arrow = FancyArrowPatch((5, y1), (5, y2),
                                arrowstyle='->', mutation_scale=15,
                                color='#333333', linewidth=1.2)
        ax.add_patch(arrow)

    # 判断分支
    ax.text(7.5, 4.2, '是否已\n登录？', fontsize=7, ha='center', va='center',
            bbox=dict(boxstyle='round,pad=0.2', facecolor='#FFCDD2', edgecolor='#C62828'))
    ax.annotate('否→跳转登录', xy=(7.5, 4.2), xytext=(8.5, 5.5),
                fontsize=7, color='#C62828',
                arrowprops=dict(arrowstyle='->', color='#C62828', linewidth=1))

    save_figure(fig, 'fig_4_5_order_process')


def draw_login_sequence():
    """图4-6 登录认证时序图"""
    fig, ax = plt.subplots(1, 1, figsize=(10, 6))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 8)
    ax.axis('off')
    ax.set_title('图4-6 用户登录认证时序图', fontsize=14, fontweight='bold', pad=15)

    # 参与者
    actors = [
        (1.5, '用户/浏览器'),
        (4.0, 'Controller'),
        (6.5, 'Service'),
        (9.0, 'Repository/DB'),
    ]

    for x, name in actors:
        box = FancyBboxPatch((x - 0.8, 7), 1.6, 0.5,
                             boxstyle="round,pad=0.1",
                             facecolor='#E3F2FD', edgecolor='#1565C0', linewidth=1.5)
        ax.add_patch(box)
        ax.text(x, 7.25, name, ha='center', va='center', fontsize=8, fontweight='bold')
        # 生命线
        ax.plot([x, x], [7, 0.5], 'k--', linewidth=0.5, alpha=0.3)

    # 消息
    messages = [
        (1.5, 4.0, 6.3, 'POST /api/users/login {username, password}'),
        (4.0, 6.5, 5.5, 'login(LoginRequest)'),
        (6.5, 9.0, 4.7, 'findByUsername(username)'),
        (9.0, 6.5, 3.9, 'User实体（含BCrypt密码）'),
        (6.5, 4.0, 3.1, 'LoginResponse / null'),
        (4.0, 1.5, 2.3, '200 OK / 401 Unauthorized'),
    ]

    for x1, x2, y, label in messages:
        arrow = FancyArrowPatch((x1, y), (x2, y),
                                arrowstyle='->', mutation_scale=12,
                                color='#333333', linewidth=1)
        ax.add_patch(arrow)
        mid_x = (x1 + x2) / 2
        ax.text(mid_x, y + 0.15, label, ha='center', va='bottom',
                fontsize=7, color='#333333')

    save_figure(fig, 'fig_4_6_login_sequence')


if __name__ == '__main__':
    print("开始生成第四章图表...")
    draw_architecture()
    draw_modules()
    draw_er_diagram()
    draw_order_flow()
    draw_order_process()
    draw_login_sequence()
    print(f"\n所有图表已生成到: {FIG_DIR}")
    print("SVG源文件和PNG图片均已保存。")
