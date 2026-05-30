import request from './request'

/** 创建订单 */
export function createOrder(data: { cartItemIds?: number[]; skuId?: number; quantity?: number; addressId: number }) {
  return request.post('/order/create', data)
}

/** 订单列表 */
export function getOrderList(params?: { page?: number; size?: number; status?: number }) {
  return request.get('/order/list', { params })
}

/** 订单详情 */
export function getOrderDetail(orderNo: string) {
  return request.get(`/order/${orderNo}`)
}

/** 取消订单 */
export function cancelOrder(orderNo: string) {
  return request.put(`/order/cancel/${orderNo}`)
}

/** 确认收货 */
export function confirmOrder(orderNo: string) {
  return request.put(`/order/confirm/${orderNo}`)
}
