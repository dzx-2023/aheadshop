<template>
  <div class="register-page">
    <div class="brand-panel">
      <div class="brand-content">
        <div class="brand-pattern"></div>
        <h1 class="brand-title">AheadShop</h1>
        <p class="brand-tagline">开启你的购物之旅</p>
        <div class="brand-decoration">
          <span></span><span></span><span></span>
        </div>
      </div>
    </div>

    <div class="form-panel">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>创建账户</h2>
          <p>注册成为会员，享受专属优惠</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large" @submit.prevent="handleRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名（4-20个字符）" :prefix-icon="User" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="密码（6-20个字符）" type="password" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" placeholder="确认密码" type="password" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号（选填）" :prefix-icon="Iphone" clearable />
          </el-form-item>
          <el-form-item prop="inviteCode">
            <el-input v-model="form.inviteCode" placeholder="邀请码（选填）" :prefix-icon="Ticket" clearable maxlength="6" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" class="register-btn" @click="handleRegister">
              {{ loading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Ticket } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '', confirmPassword: '', phone: '', inviteCode: '' })

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度 4-20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度 6-20 个字符', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (value !== form.password) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur',
    },
  ],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  inviteCode: [{ pattern: /^[A-Z0-9]{6}$/, message: '邀请码为6位大写字母+数字', trigger: 'blur' }],
}

onMounted(() => {
  // 从 URL 参数自动填充邀请码
  const invite = route.query.invite as string
  if (invite) {
    form.inviteCode = invite.toUpperCase()
  }
})

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await request.post('/user/register', {
      username: form.username,
      password: form.password,
      phone: form.phone || undefined,
      inviteCode: form.inviteCode || undefined,
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page { display: flex; min-height: 100vh; background: var(--color-cream, #fdfbf7); }
.brand-panel { flex: 0 0 40%; position: relative; background: linear-gradient(160deg, #2d2926 0%, #3a3532 40%, #1a1714 100%); display: flex; align-items: center; justify-content: center; overflow: hidden; }
.brand-content { position: relative; z-index: 2; text-align: center; padding: 40px; animation: brandFadeIn 1s ease-out; }
.brand-title { font-family: 'Playfair Display', 'Noto Serif SC', serif; font-size: 52px; font-weight: 700; color: #fdfbf7; letter-spacing: 4px; margin-bottom: 12px; }
.brand-tagline { font-family: 'DM Sans', 'Noto Sans SC', sans-serif; font-size: 16px; color: #d4a574; letter-spacing: 6px; text-transform: uppercase; }
.brand-decoration { display: flex; justify-content: center; gap: 8px; margin-top: 32px; }
.brand-decoration span { display: block; width: 6px; height: 6px; border-radius: 50%; background: #d4a574; opacity: 0.6; }
.brand-decoration span:nth-child(2) { opacity: 1; width: 24px; border-radius: 3px; }
.brand-pattern { position: absolute; inset: 0; opacity: 0.04; background-image: repeating-linear-gradient(45deg, transparent, transparent 40px, #fdfbf7 40px, #fdfbf7 41px); z-index: 1; }
.form-panel { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px; overflow-y: auto; }
.form-wrapper { width: 100%; max-width: 380px; animation: formSlideUp 0.8s ease-out 0.3s both; }
.form-header { margin-bottom: 36px; }
.form-header h2 { font-family: 'Playfair Display', 'Noto Serif SC', serif; font-size: 28px; font-weight: 600; color: #2d2926; margin-bottom: 8px; }
.form-header p { font-family: 'DM Sans', 'Noto Sans SC', sans-serif; font-size: 14px; color: #8a8580; letter-spacing: 0.5px; }
:deep(.el-input__wrapper) { background: #fff; border: 1px solid #e8e4df; border-radius: 8px; box-shadow: none; padding: 4px 12px; transition: all 0.3s ease; }
:deep(.el-input__wrapper:hover) { border-color: #d4a574; }
:deep(.el-input__wrapper.is-focus) { border-color: #d4a574; box-shadow: 0 0 0 3px rgba(212, 165, 116, 0.1); }
:deep(.el-input__inner) { font-family: 'DM Sans', 'Noto Sans SC', sans-serif; font-size: 14px; color: #2d2926; }
:deep(.el-input__inner::placeholder) { color: #b8b4af; }
:deep(.el-form-item) { margin-bottom: 22px; }
:deep(.el-form-item__error) { font-family: 'DM Sans', 'Noto Sans SC', sans-serif; font-size: 12px; padding-top: 6px; }
.register-btn { width: 100%; height: 48px; font-family: 'DM Sans', 'Noto Sans SC', sans-serif; font-size: 15px; font-weight: 500; letter-spacing: 2px; background: #2d2926; border: none; border-radius: 8px; transition: all 0.3s ease; }
.register-btn:hover { background: #3a3532; transform: translateY(-1px); box-shadow: 0 8px 24px rgba(45, 41, 38, 0.2); }
.register-btn:active { transform: translateY(0); }
.form-footer { text-align: center; margin-top: 28px; font-family: 'DM Sans', 'Noto Sans SC', sans-serif; font-size: 14px; color: #8a8580; }
.form-footer .link { color: #d4a574; font-weight: 500; margin-left: 4px; transition: color 0.2s; }
.form-footer .link:hover { color: #c4956a; }
@keyframes brandFadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@keyframes formSlideUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
@media (max-width: 768px) { .register-page { flex-direction: column; } .brand-panel { flex: 0 0 auto; min-height: 180px; } .brand-title { font-size: 36px; } .form-panel { padding: 24px; } }
</style>
