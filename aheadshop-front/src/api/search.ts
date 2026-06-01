import request from './request'

/** 商品搜索 */
export function searchProducts(params?: {
  keyword?: string
  categoryId?: number
  brandId?: number
  minPrice?: number
  maxPrice?: number
  sort?: string
  pageNum?: number
  pageSize?: number
}) {
  return request.get('/search', { params })
}

/** 搜索建议 */
export function getSearchSuggest(keyword: string) {
  return request.get('/search/suggest', { params: { keyword } })
}
