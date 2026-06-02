<template>
  <div class="order-detail-page" v-loading="loading">
    <template v-if="order">
      <!-- 订单状态 -->
      <div class="status-banner" :class="`status-${order.status}`">
        <h2 class="status-text">{{ OrderStatusText[order.status] }}</h2>
        <p class="status-hint">{{ statusHint }}</p>
        <!-- 待支付倒计时 -->
        <div v-if="order.status === OrderStatus.PENDING_PAY && countdownText" class="countdown-bar">
          <svg class="countdown-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <polyline points="12 6 12 12 16 14"/>
          </svg>
          <span class="countdown-label">支付剩余</span>
          <span class="countdown-time" :class="{ urgent: isUrgent }">{{ countdownText }}</span>
          <span class="countdown-suffix">后自动取消</span>
        </div>
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

      <!-- 退款信息 -->
      <section v-if="refundInfo" class="section refund-section">
        <h3>退款信息</h3>
        <div class="info-row">
          <span class="info-label">退款单号</span>
          <span class="info-value">{{ refundInfo.refundNo }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">退款类型</span>
          <span class="info-value">{{ RefundTypeText[refundInfo.refundType] || '仅退款' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">退款金额</span>
          <span class="info-value" style="color: #c44536; font-weight: 600">¥{{ formatPrice(refundInfo.refundAmount) }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">退款原因</span>
          <span class="info-value">{{ refundInfo.refundReason || '-' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">退款状态</span>
          <span class="info-value">
            <el-tag :type="refundInfo.status === 3 ? 'success' : refundInfo.status === 4 ? 'danger' : 'warning'" size="small">
              {{ RefundStatusText[refundInfo.status] || '未知' }}
            </el-tag>
          </span>
        </div>
        <div v-if="refundInfo.auditRemark" class="info-row">
          <span class="info-label">审批备注</span>
          <span class="info-value">{{ refundInfo.auditRemark }}</span>
        </div>
      </section>

      <!-- 操作按钮 -->
      <div class="action-bar">
        <template v-if="order.status === OrderStatus.PENDING_PAY">
          <el-button size="large" @click="handleCancel">取消订单</el-button>
          <el-button type="primary" size="large" class="pay-btn" :loading="paying" @click="goPay">{{ paying ? '等待支付...' : '去支付' }}</el-button>
        </template>
        <template v-if="order.status === OrderStatus.SHIPPED">
          <el-button type="primary" size="large" @click="handleConfirm">确认收货</el-button>
        </template>
        <template v-if="canRefund">
          <el-button size="large" type="danger" plain @click="showRefundDialog">申请退款</el-button>
        </template>
        <el-button size="large" @click="$router.push('/order')">返回订单列表</el-button>
      </div>

      <!-- 退款申请弹窗 -->
      <el-dialog v-model="refundDialogVisible" title="申请退款" width="460px" :close-on-click-modal="false">
        <el-form label-width="80px">
          <el-form-item label="订单号">
            <el-input :model-value="order?.orderNo" disabled />
          </el-form-item>
          <el-form-item label="退款类型">
            <el-radio-group v-model="refundType">
              <el-radio :value="1">仅退款</el-radio>
              <el-radio :value="2">退货退款</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="退款金额">
            <span style="color: #c44536; font-weight: 600; font-size: 16px">¥{{ order?.payAmount?.toFixed(2) }}</span>
          </el-form-item>
          <el-form-item label="退款原因">
            <el-input v-model="refundReason" type="textarea" :rows="3" placeholder="请输入退款原因（选填）" maxlength="200" show-word-limit />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="refundDialogVisible = false">取消</el-button>
          <el-button type="danger" :loading="refundLoading" @click="handleRefund">确认退款</el-button>
        </template>
      </el-dialog>
    </template>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder, confirmOrder, applyRefund, getRefundByOrderNo } from '@/api/order'
import { createPay, queryPayStatus } from '@/api/pay'
import { OrderStatus, OrderStatusText, RefundStatusText, RefundTypeText } from '@/types/order'
import type { RefundItem } from '@/types/order'
import type { OrderDetail as OrderDetailType } from '@/types/order'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const order = ref<OrderDetailType | null>(null)
const placeholderImg = 'https://placehold.co/60x60/f5f3ef/d0ccc7?text=No+Image'

const stepsActive = computed(() => {
  if (!order.value) return 0
  const map: Record<number, number> = { 0: 1, 1: 2, 2: 3, 3: 3, 4: 4, 5: 1, 6: 2, 7: 2 }
  return map[order.value.status] ?? 0
})

const statusHint = computed(() => {
  if (!order.value) return ''
  const map: Record<number, string> = {
    0: '请在30分钟内完成支付，超时订单将自动取消',
    1: '商家正在准备发货，请耐心等待',
    2: '商品已发出，请注意查收',
    3: '商品已收货',
    4: '订单已完成，感谢您的购买',
    5: '订单已取消',
    6: '退款申请处理中',
    7: '退款已完成',
  }
  return map[order.value.status] || ''
})

// ── 30 分钟支付倒计时 ──
const TIMEOUT_MS = 30 * 60 * 1000
const countdownText = ref('')
const isUrgent = ref(false)
let countdownTimer: ReturnType<typeof setInterval> | null = null

function startCountdown() {
  stopCountdown()
  if (!order.value || order.value.status !== OrderStatus.PENDING_PAY) return

  const created = new Date(order.value.createTime).getTime()
  const deadline = created + TIMEOUT_MS

  function tick() {
    const remaining = deadline - Date.now()
    if (remaining <= 0) {
      countdownText.value = ''
      isUrgent.value = false
      stopCountdown()
      // 刷新订单状态（后端会自动取消）
      if (order.value) {
        getOrderDetail(order.value.orderNo).then((res: any) => {
          order.value = res.data
        }).catch(() => {})
      }
      return
    }
    const mins = Math.floor(remaining / 60000)
    const secs = Math.floor((remaining % 60000) / 1000)
    countdownText.value = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`
    isUrgent.value = remaining < 5 * 60 * 1000 // 最后 5 分钟标红
  }

  tick()
  countdownTimer = setInterval(tick, 1000)
}

function stopCountdown() {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

// 监听订单状态变化，启动/停止倒计时
watch(() => order.value?.status, (status) => {
  if (status === OrderStatus.PENDING_PAY) {
    startCountdown()
  } else {
    stopCountdown()
    countdownText.value = ''
  }
})

onUnmounted(() => {
  stopCountdown()
  stopPoll()
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

// ── 支付 ──
const paying = ref(false)
let pollTimer: ReturnType<typeof setInterval> | null = null

async function goPay() {
  if (!order.value || paying.value) return
  paying.value = true

  try {
    const res: any = await createPay(order.value.orderNo, 1) // 1=支付宝
    const data = res.data
    const payNo = data.payNo
    const payForm = data.payForm

    // 新窗口打开支付宝
    if (payForm) {
      const win = window.open('', '_blank')
      if (win) {
        win.document.write(payForm)
        win.document.close()
      } else {
        ElMessage.warning('浏览器阻止了弹窗，请允许弹窗后重试')
        paying.value = false
        return
      }
    }

    // 后台轮询支付结果
    let count = 0
    pollTimer = setInterval(async () => {
      count++
      if (count >= 150) { // 最多 5 分钟
        stopPoll()
        return
      }
      try {
        const statusRes: any = await queryPayStatus(payNo)
        if (statusRes.data === 1) {
          stopPoll()
          ElMessage.success('支付成功')
          const detailRes: any = await getOrderDetail(order.value!.orderNo)
          order.value = detailRes.data
        } else if (statusRes.data === 2) {
          stopPoll()
          ElMessage.error('支付失败')
        }
      } catch { /* 继续轮询 */ }
    }, 2000)
  } catch {
    paying.value = false
  }
}

function stopPoll() {
  paying.value = false
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
}

// ── 退款 ──
const refundInfo = ref<RefundItem | null>(null)
const refundDialogVisible = ref(false)
const refundLoading = ref(false)
const refundReason = ref('')
const refundType = ref(1)

const canRefund = computed(() => {
  if (!order.value || refundInfo.value) return false
  return [OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.RECEIVED].includes(order.value.status as any)
})

function showRefundDialog() {
  refundReason.value = ''
  refundType.value = 1
  refundDialogVisible.value = true
}

async function handleRefund() {
  if (!order.value) return
  refundLoading.value = true
  try {
    await applyRefund(order.value.orderNo, refundType.value, refundReason.value || undefined)
    ElMessage.success('退款申请已提交')
    refundDialogVisible.value = false
    // 刷新订单和退款信息
    const [detailRes, refundRes] = await Promise.all([
      getOrderDetail(order.value.orderNo),
      getRefundByOrderNo(order.value.orderNo).catch(() => null),
    ])
    order.value = (detailRes as any).data
    refundInfo.value = (refundRes as any)?.data || null
  } catch {
    // 错误已在拦截器处理
  } finally {
    refundLoading.value = false
  }
}

async function fetchRefundInfo() {
  if (!order.value) return
  try {
    const res: any = await getRefundByOrderNo(order.value.orderNo)
    refundInfo.value = res.data
  } catch {
    refundInfo.value = null
  }
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
    // 加载退款信息（如有）
    fetchRefundInfo()
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

.status-banner.status-0 {
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
  margin-bottom: 0;
}

/* ── 倒计时 ── */
.countdown-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 14px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  width: fit-content;
}

.countdown-icon {
  width: 18px;
  height: 18px;
  opacity: 0.8;
  flex-shrink: 0;
}

.countdown-label {
  font-family: var(--font-body);
  font-size: 13px;
  opacity: 0.85;
}

.countdown-time {
  font-family: 'DM Sans', sans-serif;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #fff;
}

.countdown-time.urgent {
  color: #ffd666;
  animation: pulse 1s ease-in-out infinite;
}

.countdown-suffix {
  font-family: var(--font-body);
  font-size: 13px;
  opacity: 0.85;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
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

/* ── 退款信息 ── */
.refund-section {
  border-left: 3px solid #d4a574;
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
