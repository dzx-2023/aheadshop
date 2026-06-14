import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
	const token = ref('')
	const userInfo = ref({
		id: '',
		nickname: '',
		avatar: '',
		phone: ''
	})
	const isLoggedIn = ref(false)

	// 设置 token
	const setToken = (newToken) => {
		token.value = newToken
		uni.setStorageSync('token', newToken)
	}

	// 获取 token
	const getToken = () => {
		if (!token.value) {
			token.value = uni.getStorageSync('token') || ''
		}
		return token.value
	}

	// 设置用户信息
	const setUserInfo = (info) => {
		userInfo.value = info
		isLoggedIn.value = true
		uni.setStorageSync('userInfo', JSON.stringify(info))
	}

	// 获取用户信息
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

	// 登录（由 login 页面直接调用 API，此方法备用）
	const login = async (loginData) => {
		try {
			const { post } = await import('@/utils/request')
			const res = await post('/api/user/login', loginData)
			setToken(res.accessToken)
			setUserInfo({
				id: res.userId,
				nickname: res.nickname,
				avatar: '',
				phone: loginData.username
			})
			return true
		} catch (error) {
			console.error('登录失败', error)
			return false
		}
	}

	// 退出登录
	const logout = () => {
		token.value = ''
		userInfo.value = { id: '', nickname: '', avatar: '', phone: '' }
		isLoggedIn.value = false
		uni.removeStorageSync('token')
		uni.removeStorageSync('userInfo')
	}

	return {
		token,
		userInfo,
		isLoggedIn,
		setToken,
		getToken,
		setUserInfo,
		getUserInfo,
		login,
		logout
	}
})
