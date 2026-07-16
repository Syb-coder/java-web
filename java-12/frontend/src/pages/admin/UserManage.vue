<script setup lang="ts">
// 用户管理：查看用户列表、封禁/解封
import { ref, onMounted } from 'vue'
import { adminGetUsers, adminBanUser, adminUnbanUser } from '@/api'
import type { UserInfo, Page } from '@/types'

const users = ref<UserInfo[]>([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(false)

// 加载用户列表
async function fetchUsers() {
  loading.value = true
  try {
    const resp = await adminGetUsers(page.value, 20)
    users.value = resp.data.content
    totalPages.value = resp.data.totalPages
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

// 封禁用户
async function handleBan(user: UserInfo) {
  if (!confirm(`确定封禁用户"${user.nickname}"吗？`)) return
  try {
    await adminBanUser(user.id)
    await fetchUsers()
  } catch (e: any) {
    alert(e.message || '操作失败')
  }
}

// 解封用户
async function handleUnban(user: UserInfo) {
  if (!confirm(`确定解封用户"${user.nickname}"吗？`)) return
  try {
    await adminUnbanUser(user.id)
    await fetchUsers()
  } catch (e: any) {
    alert(e.message || '操作失败')
  }
}

// 翻页
function changePage(newPage: number) {
  if (newPage < 0 || newPage >= totalPages.value) return
  page.value = newPage
  fetchUsers()
}

// 格式化时间
function formatTime(time: string) {
  if (!time) return '-'
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div>
    <h2 class="text-lg font-medium text-ink-800 mb-4">用户列表</h2>

    <div v-if="loading" class="text-center text-ink-400 py-12">加载中...</div>
    <div v-else class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <table class="w-full">
        <thead class="bg-ink-50 border-b border-ink-200">
          <tr>
            <th class="px-4 py-3 text-left text-sm text-ink-500">ID</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">账号</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">昵称</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">角色</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">状态</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">发帖数</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">注册时间</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">更新时间</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-ink-100">
          <tr v-for="user in users" :key="user.id" class="hover:bg-ink-50">
            <td class="px-4 py-3 text-sm text-ink-600">{{ user.id }}</td>
            <td class="px-4 py-3 text-sm text-ink-800">{{ user.account }}</td>
            <td class="px-4 py-3 text-sm text-ink-800">{{ user.nickname }}</td>
            <td class="px-4 py-3">
              <span :class="['text-xs px-2 py-0.5 rounded',
                user.role === 'ADMIN' ? 'bg-primary-50 text-primary-600' : 'bg-ink-100 text-ink-500']">
                {{ user.role === 'ADMIN' ? '管理员' : '用户' }}
              </span>
            </td>
            <td class="px-4 py-3">
              <span :class="['text-xs px-2 py-0.5 rounded',
                user.status === 'NORMAL' ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600']">
                {{ user.status === 'NORMAL' ? '正常' : '封禁' }}
              </span>
            </td>
            <td class="px-4 py-3 text-sm text-ink-600">{{ user.postCount }}</td>
            <td class="px-4 py-3 text-sm text-ink-400">{{ formatTime(user.createTime) }}</td>
            <td class="px-4 py-3 text-sm text-accent-400">{{ formatTime(user.updateTime) }}</td>
            <td class="px-4 py-3">
              <button v-if="user.role !== 'ADMIN'"
                @click="user.status === 'NORMAL' ? handleBan(user) : handleUnban(user)"
                :class="['text-sm',
                  user.status === 'NORMAL' ? 'text-red-500 hover:text-red-600' : 'text-green-500 hover:text-green-600']">
                {{ user.status === 'NORMAL' ? '封禁' : '解封' }}
              </button>
              <span v-else class="text-sm text-ink-300">-</span>
            </td>
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
