<template>
  <div class="dashboard">
    <!-- 数据卡片 -->
    <div class="stat-cards">
      <div class="stat-card" v-for="card in statCards" :key="card.label">
        <div class="stat-icon" :style="{ background: card.bg }">
          <el-icon :size="24"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- 图表 + 热销排行 -->
    <div class="chart-row">
      <!-- 销售趋势 -->
      <div class="chart-card">
        <div class="card-header">
          <h4>近 7 天销售趋势</h4>
        </div>
        <div ref="chartRef" class="chart-container" />
      </div>

      <!-- 热销商品 -->
      <div class="chart-card hot-products-card">
        <div class="card-header">
          <h4>热销商品 Top 10</h4>
        </div>
        <el-table :data="hotProducts" stripe size="small" max-height="360">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column label="商品" min-width="200">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image :src="row.mainImage" fit="cover" class="product-img" />
                <span class="product-name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="sales" label="销量" width="80" sortable />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, markRaw } from 'vue'
import * as echarts from 'echarts'
import { ShoppingCart, Money, UserFilled, Van } from '@element-plus/icons-vue'
import { getDashboardOverview } from '@/api/admin'

// ========== 数据卡片 ==========
const statCards = reactive([
  { label: '今日订单数', value: '0', icon: markRaw(ShoppingCart), bg: 'linear-gradient(135deg, #667eea, #764ba2)' },
  { label: '今日销售额', value: '¥0', icon: markRaw(Money), bg: 'linear-gradient(135deg, #f093fb, #f5576c)' },
  { label: '新增用户', value: '0', icon: markRaw(UserFilled), bg: 'linear-gradient(135deg, #4facfe, #00f2fe)' },
  { label: '待发货', value: '0', icon: markRaw(Van), bg: 'linear-gradient(135deg, #43e97b, #38f9d7)' },
])

// ========== ECharts ==========
const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = (dates: string[], values: number[]) => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>销售额：¥{c}',
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true,
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#ddd' } },
      axisLabel: { color: '#666' },
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: {
        color: '#666',
        formatter: (v: number) => v >= 10000 ? (v / 10000).toFixed(1) + 'w' : v >= 1000 ? (v / 1000).toFixed(1) + 'k' : String(v),
      },
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3, color: '#667eea' },
        itemStyle: { color: '#667eea' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
            { offset: 1, color: 'rgba(102, 126, 234, 0.02)' },
          ]),
        },
        data: values,
      },
    ],
  })
}

// ========== 热销商品 ==========
const hotProducts = ref<any[]>([])

// ========== 加载数据 ==========
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDashboardOverview()
    const data = res.data

    // 更新卡片
    statCards[0].value = String(data.todayOrderCount ?? 0)
    statCards[1].value = '¥' + (data.todaySalesAmount ?? 0)
    statCards[2].value = String(data.todayNewUsers ?? 0)
    statCards[3].value = String(data.pendingShipCount ?? 0)

    // 销售趋势
    const trend = data.salesTrend || []
    const dates = trend.map((t: any) => t.date || t.day || '')
    const values = trend.map((t: any) => Number(t.amount ?? t.total ?? 0))
    await nextTick()
    initChart(dates.length ? dates : ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], values.length ? values : [0, 0, 0, 0, 0, 0, 0])

    // 热销商品
    hotProducts.value = data.hotProducts || []
  } catch {
    // 接口失败时展示空数据
    await nextTick()
    initChart(['周一', '周二', '周三', '周四', '周五', '周六', '周日'], [0, 0, 0, 0, 0, 0, 0])
  } finally {
    loading.value = false
  }
}

// ========== 窗口自适应 ==========
const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 数据卡片 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

/* 图表行 */
.chart-row {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 16px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.card-header {
  margin-bottom: 16px;
}

.card-header h4 {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 320px;
}

/* 热销商品 */
.product-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-img {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  flex-shrink: 0;
}

.product-name {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-row {
    grid-template-columns: 1fr;
  }
}
</style>
