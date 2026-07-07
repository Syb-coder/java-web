#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
用matplotlib生成第四章所需的6张PNG图表
"""
import os
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch, Polygon
import matplotlib.font_manager as fm
import numpy as np

OUTPUT_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'figures')
os.makedirs(OUTPUT_DIR, exist_ok=True)

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['Microsoft YaHei', 'SimHei', 'sans-serif']
plt.rcParams['axes.unicode_minus'] = False

# 颜色定义
C_BROWSER = '#E8F5E9'
C_BROWSER_BD = '#2E7D32'
C_FRONTEND = '#E3F2FD'
C_FRONTEND_BD = '#1565C0'
C_CONTROLLER = '#E8EAF6'
C_CONTROLLER_BD = '#283593'
C_SERVICE = '#FFF3E0'
C_SERVICE_BD = '#E65100'
C_REPO = '#FCE4EC'
C_REPO_BD = '#C62828'
C_DB = '#F3E5F5'
C_DB_BD = '#6A1B9A'
C_DECISION = '#FFF9C4'
C_DECISION_BD = '#F57F17'
C_ERROR = '#FFEBEE'
C_ERROR_BD = '#C62828'
C_START = '#E8F5E9'
C_START_BD = '#2E7D32'
C_ARROW = '#455A64'


def draw_box(ax, x, y, w, h, text, fill, edge, fs=11, bold=False, round_box=True):
    """绘制圆角矩形+居中文字"""
    style = "round,pad=0.02" if round_box else "square,pad=0"
    box = FancyBboxPatch((x, y), w, h, boxstyle=style, facecolor=fill, edgecolor=edge, linewidth=1.5)
    ax.add_patch(box)
    fw = 'bold' if bold else 'normal'
    lines = text.split('|')
    if len(lines) == 1:
        ax.text(x + w/2, y + h/2, text, ha='center', va='center', fontsize=fs, fontweight=fw, color='#1a1a1a')
    else:
        total_h = len(lines) * (fs * 0.018)
        start_y = y + h/2 + total_h * (len(lines) - 1) / 2
        for i, line in enumerate(lines):
            ax.text(x + w/2, start_y - i * (fs * 0.018), line, ha='center', va='center', fontsize=fs, fontweight=fw, color='#1a1a1a')


def draw_diamond(ax, cx, cy, w, h, text, fill, edge, fs=10):
    """绘制菱形"""
    pts = np.array([[cx, cy + h/2], [cx + w/2, cy], [cx, cy - h/2], [cx - w/2, cy]])
    poly = Polygon(pts, closed=True, facecolor=fill, edgecolor=edge, linewidth=1.5)
    ax.add_patch(poly)
    lines = text.split('|')
    if len(lines) == 1:
        ax.text(cx, cy, text, ha='center', va='center', fontsize=fs, color='#1a1a1a')
    else:
        for i, line in enumerate(lines):
            offset = (i - (len(lines) - 1) / 2) * (fs * 0.016)
            ax.text(cx, cy + offset, line, ha='center', va='center', fontsize=fs, color='#1a1a1a')


def draw_arrow(ax, x1, y1, x2, y2, label="", color=C_ARROW):
    """绘制箭头"""
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle='->', color=color, lw=1.5))
    if label:
        mx, my = (x1 + x2) / 2, (y1 + y2) / 2
        ax.text(mx + 0.01, my, label, ha='left', va='center', fontsize=9, color='#555')


def draw_line(ax, x1, y1, x2, y2, label="", color=C_ARROW, style='-'):
    """绘制带箭头的线（用于流程图回流）"""
    ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle='->', color=color, lw=1.5, linestyle=style))
    if label:
        mx, my = (x1 + x2) / 2, (y1 + y2) / 2
        ax.text(mx + 0.01, my, label, ha='left', va='center', fontsize=9, color='#555')


def save_fig(fig, name):
    path = os.path.join(OUTPUT_DIR, name + '.png')
    fig.savefig(path, dpi=150, bbox_inches='tight', facecolor='white', edgecolor='none')
    plt.close(fig)
    print('PNG: ' + path)


# ============================================================
# 图4-1 系统架构图
# ============================================================
def gen_fig_4_1():
    fig, ax = plt.subplots(1, 1, figsize=(8, 6))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 8)
    ax.axis('off')
    ax.set_title('求职信息管理平台系统架构', fontsize=14, fontweight='bold', pad=10)

    draw_box(ax, 3, 7, 4, 0.6, '浏览器（Browser）', C_BROWSER, C_BROWSER_BD, 12, True)
    draw_arrow(ax, 5, 7, 5, 6.6, 'HTTP / JSON')

    draw_box(ax, 1.5, 5.8, 7, 0.8, '前端表示层：HTML5 + CSS3 + JavaScript（单页应用）', C_FRONTEND, C_FRONTEND_BD, 11, True)
    draw_arrow(ax, 5, 5.8, 5, 5.4, 'Fetch API')

    draw_box(ax, 1.5, 4.6, 7, 0.7, 'Controller层（RESTful API）：JobController', C_CONTROLLER, C_CONTROLLER_BD, 11, True)
    draw_arrow(ax, 5, 4.6, 5, 4.2)

    draw_box(ax, 0.5, 3.4, 4, 0.7, 'Service层：JobService', C_SERVICE, C_SERVICE_BD, 11, True)
    draw_box(ax, 5.5, 3.4, 4, 0.7, 'Service层：V2exJobSyncService', C_SERVICE, C_SERVICE_BD, 11, True)
    draw_arrow(ax, 2.5, 3.4, 2.5, 3.0)
    draw_arrow(ax, 7.5, 3.4, 7.5, 3.0)

    draw_box(ax, 1.5, 2.2, 7, 0.7, 'Repository层：JobPostingRepository（Spring Data JPA）', C_REPO, C_REPO_BD, 11, True)
    draw_arrow(ax, 5, 2.2, 5, 1.8)

    draw_box(ax, 3, 1.0, 4, 0.7, 'H2 数据库（job_postings表）', C_DB, C_DB_BD, 12, True)

    save_fig(fig, 'fig_4_1_architecture')


# ============================================================
# 图4-2 功能模块结构图
# ============================================================
def gen_fig_4_2():
    fig, ax = plt.subplots(1, 1, figsize=(10, 5.5))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 6)
    ax.axis('off')
    ax.set_title('系统功能模块结构图', fontsize=14, fontweight='bold', pad=10)

    # 顶层
    draw_box(ax, 3.5, 5, 3, 0.6, '求职信息管理平台', C_FRONTEND, C_FRONTEND_BD, 12, True)
    draw_arrow(ax, 5, 5, 5, 4.5)

    # 五个模块
    modules = [
        (0.2, 3.8, 1.7, '岗位信息|展示模块'),
        (2.2, 3.8, 1.7, '多条件|检索模块'),
        (4.2, 3.8, 1.7, '岗位|发布模块'),
        (6.2, 3.8, 1.7, 'V2EX数据|同步模块'),
        (8.2, 3.8, 1.6, '数据|统计模块'),
    ]
    for mx, my, mw, mtext in modules:
        draw_box(ax, mx, my, mw, 0.7, mtext, C_BROWSER, C_BROWSER_BD, 10, True)

    # 子功能
    subs = [
        (0.2, 2.7, '卡片列表展示'), (0.2, 2.0, '来源标识区分'),
        (2.2, 2.7, '六维筛选'), (2.2, 2.0, 'JPQL查询'),
        (4.2, 2.7, '表单填写校验'), (4.2, 2.0, '薪资K转元'),
        (6.2, 2.7, 'API调用获取'), (6.2, 2.0, '正则提取去重'),
        (8.2, 2.7, '分类/城市统计'), (8.2, 2.0, '来源统计'),
    ]
    for sx, sy, stext in subs:
        draw_box(ax, sx, sy, 1.7 if sx < 8 else 1.6, 0.5, stext, '#F1F8E9', '#558B2F', 9)

    # 连接线
    for cx in [1.05, 3.05, 5.05, 7.05, 9.0]:
        ax.annotate('', xy=(cx, 2.7), xytext=(cx, 3.8), arrowprops=dict(arrowstyle='-', color='#888', lw=1))

    # 底层
    draw_box(ax, 2.5, 0.8, 5, 0.55, '公共支撑：JobPosting实体 / DTO / 枚举类型', '#FFF8E1', '#F57F17', 10, True)
    draw_box(ax, 2.5, 0.15, 5, 0.45, '数据库：H2（job_postings表）', C_DB, C_DB_BD, 9, True)

    save_fig(fig, 'fig_4_2_modules')


# ============================================================
# 图4-3 岗位检索功能流程图
# ============================================================
def gen_fig_4_3():
    fig, ax = plt.subplots(1, 1, figsize=(7, 9))
    ax.set_xlim(0, 8)
    ax.set_ylim(0, 10)
    ax.axis('off')
    ax.set_title('岗位检索功能流程图', fontsize=14, fontweight='bold', pad=10)

    draw_box(ax, 3, 9.2, 2, 0.5, '开始', C_START, C_START_BD, 12, True)
    draw_arrow(ax, 4, 9.2, 4, 8.8)

    draw_box(ax, 2.2, 8.2, 3.6, 0.55, '用户填写筛选条件|（关键词/城市/分类等）', C_FRONTEND, C_FRONTEND_BD, 10)
    draw_arrow(ax, 4, 8.2, 4, 7.8)

    draw_box(ax, 2.8, 7.2, 2.4, 0.5, '点击搜索按钮', C_FRONTEND, C_FRONTEND_BD, 11)
    draw_arrow(ax, 4, 7.2, 4, 6.8)

    draw_box(ax, 2.2, 6.2, 3.6, 0.55, '前端收集筛选参数为|URLSearchParams', C_FRONTEND, C_FRONTEND_BD, 10)
    draw_arrow(ax, 4, 6.2, 4, 5.8)

    draw_box(ax, 2.2, 5.2, 3.6, 0.5, 'Fetch API发送 GET /api/jobs请求', C_CONTROLLER, C_CONTROLLER_BD, 10)
    draw_arrow(ax, 4, 5.2, 4, 4.8)

    draw_box(ax, 2.2, 4.2, 3.6, 0.5, 'JobController接收请求参数', C_CONTROLLER, C_CONTROLLER_BD, 10)
    draw_arrow(ax, 4, 4.2, 4, 3.8)

    draw_box(ax, 2.2, 3.2, 3.6, 0.5, 'JobService调用 search方法', C_SERVICE, C_SERVICE_BD, 10)
    draw_arrow(ax, 4, 3.2, 4, 2.8)

    draw_box(ax, 1.8, 2.2, 4.4, 0.55, 'Repository执行JPQL|多条件查询数据库', C_REPO, C_REPO_BD, 10)
    draw_arrow(ax, 4, 2.2, 4, 1.8)

    draw_box(ax, 2.2, 1.0, 3.6, 0.5, '返回DTO列表并渲染页面', C_START, C_START_BD, 10)

    save_fig(fig, 'fig_4_3_search_flow')


# ============================================================
# 图4-4 岗位发布功能流程图
# ============================================================
def gen_fig_4_4():
    fig, ax = plt.subplots(1, 1, figsize=(7, 11))
    ax.set_xlim(0, 8)
    ax.set_ylim(0, 12)
    ax.axis('off')
    ax.set_title('岗位发布功能流程图', fontsize=14, fontweight='bold', pad=10)

    cx = 4.0
    # 开始
    draw_box(ax, cx-1, 11.2, 2, 0.5, '开始', C_START, C_START_BD, 12, True)
    draw_arrow(ax, cx, 11.2, cx, 10.8)

    # 用户点击
    draw_box(ax, cx-2, 10.2, 4, 0.5, '用户点击发布岗位按钮', C_FRONTEND, C_FRONTEND_BD, 11)
    draw_arrow(ax, cx, 10.2, cx, 9.8)

    # 填写表单
    draw_box(ax, cx-2.5, 9.2, 5, 0.55, '弹出发布表单，填写岗位信息|（标题/公司/薪资/职责等）', C_FRONTEND, C_FRONTEND_BD, 10)
    draw_arrow(ax, cx, 9.2, cx, 8.8)

    # 判断
    draw_diamond(ax, cx, 8.3, 3, 0.8, '必填字段|是否为空？', C_DECISION, C_DECISION_BD, 10)

    # 是分支 - 左侧
    draw_arrow(ax, cx-1.5, 8.3, 0.8, 8.3, '是')
    draw_box(ax, 0.1, 8.0, 1.4, 0.6, '提示填写|必填字段', C_ERROR, C_ERROR_BD, 9)
    # 回流箭头：从错误框向上到表单框
    ax.annotate('', xy=(0.8, 9.2), xytext=(0.8, 8.6),
                arrowprops=dict(arrowstyle='->', color=C_ARROW, lw=1.5, linestyle='--'))

    # 否分支 - 向下
    draw_arrow(ax, cx, 7.9, cx, 7.5, '否')
    draw_box(ax, cx-2, 7.0, 4, 0.5, '前端序列化表单为JSON', C_FRONTEND, C_FRONTEND_BD, 10)
    draw_arrow(ax, cx, 7.0, cx, 6.6)

    draw_box(ax, cx-2, 6.1, 4, 0.5, 'POST /api/jobs 发送JSON', C_CONTROLLER, C_CONTROLLER_BD, 10)
    draw_arrow(ax, cx, 6.1, cx, 5.7)

    draw_box(ax, cx-2, 5.2, 4, 0.5, 'JobService.create() 映射DTO到实体', C_SERVICE, C_SERVICE_BD, 10)
    draw_arrow(ax, cx, 5.2, cx, 4.8)

    draw_box(ax, cx-2, 4.3, 4, 0.5, '设置来源LOCAL 发布日期为当天', C_SERVICE, C_SERVICE_BD, 10)
    draw_arrow(ax, cx, 4.3, cx, 3.9)

    draw_box(ax, cx-2, 3.4, 4, 0.5, 'Repository持久化 返回响应DTO', C_REPO, C_REPO_BD, 10)
    draw_arrow(ax, cx, 3.4, cx, 3.0)

    draw_box(ax, cx-1, 2.4, 2, 0.5, '关闭弹窗并刷新', C_START, C_START_BD, 10)

    save_fig(fig, 'fig_4_4_publish_flow')


# ============================================================
# 图4-5 V2EX数据同步功能流程图
# ============================================================
def gen_fig_4_5():
    fig, ax = plt.subplots(1, 1, figsize=(7, 13))
    ax.set_xlim(0, 8)
    ax.set_ylim(0, 14)
    ax.axis('off')
    ax.set_title('V2EX数据同步功能流程图', fontsize=14, fontweight='bold', pad=10)

    cx = 4.0
    # 开始
    draw_box(ax, cx-1, 13.2, 2, 0.5, '开始', C_START, C_START_BD, 12, True)
    draw_arrow(ax, cx, 13.2, cx, 12.8)

    draw_box(ax, cx-2, 12.2, 4, 0.5, '用户点击同步V2EX按钮', C_FRONTEND, C_FRONTEND_BD, 11)
    draw_arrow(ax, cx, 12.2, cx, 11.8)

    draw_box(ax, cx-2, 11.2, 4, 0.5, 'POST /api/jobs/sync-v2ex', C_CONTROLLER, C_CONTROLLER_BD, 11)
    draw_arrow(ax, cx, 11.2, cx, 10.8)

    draw_box(ax, cx-2.5, 10.2, 5, 0.55, 'V2exJobSyncService调用|V2EX API获取主题列表', C_SERVICE, C_SERVICE_BD, 10)
    draw_arrow(ax, cx, 10.2, cx, 9.8)

    draw_box(ax, cx-2, 9.2, 4, 0.5, '遍历每条主题数据', C_SERVICE, C_SERVICE_BD, 10)
    draw_arrow(ax, cx, 9.2, cx, 8.8)

    # 判断1：externalId是否已存在
    draw_diamond(ax, cx, 8.3, 3.2, 0.8, 'externalId|已存在？', C_DECISION, C_DECISION_BD, 10)
    # 是 - 右侧跳过
    draw_arrow(ax, cx+1.6, 8.3, 7.0, 8.3, '是')
    draw_box(ax, 6.3, 8.0, 1.5, 0.55, '跳过该条', C_ERROR, C_ERROR_BD, 9)
    # 回流到遍历
    ax.annotate('', xy=(cx+2, 9.2), xytext=(7.0, 8.55),
                arrowprops=dict(arrowstyle='->', color=C_ARROW, lw=1.5, linestyle='--'))

    # 否 - 向下
    draw_arrow(ax, cx, 7.9, cx, 7.5, '否')
    draw_box(ax, cx-2.5, 6.9, 5, 0.55, 'convert方法转换数据|（正则提取城市/薪资/分类）', C_FRONTEND, C_FRONTEND_BD, 10)
    draw_arrow(ax, cx, 6.9, cx, 6.5)

    draw_box(ax, cx-2, 5.9, 4, 0.5, '逐条保存到数据库', C_REPO, C_REPO_BD, 10)
    draw_arrow(ax, cx, 5.9, cx, 5.5)

    # 判断2：保存是否成功
    draw_diamond(ax, cx, 5.0, 3, 0.7, '保存是否|成功？', C_DECISION, C_DECISION_BD, 10)
    # 否 - 左侧
    draw_arrow(ax, cx-1.5, 5.0, 0.8, 5.0, '否')
    draw_box(ax, 0.1, 4.7, 1.4, 0.6, '记录失败|继续下一条', C_ERROR, C_ERROR_BD, 9)
    # 回流到遍历
    ax.annotate('', xy=(0.8, 9.2), xytext=(0.8, 5.3),
                arrowprops=dict(arrowstyle='->', color=C_ARROW, lw=1.5, linestyle='--'))
    ax.annotate('', xy=(cx-2, 9.2), xytext=(0.8, 9.2),
                arrowprops=dict(arrowstyle='->', color=C_ARROW, lw=1.5, linestyle='--'))

    # 是 - 向下
    draw_arrow(ax, cx, 4.65, cx, 4.25, '是')

    # 判断3：还有更多主题？
    draw_diamond(ax, cx, 3.8, 3, 0.7, '还有更多|主题？', C_DECISION, C_DECISION_BD, 10)
    # 是 - 右侧回流到遍历
    draw_arrow(ax, cx+1.5, 3.8, 7.0, 3.8, '是')
    ax.annotate('', xy=(7.0, 9.2), xytext=(7.0, 3.8),
                arrowprops=dict(arrowstyle='->', color=C_ARROW, lw=1.5, linestyle='--'))
    ax.annotate('', xy=(cx+2, 9.2), xytext=(7.0, 9.2),
                arrowprops=dict(arrowstyle='->', color=C_ARROW, lw=1.5, linestyle='--'))

    # 否 - 向下
    draw_arrow(ax, cx, 3.45, cx, 3.05, '否')
    draw_box(ax, cx-1.5, 2.4, 3, 0.5, '返回新增数量', C_START, C_START_BD, 10)

    save_fig(fig, 'fig_4_5_v2ex_sync_flow')


# ============================================================
# 图4-6 数据库ER图
# ============================================================
def gen_fig_4_6():
    fields = [
        ("id", "BIGINT (PK, 自增)", True),
        ("title", "VARCHAR(255)", False),
        ("company", "VARCHAR(255)", False),
        ("city", "VARCHAR(255)", False),
        ("category", "ENUM(STRING)", False),
        ("experience", "ENUM(STRING)", False),
        ("education", "ENUM(STRING)", False),
        ("salaryMin", "INTEGER (nullable)", False),
        ("salaryMax", "INTEGER (nullable)", False),
        ("salaryDesc", "VARCHAR(255)", False),
        ("skills", "VARCHAR(255)", False),
        ("responsibilities", "VARCHAR(4000)", False),
        ("requirements", "VARCHAR(4000)", False),
        ("benefits", "VARCHAR(1000)", False),
        ("companyDesc", "VARCHAR(1000)", False),
        ("publishedDate", "DATE", False),
        ("source", "ENUM(STRING)", False),
        ("externalId", "VARCHAR(255)", False),
        ("externalUrl", "VARCHAR(255)", False),
        ("publisherName", "VARCHAR(255)", False),
        ("contact", "VARCHAR(255)", False),
        ("createdAt", "DATETIME (自动)", False),
    ]

    n = len(fields)
    fig_h = 2 + n * 0.4
    fig, ax = plt.subplots(1, 1, figsize=(9, fig_h))
    ax.set_xlim(0, 10)
    ax.set_ylim(0, fig_h)
    ax.axis('off')
    ax.set_title('数据库ER图（job_postings表）', fontsize=14, fontweight='bold', pad=10)

    # 表头
    header_h = 0.5
    table_top = fig_h - 0.5
    table_bottom = 0.5
    table_left = 0.5
    table_right = 9.5
    row_h = (table_top - header_h - table_bottom) / n

    # 表头背景
    header_box = FancyBboxPatch((table_left, table_top - header_h), table_right - table_left, header_h,
                                 boxstyle="square,pad=0", facecolor=C_FRONTEND_BD, edgecolor=C_FRONTEND_BD, linewidth=1.5)
    ax.add_patch(header_box)
    ax.text(5, table_top - header_h/2, 'JobPosting（job_postings表）', ha='center', va='center',
            fontsize=12, fontweight='bold', color='white')

    # 字段行
    for i, (name, typ, is_pk) in enumerate(fields):
        y = table_top - header_h - (i + 1) * row_h
        bg = '#E3F2FD' if is_pk else ('#F5F5F5' if i % 2 == 0 else '#FFFFFF')
        rect = mpatches.Rectangle((table_left, y), table_right - table_left, row_h, facecolor=bg, edgecolor='#E0E0E0', linewidth=0.5)
        ax.add_patch(rect)
        fw = 'bold' if is_pk else 'normal'
        ax.text(table_left + 0.3, y + row_h/2, name, ha='left', va='center', fontsize=9, fontweight=fw, color='#1a1a1a')
        ax.text(table_left + 4, y + row_h/2, typ, ha='left', va='center', fontsize=8, color='#616161')
        if is_pk:
            ax.text(table_right - 0.3, y + row_h/2, 'PK', ha='center', va='center', fontsize=9, fontweight='bold', color=C_REPO_BD)

    # 外框
    outer = mpatches.Rectangle((table_left, table_bottom), table_right - table_left, table_top - table_bottom,
                                facecolor='none', edgecolor='#BDBDBD', linewidth=1.5)
    ax.add_patch(outer)

    save_fig(fig, 'fig_4_6_er_diagram')


# ============================================================
# 主函数
# ============================================================
if __name__ == '__main__':
    gen_fig_4_1()
    gen_fig_4_2()
    gen_fig_4_3()
    gen_fig_4_4()
    gen_fig_4_5()
    gen_fig_4_6()
    print('\n所有图表已生成完毕！')
