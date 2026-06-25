<template>
  <div class="distribution-page">
    <div class="dist-container">
      <div class="page-header">
        <h1>我的团队</h1>
        <p>共 <strong>{{ total }}</strong> 位成员</p>
      </div>

      <div class="table-card">
        <el-table :data="teamList" v-loading="loading" empty-text="暂无团队成员" stripe>
          <el-table-column label="成员" min-width="200">
            <template #default="{ row }">
              <div class="member-cell">
                <el-avatar :size="36" :src="row.avatar || undefined">
                  {{ row.nickname?.[0] || '?' }}
                </el-avatar>
                <div class="member-info">
                  <div class="member-name">{{ row.nickname || '用户' + row.userId }}</div>
                  <div class="member-id">ID: {{ row.userId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="加入时间" prop="joinTime" width="200">
            <template #default="{ row }">
              {{ row.joinTime?.replace('T', ' ')?.substring(0, 19) || '-' }}
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="loadData"
          />
        </div>
      </div>

      <div class="back-link">
        <router-link to="/distribution">← 返回分销中心</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTeamList } from '@/api/distribution'
import type { TeamMember } from '@/types/distribution'

const teamList = ref<TeamMember[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTeamList({ pageNum: pageNum.value, pageSize })
    teamList.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    // ignore
  } finally {
    loading.value = false
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

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-family: var(--font-display, 'Playfair Display', serif);
  font-size: 28px;
  color: var(--color-charcoal, #2d2926);
  margin: 0;
}

.page-header p {
  color: #666;
  margin: 4px 0 0;
  font-size: 14px;
}

.table-card {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.06));
}

.member-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-name {
  font-weight: 600;
  color: var(--color-charcoal, #2d2926);
  font-size: 14px;
}

.member-id {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.back-link {
  margin-top: 20px;
  text-align: center;
}

.back-link a {
  color: var(--color-amber, #d4a574);
  text-decoration: none;
  font-size: 14px;
}

.back-link a:hover {
  text-decoration: underline;
}
</style>
