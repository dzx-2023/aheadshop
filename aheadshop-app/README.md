# AheadShop App

AheadShop 商城 App 端，基于 uni-app 开发。

## 技术栈

- uni-app
- Vue 3
- Pinia
- Vite

## 项目结构

```
aheadshop-app/
├── api/                 # API 接口封装
│   └── index.js
├── components/          # 公共组件
├── pages/               # 页面
│   ├── index/           # 首页
│   ├── category/        # 分类
│   ├── cart/            # 购物车
│   ├── my/              # 我的
│   ├── login/           # 登录
│   ├── register/        # 注册
│   ├── product/         # 商品
│   ├── order/           # 订单
│   ├── address/         # 地址
│   └── search/          # 搜索
├── store/               # 状态管理
│   └── modules/
│       ├── user.js      # 用户状态
│       └── cart.js      # 购物车状态
├── static/              # 静态资源
├── utils/               # 工具函数
│   ├── request.js       # 请求封装
│   └── util.js          # 通用工具
├── App.vue              # 应用入口
├── main.js              # 主入口
├── manifest.json        # 应用配置
├── pages.json           # 页面配置
├── uni.scss             # 全局样式
└── vite.config.js       # Vite 配置
```

## 开发

```bash
# 安装依赖
npm install

# 运行 H5
npm run dev:h5

# 运行微信小程序
npm run dev:mp-weixin

# 运行 App
npm run dev:app
```

## 构建

```bash
# 构建 H5
npm run build:h5

# 构建微信小程序
npm run build:mp-weixin

# 构建 App
npm run build:app
```

## 功能模块

- [x] 首页
- [x] 分类
- [x] 购物车
- [x] 我的
- [x] 登录/注册
- [x] 商品列表
- [x] 商品详情
- [x] 订单确认
- [x] 订单列表
- [x] 地址管理
- [x] 搜索

## 后端接口

后端服务运行在 http://localhost:8080，API 代理配置在 vite.config.js 中。
