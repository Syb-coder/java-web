<script setup lang="ts">
// 后台仪表盘：站点统计数据展示
import { ref, onMounted } from 'vue'
import { adminGetStats } from '@/api'
import type { StatsResponse } from '@/types'

const stats = ref<StatsResponse | null>(null)
const loading = ref(true)

async function fetchStats() {
  try {
    const resp = await adminGetStats()
    stats.value = resp.data
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<template>
  <div>
    <div v-if="loading" class="text-center text-ink-400 py-12">加载中...</div>
    <template v-else-if="stats">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 mb-6">
        <div v-for="item in [
          { label: '总用户数', value: stats.totalUsers, icon: '👥', color: 'bg-blue-50 text-blue-600' },
          { label: '总帖子数', value: stats.totalPosts, icon: '📝', color: 'bg-orange-50 text-orange-600' },
          { label: '总评论数', value: stats.totalComments, icon: '💬', color: 'bg-green-50 text-green-600' },
          { label: '总板块数', value: stats.totalPlates, icon: '📂', color: 'bg-purple-50 text-purple-600' },
          { label: '今日新帖', value: stats.todayPosts, icon: '🔥', color: 'bg-red-50 text-red-600' },
        ]" :key="item.label" class="bg-white rounded-xl border border-ink-200 p-5">
          <div :class="['w-10 h-10 rounded-lg flex items-center justify-center text-xl mb-3', item.color]">
            {{ item.icon }}
          </div>
          <div class="text-2xl font-bold text-ink-800">{{ item.value }}</div>
          <div class="text-sm text-ink-400 mt-1">{{ item.label }}</div>
        </div>
      </div>

      <!-- 说明 -->
      <div class="bg-white rounded-xl border border-ink-200 p-6">
        <h2 class="font-medium text-ink-700 mb-3">管理说明</h2>
        <ul class="text-sm text-ink-500 space-y-2">
          <li>• <b>板块管理</b>：创建、编辑、删除论坛板块</li>
          <li>• <b>用户管理</b>：查看用户列表，封禁/解封违规用户</li>
          <li>• <b>版主管理</b>：为指定板块分配或回收版主权限</li>
          <li>• <b>操作日志</b>：查看管理员和版主的操作记录</li>
        </ul>
      </div>
    </template>
  </div>
</template>
