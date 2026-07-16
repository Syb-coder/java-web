<script setup lang="ts">
// 帖子编辑/发布页
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPlates, createPost, updatePost, getPostById } from '@/api'
import type { Plate } from '@/types'

const route = useRoute()
const router = useRouter()

const isEdit = ref(false)
const plates = ref<Plate[]>([])
const loading = ref(false)

const form = reactive({
  title: '',
  content: '',
  plateId: 0,
})

// 加载板块列表
async function fetchPlates() {
  try {
    const resp = await getPlates()
    plates.value = resp.data
    if (plates.value.length > 0 && !form.plateId) {
      form.plateId = plates.value[0].id
    }
  } catch { /* 静默处理 */ }
}

// 如果是编辑模式，加载帖子内容
async function fetchPost() {
  const id = route.params.id
  if (!id) return
  isEdit.value = true
  try {
    const resp = await getPostById(Number(id))
    form.title = resp.data.title
    form.content = resp.data.content
    form.plateId = resp.data.plateId
  } catch (e: any) {
    alert(e.message || '帖子加载失败')
    router.push('/')
  }
}

// 提交
async function handleSubmit() {
  if (!form.title.trim()) {
    alert('请输入标题')
    return
  }
  if (form.title.length < 2 || form.title.length > 100) {
    alert('标题长度需 2-100 个字符')
    return
  }
  if (!form.content.trim()) {
    alert('请输入内容')
    return
  }
  if (!form.plateId) {
    alert('请选择板块')
    return
  }
  loading.value = true
  try {
    if (isEdit.value) {
      await updatePost(Number(route.params.id), { ...form })
      router.push(`/post/${route.params.id}`)
    } else {
      const resp = await createPost({ ...form })
      router.push(`/post/${resp.data.id}`)
    }
  } catch (e: any) {
    alert(e.message || '操作失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPlates()
  fetchPost()
})
</script>

<template>
  <div class="max-w-3xl mx-auto">
    <div class="bg-white rounded-xl border border-ink-200 p-6">
      <h1 class="text-xl font-bold text-ink-800 mb-6">{{ isEdit ? '编辑帖子' : '发布帖子' }}</h1>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <!-- 板块选择 -->
        <div>
          <label class="block text-sm text-ink-600 mb-1">板块</label>
          <select v-model="form.plateId"
            class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 bg-white">
            <option v-for="plate in plates" :key="plate.id" :value="plate.id">{{ plate.name }}</option>
          </select>
        </div>

        <!-- 标题 -->
        <div>
          <label class="block text-sm text-ink-600 mb-1">标题</label>
          <input v-model="form.title" type="text" maxlength="100" placeholder="请输入标题（2-100字符）"
            class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500" />
        </div>

        <!-- 内容 -->
        <div>
          <label class="block text-sm text-ink-600 mb-1">内容</label>
          <textarea v-model="form.content" rows="15" placeholder="请输入帖子内容..."
            class="w-full px-4 py-2.5 border border-ink-300 rounded-lg focus:outline-none focus:border-primary-500 resize-none"></textarea>
        </div>

        <!-- 按钮组 -->
        <div class="flex items-center justify-end gap-3 pt-2">
          <button type="button" @click="router.back()"
            class="px-4 py-2 text-sm border border-ink-300 rounded-lg text-ink-600 hover:bg-ink-50">
            取消
          </button>
          <button type="submit" :disabled="loading"
            class="px-6 py-2 bg-primary-500 text-white rounded-lg text-sm hover:bg-primary-600 disabled:opacity-50">
            {{ loading ? '提交中...' : (isEdit ? '保存修改' : '发布') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
