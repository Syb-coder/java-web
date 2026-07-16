<script setup lang="ts">
// 后台管理布局：左侧菜单 + 顶部栏 + 内容区域
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { logout as apiLogout } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 菜单项定义
const menuItems = [
  { name: 'AdminDashboard', label: '仪表盘', icon: '📊', path: '/admin' },
  { name: 'AdminPlates', label: '板块管理', icon: '📂', path: '/admin/plates' },
  { name: 'AdminUsers', label: '用户管理', icon: '👥', path: '/admin/users' },
  { name: 'AdminModerators', label: '版主管理', icon: '🛡️', path: '/admin/moderators' },
  { name: 'AdminLogs', label: '操作日志', icon: '📋', path: '/admin/logs' },
]

// 退出登录
async function handleLogout() {
  try {
    await apiLogout()
  } finally {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<template>
  <div class="min-h-full flex">
    <!-- 左侧菜单 -->
    <aside class="w-56 bg-ink-800 text-white flex flex-col">
      <!-- Logo -->
      <div class="h-14 flex items-center px-6 border-b border-ink-700">
        <router-link to="/" class="text-primary-400 font-bold text-lg">网文论坛</router-link>
      </div>
      <!-- 菜单列表 -->
      <nav class="flex-1 py-4">
        <router-link v-for="item in menuItems" :key="item.name" :to="item.path"
          :class="[
            'flex items-center gap-3 px-6 py-3 text-sm transition',
            route.name === item.name
              ? 'bg-primary-600 text-white border-l-4 border-primary-300'
              : 'text-ink-300 hover:bg-ink-700 hover:text-white'
          ]">
          <span>{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>
      <!-- 底部用户信息 -->
      <div class="p-4 border-t border-ink-700">
        <div class="text-sm text-ink-300 mb-2">{{ userStore.userInfo?.nickname }}</div>
        <button @click="handleLogout" class="text-xs text-red-400 hover:text-red-300">
          退出登录
        </button>
      </div>
    </aside>

    <!-- 右侧内容 -->
    <div class="flex-1 flex flex-col">
      <!-- 顶部栏 -->
      <header class="h-14 bg-white border-b border-ink-200 flex items-center justify-between px-6">
        <h1 class="text-lg font-medium text-ink-800">
          {{ menuItems.find(m => m.name === route.name)?.label || '后台管理' }}
        </h1>
        <router-link to="/" class="text-sm text-ink-500 hover:text-primary-500">
          ← 返回前台
        </router-link>
      </header>
      <!-- 内容区域 -->
      <main class="flex-1 bg-ink-50 p-6 overflow-auto">
        <router-view />
      </main>
    </div>
  </div>
</template>
