<script setup lang="ts">
// 个人中心：资料编辑、我的发帖、我的收藏、我的评论、站内消息、修改密码
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import {
  getProfile, updateProfile, changePassword,
  getMyPosts, getMyCollects, getMyComments,
  getMyMessages, markMessageRead,
} from '@/api'
import type { UserInfo, PostResponse, CommentResponse, Message } from '@/types'

const userStore = useUserStore()

const activeTab = ref<'posts' | 'collects' | 'comments' | 'messages' | 'settings'>('posts')
const profile = ref<UserInfo | null>(null)
const posts = ref<PostResponse[]>([])
const collects = ref<PostResponse[]>([])
const comments = ref<CommentResponse[]>([])
const messages = ref<Message[]>([])

// 编辑资料表单
const editForm = ref({ nickname: '', avatar: '', signature: '' })
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const saving = ref(false)
const msg = ref('')

// 加载个人资料
async function fetchProfile() {
  try {
    const resp = await getProfile()
    profile.value = resp.data
    editForm.value = {
      nickname: resp.data.nickname,
      avatar: resp.data.avatar || '',
      signature: resp.data.signature || '',
    }
  } catch { /* 静默处理 */ }
}

// 加载我的发帖
async function fetchMyPosts() {
  try {
    const resp = await getMyPosts(0, 20)
    posts.value = resp.data.content
  } catch { /* 静默处理 */ }
}

// 加载我的收藏
async function fetchMyCollects() {
  try {
    const resp = await getMyCollects()
    collects.value = resp.data
  } catch { /* 静默处理 */ }
}

// 加载我的评论
async function fetchMyComments() {
  try {
    const resp = await getMyComments()
    comments.value = resp.data
  } catch { /* 静默处理 */ }
}

// 加载消息
async function fetchMessages() {
  try {
    const resp = await getMyMessages()
    messages.value = resp.data
  } catch { /* 静默处理 */ }
}

// 保存资料
async function handleSaveProfile() {
  saving.value = true
  msg.value = ''
  try {
    const resp = await updateProfile(editForm.value)
    profile.value = resp.data
    if (userStore.userInfo) {
      userStore.updateUserInfo(resp.data)
    }
    msg.value = '保存成功'
  } catch (e: any) {
    msg.value = e.message || '保存失败'
  } finally {
    saving.value = false
  }
}

// 修改密码
async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    msg.value = '请填写完整'
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    msg.value = '新密码至少6个字符'
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    msg.value = '两次密码不一致'
    return
  }
  saving.value = true
  msg.value = ''
  try {
    await changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword,
      confirmPassword: passwordForm.value.confirmPassword,
    })
    msg.value = '密码修改成功，请重新登录'
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    // 修改密码后自动登出
    setTimeout(() => {
      userStore.logout()
      window.location.href = '/login'
    }, 2000)
  } catch (e: any) {
    msg.value = e.message || '修改失败'
  } finally {
    saving.value = false
  }
}

// 标记消息已读
async function handleReadMessage(id: number) {
  try {
    await markMessageRead(id)
    const msg = messages.value.find(m => m.id === id)
    if (msg) msg.isRead = true
  } catch { /* 静默处理 */ }
}

// 格式化时间
function formatTime(time: string) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchProfile()
  fetchMyPosts()
})
</script>

