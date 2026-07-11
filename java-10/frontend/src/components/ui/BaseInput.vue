<script setup lang="ts">
import { computed } from 'vue'
import type { Component } from 'vue'
import {
  User, Lock, Phone, Key, Mail, Eye, EyeOff, Calendar, Search,
} from 'lucide-vue-next'
import { cn } from '@/lib/utils'

/**
 * 文本输入框，支持前缀图标
 * @prop modelValue - v-model 绑定值
 * @prop type - 输入类型：text/password/number/date
 * @prop icon - lucide 图标组件名（字符串），如 'User'、'Lock'
 * @prop disabled - 是否禁用
 * @prop error - 错误提示文案，有值时输入框显示红色边框
 */
interface Props {
  modelValue?: string
  type?: 'text' | 'password' | 'number' | 'date'
  placeholder?: string
  icon?: string
  disabled?: boolean
  error?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  type: 'text',
  placeholder: '',
  disabled: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

// 前缀图标映射：将字符串名解析为已导入的 lucide 图标组件，避免外部直接传组件
const iconMap: Record<string, Component> = {
  User, Lock, Phone, Key, Mail, Eye, EyeOff, Calendar, Search,
}

// 解析当前应渲染的图标组件，未匹配时返回 undefined
const resolvedIcon = computed<Component | undefined>(() =>
  props.icon ? iconMap[props.icon] : undefined,
)

const wrapperClass = computed(() =>
  cn(
    'flex items-center rounded-md border bg-white transition-colors focus-within:ring-2 focus-within:ring-primary-500/30',
    props.error ? 'border-red-500' : 'border-ink-200',
    props.disabled && 'cursor-not-allowed bg-ink-50 opacity-50',
  ),
)

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>

<template>
  <div>
    <div :class="wrapperClass">
      <component
        v-if="resolvedIcon"
        :is="resolvedIcon"
        class="ml-3 h-4 w-4 shrink-0 text-ink-400"
      />
      <input
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        class="w-full bg-transparent px-3 py-2 text-sm text-ink-800 placeholder-ink-400 focus:outline-none"
        @input="handleInput"
      />
    </div>
    <p v-if="error" class="mt-1 text-xs text-red-500">{{ error }}</p>
  </div>
</template>
