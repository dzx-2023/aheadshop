/** 购物车项 */
export interface CartItem {
  skuId: number
  spuId: number
  skuName: string
  image: string
  spuMainImage: string
  price: number
  specs: string
  quantity: number
  checked: number
}
