<template>
  <div class="admin-orders">
    <!-- 搜索区 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 130px">
            <el-option v-for="(label, key) in OrderStatusText" :key="key" :label="label" :value="Number(key)" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单表格 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
        </div>
      </template>

      <el-table :data="orders" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="200" show-overflow-tooltip />
        <el-table-column label="金额" width="110">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600">¥{{ row.payAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ OrderStatusText[row.status] || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="trackingNumber" label="快递单号" width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.trackingNumber || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="下单时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/admin/orders/${row.orderNo}`)">详情</el-button>
            <el-button
              v-if="row.status === OrderStatus.PAID"
              link
              type="success"
              size="small"
              @click="openShipDialog(row)"
            >
              发货
            </el-button>
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
          @current-change="fetchOrders"
        />
      </div>
    </el-card>

    <!-- 发货弹窗 -->
    <el-dialog v-model="shipDialogVisible" title="发货" width="420px" :close-on-click-modal="false">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="订单号">
          <el-input :model-value="shipForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNumber" placeholder="请输入快递单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipLoading" @click="handleShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrders, shipOrder } from '@/api/admin'
import type { OrderPageItem } from '@/types/order'
import { OrderStatus, OrderStatusText } from '@/types/order'

const loading = ref(false)
const orders = ref<OrderPageItem[]>([])
const pageNum = ref(1)
const pageSize = 20
const total = ref(0)

const searchForm = reactive({
  status: null as number | null,
})

// 发货弹窗
const shipDialogVisible = ref(false)
const shipLoading = ref(false)
const shipForm = reactive({
  orderNo: '',
  trackingNumber: '',
})

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

function formatTime(time: string): string {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

async function fetchOrders() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: pageNum.value,
      pageSize,
    }
    if (searchForm.status !== null) params.status = searchForm.status

    const res: any = await getAdminOrders(params)
    const pageData = res.data
    orders.value = pageData?.records || []
    total.value = pageData?.total || 0
  } catch {
    orders.value = []
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchOrders()
}

function handleReset() {
  searchForm.status = null
  pageNum.value = 1
  fetchOrders()
}

function openShipDialog(row: OrderPageItem) {
  shipForm.orderNo = row.orderNo
  shipForm.trackingNumber = ''
  shipDialogVisible.value = true
}

async function handleShip() {
  shipLoading.value = true
  try {
    await shipOrder(shipForm.orderNo, shipForm.trackingNumber || undefined)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrders()
  } catch {
    // 错误由拦截器处理
  } finally {
    shipLoading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.admin-orders {
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
