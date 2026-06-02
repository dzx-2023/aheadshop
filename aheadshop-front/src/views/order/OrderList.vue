<template>
  <div class="order-list-page">
    <h2 class="page-title">我的订单</h2>

    <!-- Tab 切换 -->
    <div class="tabs-row">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="0" />
        <el-tab-pane label="已支付" name="1" />
        <el-tab-pane label="已发货" name="2" />
        <el-tab-pane label="已完成" name="4" />
        <el-tab-pane label="退款中" name="6" />
        <el-tab-pane label="已退款" name="7" />
      </el-tabs>
      <el-button text type="primary" @click="$router.push('/refund')">
        退款记录
        <el-icon class="el-icon--right"><ArrowRight /></el-icon>
      </el-button>
    </div>

    <!-- 订单列表 -->
    <div v-if="orders.length" class="order-cards">
      <div v-for="order in orders" :key="order.orderNo" class="order-card">
        <div class="card-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ formatTime(order.createTime) }}</span>
          <el-tag :type="getStatusType(order.status)" size="small">{{ OrderStatusText[order.status] }}</el-tag>
        </div>

        <div class="card-body" @click="goDetail(order.orderNo)">
          <div class="order-receiver">
            <span>{{ order.receiverName }}</span>
            <span class="receiver-phone">{{ order.receiverPhone }}</span>
          </div>
          <p class="order-address">{{ order.receiverAddress }}</p>
        </div>

        <div class="card-footer">
          <div class="order-amount">
            <span class="amount-label">订单金额：</span>
            <span class="amount-value">¥{{ formatPrice(order.payAmount) }}</span>
          </div>
          <div class="order-actions">
            <template v-if="order.status === OrderStatus.PENDING_PAY">
              <el-button size="small" @click.stop="handleCancel(order)">取消订单</el-button>
              <el-button type="primary" size="small" @click.stop="goPay(order.orderNo)">去支付</el-button>
            </template>
            <template v-if="order.status === OrderStatus.SHIPPED">
              <el-button type="primary" size="small" @click.stop="handleConfirm(order)">确认收货</el-button>
            </template>
            <el-button text size="small" @click.stop="goDetail(order.orderNo)">查看详情</el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <el-empty description="暂无订单">
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div v-if="total > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder, confirmOrder } from '@/api/order'
import { OrderStatus, OrderStatusText } from '@/types/order'
import type { OrderPageItem } from '@/types/order'

const router = useRouter()

const loading = ref(true)
const activeTab = ref('all')
const orders = ref<OrderPageItem[]>([])
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)

function getStatusType(status: number): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<number, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    0: 'warning',
    1: '',
    2: 'success',
    3: 'success',
    4: 'success',
    5: 'info',
    6: 'danger',
    7: 'info',
  }
  return map[status] || 'info'
}

function formatPrice(price: number): string {
  return Number(price).toFixed(2)
}

function formatTime(time: string): string {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

function handleTabChange() {
  pageNum.value = 1
  fetchOrders()
}

async function fetchOrders() {
  loading.value = true
  try {
    const params: Record<string, any> = { pageNum: pageNum.value, pageSize }
    if (activeTab.value !== 'all') {
      params.status = Number(activeTab.value)
    }
    const res: any = await getOrderList(params as any)
    const data = res.data
    orders.value = data?.records || data?.list || []
    total.value = data?.total || 0
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

async function handleCancel(order: OrderPageItem) {
  try {
    await ElMessageBox.confirm('确定取消该订单？', '提示', { type: 'warning' })
    await cancelOrder(order.orderNo)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch {
    // 取消或失败
  }
}

async function handleConfirm(order: OrderPageItem) {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'info' })
    await confirmOrder(order.orderNo)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch {
    // 取消或失败
  }
}

function goDetail(orderNo: string) {
  router.push(`/order/${orderNo}`)
}

function goPay(orderNo: string) {
  router.push(`/order/${orderNo}`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px 60px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 20px;
}

.tabs-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.tabs-row :deep(.el-tabs__active-bar) {
  background: var(--color-amber);
}

.tabs-row :deep(.el-tabs__item) {
  font-family: var(--font-body);
  font-size: 14px;
}

/* ── 订单卡片 ── */
.order-cards {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 16px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  overflow: hidden;
  transition: box-shadow 0.2s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(45, 41, 38, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: var(--color-surface);
  border-bottom: 1px solid rgba(232, 228, 223, 0.4);
}

.order-no {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-charcoal);
  font-weight: 500;
}

.order-time {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  flex: 1;
}

.card-body {
  padding: 16px 20px;
  cursor: pointer;
}

.order-receiver {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
}

.receiver-phone {
  font-size: 13px;
  font-weight: 400;
  color: var(--color-text-muted);
}

.order-address {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text);
  line-height: 1.4;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  border-top: 1px solid rgba(232, 228, 223, 0.4);
}

.amount-label {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
}

.amount-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 18px;
  font-weight: 700;
  color: #c44536;
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ── 空状态 ── */
.empty-state {
  padding: 60px 0;
}

/* ── 分页 ── */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .card-header { flex-wrap: wrap; gap: 8px; }
  .card-footer { flex-direction: column; align-items: flex-start; gap: 12px; }
}
</style>
