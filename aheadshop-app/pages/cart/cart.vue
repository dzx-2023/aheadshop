<template>
	<view class="container">
		<!-- 空购物车 -->
		<view class="empty-cart" v-if="cartList.length === 0">
			<image src="/static/empty-cart.png" class="empty-image" mode="aspectFit" />
			<text class="empty-text">购物车是空的</text>
			<view class="empty-btn" @click="goShopping">
				<text>去逛逛</text>
			</view>
		</view>

		<!-- 购物车列表 -->
		<view class="cart-list" v-else>
			<view class="cart-item" v-for="(item, index) in cartList" :key="index">
				<view class="item-checkbox" :class="{ checked: item.checked }" @click="toggleCheck(index)">
					<text v-if="item.checked">✓</text>
				</view>
				<image :src="item.image" class="item-image" mode="aspectFill" />
				<view class="item-info">
					<text class="item-name">{{ item.name }}</text>
					<text class="item-spec">{{ item.spec }}</text>
					<view class="item-bottom">
						<text class="item-price">¥{{ item.price }}</text>
						<view class="quantity-control">
							<view class="qty-btn" @click="decreaseQty(index)">
								<text>-</text>
							</view>
							<text class="qty-num">{{ item.quantity }}</text>
							<view class="qty-btn" @click="increaseQty(index)">
								<text>+</text>
							</view>
						</view>
					</view>
				</view>
				<view class="item-delete" @click="deleteItem(index)">
					<text>删除</text>
				</view>
			</view>
		</view>

		<!-- 底部结算栏 -->
		<view class="checkout-bar" v-if="cartList.length > 0">
			<view class="select-all" @click="toggleSelectAll">
				<view class="item-checkbox" :class="{ checked: isAllChecked }">
					<text v-if="isAllChecked">✓</text>
				</view>
				<text>全选</text>
			</view>
			<view class="total-info">
				<text class="total-label">合计：</text>
				<text class="total-price">¥{{ totalPrice }}</text>
			</view>
			<view class="checkout-btn" @click="goCheckout">
				<text>结算({{ checkedCount }})</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue'

const cartList = ref([
	{
		name: '商品名称1',
		spec: '规格：黑色 XL',
		price: '99.00',
		quantity: 1,
		image: '/static/product/1.jpg',
		checked: true
	},
	{
		name: '商品名称2',
		spec: '规格：白色 M',
		price: '199.00',
		quantity: 2,
		image: '/static/product/2.jpg',
		checked: true
	},
	{
		name: '商品名称3',
		spec: '规格：红色 S',
		price: '299.00',
		quantity: 1,
		image: '/static/product/3.jpg',
		checked: false
	}
])

const isAllChecked = computed(() => {
	return cartList.value.length > 0 && cartList.value.every(item => item.checked)
})

const totalPrice = computed(() => {
	return cartList.value
		.filter(item => item.checked)
		.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
		.toFixed(2)
})

const checkedCount = computed(() => {
	return cartList.value.filter(item => item.checked).reduce((sum, item) => sum + item.quantity, 0)
})

const toggleCheck = (index) => {
	cartList.value[index].checked = !cartList.value[index].checked
}

const toggleSelectAll = () => {
	const newState = !isAllChecked.value
	cartList.value.forEach(item => {
		item.checked = newState
	})
}

const increaseQty = (index) => {
	cartList.value[index].quantity++
}

const decreaseQty = (index) => {
	if (cartList.value[index].quantity > 1) {
		cartList.value[index].quantity--
	}
}

const deleteItem = (index) => {
	uni.showModal({
		title: '提示',
		content: '确定要删除该商品吗？',
		success: (res) => {
			if (res.confirm) {
				cartList.value.splice(index, 1)
			}
		}
	})
}

const goShopping = () => {
	uni.switchTab({
		url: '/pages/index/index'
	})
}

const goCheckout = () => {
	const checkedItems = cartList.value.filter(item => item.checked)
	if (checkedItems.length === 0) {
		uni.showToast({
			title: '请选择商品',
			icon: 'none'
		})
		return
	}
	uni.navigateTo({
		url: '/pages/order/confirm'
	})
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.empty-cart {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 200rpx;
}

.empty-image {
	width: 300rpx;
	height: 300rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	font-size: 30rpx;
	color: #999999;
	margin-bottom: 40rpx;
}

.empty-btn {
	padding: 20rpx 60rpx;
	background-color: #ff6b35;
	border-radius: 40rpx;
}

.empty-btn text {
	font-size: 28rpx;
	color: #ffffff;
}

.cart-list {
	padding: 20rpx;
}

.cart-item {
	display: flex;
	align-items: center;
	padding: 24rpx;
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
}

.item-checkbox {
	width: 40rpx;
	height: 40rpx;
	border: 2rpx solid #cccccc;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
}

.item-checkbox.checked {
	background-color: #ff6b35;
	border-color: #ff6b35;
}

.item-checkbox text {
	font-size: 24rpx;
	color: #ffffff;
}

.item-image {
	width: 180rpx;
	height: 180rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.item-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.item-name {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 8rpx;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.item-spec {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 16rpx;
}

.item-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.item-price {
	font-size: 30rpx;
	color: #ff6b35;
	font-weight: bold;
}

.quantity-control {
	display: flex;
	align-items: center;
}

.qty-btn {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f5f5f5;
	border-radius: 8rpx;
}

.qty-btn text {
	font-size: 28rpx;
	color: #333333;
}

.qty-num {
	width: 60rpx;
	text-align: center;
	font-size: 28rpx;
	color: #333333;
}

.item-delete {
	margin-left: 20rpx;
	padding: 12rpx 20rpx;
	background-color: #ff4444;
	border-radius: 8rpx;
}

.item-delete text {
	font-size: 24rpx;
	color: #ffffff;
}

.checkout-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 100rpx;
	background-color: #ffffff;
	display: flex;
	align-items: center;
	padding: 0 24rpx;
	border-top: 1rpx solid #eeeeee;
}

.select-all {
	display: flex;
	align-items: center;
	margin-right: 40rpx;
}

.select-all text {
	font-size: 26rpx;
	color: #333333;
	margin-left: 12rpx;
}

.total-info {
	flex: 1;
	display: flex;
	align-items: center;
}

.total-label {
	font-size: 26rpx;
	color: #333333;
}

.total-price {
	font-size: 32rpx;
	color: #ff6b35;
	font-weight: bold;
}

.checkout-btn {
	padding: 16rpx 40rpx;
	background-color: #ff6b35;
	border-radius: 40rpx;
}

.checkout-btn text {
	font-size: 28rpx;
	color: #ffffff;
}
</style>
