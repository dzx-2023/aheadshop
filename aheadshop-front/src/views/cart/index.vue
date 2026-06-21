<template>
  <div class="cart-page">
    <h2 class="page-title">购物车</h2>

    <!-- 购物车列表 -->
    <div v-if="cartList.length" class="cart-content">
      <!-- 表头 -->
      <div class="cart-header">
        <el-checkbox v-model="isAllChecked" :indeterminate="isIndeterminate" @change="handleCheckAll">
          全选
        </el-checkbox>
        <span class="col-info">商品信息</span>
        <span class="col-price">单价</span>
        <span class="col-qty">数量</span>
        <span class="col-subtotal">小计</span>
        <span class="col-action">操作</span>
      </div>

      <!-- 商品列表 -->
      <div class="cart-list">
        <div v-for="item in cartList" :key="item.skuId" class="cart-item">
          <el-checkbox
            :model-value="item.checked === 1"
            @change="(val: boolean) => handleCheckItem(item, val)"
          />
          <div class="item-info" @click="goDetail(item)">
            <div class="item-image">
              <img :src="item.image || item.spuMainImage || placeholderImg" :alt="item.skuName" @error="(e: Event) => (e.target as HTMLImageElement).src = placeholderImg" />
            </div>
            <div class="item-detail">
              <h4 class="item-name">{{ item.skuName }}</h4>
              <p class="item-specs">{{ formatSpecs(item.specs) }}</p>
            </div>
          </div>
          <div class="item-price">¥{{ formatPrice(item.price) }}</div>
          <div class="item-qty">
            <div class="quantity-control">
              <button class="qty-btn" :disabled="item.quantity <= 1" @click="changeQty(item, -1)">−</button>
              <input
                :value="item.quantity"
                type="number"
                min="1"
                class="qty-input"
                @blur="(e) => handleQtyInput(item, e)"
              />
              <button class="qty-btn" @click="changeQty(item, 1)">+</button>
            </div>
          </div>
          <div class="item-subtotal">¥{{ formatPrice(Number(item.price) * item.quantity) }}</div>
          <div class="item-action">
            <el-button text type="danger" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>

      <!-- 底部结算栏 -->
      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox v-model="isAllChecked" :indeterminate="isIndeterminate" @change="handleCheckAll">
            全选
          </el-checkbox>
          <el-button text type="danger" @click="handleDeleteChecked">删除已选</el-button>
        </div>
        <div class="footer-right">
          <div class="summary">
            <span class="summary-label">已选 <em>{{ checkedCount }}</em> 件商品</span>
            <span class="summary-label">合计：</span>
            <span class="summary-price">
              <span class="symbol">¥</span>
              <span class="value">{{ formatPrice(totalAmount) }}</span>
            </span>
          </div>
          <el-button
            type="primary"
            size="large"
            class="checkout-btn"
            :disabled="checkedCount === 0"
            @click="handleCheckout"
          >
            去结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空购物车 -->
    <div v-else-if="!loading" class="empty-cart">
      <el-empty description="购物车是空的">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>

    <!-- 加载中 -->
    <div v-else class="loading-state" v-loading="true" element-loading-text="加载中..."></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, updateCartItem, removeCartItem, checkCartItem } from '@/api/cart'
import { useCartStore } from '@/store/modules/cart'
import type { CartItem } from '@/types/cart'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const cartList = ref<CartItem[]>([])
const placeholderImg = 'https://placehold.co/80x80/f5f3ef/d0ccc7?text=No+Image'

// ── 计算属性 ──
const checkedCount = computed(() =>
  cartList.value.filter((i) => i.checked === 1).reduce((sum, i) => sum + i.quantity, 0)
)

const totalAmount = computed(() =>
  cartList.value
    .filter((i) => i.checked === 1)
    .reduce((sum, i) => sum + Number(i.price) * i.quantity, 0)
)

const isAllChecked = computed({
  get: () => cartList.value.length > 0 && cartList.value.every((i) => i.checked === 1),
  set: () => {},
})

const isIndeterminate = computed(() => {
  const checked = cartList.value.filter((i) => i.checked === 1).length
  return checked > 0 && checked < cartList.value.length
})

// ── 工具函数 ──
function formatPrice(price: number): string {
  return Number(price).toFixed(2)
}

function formatSpecs(specs: string): string {
  try {
    const obj = JSON.parse(specs)
    return Object.values(obj).join(' / ')
  } catch {
    return specs || ''
  }
}

// ── 数据加载 ──
async function fetchCartList() {
  try {
    const res: any = await getCartList()
    cartList.value = res.data || []
    cartStore.setCount(cartList.value.reduce((sum, i) => sum + i.quantity, 0))
  } catch {
    cartList.value = []
  } finally {
    loading.value = false
  }
}

// ── 勾选操作 ──
async function handleCheckItem(item: CartItem, checked: boolean) {
  try {
    await checkCartItem(item.skuId, checked ? 1 : 0)
    item.checked = checked ? 1 : 0
  } catch {
    // 错误已在拦截器处理
  }
}

async function handleCheckAll(checked: boolean) {
  const val = checked ? 1 : 0
  try {
    await Promise.all(
      cartList.value
        .filter((i) => i.checked !== val)
        .map((i) => checkCartItem(i.skuId, val).then(() => { i.checked = val }))
    )
  } catch {
    // 部分失败静默处理
  }
}

// ── 数量操作 ──
async function changeQty(item: CartItem, delta: number) {
  const newQty = item.quantity + delta
  if (newQty < 1) return
  try {
    await updateCartItem(item.skuId, newQty)
    item.quantity = newQty
    cartStore.setCount(cartList.value.reduce((sum, i) => sum + i.quantity, 0))
  } catch {
    // 错误已在拦截器处理
  }
}

