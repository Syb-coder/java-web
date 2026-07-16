<script setup lang="ts">
// 操作日志：查看管理员/版主的操作记录
import { ref, onMounted } from 'vue'
import { adminGetLogs } from '@/api'
import type { OperateLog } from '@/types'

const logs = ref<OperateLog[]>([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(false)

// 加载日志
async function fetchLogs() {
  loading.value = true
  try {
    const resp = await adminGetLogs(page.value, 20)
    logs.value = resp.data.content
    totalPages.value = resp.data.totalPages
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

// 翻页
function changePage(newPage: number) {
  if (newPage < 0 || newPage >= totalPages.value) return
  page.value = newPage
  fetchLogs()
}

// 格式化时间
function formatTime(time: string) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 操作类型标签颜色
function actionColor(action: string) {
  if (action.includes('删除')) return 'bg-red-50 text-red-600'
  if (action.includes('封禁')) return 'bg-red-50 text-red-600'
  if (action.includes('创建') || action.includes('分配')) return 'bg-green-50 text-green-600'
  if (action.includes('编辑') || action.includes('解封')) return 'bg-blue-50 text-blue-600'
  return 'bg-ink-100 text-ink-500'
}

onMounted(() => {
  fetchLogs()
})
</script>

<template>
  <div>
    <h2 class="text-lg font-medium text-ink-800 mb-4">操作日志</h2>

    <div v-if="loading" class="text-center text-ink-400 py-12">加载中...</div>
    <div v-else class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <div v-if="logs.length === 0" class="text-center text-ink-400 py-12">暂无操作记录</div>
      <table v-else class="w-full">
        <thead class="bg-ink-50 border-b border-ink-200">
          <tr>
            <th class="px-4 py-3 text-left text-sm text-ink-500">ID</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作人</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作类型</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作对象</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">IP</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作时间</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">更新时间</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-ink-100">
          <tr v-for="log in logs" :key="log.id" class="hover:bg-ink-50">
            <td class="px-4 py-3 text-sm text-ink-400">{{ log.id }}</td>
            <td class="px-4 py-3 text-sm text-ink-800">{{ log.adminName }} (#{{ log.adminId }})</td>
            <td class="px-4 py-3">
              <span :class="['text-xs px-2 py-0.5 rounded', actionColor(log.action)]">
                {{ log.action }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-ink-600">{{ log.target }}</td>
            <td class="px-4 py-3 text-sm text-ink-400">{{ log.ip }}</td>
            <td class="px-4 py-3 text-sm text-ink-400">{{ formatTime(log.createTime) }}</td>
            <td class="px-4 py-3 text-sm text-accent-400">{{ formatTime(log.updateTime) }}</td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 py-4 border-t border-ink-100">
        <button @click="changePage(page - 1)" :disabled="page === 0"
          class="px-3 py-1 text-sm border border-ink-300 rounded hover:bg-ink-50 disabled:opacity-30">上一页</button>
        <span class="text-sm text-ink-500">{{ page + 1 }} / {{ totalPages }}</span>
        <button @click="changePage(page + 1)" :disabled="page >= totalPages - 1"
          class="px-3 py-1 text-sm border border-ink-300 rounded hover:bg-ink-50 disabled:opacity-30">下一页</button>
      </div>
    </div>
  </div>
</template>
