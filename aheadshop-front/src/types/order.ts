/** 订单状态枚举（与后端 OrderStatus 枚举 code 一致） */
export const OrderStatus = {
  PENDING_PAY: 0,
  PAID: 1,
  SHIPPED: 2,
  RECEIVED: 3,
  COMPLETED: 4,
  CANCELLED: 5,
  REFUNDING: 6,
  REFUNDED: 7,
} as const

export const OrderStatusText: Record<number, string> = {
  0: '待支付',
  1: '已支付',
  2: '已发货',
  3: '已收货',
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
  trackingNumber?: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  createTime: string
}

/** 订单商品项 */
export interface OrderItem {
  spuId: number
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
  shipTime: string
  trackingNumber: string
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  remark: string
  createTime: string
  items: OrderItem[]
}

/** 退款状态（与后端 RefundServiceImpl 一致） */
export const RefundStatus = {
  PENDING: 0,     // 待审核
  APPROVED: 1,    // 已审核
  PROCESSING: 2,  // 退款中
  REFUNDED: 3,    // 已退款
  REJECTED: 4,    // 已拒绝
} as const

export const RefundStatusText: Record<number, string> = {
  0: '待审核',
  1: '已审核',
  2: '退款中',
  3: '已退款',
  4: '已拒绝',
}

/** 退款类型 */
export const RefundType = {
  ONLY_REFUND: 1,      // 仅退款
  RETURN_REFUND: 2,    // 退货退款
} as const

export const RefundTypeText: Record<number, string> = {
  1: '仅退款',
  2: '退货退款',
}

/** 退款列表项 */
export interface RefundItem {
  id: number
  refundNo: string
  orderNo: string
  userId: number
  refundAmount: number
  refundReason: string
  refundType: number
  status: number
  auditRemark: string
  createTime: string
}

/** 提交订单返回 */
export interface SubmitOrderResult {
  orderNo: string
  totalAmount: number
  payAmount: number
}
