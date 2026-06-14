<template>
	<view class="container">
		<view class="header">
			<image src="/static/logo.png" class="logo" mode="aspectFit" />
			<text class="title">AheadShop</text>
			<text class="subtitle">欢迎回来</text>
		</view>

		<view class="form">
			<view class="form-item">
				<text class="label">用户名</text>
				<input
					type="text"
					v-model="form.username"
					placeholder="请输入用户名"
					maxlength="20"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">密码</text>
				<input
					type="text"
					v-model="form.password"
					placeholder="请输入密码"
					password
					class="input"
				/>
			</view>

			<button class="btn-login" @click="handleLogin" :disabled="isSubmitting">
				{{ isSubmitting ? '登录中...' : '登录' }}
			</button>

			<view class="register-link">
				<text>还没有账号？</text>
				<text class="link" @click="goRegister">立即注册</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { userApi } from '@/api/index'
import { post } from '@/utils/request'
import { showToast } from '@/utils/util'

const userStore = useUserStore()

const form = reactive({
	username: '',
	password: ''
})

const isSubmitting = ref(false)

const handleLogin = async () => {
	if (!form.username) {
		showToast('请输入用户名')
		return
	}
	if (!form.password) {
		showToast('请输入密码')
		return
	}
	if (form.password.length < 6) {
		showToast('密码长度不能少于6位')
		return
	}

	isSubmitting.value = true
	try {
		const res = await post('/api/user/login', {
			username: form.username,
			password: form.password
		})
		// 后端返回 LoginVO: { userId, username, nickname, accessToken, refreshToken }
		userStore.setToken(res.accessToken)
		userStore.setUserInfo({
			id: res.userId,
			nickname: res.nickname,
			avatar: '',
			phone: ''
		})
		// 登录后立即加载完整用户信息（含头像）
		try {
			const info = await userApi.getUserInfo()
			if (info) {
				userStore.setUserInfo({
					id: info.userId,
					nickname: info.nickname,
					avatar: info.avatar || '',
					phone: info.phone || ''
				})
			}
		} catch (e) {
			console.error('加载用户信息失败', e)
		}
		showToast('登录成功')
		setTimeout(() => {
			uni.switchTab({ url: '/pages/index/index' })
		}, 1500)
	} catch (error) {
		console.error('登录失败', error)
	} finally {
		isSubmitting.value = false
	}
}

const goRegister = () => {
	uni.navigateTo({ url: '/pages/register/register' })
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #ffffff;
	padding: 0 60rpx;
}

.header {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 160rpx;
	margin-bottom: 80rpx;
}

.logo {
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 24rpx;
}

.title {
	font-size: 48rpx;
	font-weight: bold;
	color: #ff6b35;
	margin-bottom: 16rpx;
}

.subtitle {
	font-size: 28rpx;
	color: #999999;
}

.form {
	margin-bottom: 60rpx;
}

.form-item {
	margin-bottom: 32rpx;
}

.label {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 16rpx;
	display: block;
}

.input {
	width: 100%;
	height: 88rpx;
	border: 2rpx solid #eeeeee;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 28rpx;
}

.input:focus {
	border-color: #ff6b35;
}

.btn-login {
	width: 100%;
	height: 88rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 32rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-top: 40rpx;
}

.btn-login[disabled] {
	opacity: 0.6;
}

.register-link {
	text-align: center;
	margin-top: 32rpx;
}

.register-link text {
	font-size: 26rpx;
	color: #999999;
}

.register-link .link {
	color: #ff6b35;
	margin-left: 8rpx;
}
</style>
