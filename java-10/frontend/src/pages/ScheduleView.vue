<script setup lang="ts">
import { ref, computed } from 'vue'
import { Calendar, Clock, MapPin, BookOpen } from 'lucide-vue-next'

// 课表单元格数据结构：描述一门课的展示信息
interface ScheduleCell {
  courseName: string
  teacher: string
  location: string
}

// 节次定义：第1-5节，含时间区间用于表头展示
const periods = [
  { index: 1, time: '08:00 - 08:45' },
  { index: 2, time: '08:55 - 09:40' },
  { index: 3, time: '10:00 - 10:45' },
  { index: 4, time: '10:55 - 11:40' },
  { index: 5, time: '14:00 - 14:45' },
]

// 星期表头：周一到周五
const weekdays = ['周一', '周二', '周三', '周四', '周五']

// 星期对应的 key 前缀，用于 scheduleData 查找
const weekdayKeys = ['mon', 'tue', 'wed', 'thu', 'fri']

// 模拟课表数据：key 为 "星期key-节次"，空课用 null 表示
// 仅作展示用途，无后端接口，后续接入真实数据时替换即可
const scheduleData = ref<Record<string, ScheduleCell | null>>({
  'mon-1': { courseName: '高等数学', teacher: '刘老师', location: 'A101' },
  'mon-2': { courseName: '数据结构', teacher: '黄老师', location: 'B203' },
  'mon-3': null,
  'mon-4': { courseName: '英语', teacher: '王老师', location: 'C305' },
  'mon-5': null,
  'tue-1': { courseName: '操作系统', teacher: '张老师', location: 'B201' },
  'tue-2': null,
  'tue-3': { courseName: '线性代数', teacher: '刘老师', location: 'A102' },
  'tue-4': { courseName: '计算机网络', teacher: '李老师', location: 'D401' },
  'tue-5': { courseName: '体育', teacher: '赵老师', location: '操场' },
  'wed-1': null,
  'wed-2': { courseName: '高等数学', teacher: '刘老师', location: 'A101' },
  'wed-3': { courseName: '数据结构', teacher: '黄老师', location: 'B203' },
  'wed-4': null,
  'wed-5': { courseName: '软件工程', teacher: '陈老师', location: 'C201' },
  'thu-1': { courseName: '英语', teacher: '王老师', location: 'C305' },
  'thu-2': { courseName: '操作系统', teacher: '张老师', location: 'B201' },
  'thu-3': null,
  'thu-4': { courseName: '线性代数', teacher: '刘老师', location: 'A102' },
  'thu-5': null,
  'fri-1': { courseName: '计算机网络', teacher: '李老师', location: 'D401' },
  'fri-2': null,
  'fri-3': { courseName: '软件工程', teacher: '陈老师', location: 'C201' },
  'fri-4': { courseName: '体育', teacher: '赵老师', location: '操场' },
  'fri-5': null,
})

// 课程 -> 浅色背景映射：按课程名区分颜色，便于视觉识别
// 颜色选取遵循 Tailwind 默认色板的 50 色阶，保证可读性
const courseColorMap: Record<string, string> = {
  '高等数学': 'bg-blue-50',
  '数据结构': 'bg-emerald-50',
  '操作系统': 'bg-amber-50',
  '英语': 'bg-purple-50',
  '线性代数': 'bg-sky-50',
  '计算机网络': 'bg-rose-50',
  '软件工程': 'bg-indigo-50',
  '体育': 'bg-teal-50',
}

// 默认课程背景：未在映射中的课程使用统一浅灰
const DEFAULT_COURSE_BG = 'bg-ink-50'

// 当前日期：展示用，格式化为中文习惯
const today = computed(() => {
  const d = new Date()
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  })
})

// 今日对应的星期索引：周一=0 ... 周五=4，周末返回 -1 不高亮
const todayWeekdayIndex = computed(() => {
  const day = new Date().getDay()
  return day >= 1 && day <= 5 ? day - 1 : -1
})

