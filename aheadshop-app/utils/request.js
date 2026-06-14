const BASE_URL = 'http://localhost:8080'

const request = (options) => {
	return new Promise((resolve, reject) => {
		const token = uni.getStorageSync('token')

		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data || {},
			header: {
				'Content-Type': 'application/json',
				'Authorization': token ? `Bearer ${token}` : '',
				...options.header
			},
			success: (res) => {
				if (res.statusCode === 200) {
					if (res.data.code === 200) {
						resolve(res.data.data)
					} else if (res.data.code === 401) {
						// token 过期，跳转登录
						uni.removeStorageSync('token')
						uni.removeStorageSync('userInfo')
						uni.navigateTo({
							url: '/pages/login/login'
						})
						reject(new Error('登录已过期，请重新登录'))
					} else {
						uni.showToast({
							title: res.data.msg || '请求失败',
							icon: 'none'
						})
						reject(new Error(res.data.msg))
					}
				} else {
					uni.showToast({
						title: '网络请求失败',
						icon: 'none'
					})
					reject(new Error('网络请求失败'))
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络连接失败',
					icon: 'none'
				})
				reject(err)
			}
		})
	})
}

export const get = (url, data) => {
	return request({ url, method: 'GET', data })
}

export const post = (url, data) => {
	return request({ url, method: 'POST', data })
}

export const put = (url, data) => {
	return request({ url, method: 'PUT', data })
}

export const del = (url, data) => {
	return request({ url, method: 'DELETE', data })
}

export default request
