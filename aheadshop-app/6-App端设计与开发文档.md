# AheadShop-Plus App 端设计与开发文档

## 1. 项目概述

### 1.1 项目定位

AheadShop App 是 AheadShop-Plus 商城系统的移动端客户端，基于 **uni-app** 框架开发，采用 **Vue 3 + Pinia** 技术栈，支持一套代码编译到 H5、微信小程序、Android App、iOS App 多个平台。

### 1.2 技术栈

| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 跨平台框架 | uni-app | 3.0 (alpha) | DCloud 官方框架，Vue 3 模式 |
| 前端框架 | Vue | 3.4+ | 组合式 API (Composition API) |
| 状态管理 | Pinia | 2.1+ | Vue 3 官方推荐状态管理方案 |
| 构建工具 | Vite | 5.2+ | 快速热更新 |
| 编程语言 | JavaScript | ESNext+ | 页面与逻辑层 |
| 类型支持 | TypeScript | 5.4+ | tsconfig 已配置，可选使用 |
| 地图 SDK | 高德地图小程序版 | — | 定位、选址 |
| 图表库 | uCharts | — | 数据可视化（可选） |
| Markdown | marked | — | 富文本渲染（可选） |

### 1.3 编译目标

| 平台 | 命令 | 说明 |
|------|------|------|
| H5 | `npm run dev:h5` / `npm run build:h5` | 浏览器端，开发调试用 |
| 微信小程序 | `npm run dev:mp-weixin` / `npm run build:mp-weixin` | 微信生态 |
| App | `npm run dev:app` / `npm run build:app` | Android / iOS 原生 App |

---

## 2. 工程结构

### 2.1 目录总览

```
aheadshop-app/
├── src/
│   ├── api/                          # 接口请求封装
│   │   └── index.js                  #   全部 API 定义（user/product/cart/order/address/collect/search/pay/upload）
│   ├── components/                   # 公共组件
│   │   ├── product.vue               #   商品卡片组件
│   │   ├── amap-wx/                  #   高德地图小程序 SDK
│   │   │   ├── js/util.js
│   │   │   └── lib/amap-wx.js
│   │   ├── marked/                   #   Markdown 渲染库
│   │   │   ├── index.js
│   │   │   └── lib/marked.js
│   │   └── u-charts/                 #   图表组件
│   │       └── u-charts.js
│   ├── pages/                        # 页面
│   │   ├── index/index.vue           #   首页（TabBar）
│   │   ├── category/category.vue     #   分类页（TabBar）
│   │   ├── cart/cart.vue             #   购物车（TabBar）
│   │   ├── my/my.vue                 #   我的（TabBar）
│   │   ├── login/login.vue           #   登录页
│   │   ├── register/register.vue     #   注册页
│   │   ├── product/list.vue          #   商品列表
│   │   ├── product/detail.vue        #   商品详情（自定义导航栏）
│   │   ├── order/confirm.vue         #   确认订单
│   │   ├── order/list.vue            #   订单列表
│   │   ├── order/detail.vue          #   订单详情
│   │   ├── address/list.vue          #   收货地址列表
│   │   ├── address/edit.vue          #   编辑/新增地址
│   │   └── search/index.vue          #   搜索页
│   ├── store/                        # Pinia 状态管理
│   │   ├── index.js                  #   Pinia 入口，导出各模块
│   │   └── modules/
│   │       ├── user.js               #   用户状态（token、登录态、用户信息）
│   │       └── cart.js               #   购物车状态（本地持久化）
│   ├── utils/                        # 工具函数
│   │   ├── request.js                #   uni.request 封装（JWT 注入、401 处理）
│   │   └── util.js                   #   通用工具（价格格式化、日期、防抖节流等）
│   ├── static/                       # 静态资源
│   │   ├── banner/                   #   轮播图
│   │   ├── category/                 #   分类图标
│   │   ├── product/                  #   商品占位图
│   │   ├── tabbar/                   #   TabBar 图标（普通态 + 选中态）
│   │   ├── icon/                     #   功能图标
│   │   ├── logo.png                  #   应用 Logo
│   │   └── empty-cart.png            #   空购物车插画
│   ├── uni_modules/                  # uni-app 插件市场模块
│   ├── App.vue                       # 根组件（全局生命周期 + 全局样式）
│   ├── main.js                       # 应用入口（createSSRApp + Pinia 挂载）
│   ├── pages.json                    # 路由配置 + TabBar 配置 + 全局样式
│   ├── manifest.json                 # 应用配置（AppID、权限、模块开关）
│   └── uni.scss                      # 全局 SCSS 变量（颜色/字体/间距/圆角/阴影）
├── common/                           # 公共模块（uni-app 原生组件兼容层）
├── hybrid/                           # 混合开发资源
├── node_modules/                     # 依赖包
├── package.json                      # 项目配置
├── package-lock.json                 # 依赖锁定
├── tsconfig.json                     # TypeScript 配置
├── vite.config.js                    # Vite 构建配置
├── index.html                        # H5 入口 HTML
├── .gitignore                        # Git 忽略规则
└── README.md                         # 项目说明
```

