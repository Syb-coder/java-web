<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Users, GraduationCap, BookOpen, ClipboardList,
  School, CalendarCheck, FileCheck, KeyRound,
} from 'lucide-vue-next'
import { api } from '@/api'
import type { DashboardStats, Notice } from '@/types'

// 首页数据：统计指标与系统公告，通过 onMounted 并行加载

const router = useRouter()

// 统计数据与加载状态
const stats = ref<DashboardStats | null>(null)
const statsLoading = ref(true)

// 公告列表与加载状态
const notices = ref<Notice[]>([])
const noticeLoading = ref(true)

// 统计卡片配置：图标、标签、取值函数、配色集中维护，便于后续扩展
const statCards = [
  { icon: Users, label: '学生总数', value: () => stats.value?.studentCount ?? 0, bg: 'bg-primary-100', color: 'text-primary-600' },
  { icon: GraduationCap, label: '教师总数', value: () => stats.value?.teacherCount ?? 0, bg: 'bg-accent-100', color: 'text-accent-600' },
  { icon: BookOpen, label: '开课数量', value: () => stats.value?.courseCount ?? 0, bg: 'bg-amber-100', color: 'text-amber-600' },
  { icon: ClipboardList, label: '待审批数量', value: () => stats.value?.pendingApprovalCount ?? 0, bg: 'bg-red-100', color: 'text-red-600' },
]

// 快捷入口配置：name 对应路由，考勤与审批暂无独立路由，降级跳转成绩管理占位
const quickEntries = [
  { icon: Users, label: '学生管理', route: 'student' },
  { icon: GraduationCap, label: '教师管理', route: 'teacher' },
  { icon: School, label: '班级管理', route: 'class' },
  { icon: BookOpen, label: '课程管理', route: 'course' },
  { icon: ClipboardList, label: '成绩管理', route: 'score' },
  { icon: CalendarCheck, label: '考勤管理', route: 'score' },
  { icon: FileCheck, label: '审批管理', route: 'score' },
  { icon: KeyRound, label: '修改密码', route: 'profile' },
]

function handleEntry(name: string) {
  router.push({ name })
}

onMounted(() => {
  // 并行加载统计数据与公告，互不阻塞
  api.dashboard.stats()
    .then((data) => { stats.value = data })
    .catch(() => { stats.value = null })
    .finally(() => { statsLoading.value = false })

  api.notice.list()
    .then((data) => { notices.value = data })
    .catch(() => { notices.value = [] })
    .finally(() => { noticeLoading.value = false })
})
</script>

<template>
  <div class="space-y-6">
    <!-- 数据统计模块：4 个卡片横向排列 -->
    <div class="grid grid-cols-4 gap-4">
      <div
        v-for="card in statCards"
        :key="card.label"
        class="flex items-center gap-4 rounded-xl bg-white p-5 shadow-card"
      >
        <!-- 左侧彩色图标圆背景 -->
        <div :class="['flex h-12 w-12 shrink-0 items-center justify-center rounded-full', card.bg]">
          <component :is="card.icon" :class="['h-6 w-6', card.color]" />
        </div>
        <!-- 右侧大数字 + 标题；加载时显示骨架屏 -->
        <div class="min-w-0">
          <div v-if="statsLoading" class="h-7 w-16 animate-pulse rounded bg-ink-100" />
          <div v-else class="text-2xl font-bold text-ink-800">{{ card.value() }}</div>
          <div class="mt-1 text-sm text-ink-500">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- 下方布局：左侧快捷入口占 2 列，右侧系统公告占 1 列 -->
    <div class="grid grid-cols-3 gap-6">
      <!-- 快捷入口模块 -->
      <div class="col-span-2 rounded-xl bg-white p-6 shadow-card">
        <h2 class="mb-4 text-base font-semibold text-ink-800">快捷入口</h2>
        <div class="grid grid-cols-4 gap-3">
          <button
            v-for="entry in quickEntries"
            :key="entry.label"
            type="button"
            class="flex flex-col items-center gap-2 rounded-lg border border-ink-100 bg-white py-4 transition-all hover:-translate-y-0.5 hover:border-primary-200 hover:shadow-cardhover"
            @click="handleEntry(entry.route)"
          >
            <component :is="entry.icon" class="h-6 w-6 text-primary-600" />
            <span class="text-xs font-medium text-ink-700">{{ entry.label }}</span>
          </button>
        </div>
      </div>

      <!-- 系统公告模块 -->
      <div class="col-span-1 rounded-xl bg-white p-6 shadow-card">
        <h2 class="mb-4 text-base font-semibold text-ink-800">系统公告</h2>
        <!-- 加载骨架 -->
        <div v-if="noticeLoading" class="space-y-3">
          <div v-for="i in 5" :key="i" class="h-8 animate-pulse rounded bg-ink-100" />
        </div>
        <!-- 公告列表 -->
        <ul v-else class="divide-y divide-ink-100">
          <li
            v-for="(item, index) in notices"
            :key="item.id"
            class="cursor-pointer px-1 py-2.5 transition-colors hover:bg-primary-50/60"
          >
            <div class="flex items-center justify-between gap-2">
              <span class="flex min-w-0 items-center gap-2">
                <span class="shrink-0 text-xs font-medium text-ink-400">{{ index + 1 }}.</span>
                <span class="truncate text-sm text-ink-700">{{ item.title }}</span>
              </span>
              <span class="shrink-0 text-xs text-ink-400">{{ item.date }}</span>
            </div>
            <div class="mt-1 pl-4 text-xs text-ink-400">发布人：{{ item.author }}</div>
          </li>
          <li v-if="!notices.length" class="py-8 text-center text-sm text-ink-400">
            暂无公告
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>
