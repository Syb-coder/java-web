// API 封装层：基于 Mock 数据模拟后端接口，统一以 Promise 形式对外暴露
// 当前阶段不依赖真实后端，使用模块级数据副本 + setTimeout 模拟网络延迟
// 所有业务实体的 create/update 操作自动维护 updatedAt 字段

import {
  students,
  teachers,
  classes,
  courses,
  scores,
  notices,
  operationLogs,
  majors,
  attendances,
  approvals,
  admins,
} from '@/mock/data'
import type {
  Student,
  Teacher,
  ClassInfo,
  Course,
  Score,
  Notice,
  DashboardStats,
  OperationLog,
  Major,
  Attendance,
  Approval,
  Admin,
} from '@/types'

// 模拟网络延迟（毫秒）：300ms 贴近真实接口响应体感
const MOCK_DELAY = 300

/**
 * 格式化当前时间为 yyyy-MM-dd HH:mm:ss
 * 用于 updatedAt 字段统一格式，避免各处格式不一致
 */
function formatNow(): string {
  return new Date().toLocaleString('zh-CN', { hour12: false })
}

/**
 * 包裹值并延迟返回，模拟一次异步网络请求
 * @param value 最终 resolve 的数据
 */
function delay<T>(value: T): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(value), MOCK_DELAY))
}

/**
 * 计算自增 id：取当前列表中最大 id + 1，列表为空时从 1 开始
 * @param list 当前数据集合
 */
function nextId(list: { id: number }[]): number {
  return list.length ? Math.max(...list.map((item) => item.id)) + 1 : 1
}

/**
 * 通用资源 CRUD 工厂：为各实体生成一致的增删改查方法
 * 使用深拷贝对外暴露数据，避免外部修改污染模块内部状态
 * create/update 时自动注入 updatedAt，前端无需手动传入
 * @param initialData 初始数据（来自 mock）
 */
function createResource<T extends { id: number; updatedAt: string }>(initialData: T[]) {
  // 模块级数据副本：深拷贝原始 mock 数据，运行时只操作此副本
  const store: T[] = structuredClone(initialData)

  return {
    // 暴露内部存储引用，供 dashboard 等聚合查询使用
    store,
    api: {
      list: (): Promise<T[]> => delay(structuredClone(store)),
      // create 参数排除 id 与 updatedAt：这两个字段由后端自动维护
      create: (data: Omit<T, 'id' | 'updatedAt'>): Promise<T> => {
        const item = { ...data, id: nextId(store), updatedAt: formatNow() } as T
        store.push(item)
        return delay(structuredClone(item))
      },
      update: (id: number, data: Partial<T>): Promise<T> => {
        const idx = store.findIndex((item) => item.id === id)
        if (idx === -1) {
          return Promise.reject(new Error(`id 为 ${id} 的记录不存在`))
        }
        // 更新时自动刷新 updatedAt，保证时间戳与数据变更同步
        store[idx] = { ...store[idx], ...data, updatedAt: formatNow() }
        return delay(structuredClone(store[idx]))
      },
      remove: (id: number): Promise<void> => {
        const idx = store.findIndex((item) => item.id === id)
        if (idx === -1) {
          return Promise.reject(new Error(`id 为 ${id} 的记录不存在`))
        }
        store.splice(idx, 1)
        return delay(undefined)
      },
    },
  }
}

// 各实体资源实例：分别持有独立的数据副本
const studentResource = createResource<Student>(students)
const teacherResource = createResource<Teacher>(teachers)
const classResource = createResource<ClassInfo>(classes)
const courseResource = createResource<Course>(courses)
const scoreResource = createResource<Score>(scores)
const noticeResource = createResource<Notice>(notices)
const majorResource = createResource<Major>(majors)
const attendanceResource = createResource<Attendance>(attendances)
const approvalResource = createResource<Approval>(approvals)
const adminResource = createResource<Admin>(admins)

// 操作记录仅支持查询，使用独立深拷贝副本，避免被修改
const logStore: OperationLog[] = structuredClone(operationLogs)

export const api = {
  student: studentResource.api,
  teacher: teacherResource.api,
  class: classResource.api,
  course: courseResource.api,
  score: scoreResource.api,
  major: majorResource.api,
  attendance: attendanceResource.api,
  approval: approvalResource.api,
  admin: adminResource.api,
  // 公告按 PRD 仅提供查询、新增与删除能力，不暴露 update
  notice: {
    list: noticeResource.api.list,
    create: noticeResource.api.create,
    remove: noticeResource.api.remove,
  },
  dashboard: {
    // 首页统计：实时聚合各资源当前数量
    stats: (): Promise<DashboardStats> =>
      delay({
        studentCount: studentResource.store.length,
        teacherCount: teacherResource.store.length,
        courseCount: courseResource.store.length,
        // 待审批数量：统计状态为"待审批"的申请记录
        pendingApprovalCount: approvalResource.store.filter((a) => a.status === '待审批').length,
      }),
  },
  log: {
    list: (): Promise<OperationLog[]> => delay(structuredClone(logStore)),
  },
}
