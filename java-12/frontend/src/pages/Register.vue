<script setup lang="ts">
// 注册页
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { register } from '@/api'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  account: '',
  nickname: '',
  password: '',
  confirmPassword: '',
})
const loading = ref(false)
const errorMsg = ref('')

// 提交注册
async function handleSubmit() {
  // 前端校验
  if (!form.account || !form.nickname || !form.password) {
    errorMsg.value = '请填写所有必填项'
    return
  }
  if (form.account.length < 4 || form.account.length > 20) {
    errorMsg.value = '账号长度需 4-20 个字符'
    return
  }
  if (form.nickname.length < 2 || form.nickname.length > 15) {
    errorMsg.value = '昵称长度需 2-15 个字符'
    return
  }
  if (form.password.length < 6) {
    errorMsg.value = '密码至少 6 个字符'
    return
  }
  if (form.password !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const resp = await register({
      account: form.account,
      nickname: form.nickname,
      password: form.password,
      confirmPassword: form.confirmPassword,
    })
    userStore.login(resp.data)
    router.push('/')
  } catch (e: any) {
    errorMsg.value = e.message || '注册失败'
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
        <p class="text-ink-400 mt-2">创建你的账号</p>
      </div>

      <!-- 注册表单 -->
      <div class="bg-white rounded-xl shadow-sm border border-ink-200 p-8">
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <!-- 账号 -->
          <div>
            <label class="block text-sm text-ink-600 mb-1">账号</label>
            <input v-model="form.account" type="text" placeholder="4-20位字母数字"
              class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500" />
          </div>
          <!-- 昵称 -->
          <div>
            <label class="block text-sm text-ink-600 mb-1">昵称</label>
            <input v-model="form.nickname" type="text" placeholder="2-15个字符"
              class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500" />
          </div>
          <!-- 密码 -->
          <div>
            <label class="block text-sm text-ink-600 mb-1">密码</label>
            <input v-model="form.password" type="password" placeholder="至少6个字符"
              class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500" />
          </div>
          <!-- 确认密码 -->
          <div>
            <label class="block text-sm text-ink-600 mb-1">确认密码</label>
            <input v-model="form.confirmPassword" type="password" placeholder="再次输入密码"
              class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500" />
          </div>
          <!-- 错误提示 -->
          <p v-if="errorMsg" class="text-red-500 text-sm">{{ errorMsg }}</p>
          <!-- 提交按钮 -->
          <button type="submit" :disabled="loading"
            class="w-full bg-primary-500 text-white py-2.5 rounded-lg hover:bg-primary-600 transition disabled:opacity-50">
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <!-- 登录链接 -->
        <div class="mt-4 text-center text-sm text-ink-400">
          已有账号？
          <router-link to="/login" class="text-primary-500 hover:text-primary-600">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>
