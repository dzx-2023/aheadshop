<template>
	<view class="container">
		<!-- 订单状态标签 -->
		<view class="status-tabs">
			<view
				class="tab-item"
				:class="{ active: currentStatus === index }"
				v-for="(item, index) in statusTabs"
				:key="index"
				@click="changeStatus(index)"
			>
				<text>{{ item }}</text>
			</view>
		</view>

		<!-- 订单列表 -->
		<scroll-view
			scroll-y
			class="order-list"
			@scrolltolower="loadMore"
			refresher-enabled
			@refresherrefresh="onRefresh"
			:refresher-triggered="isRefreshing"
		>
			<view class="order-item" v-for="order in orderList" :key="order.orderNo" @click="goDetail(order)">
				<view class="order-header">
					<text class="order-no">订单号：{{ order.orderNo }}</text>
					<text class="order-status">{{ getStatusText(order.status) }}</text>
				</view>
				<view class="order-info">
					<view class="info-row">
						<text class="info-label">收货人</text>
						<text class="info-value">{{ order.receiverName || '-' }}</text>
					</view>
					<view class="info-row">
						<text class="info-label">下单时间</text>
						<text class="info-value">{{ order.createTime }}</text>
					</view>
					<view class="info-row" v-if="order.trackingNumber">
						<text class="info-label">物流单号</text>
						<text class="info-value">{{ order.trackingNumber }}</text>
					</view>
				</view>
				<view class="order-footer">
					<text class="order-total">合计：<text class="price">¥{{ order.payAmount || order.totalAmount }}</text></text>
					<view class="order-actions">
						<button class="btn-action" v-if="order.status === 0" @click.stop="payOrder(order)">去支付</button>
						<button class="btn-action" v-if="order.status === 0" @click.stop="cancelOrder(order)">取消订单</button>
						<button class="btn-action" v-if="order.status === 2" @click.stop="confirmReceive(order)">确认收货</button>
						<button class="btn-action btn-default" v-if="order.status !== 0" @click.stop="goDetail(order)">查看详情</button>
					</view>
				</view>
			</view>

			<!-- 加载状态 -->
			<view class="loading-status">
				<text v-if="isLoading">加载中...</text>
				<text v-else-if="noMore">没有更多了</text>
			</view>

			<!-- 空状态 -->
			<view class="empty" v-if="!isLoading && orderList.length === 0">
				<text class="empty-text">暂无订单</text>
				<button class="btn-go-shopping" @click="goShopping">去逛逛</button>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { orderApi, payApi } from '@/api/index'
import { showToast, showLoading, hideLoading } from '@/utils/util'

const statusTabs = ['全部', '待付款', '待发货', '待收货', '已完成']
const currentStatus = ref(0)

onLoad((options) => {
	if (options && options.status) {
		currentStatus.value = parseInt(options.status) || 0
	}
	loadOrders()
})
const orderList = ref([])
const isLoading = ref(false)
const isRefreshing = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = 10

const loadOrders = async (isRefresh = false) => {
	if (isLoading.value) return

	isLoading.value = true
	try {
		if (isRefresh) {
			page.value = 1
			noMore.value = false
		}

		// 后端 status: null=全部, 0=待付款, 1=待发货, 2=待收货, 3=已完成
		const params = {
			pageNum: page.value,
			pageSize: pageSize
		}
		if (currentStatus.value > 0) {
			params.status = currentStatus.value - 1
		}

		const res = await orderApi.getList(params)
		const list = res?.records || []

		if (isRefresh) {
			orderList.value = list
		} else {
			orderList.value = [...orderList.value, ...list]
		}

		if (list.length < pageSize) {
			noMore.value = true
		}

		page.value++
	} catch (error) {
		console.error('加载订单失败', error)
	} finally {
		isLoading.value = false
		isRefreshing.value = false
	}
}

const changeStatus = (index) => {
	currentStatus.value = index
	loadOrders(true)
}

