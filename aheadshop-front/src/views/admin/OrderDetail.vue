<template>
  <div class="admin-order-detail">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>订单详情</span>
          <el-button @click="$router.push('/admin/orders')">返回列表</el-button>
        </div>
      </template>

      <template v-if="order">
        <!-- 订单状态 -->
        <div class="status-bar">
          <el-tag :type="statusTagType(order.status)" size="large">
            {{ OrderStatusText[order.status] || '未知' }}
          </el-tag>
          <span class="order-no">订单号：{{ order.orderNo }}</span>
        </div>

        <!-- 基本信息 -->
        <el-descriptions title="订单信息" :column="2" border style="margin-bottom: 24px">
          <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ formatTime(order.payTime) }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ formatTime(order.shipTime) }}</el-descriptions-item>
          <el-descriptions-item label="快递单号">{{ order.trackingNumber || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 收货人信息 -->
        <el-descriptions title="收货人信息" :column="2" border style="margin-bottom: 24px">
          <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ order.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item v-if="order.remark" label="备注" :span="2">{{ order.remark }}</el-descriptions-item>
        </el-descriptions>

        <!-- 商品明细 -->
        <h4 style="margin-bottom: 12px">商品明细</h4>
        <el-table :data="order.items" border style="width: 100%; margin-bottom: 24px">
          <el-table-column label="图片" width="80">
            <template #default="{ row }">
              <el-image
                :src="row.skuImage"
                fit="cover"
                style="width: 50px; height: 50px; border-radius: 4px"
                v-if="row.skuImage"
              >
                <template #error>
                  <div style="width: 50px; height: 50px; background: #f5f7fa; display: flex; align-items: center; justify-content: center; border-radius: 4px;">
                    <el-icon size="20" color="#c0c4cc"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="skuName" label="商品名称" min-width="200" show-overflow-tooltip />
          <el-table-column prop="specs" label="规格" width="150" />
          <el-table-column label="单价" width="100">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">
              <span style="color: #f56c6c; font-weight: 600">¥{{ row.totalPrice?.toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>

        <!-- 金额信息 -->
        <el-descriptions title="金额信息" :column="2" border style="margin-bottom: 24px">
          <el-descriptions-item label="商品总额">¥{{ order.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="运费">¥{{ order.freightAmount?.toFixed(2) || '0.00' }}</el-descriptions-item>
          <el-descriptions-item label="优惠">-¥{{ order.discountAmount?.toFixed(2) || '0.00' }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span style="color: #f56c6c; font-weight: 700; font-size: 16px">¥{{ order.payAmount?.toFixed(2) }}</span>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 退款信息 -->
        <template v-if="refund">
          <el-descriptions title="退款信息" :column="2" border style="margin-bottom: 24px">
            <el-descriptions-item label="退款单号">{{ refund.refundNo }}</el-descriptions-item>
            <el-descriptions-item label="退款类型">{{ RefundTypeText[refund.refundType] || '仅退款' }}</el-descriptions-item>
            <el-descriptions-item label="退款金额">
              <span style="color: #f56c6c">¥{{ refund.refundAmount?.toFixed(2) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="退款原因">{{ refund.refundReason || '-' }}</el-descriptions-item>
            <el-descriptions-item label="退款状态">
              <el-tag :type="refund.status === 3 ? 'success' : refund.status === 4 ? 'danger' : 'warning'" size="small">
                {{ RefundStatusText[refund.status] || '未知' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item v-if="refund.auditRemark" label="审批备注" :span="2">
              {{ refund.auditRemark }}
            </el-descriptions-item>
          </el-descriptions>

          <!-- 退款审批操作 -->
          <div v-if="refund.status === 0" class="refund-actions">
            <el-button type="success" @click="handleAuditRefund(true)">同意退款</el-button>
            <el-button type="danger" @click="handleAuditRefund(false)">拒绝退款</el-button>
          </div>
        </template>

        <!-- 发货操作 -->
        <div v-if="order.status === OrderStatus.PAID" class="ship-actions">
          <el-button type="primary" @click="openShipDialog">发货</el-button>
        </div>
      </template>
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialogVisible" title="发货" width="420px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="快递单号">
          <el-input v-model="shipTrackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>

    <!-- 退款审批弹窗 -->
    <el-dialog v-model="auditDialogVisible" :title="auditApproved ? '同意退款' : '拒绝退款'" width="420px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="审批备注">
          <el-input v-model="auditRemark" type="textarea" :rows="3" placeholder="请输入备注（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button :type="auditApproved ? 'success' : 'danger'" :loading="auditLoading" @click="submitAudit">
          {{ auditApproved ? '确认同意' : '确认拒绝' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getAdminOrderDetail, shipOrder, getRefundByOrderNo, auditRefund } from '@/api/admin'
import type { OrderDetail, RefundItem } from '@/types/order'
import { OrderStatus, OrderStatusText, RefundStatusText, RefundTypeText } from '@/types/order'

const route = useRoute()

const loading = ref(false)
const order = ref<OrderDetail | null>(null)
const refund = ref<RefundItem | null>(null)

// 发货
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const shipTrackingNumber = ref('')

// 退款审批
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditApproved = ref(false)
const auditRemark = ref('')
const auditRefundId = ref(0)

function statusTagType(status: number): '' | 'success' | 'info' | 'warning' | 'danger' {
  const map: Record<number, '' | 'success' | 'info' | 'warning' | 'danger'> = {
    0: 'warning',
    1: '',
    2: 'success',
    3: 'success',
    4: 'success',
    5: 'info',
    6: 'danger',
    7: 'info',
  }
  return map[status] || 'info'
}

function formatTime(time: string | null | undefined): string {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function fetchOrderDetail() {
  const orderNo = route.params.orderNo as string
  if (!orderNo) return

  loading.value = true
  try {
    const res: any = await getAdminOrderDetail(orderNo)
    order.value = res.data

    // 尝试获取退款信息
    try {
      const refundRes: any = await getRefundByOrderNo(orderNo)
      refund.value = refundRes.data
    } catch {
      // 没有退款记录，静默
      refund.value = null
    }
  } catch {
    ElMessage.error('加载订单信息失败')
  } finally {
    loading.value = false
  }
}

function openShipDialog() {
  shipTrackingNumber.value = ''
  shipDialogVisible.value = true
}

async function handleShip() {
  shipLoading.value = true
  try {
    await shipOrder(order.value!.orderNo, shipTrackingNumber.value || undefined)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrderDetail()
  } catch {
    // 错误由拦截器处理
  } finally {
    shipLoading.value = false
  }
}

function handleAuditRefund(approved: boolean) {
  if (!refund.value) return
  auditApproved.value = approved
  auditRefundId.value = refund.value.id
  auditRemark.value = ''
  auditDialogVisible.value = true
}

async function submitAudit() {
  auditLoading.value = true
  try {
    await auditRefund(auditRefundId.value, auditApproved.value, auditRemark.value || undefined)
    ElMessage.success(auditApproved.value ? '已同意退款' : '已拒绝退款')
    auditDialogVisible.value = false
    fetchOrderDetail()
  } catch {
    // 错误由拦截器处理
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.admin-order-detail {
  max-width: 1100px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.order-no {
  font-size: 14px;
  color: #606266;
}

.ship-actions,
.refund-actions {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
