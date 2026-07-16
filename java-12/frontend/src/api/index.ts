// API 接口定义：封装所有后端接口调用
import request from './request'
import type {
  ApiResponse, Page, UserInfo, LoginResponse, Plate,
  PostResponse, CommentResponse, Message, OperateLog, StatsResponse,
} from '@/types'

// ===== 认证接口 =====

/** 用户注册 */
export const register = (data: { account: string; nickname: string; password: string; confirmPassword: string }) =>
  request.post<unknown, ApiResponse<LoginResponse>>('/auth/register', data)

/** 用户登录 */
export const login = (data: { account: string; password: string }) =>
  request.post<unknown, ApiResponse<LoginResponse>>('/auth/login', data)

/** 管理员登录 */
export const adminLogin = (data: { account: string; password: string }) =>
  request.post<unknown, ApiResponse<LoginResponse>>('/auth/admin/login', data)

/** 退出登录 */
export const logout = () => request.post<unknown, ApiResponse<null>>('/auth/logout')

/** 修改密码 */
export const changePassword = (data: { oldPassword: string; newPassword: string; confirmPassword: string }) =>
  request.put<unknown, ApiResponse<null>>('/auth/password', data)

// ===== 板块接口 =====

/** 获取全部板块 */
export const getPlates = () => request.get<unknown, ApiResponse<Plate[]>>('/plates')

/** 获取板块详情 */
export const getPlateById = (id: number) => request.get<unknown, ApiResponse<Plate>>(`/plates/${id}`)

// ===== 帖子接口 =====

/** 最新帖子 */
export const getLatestPosts = (page = 0, size = 20) =>
  request.get<unknown, ApiResponse<Page<PostResponse>>>('/posts/latest', { params: { page, size } })

/** 热门帖子 */
export const getHotPosts = (page = 0, size = 20) =>
  request.get<unknown, ApiResponse<Page<PostResponse>>>('/posts/hot', { params: { page, size } })

/** 置顶帖子 */
export const getTopPosts = () => request.get<unknown, ApiResponse<PostResponse[]>>('/posts/top')

/** 搜索帖子 */
export const searchPosts = (params: {
  keyword?: string; plateId?: number; timeRange?: string; sortBy?: string; page?: number; size?: number
}) => request.get<unknown, ApiResponse<Page<PostResponse>>>('/posts/search', { params: { ...params } })

/** 按板块查询帖子 */
export const getPostsByPlate = (plateId: number, page = 0, size = 20) =>
  request.get<unknown, ApiResponse<Page<PostResponse>>>(`/posts/plate/${plateId}`, { params: { page, size } })

/** 帖子详情 */
export const getPostById = (id: number) => request.get<unknown, ApiResponse<PostResponse>>(`/posts/${id}`)

/** 发布帖子 */
export const createPost = (data: { title: string; content: string; plateId: number }) =>
  request.post<unknown, ApiResponse<PostResponse>>('/posts', data)

/** 编辑帖子 */
export const updatePost = (id: number, data: { title: string; content: string; plateId: number }) =>
  request.put<unknown, ApiResponse<PostResponse>>(`/posts/${id}`, data)

/** 删除帖子 */
export const deletePost = (id: number) => request.delete<unknown, ApiResponse<null>>(`/posts/${id}`)

/** 点赞/取消点赞 */
export const toggleLike = (id: number) =>
  request.post<unknown, ApiResponse<{ liked: boolean }>>(`/posts/${id}/like`)

/** 收藏/取消收藏 */
export const toggleCollect = (id: number) =>
  request.post<unknown, ApiResponse<{ collected: boolean }>>(`/posts/${id}/collect`)

// ===== 评论接口 =====

/** 获取帖子评论 */
export const getComments = (postId: number) =>
  request.get<unknown, ApiResponse<CommentResponse[]>>('/comments', { params: { postId } })

/** 发表评论 */
export const createComment = (data: { postId: number; content: string; parentId?: number | null }) =>
  request.post<unknown, ApiResponse<CommentResponse>>('/comments', data)