### 2.2 关键配置文件

#### pages.json — 路由与 TabBar

```json
{
  "pages": [
    { "path": "pages/index/index",     "style": { "navigationBarTitleText": "首页" } },
    { "path": "pages/category/category","style": { "navigationBarTitleText": "分类" } },
    { "path": "pages/cart/cart",        "style": { "navigationBarTitleText": "购物车" } },
    { "path": "pages/my/my",            "style": { "navigationBarTitleText": "我的" } },
    { "path": "pages/login/login",      "style": { "navigationBarTitleText": "登录" } },
    { "path": "pages/register/register","style": { "navigationBarTitleText": "注册" } },
    { "path": "pages/product/list",     "style": { "navigationBarTitleText": "商品列表" } },
    { "path": "pages/product/detail",   "style": { "navigationBarTitleText": "商品详情", "navigationStyle": "custom" } },
    { "path": "pages/order/confirm",    "style": { "navigationBarTitleText": "确认订单" } },
    { "path": "pages/order/list",       "style": { "navigationBarTitleText": "我的订单" } },
    { "path": "pages/order/detail",     "style": { "navigationBarTitleText": "订单详情" } },
    { "path": "pages/address/list",     "style": { "navigationBarTitleText": "收货地址" } },
    { "path": "pages/address/edit",     "style": { "navigationBarTitleText": "编辑地址" } },
    { "path": "pages/search/index",     "style": { "navigationBarTitleText": "搜索" } }
  ],
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#ff6b35",
    "list": [
      { "pagePath": "pages/index/index",     "text": "首页",   "iconPath": "static/tabbar/home.png",     "selectedIconPath": "static/tabbar/home-active.png" },
      { "pagePath": "pages/category/category","text": "分类",   "iconPath": "static/tabbar/category.png", "selectedIconPath": "static/tabbar/category-active.png" },
      { "pagePath": "pages/cart/cart",        "text": "购物车", "iconPath": "static/tabbar/cart.png",     "selectedIconPath": "static/tabbar/cart-active.png" },
      { "pagePath": "pages/my/my",            "text": "我的",   "iconPath": "static/tabbar/my.png",       "selectedIconPath": "static/tabbar/my-active.png" }
    ]
  }
}
```

#### vite.config.js — 构建与代理

```javascript
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

export default defineConfig({
  plugins: [uni()],
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 代理到 Gateway
        changeOrigin: true
      }
    }
  }
})
```

#### manifest.json — 应用配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| name | AheadShop | 应用名称 |
| appid | \_\_UNI\_\_4E1B79A | DCloud 应用标识 |
| versionName | 1.0.0 | 版本号 |
| vueVersion | 3 | Vue 3 模式 |
| app-plus.modules | OAuth, Payment, Push, Share | 原生模块开关 |

---

## 3. 核心架构设计

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    App 端 (uni-app)                          │
│                                                             │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐       │
│  │  Pages   │  │Components│  │  Store  │  │  Utils  │       │
│  │ (页面)   │  │ (组件)   │  │(Pinia)  │  │ (工具)  │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
│       │             │             │             │             │
│       └─────────────┴──────┬──────┴─────────────┘             │
│                            │                                 │
│                    ┌───────▼───────┐                         │
│                    │   API 层      │                         │
│                    │ (request.js)  │                         │
│                    └───────┬───────┘                         │
│                            │ HTTP (uni.request)              │
└────────────────────────────┼────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │   Gateway       │
                    │  (port 8080)    │
                    │  JWT 鉴权/路由   │
                    └────────┬────────┘
                             │
        ┌────────┬───────────┼───────────┬────────┐
        ▼        ▼           ▼           ▼        ▼
    ┌──────┐ ┌──────┐   ┌──────┐   ┌──────┐ ┌──────┐
    │ User │ │Product│  │ Cart │   │Order │ │ Pay  │
    │:8081 │ │:8082 │  │:8083 │   │:8084 │ │:8085 │
    └──────┘ └──────┘   └──────┘   └──────┘ └──────┘
```

### 3.2 数据流设计

```
用户操作
   │
   ▼
Page (页面事件处理)
   │
   ├──→ Store (本地状态管理)  ←──→  Storage (uni.setStorageSync 持久化)
   │         │
   │         ▼
   └──→ API 层 (接口请求)
            │
            ▼
       request.js (统一拦截)
            │
            ├── 注入 Authorization: Bearer {token}
            ├── 401 → 清除 token → 跳转登录
            ├── 200 + code=200 → resolve(data)
            └── 其他 → showToast 错误信息 → reject
