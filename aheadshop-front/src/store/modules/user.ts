import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request'

export interface UserInfo {
  userId: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  gender: number
  roles: string[]
  inviteCode: string
  referrerId: number | null
}

export interface LoginResult {
  userId: number
  username: string
  nickname: string
  accessToken: string
  refreshToken: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(null)

  /** 登录 */
  async function login(username: string, password: string) {
    const res: any = await request.post('/user/login', { username, password })
    const data: LoginResult = res.data
    token.value = data.accessToken
    refreshToken.value = data.refreshToken
    localStorage.setItem('token', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
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
    refreshToken.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  /** 是否已登录 */
  const isLoggedIn = () => !!token.value

  return { token, refreshToken, userInfo, login, fetchUserInfo, logout, isLoggedIn }
})
