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
export function shipOrder(orderNo: string, trackingNumber?: string) {
  return request.put(`/admin/order/ship/${orderNo}`, null, {
    params: trackingNumber ? { trackingNumber } : undefined,
  })
}

/** 管理端-订单详情 */
export function getAdminOrderDetail(orderNo: string) {
  return request.get(`/admin/order/${orderNo}`)
}

/** 管理端-退款列表 */
export function getRefundList(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return request.get('/admin/refund/list', { params })
}

/** 管理端-审批退款 */
export function auditRefund(refundId: number, approved: boolean, remark?: string) {
  return request.put(`/admin/refund/audit/${refundId}`, null, {
    params: { approved, ...(remark ? { remark } : {}) },
  })
}

/** 管理端-根据订单号查询退款 */
export function getRefundByOrderNo(orderNo: string) {
  return request.get(`/admin/refund/order/${orderNo}`)
}

/** 管理端-分类树 */
export function getCategoryTree() {
  return request.get('/admin/product/category/tree')
}

/** 管理端-新增分类 */
export function addCategory(data: { parentId: number; name: string; icon?: string; sortOrder?: number }) {
  return request.post('/admin/product/category', data)
}

/** 管理端-修改分类 */
export function updateCategory(data: { id: number; parentId?: number; name?: string; icon?: string; sortOrder?: number }) {
  return request.put('/admin/product/category', data)
}

/** 管理端-删除分类 */
export function deleteCategory(id: number) {
  return request.delete(`/admin/product/category/${id}`)
}

/** 管理端-用户列表 */
export function getAdminUsers(params?: { keyword?: string; pageNum?: number; pageSize?: number }) {
  return request.get('/admin/user/list', { params })
}

/** 管理端-启用/禁用用户 */
export function updateUserStatus(userId: number, status: number) {
  return request.put('/admin/user/status', null, { params: { userId, status } })
}

// ── 背景图管理 ──

/** 管理端-获取全部背景图列表 */
export function getBackgroundList() {
  return request.get('/admin/background/list')
}

/** 管理端-删除背景图 */
export function deleteBackground(id: number) {
  return request.delete(`/admin/background/${id}`)
}

/** 管理端-更新排序 */
export function updateBackgroundSort(id: number, sortOrder: number) {
  return request.put(`/admin/background/${id}/sort`, null, { params: { sortOrder } })
}

/** 管理端-启用/禁用背景图 */
export function toggleBackground(id: number, enabled: number) {
  return request.put(`/admin/background/${id}/enabled`, null, { params: { enabled } })
}

/** 上传背景图（直接调用产品服务） */
export function uploadBackground(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/product/background/upload', formData)
}
