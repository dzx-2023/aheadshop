<template>
  <div class="product-form">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑商品' : '新增商品' }}</span>
          <el-button @click="$router.push('/admin/products')">返回列表</el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 900px"
        v-loading="pageLoading"
      >
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>

        <el-form-item label="商品分类" prop="categoryId">
          <el-cascader
            v-model="form.categoryPath"
            :options="categoryOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择分类（需选到最末级）"
            clearable
            style="width: 300px"
            @change="handleCategoryChange"
          />
        </el-form-item>

        <el-form-item label="品牌" prop="brandId">
          <el-select v-model="form.brandId" placeholder="选择品牌" clearable style="width: 300px">
            <el-option v-for="b in brandOptions" :key="b.id" :label="b.name" :value="b.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="副标题" prop="subtitle">
          <el-input v-model="form.subtitle" placeholder="请输入副标题" maxlength="200" show-word-limit />
        </el-form-item>

        <!-- 图片 -->
        <el-divider content-position="left">商品图片</el-divider>

        <el-form-item label="主图">
          <el-upload
            :action="''"
            :http-request="handleUploadMain"
            :show-file-list="false"
            accept="image/*"
          >
            <el-image
              v-if="form.mainImage"
              :src="form.mainImage"
              fit="cover"
              style="width: 120px; height: 120px; border-radius: 6px; border: 1px solid #dcdfe6"
            />
            <div v-else class="upload-placeholder">
              <el-icon size="24"><Plus /></el-icon>
              <span>上传主图</span>
            </div>
          </el-upload>
        </el-form-item>

        <el-form-item label="多图">
          <el-upload
            :action="''"
            :http-request="handleUploadImages"
            :file-list="imagesFileList"
            list-type="picture-card"
            accept="image/*"
            :on-remove="handleRemoveImage"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <!-- 详情 -->
        <el-divider content-position="left">商品详情</el-divider>

        <el-form-item label="详情描述">
          <el-input
            v-model="form.detail"
            type="textarea"
            :rows="6"
            placeholder="请输入商品详情描述"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>

        <!-- SKU -->
        <el-divider content-position="left">SKU 规格</el-divider>

        <el-form-item>
          <el-button type="primary" plain @click="addSku">
            <el-icon><Plus /></el-icon>添加 SKU
          </el-button>
        </el-form-item>

        <el-table :data="form.skus" border style="width: 100%; margin-bottom: 20px">
          <el-table-column label="SKU 名称" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.skuName" placeholder="如: 128G 黑色" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="规格" width="150">
            <template #default="{ row }">
              <el-input v-model="row.specs" placeholder="如: 颜色:黑" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="价格(¥)" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.price" :min="0" :precision="2" :controls="false" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="原价(¥)" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.originalPrice" :min="0" :precision="2" :controls="false" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="库存" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.stock" :min="0" :controls="false" size="small" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="70" align="center">
            <template #default="{ $index }">
              <el-button link type="danger" size="small" @click="removeSku($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 提交 -->
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ isEdit ? '保存修改' : '提交' }}
          </el-button>
          <el-button @click="$router.push('/admin/products')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'
import {
  getCategoryTree,
  getBrandsByCategory,
  getSpuDetail,
  createSpu,
  updateSpu,
  uploadProductImage,
} from '@/api/product'
import type { CategoryTree, BrandItem, SpuSaveData } from '@/types/product'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const pageLoading = ref(false)
const submitting = ref(false)

const formRef = ref<FormInstance>()
const categoryOptions = ref<CategoryTree[]>([])
const brandOptions = ref<BrandItem[]>([])
const imagesFileList = ref<{ name: string; url: string }[]>([])

const form = reactive({
  categoryId: null as number | null,
  categoryPath: [] as number[],
  brandId: null as number | null,
  name: '',
  subtitle: '',
  mainImage: '',
  images: '',
  detail: '',
  skus: [] as {
    skuName: string
    specs: string
    price: number
    originalPrice: number | null
    stock: number
    image: string
  }[],
})

const rules: FormRules = {
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
}

function addSku() {
  form.skus.push({
    skuName: '',
    specs: '',
    price: 0,
    originalPrice: null,
    stock: 0,
    image: '',
  })
}

function removeSku(index: number) {
  form.skus.splice(index, 1)
}

// 分类变化时加载品牌
async function handleCategoryChange(val: any) {
  form.brandId = null
  brandOptions.value = []
  if (!val || !Array.isArray(val) || val.length === 0) {
    form.categoryId = null
    return
  }
  form.categoryId = val[val.length - 1]
  try {
    const res: any = await getBrandsByCategory(form.categoryId!)
    brandOptions.value = res.data || []
  } catch {
    brandOptions.value = []
  }
}

