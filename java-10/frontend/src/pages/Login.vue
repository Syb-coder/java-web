<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { School, ShieldCheck, GraduationCap, UserCog, RefreshCw } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import type { Role } from '@/stores/user'
import request from '@/api/request'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseButton from '@/components/ui/BaseButton.vue'

// PRD 3.2 登录系统：用户名、密码、角色选择、验证码、记住密码

const router = useRouter()
const userStore = useUserStore()

// 表单数据
const form = reactive({
  username: '',
  password: '',
  role: 'admin' as Role,
  captcha: '',
  remember: false,
})

// 错误提示：字段级错误
const errors = reactive({
  username: '',
  password: '',
  captcha: '',
})

// 登录提交中状态
const loading = ref(false)

// 验证码：前端生成 4 位随机字符，点击刷新
const captchaCode = ref('')

// 角色选项配置：对应 PRD 2.1 三种角色
const roleOptions: { label: string; value: Role; icon: typeof School }[] = [
  { label: '管理员', value: 'admin', icon: ShieldCheck },
  { label: '教师', value: 'teacher', icon: GraduationCap },
  { label: '学生', value: 'student', icon: UserCog },
]

// 生成 4 位随机验证码（字母+数字，不区分大小写校验）
function generateCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789'
  let code = ''
  for (let i = 0; i < 4; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  captchaCode.value = code
}

// 表单校验：返回是否通过
function validate(): boolean {
  let valid = true
  errors.username = ''
  errors.password = ''
  errors.captcha = ''

  // PRD 规则约束：用户名、密码必填
  if (!form.username.trim()) {
    errors.username = '用户名不能为空'
    valid = false
  }
  if (!form.password) {
    errors.password = '密码不能为空'
    valid = false
  }
  // 验证码必填且不区分大小写比对
  if (!form.captcha.trim()) {
    errors.captcha = '验证码不能为空'
    valid = false
  } else if (form.captcha.toLowerCase() !== captchaCode.value.toLowerCase()) {
    errors.captcha = '验证码错误'
    valid = false
  }
  return valid
}

// 登录提交：调用后端真实登录接口验证账号密码
async function handleLogin() {
  if (!validate()) {
    // 验证码错误时刷新验证码（PRD 3.2 边界与异常）
    if (errors.captcha) generateCaptcha()
    return
  }

  loading.value = true
  try {
    // 调用后端 /api/auth/login 接口，验证用户名与密码
    const res = await request.post('/auth/login', {
      username: form.username,
      password: form.password,
    })
    const data = res.data
    // 以后端返回的角色为准，前端角色选择仅作 UI 参考
    userStore.login({
      username: data.username,
      realName: data.realName,
      role: data.role as Role,
    })

    // 记住密码：勾选后用 localStorage 存储 7 天（模拟 Cookie 存储）
    if (form.remember) {
      const expires = Date.now() + 7 * 24 * 60 * 60 * 1000
      localStorage.setItem(
        'rememberLogin',
        JSON.stringify({
          username: form.username,
          password: btoa(form.password), // Base64 编码，避免明文存储
          role: data.role,
          expires,
        }),
      )
    } else {
      localStorage.removeItem('rememberLogin')
    }

    // 登录成功后跳转首页
    router.push({ name: 'dashboard' })
  } catch (err: unknown) {
    // 401 表示用户名或密码错误，其他错误提示后端服务问题
    const status = (err as { response?: { status?: number } })?.response?.status
    if (status === 401) {
      errors.password = '用户名或密码错误'
    } else {
      errors.password = '登录失败，请确认后端服务已启动'
    }
    generateCaptcha()
  } finally {
    loading.value = false
  }
}

// 页面加载时生成验证码，并尝试恢复记住的登录信息
onMounted(() => {
  generateCaptcha()
  const saved = localStorage.getItem('rememberLogin')
  if (saved) {
    try {
      const data = JSON.parse(saved)
      // 检查是否在 7 天有效期内
      if (data.expires > Date.now()) {
        form.username = data.username
        form.password = atob(data.password)
        form.role = data.role
        form.remember = true
      } else {
        localStorage.removeItem('rememberLogin')
      }
    } catch {
      localStorage.removeItem('rememberLogin')
    }
  }
})
</script>

