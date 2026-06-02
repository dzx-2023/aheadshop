<template>
  <div class="admin-backgrounds">
    <!-- 上传区域 -->
    <el-card shadow="never" class="upload-card">
      <template #header>
        <div class="card-header">
          <span>上传背景图</span>
          <span class="hint">支持 JPG / PNG / WebP，建议 1920×1080 以上</span>
        </div>
      </template>
      <el-upload
        class="bg-upload"
        :show-file-list="false"
        :http-request="handleUpload"
        accept="image/*"
        drag
      >
        <div class="upload-inner">
          <el-icon class="upload-icon"><Plus /></el-icon>
          <p class="upload-text">拖拽图片到此处，或 <em>点击上传</em></p>
        </div>
      </el-upload>
    </el-card>

    <!-- 全局设置 -->
    <el-card shadow="never" class="settings-card">
      <template #header>
        <span>显示设置</span>
      </template>
      <el-form label-width="100px">
        <el-form-item label="背景透明度">
          <div class="opacity-control">
            <el-slider
              v-model="opacity"
              :min="0.05"
              :max="0.6"
              :step="0.05"
              :format-tooltip="(v: number) => `${Math.round(v * 100)}%`"
              @change="handleOpacityChange"
            />
            <span class="opacity-value">{{ Math.round(opacity * 100) }}%</span>
          </div>
          <div class="opacity-hint">背景以氛围层形式覆盖在所有内容上方，建议 10%~20%</div>
        </el-form-item>
        <el-form-item label="轮播间隔">
          <div class="opacity-control">
            <el-slider
              v-model="interval"
              :min="5"
              :max="120"
              :step="5"
              :format-tooltip="(v: number) => `${v} 秒`"
              @change="handleIntervalChange"
            />
            <span class="opacity-value">{{ interval }}s</span>
          </div>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图片列表 -->
    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="card-header">
          <span>背景图列表</span>
          <span class="count">共 {{ backgrounds.length }} 张</span>
        </div>
      </template>

      <div v-loading="loading">
        <div v-if="backgrounds.length > 0" class="bg-grid">
          <div v-for="bg in backgrounds" :key="bg.id" class="bg-item">
            <div class="bg-preview">
              <img :src="bg.imageUrl" :alt="`背景图 ${bg.id}`" />
              <div class="bg-badge" :class="{ disabled: bg.enabled === 0 }">
                {{ bg.enabled === 1 ? '启用' : '禁用' }}
              </div>
            </div>
            <div class="bg-actions">
              <div class="sort-control">
                <span class="sort-label">排序</span>
                <el-input-number
                  v-model="bg.sortOrder"
                  :min="0"
                  :max="999"
                  size="small"
                  controls-position="right"
                  @change="(val: number) => handleSortChange(bg.id, val)"
                />
              </div>
              <div class="action-buttons">
                <el-switch
                  v-model="bg.enabled"
                  :active-value="1"
                  :inactive-value="0"
                  active-text="启用"
                  inactive-text="禁用"
                  inline-prompt
                  size="small"
                  @change="(val: number) => handleEnabledChange(bg.id, val)"
                />
                <el-button
                  type="danger"
                  :icon="Delete"
                  size="small"
                  plain
                  @click="handleDelete(bg)"
                />
              </div>
            </div>
          </div>
        </div>

        <el-empty v-else-if="!loading" description="暂无背景图，上传一张试试" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getBackgroundList,
  uploadBackground,
  deleteBackground,
  updateBackgroundSort,
  toggleBackground,
} from '@/api/admin'

interface Background {
  id: number
  imageUrl: string
  sortOrder: number
  enabled: number
}

const loading = ref(false)
const backgrounds = ref<Background[]>([])
const opacity = ref(Number(localStorage.getItem('bg_opacity') || '0.15'))
const interval = ref(Number(localStorage.getItem('bg_interval') || '30'))

