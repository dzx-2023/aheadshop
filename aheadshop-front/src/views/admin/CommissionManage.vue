<template>
  <div class="admin-commission">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>佣金记录</span>
          <span class="total-text">共 {{ total }} 条</span>
        </div>
      </template>

      <el-table :data="records" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="distributorId" label="分销商ID" width="120" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="buyerId" label="买家ID" width="100" />
        <el-table-column label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.orderAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="佣金比例" width="100">
          <template #default="{ row }">
            {{ (row.commissionRate * 100).toFixed(0) }}%
          </template>
        </el-table-column>
        <el-table-column label="佣金金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.commissionAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type as any" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="结算时间" width="180">
          <template #default="{ row }">
            {{ row.settleTime?.replace('T', ' ')?.substring(0, 19) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createTime?.replace('T', ' ')?.substring(0, 19) || '-' }}
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
import request from '@/api/request'

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '冻结中', type: 'warning' },
  1: { label: '已结算', type: 'success' },
  2: { label: '已取消', type: 'info' },
}

const records = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 15
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/admin/distribution/commission/list', {
      params: { pageNum: pageNum.value, pageSize }
    })
    records.value = res?.records || []
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
.admin-commission {
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  color: #d4a574;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
