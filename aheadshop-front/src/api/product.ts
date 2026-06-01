import request from './request'

/** 商品列表（分页） */
export function getSpuList(params?: {
  keyword?: string
  categoryId?: number
  brandId?: number
  pageNum?: number
  pageSize?: number
}) {
  return request.get('/product/spu/list', { params })
}

/** 商品详情（含 SKU 列表） */
export function getSpuDetail(id: number) {
  return request.get(`/product/spu/${id}`)
}

/** SKU 详情 */
export function getSkuInfo(id: number) {
  return request.get(`/product/sku/${id}`)
}

/** 分类树 */
export function getCategoryTree() {
  return request.get('/product/category/tree')
}

/** 品牌列表 */
export function getBrandList(params?: { pageNum?: number; pageSize?: number }) {
  return request.get('/product/brand/list', { params })
}

/** SPU 评价列表 */
export function getReviewList(spuId: number, params?: { pageNum?: number; pageSize?: number }) {
  return request.get(`/product/review/list/${spuId}`, { params })
}

/** SPU 平均评分 */
export function getAvgScore(spuId: number) {
  return request.get(`/product/review/avg-score/${spuId}`)
}

/** 我的评价列表 */
export function getMyReviews(params?: { pageNum?: number; pageSize?: number }) {
  return request.get('/product/review/my', { params })
}

/** 检查订单是否已评价 */
export function checkReviewed(orderId: number) {
  return request.get(`/product/review/check/${orderId}`)
}

/** 发表评价 */
export function submitReview(data: {
  spuId: number
  skuId: number
  orderId: number
  score: number
  content?: string
  images?: string
  isAnonymous?: number
}) {
  return request.post('/product/review', data)
}
