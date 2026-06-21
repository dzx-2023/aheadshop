<template>
  <div class="chat-window">
    <!-- 移动端遮罩 -->
    <div
      v-if="drawerOpen"
      class="drawer-overlay"
      @click="drawerOpen = false"
    />

    <!-- 左侧会话列表 -->
    <ChatSessionList
      :sessions="sessions"
      :active-id="activeSessionId"
      :is-open="drawerOpen"
      @create="handleCreate"
      @select="handleSelect"
      @delete="handleDelete"
    />

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 顶栏 -->
      <div class="chat-topbar">
        <button class="back-btn" @click="router.push('/')" title="返回商城">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5" />
            <path d="M12 19l-7-7 7-7" />
          </svg>
        </button>
        <button class="menu-btn" @click="drawerOpen = !drawerOpen">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="3" y1="6" x2="21" y2="6" />
            <line x1="3" y1="12" x2="21" y2="12" />
            <line x1="3" y1="18" x2="21" y2="18" />
          </svg>
        </button>
        <span class="topbar-title">{{ currentTitle }}</span>
        <div class="topbar-status" :class="{ connected: wsConnected, reconnecting: wsReconnecting }">
          <span class="dot" />
          {{ wsReconnecting ? '重连中...' : wsConnected ? '在线' : '离线' }}
        </div>
      </div>

      <!-- 断线提示 -->
      <div v-if="!wsConnected && !wsReconnecting && activeSessionId" class="offline-banner">
        <span>网络连接已断开</span>
        <button class="reconnect-btn" @click="wsReconnect">重新连接</button>
      </div>

      <!-- 消息区域 -->
      <div ref="messagesRef" class="chat-messages">
        <!-- 欢迎语 -->
        <div v-if="!messages.length && !loadingMessages" class="welcome">
          <div class="welcome-icon">
            <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="24" cy="24" r="20" />
              <path d="M16 32s2 4 8 4 8-4 8-4" />
              <circle cx="18" cy="20" r="1.5" fill="currentColor" stroke="none" />
              <circle cx="30" cy="20" r="1.5" fill="currentColor" stroke="none" />
            </svg>
          </div>
          <h2>欢迎使用智能客服</h2>
          <p>我是 AheadShop 的 AI 助手，可以帮您解答商品、订单、售后等问题</p>
          <div class="quick-actions">
            <button @click="quickSend('有什么热销商品推荐吗？')">🔥 热销推荐</button>
            <button @click="quickSend('我想查询一下我的订单')">📦 查询订单</button>
            <button @click="quickSend('退换货政策是什么？')">🔄 退换货政策</button>
          </div>
        </div>

        <!-- 消息列表 -->
        <ChatMessage
          v-for="msg in messages"
          :key="msg.id"
          :msg="msg"
          :is-streaming="streamingMsgId === msg.id"
        />

        <!-- 加载中 -->
        <div v-if="loadingMessages" class="loading-tip">加载中...</div>
      </div>

      <!-- 输入框 -->
      <ChatInput
        :disabled="isSending"
        @send="handleSend"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ChatSessionList from './ChatSessionList.vue'
import ChatMessage from './ChatMessage.vue'
import ChatInput from './ChatInput.vue'
import { useChatWebSocket } from '@/composables/useChatWebSocket'
import { createSession, getSessions, getMessages, deleteSession, sendMessage } from '@/api/chat'
import type { ChatSession, ChatMessage as ChatMsg } from '@/types/chat'

const router = useRouter()

// ── 状态 ──
const sessions = ref<ChatSession[]>([])
const messages = ref<ChatMsg[]>([])
const activeSessionId = ref<number | null>(null)
const loadingMessages = ref(false)
const isSending = ref(false)
const streamingMsgId = ref<number | null>(null)
const drawerOpen = ref(false)
const messagesRef = ref<HTMLElement>()
let streamBuffer = ''

// ── WebSocket ──
const { connected: wsConnected, reconnecting: wsReconnecting, connect: wsConnect, send: wsSend, reconnect: wsReconnect } = useChatWebSocket({
  onChunk(content) {
    streamBuffer += content
    const last = messages.value[messages.value.length - 1]
    if (last && last.role === 'assistant') {
      last.content = streamBuffer
    }
    scrollToBottom()
  },
  onDone(tokenCount) {
    streamingMsgId.value = null
    isSending.value = false
    const last = messages.value[messages.value.length - 1]
    if (last && last.role === 'assistant') {
      last.tokenCount = tokenCount
    }
    streamBuffer = ''
    loadSessions()
  },
  onError(message) {
    ElMessage.error(message)
    streamingMsgId.value = null
    isSending.value = false
    streamBuffer = ''
  },
})

// ── 计算属性 ──
const currentTitle = computed(() => {
  if (!activeSessionId.value) return '智能客服'
  const s = sessions.value.find(s => s.id === activeSessionId.value)
  return s?.title || '新对话'
})

// ── 初始化 ──
onMounted(async () => {
  await loadSessions()
  if (sessions.value.length) {
    handleSelect(sessions.value[0].id)
  }
})

// ── 会话操作 ──
async function loadSessions() {
  try {
    const res = await getSessions()
    sessions.value = res.data || []
  } catch {
    // ignore
  }
}

async function handleCreate() {
  try {
    const res = await createSession()
    const newId = res.data
    await loadSessions()
    handleSelect(newId)
    drawerOpen.value = false
  } catch {
    ElMessage.error('创建会话失败')
  }
}

