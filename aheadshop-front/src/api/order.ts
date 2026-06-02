import request from './request'

/** 创建订单 */
export function createOrder(data: { addressId: number; remark?: string; skuId?: number; quantity?: number }) {
  return request.post('/order/create', data)
}

/** 订单列表 */
export function getOrderList(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return request.get('/order/list', { params })
}

/** 订单详情 */
export function getOrderDetail(orderNo: string) {
  return request.get(`/order/detail/${orderNo}`)
}

/** 取消订单 */
export function cancelOrder(orderNo: string) {
  return request.put(`/order/cancel/${orderNo}`)
}

/** 确认收货 */
export function confirmOrder(orderNo: string) {
  return request.put(`/order/confirm/${orderNo}`)
}

/** 申请退款 */
export function applyRefund(orderNo: string, refundType: number = 1, reason?: string) {
  return request.post(`/order/refund/${orderNo}`, null, {
    params: {
      refundType,
      ...(reason ? { reason } : {}),
    },
  })
}

/** 查询订单的退款信息 */
export function getRefundByOrderNo(orderNo: string) {
  return request.get(`/order/refund/${orderNo}`)
}

/** 我的退款列表 */
export function getUserRefundList(params?: { status?: number; pageNum?: number; pageSize?: number }) {
  return request.get('/order/refund/list', { params })
}
