// Mock 初始数据：为前端开发提供模拟数据源，避免依赖后端接口
// 所有数据均为虚构，仅用于功能演示与联调
// 业务实体统一包含 updatedAt 字段，模拟后端自动维护的最近更新时间

import type {
  Student,
  Teacher,
  ClassInfo,
  Course,
  Score,
  Notice,
  OperationLog,
  Major,
  Attendance,
  Approval,
  Admin,
} from '@/types'

// 学生信息：覆盖各班级，学号统一使用 10 位编码
export const students: Student[] = [
  { id: 1, name: '张三', studentNo: '2507012401', gender: '男', age: '20', className: '计算机2401', phone: '13800138001', updatedAt: '2026-01-10 09:30:00' },
  { id: 2, name: '李四', studentNo: '2507012402', gender: '女', age: '19', className: '计算机2401', phone: '13800138002', updatedAt: '2026-01-10 09:30:00' },
  { id: 3, name: '王五', studentNo: '2507012403', gender: '男', age: '21', className: '计算机2402', phone: '13800138003', updatedAt: '2026-01-10 09:30:00' },
  { id: 4, name: '赵六', studentNo: '2507012404', gender: '女', age: '20', className: '计算机2402', phone: '13800138004', updatedAt: '2026-01-10 09:30:00' },
  { id: 5, name: '孙七', studentNo: '2507012405', gender: '男', age: '19', className: '软件2401', phone: '13800138005', updatedAt: '2026-01-10 09:30:00' },
  { id: 6, name: '周八', studentNo: '2507012406', gender: '女', age: '20', className: '软件2401', phone: '13800138006', updatedAt: '2026-01-10 09:30:00' },
  { id: 7, name: '吴九', studentNo: '2507012407', gender: '男', age: '21', className: '软件2402', phone: '13800138007', updatedAt: '2026-01-10 09:30:00' },
  { id: 8, name: '郑十', studentNo: '2507012408', gender: '女', age: '19', className: '软件2402', phone: '13800138008', updatedAt: '2026-01-10 09:30:00' },
  { id: 9, name: '钱十一', studentNo: '2507012409', gender: '男', age: '20', className: '网络2401', phone: '13800138009', updatedAt: '2026-01-10 09:30:00' },
  { id: 10, name: '冯十二', studentNo: '2507012410', gender: '女', age: '21', className: '网络2401', phone: '13800138010', updatedAt: '2026-01-10 09:30:00' },
  { id: 11, name: '陈十三', studentNo: '2507012411', gender: '男', age: '20', className: '计算机2401', phone: '13800138011', updatedAt: '2026-01-10 09:30:00' },
  { id: 12, name: '褚十四', studentNo: '2507012412', gender: '女', age: '19', className: '软件2402', phone: '13800138012', updatedAt: '2026-01-10 09:30:00' },
]

// 教师信息：工号使用 T + 年份 + 序号格式
export const teachers: Teacher[] = [
  { id: 1, name: '刘老师', teacherNo: 'T2026001', gender: '男', age: '35', phone: '13900139001', updatedAt: '2026-01-08 14:20:00' },
  { id: 2, name: '杨老师', teacherNo: 'T2026002', gender: '女', age: '40', phone: '13900139002', updatedAt: '2026-01-08 14:20:00' },
  { id: 3, name: '黄老师', teacherNo: 'T2026003', gender: '男', age: '45', phone: '13900139003', updatedAt: '2026-01-08 14:20:00' },
  { id: 4, name: '林老师', teacherNo: 'T2026004', gender: '女', age: '38', phone: '13900139004', updatedAt: '2026-01-08 14:20:00' },
  { id: 5, name: '何老师', teacherNo: 'T2026005', gender: '男', age: '50', phone: '13900139005', updatedAt: '2026-01-08 14:20:00' },
  { id: 6, name: '高老师', teacherNo: 'T2026006', gender: '女', age: '36', phone: '13900139006', updatedAt: '2026-01-08 14:20:00' },
  { id: 7, name: '罗老师', teacherNo: 'T2026007', gender: '男', age: '42', phone: '13900139007', updatedAt: '2026-01-08 14:20:00' },
  { id: 8, name: '梁老师', teacherNo: 'T2026008', gender: '女', age: '33', phone: '13900139008', updatedAt: '2026-01-08 14:20:00' },
  { id: 9, name: '宋老师', teacherNo: 'T2026009', gender: '男', age: '48', phone: '13900139009', updatedAt: '2026-01-08 14:20:00' },
  { id: 10, name: '谢老师', teacherNo: 'T2026010', gender: '女', age: '41', phone: '13900139010', updatedAt: '2026-01-08 14:20:00' },
]

