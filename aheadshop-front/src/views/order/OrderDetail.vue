<template>
  <div class="order-detail-page" v-loading="loading">
    <template v-if="order">
      <!-- 订单状态 -->
      <div class="status-banner" :class="`status-${order.status}`">
        <h2 class="status-text">{{ OrderStatusText[order.status] }}</h2>
        <p class="status-hint">{{ statusHint }}</p>
      </div>

      <!-- 状态步骤条 -->
      <div class="steps-section">
        <el-steps :active="stepsActive" finish-status="success" align-center>
          <el-step title="提交订单" :description="formatTime(order.createTime)" />
          <el-step title="付款成功" :description="order.payTime ? formatTime(order.payTime) : ''" />
          <el-step title="已发货" />
          <el-step title="已完成" />
        </el-steps>
      </div>

      <!-- 收货地址 -->
      <section class="section">
        <h3>收货信息</h3>
        <div class="address-info">
          <div class="address-row">
            <span class="address-name">{{ order.receiverName }}</span>
            <span class="address-phone">{{ order.receiverPhone }}</span>
          </div>
          <p class="address-detail">{{ order.receiverAddress }}</p>
        </div>
      </section>

      <!-- 商品明细 -->
      <section class="section">
        <h3>商品明细</h3>
        <div class="goods-list">
          <div v-for="item in order.items" :key="item.skuId" class="goods-item">
            <div class="goods-image">
              <img :src="item.skuImage || placeholderImg" :alt="item.skuName" />
            </div>
            <div class="goods-info">
              <h4 class="goods-name">{{ item.skuName }}</h4>
              <p class="goods-specs">{{ formatSpecs(item.specs) }}</p>
            </div>
            <div class="goods-price">¥{{ formatPrice(item.price) }}</div>
            <div class="goods-qty">×{{ item.quantity }}</div>
            <div class="goods-subtotal">¥{{ formatPrice(item.totalPrice) }}</div>
          </div>
        </div>
      </section>

      <!-- 费用明细 -->
      <section class="section fee-section">
        <h3>费用明细</h3>
        <div class="fee-row">
          <span class="fee-label">商品总额</span>
          <span class="fee-value">¥{{ formatPrice(order.totalAmount) }}</span>
        </div>
        <div class="fee-row">
          <span class="fee-label">运费</span>
          <span class="fee-value">¥{{ formatPrice(order.freightAmount || 0) }}</span>
        </div>
        <div v-if="order.discountAmount > 0" class="fee-row">
          <span class="fee-label">优惠</span>
          <span class="fee-value discount">-¥{{ formatPrice(order.discountAmount) }}</span>
        </div>
        <div class="fee-row total">
          <span class="fee-label">实付金额</span>
          <span class="fee-value">
            <span class="symbol">¥</span>
            <span class="amount">{{ formatPrice(order.payAmount) }}</span>
          </span>
        </div>
      </section>

      <!-- 订单信息 -->
      <section class="section info-section">
        <h3>订单信息</h3>
        <div class="info-row">
          <span class="info-label">订单号</span>
          <span class="info-value">{{ order.orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">下单时间</span>
          <span class="info-value">{{ formatTime(order.createTime) }}</span>
        </div>
        <div v-if="order.payTime" class="info-row">
          <span class="info-label">支付时间</span>
          <span class="info-value">{{ formatTime(order.payTime) }}</span>
        </div>
        <div v-if="order.remark" class="info-row">
          <span class="info-label">订单备注</span>
          <span class="info-value">{{ order.remark }}</span>
        </div>
      </section>

      <!-- 操作按钮 -->
      <div class="action-bar">
        <template v-if="order.status === 1">
          <el-button size="large" @click="handleCancel">取消订单</el-button>
          <el-button type="primary" size="large" class="pay-btn" @click="goPay">去支付</el-button>
        </template>
        <template v-if="order.status === 3">
          <el-button type="primary" size="large" @click="handleConfirm">确认收货</el-button>
        </template>
        <el-button size="large" @click="$router.push('/order')">返回订单列表</el-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder, confirmOrder } from '@/api/order'
import { OrderStatusText } from '@/types/order'
import type { OrderDetail as OrderDetailType } from '@/types/order'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const order = ref<OrderDetailType | null>(null)
const placeholderImg = 'https://placehold.co/60x60/f5f3ef/d0ccc7?text=No+Image'

const stepsActive = computed(() => {
  if (!order.value) return 0
  const map: Record<number, number> = { 1: 1, 2: 2, 3: 3, 4: 4, 5: 1, 6: 2, 7: 2 }
  return map[order.value.status] || 0
})

const statusHint = computed(() => {
  if (!order.value) return ''
  const map: Record<number, string> = {
    1: '请在30分钟内完成支付，超时订单将自动取消',
    2: '商家正在准备发货，请耐心等待',
    3: '商品已发出，请注意查收',
    4: '订单已完成，感谢您的购买',
    5: '订单已取消',
    6: '退款申请处理中',
    7: '退款已完成',
  }
  return map[order.value.status] || ''
})

function formatPrice(price: number): string {
  return Number(price).toFixed(2)
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

function formatSpecs(specs: string): string {
  try {
    return Object.values(JSON.parse(specs)).join(' / ')
  } catch {
    return specs || ''
  }
}

async function handleCancel() {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
    await cancelOrder(order.value.orderNo)
    ElMessage.success('订单已取消')
    router.replace('/order')
  } catch {
    // 取消或失败
  }
}

async function handleConfirm() {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'info' })
    await confirmOrder(order.value.orderNo)
    ElMessage.success('已确认收货')
    // 刷新详情
    const res: any = await getOrderDetail(order.value.orderNo)
    order.value = res.data
  } catch {
    // 取消或失败
  }
}

