<template>
  <div class="admin-refunds">
    <!-- 搜索区 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="(label, key) in RefundStatusText" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 退款表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>退款管理</span>
        </div>
      </template>

      <el-table :data="refunds" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="refundNo" label="退款单号" width="200" show-overflow-tooltip />
        <el-table-column prop="orderNo" label="订单号" width="200" show-overflow-tooltip />
        <el-table-column label="退款类型" width="100">
          <template #default="{ row }">
            {{ RefundTypeText[row.refundType] || '仅退款' }}
          </template>
        </el-table-column>
        <el-table-column label="退款金额" width="120">
          <template #default="{ row }">
            <span style="color: #c44536; font-weight: 600">¥{{ row.refundAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ RefundStatusText[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button link type="success" size="small" @click="openAuditDialog(row, true)">同意</el-button>
              <el-button link type="danger" size="small" @click="openAuditDialog(row, false)">拒绝</el-button>
            </template>
            <el-button link type="primary" size="small" @click="goOrderDetail(row.orderNo)">查看订单</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchRefunds"
        />
      </div>
    </el-card>

    <!-- 审批弹窗 -->
    <el-dialog v-model="auditDialogVisible" :title="auditApproved ? '同意退款' : '拒绝退款'" width="420px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="退款单号">
          <el-input :model-value="auditRefund?.refundNo" disabled />
        </el-form-item>
        <el-form-item label="退款金额">
          <span style="color: #c44536; font-weight: 600">¥{{ auditRefund?.refundAmount?.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="审批备注">
          <el-input v-model="auditRemark" type="textarea" :rows="3" placeholder="请输入备注（可选）" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button :type="auditApproved ? 'success' : 'danger'" :loading="auditLoading" @click="handleAudit">
          {{ auditApproved ? '确认同意' : '确认拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRefundList, auditRefund as auditRefundApi } from '@/api/admin'
import { RefundStatusText, RefundTypeText } from '@/types/order'
import type { RefundItem } from '@/types/order'

const router = useRouter()

const loading = ref(false)
const refunds = ref<RefundItem[]>([])
const pageNum = ref(1)
const pageSize = 20
const total = ref(0)

const searchForm = reactive({
  status: null as number | null,
})

// 审批弹窗
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditApproved = ref(false)
const auditRefund = ref<RefundItem | null>(null)
const auditRemark = ref('')

function statusTagType(status: number): '' | 'success' | 'info' | 'warning' | 'danger' {
  const map: Record<number, '' | 'success' | 'info' | 'warning' | 'danger'> = {
    0: 'warning',
    1: 'info',
    2: 'warning',
    3: 'success',
    4: 'danger',
  }
  return map[status] || 'info'
}

function formatTime(time: string): string {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function fetchRefunds() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: pageNum.value,
      pageSize,
    }
    if (searchForm.status !== null) params.status = searchForm.status

    const res: any = await getRefundList(params)
    const pageData = res.data
    refunds.value = pageData?.records || []
    total.value = pageData?.total || 0
  } catch {
    refunds.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchRefunds()
}

function handleReset() {
  searchForm.status = null
  pageNum.value = 1
  fetchRefunds()
}

function openAuditDialog(refund: RefundItem, approved: boolean) {
  auditRefund.value = refund
  auditApproved.value = approved
  auditRemark.value = ''
  auditDialogVisible.value = true
}

async function handleAudit() {
  if (!auditRefund.value) return
  auditLoading.value = true
  try {
    await auditRefundApi(auditRefund.value.id, auditApproved.value, auditRemark.value || undefined)
    ElMessage.success(auditApproved.value ? '已同意退款' : '已拒绝退款')
    auditDialogVisible.value = false
    fetchRefunds()
  } catch {
    // 错误已在拦截器处理
  } finally {
    auditLoading.value = false
  }
}

function goOrderDetail(orderNo: string) {
  router.push(`/admin/orders/${orderNo}`)
}

onMounted(() => {
  fetchRefunds()
})
</script>

<style scoped>
.admin-refunds {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card :deep(.el-card__body) {
  padding-bottom: 2px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