async function handleQtyInput(item: CartItem, e: Event) {
  const input = e.target as HTMLInputElement
  const newQty = Math.max(1, Math.floor(Number(input.value) || 1))
  if (newQty === item.quantity) return
  try {
    await updateCartItem(item.skuId, newQty)
    item.quantity = newQty
    cartStore.setCount(cartList.value.reduce((sum, i) => sum + i.quantity, 0))
  } catch {
    // 回滚显示
    input.value = String(item.quantity)
  }
}

// ── 删除操作 ──
async function handleDelete(item: CartItem) {
  try {
    await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
    await removeCartItem(item.skuId)
    cartList.value = cartList.value.filter((i) => i.skuId !== item.skuId)
    cartStore.setCount(cartList.value.reduce((sum, i) => sum + i.quantity, 0))
    ElMessage.success('已删除')
  } catch {
    // 取消或失败
  }
}

async function handleDeleteChecked() {
  const checkedItems = cartList.value.filter((i) => i.checked === 1)
  if (!checkedItems.length) return
  try {
    await ElMessageBox.confirm(`确定删除已选的 ${checkedItems.length} 件商品？`, '提示', { type: 'warning' })
    await Promise.all(checkedItems.map((i) => removeCartItem(i.skuId)))
    cartList.value = cartList.value.filter((i) => i.checked !== 1)
    cartStore.setCount(cartList.value.reduce((sum, i) => sum + i.quantity, 0))
    ElMessage.success('已删除')
  } catch {
    // 取消或失败
  }
}

// ── 跳转 ──
function goDetail(item: CartItem) {
  router.push(`/product/${item.spuId}`)
}

function handleCheckout() {
  const checkedItems = cartList.value.filter((i) => i.checked === 1)
  if (!checkedItems.length) {
    ElMessage.warning('请先选择商品')
    return
  }
  // 跳转下单确认页（后续实现）
  router.push('/order/confirm')
}

onMounted(() => {
  fetchCartList()
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 24px;
}

/* ── 表头 ── */
.cart-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: var(--color-surface);
  border-radius: 10px 10px 0 0;
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-muted);
}

.col-info { flex: 1; }
.col-price { width: 100px; text-align: center; }
.col-qty { width: 120px; text-align: center; }
.col-subtotal { width: 100px; text-align: center; }
.col-action { width: 80px; text-align: center; }

/* ── 商品列表 ── */
.cart-list {
  background: #fff;
  border: 1px solid rgba(232, 228, 223, 0.6);
  border-top: none;
  border-radius: 0 0 10px 10px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 16px;
  border-bottom: 1px solid rgba(232, 228, 223, 0.4);
  transition: background 0.2s;
}

.cart-item:last-child {
  border-bottom: none;
}

.cart-item:hover {
  background: rgba(253, 251, 247, 0.6);
}

.item-info {
  flex: 1;
  display: flex;
  gap: 14px;
  cursor: pointer;
  min-width: 0;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f3ef;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.item-info:hover .item-image img {
  transform: scale(1.05);
}

.item-detail {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}

.item-name {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 6px;
}

.item-specs {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  padding: 3px 8px;
  background: var(--color-surface);
  border-radius: 4px;
  display: inline-block;
  width: fit-content;
}

.item-price {
  width: 100px;
  text-align: center;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
}

.item-qty {
  width: 120px;
  display: flex;
  justify-content: center;
}

.quantity-control {
  display: inline-flex;
  align-items: center;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  overflow: hidden;
}

.qty-btn {
  width: 30px;
  height: 30px;
  background: #fff;
  border: none;
  font-size: 14px;
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
  width: 44px;
  height: 30px;
  border: none;
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);
  text-align: center;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
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

.item-subtotal {
  width: 100px;
  text-align: center;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 600;
  color: #c44536;
}

.item-action {
  width: 80px;
  text-align: center;
}

/* ── 底部结算栏 ── */
.cart-footer {
  position: sticky;
  bottom: 0;
  z-index: 50;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(232, 228, 223, 0.6);
  border-radius: 12px;
  box-shadow: 0 -2px 20px rgba(45, 41, 38, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  margin-top: 16px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 28px;
}

.summary {
  display: flex;
  align-items: baseline;
  gap: 10px;
}

.summary-label {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
}

.summary-label em {
  font-style: normal;
  color: var(--color-amber);
  font-weight: 600;
}

.summary-price {
  color: #c44536;
  font-weight: 700;
}

.summary-price .symbol {
  font-size: 14px;
}

.summary-price .value {
  font-family: 'DM Sans', sans-serif;
  font-size: 26px;
  letter-spacing: -0.5px;
}

.checkout-btn {
  height: 46px;
  min-width: 150px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  background: var(--color-charcoal);
  border: none;
  border-radius: 10px;
  transition: all 0.25s;
}

.checkout-btn:hover:not(:disabled) {
  background: var(--color-charcoal-light, #3d3935);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(45, 41, 38, 0.2);
}

.checkout-btn:disabled {
  opacity: 0.5;
}

/* ── 空状态 ── */
.empty-cart {
  padding: 80px 0;
}

.loading-state {
  height: 400px;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .cart-header {
    display: none;
  }

  .cart-item {
    flex-wrap: wrap;
    gap: 12px;
  }

  .item-price,
  .col-price,
  .col-qty,
  .col-subtotal,
  .col-action {
    display: none;
  }

  .item-info {
    flex: 1 0 0;
  }

  .item-qty {
    width: auto;
  }

  .item-subtotal {
    width: auto;
    font-size: 14px;
  }

  .item-action {
    width: auto;
  }

  .cart-footer {
    padding: 12px 16px;
  }

  .summary-price .value {
    font-size: 20px;
  }
}
</style>