// 班级信息：同一班级同时配置班主任与辅导员两类教师
export const classes: ClassInfo[] = [
  { id: 1, name: '计算机2401', capacity: '40', teacherType: '班主任', updatedAt: '2026-01-05 10:00:00' },
  { id: 2, name: '计算机2402', capacity: '40', teacherType: '班主任', updatedAt: '2026-01-05 10:00:00' },
  { id: 3, name: '软件2401', capacity: '35', teacherType: '班主任', updatedAt: '2026-01-05 10:00:00' },
  { id: 4, name: '软件2402', capacity: '35', teacherType: '班主任', updatedAt: '2026-01-05 10:00:00' },
  { id: 5, name: '网络2401', capacity: '30', teacherType: '班主任', updatedAt: '2026-01-05 10:00:00' },
  { id: 6, name: '计算机2401', capacity: '40', teacherType: '辅导员', updatedAt: '2026-01-05 10:00:00' },
  { id: 7, name: '软件2401', capacity: '35', teacherType: '辅导员', updatedAt: '2026-01-05 10:00:00' },
  { id: 8, name: '网络2401', capacity: '30', teacherType: '辅导员', updatedAt: '2026-01-05 10:00:00' },
]

// 课程信息：涵盖公共基础课与专业核心课
export const courses: Course[] = [
  { id: 1, name: '高等数学', remark: '公共基础必修课', updatedAt: '2026-01-06 11:00:00' },
  { id: 2, name: '线性代数', remark: '公共基础必修课', updatedAt: '2026-01-06 11:00:00' },
  { id: 3, name: '数据结构', remark: '专业核心课', updatedAt: '2026-01-06 11:00:00' },
  { id: 4, name: '操作系统', remark: '专业核心课', updatedAt: '2026-01-06 11:00:00' },
  { id: 5, name: '计算机网络', remark: '专业核心课', updatedAt: '2026-01-06 11:00:00' },
  { id: 6, name: '数据库原理', remark: '专业核心课', updatedAt: '2026-01-06 11:00:00' },
  { id: 7, name: '软件工程', remark: '专业必修课', updatedAt: '2026-01-06 11:00:00' },
  { id: 8, name: 'Java程序设计', remark: '专业必修课', updatedAt: '2026-01-06 11:00:00' },
]

// 成绩信息：关联课程 id 与学生学号
export const scores: Score[] = [
  { id: 1, courseId: 1, studentNo: '2507012401', score: '85', updatedAt: '2026-01-12 16:00:00' },
  { id: 2, courseId: 1, studentNo: '2507012402', score: '92', updatedAt: '2026-01-12 16:00:00' },
  { id: 3, courseId: 3, studentNo: '2507012401', score: '78', updatedAt: '2026-01-12 16:00:00' },
  { id: 4, courseId: 3, studentNo: '2507012403', score: '88', updatedAt: '2026-01-12 16:00:00' },
  { id: 5, courseId: 8, studentNo: '2507012401', score: '90', updatedAt: '2026-01-12 16:00:00' },
  { id: 6, courseId: 8, studentNo: '2507012405', score: '75', updatedAt: '2026-01-12 16:00:00' },
  { id: 7, courseId: 6, studentNo: '2507012402', score: '82', updatedAt: '2026-01-12 16:00:00' },
  { id: 8, courseId: 6, studentNo: '2507012404', score: '95', updatedAt: '2026-01-12 16:00:00' },
  { id: 9, courseId: 4, studentNo: '2507012407', score: '68', updatedAt: '2026-01-12 16:00:00' },
  { id: 10, courseId: 5, studentNo: '2507012408', score: '88', updatedAt: '2026-01-12 16:00:00' },
]

