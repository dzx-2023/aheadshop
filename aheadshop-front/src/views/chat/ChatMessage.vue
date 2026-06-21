<template>
  <div class="chat-msg" :class="[msg.role, { streaming: isStreaming }]">
    <!-- AI 头像 -->
    <div v-if="msg.role === 'assistant'" class="avatar assistant-avatar">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z" />
        <path d="M8 14s1.5 2 4 2 4-2 4-2" />
        <circle cx="9" cy="10" r="0.5" fill="currentColor" />
        <circle cx="15" cy="10" r="0.5" fill="currentColor" />
      </svg>
    </div>

    <!-- 消息气泡 -->
    <div class="bubble-wrapper" :class="msg.role">
      <div class="bubble" :class="msg.role">
        <div v-if="msg.role === 'assistant'" class="content" v-html="renderedContent"></div>
        <div v-else class="content plain">{{ msg.content }}</div>
        <div v-if="isStreaming" class="typing-cursor"></div>
      </div>
      <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
    </div>

    <!-- 用户头像 -->
    <div v-if="msg.role === 'user'" class="avatar user-avatar">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
        <circle cx="12" cy="7" r="4" />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'
import type { ChatMessage } from '@/types/chat'

const props = defineProps<{
  msg: ChatMessage
  isStreaming?: boolean
}>()

const md = new MarkdownIt({
  html: false,
  breaks: true,
  linkify: true,
})

const renderedContent = computed(() => {
  if (!props.msg.content) return ''
  return md.render(props.msg.content)
})

function formatTime(time: string): string {
  if (!time) return ''
  const date = new Date(time)
  const h = date.getHours().toString().padStart(2, '0')
  const m = date.getMinutes().toString().padStart(2, '0')
  return `${h}:${m}`
}
</script>

<style scoped>
.chat-msg {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 20px;
  animation: msgIn 0.3s ease-out;
}

.chat-msg.user {
  flex-direction: row;
  justify-content: flex-end;
}

@keyframes msgIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ── 头像 ── */
.avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar svg {
  width: 18px;
  height: 18px;
}

.assistant-avatar {
  background: var(--color-surface);
  color: var(--color-amber);
  border: 1px solid var(--color-border);
}

.user-avatar {
  background: var(--color-charcoal);
  color: var(--color-cream);
}

/* ── 气泡 ── */
.bubble-wrapper {
  display: flex;
  flex-direction: column;
}

.bubble-wrapper.user {
  align-items: flex-end;
}

.bubble-wrapper.assistant {
  align-items: flex-start;
}

.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 16px;
  line-height: 1.7;
  font-size: 14px;
  position: relative;
  word-break: break-word;
}

.bubble.assistant {
  background: #fff;
  color: var(--color-charcoal);
  border: 1px solid var(--color-border);
  border-top-left-radius: 4px;
}

.bubble.user {
  background: var(--color-charcoal);
  color: var(--color-cream);
  border-top-right-radius: 4px;
}

.msg-time {
  font-size: 11px;
  color: var(--color-text-muted);
  margin-top: 4px;
  padding: 0 4px;
}

/* ── Markdown 内容 ── */
.content :deep(p) {
  margin: 0 0 8px;
}

.content :deep(p:last-child) {
  margin-bottom: 0;
}

.content :deep(code) {
  background: var(--color-surface);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 13px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
}

.content :deep(pre) {
  background: var(--color-charcoal);
  color: var(--color-cream);
  padding: 12px 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
}

.content :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
  font-size: 13px;
}

.content :deep(ul),
.content :deep(ol) {
  padding-left: 20px;
  margin: 4px 0;
}

.content :deep(li) {
  margin: 2px 0;
}

.content :deep(strong) {
  font-weight: 600;
}

.content :deep(a) {
  color: var(--color-amber);
  text-decoration: underline;
}

.content.plain {
  white-space: pre-wrap;
}

/* ── 打字光标 ── */
.typing-cursor {
  display: inline-block;
  width: 2px;
  height: 16px;
  background: var(--color-amber);
  margin-left: 2px;
  vertical-align: text-bottom;
  animation: blink 0.8s step-end infinite;
}

@keyframes blink {
  50% {
    opacity: 0;
  }
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .bubble {
    max-width: 85%;
  }
}
</style>
