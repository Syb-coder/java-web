// 实体类型定义：与 PRD 3.4-3.9 节数据字段一一对应
// 所有业务实体统一增加 updatedAt 字段，记录最近一次更新时间

// 学生信息（PRD 3.4 数据字段）
export interface Student {
  id: number
  name: string
  studentNo: string
  gender: '男' | '女'
  age: string
  className: string
  phone: string
  // 记录最近一次修改时间，由后端/API层自动维护，前端只读展示
  updatedAt: string
}

// 教师信息（PRD 3.5 数据字段）
export interface Teacher {
  id: number
  name: string
  teacherNo: string
  gender: '男' | '女'
  age: string
  phone: string
  updatedAt: string
}

// 班级信息（PRD 3.6 数据字段）
export interface ClassInfo {
  id: number
  name: string
  capacity: string
  teacherType: string
  updatedAt: string
}

// 课程信息（PRD 3.8 数据字段）
export interface Course {
  id: number
  name: string
  remark: string
  updatedAt: string
}

// 成绩信息（PRD 3.9 数据字段）
export interface Score {
  id: number
  courseId: number
  studentNo: string
  score: string
  updatedAt: string
}

// 系统公告（PRD 3.3 公告模块）
export interface Notice {
  id: number
  title: string
  author: string
  date: string
  updatedAt: string
}

// 首页统计数据（PRD 3.3 数据统计模块）
export interface DashboardStats {
  studentCount: number
  teacherCount: number
  courseCount: number
  pendingApprovalCount: number
}

// 操作记录（PRD 3.13 个人中心）
// 操作记录本身有 time 字段记录操作发生时间，无需额外的 updatedAt
export interface OperationLog {
  id: number
  action: string
  detail: string
  time: string
}

// 专业信息（PRD 3.7 数据字段）—— P1 全量开发新增
export interface Major {
  id: number
  name: string
  college: string
  remark: string
  updatedAt: string
}

// 考勤记录（PRD 3.10）—— P1 全量开发新增
export interface Attendance {
  id: number
  studentNo: string
  courseId: number
  date: string
  // 考勤状态：出勤/迟到/旷课/请假
  status: '出勤' | '迟到' | '旷课' | '请假'
  updatedAt: string
}

// 审批申请（PRD 3.11）—— P1 全量开发新增
export interface Approval {
  id: number
  studentNo: string
  // 申请类型：请假/休学/奖学金等
  type: string
  reason: string
  // 审批状态：待审批/已通过/已驳回
  status: '待审批' | '已通过' | '已驳回'
  opinion: string
  createdAt: string
  updatedAt: string
}

// 管理员账号（PRD 3.12）—— P1 全量开发新增
export interface Admin {
  id: number
  username: string
  realName: string
  phone: string
  updatedAt: string
}