// 系统公告：标题贴近真实校园场景
export const notices: Notice[] = [
  { id: 1, title: '关于2026年春季学期选课通知', author: '教务处', date: '2026-01-15', updatedAt: '2026-01-15 08:00:00' },
  { id: 2, title: '2026年寒假放假安排', author: '校办公室', date: '2026-01-10', updatedAt: '2026-01-10 09:00:00' },
  { id: 3, title: '期末考试时间安排通知', author: '教务处', date: '2026-01-05', updatedAt: '2026-01-05 10:00:00' },
  { id: 4, title: '关于开展2026年寒假社会实践活动的通知', author: '学生处', date: '2025-12-28', updatedAt: '2025-12-28 14:00:00' },
  { id: 5, title: '校园网络升级维护公告', author: '信息中心', date: '2025-12-20', updatedAt: '2025-12-20 15:00:00' },
  { id: 6, title: '关于2026届毕业生答辩安排', author: '教务处', date: '2025-12-15', updatedAt: '2025-12-15 09:00:00' },
  { id: 7, title: '图书馆开放时间调整通知', author: '图书馆', date: '2025-12-10', updatedAt: '2025-12-10 11:00:00' },
  { id: 8, title: '关于加强校园安全管理的通知', author: '保卫处', date: '2025-12-05', updatedAt: '2025-12-05 16:00:00' },
]

// 操作记录：记录用户在系统中的关键操作行为
// OperationLog 本身有 time 字段记录操作发生时间，无需额外的 updatedAt
export const operationLogs: OperationLog[] = [
  { id: 1, action: '登录系统', detail: '用户张三登录管理后台', time: '2026-01-15 09:30' },
  { id: 2, action: '新增学生', detail: '新增学生"张三"(学号2507012401)', time: '2026-01-15 09:35' },
  { id: 3, action: '修改成绩', detail: '修改学号2507012401的高等数学成绩为85', time: '2026-01-15 10:12' },
  { id: 4, action: '发布公告', detail: '发布公告"关于2026年春季学期选课通知"', time: '2026-01-15 08:00' },
  { id: 5, action: '导出数据', detail: '导出学生花名册Excel', time: '2026-01-13 14:25' },
  { id: 6, action: '修改课程', detail: '修改课程"数据结构"备注信息', time: '2026-01-12 11:40' },
  { id: 7, action: '新增教师', detail: '新增教师"刘老师"(工号T2026001)', time: '2026-01-10 15:30' },
  { id: 8, action: '删除公告', detail: '删除过期的旧版通知', time: '2026-01-14 16:20' },
  { id: 9, action: '查看统计', detail: '查看首页统计数据', time: '2026-01-15 09:31' },
  { id: 10, action: '退出登录', detail: '用户退出系统', time: '2026-01-15 18:00' },
]

// 专业信息（P1 新增）：覆盖计算机科学与技术、软件工程等主流专业
export const majors: Major[] = [
  { id: 1, name: '计算机科学与技术', college: '计算机学院', remark: '国家级一流本科专业', updatedAt: '2026-01-04 09:00:00' },
  { id: 2, name: '软件工程', college: '计算机学院', remark: '省级特色专业', updatedAt: '2026-01-04 09:00:00' },
  { id: 3, name: '网络工程', college: '计算机学院', remark: '应用型人才培养专业', updatedAt: '2026-01-04 09:00:00' },
  { id: 4, name: '信息安全', college: '计算机学院', remark: '新工科建设专业', updatedAt: '2026-01-04 09:00:00' },
  { id: 5, name: '人工智能', college: '人工智能学院', remark: '前沿交叉学科', updatedAt: '2026-01-04 09:00:00' },
  { id: 6, name: '数据科学与大数据技术', college: '人工智能学院', remark: '校企共建专业', updatedAt: '2026-01-04 09:00:00' },
]

