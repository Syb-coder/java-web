<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Plus, Pencil, Trash2, Users, TrendingUp, TrendingDown, Award } from 'lucide-vue-next'
import { api } from '@/api'
import type { Score, Course, Student } from '@/types'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseInput from '@/components/ui/BaseInput.vue'
import BaseSelect from '@/components/ui/BaseSelect.vue'
import BaseModal from '@/components/ui/BaseModal.vue'
import BaseTable from '@/components/ui/BaseTable.vue'
import BasePagination from '@/components/ui/BasePagination.vue'
import SearchBar from '@/components/ui/SearchBar.vue'

// 每页条数：固定 10，与 BasePagination 默认值保持一致
const PAGE_SIZE = 10

// 分数取值边界：业务规则限定 0-100，避免魔法值散落
const SCORE_MIN = 0
const SCORE_MAX = 100

// 表格列配置：序号与操作列通过插槽自定义渲染
const columns: { key: string; title: string; width?: string; align?: 'left' | 'center' | 'right' }[] = [
  { key: 'index', title: '序号', width: '70px', align: 'center' },
  { key: 'studentNo', title: '学号' },
  { key: 'studentName', title: '学生姓名' },
  { key: 'courseName', title: '课程名称' },
  { key: 'score', title: '分数', width: '100px', align: 'center' },
  { key: 'updatedAt', title: '更新时间', width: '160px' },
  { key: 'action', title: '操作', width: '150px', align: 'center' },
]

// 三个主数据源：成绩为主表，课程与学生为关联展示数据
const scoreList = ref<Score[]>([])
const courseList = ref<Course[]>([])
const studentList = ref<Student[]>([])
const loading = ref(false)

// 搜索关键字：按学号或课程名模糊匹配
const keyword = ref('')
// 分页：page 由 BasePagination 双向绑定，前端切片
const page = ref(1)

// 课程 id -> 课程名 映射：避免表格渲染时反复线性查找
const courseMap = computed<Record<number, string>>(() => {
  const map: Record<number, string> = {}
  courseList.value.forEach((c) => (map[c.id] = c.name))
  return map
})

// 学号 -> 学生名 映射：同上
const studentMap = computed<Record<string, string>>(() => {
  const map: Record<string, string> = {}
  studentList.value.forEach((s) => (map[s.studentNo] = s.name))
  return map
})

// 统计指标：基于全量成绩计算，score 为 string 需先转 number 再聚合
const stats = computed(() => {
  const nums = scoreList.value.map((s) => Number(s.score)).filter((n) => !Number.isNaN(n))
  const count = nums.length
  // 列表为空时各项指标给 0，避免除零与 undefined 传导到模板
  if (count === 0) {
    return { count: 0, avg: '0', max: '0', min: '0' }
  }
  const sum = nums.reduce((acc, n) => acc + n, 0)
  return {
    count,
    avg: (sum / count).toFixed(1),
    max: String(Math.max(...nums)),
    min: String(Math.min(...nums)),
  }
})

// 过滤结果：按学号或课程名匹配，关键字为空时返回全量
const filteredList = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return scoreList.value
  return scoreList.value.filter((s) => {
    const courseName = courseMap.value[s.courseId] || ''
    return s.studentNo.includes(kw) || courseName.includes(kw)
  })
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

// 表单：studentNo / courseId / score 三字段对应成绩实体
const form = reactive({
  studentNo: '' as string,
  courseId: 0 as number,
  score: '' as string,
})

// 校验错误：仅分数需范围校验，选择项由 placeholder 兜底
const errors = reactive({ score: '' })

// 学生下拉选项：label 拼接学号与姓名便于辨认，value 为学号
const studentOptions = computed(() =>
  studentList.value.map((s) => ({ label: `${s.studentNo}-${s.name}`, value: s.studentNo })),
)

// 课程下拉选项：value 为课程 id（number），BaseSelect 会回填原始类型
const courseOptions = computed(() =>
  courseList.value.map((c) => ({ label: c.name, value: c.id })),
)

// 重置表单至默认值，供新增与弹窗关闭复用
function resetForm() {
  form.studentNo = ''
  form.courseId = 0
  form.score = ''
  errors.score = ''
}

