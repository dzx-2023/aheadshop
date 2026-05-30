import request from './request'

/** 发起支付 */
export function payOrder(orderNo: string, payMethod: number) {
  return request.post('/pay/create', { orderNo, payMethod })
}

/** 查询支付状态 */
export function queryPayStatus(orderNo: string) {
  return request.get(`/pay/status/${orderNo}`)
}
