<script setup lang="ts">
import { computed } from 'vue'
import { Loader2 } from 'lucide-vue-next'
import { cn } from '@/lib/utils'

/**
 * 通用按钮组件
 * @prop type - 原生 button 类型，默认 button
 * @prop variant - 视觉变体：primary/secondary/danger/ghost
 * @prop size - 尺寸：sm/md/lg
 * @prop disabled - 是否禁用
 * @prop loading - 是否加载中（加载时显示旋转图标并禁用交互）
 */
interface Props {
  type?: 'button' | 'submit'
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'button',
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
})

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()

// 各变体对应的视觉样式，集中维护便于统一调整
const variantClass: Record<NonNullable<Props['variant']>, string> = {
  primary: 'bg-primary-600 text-white hover:bg-primary-700',
  secondary: 'bg-white border border-ink-200 text-ink-700 hover:bg-ink-50',
  danger: 'bg-red-600 text-white hover:bg-red-700',
  ghost: 'text-ink-600 hover:bg-ink-100',
}

// 各尺寸对应的间距与字号
const sizeClass: Record<NonNullable<Props['size']>, string> = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2',
  lg: 'px-5 py-2.5 text-base',
}

const classes = computed(() =>
  cn(
    'inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500/30 disabled:opacity-50 disabled:cursor-not-allowed',
    variantClass[props.variant],
    sizeClass[props.size],
  ),
)

// 点击处理：loading/disabled 状态下不触发回调，避免重复提交
const handleClick = (event: MouseEvent) => {
  if (props.disabled || props.loading) return
  emit('click', event)
}
</script>

<template>
  <button
    :type="type"
    :class="classes"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <Loader2 v-if="loading" class="mr-1.5 h-4 w-4 animate-spin" />
    <slot />
  </button>
</template>
