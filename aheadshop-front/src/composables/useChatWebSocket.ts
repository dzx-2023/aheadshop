import { ref, onUnmounted } from 'vue'
import type { WsMessage } from '@/types/chat'

export interface UseChatWebSocketOptions {
  onChunk: (content: string) => void
  onDone: (tokenCount: number) => void
  onError: (message: string) => void
}

/**
 * WebSocket 聊天连接管理
 * - 自动重连（断线后 3 秒）
 * - 通过 Gateway 代理连接（ws://host:8080/ws/chat/{sessionId}）
 */
export function useChatWebSocket(options: UseChatWebSocketOptions) {
  let ws: WebSocket | null = null
  let sessionId: number | null = null
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null
  let manualClose = false

  const connected = ref(false)
  const reconnecting = ref(false)

  function getUrl(sessionId: number): string {
    const protocol = location.protocol === 'https:' ? 'wss:' : 'ws:'
    // 开发环境直连 aichat 8086（Gateway WS 代理存在协议兼容问题）
    // 生产环境走 Gateway 同源
    const isDev = import.meta.env.DEV
    const host = isDev ? `${location.hostname}:8086` : location.host
    const token = localStorage.getItem('token') || ''
    return `${protocol}//${host}/ws/chat/${sessionId}?token=${token}`
  }

  function connect(sid: number) {
    disconnect()
    manualClose = false
    sessionId = sid

    const url = getUrl(sid)
    const newWs = new WebSocket(url)
    ws = newWs

    newWs.onopen = () => {
      connected.value = true
      reconnecting.value = false
    }

    newWs.onmessage = (event) => {
      try {
        const msg: WsMessage = JSON.parse(event.data)
        if (msg.type === 'chunk') {
          options.onChunk(msg.content)
        } else if (msg.type === 'done') {
          options.onDone(msg.tokenCount)
        } else if (msg.type === 'error') {
          options.onError(msg.message)
        }
      } catch {
        // ignore parse errors
      }
    }

    newWs.onclose = () => {
      // 旧连接的 onclose 触发时 ws 已指向新连接，忽略
      if (ws !== newWs) return
      connected.value = false
      ws = null
      if (!manualClose && sessionId !== null) {
        scheduleReconnect()
      }
    }

    newWs.onerror = () => {
      connected.value = false
    }
  }

  function scheduleReconnect() {
    if (reconnectTimer) return
    reconnecting.value = true
    reconnectTimer = setTimeout(() => {
      reconnectTimer = null
      if (sessionId !== null && !manualClose) {
        connect(sessionId)
      }
    }, 3000)
  }

  function send(content: string) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ content }))
      return true
    }
    return false
  }

  function disconnect() {
    manualClose = true
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }
    if (ws) {
      ws.close()
      ws = null
    }
    connected.value = false
    reconnecting.value = false
  }

  function reconnect() {
    // 已连接则不重复操作
    if (ws && ws.readyState === WebSocket.OPEN) return
    if (sessionId !== null) {
      connect(sessionId)
    }
  }

  onUnmounted(() => {
    disconnect()
  })

  return {
    connected,
    reconnecting,
    connect,
    send,
    disconnect,
    reconnect,
  }
}
