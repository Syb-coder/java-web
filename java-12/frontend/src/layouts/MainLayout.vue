<script setup lang="ts">
// 前台主布局：顶部导航栏 + 内容区域
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { logout as apiLogout, getUnreadCount } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

// 获取未读消息数
async function fetchUnread() {
  if (!userStore.isLoggedIn) return
  try {
    const resp = await getUnreadCount()
    unreadCount.value = resp.data.count
  } catch { /* 静默处理 */ }
}

// 退出登录
async function handleLogout() {
  try {
    await apiLogout()
  } finally {
    userStore.logout()
    router.push('/')
  }
}

onMounted(() => {
  fetchUnread()
})
</script>

<template>
  <div class="min-h-full flex flex-col">
    <!-- 顶部导航栏 -->
    <header class="bg-white border-b border-ink-200 sticky top-0 z-50">
      <div class="max-w-6xl mx-auto px-4 h-14 flex items-center justify-between">
        <!-- Logo -->
        <router-link to="/" class="flex items-center gap-2 text-primary-600 font-bold text-lg">
          <span>📖</span>
          <span>网文论坛</span>
        </router-link>

        <!-- 右侧操作区 -->
        <div class="flex items-center gap-4">
          <!-- 搜索入口 -->
          <router-link to="/search" class="text-ink-500 hover:text-primary-500 text-sm">
            搜索
          </router-link>

          <!-- 未登录：显示登录/注册按钮 -->
          <template v-if="!userStore.isLoggedIn">
            <router-link to="/login" class="text-ink-600 hover:text-primary-500 text-sm">
              登录
            </router-link>
            <router-link to="/register"
              class="bg-primary-500 text-white px-4 py-1.5 rounded-lg text-sm hover:bg-primary-600 transition">
              注册
            </router-link>
          </template>

          <!-- 已登录：显示用户菜单 -->
          <template v-else>
            <!-- 发帖按钮 -->
            <router-link to="/post/create"
              class="bg-primary-500 text-white px-4 py-1.5 rounded-lg text-sm hover:bg-primary-600 transition">
              发帖
            </router-link>

            <!-- 消息通知 -->
            <router-link to="/profile" class="relative text-ink-500 hover:text-primary-500">
              <span class="text-xl">🔔</span>
              <span v-if="unreadCount > 0"
                class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
                {{ unreadCount > 99 ? '99+' : unreadCount }}
              </span>
            </router-link>

            <!-- 用户头像/昵称下拉 -->
            <div class="relative group">
              <button class="flex items-center gap-2 text-sm text-ink-700 hover:text-primary-500">
                <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar"
                  class="w-8 h-8 rounded-full object-cover" alt="avatar" />
                <span v-else class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-600">
                  {{ userStore.userInfo?.nickname?.charAt(0) }}
                </span>
                <span>{{ userStore.userInfo?.nickname }}</span>
              </button>
              <!-- 下拉菜单 -->
              <div class="absolute right-0 top-full mt-1 bg-white border border-ink-200 rounded-lg shadow-lg py-2 hidden group-hover:block min-w-[120px]">
                <router-link to="/profile" class="block px-4 py-2 text-sm text-ink-600 hover:bg-ink-50">
                  个人中心
                </router-link>
                <router-link v-if="userStore.isAdmin" to="/admin" class="block px-4 py-2 text-sm text-ink-600 hover:bg-ink-50">
                  后台管理
                </router-link>
                <button @click="handleLogout" class="block w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-ink-50">
                  退出登录
                </button>
              </div>
            </div>
          </template>
        </div>
      </div>
    </header>

    <!-- 内容区域 -->
    <main class="flex-1 max-w-6xl mx-auto w-full px-4 py-6">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="border-t border-ink-200 py-4 text-center text-ink-400 text-sm">
      网文论坛 © 2026
    </footer>
  </div>
</template>
