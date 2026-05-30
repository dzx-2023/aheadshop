import request from './request'

/** 商品列表 */
export function getProductList(params?: { page?: number; size?: number; categoryId?: number; keyword?: string }) {
  return request.get('/product/list', { params })
}

/** 商品详情 */
export function getProductDetail(id: number) {
  return request.get(`/product/detail/${id}`)
}

/** 商品分类列表 */
export function getCategoryList() {
  return request.get('/product/category/list')
}
