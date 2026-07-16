<script setup lang="ts">
// 首页：置顶帖子 + 板块导航 + 最新/热门帖子列表
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getLatestPosts, getHotPosts, getTopPosts, getPlates } from '@/api'
import type { PostResponse, Plate } from '@/types'

const router = useRouter()

const plates = ref<Plate[]>([])
const topPosts = ref<PostResponse[]>([])
const posts = ref<PostResponse[]>([])
const page = ref(0)
const totalPages = ref(0)
const loading = ref(false)
const activeTab = ref<'latest' | 'hot'>('latest')

// 加载板块列表
async function fetchPlates() {
  try {
    const resp = await getPlates()
    plates.value = resp.data
  } catch { /* 静默处理 */ }
}

// 加载置顶帖子
async function fetchTopPosts() {
  try {
    const resp = await getTopPosts()
    topPosts.value = resp.data
  } catch { /* 静默处理 */ }
}

// 加载帖子列表
async function fetchPosts() {
  loading.value = true
  try {
    const api = activeTab.value === 'latest' ? getLatestPosts : getHotPosts
    const resp = await api(page.value, 15)
    posts.value = resp.data.content
    totalPages.value = resp.data.totalPages
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

// 切换标签
function switchTab(tab: 'latest' | 'hot') {
  if (activeTab.value === tab) return
  activeTab.value = tab
  page.value = 0
  fetchPosts()
}

// 翻页
function changePage(newPage: number) {
  if (newPage < 0 || newPage >= totalPages.value) return
  page.value = newPage
  fetchPosts()
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

onMounted(() => {
  fetchPlates()
  fetchTopPosts()
  fetchPosts()
})
</script>

<template>
  <div class="space-y-6">
    <!-- 板块导航 -->
    <div class="bg-white rounded-xl border border-ink-200 p-4">
      <h2 class="text-sm font-medium text-ink-500 mb-3">板块导航</h2>
      <div class="flex flex-wrap gap-2">
        <router-link v-for="plate in plates" :key="plate.id"
          :to="`/search?plateId=${plate.id}`"
          class="px-3 py-1.5 bg-primary-50 text-primary-600 rounded-lg text-sm hover:bg-primary-100 transition">
          {{ plate.name }}
          <span class="text-ink-400 text-xs ml-1">{{ plate.postCount }}</span>
        </router-link>
      </div>
    </div>

    <!-- 置顶帖子 -->
    <div v-if="topPosts.length > 0" class="space-y-2">
      <h2 class="text-sm font-medium text-ink-500">📌 置顶帖子</h2>
      <div v-for="post in topPosts" :key="post.id"
        class="bg-primary-50 border border-primary-200 rounded-lg p-3 cursor-pointer hover:bg-primary-100 transition"
        @click="router.push(`/post/${post.id}`)">
        <div class="flex items-center gap-2">
          <span class="bg-primary-500 text-white text-xs px-1.5 py-0.5 rounded">置顶</span>
          <span class="font-medium text-ink-800">{{ post.title }}</span>
        </div>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <!-- 标签切换 -->
      <div class="flex border-b border-ink-200">
        <button @click="switchTab('latest')"
          :class="['flex-1 py-3 text-sm font-medium transition',
            activeTab === 'latest' ? 'text-primary-500 border-b-2 border-primary-500' : 'text-ink-400 hover:text-ink-600']">
          最新
        </button>
        <button @click="switchTab('hot')"
          :class="['flex-1 py-3 text-sm font-medium transition',
            activeTab === 'hot' ? 'text-primary-500 border-b-2 border-primary-500' : 'text-ink-400 hover:text-ink-600']">
          热门
        </button>
      </div>

      <!-- 帖子列表 -->
      <div v-if="loading" class="py-12 text-center text-ink-400">加载中...</div>
      <div v-else-if="posts.length === 0" class="py-12 text-center text-ink-400">暂无帖子</div>
      <div v-else class="divide-y divide-ink-100">
        <div v-for="post in posts" :key="post.id"
          class="p-4 cursor-pointer hover:bg-ink-50 transition"
          @click="router.push(`/post/${post.id}`)">
          <div class="flex items-start justify-between gap-4">
            <div class="flex-1 min-w-0">
              <!-- 标题行 -->
              <div class="flex items-center gap-2 mb-1">
                <span v-if="post.plateName" class="text-xs text-primary-500 bg-primary-50 px-1.5 py-0.5 rounded">
                  {{ post.plateName }}
                </span>
                <h3 class="font-medium text-ink-800 truncate">{{ post.title }}</h3>
              </div>
              <!-- 作者与时间 -->
              <div class="flex items-center gap-3 text-xs text-ink-400">
                <span>{{ post.authorName || '匿名' }}</span>
                <span>发布: {{ formatTime(post.createTime) }}</span>
                <span v-if="post.updateTime && post.updateTime !== post.createTime" class="text-accent-400">更新: {{ formatTime(post.updateTime) }}</span>
              </div>
            </div>
            <!-- 统计数据 -->
            <div class="flex items-center gap-3 text-xs text-ink-400 shrink-0">
              <span>💬 {{ post.commentCount }}</span>
              <span>❤️ {{ post.likeCount }}</span>
              <span>👁️ {{ post.viewCount }}</span>
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