// 获取单元格数据：key 拼接规则与 scheduleData 一致，未命中时回落 null
function getCell(dayKey: string, period: number): ScheduleCell | null {
  return scheduleData.value[`${dayKey}-${period}`] || null
}

// 获取课程背景色：未配置时回落到默认浅灰
function getCourseBg(cell: ScheduleCell | null): string {
  if (!cell) return ''
  return courseColorMap[cell.courseName] || DEFAULT_COURSE_BG
}
</script>

<template>
  <div class="space-y-4">
    <!-- 顶部标题区域：学年学期 + 周次 + 当前日期 -->
    <div class="rounded-lg bg-white p-5 shadow-card">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-xl font-semibold text-ink-800">2025-2026学年第二学期 · 第3周</h1>
          <p class="mt-1 flex items-center text-sm text-ink-500">
            <Calendar class="mr-1.5 h-4 w-4" />
            {{ today }}
          </p>
        </div>
        <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-primary-50">
          <BookOpen class="h-6 w-6 text-primary-600" />
        </div>
      </div>
    </div>

    <!-- 图例说明：帮助理解单元格颜色含义 -->
    <div class="flex flex-wrap items-center gap-x-4 gap-y-2 rounded-lg bg-white p-3 text-xs text-ink-600 shadow-card">
      <span class="font-medium text-ink-700">图例说明：</span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-blue-50"></span>高等数学
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-emerald-50"></span>数据结构
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-amber-50"></span>操作系统
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-purple-50"></span>英语
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-sky-50"></span>线性代数
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-rose-50"></span>计算机网络
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-indigo-50"></span>软件工程
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-teal-50"></span>体育
      </span>
      <span class="flex items-center gap-1">
        <span class="h-3 w-3 rounded bg-ink-100"></span>无课
      </span>
    </div>

    <!-- 课表主体：表格布局，行为节次，列为星期 -->
    <div class="overflow-hidden rounded-lg bg-white shadow-card">
      <table class="w-full border-collapse">
        <thead>
          <!-- 表头深色背景：节次列 + 周一到周五，今日列高亮 -->
          <tr class="bg-ink-800 text-white">
            <th class="w-32 px-3 py-3 text-center text-sm font-medium">节次 / 时间</th>
            <th
              v-for="(day, idx) in weekdays"
              :key="day"
              class="px-3 py-3 text-center text-sm font-medium transition-colors"
              :class="idx === todayWeekdayIndex ? 'bg-primary-600' : ''"
            >
              {{ day }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="period in periods"
            :key="period.index"
            class="border-t border-ink-100"
          >
            <!-- 节次列：显示节次序号与时间区间 -->
            <td class="bg-ink-50 px-3 py-3 text-center align-middle">
              <div class="text-sm font-medium text-ink-800">第{{ period.index }}节</div>
              <div class="mt-0.5 flex items-center justify-center text-xs text-ink-400">
                <Clock class="mr-0.5 h-3 w-3" />
                {{ period.time }}
              </div>
            </td>
            <!-- 课程单元格：有课显示课程信息，无课显示占位 -->
            <td
              v-for="(dayKey, dayIdx) in weekdayKeys"
              :key="dayKey"
              class="p-2 align-middle"
              :class="dayIdx === todayWeekdayIndex ? 'bg-primary-50/30' : ''"
            >
              <div
                v-if="getCell(dayKey, period.index)"
                class="rounded-md p-2 transition-shadow hover:shadow-cardhover"
                :class="getCourseBg(getCell(dayKey, period.index))"
              >
                <div class="text-sm font-semibold text-ink-800">
                  {{ getCell(dayKey, period.index)?.courseName }}
                </div>
                <div class="mt-1 text-xs text-ink-600">
                  {{ getCell(dayKey, period.index)?.teacher }}
                </div>
                <div class="mt-1 inline-flex items-center rounded bg-white/60 px-1.5 py-0.5 text-xs text-ink-500">
                  <MapPin class="mr-0.5 h-3 w-3" />
                  {{ getCell(dayKey, period.index)?.location }}
                </div>
              </div>
              <div v-else class="flex items-center justify-center py-4 text-ink-300">
                <span class="text-lg">-</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
