<template>
  <div class="default-layout">
    <!-- 顶部导航 -->
    <header class="header" :class="{ scrolled: isScrolled }">
      <div class="header-inner">
        <!-- Logo -->
        <router-link to="/" class="logo">
          <span class="logo-text">Ahead</span><span class="logo-accent">Shop</span>
        </router-link>

        <!-- 导航链接 -->
        <nav class="nav-center">
          <router-link to="/" class="nav-link" :class="{ active: route.path === '/' }">
            首页
          </router-link>
          <router-link to="/category" class="nav-link" :class="{ active: route.path.startsWith('/category') }">商品分类</router-link>
          <div class="search-box">
            <el-input
              v-model="searchQuery"
              placeholder="搜索商品..."
              :prefix-icon="Search"
              clearable
              size="default"
              @keyup.enter="handleSearch"
            />
          </div>
        </nav>

        <!-- 右侧操作区 -->
        <div class="nav-right">
          <!-- 购物车 -->
          <router-link to="/cart" class="icon-btn cart-btn">
            <el-badge :value="cartStore.count" :hidden="!cartStore.count" :max="99">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>

          <!-- 已登录 -->
          <template v-if="userStore.isLoggedIn()">
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-trigger">
                <el-avatar :size="32" :src="userStore.userInfo?.avatar || ''" class="user-avatar">
                  {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '?')[0] }}
                </el-avatar>
                <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                <el-icon class="arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="user">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="order">
                    <el-icon><List /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="refund">
                    <el-icon><Money /></el-icon>退款记录
                  </el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" command="admin">
                    <el-icon><Setting /></el-icon>后台管理
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>

          <!-- 未登录 -->
          <template v-else>
            <router-link to="/login" class="nav-link login-link">登录</router-link>
            <router-link to="/register" class="register-btn-link">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="logo-text">Ahead</span><span class="logo-accent">Shop</span>
          <p>发现生活的美好，品味品质的选择</p>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>购物指南</h4>
            <a href="#">购物流程</a>
            <a href="#">配送方式</a>
            <a href="#">售后服务</a>
          </div>
          <div class="footer-col">
            <h4>关于我们</h4>
            <a href="#">品牌故事</a>
            <a href="#">联系我们</a>
            <a href="#">加入我们</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© {{ new Date().getFullYear() }} AheadShop. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { useCartStore } from '@/store/modules/cart'
import { Search, ShoppingCart, ArrowDown, User, List, SwitchButton, Setting, Money } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchQuery = ref('')
const isScrolled = ref(false)

const isAdmin = computed(() => {
  const roles = userStore.userInfo?.roles || []
  return roles.includes('ADMIN') || roles.includes('admin')
})

function handleScroll() {
  isScrolled.value = window.scrollY > 10
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  // 已登录则拉取用户信息和购物车数量
  if (userStore.isLoggedIn()) {
    userStore.fetchUserInfo().catch(() => {})
    cartStore.fetchCount().catch(() => {})
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleSearch() {
  if (searchQuery.value.trim()) {
    router.push({ path: '/search', query: { q: searchQuery.value.trim() } })
  }
}

function handleCommand(command: string) {
  switch (command) {
    case 'user':
      router.push('/user')
      break
    case 'order':
      router.push('/order')
      break
    case 'refund':
      router.push('/refund')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      cartStore.reset()
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.default-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #fdfbf7;
}

/* ── 顶部导航 ── */
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(253, 251, 247, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid transparent;
  transition: all 0.3s ease;
}

.header.scrolled {
  border-bottom-color: #e8e4df;
  box-shadow: 0 2px 16px rgba(45, 41, 38, 0.04);
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

/* Logo */
.logo {
  flex-shrink: 0;
  font-family: 'Playfair Display', 'Noto Serif SC', serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 1px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-text {
  color: #2d2926;
}

.logo-accent {
  color: #d4a574;
}

/* 中间导航 */
.nav-center {
  display: flex;
  align-items: center;
  gap: 28px;
  flex: 1;
  justify-content: center;
}

.nav-link {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #5a5652;
  letter-spacing: 0.5px;
  transition: color 0.2s;
  position: relative;
  white-space: nowrap;
}

.nav-link:hover,
.nav-link.active {
  color: #2d2926;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: #d4a574;
  border-radius: 1px;
}

/* 搜索框 */
.search-box {
  max-width: 280px;
  width: 100%;
}

:deep(.search-box .el-input__wrapper) {
  background: #f5f3ef;
  border: 1px solid transparent;
  border-radius: 20px;
  box-shadow: none;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

:deep(.search-box .el-input__wrapper:hover) {
  background: #efecea;
}

:deep(.search-box .el-input__wrapper.is-focus) {
  background: #fff;
  border-color: #d4a574;
  box-shadow: 0 0 0 3px rgba(212, 165, 116, 0.08);
}

:deep(.search-box .el-input__inner) {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 13px;
}

/* 右侧操作区 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.icon-btn {
  display: flex;
  align-items: center;
  color: #5a5652;
  transition: color 0.2s;
}

.icon-btn:hover {
  color: #d4a574;
}

/* 购物车 */
:deep(.cart-btn .el-badge__content) {
  background: #d4a574;
  border: none;
  font-size: 10px;
}

/* 用户下拉 */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: #f5f3ef;
}

.user-avatar {
  background: #d4a574;
  color: #fdfbf7;
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 600;
}

.user-name {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: #2d2926;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  color: #8a8580;
  font-size: 12px;
}

/* 未登录按钮 */
.login-link {
  padding: 6px 12px;
}

.register-btn-link {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  font-weight: 500;
  color: #fdfbf7;
  background: #2d2926;
  padding: 7px 18px;
  border-radius: 6px;
  transition: all 0.2s;
  letter-spacing: 0.5px;
}

.register-btn-link:hover {
  background: #3a3532;
  transform: translateY(-1px);
}

/* ── 主内容 ── */
.main {
  flex: 1;
}

/* ── 底部 ── */
.footer {
  background: #2d2926;
  color: #b8b4af;
  margin-top: auto;
}

.footer-inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 32px 16px;
}

.footer-brand {
  margin-bottom: 16px;
}

.footer-brand .logo-text {
  color: #fdfbf7;
}

.footer-brand .logo-accent {
  color: #d4a574;
}

.footer-brand p {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  color: #8a8580;
  margin-top: 8px;
  letter-spacing: 0.5px;
}

.footer-links {
  display: flex;
  gap: 64px;
  margin-bottom: 16px;
}

.footer-col h4 {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  font-weight: 600;
  color: #fdfbf7;
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 16px;
}

.footer-col a {
  display: block;
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 13px;
  color: #8a8580;
  margin-bottom: 10px;
  transition: color 0.2s;
}

.footer-col a:hover {
  color: #d4a574;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  padding-top: 20px;
}

.footer-bottom p {
  font-family: 'DM Sans', 'Noto Sans SC', sans-serif;
  font-size: 12px;
  color: #6a6660;
  text-align: center;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .header-inner {
    padding: 0 16px;
  }

  .nav-center {
    display: none;
  }

  .footer-links {
    flex-direction: column;
    gap: 24px;
  }
}
</style>
