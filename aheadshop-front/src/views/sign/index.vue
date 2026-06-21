<template>
  <div class="sign-page">
    <div class="sign-container">
      <!-- 顶部标题栏 -->
      <div class="sign-hero">
        <div class="hero-left">
          <h1>每日签到</h1>
          <p>坚持签到，赢取积分奖励</p>
        </div>
        <button
          class="sign-btn"
          :class="{ signed: status?.todaySigned, animating: btnAnimating }"
          :disabled="status?.todaySigned || signing"
          @click="handleCheckIn"
        >
          <template v-if="signing">
            <span class="btn-loading" />
          </template>
          <template v-else-if="status?.todaySigned">
            ✅ 已签到
          </template>
          <template v-else>
            ✨ 立即签到
          </template>
        </button>
      </div>

      <!-- 签到成功弹幕 -->
      <Transition name="toast">
        <div v-if="showToast" class="sign-toast">
          <span class="toast-icon">🎉</span>
          <span class="toast-text">签到成功！获得 <strong>+{{ toastPoints }}</strong> 积分</span>
          <span v-if="toastBonus > 0" class="toast-bonus">（含连续奖励 +{{ toastBonus }}）</span>
        </div>
      </Transition>

      <!-- 主内容区 -->
      <div class="sign-body">
        <!-- 左栏: 日历 + 积分流水 -->
        <div class="sign-left">
          <SignCalendar
            v-model:year="calYear"
            v-model:month="calMonth"
            :sign-days="status?.signDays || []"
            :today-signed="status?.todaySigned || false"
            @retro="handleRetro"
          />

          <!-- 积分流水 -->
          <div class="points-log-section">
            <div class="log-header">
              <h3>积分明细</h3>
            </div>
            <div v-if="logLoading" v-loading="true" style="height: 80px" />
            <div v-else-if="logList.length === 0" class="log-empty">
              <el-text type="info" size="small">暂无积分记录</el-text>
            </div>
            <div v-else class="log-list">
              <div v-for="log in logList" :key="log.id" class="log-item">
                <div class="log-left">
                  <span class="log-desc">{{ log.description }}</span>
                  <span class="log-time">{{ formatTime(log.createTime) }}</span>
                </div>
                <span class="log-points" :class="{ negative: log.points < 0 }">
                  {{ log.points > 0 ? '+' : '' }}{{ log.points }}
                </span>
              </div>
            </div>
            <el-pagination
              v-if="logTotal > logPageSize"
              v-model:current-page="logPage"
              :page-size="logPageSize"
              :total="logTotal"
              layout="prev, pager, next"
              small
              @current-change="loadLog"
            />
          </div>
        </div>

        <!-- 右栏: 连续签到 + 积分 + 排行榜 -->
        <div class="sign-right">
          <SignStreak :streak="streak" />
          <SignPoints
            :total-points="points?.totalPoints || 0"
            :month-points="points?.monthPoints || 0"
            :rank="points?.rank || 0"
          />
          <SignLeaderboard ref="leaderboardRef" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import SignCalendar from './SignCalendar.vue'
import SignStreak from './SignStreak.vue'
import SignPoints from './SignPoints.vue'
import SignLeaderboard from './SignLeaderboard.vue'
import { checkIn, retroSign, getSignStatus, getStreak, getPoints, getPointsLog } from '@/api/sign'
import type { SignStatusVO, PointsVO, PointsLogVO } from '@/types/sign'

const now = new Date()
const calYear = ref(now.getFullYear())
const calMonth = ref(now.getMonth() + 1)

const status = ref<SignStatusVO | null>(null)
const streak = ref(0)
const points = ref<PointsVO | null>(null)
const leaderboardRef = ref<InstanceType<typeof SignLeaderboard>>()

// 签到按钮
const signing = ref(false)
const btnAnimating = ref(false)

// 签到成功提示
const showToast = ref(false)
const toastPoints = ref(0)
const toastBonus = ref(0)

// 积分流水
const logLoading = ref(false)
const logList = ref<PointsLogVO[]>([])
const logTotal = ref(0)
const logPage = ref(1)
const logPageSize = 8

// ── 加载数据 ──

async function loadStatus() {
  try {
    const res = await getSignStatus(calYear.value, calMonth.value)
    status.value = res.data
  } catch {}
}

async function loadStreak() {
  try {
    const res = await getStreak()
    streak.value = res.data ?? 0
  } catch {}
}

async function loadPoints() {
  try {
    const res = await getPoints()
    points.value = res.data
  } catch {}
}

async function loadLog() {
  logLoading.value = true
  try {
    const res = await getPointsLog(logPage.value, logPageSize)
    logList.value = res.data?.records || []
    logTotal.value = res.data?.total || 0
  } catch {
    logList.value = []
  } finally {
    logLoading.value = false
  }
}

function refreshAll() {
  loadStatus()
  loadStreak()
  loadPoints()
  loadLog()
  leaderboardRef.value?.refresh()
}

// 切换月份时重新加载日历
watch([calYear, calMonth], () => {
  loadStatus()
})

