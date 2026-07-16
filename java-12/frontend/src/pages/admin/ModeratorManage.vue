<script setup lang="ts">
// 版主管理：为板块分配/回收版主权限
import { ref, reactive, onMounted, watch } from 'vue'
import { getPlates, adminGetUsers, adminAssignModerator, adminRemoveModerator, adminGetPlateModerators } from '@/api'
import type { Plate, UserInfo } from '@/types'

const plates = ref<Plate[]>([])
const users = ref<UserInfo[]>([])
const moderators = ref<any[]>([])
const selectedPlateId = ref<number | undefined>(undefined)
const loading = ref(false)
const showAssign = ref(false)

const assignForm = reactive({
  userId: undefined as number | undefined,
  plateId: undefined as number | undefined,
})

// 加载板块列表
async function fetchPlates() {
  try {
    const resp = await getPlates()
    plates.value = resp.data
    if (plates.value.length > 0 && !selectedPlateId.value) {
      selectedPlateId.value = plates.value[0].id
    }
  } catch { /* 静默处理 */ }
}

// 加载用户列表
async function fetchUsers() {
  try {
    const resp = await adminGetUsers(0, 100)
    users.value = resp.data.content.filter((u: UserInfo) => u.role !== 'ADMIN')
  } catch { /* 静默处理 */ }
}

// 加载指定板块的版主列表
async function fetchModerators() {
  if (!selectedPlateId.value) return
  loading.value = true
  try {
    const resp = await adminGetPlateModerators(selectedPlateId.value)
    moderators.value = resp.data
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

// 打开分配弹窗
function openAssign() {
  assignForm.userId = undefined
  assignForm.plateId = selectedPlateId.value
  showAssign.value = true
}

// 提交分配
async function handleAssign() {
  if (!assignForm.userId || !assignForm.plateId) {
    alert('请选择用户和板块')
    return
  }
  try {
    await adminAssignModerator({ userId: assignForm.userId, plateId: assignForm.plateId })
    showAssign.value = false
    await fetchModerators()
  } catch (e: any) {
    alert(e.message || '分配失败')
  }
}

// 回收版主权限
async function handleRemove(userId: number) {
  if (!selectedPlateId.value) return
  if (!confirm('确定回收该用户的版主权限吗？')) return
  try {
    await adminRemoveModerator(userId, selectedPlateId.value)
    await fetchModerators()
  } catch (e: any) {
    alert(e.message || '回收失败')
  }
}

// 监听板块切换
watch(selectedPlateId, () => {
  fetchModerators()
})

onMounted(async () => {
  await fetchPlates()
  await fetchUsers()
  await fetchModerators()
})
</script>

<template>
  <div>
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-medium text-ink-800">版主管理</h2>
      <button @click="openAssign"
        class="bg-primary-500 text-white px-4 py-2 rounded-lg text-sm hover:bg-primary-600">
        + 分配版主
      </button>
    </div>

    <!-- 板块选择 -->
    <div class="bg-white rounded-xl border border-ink-200 p-4 mb-4">
      <div class="flex items-center gap-2">
        <span class="text-sm text-ink-500">选择板块:</span>
        <select v-model="selectedPlateId"
          class="border border-ink-300 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:border-primary-500 bg-white">
          <option v-for="plate in plates" :key="plate.id" :value="plate.id">{{ plate.name }}</option>
        </select>
      </div>
    </div>

    <!-- 版主列表 -->
    <div v-if="loading" class="text-center text-ink-400 py-12">加载中...</div>
    <div v-else class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <div v-if="moderators.length === 0" class="text-center text-ink-400 py-12">
        该板块暂无版主
      </div>
      <table v-else class="w-full">
        <thead class="bg-ink-50 border-b border-ink-200">
          <tr>
            <th class="px-4 py-3 text-left text-sm text-ink-500">版主用户 ID</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">板块 ID</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">分配时间</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-ink-100">
          <tr v-for="mod in moderators" :key="mod.id" class="hover:bg-ink-50">
            <td class="px-4 py-3 text-sm text-ink-800">{{ mod.userId }}</td>
            <td class="px-4 py-3 text-sm text-ink-600">{{ mod.plateId }}</td>
            <td class="px-4 py-3 text-sm text-ink-400">{{ mod.createTime }}</td>
            <td class="px-4 py-3">
              <button @click="handleRemove(mod.userId)"
                class="text-sm text-red-500 hover:text-red-600">回收权限</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分配弹窗 -->
    <div v-if="showAssign" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50" @click.self="showAssign = false">
      <div class="bg-white rounded-xl p-6 w-full max-w-md">
        <h3 class="text-lg font-medium text-ink-800 mb-4">分配版主</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-ink-600 mb-1">选择用户</label>
            <select v-model="assignForm.userId"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 bg-white">
              <option :value="undefined" disabled>请选择用户</option>
              <option v-for="user in users" :key="user.id" :value="user.id">
                {{ user.nickname }} ({{ user.account }})
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">选择板块</label>
            <select v-model="assignForm.plateId"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 bg-white">
              <option v-for="plate in plates" :key="plate.id" :value="plate.id">{{ plate.name }}</option>
            </select>
          </div>
          <div class="flex justify-end gap-3 pt-2">
            <button @click="showAssign = false" class="px-4 py-2 text-sm border border-ink-300 rounded-lg text-ink-600">取消</button>
            <button @click="handleAssign" class="px-6 py-2 bg-primary-500 text-white rounded-lg text-sm hover:bg-primary-600">确认分配</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
