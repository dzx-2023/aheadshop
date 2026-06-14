import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
	const cartList = ref([])

	// 购物车商品数量
	const cartCount = computed(() => {
		return cartList.value.reduce((sum, item) => sum + item.quantity, 0)
	})

	// 选中的商品
	const checkedItems = computed(() => {
		return cartList.value.filter(item => item.checked)
	})

	// 总价
	const totalPrice = computed(() => {
		return checkedItems.value
			.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
			.toFixed(2)
	})

	// 添加商品到购物车
	const addToCart = (product) => {
		const existingItem = cartList.value.find(
			item => item.id === product.id && item.specId === product.specId
		)

		if (existingItem) {
			existingItem.quantity += product.quantity || 1
		} else {
			cartList.value.push({
				...product,
				quantity: product.quantity || 1,
				checked: true
			})
		}

		saveCartToStorage()
		uni.showToast({
			title: '已加入购物车',
			icon: 'success'
		})
	}

	// 从购物车移除
	const removeFromCart = (index) => {
		cartList.value.splice(index, 1)
		saveCartToStorage()
	}

	// 更新商品数量
	const updateQuantity = (index, quantity) => {
		if (quantity <= 0) {
			removeFromCart(index)
		} else {
			cartList.value[index].quantity = quantity
			saveCartToStorage()
		}
	}

	// 切换选中状态
	const toggleCheck = (index) => {
		cartList.value[index].checked = !cartList.value[index].checked
		saveCartToStorage()
	}

	// 全选/取消全选
	const toggleSelectAll = (checked) => {
		cartList.value.forEach(item => {
			item.checked = checked
		})
		saveCartToStorage()
	}

	// 清空购物车
	const clearCart = () => {
		cartList.value = []
		saveCartToStorage()
	}

	// 保存到本地存储
	const saveCartToStorage = () => {
		uni.setStorageSync('cartList', JSON.stringify(cartList.value))
	}

	// 从本地存储加载
	const loadCartFromStorage = () => {
		const cartData = uni.getStorageSync('cartList')
		if (cartData) {
			cartList.value = JSON.parse(cartData)
		}
	}

	// 初始化时加载
	loadCartFromStorage()

	return {
		cartList,
		cartCount,
		checkedItems,
		totalPrice,
		addToCart,
		removeFromCart,
		updateQuantity,
		toggleCheck,
		toggleSelectAll,
		clearCart,
		loadCartFromStorage
	}
})
