import request from './request'

/** 仪表盘总览数据 */
export function getDashboardOverview() {
  return request.get('/admin/dashboard/overview')
}

/** 管理端-订单列表 */
export function getAdminOrders(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return request.get('/admin/order/list', { params })
}

/** 管理端-发货 */
export function shipOrder(orderNo: string) {
  return request.put(`/admin/order/ship/${orderNo}`)
}

/** 管理端-用户列表 */
export function getAdminUsers(params?: { keyword?: string; pageNum?: number; pageSize?: number }) {
  return request.get('/admin/user/list', { params })
}

/** 管理端-启用/禁用用户 */
export function updateUserStatus(userId: number, status: number) {
  return request.put('/admin/user/status', null, { params: { userId, status } })
}