function goPay() {
  // 支付页后续实现
  ElMessage.info('支付功能开发中')
}

onMounted(async () => {
  const orderNo = route.params.orderNo as string
  if (!orderNo) {
    loading.value = false
    return
  }
  try {
    const res: any = await getOrderDetail(orderNo)
    order.value = res.data
  } catch {
    ElMessage.error('订单信息加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.order-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px 60px;
  min-height: 60vh;
}

/* ── 状态横幅 ── */
.status-banner {
  border-radius: 12px;
  padding: 28px 32px;
  margin-bottom: 20px;
  color: #fff;
  background: linear-gradient(135deg, #2d2926 0%, #4a4540 100%);
}

.status-banner.status-1 {
  background: linear-gradient(135deg, #d4a574 0%, #c4956a 100%);
}

.status-banner.status-4 {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.status-banner.status-5,
.status-banner.status-7 {
  background: linear-gradient(135deg, #8c8c8c 0%, #bfbfbf 100%);
}

.status-text {
  font-family: var(--font-display);
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.status-hint {
  font-family: var(--font-body);
  font-size: 13px;
  opacity: 0.85;
}

/* ── 步骤条 ── */
.steps-section {
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  padding: 28px 32px;
  margin-bottom: 16px;
}

:deep(.el-step__head.is-finish) {
  color: var(--color-amber);
  border-color: var(--color-amber);
}

:deep(.el-step__title.is-finish) {
  color: var(--color-amber);
}

:deep(.el-step__head.is-process) {
  color: var(--color-charcoal);
}

/* ── 通用 section ── */
.section {
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  padding: 24px;
  margin-bottom: 16px;
}

.section h3 {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 16px;
}

/* ── 收货信息 ── */
.address-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.address-name {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.address-phone {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
}

.address-detail {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text);
}

/* ── 商品明细 ── */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  background: var(--color-surface);
  border-radius: 8px;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f3ef;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-charcoal);
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-specs {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
}

.goods-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--color-charcoal);
  width: 80px;
  text-align: center;
}

.goods-qty {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
  width: 40px;
  text-align: center;
}

.goods-subtotal {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: #c44536;
  width: 90px;
  text-align: right;
}

/* ── 费用明细 ── */
.fee-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  font-family: var(--font-body);
  font-size: 14px;
}

.fee-label {
  color: var(--color-text-muted);
}

.fee-value {
  color: var(--color-charcoal);
}

.fee-value.discount {
  color: #52c41a;
}

.fee-row.total {
  border-top: 1px solid var(--color-border);
  margin-top: 8px;
  padding-top: 16px;
}

.fee-row.total .fee-label {
  font-weight: 600;
  color: var(--color-charcoal);
  font-size: 15px;
}

.fee-row.total .fee-value {
  color: #c44536;
  font-weight: 700;
}

.fee-row.total .symbol {
  font-size: 14px;
}

.fee-row.total .amount {
  font-family: 'DM Sans', sans-serif;
  font-size: 24px;
  letter-spacing: -0.5px;
}

/* ── 订单信息 ── */
.info-row {
  display: flex;
  padding: 8px 0;
  font-family: var(--font-body);
  font-size: 13px;
}

.info-label {
  width: 80px;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.info-value {
  color: var(--color-charcoal);
}

/* ── 操作按钮 ── */
.action-bar {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
}

.pay-btn {
  min-width: 140px;
  background: var(--color-charcoal);
  border: none;
}

@media (max-width: 768px) {
  .goods-price { display: none; }
  .goods-qty { display: none; }
  .action-bar { flex-wrap: wrap; }
}
</style>
