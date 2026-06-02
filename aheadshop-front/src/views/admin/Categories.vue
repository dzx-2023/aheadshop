<template>
  <div class="admin-categories">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="openDialog(null)">
            <el-icon><Plus /></el-icon>新增分类
          </el-button>
        </div>
      </template>

      <el-table
        :data="treeData"
        v-loading="loading"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children' }"
        style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" min-width="200" />
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <span v-if="row.icon">{{ row.icon }}</span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="level" label="层级" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.level }}级</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDialog(null, row.id)">新增子分类</el-button>
            <el-button link type="primary" size="small" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
      width="460px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="父级分类">
          <el-cascader
            v-model="form.parentPath"
            :options="treeData"
            :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }"
            placeholder="无（顶级分类）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="图标名称或 emoji（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategoryTree, addCategory, updateCategory, deleteCategory } from '@/api/admin'
import type { CategoryTree } from '@/types/product'

const loading = ref(false)
const treeData = ref<CategoryTree[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const form = reactive({
  parentPath: [] as number[],
  name: '',
  icon: '',
  sortOrder: 0,
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
}

async function fetchTree() {
  loading.value = true
  try {
    const res: any = await getCategoryTree()
    treeData.value = res.data || []
  } catch {
    treeData.value = []
  } finally {
    loading.value = false
  }
}

function openDialog(row?: CategoryTree | null, parentId?: number) {
  isEdit.value = !!row
  editingId.value = row?.id || null
  form.parentPath = []
  form.name = ''
  form.icon = ''
  form.sortOrder = 0

  if (row) {
    // 编辑：回填数据
    form.name = row.name
    form.icon = row.icon || ''
    form.sortOrder = row.sortOrder ?? 0
    // 查找父级路径
    if (row.parentId && row.parentId !== 0) {
      const path = findPath(treeData.value, row.parentId)
      if (path) form.parentPath = path
    }
  } else if (parentId) {
    // 新增子分类
    const path = findPath(treeData.value, parentId)
    if (path) form.parentPath = path
  }

  dialogVisible.value = true
}

function findPath(nodes: CategoryTree[], targetId: number): number[] | null {
  for (const node of nodes) {
    if (node.id === targetId) return [node.id]
    if (node.children?.length) {
      const sub = findPath(node.children, targetId)
      if (sub) return [node.id, ...sub]
    }
  }
  return null
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    const parentId = form.parentPath.length ? form.parentPath[form.parentPath.length - 1] : 0
    if (isEdit.value && editingId.value) {
      await updateCategory({
        id: editingId.value,
        parentId,
        name: form.name,
        icon: form.icon || undefined,
        sortOrder: form.sortOrder,
      })
      ElMessage.success('修改成功')
    } else {
      await addCategory({
        parentId,
        name: form.name,
        icon: form.icon || undefined,
        sortOrder: form.sortOrder,
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchTree()
  } catch {
    // 错误由拦截器处理
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: CategoryTree) {
  try {
    await ElMessageBox.confirm(`确认删除分类「${row.name}」？删除后子分类也会被删除。`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    fetchTree()
  } catch {
    // 取消
  }
}

onMounted(fetchTree)
</script>

<style scoped>
.admin-categories {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