/** 删除评论 */
export const deleteComment = (id: number) => request.delete<unknown, ApiResponse<null>>(`/comments/${id}`)

// ===== 个人中心接口 =====

/** 获取个人资料 */
export const getProfile = () => request.get<unknown, ApiResponse<UserInfo>>('/user/profile')

/** 修改个人资料 */
export const updateProfile = (data: { nickname?: string; avatar?: string; signature?: string }) =>
  request.put<unknown, ApiResponse<UserInfo>>('/user/profile', data)

/** 我的发帖 */
export const getMyPosts = (page = 0, size = 20) =>
  request.get<unknown, ApiResponse<Page<PostResponse>>>('/user/posts', { params: { page, size } })

/** 我的收藏 */
export const getMyCollects = () => request.get<unknown, ApiResponse<PostResponse[]>>('/user/collects')

/** 我的评论 */
export const getMyComments = () => request.get<unknown, ApiResponse<CommentResponse[]>>('/user/comments')

/** 站内消息 */
export const getMyMessages = () => request.get<unknown, ApiResponse<Message[]>>('/user/messages')

/** 未读消息数 */
export const getUnreadCount = () =>
  request.get<unknown, ApiResponse<{ count: number }>>('/user/messages/unread-count')

/** 标记消息已读 */
export const markMessageRead = (id: number) =>
  request.put<unknown, ApiResponse<null>>(`/user/messages/${id}/read`)

// ===== 管理员接口 =====

/** 创建板块 */
export const adminCreatePlate = (data: { name: string; description: string; icon?: string; sortOrder?: number }) =>
  request.post<unknown, ApiResponse<Plate>>('/admin/plates', data)

/** 编辑板块 */
export const adminUpdatePlate = (id: number, data: { name: string; description: string; icon?: string; sortOrder?: number }) =>
  request.put<unknown, ApiResponse<Plate>>(`/admin/plates/${id}`, data)

/** 删除板块 */
export const adminDeletePlate = (id: number) => request.delete<unknown, ApiResponse<null>>(`/admin/plates/${id}`)

/** 用户列表 */
export const adminGetUsers = (page = 0, size = 20) =>
  request.get<unknown, ApiResponse<Page<UserInfo>>>('/admin/users', { params: { page, size } })

/** 封禁用户 */
export const adminBanUser = (id: number) => request.put<unknown, ApiResponse<null>>(`/admin/users/${id}/ban`)

/** 解封用户 */
export const adminUnbanUser = (id: number) => request.put<unknown, ApiResponse<null>>(`/admin/users/${id}/unban`)

/** 分配版主 */
export const adminAssignModerator = (data: { userId: number; plateId: number }) =>
  request.post<unknown, ApiResponse<null>>('/admin/moderators', data)

/** 回收版主 */
export const adminRemoveModerator = (userId: number, plateId: number) =>
  request.delete<unknown, ApiResponse<null>>('/admin/moderators', { params: { userId, plateId } })

/** 查询板块版主 */
export const adminGetPlateModerators = (plateId: number) =>
  request.get<unknown, ApiResponse<any[]>>(`/admin/plates/${plateId}/moderators`)

/** 操作日志 */
export const adminGetLogs = (page = 0, size = 20) =>
  request.get<unknown, ApiResponse<Page<OperateLog>>>('/admin/logs', { params: { page, size } })

/** 站点统计 */
export const adminGetStats = () => request.get<unknown, ApiResponse<StatsResponse>>('/admin/stats')

// ===== 版主接口 =====

/** 版主删帖 */
export const modDeletePost = (id: number) => request.delete<unknown, ApiResponse<null>>(`/moderator/posts/${id}`)

/** 版主屏蔽评论 */
export const modHideComment = (id: number) => request.put<unknown, ApiResponse<null>>(`/moderator/comments/${id}/hide`)

/** 版主置顶/取消置顶 */
export const modToggleTop = (id: number) => request.put<unknown, ApiResponse<null>>(`/moderator/posts/${id}/top`)
