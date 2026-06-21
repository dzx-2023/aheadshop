<template>
  <div class="sign-calendar">
    <!-- 月份导航 -->
    <div class="cal-header">
      <button class="cal-nav" @click="prevMonth" :disabled="!canPrev">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <span class="cal-title">{{ year }} 年 {{ month }} 月</span>
      <button class="cal-nav" @click="nextMonth" :disabled="!canNext">
        <el-icon><ArrowRight /></el-icon>
      </button>
    </div>

    <!-- 星期标题 -->
    <div class="cal-weekdays">
      <span v-for="d in weekdays" :key="d">{{ d }}</span>
    </div>

    <!-- 日期网格 -->
    <div class="cal-grid">
      <!-- 前置空白 -->
      <span v-for="i in leadingBlanks" :key="'b'+i" class="cal-day empty" />

      <!-- 日期 -->
      <span
        v-for="day in daysInMonth"
        :key="day"
        class="cal-day"
        :class="{
          signed: isSigned(day),
          today: isToday(day),
          future: isFuture(day),
          retro: isRetro(day),
        }"
        @click="handleDayClick(day)"
      >
        <span class="day-num">{{ day }}</span>
        <span v-if="isSigned(day)" class="day-check">✓</span>
        <span v-if="isToday(day) && !isSigned(day)" class="day-dot" />
      </span>
    </div>

    <!-- 图例 -->
    <div class="cal-legend">
      <span class="legend-item"><span class="legend-dot signed" /> 已签到</span>
      <span class="legend-item"><span class="legend-dot today" /> 今天</span>
      <span class="legend-item"><span class="legend-dot retro" /> 补签</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const props = defineProps<{
  year: number
  month: number
  signDays: number[]
  retroDays?: number[]
  todaySigned: boolean
}>()

const emit = defineEmits<{
  (e: 'update:year', v: number): void
  (e: 'update:month', v: number): void
  (e: 'retro', day: number): void
}>()

const weekdays = ['一', '二', '三', '四', '五', '六', '日']

const today = new Date()
const todayYear = today.getFullYear()
const todayMonth = today.getMonth() + 1
const todayDate = today.getDate()

const daysInMonth = computed(() => new Date(props.year, props.month, 0).getDate())

/** 该月1号是星期几 (0=日,1=一,...6=六) → 转成周一开头 */
const leadingBlanks = computed(() => {
  const day = new Date(props.year, props.month - 1, 1).getDay()
  return day === 0 ? 6 : day - 1
})

const canPrev = computed(() => {
  return !(props.year === todayYear && props.month === todayMonth)
})

const canNext = computed(() => {
  // 允许查看下月(理论上不该有签到数据)
  return true
})

function prevMonth() {
  if (props.month === 1) {
    emit('update:year', props.year - 1)
    emit('update:month', 12)
  } else {
    emit('update:month', props.month - 1)
  }
}

function nextMonth() {
  if (props.month === 12) {
    emit('update:year', props.year + 1)
    emit('update:month', 1)
  } else {
    emit('update:month', props.month + 1)
  }
}

function isSigned(day: number) {
  return props.signDays.includes(day)
}

function isToday(day: number) {
  return props.year === todayYear && props.month === todayMonth && day === todayDate
}

function isFuture(day: number) {
  if (props.year > todayYear) return true
  if (props.year === todayYear && props.month > todayMonth) return true
  if (props.year === todayYear && props.month === todayMonth && day > todayDate) return true
  return false
}

function isRetro(day: number) {
  return props.retroDays?.includes(day) ?? false
}

function handleDayClick(day: number) {
  // 只有过去的未签到日期可以补签
  if (!isFuture(day) && !isSigned(day) && !isToday(day)) {
    emit('retro', day)
  }
}
</script>

<style scoped>
.sign-calendar {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--color-border);
}

.cal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.cal-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.cal-nav {
  width: 32px;
  height: 32px;
  border: none;
  background: var(--color-surface);
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text);
  transition: all 0.2s;
}

.cal-nav:hover:not(:disabled) {
  background: var(--color-charcoal);
  color: var(--color-cream);
}

.cal-nav:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.cal-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 8px;
}

.cal-weekdays span {
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--color-text-muted);
  padding: 4px 0;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.cal-day {
  position: relative;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  cursor: default;
  transition: all 0.2s;
  font-size: 13px;
  color: var(--color-text);
}

.cal-day.empty {
  cursor: default;
}

.cal-day.future {
  opacity: 0.3;
}

.cal-day.today {
  background: rgba(212, 165, 116, 0.12);
  font-weight: 700;
  color: var(--color-amber);
}

.cal-day.signed {
  background: var(--color-charcoal);
  color: var(--color-cream);
  font-weight: 600;
}

.cal-day.signed.today {
  background: var(--color-charcoal);
  color: var(--color-cream);
}

.cal-day:not(.signed):not(.future):not(.empty):hover {
  background: rgba(212, 165, 116, 0.08);
  cursor: pointer;
}

.day-num {
  line-height: 1;
}

.day-check {
  font-size: 9px;
  margin-top: 1px;
  opacity: 0.8;
}

.day-dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--color-amber);
  margin-top: 2px;
}

.cal-legend {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid var(--color-surface);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 3px;
}

.legend-dot.signed {
  background: var(--color-charcoal);
}

.legend-dot.today {
  background: rgba(212, 165, 116, 0.3);
  border: 2px solid var(--color-amber);
}

.legend-dot.retro {
  background: var(--color-amber);
}

/* 响应式 */
@media (max-width: 480px) {
  .sign-calendar {
    padding: 14px;
  }

  .cal-day {
    font-size: 12px;
    border-radius: 8px;
  }
}
</style>
