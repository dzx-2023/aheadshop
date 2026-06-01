import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  // 前台路由（DefaultLayout）
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/product/index.vue'),
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/product/Category.vue'),
      },
      {
        path: 'search',
        name: 'Search',
        component: () => import('@/views/product/Search.vue'),
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/index.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'order',
        name: 'OrderList',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/order/OrderConfirm.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'order/:orderNo',
        name: 'OrderDetail',
        component: () => import('@/views/order/OrderDetail.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
  // 登录 / 注册（无布局，已登录不可访问）
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/Login.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/user/Register.vue'),
    meta: { guestOnly: true },
  },
  // 后台管理（AdminLayout）
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
      },
      {
        path: 'products',
        name: 'AdminProducts',
        component: () => import('@/views/admin/Products.vue'),
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
      },
    ],
  },
  // 403
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/Forbidden.vue'),
  },
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  const token = localStorage.getItem('token')

  // 需要登录
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 仅游客可访问（已登录跳首页）
  if (to.meta.guestOnly && token) {
    next({ path: '/' })
    return
  }

  // 需要管理员角色
  if (to.meta.requiresAdmin && token) {
    try {
      const { useUserStore } = await import('@/store/modules/user')
      const userStore = useUserStore()

      // 如果还没拉取过用户信息，先拉取
      if (!userStore.userInfo) {
        await userStore.fetchUserInfo()
      }

      const roles = userStore.userInfo?.roles || []
      if (!roles.includes('ADMIN') && !roles.includes('admin')) {
        next({ path: '/403' })
        return
      }
    } catch {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }

  next()
})

export default router
