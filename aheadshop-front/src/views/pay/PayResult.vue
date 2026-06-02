<template>
  <div class="pay-result-page" v-loading="loading" element-loading-text="正在确认支付结果...">
    <!-- 支付成功 -->
    <template v-if="payStatus === 1">
      <div class="result-card success">
        <div class="result-icon">
          <svg viewBox="0 0 64 64" fill="none">
            <circle cx="32" cy="32" r="30" fill="#52c41a" opacity="0.12"/>
            <circle cx="32" cy="32" r="24" fill="#52c41a" opacity="0.2"/>
            <path d="M20 33l8 8 16-16" stroke="#52c41a" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2 class="result-title">支付成功</h2>
        <p class="result-desc">订单号：{{ orderNo }}</p>
        <div class="result-actions">
          <el-button size="large" @click="$router.push(`/order/${orderNo}`)">查看订单</el-button>
          <el-button type="primary" size="large" @click="$router.push('/')">继续购物</el-button>
        </div>
      </div>
    </template>

    <!-- 支付失败 -->
    <template v-else-if="payStatus === 2">
      <div class="result-card failed">
        <div class="result-icon">
          <svg viewBox="0 0 64 64" fill="none">
            <circle cx="32" cy="32" r="30" fill="#f5222d" opacity="0.12"/>
            <circle cx="32" cy="32" r="24" fill="#f5222d" opacity="0.2"/>
            <path d="M24 24l16 16M40 24L24 40" stroke="#f5222d" stroke-width="3.5" stroke-linecap="round"/>
          </svg>
        </div>
        <h2 class="result-title">支付失败</h2>
        <p class="result-desc">如有疑问请联系客服</p>
        <div class="result-actions">
          <el-button size="large" @click="$router.push('/order')">查看订单</el-button>
          <el-button type="primary" size="large" @click="retryPay">重新支付</el-button>
        </div>
      </div>
    </template>

    <!-- 待支付（轮询中或超时） -->
    <template v-else-if="!loading && payStatus === 0">
      <div class="result-card pending">
        <div class="result-icon">
          <svg viewBox="0 0 64 64" fill="none">
            <circle cx="32" cy="32" r="30" fill="#d4a574" opacity="0.12"/>
            <circle cx="32" cy="32" r="24" fill="#d4a574" opacity="0.2"/>
            <circle cx="32" cy="32" r="16" stroke="#d4a574" stroke-width="3" fill="none"/>
            <polyline points="32 20 32 32 40 36" stroke="#d4a574" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2 class="result-title">等待支付结果</h2>
        <p class="result-desc">如已完成支付，请稍候...</p>
        <div class="result-actions">
          <el-button size="large" @click="$router.push(`/order/${orderNo}`)">查看订单</el-button>
          <el-button type="primary" size="large" @click="retryPay">重新支付</el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { queryPayStatus } from '@/api/pay'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const payStatus = ref<number | null>(null)
const orderNo = ref('')
let pollTimer: ReturnType<typeof setInterval> | null = null
let pollCount = 0
const MAX_POLL = 30 // 最多轮询 30 次（约 60 秒）

async function checkStatus(payNo: string) {
  try {
    const res: any = await queryPayStatus(payNo)
    const status = res.data
    payStatus.value = status
    // 支付成功或失败，停止轮询
    if (status === 1 || status === 2) {
      stopPolling()
      loading.value = false
    }
  } catch {
    // 查询失败，停止轮询
    stopPolling()
    loading.value = false
  }
}

function startPolling(payNo: string) {
  pollTimer = setInterval(() => {
    pollCount++
    if (pollCount >= MAX_POLL) {
      stopPolling()
      loading.value = false
      return
    }
    checkStatus(payNo)
  }, 2000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

function retryPay() {
  if (orderNo.value) {
    router.push(`/order/${orderNo.value}`)
  } else {
    router.push('/order')
  }
}

onMounted(async () => {
  // 从 URL 参数获取 payNo 和 orderNo
  const payNo = route.query.payNo as string
  orderNo.value = (route.query.orderNo as string) || ''

  if (!payNo) {
    loading.value = false
    payStatus.value = 0
    return
  }

  // 立即查一次
  await checkStatus(payNo)

  // 如果还是待支付，开始轮询
  if (payStatus.value === 0) {
    startPolling(payNo)
  }
})

onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.pay-result-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 80px 20px;
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-card {
  text-align: center;
  width: 100%;
}

.result-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 24px;
  animation: scaleIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.result-icon svg {
  width: 100%;
  height: 100%;
}

.result-title {
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 8px;
}

.result-desc {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
  margin-bottom: 32px;
}

.result-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.result-actions .el-button {
  min-width: 130px;
  font-family: var(--font-body);
  font-weight: 500;
  border-radius: 10px;
}

.result-actions .el-button--primary {
  background: var(--color-charcoal);
  border-color: var(--color-charcoal);
}

@keyframes scaleIn {
  from { transform: scale(0.5); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

@media (max-width: 768px) {
  .pay-result-page {
    padding: 48px 20px;
  }

  .result-title {
    font-size: 22px;
  }

  .result-actions {
    flex-direction: column;
  }
}
</style>