```

### 3.3 页面路由设计

#### TabBar 页面（4 个主页）

| 序号 | 路径 | 页面 | 功能 |
|------|------|------|------|
| 1 | pages/index/index | 首页 | 搜索栏、轮播图、分类入口、热门推荐、新品上市 |
| 2 | pages/category/category | 分类 | 左侧分类导航 + 右侧商品列表 |
| 3 | pages/cart/cart | 购物车 | 商品列表、数量调整、全选/单选、结算 |
| 4 | pages/my/my | 我的 | 用户信息、订单入口、地址管理、收藏、设置 |

#### 业务页面（10 个子页面）

| 序号 | 路径 | 页面 | 功能 |
|------|------|------|------|
| 5 | pages/login/login | 登录 | 手机号+密码登录、微信登录入口 |
| 6 | pages/register/register | 注册 | 新用户注册 |
| 7 | pages/product/list | 商品列表 | 分类筛选、排序、搜索 |
| 8 | pages/product/detail | 商品详情 | 图片轮播、规格选择弹窗、加入购物车、立即购买 |
| 9 | pages/order/confirm | 确认订单 | 收货地址、商品清单、支付方式、提交订单 |
| 10 | pages/order/list | 订单列表 | 按状态 Tab 筛选（全部/待支付/待发货/待收货/已完成） |
| 11 | pages/order/detail | 订单详情 | 订单信息、物流状态、操作按钮 |
| 12 | pages/address/list | 收货地址 | 地址列表、默认地址标记、编辑/删除 |
| 13 | pages/address/edit | 编辑地址 | 新增/编辑收货地址 |
| 14 | pages/search/index | 搜索 | 搜索输入框、热门搜索、搜索历史、搜索结果 |

---

## 4. 状态管理设计（Pinia）

### 4.1 Store 模块总览

```
store/
├── index.js          # Pinia 实例创建 + 统一导出
└── modules/
    ├── user.js       # 用户状态管理
    └── cart.js       # 购物车状态管理
```

### 4.2 User Store

**职责：** 管理用户登录态、Token、用户信息，持久化到 `uni.storage`。

```javascript
// store/modules/user.js
export const useUserStore = defineStore('user', () => {
  // ─── State ───
  const token = ref('')           // JWT Token
  const userInfo = ref({          // 用户信息
    id: '',
    nickname: '',
    avatar: '',
    phone: ''
  })
  const isLoggedIn = ref(false)   // 登录状态

  // ─── Actions ───
  const setToken = (newToken) => {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }

  const getToken = () => {
    if (!token.value) {
      token.value = uni.getStorageSync('token') || ''
    }
    return token.value
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    isLoggedIn.value = true
    uni.setStorageSync('userInfo', JSON.stringify(info))
  }

  const getUserInfo = () => {
    if (!userInfo.value.id) {
      const info = uni.getStorageSync('userInfo')
      if (info) {
        userInfo.value = JSON.parse(info)
        isLoggedIn.value = true
      }
    }
    return userInfo.value
  }

  const logout = () => {
    token.value = ''
    userInfo.value = { id: '', nickname: '', avatar: '', phone: '' }
    isLoggedIn.value = false
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
  }

  return { token, userInfo, isLoggedIn, setToken, getToken, setUserInfo, getUserInfo, logout }
})
```

**持久化策略：**

| 数据 | Storage Key | 说明 |
|------|-------------|------|
| JWT Token | `token` | 登录后写入，退出时清除 |
| 用户信息 | `userInfo` | JSON 字符串存储 |

### 4.3 Cart Store

**职责：** 管理购物车商品列表，支持本地持久化，提供增删改查、全选、价格计算。

```javascript
// store/modules/cart.js
export const useCartStore = defineStore('cart', () => {
  // ─── State ───
  const cartList = ref([])

  // ─── Getters ───
  const cartCount = computed(() =>
    cartList.value.reduce((sum, item) => sum + item.quantity, 0)
  )
  const checkedItems = computed(() =>
    cartList.value.filter(item => item.checked)
  )
  const totalPrice = computed(() =>
    checkedItems.value
      .reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
      .toFixed(2)
  )

  // ─── Actions ───
  const addToCart = (product) => {
    const existing = cartList.value.find(
      item => item.id === product.id && item.specId === product.specId
    )
    if (existing) {
      existing.quantity += product.quantity || 1
    } else {
      cartList.value.push({ ...product, quantity: product.quantity || 1, checked: true })
    }
    saveCartToStorage()
  }

  const removeFromCart = (index) => { cartList.value.splice(index, 1); saveCartToStorage() }
  const updateQuantity = (index, qty) => {
    if (qty <= 0) removeFromCart(index)
    else { cartList.value[index].quantity = qty; saveCartToStorage() }
  }
  const toggleCheck = (index) => { cartList.value[index].checked = !cartList.value[index].checked; saveCartToStorage() }
  const toggleSelectAll = (checked) => { cartList.value.forEach(item => item.checked = checked); saveCartToStorage() }
  const clearCart = () => { cartList.value = []; saveCartToStorage() }

  // ─── 持久化 ───
  const saveCartToStorage = () => uni.setStorageSync('cartList', JSON.stringify(cartList.value))
  const loadCartFromStorage = () => {
    const data = uni.getStorageSync('cartList')
    if (data) cartList.value = JSON.parse(data)
  }
  loadCartFromStorage() // 初始化时自动加载

  return { cartList, cartCount, checkedItems, totalPrice, addToCart, removeFromCart, updateQuantity, toggleCheck, toggleSelectAll, clearCart }
})
```

**持久化策略：**

| 数据 | Storage Key | 说明 |
|------|-------------|------|
| 购物车列表 | `cartList` | JSON 数组，每次变更后自动保存 |

---

## 5. 网络请求设计

### 5.1 请求封装（request.js）

所有后端 API 请求统一封装，基于 `uni.request`。

```javascript
const BASE_URL = 'http://localhost:8080'

