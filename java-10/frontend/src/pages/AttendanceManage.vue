<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Plus, Pencil, Trash2 } from 'lucide-vue-next'
import request from '@/api/request'
import type { Attendance, Student, Course } from '@/types'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseSelect from '@/components/ui/BaseSelect.vue'
import BaseModal from '@/components/ui/BaseModal.vue'
import BaseTable from '@/components/ui/BaseTable.vue'
import BasePagination from '@/components/ui/BasePagination.vue'
import SearchBar from '@/components/ui/SearchBar.vue'

// 每页条数：固定 10，与 BasePagination 默认值保持一致
const PAGE_SIZE = 10

// 考勤状态选项：抽取为常量，避免模板内重复字面量
const statusOptions = [
  { label: '出勤', value: '出勤' },
  { label: '迟到', value: '迟到' },
  { label: '旷课', value: '旷课' },
  { label: '请假', value: '请假' },
]

// 表格列配置：序号与操作列通过插槽自定义渲染
const columns: { key: string; title: string; width?: string; align?: 'left' | 'center' | 'right' }[] = [
  { key: 'index', title: '序号', width: '70px', align: 'center' },
  { key: 'studentNo', title: '学号' },
  { key: 'studentName', title: '学生姓名' },
  { key: 'courseName', title: '课程名称' },
  { key: 'date', title: '日期', width: '120px' },
  { key: 'status', title: '状态', width: '80px', align: 'center' },
  { key: 'updatedAt', title: '更新时间', width: '160px' },
  { key: 'action', title: '操作', width: '150px', align: 'center' },
]

// 三个主数据源：考勤为主表，学生与课程为关联展示数据
const attendanceList = ref<Attendance[]>([])
const studentList = ref<Student[]>([])
const courseList = ref<Course[]>([])
const loading = ref(false)

// 搜索关键字：按学号或日期模糊匹配
const keyword = ref('')
// 分页：page 由 BasePagination 双向绑定，前端切片
const page = ref(1)

// 学号 -> 学生名 映射：避免表格渲染时反复线性查找
const studentMap = computed<Record<string, string>>(() => {
  const map: Record<string, string> = {}
  studentList.value.forEach((s) => (map[s.studentNo] = s.name))
  return map
})

// 课程 id -> 课程名 映射：同上
const courseMap = computed<Record<number, string>>(() => {
  const map: Record<number, string> = {}
  courseList.value.forEach((c) => (map[c.id] = c.name))
  return map
})

// 学生下拉选项：label 拼接学号与姓名便于辨认，value 为学号
const studentOptions = computed(() =>
  studentList.value.map((s) => ({ label: `${s.studentNo}-${s.name}`, value: s.studentNo })),
)

// 课程下拉选项：value 为课程 id（number），BaseSelect 会回填原始类型
const courseOptions = computed(() =>
  courseList.value.map((c) => ({ label: c.name, value: c.id })),
)

