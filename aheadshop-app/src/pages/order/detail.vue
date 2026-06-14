<template>
	<view class="container">
		<!-- 订单状态 -->
		<view class="status-section" :class="'status-' + order.status">
			<text class="status-text">{{ getStatusText(order.status) }}</text>
			<text class="status-tip">{{ getStatusTip(order.status) }}</text>
		</view>

		<!-- 收货地址 -->
		<view class="address-section" v-if="order.receiverName">
			<view class="address-icon">📍</view>
			<view class="address-info">
				<view class="address-top">
					<text class="address-name">{{ order.receiverName }}</text>
					<text class="address-phone">{{ order.receiverPhone }}</text>
				</view>
				<text class="address-detail">{{ order.receiverAddress }}</text>
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="goods-item" v-for="item in order.items" :key="item.skuId" @click="goProductDetail(item)">
				<image :src="item.skuImage" class="goods-image" mode="aspectFill" />
				<view class="goods-info">
					<text class="goods-name">{{ item.skuName }}</text>
					<text class="goods-spec">{{ item.specs }}</text>
					<view class="goods-bottom">
						<text class="goods-price">¥{{ item.price }}</text>
						<text class="goods-quantity">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单信息 -->
		<view class="info-section">
			<view class="info-item">
				<text class="info-label">订单编号</text>
				<text class="info-value" @click="copyOrderNo">{{ order.orderNo }}</text>
			</view>
			<view class="info-item">
				<text class="info-label">下单时间</text>
				<text class="info-value">{{ order.createTime }}</text>
			</view>
			<view class="info-item" v-if="order.payTime">
				<text class="info-label">支付时间</text>
				<text class="info-value">{{ order.payTime }}</text>
			</view>
			<view class="info-item" v-if="order.payType">
				<text class="info-label">支付方式</text>
				<text class="info-value">{{ getPayTypeText(order.payType) }}</text>
			</view>
			<view class="info-item" v-if="order.trackingNumber">
				<text class="info-label">物流单号</text>
				<text class="info-value">{{ order.trackingNumber }}</text>
			</view>
		</view>

		<!-- 金额明细 -->
		<view class="amount-section">
			<view class="amount-item">
				<text class="amount-label">商品金额</text>
				<text class="amount-value">¥{{ order.totalAmount }}</text>
			</view>
			<view class="amount-item" v-if="order.freightAmount && parseFloat(order.freightAmount) > 0">
				<text class="amount-label">运费</text>
				<text class="amount-value">¥{{ order.freightAmount }}</text>
			</view>
			<view class="amount-item" v-if="order.discountAmount && parseFloat(order.discountAmount) > 0">
				<text class="amount-label">优惠</text>
				<text class="amount-value discount">-¥{{ order.discountAmount }}</text>
			</view>
			<view class="amount-item total">
				<text class="amount-label">实付金额</text>
				<text class="amount-value price">¥{{ order.payAmount }}</text>
			</view>
		</view>

		<!-- 备注 -->
		<view class="remark-section" v-if="order.remark">
			<text class="remark-label">订单备注：</text>
			<text class="remark-value">{{ order.remark }}</text>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar" v-if="showActions">
			<button class="btn-action btn-default" v-if="order.status === 0" @click="cancelOrder">取消订单</button>
			<button class="btn-action btn-primary" v-if="order.status === 0" @click="payOrder">去支付</button>
			<button class="btn-action btn-primary" v-if="order.status === 2" @click="confirmReceive">确认收货</button>
			<button class="btn-action btn-default" v-if="order.status === 3" @click="goShopping">再次购买</button>
		</view>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderApi, payApi } from '@/api/index'
import { showToast, showLoading, hideLoading } from '@/utils/util'

const orderNo = ref('')

onLoad((options) => {
	if (options && options.orderNo) {
		orderNo.value = options.orderNo
		loadOrderDetail()
	}
})

const order = ref({
	orderNo: '',
	status: 0,
	totalAmount: '0.00',
	payAmount: '0.00',
	discountAmount: '0.00',
	freightAmount: '0.00',
	payType: null,
	payTime: '',
	shipTime: '',
	trackingNumber: '',
	receiverName: '',
	receiverPhone: '',
	receiverAddress: '',
	remark: '',
	createTime: '',
	items: []
})

const showActions = computed(() => {
	return [0, 2, 3].includes(order.value.status)
})

const loadOrderDetail = async () => {
	try {
		const res = await orderApi.getDetail(orderNo.value)
		if (res) order.value = res
	} catch (error) {
		console.error('加载订单详情失败', error)
	}
}

const getStatusText = (status) => {
	const map = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成' }
	return map[status] || '未知'
}

const getStatusTip = (status) => {
	const map = {
		0: '请在30分钟内完成支付',
		1: '商家正在准备发货',
		2: '商品正在配送中',
		3: '感谢您的购买'
	}
	return map[status] || ''
}

const getPayTypeText = (type) => {
	const map = { 1: '支付宝', 2: '微信支付' }
	return map[type] || '未知'
}

const copyOrderNo = () => {
	uni.setClipboardData({
		data: order.value.orderNo,
		success: () => showToast('已复制订单号')
	})
}

const goProductDetail = (item) => {
	uni.navigateTo({ url: `/pages/product/detail?id=${item.spuId}` })
}

