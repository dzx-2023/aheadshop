<template>
  <div class="product-detail" v-loading="loading">
    <template v-if="spu">
      <!-- 面包屑 -->
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span class="sep">/</span>
        <router-link to="/category">{{ spu.categoryName }}</router-link>
        <span class="sep">/</span>
        <span class="current">{{ spu.name }}</span>
      </div>

      <!-- 商品主体 -->
      <div class="product-main">
        <!-- 左侧图片 -->
        <div class="gallery">
          <div class="main-image">
            <img :src="currentImage || spu.mainImage" :alt="spu.name" />
          </div>
          <div class="thumb-list" v-if="imageList.length > 1">
            <div
              v-for="(img, idx) in imageList"
              :key="idx"
              class="thumb-item"
              :class="{ active: currentImageIndex === idx }"
              @click="currentImageIndex = idx"
            >
              <img :src="img" alt="" />
            </div>
          </div>
        </div>

        <!-- 右侧信息 -->
        <div class="info">
          <h1 class="product-name">{{ spu.name }}</h1>
          <p v-if="spu.subtitle" class="product-subtitle">{{ spu.subtitle }}</p>

          <!-- 价格区 -->
          <div class="price-box">
            <div class="current-price">
              <span class="symbol">¥</span>
              <span class="value">{{ formatPrice(selectedSku?.price ?? spu.skus[0]?.price ?? 0) }}</span>
            </div>
            <div v-if="originalPrice" class="original-price">¥{{ formatPrice(originalPrice) }}</div>
            <div class="sales-info">累计销量 {{ spu.sales }}</div>
          </div>

          <!-- SKU 选择 -->
          <div v-if="specGroups.length" class="spec-section">
            <div v-for="group in specGroups" :key="group.name" class="spec-group">
              <div class="spec-label">{{ group.name }}</div>
              <div class="spec-options">
                <button
                  v-for="val in group.values"
                  :key="val"
                  class="spec-btn"
                  :class="{ active: selectedSpecs[group.name] === val }"
                  @click="selectSpec(group.name, val)"
                >
                  {{ val }}
                </button>
              </div>
            </div>
            <div v-if="selectedSku" class="stock-info">
              库存 {{ selectedSku.stock - selectedSku.lockedStock }} 件
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-section">
            <div class="spec-label">数量</div>
            <div class="quantity-control">
              <button class="qty-btn" :disabled="quantity <= 1" @click="quantity--">−</button>
              <input v-model.number="quantity" type="number" min="1" :max="maxStock" class="qty-input" />
              <button class="qty-btn" :disabled="quantity >= maxStock" @click="quantity++">+</button>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button size="large" class="btn-cart" :loading="cartLoading" @click="handleAddCart">
              加入购物车
            </el-button>
            <el-button size="large" type="primary" class="btn-buy" @click="handleBuyNow">
              立即购买
            </el-button>
          </div>
        </div>
      </div>

      <!-- 下部详情 + 评价 -->
      <div class="product-bottom">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="商品详情" name="detail">
            <div v-if="spu.detail" class="detail-content" v-html="spu.detail"></div>
            <el-empty v-else description="暂无详情" />
          </el-tab-pane>
          <el-tab-pane :label="`商品评价 (${reviewTotal})`" name="reviews">
            <div v-if="reviews.length" class="review-list">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <el-avatar :size="36" :src="review.avatar" class="review-avatar">
                    {{ (review.isAnonymous ? '匿' : review.nickname || '?')[0] }}
                  </el-avatar>
                  <div class="review-user">
                    <span class="review-name">{{ review.isAnonymous ? '匿名用户' : review.nickname }}</span>
                    <el-rate :model-value="review.score" disabled size="small" />
                  </div>
                  <span class="review-time">{{ formatTime(review.createTime) }}</span>
                </div>
                <p class="review-content">{{ review.content }}</p>
                <div v-if="review.images" class="review-images">
                  <img v-for="(img, i) in parseImages(review.images)" :key="i" :src="img" class="review-img" />
                </div>
                <div v-if="review.reply" class="review-reply">
                  <span class="reply-tag">商家回复</span>
                  {{ review.reply }}
                </div>
              </div>
              <div v-if="reviewTotal > reviews.length" class="load-more">
                <el-button @click="loadMoreReviews">加载更多评价</el-button>
              </div>
            </div>
            <el-empty v-else description="暂无评价" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSpuDetail, getReviewList } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useCartStore } from '@/store/modules/cart'
import type { SpuDetail, SkuItem, ReviewItem } from '@/types/product'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const cartLoading = ref(false)
const spu = ref<SpuDetail | null>(null)
const activeTab = ref('detail')

