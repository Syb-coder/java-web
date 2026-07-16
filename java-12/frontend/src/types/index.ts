// ===== 通用类型 =====

/** 统一 API 响应格式 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/** 分页响应 */
export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// ===== 用户相关 =====

export type Role = 'USER' | 'ADMIN'
export type UserStatus = 'NORMAL' | 'BANNED'

/** 用户信息 */
export interface UserInfo {
  id: number
  account: string
  nickname: string
  avatar: string | null
  signature: string | null
  role: Role
  status: UserStatus
  postCount: number
  createTime: string
  updateTime: string
}

/** 登录响应 */
export interface LoginResponse {
  id: number
  account: string
  nickname: string
  avatar: string | null
  role: Role
  token: string
}

// ===== 板块相关 =====

export interface Plate {
  id: number
  name: string
  description: string | null
  icon: string | null
  postCount: number
  sortOrder: number
  createTime: string
  updateTime: string
}

// ===== 帖子相关 =====

/** 帖子响应 */
export interface PostResponse {
  id: number
  userId: number
  plateId: number
  title: string
  content: string
  likeCount: number
  collectCount: number
  commentCount: number
  viewCount: number
  isTop: boolean
  isDeleted: boolean
  createTime: string
  updateTime: string
  authorName: string | null
  authorAvatar: string | null
  plateName: string | null
  liked: boolean
  collected: boolean
}

// ===== 评论相关 =====

export interface CommentResponse {
  id: number
  postId: number
  userId: number
  parentId: number | null
  content: string
  isHidden: boolean
  isDeleted: boolean
  createTime: string
  updateTime: string
  authorName: string | null
  authorAvatar: string | null
}

// ===== 消息相关 =====

export interface Message {
  id: number
  userId: number
  content: string
  isRead: boolean
  createTime: string
  updateTime: string
}

// ===== 操作日志 =====

export interface OperateLog {
  id: number
  adminId: number
  adminName: string
  action: string
  target: string
  ip: string
  createTime: string
  updateTime: string
}

// ===== 统计数据 =====

export interface StatsResponse {
  totalUsers: number
  totalPosts: number
  totalComments: number
  totalPlates: number
  todayPosts: number
}
