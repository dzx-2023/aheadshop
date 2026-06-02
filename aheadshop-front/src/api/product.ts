import request from './request'
import type { SpuSaveData } from '@/types/product'

/** 商品列表（分页） */
export function getSpuList(params?: {
  keyword?: string
  categoryId?: number
  brandId?: number
  status?: number
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
export function checkReviewed(orderNo: string) {
  return request.get(`/product/review/check/${orderNo}`)
}

/** 发表评价 */
export function submitReview(data: {
  spuId: number
  skuId: number
  orderNo: string
  score: number
  content?: string
  images?: string
  isAnonymous?: number
}) {
  return request.post('/product/review', data)
}

/** 新增商品 */
export function createSpu(data: SpuSaveData) {
  return request.post('/product/spu', data)
}

/** 修改商品 */
export function updateSpu(id: number, data: SpuSaveData) {
  return request.put(`/product/spu/${id}`, data)
}

/** 上架 */
export function onShelf(id: number) {
  return request.put(`/product/spu/on-shelf/${id}`)
}

/** 下架 */
export function offShelf(id: number) {
  return request.put(`/product/spu/off-shelf/${id}`)
}

/** 上传商品图片 */
export function uploadProductImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/product/spu/upload', formData)
}

/** 获取启用的背景图列表（公开） */
export function getActiveBackgrounds() {
  return request.get('/product/background/active')
}

/** 查询分类下的品牌列表 */
export function getBrandsByCategory(categoryId: number) {
  return request.get(`/product/category-brand/brands/${categoryId}`)
}
