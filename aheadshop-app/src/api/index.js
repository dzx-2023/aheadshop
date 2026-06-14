import { get, post, put, del } from '@/utils/request'

// ==================== 用户模块 ====================
export const userApi = {
	login: (data) => post('/api/user/login', data),
	register: (data) => post('/api/user/register', data),
	getUserInfo: () => get('/api/user/info'),
	updateUserInfo: (data) => put('/api/user/info', data),
	uploadAvatar: (filePath) => {
		return new Promise((resolve, reject) => {
			uni.uploadFile({
				url: '/api/user/avatar',
				filePath,
				name: 'file',
				header: { Authorization: `Bearer ${uni.getStorageSync('token')}` },
				success: (res) => {
					if (res.statusCode === 200) {
						const data = JSON.parse(res.data)
						data.code === 200 ? resolve(data.data) : reject(new Error(data.msg))
					} else {
						reject(new Error('上传失败'))
					}
				},
				fail: reject
			})
		})
	},
	sendSms: (data) => post('/api/sms/send', data),
	refreshToken: (data) => post('/api/user/refresh-token', data)
}

// ==================== 地址模块 ====================
export const addressApi = {
	getList: () => get('/api/user/address/list'),
	getDetail: (id) => get(`/api/user/address/${id}`),
	add: (data) => post('/api/user/address', data),
	update: (data) => put('/api/user/address', data),
	delete: (id) => del(`/api/user/address/${id}`),
	setDefault: (id) => put(`/api/user/address/default/${id}`)
}

// ==================== 商品模块 ====================
export const productApi = {
	// SPU 列表（分页、筛选、搜索）
	getList: (params) => get('/api/product/spu/list', params),
	// SPU 详情（含 SKU 列表）
	getDetail: (id) => get(`/api/product/spu/${id}`),
	// 分类树
	getCategoryTree: () => get('/api/product/category/tree'),
	// 品牌列表
	getBrandList: (params) => get('/api/product/brand/list', params),
	// 分类下的品牌
	getBrandsByCategory: (categoryId) => get(`/api/product/category-brand/brands/${categoryId}`)
}

// ==================== 轮播图/背景图 ====================
export const backgroundApi = {
	// 获取启用的背景图（公开接口）
	getActive: () => get('/api/product/background/active')
}

// ==================== 评价模块 ====================
export const reviewApi = {
	// 提交评价
	submit: (data) => post('/api/product/review', data),
	// 商品评价列表
	getList: (spuId, params) => get(`/api/product/review/list/${spuId}`, params),
	// 商品平均评分
	getAvgScore: (spuId) => get(`/api/product/review/avg-score/${spuId}`),
	// 我的评价
	getMyReviews: (params) => get('/api/product/review/my', params),
	// 检查订单是否已评价
	checkReviewed: (orderNo) => get(`/api/product/review/check/${orderNo}`)
}

// ==================== 购物车模块 ====================
export const cartApi = {
	// 获取购物车列表
	getList: () => get('/api/cart/list'),
	// 添加到购物车
	add: (data) => post('/api/cart/add', data),
	// 更新数量
	update: (data) => put('/api/cart/update', data),
	// 删除商品
	remove: (skuId) => del(`/api/cart/delete/${skuId}`),
	// 获取购物车数量
	getCount: () => get('/api/cart/count'),
	// 勾选/取消勾选
	check: (skuId, checked) => put(`/api/cart/check/${skuId}?checked=${checked}`)
}

// ==================== 订单模块 ====================
export const orderApi = {
	// 创建订单
	create: (data) => post('/api/order/create', data),
	// 订单列表
	getList: (params) => get('/api/order/list', params),
	// 订单详情
	getDetail: (orderNo) => get(`/api/order/detail/${orderNo}`),
	// 取消订单
	cancel: (orderNo) => put(`/api/order/cancel/${orderNo}`),
	// 确认收货
	confirmReceive: (orderNo) => put(`/api/order/confirm/${orderNo}`),
	// 申请退款
	applyRefund: (orderNo, refundType = 1, reason = '') => post(`/api/order/refund/${orderNo}?refundType=${refundType}&reason=${encodeURIComponent(reason)}`),
	// 退款详情
	getRefund: (orderNo) => get(`/api/order/refund/${orderNo}`),
	// 退款列表
	getRefundList: (params) => get('/api/order/refund/list', params)
}

// ==================== 支付模块 ====================
export const payApi = {
	// 创建支付
	create: (data) => post('/api/pay/create', data),
	// 查询支付状态
	queryStatus: (payNo) => get(`/api/pay/status/${payNo}`),
	// 申请退款
	refund: (data) => post('/api/pay/refund', data)
}

// ==================== 文件上传 ====================
export const uploadApi = {
	uploadImage: (filePath) => {
		return new Promise((resolve, reject) => {
			uni.uploadFile({
				url: '/api/product/spu/upload',
				filePath,
				name: 'file',
				header: { Authorization: `Bearer ${uni.getStorageSync('token')}` },
				success: (res) => {
					if (res.statusCode === 200) {
						const data = JSON.parse(res.data)
						data.code === 200 ? resolve(data.data) : reject(new Error(data.msg))
					} else {
						reject(new Error('上传失败'))
					}
				},
				fail: reject
			})
		})
	}
}
