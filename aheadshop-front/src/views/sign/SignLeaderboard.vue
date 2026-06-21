<template>
  <div class="sign-leaderboard">
    <div class="lb-header">
      <span class="lb-icon">🏆</span>
      <span class="lb-title">积分排行榜</span>
      <div class="lb-tabs">
        <button :class="{ active: activeTab === 'total' }" @click="switchTab('total')">总排行</button>
        <button :class="{ active: activeTab === 'month' }" @click="switchTab('month')">月排行</button>
      </div>
    </div>

    <div v-if="loading" class="lb-loading" v-loading="true" style="height: 120px" />

    <div v-else-if="list.length === 0" class="lb-empty">
      <el-text type="info" size="small">暂无排行数据</el-text>
    </div>

    <div v-else class="lb-list">
      <div v-for="item in list" :key="item.userId" class="lb-item" :class="{ 'is-top': item.rank <= 3 }">
        <!-- 排名 -->
        <div class="lb-rank" :class="'rank-' + item.rank">
          <template v-if="item.rank === 1">🥇</template>
          <template v-else-if="item.rank === 2">🥈</template>
          <template v-else-if="item.rank === 3">🥉</template>
          <template v-else>{{ item.rank }}</template>
        </div>

        <!-- 头像 -->
        <el-avatar :size="32" :src="item.avatar" class="lb-avatar">
          {{ (item.nickname || '?')[0] }}
        </el-avatar>

        <!-- 昵称 -->
        <span class="lb-name">{{ item.nickname }}</span>

        <!-- 积分 -->
        <span class="lb-points">{{ item.points }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLeaderboard } from '@/api/sign'
import type { LeaderboardItemVO } from '@/types/sign'

const activeTab = ref<'total' | 'month'>('total')
const loading = ref(false)
const list = ref<LeaderboardItemVO[]>([])

async function load() {
  loading.value = true
  try {
    const res = await getLeaderboard(activeTab.value)
    list.value = res.data || []
  } catch {
    list.value = []
  } finally {
    loading.value = false
  }
}

function switchTab(tab: 'total' | 'month') {
  activeTab.value = tab
  load()
}

onMounted(load)

defineExpose({ refresh: load })
</script>

<style scoped>
.sign-leaderboard {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--color-border);
}

.lb-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.lb-icon {
  font-size: 18px;
}

.lb-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-muted);
  letter-spacing: 0.5px;
  flex: 1;
}

.lb-tabs {
  display: flex;
  gap: 4px;
  background: var(--color-surface);
  border-radius: 8px;
  padding: 3px;
}

.lb-tabs button {
  border: none;
  background: transparent;
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-muted);
  padding: 4px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

.lb-tabs button.active {
  background: #fff;
  color: var(--color-charcoal);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.lb-empty {
  padding: 20px 0;
  text-align: center;
}

.lb-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.lb-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 8px;
  transition: background 0.15s;
}

.lb-item:hover {
  background: var(--color-surface);
}

.lb-item.is-top {
  padding: 10px;
}

.lb-rank {
  width: 28px;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.lb-rank.rank-1,
.lb-rank.rank-2,
.lb-rank.rank-3 {
  font-size: 18px;
}

.lb-avatar {
  flex-shrink: 0;
  background: var(--color-amber);
  color: var(--color-cream);
  font-size: 13px;
}

.lb-name {
  flex: 1;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-charcoal);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.lb-points {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-amber);
  font-family: var(--font-display);
}
</style>