const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data.data)         // 业务成功，直接返回 data
          } else if (res.data.code === 401) {
            // Token 过期，清除登录态，跳转登录
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.navigateTo({ url: '/pages/login/login' })
            reject(new Error('登录已过期，请重新登录'))
          } else {
            uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
            reject(new Error(res.data.msg))
          }
        } else {
          uni.showToast({ title: '网络请求失败', icon: 'none' })
          reject(new Error('网络请求失败'))
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络连接失败', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get  = (url, data) => request({ url, method: 'GET', data })
export const post = (url, data) => request({ url, method: 'POST', data })
export const put  = (url, data) => request({ url, method: 'PUT', data })
export const del  = (url, data) => request({ url, method: 'DELETE', data })
```

### 5.2 请求流程

```
页面调用 API
     │
     ▼
request.js
     │
     ├── 从 Storage 读取 token
     ├── 拼接 BASE_URL + 接口路径
     ├── 设置 Header: Authorization: Bearer {token}
     │
     ▼
uni.request 发送 HTTP 请求
     │
     ▼
Gateway (port 8080)
     │
     ├── 白名单放行（/api/user/login, /api/user/register, /api/product/**）
     ├── 校验 JWT Token
     │     ├── 通过 → 注入 X-User-Id / X-User-Role 请求头 → 转发到微服务
     │     └── 失败 → 返回 401
     ▼
微服务处理 → 返回 Result<T> { code, msg, data }
     │
     ▼
request.js 处理响应
     ├── code === 200  → resolve(data)
     ├── code === 401  → 清除 token → 跳转登录 → reject
     └── 其他          → showToast(msg) → reject
```

### 5.3 API 接口定义

#### 用户模块（userApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /api/user/login | 用户登录 | ❌ |
| POST | /api/user/register | 用户注册 | ❌ |
| GET | /api/user/info | 获取用户信息 | ✅ |
| PUT | /api/user/info | 更新用户信息 | ✅ |
| POST | /api/user/change-password | 修改密码 | ✅ |
| POST | /api/sms/send | 发送短信验证码 | ❌ |
| POST | /api/user/reset-password | 重置密码 | ❌ |

#### 商品模块（productApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /api/product/list | 商品列表（分页） | ❌ |
| GET | /api/product/detail/{id} | 商品详情 | ❌ |
| GET | /api/product/search | 商品搜索 | ❌ |
| GET | /api/category/list | 分类列表 | ❌ |
| GET | /api/product/category | 分类商品 | ❌ |

#### 购物车模块（cartApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /api/cart/list | 购物车列表 | ✅ |
| POST | /api/cart/add | 添加到购物车 | ✅ |
| PUT | /api/cart/update | 更新数量 | ✅ |
| DELETE | /api/cart/{id} | 删除商品 | ✅ |
| DELETE | /api/cart/clear | 清空购物车 | ✅ |

#### 订单模块（orderApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /api/order/create | 创建订单 | ✅ |
| GET | /api/order/list | 订单列表 | ✅ |
| GET | /api/order/detail/{id} | 订单详情 | ✅ |
| PUT | /api/order/{id}/cancel | 取消订单 | ✅ |
| PUT | /api/order/{id}/receive | 确认收货 | ✅ |
| DELETE | /api/order/{id} | 删除订单 | ✅ |
| GET | /api/order/stats | 订单统计 | ✅ |

#### 地址模块（addressApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /api/address/list | 地址列表 | ✅ |
| GET | /api/address/{id} | 地址详情 | ✅ |
| POST | /api/address | 添加地址 | ✅ |
| PUT | /api/address/{id} | 更新地址 | ✅ |
| DELETE | /api/address/{id} | 删除地址 | ✅ |
| PUT | /api/address/{id}/default | 设置默认 | ✅ |

#### 收藏模块（collectApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /api/collect/list | 收藏列表 | ✅ |
| POST | /api/collect/add | 添加收藏 | ✅ |
| DELETE | /api/collect/{id} | 取消收藏 | ✅ |
| GET | /api/collect/check/{id} | 检查是否收藏 | ✅ |

#### 搜索模块（searchApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| GET | /api/search/hot | 热门搜索 | ❌ |
| GET | /api/search/suggestions | 搜索建议 | ❌ |
| GET | /api/search/history | 搜索历史 | ✅ |
| DELETE | /api/search/history | 清除历史 | ✅ |

#### 支付模块（payApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /api/pay/create | 创建支付单 | ✅ |
| GET | /api/pay/status/{id} | 查询支付状态 | ✅ |

#### 文件上传（uploadApi）

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | /api/upload/image | 上传图片（uni.uploadFile） | ✅ |

---

## 6. 页面详细设计

### 6.1 首页（pages/index/index）

**布局结构：**

```
┌─────────────────────────────┐
│  🔍 搜索商品                 │  ← 搜索栏（点击跳转搜索页）
├─────────────────────────────┤
│  ┌───────────────────────┐  │
│  │      轮播图 Banner     │  │  ← swiper 组件，自动轮播
│  └───────────────────────┘  │
├─────────────────────────────┤
│  数码  服装  美妆  食品      │  ← 分类入口（8 宫格）
│  家居  运动  图书  更多      │
├─────────────────────────────┤
│  热门推荐           查看更多 >│
│  ┌────┐ ┌────┐ ┌────┐ ┌────┐│  ← 横向滚动商品卡片
│  │商品│ │商品│ │商品│ │商品││
│  └────┘ └────┘ └────┘ └────┘│
├─────────────────────────────┤
│  新品上市           查看更多 >│
│  ┌───────┐  ┌───────┐      │
│  │ 商品1  │  │ 商品2  │      │  ← 双列商品网格
│  │ ¥128  │  │ ¥258  │      │
│  └───────┘  └───────┘      │
│  ┌───────┐  ┌───────┐      │
│  │ 商品3  │  │ 商品4  │      │
│  └───────┘  └───────┘      │
└─────────────────────────────┘
```

**数据来源：**

| 区域 | 接口 | 说明 |
|------|------|------|
| 轮播图 | 待定（可配置） | 当前为静态数据 |
| 分类入口 | GET /api/category/list | 前 8 个分类 |
| 热门推荐 | GET /api/product/list?sort=hot | 按销量排序 |
| 新品上市 | GET /api/product/list?sort=new | 按创建时间排序 |

### 6.2 商品详情页（pages/product/detail）

**布局结构：**

```
┌─────────────────────────────┐
│  ┌───────────────────────┐  │
│  │   商品图片轮播 (750rpx) │  │  ← 自定义导航栏，全屏轮播
│  └───────────────────────┘  │
├─────────────────────────────┤
│  ¥99.00    ¥199.00   促销   │  ← 价格 + 原价 + 标签
│  商品名称（最多两行显示）      │
│  已售100件          ❤️ 收藏  │
├─────────────────────────────┤
│  规格                    >  │  ← 点击弹出规格选择弹窗
├─────────────────────────────┤
│  [商品详情]  [规格参数]       │  ← Tab 切换
│  ┌───────────────────────┐  │
│  │  富文本详情 / 参数表格   │  │
│  └───────────────────────┘  │
├─────────────────────────────┤
│  🏠首页  🛒购物车  │ 加入购物车 │ 立即购买 │  ← 底部操作栏
└─────────────────────────────┘
```

**规格选择弹窗：**

```
┌─────────────────────────────┐
│  [商品图]  ¥99.00        ✕  │
│           库存：100件        │
│           已选：黑色，XL     │
├─────────────────────────────┤
│  颜色                        │
│  [黑色] [白色] [红色]        │  ← 规格选项，点选高亮
│                              │
│  尺码                        │
│  [S] [M] [L] [XL]           │
│                              │
│  购买数量         [-] 1 [+]  │
├─────────────────────────────┤
│  [        确定        ]      │
└─────────────────────────────┘
```

### 6.3 购物车页（pages/cart/cart）

**布局结构：**

```
┌─────────────────────────────┐
│                              │
│  ┌───────────────────────┐  │
│  │ ☑  [图片] 商品名       │  │
│  │     规格：黑色 XL      │  │
│  │     ¥99.00    [-] 1 [+]│  │
│  │                    删除 │  │
│  └───────────────────────┘  │
│                              │
│  ┌───────────────────────┐  │
│  │ ☑  [图片] 商品名       │  │
│  │     规格：白色 M       │  │
│  │     ¥199.00   [-] 2 [+]│  │
│  │                    删除 │  │
│  └───────────────────────┘  │
│                              │
├─────────────────────────────┤
│  ☑全选    合计：¥497.00  结算(3)│  ← 底部结算栏（固定定位）
└─────────────────────────────┘
```

**空状态：**

```
┌─────────────────────────────┐
│                              │
│       ┌─────────────┐       │
│       │  空购物车插画  │       │
│       └─────────────┘       │
│       购物车是空的            │
│       [ 去逛逛 ]             │
│                              │
└─────────────────────────────┘
```

### 6.4 登录页（pages/login/login）

**布局结构：**

```
┌─────────────────────────────┐
│                              │
│         [ Logo ]             │
│        AheadShop             │
│         欢迎回来              │
│                              │
│  手机号                      │
│  ┌───────────────────────┐  │
│  │ 请输入手机号            │  │
│  └───────────────────────┘  │
│                              │
│  密码                        │
│  ┌───────────────────────┐  │
│  │ 请输入密码              │  │
│  └───────────────────────┘  │
│                忘记密码？     │
│                              │
│  [        登录        ]      │
│         还没有账号？立即注册   │
│                              │
│  ─── 其他登录方式 ───        │
│         [微信图标]           │
└─────────────────────────────┘
```

**登录流程：**

```
用户输入手机号 + 密码
     │
     ▼
前端校验
     ├── 手机号格式校验（/^1[3-9]\d{9}$/）
     ├── 密码长度校验（≥6 位）
     └── 非空校验
     │
     ▼
调用 post('/api/user/login', { phone, password })
     │
     ▼
后端校验密码（BCrypt）→ 生成 JWT Token
     │
     ▼
前端存储
     ├── userStore.setToken(res.token)     → Storage: token
     ├── userStore.setUserInfo(res.userInfo) → Storage: userInfo
     └── 跳转首页 uni.switchTab('/pages/index/index')
```

---

## 7. 工具函数设计

### 7.1 utils/util.js

| 函数 | 签名 | 说明 |
|------|------|------|
| formatPrice | `(price, decimals=2) → string` | 价格格式化，保留小数位 |
| formatDate | `(date, format='YYYY-MM-DD HH:mm:ss') → string` | 日期格式化 |
| debounce | `(fn, delay=300) → Function` | 防抖函数 |
| throttle | `(fn, delay=300) → Function` | 节流函数 |
| generateId | `() → string` | 生成唯一 ID（时间戳 + 随机数） |
| deepClone | `(obj) → any` | 深拷贝 |
| maskPhone | `(phone) → string` | 手机号脱敏（138****8888） |
| showLoading | `(title='加载中...') → void` | 显示加载弹窗 |
| hideLoading | `() → void` | 隐藏加载弹窗 |
| showToast | `(title, icon='none') → void` | 显示提示信息 |

---

## 8. 样式设计规范

### 8.1 全局 SCSS 变量（uni.scss）

```scss
/* ─── 主题色 ─── */
$brand-color: #ff6b35;              // 主色（橙色）
$brand-color-light: #ff8f65;        // 浅色
$brand-color-dark: #e55a28;         // 深色

/* ─── 文字颜色 ─── */
$text-primary: #333333;             // 主要文字
$text-secondary: #666666;           // 次要文字
$text-placeholder: #999999;         // 占位文字
$text-disabled: #cccccc;            // 禁用文字

/* ─── 背景颜色 ─── */
$bg-primary: #f5f5f5;               // 页面背景
$bg-white: #ffffff;                 // 卡片背景
$bg-mask: rgba(0, 0, 0, 0.5);      // 遮罩层

/* ─── 价格颜色 ─── */
$price-color: #ff6b35;              // 现价
$price-original: #999999;           // 原价（划线）

/* ─── 按钮颜色 ─── */
$btn-primary: #ff6b35;              // 主按钮
$btn-primary-hover: #e55a28;        // 主按钮悬停
$btn-danger: #ff4444;               // 危险按钮

/* ─── 字体大小（rpx 响应式单位）─── */
$font-xs: 20rpx;
$font-sm: 24rpx;
$font-base: 28rpx;                  // 正文
$font-lg: 32rpx;
$font-xl: 36rpx;
$font-xxl: 40rpx;

/* ─── 间距 ─── */
$spacing-xs: 8rpx;
$spacing-sm: 16rpx;
$spacing-base: 24rpx;
$spacing-lg: 32rpx;
$spacing-xl: 40rpx;

/* ─── 圆角 ─── */
$radius-sm: 8rpx;
$radius-base: 12rpx;
$radius-lg: 16rpx;
$radius-xl: 24rpx;
$radius-round: 50%;

/* ─── 阴影 ─── */
$shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
$shadow-base: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
$shadow-lg: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
```

### 8.2 全局基础样式（App.vue）

```css
page {
  background-color: #f5f5f5;
  height: 100%;
  font-size: 28rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

view, text, scroll-view, swiper, swiper-item, image {
  box-sizing: border-box;
}

::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
  color: transparent;
}
```

### 8.3 设计规范

| 规范项 | 标准值 | 说明 |
|--------|--------|------|
| 设计稿宽度 | 750rpx | uni-app 默认适配方案 |
| 主色调 | #ff6b35 | 全局统一品牌色 |
| 导航栏背景 | #ff6b35 | 白色文字 |
| 页面背景 | #f5f5f5 | 浅灰色 |
| 卡片背景 | #ffffff | 纯白 |
| 卡片圆角 | 16rpx | 统一圆角 |
| 卡片间距 | 20rpx | 卡片之间 |
| 内容内边距 | 24rpx | 卡片内部 |
| 底部操作栏高度 | 100rpx | 固定定位 |

---

## 9. 跨平台适配说明

### 9.1 条件编译

uni-app 使用条件编译处理平台差异：

```javascript
// #ifdef H5
console.log('H5 平台特有逻辑')
// #endif

// #ifdef MP-WEIXIN
console.log('微信小程序特有逻辑')
// #endif

// #ifdef APP-PLUS
console.log('App 平台特有逻辑')
// #endif
```

### 9.2 平台差异对照

| 功能 | H5 | 微信小程序 | App |
|------|-----|-----------|-----|
| 登录 | 手机号+密码 | 微信授权登录 | 手机号+密码 / 微信 / Apple ID |
| 支付 | 支付宝/微信 H5 | 微信支付 | 支付宝/微信原生支付 |
| 推送 | 不支持 | 微信模板消息 | UniPush |
| 地图 | H5 地图 | 微信内置地图 | 高德/原生地图 |
| 扫码 | 不支持 | 微信扫码 | 原生扫码 |
| 分享 | URL 分享 | 微信分享 | 系统分享 |

### 9.3 App 原生模块配置

manifest.json 中已启用的原生模块：

```json
{
  "app-plus": {
    "modules": {
      "OAuth": {},     // 第三方登录（微信、Apple）
      "Payment": {},   // 支付（支付宝、微信）
      "Push": {},      // 推送（UniPush）
      "Share": {}      // 分享
    }
  }
}
```

Android 权限配置（manifest.json）：

- `CAMERA` — 相机（扫码、拍照）
- `ACCESS_FINE_LOCATION` — 精确定位
- `ACCESS_COARSE_LOCATION` — 粗略定位
- `INTERNET` — 网络访问
- `READ_PHONE_STATE` — 设备信息
- `WRITE_EXTERNAL_STORAGE` — 文件存储

---

## 10. 与后端对接规范

### 10.1 数据格式约定

#### 请求格式

```javascript
// GET 请求 — 参数拼到 URL
GET /api/product/list?pageNum=1&pageSize=10&categoryId=1

// POST/PUT 请求 — JSON Body
POST /api/user/login
Content-Type: application/json
{
  "phone": "13800138000",
  "password": "123456"
}
```

#### 响应格式（统一 Result\<T\>）

```json
// 成功
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}