<template>
  <div class="space-y-4">
    <!-- 用户信息卡片 -->
    <div class="bg-white rounded-xl border border-ink-200 p-6">
      <div class="flex items-center gap-4">
        <div class="w-16 h-16 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 text-2xl">
          {{ profile?.nickname?.charAt(0) || '?' }}
        </div>
        <div>
          <h2 class="text-lg font-bold text-ink-800">{{ profile?.nickname || '加载中...' }}</h2>
          <p class="text-sm text-ink-400">账号: {{ profile?.account }}</p>
          <div class="flex items-center gap-3 mt-1 text-xs text-ink-400">
            <span>发帖 {{ profile?.postCount || 0 }}</span>
            <span v-if="profile?.role === 'ADMIN'" class="bg-primary-50 text-primary-600 px-2 py-0.5 rounded">管理员</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <div class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <div class="flex border-b border-ink-200">
        <button v-for="tab in [
          { key: 'posts', label: '我的发帖' },
          { key: 'collects', label: '我的收藏' },
          { key: 'comments', label: '我的评论' },
          { key: 'messages', label: '站内消息' },
          { key: 'settings', label: '设置' },
        ]" :key="tab.key" @click="activeTab = tab.key as any"
          :class="['px-6 py-3 text-sm font-medium transition',
            activeTab === tab.key ? 'text-primary-500 border-b-2 border-primary-500' : 'text-ink-400 hover:text-ink-600']">
          {{ tab.label }}
        </button>
      </div>

      <!-- 我的发帖 -->
      <div v-if="activeTab === 'posts'" class="p-4">
        <div v-if="posts.length === 0" class="text-center text-ink-400 py-8">暂无发帖</div>
        <div v-else class="divide-y divide-ink-100">
          <router-link v-for="post in posts" :key="post.id" :to="`/post/${post.id}`"
            class="block py-3 hover:bg-ink-50 px-2 rounded">
            <div class="flex items-center justify-between">
              <span class="font-medium text-ink-700">{{ post.title }}</span>
              <span class="text-xs text-ink-400">{{ formatTime(post.createTime) }}</span>
            </div>
            <div class="text-xs text-ink-400 mt-1">
              💬 {{ post.commentCount }} ❤️ {{ post.likeCount }} 👁️ {{ post.viewCount }}
              <span v-if="post.updateTime && post.updateTime !== post.createTime" class="ml-2 text-accent-400">更新: {{ formatTime(post.updateTime) }}</span>
            </div>
          </router-link>
        </div>
      </div>

      <!-- 我的收藏 -->
      <div v-else-if="activeTab === 'collects'" class="p-4">
        <button @click="fetchMyCollects" class="mb-2 text-sm text-primary-500">刷新收藏</button>
        <div v-if="collects.length === 0" class="text-center text-ink-400 py-8">暂无收藏</div>
        <div v-else class="divide-y divide-ink-100">
          <router-link v-for="post in collects" :key="post.id" :to="`/post/${post.id}`"
            class="block py-3 hover:bg-ink-50 px-2 rounded">
            <span class="font-medium text-ink-700">{{ post.title }}</span>
            <span class="text-xs text-ink-400 ml-2">{{ post.authorName }}</span>
          </router-link>
        </div>
      </div>

      <!-- 我的评论 -->
      <div v-else-if="activeTab === 'comments'" class="p-4">
        <button @click="fetchMyComments" class="mb-2 text-sm text-primary-500">刷新评论</button>
        <div v-if="comments.length === 0" class="text-center text-ink-400 py-8">暂无评论</div>
        <div v-else class="divide-y divide-ink-100">
          <div v-for="comment in comments" :key="comment.id" class="py-3 px-2">
            <p class="text-sm text-ink-600">{{ comment.content }}</p>
            <div class="text-xs text-ink-400 mt-1">
              <span>{{ formatTime(comment.createTime) }}</span>
              <span v-if="comment.updateTime && comment.updateTime !== comment.createTime" class="ml-2 text-accent-400">更新: {{ formatTime(comment.updateTime) }}</span>
            </div>
            <router-link :to="`/post/${comment.postId}`" class="text-xs text-primary-500 mt-1 inline-block">
              查看原帖 →
            </router-link>
          </div>
        </div>
      </div>

      <!-- 站内消息 -->
      <div v-else-if="activeTab === 'messages'" class="p-4">
        <button @click="fetchMessages" class="mb-2 text-sm text-primary-500">刷新消息</button>
        <div v-if="messages.length === 0" class="text-center text-ink-400 py-8">暂无消息</div>
        <div v-else class="divide-y divide-ink-100">
          <div v-for="msg in messages" :key="msg.id"
            :class="['py-3 px-2 flex items-start justify-between', !msg.isRead && 'bg-primary-50']">
            <div class="flex-1">
              <p class="text-sm text-ink-700">{{ msg.content }}</p>
              <span class="text-xs text-ink-400">{{ formatTime(msg.createTime) }}</span>
              <span v-if="msg.updateTime && msg.updateTime !== msg.createTime" class="text-xs text-accent-400 ml-2">更新: {{ formatTime(msg.updateTime) }}</span>
            </div>
            <button v-if="!msg.isRead" @click="handleReadMessage(msg.id)"
              class="text-xs text-primary-500 hover:text-primary-600">标记已读</button>
          </div>
        </div>
      </div>

      <!-- 设置 -->
      <div v-else-if="activeTab === 'settings'" class="p-6 max-w-lg">
        <!-- 修改资料 -->
        <h3 class="font-medium text-ink-700 mb-4">修改资料</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-ink-600 mb-1">昵称（30天内可修改一次）</label>
            <input v-model="editForm.nickname" type="text" maxlength="15"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">头像 URL</label>
            <input v-model="editForm.avatar" type="text"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">个性签名</label>
            <textarea v-model="editForm.signature" rows="2" maxlength="200"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 resize-none"></textarea>
          </div>
          <button @click="handleSaveProfile" :disabled="saving"
            class="bg-primary-500 text-white px-6 py-2 rounded-lg text-sm hover:bg-primary-600 disabled:opacity-50">
            {{ saving ? '保存中...' : '保存资料' }}
          </button>
        </div>

        <!-- 修改密码 -->
        <h3 class="font-medium text-ink-700 mb-4 mt-8 pt-6 border-t border-ink-100">修改密码</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-ink-600 mb-1">原密码</label>
            <input v-model="passwordForm.oldPassword" type="password"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">新密码（至少6位）</label>
            <input v-model="passwordForm.newPassword" type="password"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">确认新密码</label>
            <input v-model="passwordForm.confirmPassword" type="password"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <button @click="handleChangePassword" :disabled="saving"
            class="bg-ink-700 text-white px-6 py-2 rounded-lg text-sm hover:bg-ink-800 disabled:opacity-50">
            {{ saving ? '提交中...' : '修改密码' }}
          </button>
        </div>

        <!-- 提示消息 -->
        <p v-if="msg" class="mt-4 text-sm" :class="msg.includes('成功') ? 'text-green-500' : 'text-red-500'">{{ msg }}</p>
      </div>
    </div>
  </div>
</template>
