<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="brand-panel">
      <div class="brand-content">
        <div class="brand-pattern"></div>
        <h1 class="brand-title">AheadShop</h1>
        <p class="brand-tagline">发现生活的美好</p>
        <div class="brand-decoration">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-panel">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>登录你的账户，继续探索</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="0"
          size="large"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              placeholder="密码"
              type="password"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度 4-20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 个字符', trigger: 'blur' },
  ],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: var(--color-cream, #fdfbf7);
}

.brand-panel {
  flex: 0 0 45%;
  position: relative;
  background: linear-gradient(160deg, #2d2926 0%, #3a3532 40%, #1a1714 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 2;
  text-align: center;
  padding: 40px;
  animation: brandFadeIn 1s ease-out;
}

.brand-title {
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 52px;
  font-weight: 700;
  color: #fdfbf7;
  letter-spacing: 4px;
  margin-bottom: 12px;
}

.brand-tagline {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 16px;
  color: #d4a574;
  letter-spacing: 6px;
  text-transform: uppercase;
}

.brand-decoration {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 32px;
}

.brand-decoration span {
  display: block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d4a574;
  opacity: 0.6;
}

.brand-decoration span:nth-child(2) {
  opacity: 1;
  width: 24px;
  border-radius: 3px;
}

.brand-pattern {
  position: absolute;
  inset: 0;
  opacity: 0.04;
  background-image: repeating-linear-gradient(45deg, transparent, transparent 40px, #fdfbf7 40px, #fdfbf7 41px);
  z-index: 1;
}

.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-wrapper {
  width: 100%;
  max-width: 380px;
  animation: formSlideUp 0.8s ease-out 0.3s both;
}

.form-header {
  margin-bottom: 40px;
}

.form-header h2 {
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 28px;
  font-weight: 600;
  color: #2d2926;
  margin-bottom: 8px;
}

.form-header p {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  color: #8a8580;
  letter-spacing: 0.5px;
}

:deep(.el-input__wrapper) {
  background: #fff;
  border: 1px solid #e8e4df;
  border-radius: 8px;
  box-shadow: none;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #d4a574;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #d4a574;
  box-shadow: 0 0 0 3px rgba(212, 165, 116, 0.1);
}

:deep(.el-input__inner) {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  color: #2d2926;
}

:deep(.el-input__inner::placeholder) {
  color: #b8b4af;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__error) {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 12px;
  padding-top: 6px;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 15px;
  font-weight: 500;
  letter-spacing: 2px;
  background: #2d2926;
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: #3a3532;
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(45, 41, 38, 0.2);
}

.login-btn:active {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
  margin-top: 32px;
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  color: #8a8580;
}

.form-footer .link {
  color: #d4a574;
  font-weight: 500;
  margin-left: 4px;
  transition: color 0.2s;
}

.form-footer .link:hover {
  color: #c4956a;
}

@keyframes brandFadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes formSlideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 768px) {
  .login-page { flex-direction: column; }
  .brand-panel { flex: 0 0 auto; min-height: 200px; }
  .brand-title { font-size: 36px; }
  .form-panel { padding: 32px 24px; }
}
</style>