async function handleSelect(id: number) {
  if (activeSessionId.value === id) return
  activeSessionId.value = id
  drawerOpen.value = false
  messages.value = []
  loadingMessages.value = true

  try {
    const res = await getMessages(id)
    messages.value = res.data || []
    scrollToBottom()
    wsConnect(id)
  } catch {
    ElMessage.error('加载消息失败')
  } finally {
    loadingMessages.value = false
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除这个会话吗？', '删除会话', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteSession(id)
    sessions.value = sessions.value.filter(s => s.id !== id)
    if (activeSessionId.value === id) {
      activeSessionId.value = null
      messages.value = []
      if (sessions.value.length) {
        handleSelect(sessions.value[0].id)
      }
    }
    ElMessage.success('已删除')
  } catch {
    // cancelled
  }
}

// ── 发送消息 ──
async function handleSend(content: string) {
  if (!activeSessionId.value || isSending.value) return

  isSending.value = true
  streamBuffer = ''

  // 添加用户消息到列表
  const userMsg: ChatMsg = {
    id: Date.now(),
    sessionId: activeSessionId.value,
    role: 'user',
    content,
    tokenCount: 0,
    createTime: new Date().toISOString(),
  }
  messages.value.push(userMsg)
  scrollToBottom()

  // 添加空的 assistant 消息（流式填充）
  const assistantMsg: ChatMsg = {
    id: Date.now() + 1,
    sessionId: activeSessionId.value,
    role: 'assistant',
    content: '',
    tokenCount: 0,
    createTime: new Date().toISOString(),
  }
  messages.value.push(assistantMsg)
  streamingMsgId.value = assistantMsg.id
  scrollToBottom()

  // 通过 WebSocket 发送
  if (wsSend(content)) {
    // WebSocket 已发送，等待流式回调
  } else {
    // WebSocket 未连接，回退到 HTTP
    try {
      const res = await sendMessage(activeSessionId.value, content)
      assistantMsg.content = res.data.reply
      assistantMsg.tokenCount = res.data.tokenCount || 0
    } catch {
      ElMessage.error('发送失败')
      messages.value.pop()
    } finally {
      streamingMsgId.value = null
      isSending.value = false
      scrollToBottom()
      loadSessions()
    }
  }
}

function quickSend(content: string) {
  if (!activeSessionId.value) {
    handleCreate().then(() => {
      nextTick(() => handleSend(content))
    })
    return
  }
  handleSend(content)
}

// ── 工具 ──
function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.chat-window {
  display: flex;
  height: 100%;
  background: var(--color-cream);
  overflow: hidden;
}

/* ── 遮罩 ── */
.drawer-overlay {
  display: none;
}

/* ── 右侧聊天区 ── */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* ── 顶栏 ── */
.chat-topbar {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid var(--color-border);
  gap: 12px;
  flex-shrink: 0;
}

.back-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  color: var(--color-charcoal);
  cursor: pointer;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.back-btn:hover {
  background: var(--color-surface);
}

.back-btn svg {
  width: 20px;
  height: 20px;
}

.menu-btn {
  display: none;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  color: var(--color-charcoal);
  cursor: pointer;
  border-radius: 8px;
  align-items: center;
  justify-content: center;
}

.menu-btn:hover {
  background: var(--color-surface);
}

.menu-btn svg {
  width: 20px;
  height: 20px;
}

.topbar-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-charcoal);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.topbar-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.topbar-status .dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #d1d5db;
  transition: background 0.3s;
}

.topbar-status.connected .dot {
  background: #22c55e;
  box-shadow: 0 0 6px rgba(34, 197, 94, 0.4);
}

.topbar-status.reconnecting .dot {
  background: #f59e0b;
  animation: pulse 1s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

/* ── 断线提示 ── */
.offline-banner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 8px 16px;
  background: #fef3c7;
  border-bottom: 1px solid #fde68a;
  font-size: 13px;
  color: #92400e;
  flex-shrink: 0;
}

.reconnect-btn {
  padding: 4px 12px;
  border: 1px solid #92400e;
  background: transparent;
  color: #92400e;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.reconnect-btn:hover {
  background: #92400e;
  color: #fff;
}

/* ── 消息区域 ── */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scroll-behavior: smooth;
}

/* ── 欢迎语 ── */
.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 40px 20px;
  animation: fadeIn 0.6s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.welcome-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  color: var(--color-amber);
}

.welcome-icon svg {
  width: 40px;
  height: 40px;
}

.welcome h2 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  color: var(--color-charcoal);
  margin-bottom: 8px;
}

.welcome p {
  font-size: 14px;
  color: var(--color-text-muted);
  max-width: 360px;
  line-height: 1.6;
  margin-bottom: 28px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.quick-actions button {
  padding: 10px 18px;
  border: 1px solid var(--color-border);
  background: #fff;
  border-radius: 20px;
  font-size: 13px;
  color: var(--color-charcoal);
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.quick-actions button:hover {
  background: var(--color-charcoal);
  color: var(--color-cream);
  border-color: var(--color-charcoal);
  transform: translateY(-1px);
}

.loading-tip {
  text-align: center;
  color: var(--color-text-muted);
  font-size: 13px;
  padding: 20px;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .menu-btn {
    display: flex;
  }

  .drawer-overlay {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 199;
  }

  .chat-messages {
    padding: 16px;
  }
}
</style>
