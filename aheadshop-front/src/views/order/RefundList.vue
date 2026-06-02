<template>
  <div class="refund-list-page">
    <div class="page-header">
      <h2>退款记录</h2>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="activeStatus" @change="handleFilter">
        <el-radio-button :value="null">全部</el-radio-button>
        <el-radio-button v-for="(label, key) in RefundStatusText" :key="key" :value="Number(key)">
          {{ label }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 退款列表 -->
    <div class="refund-cards" v-loading="loading">
      <template v-if="refunds.length > 0">
        <div v-for="item in refunds" :key="item.id" class="refund-card" @click="goOrderDetail(item.orderNo)">
          <div class="card-header">
            <span class="refund-no">退款单号：{{ item.refundNo }}</span>
            <el-tag :type="statusTagType(item.status)" size="small">
              {{ RefundStatusText[item.status] || '未知' }}
            </el-tag>
          </div>

          <div class="card-body">
            <div class="info-row">
              <span class="label">订单号</span>
              <span class="value">{{ item.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="label">退款金额</span>
              <span class="value refund-amount">¥{{ item.refundAmount?.toFixed(2) }}</span>
            </div>
            <div class="info-row">
              <span class="label">退款类型</span>
              <span class="value">{{ RefundTypeText[item.refundType] || '仅退款' }}</span>
            </div>
            <div v-if="item.refundReason" class="info-row">
              <span class="label">退款原因</span>
              <span class="value">{{ item.refundReason }}</span>
            </div>
            <div v-if="item.auditRemark" class="info-row">
              <span class="label">审批备注</span>
              <span class="value">{{ item.auditRemark }}</span>
            </div>
          </div>

          <div class="card-footer">
            <span class="time">申请时间：{{ formatTime(item.createTime) }}</span>
            <el-button link type="primary" size="small">
              查看订单
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </template>

      <el-empty v-else-if="!loading" description="暂无退款记录" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchRefunds"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { getUserRefundList } from '@/api/order'
import { RefundStatusText, RefundTypeText } from '@/types/order'
import type { RefundItem } from '@/types/order'

const router = useRouter()

const loading = ref(false)
const refunds = ref<RefundItem[]>([])
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)
const activeStatus = ref<number | null>(null)

function statusTagType(status: number): '' | 'success' | 'info' | 'warning' | 'danger' {
  const map: Record<number, '' | 'success' | 'info' | 'warning' | 'danger'> = {
    0: 'warning',
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger',
  }
  return map[status] || 'info'
}

function formatTime(time: string): string {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function fetchRefunds() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: pageNum.value,
      pageSize,
    }
    if (activeStatus.value !== null) params.status = activeStatus.value

    const res: any = await getUserRefundList(params)
    const pageData = res.data
    refunds.value = pageData?.records || []
    total.value = pageData?.total || 0
  } catch {
    refunds.value = []
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  pageNum.value = 1
  fetchRefunds()
}

function goOrderDetail(orderNo: string) {
  router.push(`/order/${orderNo}`)
}

onMounted(() => {
  fetchRefunds()
})
</script>

<style scoped>
.refund-list-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px 60px;
  min-height: 60vh;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin: 0;
}

.filter-bar {
  margin-bottom: 20px;
}

.filter-bar :deep(.el-radio-button__inner) {
  font-family: var(--font-body);
  font-size: 13px;
}

.refund-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.refund-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  padding: 20px 24px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.refund-card:hover {
  box-shadow: var(--shadow-md);
  border-color: var(--color-amber);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
}

.refund-no {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  font-family: var(--font-body);
  font-size: 13px;
}

.info-row .label {
  color: var(--color-text-muted);
  min-width: 60px;
  flex-shrink: 0;
}

.info-row .value {
  color: var(--color-charcoal);
}

.refund-amount {
  color: #c44536 !important;
  font-weight: 600;
  font-size: 15px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
}

.time {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .refund-list-page {
    padding: 16px 12px 40px;
  }

  .refund-card {
    padding: 16px;
  }
}
</style>