// 分页
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [ ... ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}

// 失败
{
  "code": 10002,
  "msg": "用户名或密码错误",
  "data": null
}
```

### 10.2 错误码处理

| 错误码 | App 端处理 |
|--------|-----------|
| 200 | 正常返回 data |
| 400 | showToast 显示 msg |
| 401 | 清除 token → 跳转登录页 |
| 403 | showToast "无权限" |
| 404 | showToast "资源不存在" |
| 500 | showToast "服务器异常" |
| 10xxx | showToast 显示具体业务错误 |
| 20xxx | showToast 显示商品相关错误 |
| 30xxx | showToast 显示订单相关错误 |
| 40xxx | showToast 显示支付相关错误 |

### 10.3 Token 管理

```
登录成功
   │
   ├── AccessToken  → Storage: token （有效期 2h）
   ├── RefreshToken → Redis: user:refresh:{id} （有效期 7d）
   │
   ▼
每次请求自动携带 Authorization: Bearer {accessToken}
   │
   ├── 401 响应 → 尝试用 RefreshToken 刷新（待实现）
   │                ├── 刷新成功 → 更新 Storage → 重试请求
   │                └── 刷新失败 → 清除登录态 → 跳转登录
   │
   └── 退出登录 → 清除 Storage → 跳转首页
