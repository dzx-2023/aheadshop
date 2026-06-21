<template>
  <div class="chat-input-wrapper">
    <div class="chat-input">
      <textarea
        ref="textareaRef"
        v-model="text"
        :placeholder="disabled ? 'AI 正在回复中...' : '输入消息，Enter 发送...'"
        :disabled="disabled"
        rows="1"
        @input="autoResize"
        @keydown="handleKeydown"
      />
      <button
        class="send-btn"
        :disabled="!text.trim() || disabled"
        @click="emitSend"
      >
        <svg v-if="!disabled" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M22 2L11 13" />
          <path d="M22 2L15 22L11 13L2 9L22 2Z" />
        </svg>
        <svg v-else class="spinner" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" stroke-dasharray="60" stroke-dashoffset="20" />
        </svg>
      </button>
    </div>
    <p class="hint">Enter 发送 · Shift + Enter 换行</p>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'

defineProps<{
  disabled?: boolean
}>()

const emit = defineEmits<{
  send: [content: string]
}>()

const text = ref('')
const textareaRef = ref<HTMLTextAreaElement>()

function autoResize() {
  const el = textareaRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 120) + 'px'
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    emitSend()
  }
}

function emitSend() {
  const content = text.value.trim()
  if (!content) return
  emit('send', content)
  text.value = ''
  nextTick(() => {
    if (textareaRef.value) {
      textareaRef.value.style.height = 'auto'
    }
  })
}
</script>

<style scoped>
.chat-input-wrapper {
  padding: 16px 24px 12px;
  background: #fff;
  border-top: 1px solid var(--color-border);
}

.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  padding: 8px 8px 8px 16px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.chat-input:focus-within {
  border-color: var(--color-amber);
  box-shadow: 0 0 0 3px rgba(212, 165, 116, 0.1);
}

textarea {
  flex: 1;
  border: none;
  background: transparent;
  resize: none;
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-charcoal);
  font-family: var(--font-body);
  padding: 4px 0;
  max-height: 120px;
}

textarea::placeholder {
  color: var(--color-text-muted);
}

textarea:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: var(--color-charcoal);
  color: var(--color-cream);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: background 0.2s, transform 0.15s, opacity 0.2s;
}

.send-btn:hover:not(:disabled) {
  background: var(--color-charcoal-light);
  transform: scale(1.05);
}

.send-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.send-btn svg {
  width: 18px;
  height: 18px;
}

.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.hint {
  font-size: 11px;
  color: var(--color-text-muted);
  text-align: center;
  margin-top: 8px;
  letter-spacing: 0.3px;
}

@media (max-width: 768px) {
  .chat-input-wrapper {
    padding: 12px 16px calc(8px + env(safe-area-inset-bottom, 0px));
  }
}
</style>
