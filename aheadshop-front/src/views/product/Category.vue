<template>
  <div class="category-page">
    <div class="category-layout">
      <!-- 左侧分类树 -->
      <aside class="category-sidebar">
        <h3 class="sidebar-title">商品分类</h3>
        <div class="category-tree">
          <template v-for="cat in categories" :key="cat.id">
            <div class="tree-group">
              <div
                class="tree-node tree-level-1"
                :class="{ active: selectedCatId === cat.id }"
                @click="selectCategory(cat.id)"
              >
                <span>{{ cat.name }}</span>
                <el-icon
                  v-if="cat.children?.length"
                  class="expand-icon"
                  :class="{ expanded: expandedCatIds.has(cat.id) }"
                ><ArrowRight /></el-icon>
              </div>
              <div v-if="cat.children?.length && expandedCatIds.has(cat.id)" class="tree-children">
                <template v-for="child in cat.children" :key="child.id">
                  <div
                    class="tree-node tree-level-2"
                    :class="{ active: selectedCatId === child.id }"
                    @click="selectCategory(child.id)"
                  >
                    <span>{{ child.name }}</span>
                    <el-icon
                      v-if="child.children?.length"
                      class="expand-icon"
                      :class="{ expanded: expandedCatIds.has(child.id) }"
                    ><ArrowRight /></el-icon>
                  </div>
                  <div v-if="child.children?.length && expandedCatIds.has(child.id)" class="tree-children">
                    <div
                      v-for="grandchild in child.children"
                      :key="grandchild.id"
                      class="tree-node tree-level-3"
                      :class="{ active: selectedCatId === grandchild.id }"
                      @click="selectCategory(grandchild.id)"
                    >
                      {{ grandchild.name }}
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </template>
        </div>
        <div v-if="!categories.length && !loading" class="empty-tree">暂无分类</div>
      </aside>

      <!-- 右侧商品列表 -->
      <main class="category-main">
        <div class="main-header">
          <h2 class="page-title">{{ currentCatName || '全部商品' }}</h2>
          <div class="sort-bar">
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
          <el-empty description="该分类下暂无商品" />
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
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategoryTree, getSpuList } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import { ArrowRight } from '@element-plus/icons-vue'
import type { CategoryTree, SpuPageItem } from '@/types/product'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const categories = ref<CategoryTree[]>([])
const products = ref<SpuPageItem[]>([])
const selectedCatId = ref<number | null>(null)
const expandedCatIds = ref<Set<number>>(new Set())
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

const currentCatName = computed(() => {
  if (!selectedCatId.value) return '全部商品'
  const find = (list: CategoryTree[]): string | undefined => {
    for (const c of list) {
      if (c.id === selectedCatId.value) return c.name
      if (c.children) {
        const found = find(c.children)
        if (found) return found
      }
    }
  }
  return find(categories.value) || '全部商品'
})

/** 在树中查找节点及其父链 */
function findNodeInTree(
  list: CategoryTree[],
  targetId: number,
  parents: CategoryTree[] = [],
): { node: CategoryTree; parents: CategoryTree[] } | null {
  for (const node of list) {
    if (node.id === targetId) return { node, parents }
    if (node.children?.length) {
      const found = findNodeInTree(node.children, targetId, [...parents, node])
      if (found) return found
    }
  }
  return null
}

function selectCategory(catId: number) {
  const found = findNodeInTree(categories.value, catId)
  const hasChildren = found?.node.children && found.node.children.length > 0

  if (hasChildren) {
    // 有子节点：只展开/折叠，不筛选商品
    const ids = new Set(expandedCatIds.value)
    if (ids.has(catId)) {
      ids.delete(catId)
    } else {
      ids.add(catId)
    }
    expandedCatIds.value = ids
    return
  }

  // 叶子节点：选中筛选商品（再次点击取消）
  selectedCatId.value = selectedCatId.value === catId ? null : catId
  pageNum.value = 1
  router.replace({ query: selectedCatId.value ? { id: String(selectedCatId.value) } : {} })
  fetchProducts()
}

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
    if (selectedCatId.value) params.categoryId = selectedCatId.value

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

onMounted(async () => {
  // 从 URL 读取分类 ID
  if (route.query.id) {
    selectedCatId.value = Number(route.query.id)
  }

  try {
    const res: any = await getCategoryTree()
    categories.value = res.data || []

    // 如果有选中的分类，自动展开其父链
    if (selectedCatId.value) {
      const found = findNodeInTree(categories.value, selectedCatId.value)
      if (found?.parents.length) {
        expandedCatIds.value = new Set(found.parents.map(p => p.id))
      }
    }
  } catch {
    // 静默处理
  }

  fetchProducts()
})

// 监听路由参数变化
watch(
  () => route.query.id,
  (newId) => {
    if (newId && Number(newId) !== selectedCatId.value) {
      selectedCatId.value = Number(newId)
      pageNum.value = 1
      fetchProducts()
    }
  }
)
</script>

<style scoped>
.category-page {
  padding: 24px 0 60px;
}

.category-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 24px;
}

/* ── 左侧分类 ── */
.category-sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  padding: 20px;
  position: sticky;
  top: 88px;
  max-height: calc(100vh - 112px);
  overflow-y: auto;
}

.sidebar-title {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
}

.tree-group {
  margin-bottom: 4px;
}

.tree-node {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: var(--font-body);
}

/* 一级分类 */
.tree-level-1 {
  padding: 10px 12px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
}

/* 二级分类 */
.tree-level-2 {
  padding: 8px 12px;
  font-size: 13px;
  color: var(--color-text);
}

/* 三级分类 */
.tree-level-3 {
  padding: 6px 12px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.tree-node:hover {
  background: var(--color-surface);
  color: var(--color-charcoal);
}

.tree-node.active {
  background: var(--color-amber);
  color: #fff;
}

.tree-node.active .expand-icon {
  color: #fff;
}

.expand-icon {
  font-size: 12px;
  color: var(--color-text-muted);
  transition: transform 0.2s;
  flex-shrink: 0;
}

.expand-icon.expanded {
  transform: rotate(90deg);
}

.tree-children {
  padding-left: 12px;
}

.empty-tree {
  text-align: center;
  padding: 24px 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

/* ── 右侧商品 ── */
.category-main {
  flex: 1;
  min-width: 0;
}

.main-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.sort-bar {
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
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

/* 骨架屏 */
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

/* ── 响应式 ── */
@media (max-width: 768px) {
  .category-layout {
    flex-direction: column;
  }

  .category-sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
