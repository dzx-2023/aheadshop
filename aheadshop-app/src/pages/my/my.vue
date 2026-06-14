<template>
	<view class="container">
		<!-- 用户信息区域 -->
		<view class="user-header">
			<view class="user-info" v-if="userStore.isLoggedIn" @click="goUserInfo">
				<image :src="userStore.userInfo.avatar || '/static/avatar-default.png'" class="avatar" mode="aspectFill" />
				<view class="user-detail">
					<text class="username">{{ userStore.userInfo.nickname || '用户' }}</text>
					<text class="user-id">ID: {{ userStore.userInfo.id }}</text>
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
				<view class="section-more" @click="goOrderList()">
					<text>查看全部 ></text>
				</view>
			</view>
			<view class="order-grid">
				<view class="order-item" @click="goOrderList(0)">
					<view class="order-icon-text">💰</view>
					<text class="order-name">待付款</text>
				</view>
				<view class="order-item" @click="goOrderList(1)">
					<view class="order-icon-text">📦</view>
					<text class="order-name">待发货</text>
				</view>
				<view class="order-item" @click="goOrderList(2)">
					<view class="order-icon-text">🚚</view>
					<text class="order-name">待收货</text>
				</view>
				<view class="order-item" @click="goOrderList(3)">
					<view class="order-icon-text">✅</view>
					<text class="order-name">已完成</text>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-item" @click="goPage('/pages/address/list')">
				<text class="menu-icon-text">📍</text>
				<text class="menu-name">收货地址</text>
				<text class="menu-arrow">></text>
			</view>
		</view>

		<view class="menu-section">
			<view class="menu-item" @click="showTip">
				<text class="menu-icon-text">🎫</text>
				<text class="menu-name">优惠券</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="showTip">
				<text class="menu-icon-text">❤️</text>
				<text class="menu-name">我的收藏</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="showTip">
				<text class="menu-icon-text">🕐</text>
				<text class="menu-name">浏览记录</text>
				<text class="menu-arrow">></text>
			</view>
		</view>

		<view class="menu-section">
			<view class="menu-item" @click="showTip">
				<text class="menu-icon-text">❓</text>
				<text class="menu-name">帮助中心</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="showTip">
				<text class="menu-icon-text">💬</text>
				<text class="menu-name">意见反馈</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="showTip">
				<text class="menu-icon-text">ℹ️</text>
				<text class="menu-name">关于我们</text>
				<text class="menu-arrow">></text>
			</view>
		</view>

		<!-- 退出登录 -->
		<view class="logout-section" v-if="userStore.isLoggedIn">
			<view class="logout-btn" @click="onLogout">
				<text>退出登录</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { userApi } from '@/api/index'

const userStore = useUserStore()

onMounted(() => {
	if (userStore.isLoggedIn) {
		loadUserInfo()
	}
})

const loadUserInfo = async () => {
	try {
		const res = await userApi.getUserInfo()
		if (res) {
			userStore.setUserInfo({
				id: res.userId,
				nickname: res.nickname,
				avatar: res.avatar,
				phone: res.phone
			})
		}
	} catch (error) {
		console.error('加载用户信息失败', error)
	}
}

const goLogin = () => {
	uni.navigateTo({ url: '/pages/login/login' })
}

const goUserInfo = () => {
	// 暂无个人信息编辑页
}

const goOrderList = (status) => {
	uni.navigateTo({
		url: `/pages/order/list?status=${status ?? 0}`
	})
}

const goPage = (url) => {
	uni.navigateTo({ url })
}

const showTip = () => {
	uni.showToast({ title: '功能开发中', icon: 'none' })
}

const onLogout = () => {
	uni.showModal({
		title: '提示',
		content: '确定要退出登录吗？',
		success: (res) => {
			if (res.confirm) {
				userStore.logout()
				uni.showToast({ title: '已退出登录', icon: 'success' })
			}
		}
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

.order-icon-text {
	font-size: 48rpx;
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

.menu-icon-text {
	font-size: 40rpx;
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

.logout-section {
	padding: 40rpx 20rpx;
}

.logout-btn {
	padding: 24rpx 0;
	background-color: #ffffff;
	border-radius: 16rpx;
	text-align: center;
}

.logout-btn text {
	font-size: 30rpx;
	color: #ff4444;
}
</style>
