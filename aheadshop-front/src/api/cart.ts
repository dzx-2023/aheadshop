import request from './request'

/** 获取购物车列表 */
export function getCartList() {
  return request.get('/cart/list')
}

/** 添加购物车 */
export function addToCart(data: { skuId: number; quantity: number }) {
  return request.post('/cart/add', data)
}

/** 更新购物车数量 */
export function updateCartItem(skuId: number, quantity: number) {
  return request.put('/cart/update', { skuId, quantity })
}

/** 删除购物车商品 */
export function removeCartItem(skuId: number) {
  return request.delete(`/cart/delete/${skuId}`)
}

/** 勾选/取消勾选 */
export function checkCartItem(skuId: number, checked: number) {
  return request.put(`/cart/check/${skuId}`, null, { params: { checked } })
}

/** 获取购物车数量 */
export function getCartCount() {
  return request.get('/cart/count')
}