async function fetchBackgrounds() {
  loading.value = true
  try {
    const res: any = await getBackgroundList()
    backgrounds.value = res.data || []
  } catch {
    backgrounds.value = []
  } finally {
    loading.value = false
  }
}

async function handleUpload(options: any) {
  const file = options.file
  if (!file) return

  // 验证文件类型和大小
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB')
    return
  }

  try {
    await uploadBackground(file)
    ElMessage.success('上传成功')
    fetchBackgrounds()
  } catch {
    // 错误已在拦截器处理
  }
}

async function handleDelete(bg: Background) {
  try {
    await ElMessageBox.confirm('确定删除该背景图？删除后不可恢复。', '删除确认', {
      type: 'warning',
      confirmButtonText: '删除',
      confirmButtonClass: 'el-button--danger',
    })
    await deleteBackground(bg.id)
    ElMessage.success('已删除')
    fetchBackgrounds()
  } catch {
    // 取消或失败
  }
}

async function handleSortChange(id: number, sortOrder: number) {
  try {
    await updateBackgroundSort(id, sortOrder)
  } catch {
    // 错误已在拦截器处理
  }
}

async function handleEnabledChange(id: number, enabled: number) {
  try {
    await toggleBackground(id, enabled)
    ElMessage.success(enabled === 1 ? '已启用' : '已禁用')
  } catch {
    // 回滚
    fetchBackgrounds()
  }
}

function handleOpacityChange(val: number) {
  localStorage.setItem('bg_opacity', String(val))
  window.dispatchEvent(new CustomEvent('bg-opacity-change', { detail: { opacity: val } }))
}

function handleIntervalChange(val: number) {
  localStorage.setItem('bg_interval', String(val))
  window.dispatchEvent(new CustomEvent('bg-interval-change', { detail: { interval: val } }))
}

onMounted(() => {
  fetchBackgrounds()
})
</script>

<style scoped>
.admin-backgrounds {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.hint {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 400;
}

.count {
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 400;
}

/* ── 上传区域 ── */
.bg-upload {
  width: 100%;
}

:deep(.bg-upload .el-upload) {
  width: 100%;
}

:deep(.bg-upload .el-upload-dragger) {
  width: 100%;
  padding: 40px 20px;
  border: 2px dashed var(--color-border);
  border-radius: 12px;
  background: var(--color-surface);
  transition: all 0.3s ease;
}

:deep(.bg-upload .el-upload-dragger:hover) {
  border-color: var(--color-amber);
  background: rgba(212, 165, 116, 0.04);
}

.upload-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-icon {
  font-size: 40px;
  color: var(--color-amber);
}

.upload-text {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
  margin: 0;
}

.upload-text em {
  color: var(--color-amber);
  font-style: normal;
  cursor: pointer;
}

/* ── 设置卡片 ── */
.opacity-control {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 400px;
}

.opacity-control :deep(.el-slider) {
  flex: 1;
}

.opacity-value {
  font-family: 'DM Sans', monospace;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-charcoal);
  min-width: 40px;
  text-align: right;
}

.interval-text {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-charcoal);
}

.opacity-hint {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  margin-top: 4px;
}

/* ── 图片网格 ── */
.bg-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

.bg-item {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--color-border);
  background: #fff;
  transition: all 0.3s ease;
}

.bg-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.bg-preview {
  position: relative;
  aspect-ratio: 16 / 9;
  overflow: hidden;
}

.bg-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.bg-item:hover .bg-preview img {
  transform: scale(1.05);
}

.bg-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 2px 10px;
  border-radius: 20px;
  font-family: var(--font-body);
  font-size: 11px;
  font-weight: 600;
  background: rgba(82, 196, 26, 0.9);
  color: #fff;
  backdrop-filter: blur(4px);
}

.bg-badge.disabled {
  background: rgba(140, 140, 140, 0.9);
}

.bg-actions {
  padding: 12px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sort-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.sort-control :deep(.el-input-number) {
  width: 80px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .bg-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }

  .bg-actions {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