```

---

## 11. 开发规范

### 11.1 代码规范

| 规范项 | 要求 |
|--------|------|
| 组件风格 | Vue 3 组合式 API（`<script setup>`） |
| 命名方式 | 文件名 kebab-case，变量名 camelCase，组件名 PascalCase |
| 页面文件 | 统一 `.vue` 单文件组件 |
| 样式作用域 | 使用 `<style scoped>` 避免样式污染 |
| 单位 | 统一使用 `rpx` 响应式单位 |
| 图片资源 | 放入 `static/` 目录，使用绝对路径引用 |
| 接口调用 | 统一通过 `@/api/index.js` 封装，不直接调用 `uni.request` |
| 状态管理 | 全局状态使用 Pinia，页面局部状态使用 `ref/reactive` |
| 错误处理 | 统一在 request.js 中处理，页面层使用 try-catch |

### 11.2 文件命名规范

```
页面文件：pages/模块名/页面名.vue     例：pages/login/login.vue
组件文件：components/组件名.vue       例：components/product.vue
Store：   store/modules/模块名.js     例：store/modules/user.js
工具函数： utils/文件名.js             例：utils/request.js
API：     api/index.js                统一在一个文件中
```

### 11.3 提交规范

```
feat:     新功能
fix:      修复 Bug
style:    样式调整
refactor: 重构
docs:     文档更新
chore:    构建/配置变更
```

---

## 12. 开发任务清单

### 12.1 已完成

- [x] 项目骨架搭建（uni-app + Vue 3 + Pinia）
- [x] 路由配置（14 个页面 + 4 TabBar）
- [x] 全局样式变量体系（uni.scss）
- [x] 请求封装（request.js，JWT 注入，401 处理）
- [x] API 接口定义（8 个模块，30+ 接口）
- [x] User Store（token 持久化，登录态管理）
- [x] Cart Store（本地持久化，增删改查，价格计算）
- [x] 工具函数库（formatPrice, formatDate, debounce, throttle 等）
- [x] 首页静态布局（搜索栏、轮播图、分类入口、推荐商品）
- [x] 登录页（手机号+密码，表单校验，调用后端接口）
- [x] 购物车页（静态数据，全选/单选、数量调整、结算）
- [x] 商品详情页（图片轮播、规格弹窗、加入购物车）

### 12.2 待开发

#### 第一优先级（核心购物流程）

- [ ] 首页对接后端接口（轮播图、分类、商品列表）
- [ ] 分类页开发（左侧分类导航 + 右侧商品列表）
- [ ] 商品列表页开发（筛选、排序、分页加载）
- [ ] 商品详情页对接后端（动态数据、SKU 选择联动）
- [ ] 购物车对接后端（同步到服务端 Redis）
- [ ] 确认订单页开发（收货地址选择、商品清单、提交）
- [ ] 订单列表页开发（Tab 筛选、分页）
- [ ] 订单详情页开发

#### 第二优先级（用户体系）

- [ ] 注册页开发（手机号注册、验证码）
- [ ] 微信登录对接
- [ ] 我的页面开发（用户信息、订单入口、功能菜单）
- [ ] 收货地址列表页开发
- [ ] 收货地址编辑页开发（省市区选择器）
- [ ] 搜索页开发（热词、历史、搜索结果）

#### 第三优先级（增强功能）

- [ ] 支付对接（支付宝/微信）
- [ ] 收藏功能
- [ ] 商品评价展示
- [ ] 订单状态流转（取消、确认收货）
- [ ] 图片懒加载优化
- [ ] 下拉刷新 + 上拉加载更多
- [ ] 骨架屏加载态

#### 第四优先级（平台适配）

- [ ] 微信小程序适配与测试
- [ ] App 原生打包配置
- [ ] 推送通知集成
- [ ] 分享功能集成

---

## 13. 运行与构建

### 13.1 环境要求

| 工具 | 版本 | 说明 |
|------|------|------|
| Node.js | ≥ 16.x | 推荐 18 LTS |
| npm | ≥ 8.x | 或 pnpm |
| HBuilderX | 最新稳定版 | App 打包必需（或使用 CLI） |
| 微信开发者工具 | 最新稳定版 | 小程序预览/调试 |

### 13.2 安装依赖

```bash
cd aheadshop-app
npm install
```

### 13.3 开发命令

```bash
# H5 开发（浏览器调试）
npm run dev:h5

