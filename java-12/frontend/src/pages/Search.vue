<script setup lang="ts">
// 搜索页：支持关键词、板块、时间范围、排序方式筛选
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchPosts, getPlates } from '@/api'
import type { PostResponse, Plate } from '@/types'

const route = useRoute()
const router = useRouter()

const plates = ref<Plate[]>([])
const posts = ref<PostResponse[]>([])
const loading = ref(false)
const page = ref(0)
const totalPages = ref(0)

const filters = ref({
  keyword: (route.query.keyword as string) || '',
  plateId: route.query.plateId ? Number(route.query.plateId) : undefined,
  timeRange: 'all',
  sortBy: 'time',
})

// 加载板块
async function fetchPlates() {
  try {
    const resp = await getPlates()
    plates.value = resp.data
  } catch { /* 静默处理 */ }
}

// 执行搜索
async function search() {
  loading.value = true
  try {
    const resp = await searchPosts({
      keyword: filters.value.keyword || undefined,
      plateId: filters.value.plateId,
      timeRange: filters.value.timeRange,
      sortBy: filters.value.sortBy,
      page: page.value,
      size: 15,
    })
    posts.value = resp.data.content
    totalPages.value = resp.data.totalPages
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

// 触发搜索（重置页码）
function doSearch() {
  page.value = 0
  search()
}

// 翻页
function changePage(newPage: number) {
  if (newPage < 0 || newPage >= totalPages.value) return
  page.value = newPage
  search()
}

// 格式化时间
function formatTime(time: string) {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = (now.getTime() - date.getTime()) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  if (diff < 604800) return `${Math.floor(diff / 86400)}天前`
  return date.toLocaleDateString('zh-CN')
}

// 监听路由参数变化
watch(() => route.query, (q) => {
  filters.value.keyword = (q.keyword as string) || ''
  filters.value.plateId = q.plateId ? Number(q.plateId) : undefined
  doSearch()
}, { deep: true })

onMounted(() => {
  fetchPlates()
  search()
})
</script>

<template>
  <div class="space-y-4">
    <!-- 搜索条件 -->
    <div class="bg-white rounded-xl border border-ink-200 p-4 space-y-3">
      <!-- 关键词搜索 -->
      <div class="flex gap-2">
        <input v-model="filters.keyword" type="text" placeholder="搜索帖子标题或内容..."
          class="flex-1 px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500"
          @keyup.enter="doSearch" />
        <button @click="doSearch"
          class="bg-primary-500 text-white px-6 py-2 rounded-lg text-sm hover:bg-primary-600">
          搜索
        </button>
      </div>

      <!-- 筛选条件 -->
      <div class="flex flex-wrap items-center gap-4 text-sm">
        <!-- 板块筛选 -->
        <div class="flex items-center gap-2">
          <span class="text-ink-400">板块:</span>
          <select v-model="filters.plateId"
            class="border border-ink-300 rounded px-2 py-1 focus:outline-none focus:border-primary-500 bg-white">
            <option :value="undefined">全部</option>
            <option v-for="plate in plates" :key="plate.id" :value="plate.id">{{ plate.name }}</option>
          </select>
        </div>
        <!-- 时间范围 -->
        <div class="flex items-center gap-2">
          <span class="text-ink-400">时间:</span>
          <select v-model="filters.timeRange"
            class="border border-ink-300 rounded px-2 py-1 focus:outline-none focus:border-primary-500 bg-white">
            <option value="all">全部</option>
            <option value="today">今天</option>
            <option value="week">本周</option>
            <option value="month">本月</option>
          </select>
        </div>
        <!-- 排序方式 -->
        <div class="flex items-center gap-2">
          <span class="text-ink-400">排序:</span>
          <select v-model="filters.sortBy"
            class="border border-ink-300 rounded px-2 py-1 focus:outline-none focus:border-primary-500 bg-white">
            <option value="time">最新</option>
            <option value="hot">热门</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <div v-if="loading" class="py-12 text-center text-ink-400">搜索中...</div>
      <div v-else-if="posts.length === 0" class="py-12 text-center text-ink-400">
        未找到匹配的帖子
      </div>
      <div v-else class="divide-y divide-ink-100">
        <div v-for="post in posts" :key="post.id"
          class="p-4 cursor-pointer hover:bg-ink-50 transition"
          @click="router.push(`/post/${post.id}`)">
          <div class="flex items-start justify-between gap-4">
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <span v-if="post.plateName" class="text-xs text-primary-500 bg-primary-50 px-1.5 py-0.5 rounded">
                  {{ post.plateName }}
                </span>
                <h3 class="font-medium text-ink-800 truncate">{{ post.title }}</h3>
              </div>
              <p class="text-sm text-ink-500 line-clamp-2">{{ post.content.substring(0, 100) }}...</p>
              <div class="flex items-center gap-3 text-xs text-ink-400 mt-1">
                <span>{{ post.authorName || '匿名' }}</span>
                <span>发布: {{ formatTime(post.createTime) }}</span>
                <span v-if="post.updateTime && post.updateTime !== post.createTime" class="text-accent-400">更新: {{ formatTime(post.updateTime) }}</span>
              </div>
            </div>
            <div class="flex items-center gap-3 text-xs text-ink-400 shrink-0">
              <span>💬 {{ post.commentCount }}</span>
              <span>❤️ {{ post.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 py-4 border-t border-ink-100">
        <button @click="changePage(page - 1)" :disabled="page === 0"
          class="px-3 py-1 text-sm border border-ink-300 rounded hover:bg-ink-50 disabled:opacity-30">
          上一页
        </button>
        <span class="text-sm text-ink-500">{{ page + 1 }} / {{ totalPages }}</span>
        <button @click="changePage(page + 1)" :disabled="page >= totalPages - 1"
          class="px-3 py-1 text-sm border border-ink-300 rounded hover:bg-ink-50 disabled:opacity-30">
          下一页
        </button>
      </div>
    </div>
  </div>
</template>
