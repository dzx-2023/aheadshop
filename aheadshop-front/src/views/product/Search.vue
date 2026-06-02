<template>
  <div class="search-page">
    <div class="search-inner">
      <!-- 搜索头部 -->
      <div class="search-header">
        <h2 class="search-title">
          <template v-if="keyword">
            搜索 "<span class="keyword">{{ keyword }}</span>"
          </template>
          <template v-else>全部商品</template>
        </h2>
        <p v-if="!loading" class="result-count">共找到 {{ total }} 件商品</p>
      </div>

      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="sort-options">
          <span
            v-for="opt in sortOptions"
            :key="opt.value"
            class="sort-item"
            :class="{ active: currentSort === opt.value }"
            @click="changeSort(opt.value)"
          >
            {{ opt.label }}
          </span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div v-if="products.length" class="product-grid">
        <ProductCard v-for="item in products" :key="item.id" :product="item" />
      </div>
      <div v-else-if="loading" class="product-grid">
        <div v-for="i in 8" :key="i" class="skeleton-card">
          <div class="skeleton-img"></div>
          <div class="skeleton-text"></div>
          <div class="skeleton-text short"></div>
        </div>
      </div>
      <div v-else class="empty-state">
        <el-empty :description="`未找到与 '${keyword}' 相关的商品`">
          <el-button type="primary" @click="$router.push('/')">回到首页</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchProducts"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getSpuList } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import type { SpuPageItem } from '@/types/product'

const route = useRoute()

const loading = ref(true)
const keyword = ref('')
const products = ref<SpuPageItem[]>([])
const currentSort = ref('default')
const pageNum = ref(1)
const pageSize = 20
const total = ref(0)

const sortOptions = [
  { label: '综合', value: 'default' },
  { label: '销量', value: 'sales' },
  { label: '价格↑', value: 'price_asc' },
  { label: '价格↓', value: 'price_desc' },
]

function changeSort(sort: string) {
  currentSort.value = sort
  pageNum.value = 1
  fetchProducts()
}

async function fetchProducts() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: pageNum.value,
      pageSize,
      status: 1,
    }
    if (keyword.value) params.keyword = keyword.value

    const res: any = await getSpuList(params as any)
    const pageData = res.data
    products.value = pageData?.records || []
    total.value = pageData?.total || 0
  } catch {
    products.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  keyword.value = (route.query.keyword as string) || ''
  fetchProducts()
})

watch(
  () => route.query.keyword,
  (newKeyword) => {
    keyword.value = (newKeyword as string) || ''
    pageNum.value = 1
    fetchProducts()
  }
)
</script>

<style scoped>
.search-page {
  padding: 24px 0 60px;
}

.search-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-header {
  margin-bottom: 24px;
}

.search-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.keyword {
  color: var(--color-amber);
}

.result-count {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
  margin-top: 6px;
}

.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--color-border);
}

.sort-options {
  display: flex;
  gap: 4px;
}

.sort-item {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
  padding: 6px 14px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-item:hover {
  color: var(--color-charcoal);
  background: var(--color-surface);
}

.sort-item.active {
  color: #fff;
  background: var(--color-charcoal);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.skeleton-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  padding-bottom: 16px;
}

.skeleton-img {
  aspect-ratio: 1;
  background: linear-gradient(90deg, #f5f3ef 25%, #efecea 50%, #f5f3ef 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-text {
  height: 14px;
  margin: 14px 16px 0;
  background: #f5f3ef;
  border-radius: 4px;
}

.skeleton-text.short {
  width: 60%;
  height: 12px;
  margin-top: 8px;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.empty-state {
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
}
</style>
