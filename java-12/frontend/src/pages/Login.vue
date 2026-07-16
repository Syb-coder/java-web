<script setup lang="ts">
// 登录页：支持普通用户和管理员登录
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, adminLogin } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  account: '',
  password: '',
})
const isAdminLogin = ref(false)
const loading = ref(false)
const errorMsg = ref('')

// 提交登录
async function handleSubmit() {
  if (!form.account || !form.password) {
    errorMsg.value = '请输入账号和密码'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const api = isAdminLogin.value ? adminLogin : login
    const resp = await api({ account: form.account, password: form.password })
    userStore.login(resp.data)
    // 跳转到重定向地址或首页
    const redirect = (route.query.redirect as string) || '/'
    router.push(isAdminLogin.value ? '/admin' : redirect)
  } catch (e: any) {
    errorMsg.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-ink-50 px-4">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-primary-500">📖 网文论坛</h1>
        <p class="text-ink-400 mt-2">{{ isAdminLogin ? '管理员登录' : '欢迎回来' }}</p>
      </div>

      <!-- 登录表单 -->
      <div class="bg-white rounded-xl shadow-sm border border-ink-200 p-8">
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <!-- 账号 -->
          <div>
            <label class="block text-sm text-ink-600 mb-1">账号</label>
            <input v-model="form.account" type="text" placeholder="请输入账号"
              class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500" />
          </div>
          <!-- 密码 -->
          <div>
            <label class="block text-sm text-ink-600 mb-1">密码</label>
            <input v-model="form.password" type="password" placeholder="请输入密码"
              class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500" />
          </div>
          <!-- 错误提示 -->
          <p v-if="errorMsg" class="text-red-500 text-sm">{{ errorMsg }}</p>
          <!-- 提交按钮 -->
          <button type="submit" :disabled="loading"
            class="w-full bg-primary-500 text-white py-2.5 rounded-lg hover:bg-primary-600 transition disabled:opacity-50">
            {{ loading ? '登录中...' : (isAdminLogin ? '管理员登录' : '登录') }}
          </button>
        </form>

        <!-- 切换登录模式 -->
        <div class="mt-4 text-center">
          <button @click="isAdminLogin = !isAdminLogin" class="text-sm text-accent-500 hover:text-accent-600">
            {{ isAdminLogin ? '← 普通用户登录' : '管理员登录 →' }}
          </button>
        </div>

        <!-- 注册链接（仅普通用户模式显示） -->
        <div v-if="!isAdminLogin" class="mt-4 text-center text-sm text-ink-400">
          还没有账号？
          <router-link to="/register" class="text-primary-500 hover:text-primary-600">立即注册</router-link>
        </div>
      </div>

      <!-- 返回首页 -->
      <div class="text-center mt-4">
        <router-link to="/" class="text-sm text-ink-400 hover:text-primary-500">← 返回首页</router-link>
      </div>
    </div>
  </div>
</template>
