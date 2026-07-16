<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Plus, Pencil, Trash2, ClipboardCheck, Clock, CheckCircle2, XCircle } from 'lucide-vue-next'
import request from '@/api/request'
import type { Approval, Student } from '@/types'
import BaseButton from '@/components/ui/BaseButton.vue'
import BaseSelect from '@/components/ui/BaseSelect.vue'
import BaseModal from '@/components/ui/BaseModal.vue'
import BaseTable from '@/components/ui/BaseTable.vue'
import BasePagination from '@/components/ui/BasePagination.vue'
import SearchBar from '@/components/ui/SearchBar.vue'

// 每页条数：固定 10，与 BasePagination 默认值保持一致
const PAGE_SIZE = 10

// 申请类型选项：业务枚举固定，抽取为常量避免散落
const typeOptions = [
  { label: '请假', value: '请假' },
  { label: '休学', value: '休学' },
  { label: '奖学金申请', value: '奖学金申请' },
  { label: '其他', value: '其他' },
]

// 状态 -> tag 样式映射：颜色与业务语义一致，集中维护避免模板内散落字面量
const statusStyleMap: Record<Approval['status'], string> = {
  '待审批': 'bg-amber-50 text-amber-700',
  '已通过': 'bg-emerald-50 text-emerald-700',
  '已驳回': 'bg-red-50 text-red-700',
}

// 表格列配置：序号与操作列通过插槽自定义渲染
const columns: { key: string; title: string; width?: string; align?: 'left' | 'center' | 'right' }[] = [
  { key: 'index', title: '序号', width: '70px', align: 'center' },
  { key: 'studentNo', title: '学号' },
  { key: 'studentName', title: '学生姓名' },
  { key: 'type', title: '申请类型' },
  { key: 'reason', title: '申请理由' },
  { key: 'status', title: '状态', width: '100px', align: 'center' },
  { key: 'updatedAt', title: '更新时间', width: '160px' },
  { key: 'action', title: '操作', width: '240px', align: 'center' },
]

// 审批列表为主表，学生列表用于名称关联展示
const approvalList = ref<Approval[]>([])
const studentList = ref<Student[]>([])
const loading = ref(false)

// 搜索关键字：按学号或申请类型模糊匹配
const keyword = ref('')
// 分页：page 由 BasePagination 双向绑定，前端切片
const page = ref(1)

// 学号 -> 学生名 映射：避免表格渲染时反复线性查找
const studentMap = computed<Record<string, string>>(() => {
  const map: Record<string, string> = {}
  studentList.value.forEach((s) => (map[s.studentNo] = s.name))
  return map
})

// 统计指标：按状态聚合，列表为空时各项为 0
const stats = computed(() => {
  const pending = approvalList.value.filter((a) => a.status === '待审批').length
  const passed = approvalList.value.filter((a) => a.status === '已通过').length
  const rejected = approvalList.value.filter((a) => a.status === '已驳回').length
  return { pending, passed, rejected }
})