// 并行加载三个列表：成绩、课程、学生缺一不可，任一失败仅打日志不阻断其余
async function loadAll() {
  loading.value = true
  try {
    const [scores, courses, students] = await Promise.all([
      api.score.list(),
      api.course.list(),
      api.student.list(),
    ])
    scoreList.value = scores
    courseList.value = courses
    studentList.value = students
  } catch (err) {
    console.error('加载成绩相关数据失败:', err)
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
function openEdit(row: Score) {
  modalMode.value = 'edit'
  editingId.value = row.id
  form.studentNo = row.studentNo
  form.courseId = row.courseId
  form.score = row.score
  errors.score = ''
  modalVisible.value = true
}

// 分数校验：必填且在 0-100 区间，超界给出明确提示
function validate(): boolean {
  const val = form.score.trim()
  if (!val) {
    errors.score = '请输入分数'
    return false
  }
  const num = Number(val)
  if (Number.isNaN(num) || num < SCORE_MIN || num > SCORE_MAX) {
    errors.score = `分数必须在 ${SCORE_MIN}-${SCORE_MAX} 之间`
    return false
  }
  errors.score = ''
  return true
}

// 保存：校验通过后按 mode 调用新增或更新接口
async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    const payload = { ...form }
    if (modalMode.value === 'add') {
      await api.score.create(payload)
      alert('录入成功')
    } else {
      const id = editingId.value
      if (id === null) return
      await api.score.update(id, payload)
      alert('更新成功')
    }
    modalVisible.value = false
    await loadAll()
  } catch (err) {
    console.error('保存成绩失败:', err)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 删除确认弹窗：仅记录待删除 id，确认后再调接口
const deleteVisible = ref(false)
const deletingId = ref<number | null>(null)
const deleting = ref(false)

function openDelete(row: Score) {
  deletingId.value = row.id
  deleteVisible.value = true
}

async function handleDelete() {
  const id = deletingId.value
  if (id === null) return
  deleting.value = true
  try {
    await api.score.remove(id)
    alert('删除成功')
    deleteVisible.value = false
    await loadAll()
  } catch (err) {
    console.error('删除成绩失败:', err)
    alert('删除失败，请重试')
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
    <!-- 顶部工具栏：左侧搜索 + 右侧录入 -->
    <div class="flex items-center justify-between gap-4">
      <SearchBar
        v-model="keyword"
        placeholder="请输入学号或课程名搜索"
        class="w-80"
        @search="handleSearch"
      />
      <BaseButton variant="primary" @click="openAdd">
        <Plus class="mr-1.5 h-4 w-4" />
        录入成绩
      </BaseButton>
    </div>

    <!-- 统计卡片行：四个指标横向排列 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-primary-50">
          <Users class="h-5 w-5 text-primary-600" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.count }}</div>
          <div class="text-xs text-ink-500">参与人数</div>
        </div>
      </div>
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-50">
          <TrendingUp class="h-5 w-5 text-emerald-600" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.avg }}</div>
          <div class="text-xs text-ink-500">平均分</div>
        </div>
      </div>
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-orange-50">
          <Award class="h-5 w-5 text-orange-500" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.max }}</div>
          <div class="text-xs text-ink-500">最高分</div>
        </div>
      </div>
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-red-50">
          <TrendingDown class="h-5 w-5 text-red-500" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.min }}</div>
          <div class="text-xs text-ink-500">最低分</div>
        </div>
      </div>
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
          <BaseButton variant="secondary" size="sm" @click="openEdit(row as Score)">
            <Pencil class="mr-1 h-3.5 w-3.5" />
            编辑
          </BaseButton>
          <BaseButton variant="danger" size="sm" @click="openDelete(row as Score)">
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

    <!-- 录入/编辑弹窗 -->
    <BaseModal
      v-model="modalVisible"
      :title="modalMode === 'add' ? '录入成绩' : '编辑成绩'"
    >
      <div class="space-y-4">
        <div>
          <label class="mb-1 block text-sm text-ink-700">学生 <span class="text-red-500">*</span></label>
          <BaseSelect
            v-model="form.studentNo"
            :options="studentOptions"
            placeholder="请选择学生"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">课程 <span class="text-red-500">*</span></label>
          <BaseSelect
            v-model="form.courseId"
            :options="courseOptions"
            placeholder="请选择课程"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">分数 <span class="text-red-500">*</span></label>
          <BaseInput
            v-model="form.score"
            type="number"
            placeholder="请输入 0-100 的分数"
            :error="errors.score"
          />
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
