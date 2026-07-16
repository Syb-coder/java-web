<script setup lang="ts">
// 板块管理：创建、编辑、删除板块
import { ref, reactive, onMounted } from 'vue'
import { getPlates, adminCreatePlate, adminUpdatePlate, adminDeletePlate } from '@/api'
import type { Plate } from '@/types'

const plates = ref<Plate[]>([])
const loading = ref(false)
const showForm = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  description: '',
  icon: '',
  sortOrder: 0,
})

// 加载板块列表
async function fetchPlates() {
  loading.value = true
  try {
    const resp = await getPlates()
    plates.value = resp.data
  } catch { /* 静默处理 */ }
  finally {
    loading.value = false
  }
}

// 打开新增表单
function openCreate() {
  editingId.value = null
  form.name = ''
  form.description = ''
  form.icon = ''
  form.sortOrder = plates.value.length + 1
  showForm.value = true
}

// 打开编辑表单
function openEdit(plate: Plate) {
  editingId.value = plate.id
  form.name = plate.name
  form.description = plate.description || ''
  form.icon = plate.icon || ''
  form.sortOrder = plate.sortOrder
  showForm.value = true
}

// 提交表单
async function handleSubmit() {
  if (!form.name.trim()) {
    alert('请输入板块名称')
    return
  }
  try {
    if (editingId.value) {
      await adminUpdatePlate(editingId.value, { ...form })
    } else {
      await adminCreatePlate({ ...form })
    }
    showForm.value = false
    await fetchPlates()
  } catch (e: any) {
    alert(e.message || '操作失败')
  }
}

// 删除板块
async function handleDelete(plate: Plate) {
  if (!confirm(`确定删除板块"${plate.name}"吗？`)) return
  try {
    await adminDeletePlate(plate.id)
    await fetchPlates()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

// 格式化时间
function formatTime(time: string) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchPlates()
})
</script>

<template>
  <div>
    <!-- 顶部操作栏 -->
    <div class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-medium text-ink-800">板块列表 ({{ plates.length }})</h2>
      <button @click="openCreate"
        class="bg-primary-500 text-white px-4 py-2 rounded-lg text-sm hover:bg-primary-600">
        + 新建板块
      </button>
    </div>

    <!-- 板块列表 -->
    <div v-if="loading" class="text-center text-ink-400 py-12">加载中...</div>
    <div v-else class="bg-white rounded-xl border border-ink-200 overflow-hidden">
      <table class="w-full">
        <thead class="bg-ink-50 border-b border-ink-200">
          <tr>
            <th class="px-4 py-3 text-left text-sm text-ink-500">ID</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">名称</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">描述</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">帖子数</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">排序</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">创建时间</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">更新时间</th>
            <th class="px-4 py-3 text-left text-sm text-ink-500">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-ink-100">
          <tr v-for="plate in plates" :key="plate.id" class="hover:bg-ink-50">
            <td class="px-4 py-3 text-sm text-ink-600">{{ plate.id }}</td>
            <td class="px-4 py-3 text-sm font-medium text-ink-800">{{ plate.name }}</td>
            <td class="px-4 py-3 text-sm text-ink-500">{{ plate.description || '-' }}</td>
            <td class="px-4 py-3 text-sm text-ink-600">{{ plate.postCount }}</td>
            <td class="px-4 py-3 text-sm text-ink-600">{{ plate.sortOrder }}</td>
            <td class="px-4 py-3 text-sm text-ink-400">{{ formatTime(plate.createTime) }}</td>
            <td class="px-4 py-3 text-sm text-accent-400">{{ formatTime(plate.updateTime) }}</td>
            <td class="px-4 py-3">
              <div class="flex gap-2">
                <button @click="openEdit(plate)" class="text-sm text-accent-500 hover:text-accent-600">编辑</button>
                <button @click="handleDelete(plate)" class="text-sm text-red-500 hover:text-red-600">删除</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <div v-if="showForm" class="fixed inset-0 bg-black/30 flex items-center justify-center z-50" @click.self="showForm = false">
      <div class="bg-white rounded-xl p-6 w-full max-w-md">
        <h3 class="text-lg font-medium text-ink-800 mb-4">{{ editingId ? '编辑板块' : '新建板块' }}</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm text-ink-600 mb-1">名称</label>
            <input v-model="form.name" type="text" maxlength="30"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">描述</label>
            <textarea v-model="form.description" rows="3" maxlength="500"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 resize-none"></textarea>
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">图标 URL（可选）</label>
            <input v-model="form.icon" type="text"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div>
            <label class="block text-sm text-ink-600 mb-1">排序序号（越小越靠前）</label>
            <input v-model="form.sortOrder" type="number" min="0"
              class="w-full px-4 py-2 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
          </div>
          <div class="flex justify-end gap-3 pt-2">
            <button @click="showForm = false" class="px-4 py-2 text-sm border border-ink-300 rounded-lg text-ink-600">取消</button>
            <button @click="handleSubmit" class="px-6 py-2 bg-primary-500 text-white rounded-lg text-sm hover:bg-primary-600">保存</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