// 过滤结果：按学号或申请类型匹配，关键字为空时返回全量
const filteredList = computed(() => {
  const kw = keyword.value.trim()
  if (!kw) return approvalList.value
  return approvalList.value.filter(
    (a) => a.studentNo.includes(kw) || a.type.includes(kw),
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

// 表单：仅业务字段，status/opinion/createdAt 由后端或审批流程维护
const form = reactive({
  studentNo: '' as string,
  type: '' as string,
  reason: '' as string,
})

// 必填字段校验错误
const errors = reactive({ studentNo: '', type: '', reason: '' })

// 学生下拉选项：label 拼接学号与姓名便于辨认，value 为学号
const studentOptions = computed(() =>
  studentList.value.map((s) => ({ label: `${s.studentNo}-${s.name}`, value: s.studentNo })),
)

// BaseSelect 发射 string | number，此处收窄为 string，保证类型安全
function onStudentChange(val: string | number) {
  form.studentNo = val as string
}

function onTypeChange(val: string | number) {
  form.type = val as string
}

// 重置表单至默认值，供新增与弹窗关闭复用
function resetForm() {
  form.studentNo = ''
  form.type = ''
  form.reason = ''
  errors.studentNo = ''
  errors.type = ''
  errors.reason = ''
}

// 并行加载审批与学生列表：任一失败仅打日志不阻断其余
async function loadAll() {
  loading.value = true
  try {
    const [approvalRes, studentRes] = await Promise.all([
      request.get('/approvals'),
      request.get('/students'),
    ])
    approvalList.value = approvalRes.data
    studentList.value = studentRes.data
  } catch (err) {
    console.error('加载审批相关数据失败:', err)
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

// 编辑：用当前行数据回填表单后打开弹窗（仅业务字段，不动 status）
function openEdit(row: Approval) {
  modalMode.value = 'edit'
  editingId.value = row.id
  form.studentNo = row.studentNo
  form.type = row.type
  form.reason = row.reason
  errors.studentNo = ''
  errors.type = ''
  errors.reason = ''
  modalVisible.value = true
}

// 表单校验：三个字段均必填
function validate(): boolean {
  errors.studentNo = form.studentNo ? '' : '请选择学生'
  errors.type = form.type ? '' : '请选择申请类型'
  errors.reason = form.reason.trim() ? '' : '请输入申请理由'
  return !errors.studentNo && !errors.type && !errors.reason
}

// 保存：校验通过后按 mode 调用新增或更新接口
async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    if (modalMode.value === 'add') {
      // 新增时补全默认状态与时间戳：status/opinion/createdAt 不在表单中
      // createdAt 由前端生成，与 mock 数据格式保持一致
      const payload = {
        studentNo: form.studentNo,
        type: form.type,
        reason: form.reason,
        status: '待审批' as Approval['status'],
        opinion: '',
        createdAt: new Date().toLocaleString('zh-CN', { hour12: false }),
      }
      await request.post('/approvals', payload)
      alert('提交成功')
    } else {
      const id = editingId.value
      if (id === null) return
      // 编辑仅更新业务字段，避免覆盖已有 status/opinion
      const payload = {
        studentNo: form.studentNo,
        type: form.type,
        reason: form.reason,
      }
      await request.put(`/approvals/${id}`, payload)
      alert('更新成功')
    }
    modalVisible.value = false
    await loadAll()
  } catch (err: any) {
    // 后端错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '保存失败，请重试'
    console.error('保存审批失败:', err)
    alert(msg)
  } finally {
    saving.value = false
  }
}

// 删除确认弹窗：仅记录待删除 id，确认后再调接口
const deleteVisible = ref(false)
const deletingId = ref<number | null>(null)
const deleting = ref(false)

function openDelete(row: Approval) {
  deletingId.value = row.id
  deleteVisible.value = true
}

async function handleDelete() {
  const id = deletingId.value
  if (id === null) return
  deleting.value = true
  try {
    await request.delete(`/approvals/${id}`)
    alert('删除成功')
    deleteVisible.value = false
    await loadAll()
  } catch (err: any) {
    // 后端错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '删除失败，请重试'
    console.error('删除审批失败:', err)
    alert(msg)
  } finally {
    deleting.value = false
  }
}

// 审批弹窗：独立于新增/编辑弹窗，专用于状态流转
const reviewVisible = ref(false)
const reviewingId = ref<number | null>(null)
const reviewForm = reactive({ opinion: '' })
const reviewError = ref('')
const reviewing = ref(false)

// 当前审批对象：用于弹窗内展示申请详情
const currentReview = computed(() =>
  approvalList.value.find((a) => a.id === reviewingId.value) || null,
)

function openReview(row: Approval) {
  reviewingId.value = row.id
  reviewForm.opinion = ''
  reviewError.value = ''
  reviewVisible.value = true
}

// 审批意见校验：必填，卫语句提前返回
function validateReview(): boolean {
  if (!reviewForm.opinion.trim()) {
    reviewError.value = '请输入审批意见'
    return false
  }
  reviewError.value = ''
  return true
}

// 提交审批：pass=true 通过，pass=false 驳回，统一走 update 接口
async function handleReview(pass: boolean) {
  if (!validateReview()) return
  const id = reviewingId.value
  if (id === null) return
  reviewing.value = true
  try {
    await request.put(`/approvals/${id}`, {
      status: pass ? '已通过' : '已驳回',
      opinion: reviewForm.opinion.trim(),
    })
    alert(pass ? '审批通过' : '已驳回')
    reviewVisible.value = false
    await loadAll()
  } catch (err: any) {
    // 后端错误返回 { error: "..." }，提取展示；其他错误降级为通用提示
    const msg = err.response?.data?.error || '审批失败，请重试'
    console.error('审批失败:', err)
    alert(msg)
  } finally {
    reviewing.value = false
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
    <!-- 顶部工具栏：左侧搜索 + 右侧提交申请 -->
    <div class="flex items-center justify-between gap-4">
      <SearchBar
        v-model="keyword"
        placeholder="请输入学号或申请类型搜索"
        class="w-80"
        @search="handleSearch"
      />
      <BaseButton variant="primary" @click="openAdd">
        <Plus class="mr-1.5 h-4 w-4" />
        提交申请
      </BaseButton>
    </div>

    <!-- 统计卡片行：三个状态计数横向排列 -->
    <div class="grid grid-cols-3 gap-4">
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-amber-50">
          <Clock class="h-5 w-5 text-amber-600" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.pending }}</div>
          <div class="text-xs text-ink-500">待审批数</div>
        </div>
      </div>
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-emerald-50">
          <CheckCircle2 class="h-5 w-5 text-emerald-600" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.passed }}</div>
          <div class="text-xs text-ink-500">已通过数</div>
        </div>
      </div>
      <div class="flex items-center rounded-lg bg-white p-4 shadow-card">
        <div class="mr-3 flex h-10 w-10 items-center justify-center rounded-lg bg-red-50">
          <XCircle class="h-5 w-5 text-red-500" />
        </div>
        <div>
          <div class="text-xl font-semibold text-ink-800">{{ stats.rejected }}</div>
          <div class="text-xs text-ink-500">已驳回数</div>
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
      <template #col-status="{ row }">
        <span
          class="inline-flex rounded px-2 py-0.5 text-xs font-medium"
          :class="statusStyleMap[row.status as Approval['status']]"
        >
          {{ row.status }}
        </span>
      </template>
      <template #col-action="{ row }">
        <div class="flex items-center justify-center gap-2">
          <!-- 编辑与审批仅对"待审批"记录开放，已流转的记录不可再改 -->
          <BaseButton
            v-if="row.status === '待审批'"
            variant="secondary"
            size="sm"
            @click="openEdit(row as Approval)"
          >
            <Pencil class="mr-1 h-3.5 w-3.5" />
            编辑
          </BaseButton>
          <BaseButton
            v-if="row.status === '待审批'"
            variant="primary"
            size="sm"
            @click="openReview(row as Approval)"
          >
            <ClipboardCheck class="mr-1 h-3.5 w-3.5" />
            审批
          </BaseButton>
          <BaseButton variant="danger" size="sm" @click="openDelete(row as Approval)">
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
      :title="modalMode === 'add' ? '提交申请' : '编辑申请'"
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
          <label class="mb-1 block text-sm text-ink-700">申请类型 <span class="text-red-500">*</span></label>
          <BaseSelect
            :model-value="form.type"
            :options="typeOptions"
            placeholder="请选择申请类型"
            @update:model-value="onTypeChange"
          />
          <p v-if="errors.type" class="mt-1 text-xs text-red-500">{{ errors.type }}</p>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">申请理由 <span class="text-red-500">*</span></label>
          <!-- BaseInput 不支持 textarea，此处用原生 textarea 配合同款样式，保证视觉一致 -->
          <textarea
            v-model="form.reason"
            rows="3"
            placeholder="请输入申请理由"
            class="w-full rounded-md border bg-white px-3 py-2 text-sm text-ink-800 placeholder-ink-400 transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500/30"
            :class="errors.reason ? 'border-red-500' : 'border-ink-200'"
          />
          <p v-if="errors.reason" class="mt-1 text-xs text-red-500">{{ errors.reason }}</p>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <BaseButton variant="secondary" @click="modalVisible = false">取消</BaseButton>
          <BaseButton variant="primary" :loading="saving" @click="handleSave">保存</BaseButton>
        </div>
      </template>
    </BaseModal>

    <!-- 审批弹窗：展示申请详情并输入审批意见 -->
    <BaseModal v-model="reviewVisible" title="审批申请" width="480px">
      <div v-if="currentReview" class="space-y-3">
        <div class="rounded-lg bg-ink-50 p-3 text-sm">
          <div class="mb-1"><span class="text-ink-500">学号：</span>{{ currentReview.studentNo }}</div>
          <div class="mb-1"><span class="text-ink-500">申请类型：</span>{{ currentReview.type }}</div>
          <div><span class="text-ink-500">申请理由：</span>{{ currentReview.reason }}</div>
        </div>
        <div>
          <label class="mb-1 block text-sm text-ink-700">审批意见 <span class="text-red-500">*</span></label>
          <textarea
            v-model="reviewForm.opinion"
            rows="3"
            placeholder="请输入审批意见"
            class="w-full rounded-md border bg-white px-3 py-2 text-sm text-ink-800 placeholder-ink-400 transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500/30"
            :class="reviewError ? 'border-red-500' : 'border-ink-200'"
          />
          <p v-if="reviewError" class="mt-1 text-xs text-red-500">{{ reviewError }}</p>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <BaseButton variant="secondary" @click="reviewVisible = false">取消</BaseButton>
          <BaseButton variant="danger" :loading="reviewing" @click="handleReview(false)">驳回</BaseButton>
          <BaseButton variant="primary" :loading="reviewing" @click="handleReview(true)">通过</BaseButton>
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
