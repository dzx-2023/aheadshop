<template>
  <div class="default-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo">AheadShop</router-link>
        <nav class="nav">
          <router-link to="/">首页</router-link>
          <router-link to="/cart">
            购物车
            <el-badge v-if="cartStore.count" :value="cartStore.count" :max="99" class="cart-badge" />
          </router-link>
          <template v-if="userStore.isLoggedIn()">
            <router-link to="/order">订单</router-link>
            <router-link to="/user">个人中心</router-link>
            <a @click="handleLogout">退出</a>
          </template>
          <template v-else>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
        </nav>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <p>© {{ new Date().getFullYear() }} AheadShop. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const cartStore = useCartStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  cartStore.reset()
  router.push('/login')
}
</script>

<style scoped>
.default-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
}

.nav {
  display: flex;
  gap: 20px;
  align-items: center;
}

.nav a {
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.nav a:hover,
.nav a.router-link-active {
  color: #409eff;
}

.cart-badge {
  margin-left: 4px;
}

.main {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.footer {
  background: #545c64;
  color: #c0c4cc;
  text-align: center;
  padding: 20px;
  font-size: 13px;
}
</style>