const payOrder = async () => {
	showLoading('发起支付...')
	try {
		// payType: 1=支付宝
		const res = await payApi.create({
			orderNo: order.value.orderNo,
			payType: 1,
			returnUrl: `${window.location.origin}/pay/result`
		})
		if (res && res.payForm) {
			// H5 支付宝：将返回的 HTML 表单写入新窗口并自动提交
			const payWindow = window.open('', '_blank')
			if (payWindow) {
				payWindow.document.write(res.payForm)
				payWindow.document.close()
			} else {
				// 弹窗被阻止时，在当前页面提交
				const div = document.createElement('div')
				div.innerHTML = res.payForm
				div.style.display = 'none'
				document.body.appendChild(div)
				const form = div.querySelector('form')
				if (form) form.submit()
			}
			// 跳转到支付结果页轮询
			uni.navigateTo({
				url: `/pages/pay/result?payNo=${res.payNo}&orderNo=${order.value.orderNo}`
			})
		} else {
			showToast('支付创建失败')
		}
	} catch (error) {
		console.error('发起支付失败', error)
	} finally {
		hideLoading()
	}
}

const cancelOrder = () => {
	uni.showModal({
		title: '提示',
		content: '确定要取消该订单吗？',
		success: async (res) => {
			if (res.confirm) {
				showLoading('取消中...')
				try {
					await orderApi.cancel(order.value.orderNo)
					showToast('订单已取消')
					loadOrderDetail()
				} catch (error) {
					console.error('取消订单失败', error)
				} finally {
					hideLoading()
				}
			}
		}
	})
}

const confirmReceive = () => {
	uni.showModal({
		title: '提示',
		content: '确认已收到商品？',
		success: async (res) => {
			if (res.confirm) {
				showLoading('确认中...')
				try {
					await orderApi.confirmReceive(order.value.orderNo)
					showToast('确认收货成功')
					loadOrderDetail()
				} catch (error) {
					console.error('确认收货失败', error)
				} finally {
					hideLoading()
				}
			}
		}
	})
}

const goShopping = () => {
	uni.switchTab({ url: '/pages/index/index' })
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.status-section {
	padding: 40rpx 30rpx;
	background: linear-gradient(135deg, #ff6b35 0%, #ff8f65 100%);
}

.status-section.status-0 {
	background: linear-gradient(135deg, #ff6b35 0%, #ff8f65 100%);
}

.status-section.status-1 {
	background: linear-gradient(135deg, #4395ff 0%, #6ab5ff 100%);
}

.status-section.status-2 {
	background: linear-gradient(135deg, #4395ff 0%, #6ab5ff 100%);
}

.status-section.status-3 {
	background: linear-gradient(135deg, #999999 0%, #bbbbbb 100%);
}

.status-text {
	font-size: 40rpx;
	color: #ffffff;
	font-weight: bold;
	display: block;
	margin-bottom: 8rpx;
}

.status-tip {
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.8);
}

.address-section {
	display: flex;
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.address-icon {
	font-size: 40rpx;
	margin-right: 16rpx;
}

.address-info {
	flex: 1;
}

.address-top {
	display: flex;
	margin-bottom: 8rpx;
}

.address-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	margin-right: 24rpx;
}

.address-phone {
	font-size: 28rpx;
	color: #666666;
}

.address-detail {
	font-size: 26rpx;
	color: #999999;
}

.goods-section {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.goods-item {
	display: flex;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.goods-item:last-child {
	border-bottom: none;
}

.goods-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.goods-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.goods-name {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 8rpx;
}

.goods-spec {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 16rpx;
}

.goods-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.goods-price {
	font-size: 30rpx;
	color: #333333;
}

.goods-quantity {
	font-size: 26rpx;
	color: #999999;
}

.info-section {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.info-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12rpx 0;
}

.info-label {
	font-size: 26rpx;
	color: #999999;
}

.info-value {
	font-size: 26rpx;
	color: #333333;
}

.amount-section {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.amount-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12rpx 0;
}

.amount-item.total {
	border-top: 1rpx solid #eeeeee;
	margin-top: 12rpx;
	padding-top: 20rpx;
}

.amount-label {
	font-size: 28rpx;
	color: #666666;
}

.amount-value {
	font-size: 28rpx;
	color: #333333;
}

.amount-value.discount {
	color: #ff6b35;
}

.amount-value.price {
	font-size: 32rpx;
	color: #ff6b35;
	font-weight: bold;
}

.remark-section {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
	display: flex;
}

.remark-label {
	font-size: 26rpx;
	color: #999999;
	white-space: nowrap;
}

.remark-value {
	font-size: 26rpx;
	color: #333333;
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 100rpx;
	background-color: #ffffff;
	display: flex;
	align-items: center;
	justify-content: flex-end;
	padding: 0 24rpx;
	border-top: 1rpx solid #eeeeee;
}

.btn-action {
	padding: 12rpx 32rpx;
	font-size: 28rpx;
	border-radius: 40rpx;
	margin-left: 20rpx;
}

.btn-primary {
	background-color: #ff6b35;
	color: #ffffff;
}

.btn-default {
	background-color: #ffffff;
	color: #666666;
	border: 2rpx solid #cccccc;
}
</style>
