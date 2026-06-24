import request from './request'

/** 分销商列表（通过 admin-service） */
export function getDistributorList(params?: { status?: number; keyword?: string; pageNum?: number; pageSize?: number }) {
  return request.get('/admin/distribution/list', { params })
}

/** 启用/禁用分销商（通过 admin-service） */
export function updateDistributorStatus(userId: number, status: number) {
  return request.put(`/admin/distribution/${userId}/status`, null, { params: { status } })
}

/** 提现记录列表 - 管理端（通过 admin-service） */
export function getAdminWithdrawList(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return request.get('/admin/distribution/withdraw/list', { params })
}

/** 审核提现（通过 admin-service） */
export function auditWithdraw(data: { id: number; approved: boolean; remark?: string }) {
  return request.post('/admin/distribution/withdraw/audit', data)
}
