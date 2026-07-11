<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'
import request from '@/api/request'
import type { Admin } from '@/types'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseModal from '@/components/ui/BaseModal.vue'
import BaseTable from '@/components/ui/BaseTable.vue'
import BasePagination from '@/components/ui/BasePagination.vue'
import SearchBar from '@/components/ui/SearchBar.vue'

// 每页条数：固定 10，与 BasePagination 默认值保持一致
const PAGE_SIZE = 10

// 表格列配置：序号与操作列均通过插槽自定义渲染
const columns: { key: string; title: string; width?: string; align?: 'left' | 'center' | 'right' }[] = [
  { key: 'index', title: '序号', width: '70px', align: 'center' },
  { key: 'username', title: '用户名' },
  { key: 'realName', title: '真实姓名' },
  { key: 'phone', title: '电话' },
  { key: 'updatedAt', title: '更新时间', width: '160px' },
  { key: 'action', title: '操作', width: '150px', align: 'center' },
]

// 全量列表与加载态：onMounted 拉取一次，后续增删改后刷新
const list = ref<Admin[]>([])
const loading = ref(false)

// 搜索关键字：前端按用户名或真实姓名模糊匹配
const keyword = ref('')

// 分页：page 由 BasePagination 双向绑定，前端切片
const page = ref(1)

// 过滤结果：关键字为空时返回全量，避免空串误命中
const filteredList = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return list.value
  return list.value.filter(
    (a) => a.username.includes(kw) || a.realName.includes(kw),
  )
})

// 当前页数据：按 page 切片
const pagedList = computed(() => {
  const start = (page.value - 1) * PAGE_SIZE
  return filteredList.value.slice(start, start + PAGE_SIZE)
})

// 列表缩减时回退页码，防止搜索或删除后停留在空页
watch(
  () => filteredList.value.length,
  (len) => {
    const totalPages = Math.max(1, Math.ceil(len / PAGE_SIZE))
    if (page.value > totalPages) page.value = totalPages
  },
)

// 弹窗状态：新增与编辑复用同一弹窗，靠 modalMode 区分提交逻辑
const modalVisible = ref(false)
const modalMode = ref<'add' | 'edit'>('add')
const editingId = ref<number | null>(null)
const saving = ref(false)

// 表单：比 Admin 多 password（仅新增时使用），少 id/updatedAt（由后端维护）
const form = reactive<Omit<Admin, 'id' | 'updatedAt'> & { password: string }>({
  username: '',
  realName: '',
  phone: '',
  password: '',
})

// 必填字段校验错误：用户名与真实姓名必填，新增时密码也必填
const errors = reactive({ username: '', realName: '', password: '' })

// 重置表单至默认值，供新增与弹窗关闭复用
function resetForm() {
  form.username = ''
  form.realName = ''
  form.phone = ''
  form.password = ''
  errors.username = ''
  errors.realName = ''
  errors.password = ''
}

// 拉取管理员列表，统一处理 loading 与异常
async function loadList() {
  loading.value = true
  try {
    const res = await request.get('/users')
    list.value = res.data
  } catch (err) {
    console.error('加载管理员列表失败:', err)
  } finally {
    loading.value = false
  }
}

// 新增：清空表单后打开弹窗
function openAdd() {
  modalMode.value = 'add'
  editingId.value = null
  resetForm()
  modalVisible.value = true
}

// 编辑：用当前行数据回填表单后打开弹窗
function openEdit(row: Admin) {
  modalMode.value = 'edit'
  editingId.value = row.id
  form.username = row.username
  form.realName = row.realName
  form.phone = row.phone
  form.password = '' // 编辑模式不修改密码，清空避免误传
  errors.username = ''
  errors.realName = ''
  errors.password = ''
  modalVisible.value = true
}

// 表单校验：用户名与真实姓名必填，新增时密码也必填；使用卫语句提前返回
function validate(): boolean {
  errors.username = form.username.trim() ? '' : '请输入用户名'
  errors.realName = form.realName.trim() ? '' : '请输入真实姓名'
  // 新增模式下密码必填，编辑模式不校验密码（不修改密码）
  errors.password = modalMode.value === 'add' && !form.password.trim() ? '请输入密码' : ''
  return !errors.username && !errors.realName && !errors.password
}

