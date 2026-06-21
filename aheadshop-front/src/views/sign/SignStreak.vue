<template>
  <div class="sign-streak">
    <div class="streak-header">
      <span class="streak-fire">🔥</span>
      <span class="streak-label">连续签到</span>
    </div>
    <div class="streak-count">
      <span class="streak-num">{{ streak }}</span>
      <span class="streak-unit">天</span>
    </div>

    <!-- 进度条 -->
    <div class="streak-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }" />
      </div>
      <div class="progress-labels">
        <span v-for="milestone in milestones" :key="milestone.day"
              class="milestone" :class="{ reached: streak >= milestone.day }">
          {{ milestone.day }}天
        </span>
      </div>
    </div>

    <!-- 下一目标提示 -->
    <div class="streak-next" v-if="nextMilestone">
      再签 <strong>{{ nextMilestone.day - streak }}</strong> 天可获得
      <span class="bonus-tag">+{{ nextMilestone.bonus }} 积分</span>
    </div>
    <div class="streak-next" v-else>
      🎉 已达成所有连续签到目标！
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  streak: number
}>()

const milestones = [
  { day: 3, bonus: 5 },
  { day: 7, bonus: 10 },
  { day: 14, bonus: 20 },
  { day: 30, bonus: 50 },
]

const nextMilestone = computed(() => {
  return milestones.find(m => m.day > props.streak) || null
})

const progressPercent = computed(() => {
  // 进度条总长度对应 30 天，各里程碑均匀分布在进度条上
  const total = milestones[milestones.length - 1].day // 30
  return Math.min((props.streak / total) * 100, 100)
})
</script>

<style scoped>
.sign-streak {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--color-border);
}

.streak-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.streak-fire {
  font-size: 18px;
}

.streak-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-muted);
  letter-spacing: 0.5px;
}

.streak-count {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 16px;
}

.streak-num {
  font-family: var(--font-display);
  font-size: 40px;
  font-weight: 700;
  color: var(--color-charcoal);
  line-height: 1;
}

.streak-unit {
  font-size: 14px;
  color: var(--color-text-muted);
}

.streak-progress {
  margin-bottom: 12px;
}

.progress-bar {
  height: 6px;
  background: var(--color-surface);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-amber), #e8b888);
  border-radius: 3px;
  transition: width 0.6s ease;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
}

.milestone {
  font-size: 11px;
  color: var(--color-text-muted);
  transition: color 0.2s;
}

.milestone.reached {
  color: var(--color-amber);
  font-weight: 600;
}

.streak-next {
  font-size: 13px;
  color: var(--color-text);
  line-height: 1.6;
}

.streak-next strong {
  color: var(--color-charcoal);
  font-weight: 700;
}

.bonus-tag {
  display: inline-block;
  background: rgba(212, 165, 116, 0.15);
  color: var(--color-amber);
  font-weight: 600;
  padding: 1px 8px;
  border-radius: 10px;
  font-size: 12px;
}
</style>
