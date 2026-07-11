<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'
import { api } from '@/api'
import type { Student } from '@/types'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseSelect from '@/components/ui/BaseSelect.vue'
import BaseModal from '@/components/ui/BaseModal.vue'
import BaseTable from '@/components/ui/BaseTable.vue'
import BasePagination from '@/components/ui/BasePagination.vue'
import SearchBar from '@/components/ui/SearchBar.vue'

// 每页条数：固定 10，与 BasePagination 默认值保持一致
const PAGE_SIZE = 10

// 性别选项：抽取为常量，避免模板内重复字面量
const genderOptions = [
  { label: '男', value: '男' },
  { label: '女', value: '女' },
]

// 表格列配置：序号与操作列均通过插槽自定义渲染
const columns: { key: string; title: string; width?: string; align?: 'left' | 'center' | 'right' }[] = [
  { key: 'index', title: '序号', width: '70px', align: 'center' },
  { key: 'name', title: '姓名' },
  { key: 'studentNo', title: '学号' },
  { key: 'gender', title: '性别', width: '80px', align: 'center' },
  { key: 'age', title: '年龄', width: '80px', align: 'center' },
  { key: 'className', title: '班级' },
  { key: 'phone', title: '电话号' },
  { key: 'updatedAt', title: '更新时间', width: '160px' },
  { key: 'action', title: '操作', width: '150px', align: 'center' },
]

// 全量列表与加载态：onMounted 拉取一次，后续增删改后刷新
const list = ref<Student[]>([])
const loading = ref(false)

// 搜索关键字：前端按学号或姓名模糊匹配
const keyword = ref('')

// 分页：page 由 BasePagination 双向绑定，前端切片
const page = ref(1)

// 过滤结果：关键字为空时返回全量，避免空串误命中
const filteredList = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return list.value
  return list.value.filter(
    (s) => s.name.includes(kw) || s.studentNo.includes(kw),
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

// 表单：gender 给默认值，避免 BaseSelect 命中空 option
const form = reactive<Omit<Student, 'id' | 'updatedAt'>>({
  name: '',
  studentNo: '',
  gender: '男',
  age: '',
  className: '',
  phone: '',
})

// 必填字段校验错误：仅校验姓名与学号
const errors = reactive({ name: '', studentNo: '' })

// BaseSelect 发射 string | number，此处收窄为性别联合类型，保证类型安全
function onGenderChange(val: string | number) {
  form.gender = val as Student['gender']
}

// 重置表单至默认值，供新增与弹窗关闭复用
function resetForm() {
  form.name = ''
  form.studentNo = ''
  form.gender = '男'
  form.age = ''
  form.className = ''
  form.phone = ''
  errors.name = ''
  errors.studentNo = ''
}

// 拉取学生列表，统一处理 loading 与异常
async function loadList() {
  loading.value = true
  try {
    list.value = await api.student.list()
  } catch (err) {
    console.error('加载学生列表失败:', err)
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
function openEdit(row: Student) {
  modalMode.value = 'edit'
  editingId.value = row.id
  form.name = row.name
  form.studentNo = row.studentNo
  form.gender = row.gender
  form.age = row.age
  form.className = row.className
  form.phone = row.phone
  errors.name = ''
  errors.studentNo = ''
  modalVisible.value = true
}

// 表单校验：姓名与学号必填
function validate(): boolean {
  errors.name = form.name.trim() ? '' : '请输入姓名'
  errors.studentNo = form.studentNo.trim() ? '' : '请输入学号'
  return !errors.name && !errors.studentNo
}

// 保存：校验通过后按 mode 调用新增或更新接口
async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    const payload = { ...form }
    if (modalMode.value === 'add') {
      await api.student.create(payload)
      alert('添加成功')
    } else {
      const id = editingId.value
      if (id === null) return
      await api.student.update(id, payload)
      alert('更新成功')
    }
    modalVisible.value = false
    await loadList()
  } catch (err) {
    console.error('保存学生失败:', err)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 删除确认弹窗：仅记录待删除 id，确认后再调接口
const deleteVisible = ref(false)
const deletingId = ref<number | null>(null)
const deleting = ref(false)

function openDelete(row: Student) {
  deletingId.value = row.id
  deleteVisible.value = true
}

async function handleDelete() {
  const id = deletingId.value
  if (id === null) return
  deleting.value = true
  try {
    await api.student.remove(id)
    alert('删除成功')
    deleteVisible.value = false
    await loadList()
  } catch (err) {
    console.error('删除学生失败:', err)
    alert('删除失败，请重试')
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
        placeholder="请输入学号或姓名搜索"
        class="w-80"
        @search="handleSearch"
      />
      <BaseButton variant="primary" @click="openAdd">
        <Plus class="mr-1.5 h-4 w-4" />
        添加学生
      </BaseButton>
    </div>

    <!-- 数据表格 -->
    <BaseTable :columns="columns" :data="pagedList" :loading="loading">
      <template #col-index="{ index }">
        {{ (page - 1) * PAGE_SIZE + index + 1 }}
      </template>
      <template #col-action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <BaseButton variant="secondary" size="sm" @click="openEdit(row as Student)">
            <Pencil class="mr-1 h-3.5 w-3.5" />
            编辑
          </BaseButton>
          <BaseButton variant="danger" size="sm" @click="openDelete(row as Student)">
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
      :title="modalMode === 'add' ? '添加学生' : '编辑学生'"
    >
      <div class="space-y-4">
        <div>
          <label class="mb-1 block text-sm text-ink-700">姓名 <span class="text-red-500">*</span></label>
          <BaseInput v-model="form.name" placeholder="请输入姓名" icon="User" :error="errors.name" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">学号 <span class="text-red-500">*</span></label>
          <BaseInput v-model="form.studentNo" placeholder="请输入学号" :error="errors.studentNo" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">性别</label>
          <BaseSelect
            :model-value="form.gender"
            :options="genderOptions"
            @update:model-value="onGenderChange"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">年龄</label>
          <BaseInput v-model="form.age" type="number" placeholder="请输入年龄" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">班级</label>
          <BaseInput v-model="form.className" placeholder="请输入班级" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">电话号</label>
          <BaseInput v-model="form.phone" placeholder="请输入电话号" icon="Phone" />
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
