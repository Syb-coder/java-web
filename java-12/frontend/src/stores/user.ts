// 用户状态管理：登录态、Token、用户信息
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginResponse } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 恢复登录态
  const token = ref<string | null>(localStorage.getItem('token'))
  const storedUser = localStorage.getItem('userInfo')
  const userInfo = ref<UserInfo | null>(storedUser ? JSON.parse(storedUser) : null)

  // 登录态计算属性
  const isLoggedIn = computed(() => !!token.value && !!userInfo.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  /** 登录成功后保存 Token 和用户信息 */
  function login(resp: LoginResponse) {
    token.value = resp.token
    userInfo.value = {
      id: resp.id,
      account: resp.account,
      nickname: resp.nickname,
      avatar: resp.avatar,
      role: resp.role,
      status: 'NORMAL',
      signature: null,
      postCount: 0,
      createTime: '',
    } as UserInfo
    localStorage.setItem('token', resp.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  /** 更新用户信息（修改资料后调用） */
  function updateUserInfo(info: UserInfo) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  /** 退出登录：清空状态与本地存储 */
  function logout() {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, isAdmin, login, updateUserInfo, logout }
})
