import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  role: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  /** 登录 */
  async function login(username: string, password: string) {
    const res: any = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await fetchUserInfo()
    return res
  }

  /** 获取用户信息 */
  async function fetchUserInfo() {
    const res: any = await request.get('/user/info')
    userInfo.value = res.data
    return res.data
  }

  /** 登出 */
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  /** 是否已登录 */
  const isLoggedIn = () => !!token.value

  return { token, userInfo, login, fetchUserInfo, logout, isLoggedIn }
})
