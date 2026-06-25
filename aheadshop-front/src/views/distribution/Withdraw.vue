<template>
  <div class="distribution-page">
    <div class="dist-container">
      <div class="page-header">
        <div class="header-left">
          <h1>提现</h1>
          <p>可提现余额：<strong>¥{{ availableBalance.toFixed(2) }}</strong></p>
        </div>
        <el-button type="primary" @click="showApply = true" :disabled="availableBalance <= 0">
          申请提现
        </el-button>
      </div>

      <!-- 提现记录 -->
      <div class="table-card">
        <h3 class="section-title">提现记录</h3>
        <el-table :data="withdrawList" v-loading="loading" empty-text="暂无提现记录" stripe>
          <el-table-column label="金额" width="140">
            <template #default="{ row }">
              <span class="amount-highlight">¥{{ row.amount?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="WithdrawalStatusMap[row.status]?.type as any" size="small">
                {{ WithdrawalStatusMap[row.status]?.label }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="审核时间" min-width="180">
            <template #default="{ row }">
              {{ row.auditTime?.replace('T', ' ')?.substring(0, 19) || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="150">
            <template #default="{ row }">
              {{ row.remark || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="申请时间" prop="createTime" width="180">
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
      </div>

      <div class="back-link">
        <router-link to="/distribution">← 返回分销中心</router-link>
      </div>
    </div>

    <!-- 申请提现弹窗 -->
    <el-dialog v-model="showApply" title="申请提现" width="420px" :close-on-click-modal="false">
      <el-form :model="applyForm" label-width="80px">
        <el-form-item label="可提现">
          <span class="balance-text">¥{{ availableBalance.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="提现金额">
          <el-input-number
            v-model="applyForm.amount"
            :min="100"
            :max="availableBalance"
            :step="100"
            :precision="2"
            style="width: 100%"
          />
          <div class="form-tip">最低提现金额 100 元</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApply = false">取消</el-button>
        <el-button type="primary" @click="handleApply" :loading="submitting">
          确认提现
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDistributionInfo, getWithdrawList, applyWithdraw } from '@/api/distribution'
import type { WithdrawalRecord } from '@/types/distribution'
import { WithdrawalStatusMap } from '@/types/distribution'

const withdrawList = ref<WithdrawalRecord[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = 10
const total = ref(0)
const availableBalance = ref(0)
const showApply = ref(false)
const submitting = ref(false)
const applyForm = ref({ amount: 100 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getWithdrawList({ pageNum: pageNum.value, pageSize })
    withdrawList.value = res?.records || []
    total.value = res?.total || 0
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

const loadBalance = async () => {
  try {
    const res = await getDistributionInfo()
    availableBalance.value = res?.availableCommission || 0
  } catch {
    // ignore
  }
}

const handleApply = async () => {
  if (applyForm.value.amount < 100) {
    ElMessage.warning('最低提现金额为 100 元')
    return
  }
  if (applyForm.value.amount > availableBalance.value) {
    ElMessage.warning('提现金额不能超过可提现余额')
    return
  }
  submitting.value = true
  try {
    await applyWithdraw({ amount: applyForm.value.amount })
    ElMessage.success('提现申请已提交')
    showApply.value = false
    applyForm.value.amount = 100
    loadBalance()
    loadData()
  } catch {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadBalance()
  loadData()
})
</script>

<style scoped>
.distribution-page {
  min-height: 100vh;
  background: var(--color-cream, #fdfbf7);
  padding: 24px 0 80px;
}

.dist-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.page-header p strong {
  color: var(--color-amber, #d4a574);
  font-size: 18px;
}

.table-card {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0,0,0,0.06));
}

.section-title {
  font-size: 16px;
  color: var(--color-charcoal, #2d2926);
  margin: 0 0 16px;
}

.amount-highlight {
  font-weight: 700;
  color: var(--color-amber, #d4a574);
  font-size: 15px;
}

.balance-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-amber, #d4a574);
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
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