# 微信小程序开发
npm run dev:mp-weixin
# 然后在微信开发者工具中导入 dist/dev/mp-weixin 目录

# App 开发
npm run dev:app
# 然后在 HBuilderX 中运行到手机或模拟器
```

### 13.4 生产构建

```bash
# H5 构建
npm run build:h5
# 产出：dist/build/h5，可部署到 Nginx

# 微信小程序构建
npm run build:mp-weixin
# 产出：dist/build/mp-weixin，在微信开发者工具中上传

# App 构建
npm run build:app
# 产出：dist/build/app，在 HBuilderX 中打原生包
```

### 13.5 开发代理

Vite 开发服务器运行在 **5174 端口**，`/api` 前缀请求自动代理到 Gateway（`localhost:8080`）：

```
浏览器 → http://localhost:5174/api/user/login
              ↓ Vite Proxy
         → http://localhost:8080/api/user/login
              ↓ Gateway 路由
         → aheadshop-user (port 8081)
```

---

## 14. 后端接口依赖对照表

| App 端功能 | 接口 | 后端服务 | 当前状态 |
|-----------|------|---------|---------|
| 首页轮播图 | 待定 | — | 静态数据 |
| 首页分类入口 | GET /api/category/list | product | 待实现 |
| 首页热门商品 | GET /api/product/list?sort=hot | product | 待实现 |
| 首页新品 | GET /api/product/list?sort=new | product | 待实现 |
| 用户登录 | POST /api/user/login | user | ✅ 已实现 |
| 用户注册 | POST /api/user/register | user | ✅ 已实现 |
| 用户信息 | GET /api/user/info | user | ✅ 已实现 |
| 商品列表 | GET /api/product/list | product | 待实现 |
| 商品详情 | GET /api/product/detail/{id} | product | 待实现 |
| 商品搜索 | GET /api/product/search | product | 待实现 |
| 分类列表 | GET /api/category/list | product | 待实现 |
| 购物车操作 | /api/cart/* | cart | 待实现 |
| 创建订单 | POST /api/order/create | order | 待实现 |
| 订单列表 | GET /api/order/list | order | 待实现 |
| 订单详情 | GET /api/order/detail/{id} | order | 待实现 |
| 收货地址 | /api/address/* | user | 待实现 |
| 支付 | /api/pay/* | pay | 待实现 |
| 文件上传 | POST /api/upload/image | gateway | 待实现 |