// 图片
const currentImageIndex = ref(0)
const imageList = computed<string[]>(() => {
  if (!spu.value) return []
  const result: string[] = []
  // 先加主图
  if (spu.value.mainImage) result.push(spu.value.mainImage)
  // 再加副图（支持 JSON 数组或逗号分隔）
  if (spu.value.images) {
    const raw = spu.value.images.trim()
    if (raw.startsWith('[')) {
      // JSON 数组格式
      try {
        const parsed = JSON.parse(raw)
        if (Array.isArray(parsed)) {
          for (const img of parsed) {
            if (img && !result.includes(img)) result.push(img)
          }
        }
      } catch { /* ignore */ }
    } else {
      // 逗号分隔格式
      for (const img of raw.split(',')) {
        const trimmed = img.trim()
        if (trimmed && !result.includes(trimmed)) result.push(trimmed)
      }
    }
  }
  return result
})
const currentImage = computed(() => imageList.value[currentImageIndex.value] || spu.value?.mainImage)

// SKU 规格
interface SpecGroup { name: string; values: string[] }
const specGroups = ref<SpecGroup[]>([])
const selectedSpecs = ref<Record<string, string>>({})

const selectedSku = computed<SkuItem | null>(() => {
  if (!spu.value?.skus?.length) return null
  // 未选任何规格时，默认第一个 SKU
  if (!Object.keys(selectedSpecs.value).length) {
    return spu.value.skus[0]
  }
  // 按已选规格匹配
  return (
    spu.value.skus.find((sku) => {
      try {
        const specs = JSON.parse(sku.specs)
        return Object.entries(selectedSpecs.value).every(([k, v]) => specs[k] === v)
      } catch {
        return false
      }
    }) || spu.value.skus[0]
  )
})

const originalPrice = computed(() => {
  const sku = selectedSku.value
  if (!sku) return 0
  return sku.originalPrice && sku.originalPrice > sku.price ? sku.originalPrice : 0
})

const maxStock = computed(() => {
  const sku = selectedSku.value
  if (!sku) return 99
  return Math.max(0, sku.stock - sku.lockedStock)
})

// 数量
const quantity = ref(1)

// 评价
const reviews = ref<ReviewItem[]>([])
const reviewTotal = ref(0)
const reviewPage = ref(1)

function formatPrice(price: number): string {
  return Number(price).toFixed(2)
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

function parseImages(images: string): string[] {
  try {
    const parsed = JSON.parse(images)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function selectSpec(groupName: string, value: string) {
  if (selectedSpecs.value[groupName] === value) {
    delete selectedSpecs.value[groupName]
  } else {
    selectedSpecs.value[groupName] = value
  }
  // 选中 SKU 后更新主图
  if (selectedSku.value?.image) {
    const idx = imageList.value.indexOf(selectedSku.value.image)
    if (idx >= 0) currentImageIndex.value = idx
  }
}

function parseSpecGroups(skus: SkuItem[]): SpecGroup[] {
  const groupMap = new Map<string, Set<string>>()
  for (const sku of skus) {
    try {
      const specs = JSON.parse(sku.specs)
      for (const [name, value] of Object.entries(specs)) {
        if (!groupMap.has(name)) groupMap.set(name, new Set())
        groupMap.get(name)!.add(value as string)
      }
    } catch {
      // specs 非 JSON，跳过
    }
  }
  return Array.from(groupMap.entries()).map(([name, values]) => ({
    name,
    values: Array.from(values),
  }))
}

async function handleAddCart() {
  if (!selectedSku.value) return
  if (maxStock.value <= 0) {
    ElMessage.warning('该规格库存不足')
    return
  }
  cartLoading.value = true
  try {
    await addToCart({ skuId: selectedSku.value.id, quantity: quantity.value })
    await cartStore.fetchCount()
    ElMessage.success('已加入购物车')
  } catch {
    // 错误已在拦截器处理
  } finally {
    cartLoading.value = false
  }
}

function handleBuyNow() {
  if (!selectedSku.value) return
  if (maxStock.value <= 0) {
    ElMessage.warning('该规格库存不足')
    return
  }
  // 跳转下单确认页（后续实现）
  router.push(`/order/confirm?skuId=${selectedSku.value.id}&quantity=${quantity.value}`)
}

async function fetchReviews(append = false) {
  if (!spu.value) return
  try {
    const res: any = await getReviewList(spu.value.id, { pageNum: reviewPage.value, pageSize: 10 })
    const pageData = res.data
    if (append) {
      reviews.value.push(...(pageData?.records || []))
    } else {
      reviews.value = pageData?.records || []
    }
    reviewTotal.value = pageData?.total || 0
  } catch {
    // 静默处理
  }
}

function loadMoreReviews() {
  reviewPage.value++
  fetchReviews(true)
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) {
    loading.value = false
    return
  }
  try {
    const res: any = await getSpuDetail(id)
    spu.value = res.data
    if (spu.value?.skus?.length) {
      specGroups.value = parseSpecGroups(spu.value.skus)
    }
    fetchReviews()
  } catch {
    ElMessage.error('商品信息加载失败')
  } finally {
    loading.value = false
  }
})

// 路由参数变化时重新加载
watch(
  () => route.params.id,
  (newId) => {
    if (newId) window.location.reload()
  }
)
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 60vh;
}

/* ── 面包屑 ── */
.breadcrumb {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
  margin-bottom: 24px;
}

.breadcrumb a {
  color: var(--color-text-muted);
  transition: color 0.2s;
}

