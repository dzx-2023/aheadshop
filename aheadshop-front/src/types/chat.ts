/** 聊天会话 */
export interface ChatSession {
  id: number
  title: string
  lastMessage: string
  updateTime: string
}

/** 聊天消息 */
export interface ChatMessage {
  id: number
  sessionId: number
  role: 'user' | 'assistant'
  content: string
  tokenCount: number
  createTime: string
}

/** WebSocket 消息类型 */
export interface WsChunkMessage {
  type: 'chunk'
  content: string
}

export interface WsDoneMessage {
  type: 'done'
  tokenCount: number
}

export interface WsErrorMessage {
  type: 'error'
  message: string
}

export type WsMessage = WsChunkMessage | WsDoneMessage | WsErrorMessage