// ── 签到 ──

async function handleCheckIn() {
  if (status.value?.todaySigned || signing.value) return
  signing.value = true
  try {
    const res = await checkIn()
    const data = res.data
    toastPoints.value = data.points
    toastBonus.value = data.bonusPoints

    // 按钮动画
    btnAnimating.value = true
    setTimeout(() => { btnAnimating.value = false }, 600)

    // 显示提示
    showToast.value = true
    setTimeout(() => { showToast.value = false }, 3000)

    // 刷新数据
    refreshAll()
  } catch {
    // interceptor 已处理错误提示
  } finally {
    signing.value = false
  }
}

// ── 补签 ──

async function handleRetro(day: number) {
  const dateStr = `${calYear.value}-${String(calMonth.value).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  try {
    await ElMessageBox.confirm(
      `补签 ${dateStr} 需要消耗 20 积分，确定补签吗？`,
      '补签确认',
      { confirmButtonText: '确定补签', cancelButtonText: '取消', type: 'info' }
    )
    await retroSign(dateStr)
    ElMessage.success('补签成功')
    refreshAll()
  } catch {
    // cancelled or error
  }
}

// ── 工具 ──

function formatTime(time: string): string {
  if (!time) return ''
  const d = new Date(time)
  const m = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  const h = d.getHours().toString().padStart(2, '0')
  const min = d.getMinutes().toString().padStart(2, '0')
  return `${m}-${day} ${h}:${min}`
}

// ── 初始化 ──

onMounted(() => {
  refreshAll()
})
</script>

<style scoped>
.sign-page {
  min-height: calc(100vh - 64px);
  padding: 24px;
}

.sign-container {
  max-width: 960px;
  margin: 0 auto;
}

/* ── 顶部标题栏 ── */
.sign-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 28px 32px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid var(--color-border);
}

.hero-left h1 {
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 700;
  color: var(--color-charcoal);
  margin-bottom: 4px;
}

.hero-left p {
  font-size: 14px;
  color: var(--color-text-muted);
}

.sign-btn {
  padding: 14px 32px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  font-family: var(--font-body);
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  background: var(--color-charcoal);
  color: var(--color-cream);
  box-shadow: 0 4px 12px rgba(45, 41, 38, 0.15);
}

.sign-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(45, 41, 38, 0.2);
}

.sign-btn:active:not(:disabled) {
  transform: translateY(0);
}

.sign-btn.signed {
  background: var(--color-surface);
  color: var(--color-text-muted);
  cursor: default;
  box-shadow: none;
}

.sign-btn:disabled {
  cursor: not-allowed;
}

.sign-btn.animating {
  animation: signPop 0.6s ease;
}

@keyframes signPop {
  0% { transform: scale(1); }
  30% { transform: scale(1.1); }
  60% { transform: scale(0.95); }
  100% { transform: scale(1); }
}

.btn-loading {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ── 签到成功提示 ── */
.sign-toast {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 20px;
  background: linear-gradient(135deg, var(--color-charcoal), #3a3532);
  color: var(--color-cream);
  border-radius: 12px;
  margin-bottom: 20px;
  font-size: 14px;
  box-shadow: 0 4px 16px rgba(45, 41, 38, 0.2);
}

.toast-icon {
  font-size: 18px;
}

.toast-text strong {
  color: var(--color-amber);
  font-size: 16px;
}

.toast-bonus {
  font-size: 12px;
  opacity: 0.8;
}

.toast-enter-active {
  animation: toastIn 0.4s ease;
}

.toast-leave-active {
  animation: toastOut 0.3s ease;
}

@keyframes toastIn {
  from { opacity: 0; transform: translateY(-12px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes toastOut {
  from { opacity: 1; transform: translateY(0); }
  to { opacity: 0; transform: translateY(-12px); }
}

/* ── 主内容区 ── */
.sign-body {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.sign-left {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sign-right {
  width: 300px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ── 积分流水 ── */
.points-log-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--color-border);
}

.log-header {
  margin-bottom: 12px;
}

.log-header h3 {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.log-empty {
  padding: 16px 0;
  text-align: center;
}

.log-list {
  display: flex;
  flex-direction: column;
}

.log-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-surface);
}

.log-item:last-child {
  border-bottom: none;
}

.log-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.log-desc {
  font-size: 13px;
  color: var(--color-charcoal);
}

.log-time {
  font-size: 12px;
  color: var(--color-text-muted);
}

.log-points {
  font-size: 15px;
  font-weight: 700;
  color: var(--color-amber);
  font-family: var(--font-display);
}

.log-points.negative {
  color: var(--color-text-muted);
}

/* ── 分页 ── */
.points-log-section :deep(.el-pagination) {
  margin-top: 12px;
  justify-content: center;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .sign-page {
    padding: 16px;
  }

  .sign-hero {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 20px;
  }

  .hero-left h1 {
    font-size: 22px;
  }

  .sign-body {
    flex-direction: column;
  }

  .sign-right {
    width: 100%;
  }
}
</style>
