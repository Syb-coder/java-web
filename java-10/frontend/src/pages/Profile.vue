<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { Role } from '@/stores/user'
import { api } from '@/api'
import type { OperationLog } from '@/types'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseTable from '@/components/ui/BaseTable.vue'

// 个人中心：个人信息、密码修改、操作记录三个卡片

const router = useRouter()
const userStore = useUserStore()

// 角色中文映射：store 存英文枚举，展示需转为中文
const roleMap: Record<Role, string> = {
  admin: '管理员',
  teacher: '教师',
  student: '学生',
}

/* ---------------- 个人信息卡片 ---------------- */

// 联系电话：UserInfo 无此字段，使用本地可编辑状态管理
const phone = ref('')
const profileSaving = ref(false)
const profileMsg = ref('')

// 保存个人信息：当前无独立接口，模拟保存并提示
async function handleSaveProfile() {
  profileSaving.value = true
  await new Promise((r) => setTimeout(r, 300))
  profileSaving.value = false
  profileMsg.value = '个人信息保存成功'
  // 2 秒后清空提示，避免持续占用视觉焦点
  setTimeout(() => { profileMsg.value = '' }, 2000)
}

/* ---------------- 密码修改卡片 ---------------- */

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})
const passwordErrors = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})
const passwordSaving = ref(false)
// 修改成功标记：为 true 时展示提示并触发延时退出
const passwordSuccess = ref(false)

// 密码校验：PRD 3.13 规则——原密码必填、新密码>=6位、两次密码一致
function validatePassword(): boolean {
  let valid = true
  passwordErrors.oldPassword = ''
  passwordErrors.newPassword = ''
  passwordErrors.confirmPassword = ''

  if (!passwordForm.oldPassword) {
    passwordErrors.oldPassword = '请输入原密码'
    valid = false
  }
  if (passwordForm.newPassword.length < 6) {
    passwordErrors.newPassword = '密码长度不能少于 6 位'
    valid = false
  }
  if (passwordForm.confirmPassword !== passwordForm.newPassword) {
    passwordErrors.confirmPassword = '两次输入的密码不一致'
    valid = false
  }
  return valid
}

async function handleChangePassword() {
  if (!validatePassword()) return
  passwordSaving.value = true
  // 模拟接口请求延迟
  await new Promise((r) => setTimeout(r, 500))
  passwordSaving.value = false
  passwordSuccess.value = true
  // PRD 3.13：修改成功后提示重新登录，1.5 秒后退出并跳转登录页
  setTimeout(() => {
    userStore.logout()
    router.push({ name: 'login' })
  }, 1500)
}

/* ---------------- 操作记录卡片 ---------------- */

const logs = ref<OperationLog[]>([])
const logLoading = ref(true)

// BaseTable 列配置：序号使用插槽渲染行号
const logColumns: { key: string; title: string; width?: string; align?: 'left' | 'center' | 'right' }[] = [
  { key: 'index', title: '序号', width: '80px', align: 'center' },
  { key: 'action', title: '操作' },
  { key: 'detail', title: '详情' },
  { key: 'time', title: '时间' },
]

onMounted(() => {
  api.log.list()
    .then((data) => { logs.value = data })
    .catch(() => { logs.value = [] })
    .finally(() => { logLoading.value = false })
})
</script>

<template>
  <div class="mx-auto max-w-3xl space-y-6">
    <!-- 个人信息卡片 -->
    <div class="rounded-xl bg-white p-6 shadow-card">
      <h2 class="mb-5 text-base font-semibold text-ink-800">个人信息</h2>
      <div class="grid grid-cols-2 gap-x-6 gap-y-4">
        <div>
          <label class="mb-1 block text-sm text-ink-500">用户名</label>
          <div class="rounded-md border border-ink-100 bg-ink-50 px-3 py-2 text-sm text-ink-700">
            {{ userStore.userInfo?.username ?? '-' }}
          </div>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-500">真实姓名</label>
          <div class="rounded-md border border-ink-100 bg-ink-50 px-3 py-2 text-sm text-ink-700">
            {{ userStore.userInfo?.realName ?? '-' }}
          </div>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-500">角色</label>
          <div class="rounded-md border border-ink-100 bg-ink-50 px-3 py-2 text-sm text-ink-700">
            {{ userStore.userInfo ? roleMap[userStore.userInfo.role] : '-' }}
          </div>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-500">联系电话</label>
          <BaseInput v-model="phone" icon="Phone" placeholder="请输入联系电话" />
        </div>
      </div>
      <div class="mt-5 flex items-center gap-3">
        <BaseButton :loading="profileSaving" @click="handleSaveProfile">保存</BaseButton>
        <span v-if="profileMsg" class="text-sm text-accent-600">{{ profileMsg }}</span>
      </div>
    </div>

    <!-- 密码修改卡片 -->
    <div class="relative rounded-xl bg-white p-6 shadow-card">
      <h2 class="mb-5 text-base font-semibold text-ink-800">修改密码</h2>
      <div class="space-y-4">
        <div>
          <label class="mb-1 block text-sm text-ink-500">原密码</label>
          <BaseInput
            v-model="passwordForm.oldPassword"
            icon="Lock"
            type="password"
            placeholder="请输入原密码"
            :error="passwordErrors.oldPassword"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-500">新密码</label>
          <BaseInput
            v-model="passwordForm.newPassword"
            icon="Lock"
            type="password"
            placeholder="至少 6 位"
            :error="passwordErrors.newPassword"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-500">确认新密码</label>
          <BaseInput
            v-model="passwordForm.confirmPassword"
            icon="Lock"
            type="password"
            placeholder="请再次输入新密码"
            :error="passwordErrors.confirmPassword"
          />
        </div>
      </div>
      <div class="mt-5">
        <BaseButton :loading="passwordSaving" @click="handleChangePassword">保存</BaseButton>
      </div>
      <!-- 修改成功提示遮罩 -->
      <div
        v-if="passwordSuccess"
        class="absolute inset-0 flex items-center justify-center rounded-xl bg-white/80 backdrop-blur-sm"
      >
        <div class="rounded-lg bg-white px-6 py-4 text-center shadow-lg">
          <p class="text-sm font-medium text-ink-800">密码修改成功，请重新登录</p>
          <p class="mt-1 text-xs text-ink-400">1.5 秒后自动跳转登录页…</p>
        </div>
      </div>
    </div>

    <!-- 操作记录卡片 -->
    <div class="rounded-xl bg-white p-6 shadow-card">
      <h2 class="mb-5 text-base font-semibold text-ink-800">操作记录</h2>
      <BaseTable :columns="logColumns" :data="logs" :loading="logLoading">
        <template #col-index="{ index }">{{ index + 1 }}</template>
      </BaseTable>
    </div>
  </div>
</template>
