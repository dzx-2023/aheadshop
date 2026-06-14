<template>
	<view class="container">
		<!-- 用户信息区域 -->
		<view class="user-header">
			<view class="user-info" v-if="isLoggedIn">
				<image :src="userInfo.avatar" class="avatar" mode="aspectFill" />
				<view class="user-detail">
					<text class="username">{{ userInfo.nickname }}</text>
					<text class="user-id">ID: {{ userInfo.id }}</text>
				</view>
			</view>
			<view class="user-info" v-else @click="goLogin">
				<image src="/static/avatar-default.png" class="avatar" mode="aspectFill" />
				<view class="user-detail">
					<text class="login-text">点击登录</text>
					<text class="login-tip">登录后享受更多权益</text>
				</view>
			</view>
		</view>

		<!-- 订单区域 -->
		<view class="order-section">
			<view class="section-header">
				<text class="section-title">我的订单</text>
				<view class="section-more" @click="goOrderList">
					<text>查看全部 ></text>
				</view>
			</view>
			<view class="order-grid">
				<view class="order-item" @click="goOrderList(0)">
					<image src="/static/order/unpaid.png" class="order-icon" />
					<text class="order-name">待付款</text>
				</view>
				<view class="order-item" @click="goOrderList(1)">
					<image src="/static/order/unshipped.png" class="order-icon" />
					<text class="order-name">待发货</text>
				</view>
				<view class="order-item" @click="goOrderList(2)">
					<image src="/static/order/shipped.png" class="order-icon" />
					<text class="order-name">待收货</text>
				</view>
				<view class="order-item" @click="goOrderList(3)">
					<image src="/static/order/review.png" class="order-icon" />
					<text class="order-name">待评价</text>
				</view>
				<view class="order-item" @click="goOrderList(4)">
					<image src="/static/order/after-sale.png" class="order-icon" />
					<text class="order-name">售后</text>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-item" @click="goPage('/pages/coupon/list')">
				<image src="/static/menu/coupon.png" class="menu-icon" />
				<text class="menu-name">优惠券</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goPage('/pages/collection/list')">
				<image src="/static/menu/collection.png" class="menu-icon" />
				<text class="menu-name">我的收藏</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goPage('/pages/address/list')">
				<image src="/static/menu/address.png" class="menu-icon" />
				<text class="menu-name">收货地址</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goPage('/pages/history/list')">
				<image src="/static/menu/history.png" class="menu-icon" />
				<text class="menu-name">浏览记录</text>
				<text class="menu-arrow">></text>
			</view>
		</view>

		<view class="menu-section">
			<view class="menu-item" @click="goPage('/pages/help/index')">
				<image src="/static/menu/help.png" class="menu-icon" />
				<text class="menu-name">帮助中心</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goPage('/pages/feedback/index')">
				<image src="/static/menu/feedback.png" class="menu-icon" />
				<text class="menu-name">意见反馈</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goPage('/pages/about/index')">
				<image src="/static/menu/about.png" class="menu-icon" />
				<text class="menu-name">关于我们</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goPage('/pages/settings/index')">
				<image src="/static/menu/settings.png" class="menu-icon" />
				<text class="menu-name">设置</text>
				<text class="menu-arrow">></text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

const isLoggedIn = ref(false)

const userInfo = ref({
	nickname: '用户昵称',
	id: '100001',
	avatar: '/static/avatar.png'
})

const goLogin = () => {
	uni.navigateTo({
		url: '/pages/login/login'
	})
}

const goOrderList = (status) => {
	uni.navigateTo({
		url: `/pages/order/list?status=${status || 0}`
	})
}

const goPage = (url) => {
	uni.navigateTo({
		url: url
	})
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
}

.user-header {
	background: linear-gradient(135deg, #ff6b35 0%, #ff8f65 100%);
	padding: 60rpx 40rpx 40rpx;
}

.user-info {
	display: flex;
	align-items: center;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	border: 4rpx solid rgba(255, 255, 255, 0.5);
	margin-right: 30rpx;
}

.user-detail {
	display: flex;
	flex-direction: column;
}

.username {
	font-size: 36rpx;
	color: #ffffff;
	font-weight: bold;
	margin-bottom: 10rpx;
}

.user-id {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.login-text {
	font-size: 36rpx;
	color: #ffffff;
	font-weight: bold;
	margin-bottom: 10rpx;
}

.login-tip {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.order-section {
	background-color: #ffffff;
	margin: 20rpx;
	border-radius: 16rpx;
	padding: 30rpx;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.section-more {
	font-size: 24rpx;
	color: #999999;
}

.order-grid {
	display: flex;
	justify-content: space-between;
}

.order-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.order-icon {
	width: 60rpx;
	height: 60rpx;
	margin-bottom: 12rpx;
}

.order-name {
	font-size: 24rpx;
	color: #666666;
}

.menu-section {
	background-color: #ffffff;
	margin: 20rpx;
	border-radius: 16rpx;
	overflow: hidden;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-icon {
	width: 44rpx;
	height: 44rpx;
	margin-right: 24rpx;
}

.menu-name {
	flex: 1;
	font-size: 28rpx;
	color: #333333;
}

.menu-arrow {
	font-size: 28rpx;
	color: #cccccc;
}
</style>
