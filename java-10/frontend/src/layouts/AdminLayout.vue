<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import {
  LayoutDashboard,
  Users,
  GraduationCap,
  School,
  BookOpen,
  ClipboardList,
  UserCircle,
  LogOut,
  Menu,
  Bell,
  Layers,
  CalendarCheck,
  FileCheck2,
  ShieldCheck,
  CalendarDays,
} from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import { cn } from '@/lib/utils'

// 路由实例：用于侧栏高亮当前菜单项
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

// 侧栏菜单配置：集中维护菜单项与路由的映射关系
const menuItems = [
  { name: 'dashboard', label: '首页', icon: LayoutDashboard },
  { name: 'student', label: '学生管理', icon: Users },
  { name: 'teacher', label: '教师管理', icon: GraduationCap },
  { name: 'class', label: '班级管理', icon: School },
  { name: 'major', label: '专业管理', icon: Layers },
  { name: 'course', label: '课程管理', icon: BookOpen },
  { name: 'score', label: '成绩管理', icon: ClipboardList },
  { name: 'attendance', label: '考勤管理', icon: CalendarCheck },
  { name: 'approval', label: '审批管理', icon: FileCheck2 },
  { name: 'schedule', label: '课表展示', icon: CalendarDays },
  { name: 'admin-user', label: '管理员管理', icon: ShieldCheck },
  { name: 'profile', label: '个人中心', icon: UserCircle },
]

// 当前激活菜单：基于路由 name 匹配，保证刷新后高亮正确
const activeMenu = computed(() => route.name as string)

// 当前页面标题：从路由 meta.title 读取
const pageTitle = computed(() => (route.meta.title as string) || '学生信息管理系统')

// 角色中文映射：用于顶栏显示用户角色
const roleLabel = computed(() => {
  const map: Record<string, string> = { admin: '管理员', teacher: '教师', student: '学生' }
  return map[userInfo.value?.role ?? 'admin'] ?? '管理员'
})

// 退出登录：清空登录态并跳转登录页
function handleLogout() {
  userStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <div class="flex h-screen overflow-hidden bg-ink-50">
    <!-- 侧栏：固定宽度，深色背景，包含 Logo 与导航菜单 -->
    <aside class="flex w-60 shrink-0 flex-col bg-ink-900 text-white">
      <!-- Logo 区域 -->
      <div class="flex h-16 items-center gap-2 px-6">
        <div class="flex h-9 w-9 items-center justify-center rounded-lg bg-primary-600">
          <School class="h-5 w-5 text-white" />
        </div>
        <span class="text-base font-semibold tracking-wide">学生信息管理系统</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="mt-2 flex-1 space-y-1 px-3 py-2">
        <router-link
          v-for="item in menuItems"
          :key="item.name"
          :to="{ name: item.name }"
          :class="
            cn(
              'flex items-center gap-3 rounded-md px-3 py-2.5 text-sm font-medium transition-colors',
              activeMenu === item.name
                ? 'bg-primary-600 text-white'
                : 'text-ink-300 hover:bg-ink-800 hover:text-white',
            )
          "
        >
          <component :is="item.icon" class="h-[18px] w-[18px]" />
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- 侧栏底部：版本信息 -->
      <div class="border-t border-ink-800 px-6 py-4 text-xs text-ink-400">
        <div>版本 v1.0.0</div>
        <div class="mt-1">© 2026 学生信息管理系统</div>
      </div>
    </aside>

    <!-- 右侧主区域：顶栏 + 内容区 -->
    <div class="flex flex-1 flex-col overflow-hidden">
      <!-- 顶栏 -->
      <header
        class="flex h-16 shrink-0 items-center justify-between border-b border-ink-200 bg-white px-6 shadow-sm"
      >
        <!-- 左侧：页面标题 -->
        <div class="flex items-center gap-3">
          <Menu class="h-5 w-5 text-ink-400" />
          <h1 class="text-lg font-semibold text-ink-800">{{ pageTitle }}</h1>
        </div>

        <!-- 右侧：通知与用户信息 -->
        <div class="flex items-center gap-4">
          <!-- 通知图标 -->
          <button
            class="relative rounded-full p-2 text-ink-500 transition-colors hover:bg-ink-100 hover:text-ink-700"
          >
            <Bell class="h-5 w-5" />
            <!-- 未读通知红点 -->
            <span class="absolute right-1.5 top-1.5 h-2 w-2 rounded-full bg-red-500"></span>
          </button>

          <!-- 用户信息 -->
          <div class="flex items-center gap-3 border-l border-ink-200 pl-4">
            <div class="flex h-9 w-9 items-center justify-center rounded-full bg-primary-100 text-sm font-semibold text-primary-700">
              {{ userInfo?.realName?.charAt(0) ?? '管' }}
            </div>
            <div class="text-sm">
              <div class="font-medium text-ink-800">{{ userInfo?.realName ?? '管理员' }}</div>
              <div class="text-xs text-ink-500">{{ roleLabel }}</div>
            </div>
          </div>

          <!-- 退出按钮 -->
          <button
            class="flex items-center gap-1.5 rounded-md px-3 py-2 text-sm text-ink-600 transition-colors hover:bg-red-50 hover:text-red-600"
            @click="handleLogout"
          >
            <LogOut class="h-4 w-4" />
            <span>退出</span>
          </button>
        </div>
      </header>

      <!-- 内容区：路由出口，overflow-y-auto 支持页面滚动 -->
      <main class="flex-1 overflow-y-auto p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>
