<template>
  <div class="admin-withdraw">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>提现审核</span>
          <span class="total-text">共 {{ total }} 条</span>
        </div>
      </template>

      <el-table :data="records" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="distributorId" label="分销商ID" width="120" />
        <el-table-column label="提现金额" width="140">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type as any" size="small">
              {{ statusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核时间" width="180">
          <template #default="{ row }">
            {{ row.auditTime?.replace('T', ' ')?.substring(0, 19) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">
            {{ row.createTime?.replace('T', ' ')?.substring(0, 19) || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button link type="success" size="small" @click="handleAudit(row, true)">
                通过
              </el-button>
              <el-button link type="danger" size="small" @click="showRejectDialog(row)">
                拒绝
              </el-button>
            </template>
            <span v-else class="processed-text">已处理</span>
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

    <!-- 拒绝弹窗 -->
    <el-dialog v-model="rejectVisible" title="拒绝提现" width="420px">
      <el-form label-width="60px">
        <el-form-item label="金额">
          <span class="reject-amount">¥{{ rejectTarget?.amount?.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="原因">
          <el-input
            v-model="rejectRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject" :loading="rejecting">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminWithdrawList, auditWithdraw } from '@/api/adminDistribution'

const statusMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已打款', type: 'success' },
  3: { label: '已拒绝', type: 'danger' },
}

const records = ref<any[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)

const rejectVisible = ref(false)
const rejectTarget = ref<any>(null)
const rejectRemark = ref('')
const rejecting = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getAdminWithdrawList({ pageNum: pageNum.value, pageSize })
    records.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row: any, approved: boolean) => {
  const action = approved ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定${action}该笔 ¥${row.amount?.toFixed(2)} 的提现申请？`, '确认操作')
    await auditWithdraw({ id: row.id, approved })
    ElMessage.success(`已${action}`)
    loadData()
  } catch {
    // cancel or error
  }
}

const showRejectDialog = (row: any) => {
  rejectTarget.value = row
  rejectRemark.value = ''
  rejectVisible.value = true
}

const handleReject = async () => {
  if (!rejectTarget.value) return
  rejecting.value = true
  try {
    await auditWithdraw({
      id: rejectTarget.value.id,
      approved: false,
      remark: rejectRemark.value || undefined,
    })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    loadData()
  } catch {
    // ignore
  } finally {
    rejecting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.admin-withdraw {
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
  font-weight: 700;
  color: #d4a574;
  font-size: 15px;
}

.processed-text {
  font-size: 12px;
  color: #999;
}

.reject-amount {
  font-size: 18px;
  font-weight: 700;
  color: #d4a574;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
