/**
 * 格式化价格
 * @param {number|string} price - 价格
 * @param {number} decimals - 小数位数
 * @returns {string}
 */
export const formatPrice = (price, decimals = 2) => {
	return parseFloat(price).toFixed(decimals)
}

/**
 * 格式化日期
 * @param {Date|string|number} date - 日期
 * @param {string} format - 格式
 * @returns {string}
 */
export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
	if (!date) return ''

	const d = new Date(date)
	const year = d.getFullYear()
	const month = String(d.getMonth() + 1).padStart(2, '0')
	const day = String(d.getDate()).padStart(2, '0')
	const hours = String(d.getHours()).padStart(2, '0')
	const minutes = String(d.getMinutes()).padStart(2, '0')
	const seconds = String(d.getSeconds()).padStart(2, '0')

	return format
		.replace('YYYY', year)
		.replace('MM', month)
		.replace('DD', day)
		.replace('HH', hours)
		.replace('mm', minutes)
		.replace('ss', seconds)
}

/**
 * 防抖
 * @param {Function} fn - 函数
 * @param {number} delay - 延迟时间
 * @returns {Function}
 */
export const debounce = (fn, delay = 300) => {
	let timer = null
	return function(...args) {
		if (timer) clearTimeout(timer)
		timer = setTimeout(() => {
			fn.apply(this, args)
		}, delay)
	}
}

/**
 * 节流
 * @param {Function} fn - 函数
 * @param {number} delay - 延迟时间
 * @returns {Function}
 */
export const throttle = (fn, delay = 300) => {
	let last = 0
	return function(...args) {
		const now = Date.now()
		if (now - last > delay) {
			last = now
			fn.apply(this, args)
		}
	}
}

/**
 * 生成唯一ID
 * @returns {string}
 */
export const generateId = () => {
	return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

/**
 * 深拷贝
 * @param {*} obj - 对象
 * @returns {*}
 */
export const deepClone = (obj) => {
	if (obj === null || typeof obj !== 'object') return obj
	if (obj instanceof Date) return new Date(obj)
	if (obj instanceof RegExp) return new RegExp(obj)

	const cloned = Array.isArray(obj) ? [] : {}
	for (let key in obj) {
		if (obj.hasOwnProperty(key)) {
			cloned[key] = deepClone(obj[key])
		}
	}
	return cloned
}

/**
 * 手机号脱敏
 * @param {string} phone - 手机号
 * @returns {string}
 */
export const maskPhone = (phone) => {
	if (!phone) return ''
	return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 显示加载中
 * @param {string} title - 提示文字
 */
export const showLoading = (title = '加载中...') => {
	uni.showLoading({
		title,
		mask: true
	})
}

/**
 * 隐藏加载中
 */
export const hideLoading = () => {
	uni.hideLoading()
}

/**
 * 显示提示
 * @param {string} title - 提示文字
 * @param {string} icon - 图标类型
 */
export const showToast = (title, icon = 'none') => {
	uni.showToast({
		title,
		icon,
		duration: 2000
	})
}
