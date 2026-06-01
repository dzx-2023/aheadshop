/** 分类树节点 */
export interface CategoryTree {
  id: number
  parentId: number
  name: string
  icon: string
  level: number
  sortOrder: number
  children?: CategoryTree[]
}

/** SPU 分页项 */
export interface SpuPageItem {
  id: number
  name: string
  subtitle: string
  mainImage: string
  categoryName: string
  brandName: string
  minPrice: number
  sales: number
  status: number
}

/** SKU */
export interface SkuItem {
  id: number
  skuName: string
  specs: string
  price: number
  originalPrice: number
  stock: number
  lockedStock: number
  image: string
  status: number
}

/** SPU 详情 */
export interface SpuDetail {
  id: number
  name: string
  subtitle: string
  categoryId: number
  categoryName: string
  brandId: number
  brandName: string
  mainImage: string
  images: string
  detail: string
  status: number
  sales: number
  skus: SkuItem[]
}

/** 商品评价 */
export interface ReviewItem {
  id: number
  userId: number
  nickname: string
  avatar: string
  spuId: number
  skuId: number
  orderId: number
  score: number
  content: string
  images: string
  isAnonymous: number
  reply: string
  createTime: string
}
