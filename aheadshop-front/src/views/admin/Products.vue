<template>
  <div class="admin-products">
    <!-- 搜索区 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="商品名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="分类">
          <el-cascader
            v-model="searchForm.categoryPath"
            :options="categoryOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="选择分类"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>商品列表</span>
          <el-button type="primary" @click="$router.push('/admin/products/add')">
            <el-icon><Plus /></el-icon>新增商品
          </el-button>
        </div>
      </template>

      <el-table :data="products" v-loading="loading" stripe style="width: 100%">
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image
              :src="row.mainImage"
              fit="cover"
              style="width: 50px; height: 50px; border-radius: 4px"
              v-if="row.mainImage"
            >
              <template #error>
                <div style="width: 50px; height: 50px; background: #f5f7fa; display: flex; align-items: center; justify-content: center; border-radius: 4px;">
                  <el-icon size="20" color="#c0c4cc"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600">¥{{ row.minPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="$router.push(`/admin/products/edit/${row.id}`)">编辑</el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleShelf(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
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
          @current-change="fetchProducts"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { getSpuList, getCategoryTree, onShelf, offShelf } from '@/api/product'
import type { SpuPageItem, CategoryTree } from '@/types/product'

const loading = ref(false)
const products = ref<SpuPageItem[]>([])
const categoryOptions = ref<CategoryTree[]>([])
const pageNum = ref(1)
const pageSize = 20
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  categoryPath: [] as number[],
  status: null as number | null,
})

async function fetchProducts() {
  loading.value = true
  try {
    const params: Record<string, any> = {
      pageNum: pageNum.value,
      pageSize,
    }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.categoryPath.length) params.categoryId = searchForm.categoryPath[searchForm.categoryPath.length - 1]
    if (searchForm.status !== null) params.status = searchForm.status

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

async function fetchCategories() {
  try {
    const res: any = await getCategoryTree()
    categoryOptions.value = res.data || []
  } catch {
    // 静默
  }
}

function handleSearch() {
  pageNum.value = 1
  fetchProducts()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.categoryPath = []
  searchForm.status = null
  pageNum.value = 1
  fetchProducts()
}

async function handleToggleShelf(row: SpuPageItem) {
  const action = row.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确认${action}商品「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    if (row.status === 1) {
      await offShelf(row.id)
    } else {
      await onShelf(row.id)
    }
    ElMessage.success(`${action}成功`)
    fetchProducts()
  } catch {
    // 取消或失败
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.admin-products {
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
