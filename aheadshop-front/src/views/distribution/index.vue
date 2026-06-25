<template>
  <div class="distribution-page">
    <div class="dist-container">
      <!-- 顶部标题 -->
      <div class="dist-hero">
        <div class="hero-left">
          <h1>分销中心</h1>
          <p>邀请好友购物，赚取佣金奖励</p>
        </div>
        <div class="hero-level" v-if="info">
          <span class="level-badge">Lv.{{ info.level }}</span>
        </div>
      </div>

      <!-- 佣金概览卡片 -->
      <div class="commission-cards">
        <div class="comm-card">
          <div class="comm-label">累计佣金</div>
          <div class="comm-value">¥{{ info?.totalCommission?.toFixed(2) || '0.00' }}</div>
        </div>
        <div class="comm-card highlight">
          <div class="comm-label">可提现</div>
          <div class="comm-value">¥{{ info?.availableCommission?.toFixed(2) || '0.00' }}</div>
        </div>
        <div class="comm-card">
          <div class="comm-label">冻结中</div>
          <div class="comm-value">¥{{ info?.frozenCommission?.toFixed(2) || '0.00' }}</div>
        </div>
      </div>

      <!-- 邀请码区域 -->
      <div class="invite-section" v-if="info">
        <div class="invite-header">
          <h3>我的邀请码</h3>
        </div>
        <div class="invite-code-box">
          <span class="invite-code">{{ info.inviteCode }}</span>
          <button class="copy-btn" @click="copyCode">
            {{ copied ? '✅ 已复制' : '📋 复制' }}
          </button>
        </div>
        <div class="invite-link">
          <span class="link-label">注册链接：</span>
          <span class="link-url">{{ inviteLink }}</span>
          <button class="copy-btn small" @click="copyLink">
            {{ linkCopied ? '已复制' : '复制' }}
          </button>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions">
        <router-link to="/distribution/team" class="action-item">
          <div class="action-icon">👥</div>
          <div class="action-info">
            <div class="action-title">我的团队</div>
            <div class="action-desc">{{ info?.totalTeamCount || 0 }} 人</div>
          </div>
          <div class="action-arrow">→</div>
        </router-link>
        <router-link to="/distribution/commission" class="action-item">
          <div class="action-icon">💰</div>
          <div class="action-info">
            <div class="action-title">佣金明细</div>
            <div class="action-desc">查看佣金记录</div>
          </div>
          <div class="action-arrow">→</div>
        </router-link>
        <router-link to="/distribution/withdraw" class="action-item">
          <div class="action-icon">🏦</div>
          <div class="action-info">
            <div class="action-title">提现记录</div>
            <div class="action-desc">申请提现 / 查看记录</div>
          </div>
          <div class="action-arrow">→</div>
        </router-link>
      </div>

      <!-- 最近佣金 -->
      <div class="recent-section">
        <div class="section-header">
          <h3>最近佣金</h3>
          <router-link to="/distribution/commission" class="view-all">查看全部 →</router-link>
        </div>
        <div v-if="recentCommissions.length === 0" class="empty-tip">
          <el-text type="info">暂无佣金记录</el-text>
        </div>
        <div v-else class="commission-list">
          <div v-for="item in recentCommissions" :key="item.id" class="commission-item">
            <div class="item-left">
              <div class="item-title">订单 {{ item.orderNo }}</div>
              <div class="item-time">{{ item.createTime }}</div>
            </div>
            <div class="item-right">
              <div class="item-amount">+¥{{ item.commissionAmount.toFixed(2) }}</div>
              <el-tag :type="CommissionStatusMap[item.status]?.type as any" size="small">
                {{ CommissionStatusMap[item.status]?.label }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部提现按钮 -->
      <div class="bottom-action" v-if="info && info.availableCommission > 0">
        <el-button type="primary" size="large" class="withdraw-btn" @click="$router.push('/distribution/withdraw')">
          申请提现
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getDistributionInfo, getCommissionList } from '@/api/distribution'
import type { DistributionInfo, CommissionRecord } from '@/types/distribution'
import { CommissionStatusMap } from '@/types/distribution'

const info = ref<DistributionInfo | null>(null)
const recentCommissions = ref<CommissionRecord[]>([])
const copied = ref(false)
const linkCopied = ref(false)

const inviteLink = computed(() => {
  if (!info.value) return ''
  return `${window.location.origin}/register?invite=${info.value.inviteCode}`
})