// 保存：校验通过后按 mode 调用新增或更新接口
async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    if (modalMode.value === 'add') {
      // 新增需要 password 字段，后端创建用户时使用
      await request.post('/users', { ...form, password: form.password })
      alert('添加成功')
    } else {
      const id = editingId.value
      if (id === null) return
      // 编辑模式不传 password，后端仅更新 realName/phone 等字段
      await request.put(`/users/${id}`, { ...form })
      alert('更新成功')
    }
    modalVisible.value = false
    await loadList()
  } catch (err: any) {
    // 后端 400 错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '保存失败，请重试'
    console.error('保存管理员失败:', err)
    alert(msg)
  } finally {
    saving.value = false
  }
}

// 删除确认弹窗：仅记录待删除 id，确认后再调接口
const deleteVisible = ref(false)
const deletingId = ref<number | null>(null)
const deleting = ref(false)

function openDelete(row: Admin) {
  deletingId.value = row.id
  deleteVisible.value = true
}

async function handleDelete() {
  const id = deletingId.value
  if (id === null) return
  deleting.value = true
  try {
    await request.delete(`/users/${id}`)
    alert('删除成功')
    deleteVisible.value = false
    await loadList()
  } catch (err: any) {
    // 后端错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '删除失败，请重试'
    console.error('删除管理员失败:', err)
    alert(msg)
  } finally {
    deleting.value = false
  }
}

// 搜索：回到首页，避免停留在越界页码
function handleSearch() {
  page.value = 1
}

onMounted(loadList)
</script>

<template>
  <div class="space-y-4">
    <!-- 顶部工具栏：左侧搜索 + 右侧新增 -->
    <div class="flex items-center justify-between gap-4">
      <SearchBar
        v-model="keyword"
        placeholder="请输入用户名或真实姓名搜索"
        class="w-80"
        @search="handleSearch"
      />
      <BaseButton variant="primary" @click="openAdd">
        <Plus class="mr-1.5 h-4 w-4" />
        添加管理员
      </BaseButton>
    </div>

    <!-- 数据表格 -->
    <BaseTable :columns="columns" :data="pagedList" :loading="loading">
      <template #col-index="{ index }">
        {{ (page - 1) * PAGE_SIZE + index + 1 }}
      </template>
      <template #col-action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <BaseButton variant="secondary" size="sm" @click="openEdit(row as Admin)">
            <Pencil class="mr-1 h-3.5 w-3.5" />
            编辑
          </BaseButton>
          <BaseButton variant="danger" size="sm" @click="openDelete(row as Admin)">
            <Trash2 class="mr-1 h-3.5 w-3.5" />
            删除
          </BaseButton>
        </div>
      </template>
    </BaseTable>

    <!-- 分页 -->
    <div class="flex justify-end">
      <BasePagination
        v-model:page="page"
        :total="filteredList.length"
        :page-size="PAGE_SIZE"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <BaseModal
      v-model="modalVisible"
      :title="modalMode === 'add' ? '添加管理员' : '编辑管理员'"
    >
      <div class="space-y-4">
        <div>
          <label class="mb-1 block text-sm text-ink-700">用户名 <span class="text-red-500">*</span></label>
          <BaseInput v-model="form.username" placeholder="请输入用户名" :error="errors.username" />
        </div>
        <!-- 密码仅新增时需要，编辑不修改密码 -->
        <div v-if="modalMode === 'add'">
          <label class="mb-1 block text-sm text-ink-700">密码 <span class="text-red-500">*</span></label>
          <BaseInput v-model="form.password" type="password" placeholder="请输入密码" icon="Lock" :error="errors.password" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">真实姓名 <span class="text-red-500">*</span></label>
          <BaseInput v-model="form.realName" placeholder="请输入真实姓名" :error="errors.realName" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">电话</label>
          <BaseInput v-model="form.phone" placeholder="请输入电话" icon="Phone" />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <BaseButton variant="secondary" @click="modalVisible = false">取消</BaseButton>
          <BaseButton variant="primary" :loading="saving" @click="handleSave">保存</BaseButton>
        </div>
      </template>
    </BaseModal>

    <!-- 删除确认弹窗 -->
    <BaseModal v-model="deleteVisible" title="确认删除" width="400px">
      <p class="text-sm text-ink-700">删除后无法恢复，确定删除？</p>
      <template #footer>
        <div class="flex justify-end gap-2">
          <BaseButton variant="secondary" @click="deleteVisible = false">取消</BaseButton>
          <BaseButton variant="danger" :loading="deleting" @click="handleDelete">确认删除</BaseButton>
        </div>
      </template>
    </BaseModal>
  </div>
</template>
