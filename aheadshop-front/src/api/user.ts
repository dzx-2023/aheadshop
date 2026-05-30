import request from './request'

/** 登录 */
export function login(data: { username: string; password: string }) {
  return request.post('/user/login', data)
}

/** 注册 */
export function register(data: { username: string; password: string; phone?: string; nickname?: string }) {
  return request.post('/user/register', data)
}

/** 获取当前用户信息 */
export function getUserInfo() {
  return request.get('/user/info')
}

/** 获取用户详情 */
export function getUserDetail(userId: number) {
  return request.get(`/user/${userId}`)
}
