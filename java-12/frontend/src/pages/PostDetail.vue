<script setup lang="ts">
// 帖子详情页：展示帖子内容、评论列表、点赞收藏、评论操作
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  getPostById, deletePost, toggleLike, toggleCollect,
  getComments, createComment, deleteComment,
} from '@/api'
import type { PostResponse, CommentResponse } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref<PostResponse | null>(null)
const comments = ref<CommentResponse[]>([])
const loading = ref(true)
const commentText = ref('')
const replyTo = ref<number | null>(null)
const submitting = ref(false)

// 加载帖子详情
async function fetchPost() {
  try {
    const resp = await getPostById(Number(route.params.id))
    post.value = resp.data
  } catch (e: any) {
    alert(e.message || '帖子加载失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 加载评论
async function fetchComments() {
  try {
    const resp = await getComments(Number(route.params.id))
    comments.value = resp.data
  } catch { /* 静默处理 */ }
}

// 点赞/取消点赞
async function handleLike() {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=' + route.fullPath)
    return
  }
  try {
    const resp = await toggleLike(post.value!.id)
    post.value!.liked = resp.data.liked
    post.value!.likeCount += resp.data.liked ? 1 : -1
  } catch (e: any) {
    alert(e.message)
  }
}

// 收藏/取消收藏
async function handleCollect() {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=' + route.fullPath)
    return
  }
  try {
    const resp = await toggleCollect(post.value!.id)
    post.value!.collected = resp.data.collected
    post.value!.collectCount += resp.data.collected ? 1 : -1
  } catch (e: any) {
    alert(e.message)
  }
}

// 发表评论
async function handleComment() {
  if (!userStore.isLoggedIn) {
    router.push('/login?redirect=' + route.fullPath)
    return
  }
  if (!commentText.value.trim()) return
  submitting.value = true
  try {
    await createComment({
      postId: Number(route.params.id),
      content: commentText.value,
      parentId: replyTo.value,
    })
    commentText.value = ''
    replyTo.value = null
    await fetchComments()
    if (post.value) post.value.commentCount++
  } catch (e: any) {
    alert(e.message)
  } finally {
    submitting.value = false
  }
}

// 删除帖子
async function handleDeletePost() {
  if (!confirm('确定删除此帖子吗？')) return
  try {
    await deletePost(post.value!.id)
    router.push('/')
  } catch (e: any) {
    alert(e.message)
  }
}

// 删除评论
async function handleDeleteComment(id: number) {
  if (!confirm('确定删除此评论吗？')) return
  try {
    await deleteComment(id)
    await fetchComments()
    if (post.value) post.value.commentCount--
  } catch (e: any) {
    alert(e.message)
  }
}

// 格式化时间
function formatTime(time: string) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

onMounted(() => {
  fetchPost()
  fetchComments()
})
</script>

<template>
  <div class="space-y-4">
    <!-- 加载中 -->
    <div v-if="loading" class="py-20 text-center text-ink-400">加载中...</div>

    <template v-else-if="post">
      <!-- 帖子内容 -->
      <div class="bg-white rounded-xl border border-ink-200 p-6">
        <!-- 标题 -->
        <div class="flex items-start justify-between gap-4 mb-4">
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-2">
              <span v-if="post.isTop" class="bg-primary-500 text-white text-xs px-1.5 py-0.5 rounded">置顶</span>
              <span v-if="post.plateName" class="text-xs text-primary-500 bg-primary-50 px-1.5 py-0.5 rounded">
                {{ post.plateName }}
              </span>
            </div>
            <h1 class="text-2xl font-bold text-ink-800">{{ post.title }}</h1>
          </div>
          <!-- 作者操作按钮 -->
          <div v-if="userStore.userInfo?.id === post.userId" class="flex gap-2 shrink-0">
            <button @click="router.push(`/post/edit/${post.id}`)"
              class="text-sm text-accent-500 hover:text-accent-600 px-3 py-1 border border-accent-300 rounded">
              编辑
            </button>
            <button @click="handleDeletePost"
              class="text-sm text-red-500 hover:text-red-600 px-3 py-1 border border-red-300 rounded">
              删除
            </button>
          </div>
        </div>

        <!-- 作者信息 -->
        <div class="flex items-center gap-3 pb-4 border-b border-ink-100">
          <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-600">
            {{ post.authorName?.charAt(0) || '?' }}
          </div>
          <div>
            <div class="text-sm font-medium text-ink-700">{{ post.authorName || '匿名' }}</div>
            <div class="text-xs text-ink-400">
              <span>发布: {{ formatTime(post.createTime) }}</span>
              <span v-if="post.updateTime && post.updateTime !== post.createTime" class="ml-2 text-accent-400">更新: {{ formatTime(post.updateTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 正文 -->
        <div class="post-content py-4 whitespace-pre-wrap text-ink-700">{{ post.content }}</div>

        <!-- 统计与操作 -->
        <div class="flex items-center justify-between pt-4 border-t border-ink-100">
          <div class="flex items-center gap-4 text-sm text-ink-400">
            <span>👁️ {{ post.viewCount }} 浏览</span>
            <span>💬 {{ post.commentCount }} 评论</span>
          </div>
          <div class="flex items-center gap-3">
            <button @click="handleLike"
              :class="['flex items-center gap-1 px-4 py-1.5 rounded-lg text-sm transition',
                post.liked ? 'bg-red-50 text-red-500' : 'bg-ink-50 text-ink-500 hover:bg-ink-100']">
              {{ post.liked ? '❤️' : '🤍' }} {{ post.likeCount }}
            </button>
            <button @click="handleCollect"
              :class="['flex items-center gap-1 px-4 py-1.5 rounded-lg text-sm transition',
                post.collected ? 'bg-primary-50 text-primary-500' : 'bg-ink-50 text-ink-500 hover:bg-ink-100']">
              {{ post.collected ? '⭐' : '☆' }} {{ post.collectCount }}
            </button>
          </div>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="bg-white rounded-xl border border-ink-200 p-6">
        <h2 class="font-medium text-ink-700 mb-4">评论 ({{ comments.length }})</h2>

        <!-- 评论输入框 -->
        <div v-if="userStore.isLoggedIn" class="mb-6">
          <div v-if="replyTo" class="text-xs text-ink-400 mb-1">
            回复评论 #{{ replyTo }}
            <button @click="replyTo = null" class="text-red-400 ml-2">取消回复</button>
          </div>
          <textarea v-model="commentText" rows="3" placeholder="写下你的评论..."
            class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 resize-none"></textarea>
          <div class="flex justify-end mt-2">
            <button @click="handleComment" :disabled="submitting || !commentText.trim()"
              class="bg-primary-500 text-white px-4 py-1.5 rounded-lg text-sm hover:bg-primary-600 disabled:opacity-50">
              {{ submitting ? '发送中...' : '发表评论' }}
            </button>
          </div>
        </div>
        <div v-else class="mb-6 text-center text-sm text-ink-400 py-4 bg-ink-50 rounded-lg">
          <router-link :to="`/login?redirect=${route.fullPath}`" class="text-primary-500">登录</router-link> 后参与评论
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length === 0" class="text-center text-ink-400 py-8">暂无评论，快来抢沙发吧</div>
        <div v-else class="space-y-4">
          <div v-for="comment in comments" :key="comment.id"
            class="flex gap-3 pb-4 border-b border-ink-100 last:border-0">
            <!-- 头像 -->
            <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 shrink-0 text-sm">
              {{ comment.authorName?.charAt(0) || '?' }}
            </div>
            <!-- 内容 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between mb-1">
                <span class="text-sm font-medium text-ink-700">{{ comment.authorName || '匿名' }}</span>
                <span class="text-xs text-ink-400">
                  <span>{{ formatTime(comment.createTime) }}</span>
                  <span v-if="comment.updateTime && comment.updateTime !== comment.createTime" class="ml-1 text-accent-400">(编辑于 {{ formatTime(comment.updateTime) }})</span>
                </span>
              </div>
              <p class="text-sm text-ink-600">{{ comment.content }}</p>
              <!-- 操作 -->
              <div class="flex items-center gap-3 mt-1">
                <button v-if="userStore.isLoggedIn" @click="replyTo = comment.id"
                  class="text-xs text-ink-400 hover:text-primary-500">回复</button>
                <button v-if="userStore.userInfo?.id === comment.userId"
                  @click="handleDeleteComment(comment.id)"
                  class="text-xs text-red-400 hover:text-red-500">删除</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
