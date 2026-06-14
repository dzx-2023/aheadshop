import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/index'

export const useCartStore = defineStore('cart', () => {
	const cartList = ref([])
	const loading = ref(false)

	// 购物车商品数量
	const cartCount = computed(() => {
		return cartList.value.reduce((sum, item) => sum + item.quantity, 0)
	})

	// 选中的商品
	const checkedItems = computed(() => {
		return cartList.value.filter(item => item.checked === 1)
	})

	// 总价
	const totalPrice = computed(() => {
		return checkedItems.value
			.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
			.toFixed(2)
	})

	// 是否全选
	const isAllChecked = computed(() => {
		return cartList.value.length > 0 && cartList.value.every(item => item.checked === 1)
	})

	// 从服务端加载购物车
	const fetchCart = async () => {
		loading.value = true
		try {
			const res = await cartApi.getList()
			cartList.value = (res || []).map(item => ({
				skuId: item.skuId,
				spuId: item.spuId,
				name: item.skuName,
				image: item.image,
				price: item.price,
				specs: item.specs,
				quantity: item.quantity,
				checked: item.checked
			}))
		} catch (error) {
			console.error('加载购物车失败', error)
		} finally {
			loading.value = false
		}
	}

	// 添加商品到购物车（服务端）
	const addToCart = async ({ skuId, quantity = 1 }) => {
		try {
			await cartApi.add({ skuId, quantity })
			await fetchCart()
			uni.showToast({ title: '已加入购物车', icon: 'success' })
		} catch (error) {
			console.error('添加购物车失败', error)
		}
	}

	// 从购物车移除
	const removeFromCart = async (skuId) => {
		try {
			await cartApi.remove(skuId)
			cartList.value = cartList.value.filter(item => item.skuId !== skuId)
		} catch (error) {
			console.error('删除购物车商品失败', error)
		}
	}

	// 更新商品数量
	const updateQuantity = async (skuId, quantity) => {
		if (quantity <= 0) {
			await removeFromCart(skuId)
			return
		}
		try {
			await cartApi.update({ skuId, quantity })
			const item = cartList.value.find(i => i.skuId === skuId)
			if (item) item.quantity = quantity
		} catch (error) {
			console.error('更新数量失败', error)
		}
	}

	// 切换选中状态
	const toggleCheck = async (skuId) => {
		const item = cartList.value.find(i => i.skuId === skuId)
		if (!item) return
		const newChecked = item.checked === 1 ? 0 : 1
		try {
			await cartApi.check(skuId, newChecked)
			item.checked = newChecked
		} catch (error) {
			console.error('切换选中失败', error)
		}
	}

	// 全选/取消全选
	const toggleSelectAll = async () => {
		const target = isAllChecked.value ? 0 : 1
		try {
			const promises = cartList.value.map(item => cartApi.check(item.skuId, target))
			await Promise.all(promises)
			cartList.value.forEach(item => { item.checked = target })
		} catch (error) {
			console.error('全选操作失败', error)
		}
	}

	// 清空购物车
	const clearCart = async () => {
		try {
			// 逐个删除（后端无清空接口）
			const promises = cartList.value.map(item => cartApi.remove(item.skuId))
			await Promise.all(promises)
			cartList.value = []
		} catch (error) {
			console.error('清空购物车失败', error)
		}
	}

	return {
		cartList,
		cartCount,
		checkedItems,
		totalPrice,
		isAllChecked,
		loading,
		fetchCart,
		addToCart,
		removeFromCart,
		updateQuantity,
		toggleCheck,
		toggleSelectAll,
		clearCart
	}
})
