<template>
	<view class="container">
		<view class="header">
			<text class="title">注册账号</text>
			<text class="subtitle">创建您的 AheadShop 账号</text>
		</view>

		<view class="form">
			<view class="form-item">
				<text class="label">用户名</text>
				<input
					type="text"
					v-model="form.username"
					placeholder="请输入用户名（4-20位）"
					maxlength="20"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">密码</text>
				<input
					type="text"
					v-model="form.password"
					placeholder="请设置密码（6-20位）"
					password
					maxlength="20"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">确认密码</text>
				<input
					type="text"
					v-model="form.confirmPassword"
					placeholder="请再次输入密码"
					password
					maxlength="20"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">昵称</text>
				<input
					type="text"
					v-model="form.nickname"
					placeholder="请输入昵称（选填）"
					maxlength="20"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">手机号</text>
				<input
					type="number"
					v-model="form.phone"
					placeholder="请输入手机号（选填）"
					maxlength="11"
					class="input"
				/>
			</view>

			<view class="agreement">
				<view class="checkbox" :class="{ checked: isAgreed }" @click="isAgreed = !isAgreed">
					<text v-if="isAgreed">✓</text>
				</view>
				<text class="agreement-text">
					我已阅读并同意
					<text class="link" @click.stop="goAgreement('user')">《用户协议》</text>
					和
					<text class="link" @click.stop="goAgreement('privacy')">《隐私政策》</text>
				</text>
			</view>

			<button class="btn-register" @click="handleRegister" :disabled="isSubmitting">
				{{ isSubmitting ? '注册中...' : '注册' }}
			</button>

			<view class="login-link">
				<text>已有账号？</text>
				<text class="link" @click="goLogin">立即登录</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { post } from '@/utils/request'
import { showToast } from '@/utils/util'

const form = reactive({
	username: '',
	password: '',
	confirmPassword: '',
	nickname: '',
	phone: ''
})

const isAgreed = ref(false)
const isSubmitting = ref(false)

const handleRegister = async () => {
	if (!form.username) {
		showToast('请输入用户名')
		return
	}
	if (form.username.length < 4) {
		showToast('用户名长度不能少于4位')
		return
	}
	if (!form.password) {
		showToast('请设置密码')
		return
	}
	if (form.password.length < 6) {
		showToast('密码长度不能少于6位')
		return
	}
	if (form.password !== form.confirmPassword) {
		showToast('两次输入的密码不一致')
		return
	}
	if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) {
		showToast('请输入正确的手机号')
		return
	}
	if (!isAgreed.value) {
		showToast('请阅读并同意用户协议')
		return
	}

	isSubmitting.value = true
	try {
		await post('/api/user/register', {
			username: form.username,
			password: form.password,
			nickname: form.nickname || form.username,
			phone: form.phone || undefined
		})
		showToast('注册成功')
		setTimeout(() => {
			uni.navigateBack()
		}, 1500)
	} catch (error) {
		console.error('注册失败', error)
	} finally {
		isSubmitting.value = false
	}
}

const goLogin = () => {
	uni.navigateBack()
}

const goAgreement = (type) => {
	uni.showToast({ title: '功能开发中', icon: 'none' })
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #ffffff;
	padding: 0 60rpx;
}

.header {
	padding-top: 120rpx;
	margin-bottom: 60rpx;
}

.title {
	font-size: 48rpx;
	font-weight: bold;
	color: #333333;
	display: block;
	margin-bottom: 16rpx;
}

.subtitle {
	font-size: 28rpx;
	color: #999999;
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

.agreement {
	display: flex;
	align-items: flex-start;
	margin-bottom: 40rpx;
}

.checkbox {
	width: 36rpx;
	height: 36rpx;
	border: 2rpx solid #cccccc;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 12rpx;
	margin-top: 4rpx;
	flex-shrink: 0;
}

.checkbox.checked {
	background-color: #ff6b35;
	border-color: #ff6b35;
}

.checkbox text {
	font-size: 24rpx;
	color: #ffffff;
}

.agreement-text {
	font-size: 24rpx;
	color: #666666;
	line-height: 1.5;
}

.link {
	color: #ff6b35;
}

.btn-register {
	width: 100%;
	height: 88rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 32rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-register[disabled] {
	opacity: 0.6;
}

.login-link {
	text-align: center;
	margin-top: 32rpx;
}

.login-link text {
	font-size: 26rpx;
	color: #999999;
}

.login-link .link {
	color: #ff6b35;
	margin-left: 8rpx;
}
</style>
