/** 订单状态枚举 */
export const OrderStatus = {
  PENDING_PAY: 1,
  PAID: 2,
  SHIPPED: 3,
  COMPLETED: 4,
  CANCELLED: 5,
  REFUNDING: 6,
  REFUNDED: 7,
} as const

export const OrderStatusText: Record<number, string> = {
  1: '待支付',
  2: '已支付',
  3: '已发货',
  4: '已完成',
  5: '已取消',
  6: '退款中',
  7: '已退款',
}

/** 订单列表项 */
export interface OrderPageItem {
  orderNo: string
  totalAmount: number
  payAmount: number
  status: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  createTime: string
}

/** 订单商品项 */
export interface OrderItem {
  skuId: number
  skuName: string
  skuImage: string
  specs: string
  price: number
  quantity: number
  totalPrice: number
}

/** 订单详情 */
export interface OrderDetail {
  orderNo: string
  totalAmount: number
  payAmount: number
  discountAmount: number
  freightAmount: number
  status: number
  payType: number
  payTime: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark: string
  createTime: string
  items: OrderItem[]
}

/** 提交订单返回 */
export interface SubmitOrderResult {
  orderNo: string
  totalAmount: number
  payAmount: number
}
