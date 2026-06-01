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

/** 修改用户信息 */
export function updateUserInfo(data: { nickname?: string; avatar?: string; phone?: string; email?: string; gender?: number }) {
  return request.put('/user/info', data)
}

// ========== 收货地址 ==========

/** 收货地址列表 */
export function getAddressList() {
  return request.get('/user/address/list')
}

/** 新增收货地址 */
export function addAddress(data: {
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
}) {
  return request.post('/user/address', data)
}

/** 修改收货地址 */
export function updateAddress(data: {
  id: number
  receiverName: string
  receiverPhone: string
  province: string
  city: string
  district: string
  detailAddress: string
}) {
  return request.put('/user/address', data)
}

/** 删除收货地址 */
export function deleteAddress(id: number) {
  return request.delete(`/user/address/${id}`)
}

/** 设为默认地址 */
export function setDefaultAddress(id: number) {
  return request.put(`/user/address/default/${id}`)
}
