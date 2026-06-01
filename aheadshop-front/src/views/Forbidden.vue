<template>
  <div class="forbidden">
    <div class="forbidden-content">
      <div class="error-code">403</div>
      <h2>权限不足</h2>
      <p>抱歉，您没有访问该页面的权限。此页面仅限管理员访问。</p>
      <div class="actions">
        <el-button type="primary" @click="router.push('/')">返回首页</el-button>
        <el-button @click="router.back()">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<style scoped>
.forbidden {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}

.forbidden-content {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1;
  margin-bottom: 16px;
}

h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 12px;
}

p {
  font-size: 14px;
  color: #909399;
  margin-bottom: 32px;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>
