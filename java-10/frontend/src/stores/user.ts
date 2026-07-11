import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 用户角色类型：对应 PRD 2.1 节三种角色
export type Role = 'admin' | 'teacher' | 'student'

// 用户信息接口
export interface UserInfo {
  username: string
  realName: string
  role: Role
  avatar?: string
}

// 用户状态管理：登录态、当前用户信息、退出登录
export const useUserStore = defineStore('user', () => {
  // 从 localStorage 恢复登录态，避免刷新丢失
  const stored = localStorage.getItem('userInfo')
  const userInfo = ref<UserInfo | null>(stored ? JSON.parse(stored) : null)

  // 登录态计算属性：userInfo 非空即视为已登录
  const isLoggedIn = computed(() => !!userInfo.value)

  // 登录成功后保存用户信息
  function login(info: UserInfo) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 退出登录：清空状态与本地存储
  function logout() {
    userInfo.value = null
    localStorage.removeItem('userInfo')
  }

  return { userInfo, isLoggedIn, login, logout }
})