const copyCode = async () => {
  if (!info.value) return
  try {
    await navigator.clipboard.writeText(info.value.inviteCode)
    copied.value = true
    ElMessage.success('邀请码已复制')
    setTimeout(() => (copied.value = false), 2000)
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(inviteLink.value)
    linkCopied.value = true
    ElMessage.success('注册链接已复制')
    setTimeout(() => (linkCopied.value = false), 2000)
  } catch {
    ElMessage.error('复制失败，请手动复制')
  }
}

const loadData = async () => {
  try {
    const res = await getDistributionInfo()
    info.value = res
  } catch {
    // 未开通分销商
  }

  try {
    const res = await getCommissionList({ pageNum: 1, pageSize: 5 })
    recentCommissions.value = res?.records || []
  } catch {
    // ignore
  }
}

onMounted(loadData)
</script>

<style scoped>
.distribution-page {
  min-height: 100vh;
  background: var(--color-cream, #fdfbf7);
  padding: 24px 0 80px;
}

.dist-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Hero */
.dist-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dist-hero h1 {
  font-family: var(--font-display, 'Playfair Display', serif);
  font-size: 28px;
  color: var(--color-charcoal, #2d2926);
  margin: 0;
}

.dist-hero p {
  color: #666;
  margin: 4px 0 0;
  font-size: 14px;
}

.level-badge {
  background: linear-gradient(135deg, #d4a574, #b8860b);
  color: #fff;
  padding: 6px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

/* 佣金卡片 */
.commission-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.comm-card {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 20px 16px;
  text-align: center;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.06));
}

.comm-card.highlight {
  background: linear-gradient(135deg, var(--color-charcoal, #2d2926), #4a4543);
  color: #fff;
}

.comm-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.comm-card.highlight .comm-label {
  color: rgba(255,255,255,0.7);
}

.comm-value {
  font-size: 22px;
  font-weight: 700;
  font-family: var(--font-display, 'Playfair Display', serif);
  color: var(--color-charcoal, #2d2926);
}

.comm-card.highlight .comm-value {
  color: #fff;
}

/* 邀请码 */
.invite-section {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.06));
}

.invite-header h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: var(--color-charcoal, #2d2926);
}

.invite-code-box {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--color-cream, #fdfbf7);
  border: 2px dashed var(--color-amber, #d4a574);
  border-radius: var(--radius-md, 8px);
  padding: 16px 20px;
  margin-bottom: 12px;
}

.invite-code {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 4px;
  color: var(--color-charcoal, #2d2926);
  font-family: monospace;
  flex: 1;
  text-align: center;
}

.copy-btn {
  background: var(--color-charcoal, #2d2926);
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: var(--radius-md, 8px);
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  transition: opacity 0.2s;
}

.copy-btn:hover {
  opacity: 0.85;
}

.copy-btn.small {
  padding: 4px 12px;
  font-size: 12px;
}

.invite-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.link-label {
  white-space: nowrap;
}

.link-url {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-amber, #d4a574);
}

/* 快捷操作 */
.quick-actions {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.06));
}

.action-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  text-decoration: none;
  color: inherit;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.15s;
}

.action-item:last-child {
  border-bottom: none;
}

.action-item:hover {
  background: var(--color-cream, #fdfbf7);
}

.action-icon {
  font-size: 24px;
  margin-right: 14px;
}

.action-info {
  flex: 1;
}

.action-title {
  font-weight: 600;
  color: var(--color-charcoal, #2d2926);
  font-size: 15px;
}

.action-desc {
  color: #999;
  font-size: 12px;
  margin-top: 2px;
}

.action-arrow {
  color: #ccc;
  font-size: 18px;
}

/* 最近佣金 */
.recent-section {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.06));
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--color-charcoal, #2d2926);
}

.view-all {
  font-size: 13px;
  color: var(--color-amber, #d4a574);
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.empty-tip {
  text-align: center;
  padding: 24px 0;
}

.commission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.commission-item:last-child {
  border-bottom: none;
}

.item-title {
  font-size: 14px;
  color: var(--color-charcoal, #2d2926);
  font-weight: 500;
}

.item-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.item-right {
  text-align: right;
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-amount {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-amber, #d4a574);
}

/* 底部按钮 */
.bottom-action {
  text-align: center;
  padding: 16px 0;
}

.withdraw-btn {
  width: 100%;
  max-width: 400px;
  height: 48px;
  font-size: 16px;
  border-radius: var(--radius-lg, 12px);
  background: var(--color-charcoal, #2d2926) !important;
  border-color: var(--color-charcoal, #2d2926) !important;
}

@media (max-width: 600px) {
  .commission-cards {
    grid-template-columns: 1fr;
  }

  .invite-code {
    font-size: 22px;
  }
}
</style>
