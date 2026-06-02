import request from './request'

/** 创建支付（返回支付宝表单 HTML） */
export function createPay(orderNo: string, payType: number) {
  return request.post('/pay/create', { orderNo, payType })
}

/** 查询支付状态（0=待支付 1=已支付 2=支付失败） */
export function queryPayStatus(payNo: string) {
  return request.get(`/pay/status/${payNo}`)
}
