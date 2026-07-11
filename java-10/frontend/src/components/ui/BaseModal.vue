<script setup lang="ts">
import { watch, onBeforeUnmount } from 'vue'
import { X } from 'lucide-vue-next'
import { cn } from '@/lib/utils'

/**
 * 弹窗组件，用于新增/编辑表单与确认框
 * @prop modelValue - v-model 控制显隐
 * @prop title - 标题
 * @prop width - 弹窗宽度，默认 500px
 * @slot default - 弹窗内容
 * @slot footer - 底部按钮区
 * @emits update:modelValue / close
 */
interface Props {
  modelValue?: boolean
  title?: string
  width?: string
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  title: '',
  width: '500px',
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'close'): void
}>()

const close = () => {
  emit('update:modelValue', false)
  emit('close')
}

// ESC 关闭：仅弹窗打开时绑定监听，关闭后立即解绑，避免全局事件污染
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.modelValue) {
    close()
  }
}

watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      document.addEventListener('keydown', handleKeydown)
      // 锁定背景滚动，防止弹窗后页面滚动穿透
      document.body.style.overflow = 'hidden'
    } else {
      document.removeEventListener('keydown', handleKeydown)
      document.body.style.overflow = ''
    }
  },
)

// 组件卸载前清理事件与样式，防止内存泄漏与样式残留
onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
  document.body.style.overflow = ''
})

const panelClass = cn(
  'relative z-10 overflow-hidden rounded-lg bg-white shadow-xl',
)
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div
        v-if="modelValue"
        class="fixed inset-0 z-50 flex items-center justify-center p-4"
      >
        <!-- 遮罩层：点击关闭 -->
        <div class="modal-overlay absolute inset-0 bg-black/40" @click="close" />
        <!-- 弹窗主体 -->
        <div :class="panelClass" class="modal-panel" :style="{ width }">
          <!-- 标题栏 -->
          <div class="flex items-center justify-between border-b border-ink-200 px-5 py-4">
            <h3 class="text-base font-semibold text-ink-800">{{ title }}</h3>
            <button
              class="rounded p-1 text-ink-400 transition-colors hover:bg-ink-100 hover:text-ink-600"
              @click="close"
            >
              <X class="h-5 w-5" />
            </button>
          </div>
          <!-- 内容区：限制高度并溢出滚动 -->
          <div class="max-h-[70vh] overflow-y-auto px-5 py-4">
            <slot />
          </div>
          <!-- 底部按钮区：仅在外部传入 footer 时渲染 -->
          <div v-if="$slots.footer" class="border-t border-ink-200 px-5 py-3">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* 进入/离开过渡：遮罩淡入 + 弹窗轻微缩放 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.2s ease;
}
.modal-enter-active .modal-panel,
.modal-leave-active .modal-panel {
  transition: transform 0.2s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from .modal-panel,
.modal-leave-to .modal-panel {
  transform: scale(0.95);
}
</style>