.breadcrumb a:hover {
  color: var(--color-amber);
}

.sep {
  margin: 0 8px;
  opacity: 0.4;
}

.current {
  color: var(--color-charcoal);
}

/* ── 商品主体 ── */
.product-main {
  display: flex;
  gap: 40px;
  margin-bottom: 48px;
}

/* 图片区 */
.gallery {
  width: 460px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f3ef;
  margin-bottom: 12px;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-list {
  display: flex;
  gap: 8px;
}

.thumb-item {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid transparent;
  cursor: pointer;
  transition: border-color 0.2s;
  opacity: 0.6;
}

.thumb-item:hover,
.thumb-item.active {
  border-color: var(--color-amber);
  opacity: 1;
}

.thumb-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息区 */
.info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  color: var(--color-charcoal);
  line-height: 1.4;
  margin-bottom: 8px;
}

.product-subtitle {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
  margin-bottom: 20px;
}

/* 价格 */
.price-box {
  background: linear-gradient(135deg, #fdf5ed 0%, #fef9f3 100%);
  border-radius: 10px;
  padding: 20px 24px;
  margin-bottom: 24px;
  display: flex;
  align-items: baseline;
  gap: 16px;
}

.current-price {
  color: #c44536;
  font-weight: 700;
}

.current-price .symbol {
  font-size: 16px;
}

.current-price .value {
  font-family: 'DM Sans', sans-serif;
  font-size: 32px;
  letter-spacing: -1px;
}

.original-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--color-text-muted);
  text-decoration: line-through;
}

.sales-info {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  margin-left: auto;
}

/* 规格选择 */
.spec-section {
  margin-bottom: 24px;
}

.spec-group {
  margin-bottom: 16px;
}

.spec-label {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-charcoal);
  margin-bottom: 10px;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.spec-btn {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-charcoal);
  background: #fff;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  padding: 7px 18px;
  cursor: pointer;
  transition: all 0.2s;
}

.spec-btn:hover {
  border-color: var(--color-amber);
  color: var(--color-amber);
}

.spec-btn.active {
  border-color: var(--color-amber);
  color: var(--color-amber);
  background: rgba(212, 165, 116, 0.06);
  font-weight: 500;
}

.stock-info {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 8px;
}

/* 数量选择 */
.quantity-section {
  margin-bottom: 28px;
}

.quantity-control {
  display: inline-flex;
  align-items: center;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  overflow: hidden;
}

.qty-btn {
  width: 36px;
  height: 36px;
  background: #fff;
  border: none;
  font-size: 16px;
  color: var(--color-charcoal);
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-btn:hover:not(:disabled) {
  background: var(--color-surface);
}

.qty-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.qty-input {
  width: 56px;
  height: 36px;
  border: none;
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);
  text-align: center;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--color-charcoal);
  background: #fff;
}

.qty-input::-webkit-inner-spin-button,
.qty-input::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.qty-input[type='number'] {
  -moz-appearance: textfield;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-cart {
  flex: 1;
  height: 48px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 1px;
  border: 2px solid var(--color-charcoal);
  color: var(--color-charcoal);
  background: #fff;
  border-radius: 8px;
  transition: all 0.3s;
}

.btn-cart:hover {
  background: var(--color-charcoal);
  color: #fff;
}

.btn-buy {
  flex: 1;
  height: 48px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 1px;
  background: var(--color-charcoal);
  border: 2px solid var(--color-charcoal);
  border-radius: 8px;
  transition: all 0.3s;
}

.btn-buy:hover {
  background: var(--color-charcoal-light);
  border-color: var(--color-charcoal-light);
}

/* ── 下部详情 + 评价 ── */
.product-bottom {
  margin-top: 16px;
}

:deep(.detail-tabs .el-tabs__header) {
  margin-bottom: 24px;
}

:deep(.detail-tabs .el-tabs__item) {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 500;
}

:deep(.detail-tabs .el-tabs__active-bar) {
  background: var(--color-amber);
}

.detail-content {
  font-family: var(--font-body);
  font-size: 14px;
  line-height: 1.8;
  color: #333;
}

.detail-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

/* 评价 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-item {
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid rgba(232, 228, 223, 0.6);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-avatar {
  background: var(--color-amber);
  color: #fff;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.review-user {
  flex: 1;
  min-width: 0;
}

.review-name {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
  display: block;
  margin-bottom: 2px;
}

.review-time {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.review-content {
  font-family: var(--font-body);
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.review-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.review-img:hover {
  opacity: 0.85;
}

.review-reply {
  font-family: var(--font-body);
  font-size: 13px;
  color: #666;
  background: var(--color-surface);
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.5;
}

.reply-tag {
  font-weight: 600;
  color: var(--color-amber);
  margin-right: 8px;
}

.load-more {
  text-align: center;
  padding: 12px 0;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .product-main {
    flex-direction: column;
    gap: 24px;
  }

  .gallery {
    width: 100%;
  }

  .product-name {
    font-size: 20px;
  }

  .current-price .value {
    font-size: 26px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
