<template>
  <div class="session-list" :class="{ open: isOpen }">
    <!-- 头部 -->
    <div class="session-header">
      <h3>智能客服</h3>
      <button class="new-btn" @click="$emit('create')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19" />
          <line x1="5" y1="12" x2="19" y2="12" />
        </svg>
      </button>
    </div>

    <!-- 会话列表 -->
    <div class="session-items">
      <div
        v-for="session in sessions"
        :key="session.id"
        class="session-item"
        :class="{ active: session.id === activeId }"
        @click="$emit('select', session.id)"
        @contextmenu.prevent="showContextMenu($event, session.id)"
      >
        <div class="session-info">
          <span class="session-title">{{ session.title || '新对话' }}</span>
          <span class="session-time">{{ formatTime(session.updateTime) }}</span>
        </div>
        <p class="session-preview">{{ session.lastMessage || '暂无消息' }}</p>
        <button
          class="delete-btn"
          @click.stop="$emit('delete', session.id)"
          title="删除会话"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <polyline points="3 6 5 6 21 6" />
            <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
          </svg>
        </button>
      </div>

      <div v-if="!sessions.length" class="empty-tip">
        <p>暂无会话</p>
        <p class="sub">点击上方 + 开始新对话</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ChatSession } from '@/types/chat'

defineProps<{
  sessions: ChatSession[]
  activeId: number | null
  isOpen: boolean
}>()

defineEmits<{
  create: []
  select: [id: number]
  delete: [id: number]
}>()

function formatTime(time: string): string {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

function showContextMenu(_e: MouseEvent, _id: number) {
  // 浏览器原生右键菜单已通过 @contextmenu.prevent 阻止
  // 删除通过气泡按钮操作
}
</script>

<style scoped>
.session-list {
  width: 280px;
  height: 100%;
  background: #fff;
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

/* ── 头部 ── */
.session-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 16px 16px;
  border-bottom: 1px solid var(--color-border);
}

.session-header h3 {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 700;
  color: var(--color-charcoal);
  letter-spacing: 0.5px;
}

.new-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  color: var(--color-charcoal);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.new-btn:hover {
  background: var(--color-charcoal);
  color: var(--color-cream);
  border-color: var(--color-charcoal);
}

.new-btn svg {
  width: 16px;
  height: 16px;
}

/* ── 会话列表 ── */
.session-items {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.15s;
  position: relative;
  margin-bottom: 2px;
}

.session-item:hover {
  background: var(--color-surface);
}

.session-item.active {
  background: var(--color-surface);
  border: 1px solid var(--color-border);
}

.session-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.session-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-charcoal);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.session-time {
  font-size: 11px;
  color: var(--color-text-muted);
  flex-shrink: 0;
  margin-left: 8px;
}

.session-preview {
  font-size: 12px;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.15s;
}

.session-item:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: #fee2e2;
  color: #dc2626;
}

.delete-btn svg {
  width: 14px;
  height: 14px;
}

/* ── 空状态 ── */
.empty-tip {
  text-align: center;
  padding: 40px 16px;
  color: var(--color-text-muted);
}

.empty-tip p {
  font-size: 13px;
}

.empty-tip .sub {
  font-size: 12px;
  margin-top: 4px;
  opacity: 0.7;
}

/* ── 移动端抽屉 ── */
@media (max-width: 768px) {
  .session-list {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 200;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
    box-shadow: none;
  }

  .session-list.open {
    transform: translateX(0);
    box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
  }
}
</style>