// 上传主图
async function handleUploadMain(options: UploadRequestOptions) {
  try {
    const res: any = await uploadProductImage(options.file)
    form.mainImage = res.data
    ElMessage.success('主图上传成功')
  } catch {
    ElMessage.error('上传失败')
  }
}

// 上传多图
async function handleUploadImages(options: UploadRequestOptions) {
  try {
    const res: any = await uploadProductImage(options.file)
    const url = res.data
    imagesFileList.value.push({ name: options.file.name, url })
    form.images = imagesFileList.value.map((f) => f.url).join(',')
    ElMessage.success('图片上传成功')
  } catch {
    ElMessage.error('上传失败')
  }
}

function handleRemoveImage(file: { name: string; url: string }) {
  imagesFileList.value = imagesFileList.value.filter((f) => f.url !== file.url)
  form.images = imagesFileList.value.map((f) => f.url).join(',')
}

// 提交
async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  if (form.skus.length === 0) {
    ElMessage.warning('请至少添加一个 SKU')
    return
  }
  for (const sku of form.skus) {
    if (!sku.skuName) {
      ElMessage.warning('SKU 名称不能为空')
      return
    }
    if (!sku.price || sku.price <= 0) {
      ElMessage.warning('SKU 价格必须大于 0')
      return
    }
    if (sku.stock === null || sku.stock === undefined) {
      ElMessage.warning('SKU 库存不能为空')
      return
    }
  }

  if (!form.categoryId) {
    ElMessage.warning('请选择商品分类')
    return
  }

  const data: SpuSaveData = {
    name: form.name,
    subtitle: form.subtitle || undefined,
    categoryId: form.categoryId,
    brandId: form.brandId || undefined,
    mainImage: form.mainImage || undefined,
    images: form.images || undefined,
    detail: form.detail || undefined,
    skus: form.skus.map((s) => ({
      skuName: s.skuName,
      specs: s.specs || undefined,
      price: s.price,
      originalPrice: s.originalPrice || undefined,
      stock: s.stock,
      image: s.image || undefined,
    })),
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateSpu(Number(route.params.id), data)
      ElMessage.success('修改成功')
    } else {
      await createSpu(data)
      ElMessage.success('新增成功')
    }
    router.push('/admin/products')
  } catch {
    // 错误由拦截器处理
  } finally {
    submitting.value = false
  }
}

/** 在分类树中查找目标 ID 的完整路径 */
function findCategoryPath(tree: CategoryTree[], targetId: number): number[] | null {
  for (const node of tree) {
    if (node.id === targetId) return [node.id]
    if (node.children?.length) {
      const childPath = findCategoryPath(node.children, targetId)
      if (childPath) return [node.id, ...childPath]
    }
  }
  return null
}

// 加载分类树
async function fetchCategories() {
  try {
    const res: any = await getCategoryTree()
    categoryOptions.value = res.data || []
  } catch {
    // 静默
  }
}

// 编辑模式：加载商品详情
async function loadProduct(id: number) {
  pageLoading.value = true
  try {
    const res: any = await getSpuDetail(id)
    const detail = res.data
    if (!detail) return

    form.name = detail.name
    form.subtitle = detail.subtitle || ''
    form.categoryId = detail.categoryId
    form.brandId = detail.brandId || null
    form.mainImage = detail.mainImage || ''
    form.images = detail.images || ''
    form.detail = detail.detail || ''

    // 解析多图
    if (detail.images) {
      imagesFileList.value = detail.images.split(',').map((url: string, i: number) => ({
        name: `image-${i}`,
        url: url.trim(),
      }))
    }

    // 设置分类路径（级联选择器需要完整路径才能正确回显）
    if (detail.categoryId && categoryOptions.value.length) {
      form.categoryPath = findCategoryPath(categoryOptions.value, detail.categoryId) || [detail.categoryId]
    }

    // 加载品牌
    if (detail.categoryId) {
      const brandRes: any = await getBrandsByCategory(detail.categoryId)
      brandOptions.value = brandRes.data || []
    }

    // SKU
    form.skus = (detail.skus || []).map((s: any) => ({
      skuName: s.skuName,
      specs: s.specs || '',
      price: s.price,
      originalPrice: s.originalPrice || null,
      stock: s.stock,
      image: s.image || '',
    }))
  } catch {
    ElMessage.error('加载商品信息失败')
  } finally {
    pageLoading.value = false
  }
}

onMounted(async () => {
  await fetchCategories()
  if (isEdit.value) {
    loadProduct(Number(route.params.id))
  } else {
    // 新增模式默认一个空 SKU
    addSku()
  }
})
</script>

<style scoped>
.product-form {
  max-width: 1100px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #8c939d;
  font-size: 12px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.upload-placeholder:hover {
  border-color: #409eff;
  color: #409eff;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list__item) {
  width: 100px;
  height: 100px;
}
</style>
