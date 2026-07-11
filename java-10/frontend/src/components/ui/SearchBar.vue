<script setup lang="ts">
import { Search } from 'lucide-vue-next'
import { cn } from '@/lib/utils'

/**
 * 搜索栏：左侧搜索图标按钮 + 右侧输入框
 * @prop modelValue - v-model 绑定值
 * @prop placeholder - 占位提示
 * @emits update:modelValue / search
 * 回车或点击图标按钮均触发 search 事件
 */
interface Props {
  modelValue?: string
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: '请输入关键字搜索',
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'search'): void
}>()

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

// 触发搜索：回车与点击搜索按钮共用
const handleSearch = () => {
  emit('search')
}

const wrapperClass = cn(
  'flex items-center rounded-md border border-ink-200 bg-white px-3 py-2 transition-colors focus-within:ring-2 focus-within:ring-primary-500/30',
)
</script>

<template>
  <div :class="wrapperClass">
    <!-- 搜索图标作为可点击按钮，便于鼠标用户触发查询 -->
    <button
      type="button"
      class="mr-2 flex shrink-0 items-center text-ink-400 transition-colors hover:text-primary-600"
      @click="handleSearch"
    >
      <Search class="h-4 w-4" />
    </button>
    <input
      :value="modelValue"
      :placeholder="placeholder"
      class="w-full bg-transparent text-sm text-ink-800 placeholder-ink-400 focus:outline-none"
      @input="handleInput"
      @keyup.enter="handleSearch"
    />
  </div>
</template>
