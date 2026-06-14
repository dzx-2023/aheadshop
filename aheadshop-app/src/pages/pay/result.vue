<template>
	<view class="container">
		<view class="result-section">
			<view class="status-icon" v-if="payStatus === 0">⏳</view>
			<view class="status-icon" v-else-if="payStatus === 1">✅</view>
			<view class="status-icon" v-else>❌</view>

			<text class="status-text">{{ statusText }}</text>
			<text class="status-tip">{{ statusTip }}</text>
		</view>

		<view class="order-info" v-if="orderNo">
			<view class="info-item">
				<text class="info-label">订单编号</text>
				<text class="info-value">{{ orderNo }}</text>
			</view>
		</view>

		<view class="actions">
			<button class="btn-primary" v-if="payStatus === 1" @click="goOrderDetail">查看订单</button>
			<button class="btn-primary" v-else-if="payStatus === 0" @click="checkStatus">刷新状态</button>
			<button class="btn-primary" v-else @click="retryPay">重新支付</button>
			<button class="btn-default" @click="goHome">返回首页</button>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { payApi } from '@/api/index'

const payNo = ref('')
const orderNo = ref('')
// 0=等待支付, 1=支付成功, 2=支付失败
const payStatus = ref(0)
let pollTimer = null

onLoad((options) => {
	if (options) {
		if (options.payNo) payNo.value = options.payNo
		if (options.orderNo) orderNo.value = options.orderNo
	}
	if (payNo.value) {
		startPolling()
	}
})

onUnload(() => {
	if (pollTimer) {
		clearInterval(pollTimer)
		pollTimer = null
	}
})

const statusText = ref('等待支付结果...')
const statusTip = ref('请在支付宝完成支付，正在查询支付状态')

const startPolling = () => {
	// 每 2 秒轮询一次，最多 60 次（2 分钟）
	let count = 0
	pollTimer = setInterval(async () => {
		count++
		if (count > 60) {
			clearInterval(pollTimer)
			payStatus.value = 2
			statusText.value = '查询超时'
			statusTip.value = '请前往订单页面查看支付状态'
			return
		}
		await checkStatus()
	}, 2000)
}

const checkStatus = async () => {
	try {
		const status = await payApi.queryStatus(payNo.value)
		// status: 0=待支付, 1=已支付, 2=已关闭, 3=已退款
		if (status === 1) {
			clearInterval(pollTimer)
			payStatus.value = 1
			statusText.value = '支付成功'
			statusTip.value = '感谢您的购买，商家将尽快发货'
		} else if (status === 2 || status === 3) {
			clearInterval(pollTimer)
			payStatus.value = 2
			statusText.value = '支付未完成'
			statusTip.value = '订单已关闭或已退款'
		}
	} catch (error) {
		console.error('查询支付状态失败', error)
	}
}

const retryPay = () => {
	uni.navigateBack()
}

const goOrderDetail = () => {
	uni.redirectTo({
		url: `/pages/order/detail?orderNo=${orderNo.value}`
	})
}

const goHome = () => {
	uni.switchTab({ url: '/pages/index/index' })
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
}

.result-section {
	background-color: #ffffff;
	padding: 80rpx 40rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 20rpx;
}

.status-icon {
	font-size: 120rpx;
	margin-bottom: 24rpx;
}

.status-text {
	font-size: 36rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
}

.status-tip {
	font-size: 26rpx;
	color: #999999;
	text-align: center;
}

.order-info {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 20rpx;
}

.info-item {
	display: flex;
	justify-content: space-between;
	padding: 12rpx 0;
}

.info-label {
	font-size: 28rpx;
	color: #999999;
}

.info-value {
	font-size: 28rpx;
	color: #333333;
}

.actions {
	padding: 40rpx;
}

.btn-primary {
	width: 100%;
	height: 88rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 30rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 24rpx;
}

.btn-default {
	width: 100%;
	height: 88rpx;
	background-color: #ffffff;
	color: #666666;
	font-size: 30rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border: 2rpx solid #cccccc;
}
</style>
