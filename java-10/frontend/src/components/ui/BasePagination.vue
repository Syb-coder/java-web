<script setup lang="ts">
import { computed } from 'vue'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

/**
 * 分页组件
 * @prop total - 总条数
 * @prop page - v-model 当前页（从 1 开始）
 * @prop pageSize - 每页条数，默认 10
 * @emits update:page
 */
interface Props {
  total: number
  page?: number
  pageSize?: number
}

const props = withDefaults(defineProps<Props>(), {
  page: 1,
  pageSize: 10,
})

const emit = defineEmits<{
  (e: 'update:page', value: number): void
}>()

// 总页数：至少 1，避免 0 页时边界异常
const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))

// 计算可见页码：超过 5 页时首尾固定，中间用省略号收拢
const pages = computed<(number | string)[]>(() => {
  const total = totalPages.value
  const cur = props.page
  if (total <= 5) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  if (cur <= 3) {
    return [1, 2, 3, 4, 5, '...', total]
  }
  if (cur >= total - 2) {
    return [1, '...', total - 4, total - 3, total - 2, total - 1, total]
  }
  return [1, '...', cur - 1, cur, cur + 1, '...', total]
})

// 跳转：忽略省略号与越界、重复页码
const goTo = (p: number | string) => {
  if (typeof p !== 'number') return
  if (p < 1 || p > totalPages.value || p === props.page) return
  emit('update:page', p)
}
</script>

<template>
  <div class="flex items-center gap-1 text-sm text-ink-600">
    <span class="mr-2">共 {{ total }} 条</span>
    <!-- 上一页：首页时禁用 -->
    <button
      class="rounded px-2 py-1 transition-colors hover:bg-ink-100 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent"
      :disabled="page <= 1"
      @click="goTo(page - 1)"
    >
      <ChevronLeft class="h-4 w-4" />
    </button>
    <template v-for="(p, i) in pages" :key="i">
      <span v-if="p === '...'" class="px-2 text-ink-400">...</span>
      <button
        v-else
        :class="[
          'min-w-[32px] rounded px-2 py-1 transition-colors',
          p === page ? 'bg-primary-600 text-white' : 'hover:bg-ink-100',
        ]"
        @click="goTo(p)"
      >
        {{ p }}
      </button>
    </template>
    <!-- 下一页：末页时禁用 -->
    <button
      class="rounded px-2 py-1 transition-colors hover:bg-ink-100 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent"
      :disabled="page >= totalPages"
      @click="goTo(page + 1)"
    >
      <ChevronRight class="h-4 w-4" />
    </button>
  </div>
</template>
