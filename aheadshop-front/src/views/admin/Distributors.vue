<template>
  <div class="admin-distributors">
    <!-- 搜索区 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" @submit.prevent="handleSearch">
        <el-form-item label="关键词">
          <el-input
            v-model="keyword"
            placeholder="邀请码/用户ID"
            clearable
            style="width: 200px"
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="statusFilter" placeholder="全部" clearable style="width: 120px" @change="handleSearch">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 分销商表格 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>分销商列表</span>
          <span class="total-text">共 {{ total }} 条</span>
        </div>
      </template>

      <el-table :data="distributors" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="inviteCode" label="邀请码" width="130">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.inviteCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="80">
          <template #default="{ row }">
            <el-tag>Lv.{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="累计佣金" width="130">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalCommission?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="可提现" width="130">
          <template #default="{ row }">
            <span class="amount success">¥{{ row.availableCommission?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="冻结" width="130">
          <template #default="{ row }">
            <span class="amount warn">¥{{ row.frozenCommission?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalTeamCount" label="团队人数" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDistributorList, updateDistributorStatus } from '@/api/adminDistribution'

interface Distributor {
  userId: number
  inviteCode: string
  level: number
  totalCommission: number
  availableCommission: number
  frozenCommission: number
  totalTeamCount: number
  status: number
  createTime: string
}

const distributors = ref<Distributor[]>([])
const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<number | undefined>(undefined)
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const params: Record<string, any> = { pageNum: pageNum.value, pageSize }
    if (keyword.value) params.keyword = keyword.value
    if (statusFilter.value !== undefined) params.status = statusFilter.value
    const res: any = await getDistributorList(params as any)
    distributors.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  keyword.value = ''
  statusFilter.value = undefined
  handleSearch()
}

const handleToggleStatus = async (row: Distributor) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定${action}该分销商？`, '确认操作')
    await updateDistributorStatus(row.userId, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch {
    // cancel or error
  }
}

onMounted(loadData)
</script>

<style scoped>
.admin-distributors {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card :deep(.el-card__body) {
  padding-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-text {
  font-size: 13px;
  color: #999;
  font-weight: normal;
}

.amount {
  font-weight: 600;
}

.amount.success {
  color: #67c23a;
}

.amount.warn {
  color: #e6a23c;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