const getStatusText = (status) => {
	const statusMap = {
		0: '待付款',
		1: '待发货',
		2: '待收货',
		3: '已完成'
	}
	return statusMap[status] || '未知'
}

const loadMore = () => {
	if (!noMore.value) {
		loadOrders()
	}
}

const onRefresh = () => {
	isRefreshing.value = true
	loadOrders(true)
}

const goDetail = (order) => {
	uni.navigateTo({
		url: `/pages/order/detail?orderNo=${order.orderNo}`
	})
}

const payOrder = async (order) => {
	showLoading('发起支付...')
	try {
		const res = await payApi.create({
			orderNo: order.orderNo,
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
				const div = document.createElement('div')
				div.innerHTML = res.payForm
				div.style.display = 'none'
				document.body.appendChild(div)
				const form = div.querySelector('form')
				if (form) form.submit()
			}
			uni.navigateTo({
				url: `/pages/pay/result?payNo=${res.payNo}&orderNo=${order.orderNo}`
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

const cancelOrder = (order) => {
	uni.showModal({
		title: '提示',
		content: '确定要取消该订单吗？',
		success: async (res) => {
			if (res.confirm) {
				showLoading('取消中...')
				try {
					await orderApi.cancel(order.orderNo)
					order.status = 3 // 标记为已完成/已取消
					showToast('订单已取消')
					loadOrders(true)
				} catch (error) {
					console.error('取消订单失败', error)
				} finally {
					hideLoading()
				}
			}
		}
	})
}

const confirmReceive = (order) => {
	uni.showModal({
		title: '提示',
		content: '确认已收到商品？',
		success: async (res) => {
			if (res.confirm) {
				showLoading('确认中...')
				try {
					await orderApi.confirmReceive(order.orderNo)
					order.status = 3
					showToast('确认收货成功')
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
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.status-tabs {
	display: flex;
	background-color: #ffffff;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #eeeeee;
}

.tab-item {
	flex: 1;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 12rpx 0;
	position: relative;
}

.tab-item text {
	font-size: 28rpx;
	color: #666666;
}

.tab-item.active text {
	color: #ff6b35;
	font-weight: bold;
}

.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 48rpx;
	height: 4rpx;
	background-color: #ff6b35;
	border-radius: 2rpx;
}

.order-list {
	flex: 1;
}

.order-item {
	background-color: #ffffff;
	margin: 16rpx;
	border-radius: 16rpx;
	overflow: hidden;
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 24rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.order-no {
	font-size: 26rpx;
	color: #666666;
}

.order-status {
	font-size: 26rpx;
	color: #ff6b35;
}

.order-info {
	padding: 16rpx 24rpx;
}

.info-row {
	display: flex;
	justify-content: space-between;
	padding: 6rpx 0;
}

.info-label {
	font-size: 24rpx;
	color: #999999;
}

.info-value {
	font-size: 24rpx;
	color: #666666;
}

.order-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16rpx 24rpx;
	border-top: 1rpx solid #f5f5f5;
}

.order-total {
	font-size: 26rpx;
	color: #666666;
}

.order-total .price {
	color: #ff6b35;
	font-weight: bold;
}

.order-actions {
	display: flex;
}

.btn-action {
	padding: 8rpx 24rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 24rpx;
	border-radius: 24rpx;
	margin-left: 16rpx;
}

.btn-default {
	background-color: #ffffff;
	color: #666666;
	border: 2rpx solid #cccccc;
}

.loading-status {
	text-align: center;
	padding: 24rpx;
	font-size: 24rpx;
	color: #999999;
}

.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 200rpx 0;
}

.empty-text {
	font-size: 28rpx;
	color: #999999;
	margin-bottom: 40rpx;
}

.btn-go-shopping {
	padding: 16rpx 48rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 28rpx;
	border-radius: 40rpx;
}
</style>
