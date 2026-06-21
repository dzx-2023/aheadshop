<template>
  <div class="sign-points">
    <div class="points-header">
      <span class="points-icon">💰</span>
      <span class="points-label">我的积分</span>
    </div>

    <div class="points-main">
      <div class="points-total">
        <span class="points-num">{{ totalPoints }}</span>
        <span class="points-desc">总积分</span>
      </div>
      <div class="points-divider" />
      <div class="points-month">
        <span class="points-num small">{{ monthPoints }}</span>
        <span class="points-desc">本月获得</span>
      </div>
      <div class="points-divider" />
      <div class="points-rank">
        <span class="points-num small">{{ rank > 0 ? '#' + rank : '-' }}</span>
        <span class="points-desc">排名</span>
      </div>
    </div>

    <div class="points-footer">
      <router-link to="/sign" class="points-link">
        查看积分明细 →
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  totalPoints: number
  monthPoints: number
  rank: number
}>()
</script>

<style scoped>
.sign-points {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--color-border);
}

.points-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
}

.points-icon {
  font-size: 18px;
}

.points-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-muted);
  letter-spacing: 0.5px;
}

.points-main {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 14px;
}

.points-divider {
  width: 1px;
  height: 36px;
  background: var(--color-surface);
}

.points-total,
.points-month,
.points-rank {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.points-num {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  color: var(--color-charcoal);
  line-height: 1;
}

.points-num.small {
  font-size: 22px;
}

.points-desc {
  font-size: 12px;
  color: var(--color-text-muted);
}

.points-footer {
  border-top: 1px solid var(--color-surface);
  padding-top: 10px;
  text-align: center;
}

.points-link {
  font-size: 13px;
  color: var(--color-amber);
  font-weight: 500;
  transition: opacity 0.2s;
}

.points-link:hover {
  opacity: 0.7;
}
</style>