// 过滤结果：按学号或日期匹配，关键字为空时返回全量
const filteredList = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return attendanceList.value
  return attendanceList.value.filter(
    (a) => a.studentNo.includes(kw) || a.date.includes(kw),
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

// 表单：updatedAt 由 API 层维护，表单不包含该字段
// status 给默认值，避免 BaseSelect 命中空 option
const form = reactive({
  studentNo: '' as string,
  courseId: 0 as number,
  date: '' as string,
  status: '出勤' as Attendance['status'],
})

// 必填字段校验错误：学生、课程、日期、状态均需校验
const errors = reactive({ studentNo: '', courseId: '', date: '', status: '' })

// BaseSelect 发射 string | number，此处收窄为具体类型，保证类型安全
function onStudentChange(val: string | number) {
  form.studentNo = val as string
}

function onCourseChange(val: string | number) {
  form.courseId = val as number
}

function onStatusChange(val: string | number) {
  form.status = val as Attendance['status']
}

// 重置表单至默认值，供新增与弹窗关闭复用
function resetForm() {
  form.studentNo = ''
  form.courseId = 0
  form.date = ''
  form.status = '出勤'
  errors.studentNo = ''
  errors.courseId = ''
  errors.date = ''
  errors.status = ''
}

// 并行加载三个列表：考勤、学生、课程缺一不可，任一失败仅打日志不阻断其余
async function loadAll() {
  loading.value = true
  try {
    const [attendanceRes, studentRes, courseRes] = await Promise.all([
      request.get('/attendances'),
      request.get('/students'),
      request.get('/courses'),
    ])
    attendanceList.value = attendanceRes.data
    studentList.value = studentRes.data
    courseList.value = courseRes.data
  } catch (err) {
    console.error('加载考勤相关数据失败:', err)
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
function openEdit(row: Attendance) {
  modalMode.value = 'edit'
  editingId.value = row.id
  form.studentNo = row.studentNo
  form.courseId = row.courseId
  form.date = row.date
  form.status = row.status
  errors.studentNo = ''
  errors.courseId = ''
  errors.date = ''
  errors.status = ''
  modalVisible.value = true
}

// 表单校验：使用卫语句提前返回，杜绝超过3层嵌套
function validate(): boolean {
  errors.studentNo = form.studentNo ? '' : '请选择学生'
  errors.courseId = form.courseId ? '' : '请选择课程'
  errors.date = form.date ? '' : '请选择日期'
  errors.status = form.status ? '' : '请选择状态'
  return !errors.studentNo && !errors.courseId && !errors.date && !errors.status
}

// 保存：校验通过后按 mode 调用新增或更新接口
async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    const payload = { ...form }
    if (modalMode.value === 'add') {
      await request.post('/attendances', payload)
      alert('添加成功')
    } else {
      const id = editingId.value
      if (id === null) return
      await request.put(`/attendances/${id}`, payload)
      alert('更新成功')
    }
    modalVisible.value = false
    await loadAll()
  } catch (err: any) {
    // 后端错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '保存失败，请重试'
    console.error('保存考勤失败:', err)
    alert(msg)
  } finally {
    saving.value = false
  }
}

// 删除确认弹窗：仅记录待删除 id，确认后再调接口
const deleteVisible = ref(false)
const deletingId = ref<number | null>(null)
const deleting = ref(false)

function openDelete(row: Attendance) {
  deletingId.value = row.id
  deleteVisible.value = true
}

async function handleDelete() {
  const id = deletingId.value
  if (id === null) return
  deleting.value = true
  try {
    await request.delete(`/attendances/${id}`)
    alert('删除成功')
    deleteVisible.value = false
    await loadAll()
  } catch (err: any) {
    // 后端错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '删除失败，请重试'
    console.error('删除考勤失败:', err)
    alert(msg)
  } finally {
    deleting.value = false
  }
}

// 搜索：回到首页，避免停留在越界页码
function handleSearch() {
  page.value = 1
}

onMounted(loadAll)
</script>

<template>
  <div class="space-y-4">
    <!-- 顶部工具栏：左侧搜索 + 右侧新增 -->
    <div class="flex items-center justify-between gap-4">
      <SearchBar
        v-model="keyword"
        placeholder="请输入学号或日期搜索"
        class="w-80"
        @search="handleSearch"
      />
      <BaseButton variant="primary" @click="openAdd">
        <Plus class="mr-1.5 h-4 w-4" />
        添加考勤
      </BaseButton>
    </div>

    <!-- 数据表格 -->
    <BaseTable :columns="columns" :data="pagedList" :loading="loading">
      <template #col-index="{ index }">
        {{ (page - 1) * PAGE_SIZE + index + 1 }}
      </template>
      <template #col-studentName="{ row }">
        {{ studentMap[row.studentNo] || '-' }}
      </template>
      <template #col-courseName="{ row }">
        {{ courseMap[row.courseId] || '-' }}
      </template>
      <template #col-action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <BaseButton variant="secondary" size="sm" @click="openEdit(row as Attendance)">
            <Pencil class="mr-1 h-3.5 w-3.5" />
            编辑
          </BaseButton>
          <BaseButton variant="danger" size="sm" @click="openDelete(row as Attendance)">
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
      :title="modalMode === 'add' ? '添加考勤' : '编辑考勤'"
    >
      <div class="space-y-4">
        <div>
          <label class="mb-1 block text-sm text-ink-700">学生 <span class="text-red-500">*</span></label>
          <BaseSelect
            :model-value="form.studentNo"
            :options="studentOptions"
            placeholder="请选择学生"
            @update:model-value="onStudentChange"
          />
          <p v-if="errors.studentNo" class="mt-1 text-xs text-red-500">{{ errors.studentNo }}</p>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">课程 <span class="text-red-500">*</span></label>
          <BaseSelect
            :model-value="form.courseId"
            :options="courseOptions"
            placeholder="请选择课程"
            @update:model-value="onCourseChange"
          />
          <p v-if="errors.courseId" class="mt-1 text-xs text-red-500">{{ errors.courseId }}</p>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">日期 <span class="text-red-500">*</span></label>
          <BaseInput v-model="form.date" type="date" placeholder="请选择日期" :error="errors.date" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">状态 <span class="text-red-500">*</span></label>
          <BaseSelect
            :model-value="form.status"
            :options="statusOptions"
            placeholder="请选择状态"
            @update:model-value="onStatusChange"
          />
          <p v-if="errors.status" class="mt-1 text-xs text-red-500">{{ errors.status }}</p>
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
