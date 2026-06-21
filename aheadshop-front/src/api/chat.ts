import request from './request'
import type { ChatSession, ChatMessage } from '@/types/chat'

/** 创建会话 */
export function createSession() {
  return request.post<null, { code: number; data: number }>('/chat/session')
}

/** 获取会话列表 */
export function getSessions() {
  return request.get<null, { code: number; data: ChatSession[] }>('/chat/sessions')
}

/** 获取会话消息 */
export function getMessages(sessionId: number) {
  return request.get<null, { code: number; data: ChatMessage[] }>(`/chat/session/${sessionId}/messages`)
}

/** 删除会话 */
export function deleteSession(sessionId: number) {
  return request.delete<null, { code: number; data: null }>(`/chat/session/${sessionId}`)
}

/** 发送消息（HTTP 回退） */
export function sendMessage(sessionId: number, content: string) {
  return request.post<null, { code: number; data: { reply: string; tokenCount: number } }>('/chat/send', {
    sessionId,
    content,
  })
}