<template>
  <div class="relative flex min-h-screen items-center justify-center overflow-hidden p-4">
    <!-- 背景图层：使用项目提供的图片，cover 模式填满视口 -->
    <div class="absolute inset-0 bg-cover bg-center" style="background-image: url('/login-bg.jpg')"></div>
    <!-- 暗色遮罩：降低背景亮度以保证表单与文字可读性 -->
    <div class="absolute inset-0 bg-black/50"></div>
    <!-- 登录卡片：relative 提升层级至遮罩之上 -->
    <div class="relative w-full max-w-md">
      <!-- Logo 与标题 -->
      <div class="mb-8 text-center">
        <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-white shadow-lg">
          <School class="h-9 w-9 text-primary-600" />
        </div>
        <h1 class="text-2xl font-bold text-white">学生信息管理系统</h1>
        <p class="mt-2 text-sm text-primary-200">Student Information Management System</p>
      </div>

      <!-- 登录表单 -->
      <div class="rounded-2xl bg-white p-8 shadow-2xl">
        <h2 class="mb-6 text-lg font-semibold text-ink-800">欢迎登录</h2>

        <!-- 角色选择 -->
        <div class="mb-5">
          <label class="mb-2 block text-sm font-medium text-ink-700">选择身份</label>
          <div class="grid grid-cols-3 gap-3">
            <button
              v-for="opt in roleOptions"
              :key="opt.value"
              type="button"
              :class="[
                'flex flex-col items-center gap-1.5 rounded-lg border-2 py-3 transition-all',
                form.role === opt.value
                  ? 'border-primary-500 bg-primary-50 text-primary-700'
                  : 'border-ink-200 text-ink-500 hover:border-primary-300 hover:bg-ink-50',
              ]"
              @click="form.role = opt.value"
            >
              <component :is="opt.icon" class="h-5 w-5" />
              <span class="text-xs font-medium">{{ opt.label }}</span>
            </button>
          </div>
        </div>

        <!-- 用户名 -->
        <div class="mb-4">
          <label class="mb-1.5 block text-sm font-medium text-ink-700">用户名</label>
          <BaseInput
            v-model="form.username"
            icon="User"
            placeholder="请输入学号、职工号或管理员用户名"
            :error="errors.username"
          />
        </div>

        <!-- 密码 -->
        <div class="mb-4">
          <label class="mb-1.5 block text-sm font-medium text-ink-700">密码</label>
          <BaseInput
            v-model="form.password"
            icon="Lock"
            type="password"
            placeholder="请输入密码"
            :error="errors.password"
          />
        </div>

        <!-- 验证码 -->
        <div class="mb-5">
          <label class="mb-1.5 block text-sm font-medium text-ink-700">验证码</label>
          <div class="flex gap-3">
            <BaseInput
              v-model="form.captcha"
              placeholder="请输入验证码"
              :error="errors.captcha"
              class="flex-1"
            />
            <!-- 验证码图片：前端生成，点击刷新 -->
            <button
              type="button"
              class="flex h-[42px] w-28 shrink-0 items-center justify-center rounded-md border border-ink-200 bg-ink-50 font-mono text-lg font-bold tracking-widest text-ink-700 transition-colors hover:bg-ink-100"
              title="点击刷新验证码"
              @click="generateCaptcha"
            >
              {{ captchaCode }}
              <RefreshCw class="ml-1.5 h-3.5 w-3.5 text-ink-400" />
            </button>
          </div>
        </div>

        <!-- 记住密码 -->
        <div class="mb-6 flex items-center">
          <label class="flex cursor-pointer items-center gap-2 text-sm text-ink-600">
            <input
              v-model="form.remember"
              type="checkbox"
              class="h-4 w-4 rounded border-ink-300 text-primary-600 focus:ring-primary-500"
            />
            记住密码（7天内免输入）
          </label>
        </div>

        <!-- 登录按钮 -->
        <BaseButton
          type="submit"
          size="lg"
          class="w-full"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登 录' }}
        </BaseButton>

        <!-- 提示信息 -->
        <p class="mt-4 text-center text-xs text-ink-400">
          演示账号：任意非空用户名+密码即可登录
        </p>
      </div>

      <!-- 底部版权 -->
      <p class="mt-6 text-center text-xs text-primary-200">
        © 2026 学生信息管理系统 · All Rights Reserved
      </p>
    </div>
  </div>
</template>