// 考勤记录（P1 新增）：覆盖部分学生的出勤情况
export const attendances: Attendance[] = [
  { id: 1, studentNo: '2507012401', courseId: 1, date: '2026-01-12', status: '出勤', updatedAt: '2026-01-12 08:30:00' },
  { id: 2, studentNo: '2507012402', courseId: 1, date: '2026-01-12', status: '迟到', updatedAt: '2026-01-12 08:30:00' },
  { id: 3, studentNo: '2507012403', courseId: 3, date: '2026-01-12', status: '旷课', updatedAt: '2026-01-12 10:00:00' },
  { id: 4, studentNo: '2507012404', courseId: 3, date: '2026-01-12', status: '请假', updatedAt: '2026-01-12 10:00:00' },
  { id: 5, studentNo: '2507012405', courseId: 8, date: '2026-01-13', status: '出勤', updatedAt: '2026-01-13 08:30:00' },
  { id: 6, studentNo: '2507012406', courseId: 8, date: '2026-01-13', status: '出勤', updatedAt: '2026-01-13 08:30:00' },
  { id: 7, studentNo: '2507012407', courseId: 4, date: '2026-01-13', status: '迟到', updatedAt: '2026-01-13 14:00:00' },
  { id: 8, studentNo: '2507012408', courseId: 5, date: '2026-01-13', status: '出勤', updatedAt: '2026-01-13 14:00:00' },
  { id: 9, studentNo: '2507012409', courseId: 6, date: '2026-01-14', status: '请假', updatedAt: '2026-01-14 08:30:00' },
  { id: 10, studentNo: '2507012410', courseId: 6, date: '2026-01-14', status: '出勤', updatedAt: '2026-01-14 08:30:00' },
]

// 审批申请（P1 新增）：包含请假、奖学金、休学等典型申请
export const approvals: Approval[] = [
  { id: 1, studentNo: '2507012401', type: '请假', reason: '家中有事需请假一天', status: '待审批', opinion: '', createdAt: '2026-01-14 09:00:00', updatedAt: '2026-01-14 09:00:00' },
  { id: 2, studentNo: '2507012403', type: '奖学金申请', reason: '申请校级一等奖学金，成绩排名专业前5%', status: '待审批', opinion: '', createdAt: '2026-01-13 10:30:00', updatedAt: '2026-01-13 10:30:00' },
  { id: 3, studentNo: '2507012405', type: '请假', reason: '身体不适需就医', status: '已通过', opinion: '同意，注意按时返校', createdAt: '2026-01-12 08:00:00', updatedAt: '2026-01-12 15:00:00' },
  { id: 4, studentNo: '2507012407', type: '休学', reason: '因个人原因申请休学一年', status: '已驳回', opinion: '材料不齐全，请补充医疗证明', createdAt: '2026-01-10 14:00:00', updatedAt: '2026-01-11 09:30:00' },
  { id: 5, studentNo: '2507012402', type: '奖学金申请', reason: '申请国家励志奖学金', status: '待审批', opinion: '', createdAt: '2026-01-15 08:30:00', updatedAt: '2026-01-15 08:30:00' },
  { id: 6, studentNo: '2507012409', type: '请假', reason: '参加学科竞赛需请假三天', status: '已通过', opinion: '同意，祝取得好成绩', createdAt: '2026-01-09 09:00:00', updatedAt: '2026-01-09 16:00:00' },
]

// 管理员账号（P1 新增）：系统后台管理员
export const admins: Admin[] = [
  { id: 1, username: 'admin', realName: '系统管理员', phone: '13800000001', updatedAt: '2026-01-01 00:00:00' },
  { id: 2, username: 'zhangwei', realName: '张伟', phone: '13800000002', updatedAt: '2026-01-03 10:00:00' },
  { id: 3, username: 'liying', realName: '李颖', phone: '13800000003', updatedAt: '2026-01-03 10:00:00' },
]
