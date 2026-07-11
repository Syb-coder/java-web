<script setup lang="ts">
import { computed } from 'vue'
import { ChevronDown } from 'lucide-vue-next'
import { cn } from '@/lib/utils'

/**
 * 下拉选择框
 * @prop modelValue - v-model 绑定值（string | number）
 * @prop options - 选项列表，每项含 label 与 value
 * @prop placeholder - 占位提示
 * @prop disabled - 是否禁用
 */
interface Option {
  label: string
  value: string | number
}

interface Props {
  modelValue: string | number
  options: Option[]
  placeholder?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '',
  disabled: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void
}>()

// 样式与 BaseInput 协调：相同圆角、边框、聚焦态
const wrapperClass = computed(() =>
  cn(
    'relative flex items-center rounded-md border border-ink-200 bg-white transition-colors focus-within:ring-2 focus-within:ring-primary-500/30',
    props.disabled && 'cursor-not-allowed bg-ink-50 opacity-50',
  ),
)

const handleChange = (event: Event) => {
  const target = event.target as HTMLSelectElement
  // select 原生 value 恒为 string，需按 value 字符串匹配回原始类型，保证 number 不丢精度
  const matched = props.options.find((opt) => String(opt.value) === target.value)
  emit('update:modelValue', matched ? matched.value : target.value)
}
</script>

<template>
  <div :class="wrapperClass">
    <select
      :value="modelValue"
      :disabled="disabled"
      class="w-full appearance-none bg-transparent px-3 py-2 pr-9 text-sm text-ink-800 focus:outline-none"
      @change="handleChange"
    >
      <option v-if="placeholder" value="" disabled>
        {{ placeholder }}
      </option>
      <option
        v-for="opt in options"
        :key="opt.value"
        :value="opt.value"
      >
        {{ opt.label }}
      </option>
    </select>
    <!-- 自定义下拉箭头，覆盖原生外观 -->
    <ChevronDown class="pointer-events-none absolute right-3 h-4 w-4 text-ink-400" />
  </div>
</template>
