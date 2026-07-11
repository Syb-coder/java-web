<script setup lang="ts">
import { computed } from 'vue'
import { Loader2 } from 'lucide-vue-next'

/**
 * 通用数据表格
 * @prop columns - 列配置，含 key/title/width/align
 * @prop data - 数据行数组
 * @prop loading - 加载状态
 * @prop emptyText - 空数据提示
 * @slot col-${key} - 自定义单元格渲染，作用域参数 { row, index }
 */
interface Column {
  key: string
  title: string
  width?: string
  align?: 'left' | 'center' | 'right'
}

interface Props {
  columns: Column[]
  data: Record<string, any>[]
  loading?: boolean
  emptyText?: string
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  emptyText: '暂无数据',
})

// 对齐方式映射，默认左对齐
const alignClass: Record<string, string> = {
  left: 'text-left',
  center: 'text-center',
  right: 'text-right',
}

// 仅在非加载且数据为空时展示空状态，避免与加载遮罩重叠
const showEmpty = computed(() => !props.loading && props.data.length === 0)
</script>

<template>
  <div class="relative overflow-hidden rounded-lg border border-ink-200">
    <table class="w-full border-collapse">
      <thead>
        <tr class="bg-ink-50">
          <th
            v-for="col in columns"
            :key="col.key"
            :style="col.width ? { width: col.width } : undefined"
            :class="[
              'px-4 py-3 text-xs font-medium uppercase tracking-wide text-ink-600',
              alignClass[col.align || 'left'],
            ]"
          >
            {{ col.title }}
          </th>
        </tr>
      </thead>
      <tbody>
        <!-- 空状态：跨整行展示提示 -->
        <tr v-if="showEmpty">
          <td :colspan="columns.length" class="px-4 py-10 text-center text-sm text-ink-400">
            {{ emptyText }}
          </td>
        </tr>
        <tr
          v-for="(row, index) in data"
          :key="index"
          class="border-t border-ink-200 transition-colors hover:bg-primary-50/40"
        >
          <td
            v-for="col in columns"
            :key="col.key"
            :class="[
              'px-4 py-3 text-sm text-ink-700',
              alignClass[col.align || 'left'],
            ]"
          >
            <!-- 自定义单元格优先；无插槽时回退到字段原值 -->
            <slot :name="`col-${col.key}`" :row="row" :index="index">
              {{ row[col.key] }}
            </slot>
          </td>
        </tr>
      </tbody>
    </table>
    <!-- 加载遮罩：覆盖表格区域 -->
    <div
      v-if="loading"
      class="absolute inset-0 flex items-center justify-center bg-white/60"
    >
      <Loader2 class="h-6 w-6 animate-spin text-primary-500" />
    </div>
  </div>
</template>
