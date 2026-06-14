<template>
	<view class="container">
		<!-- 空购物车 -->
		<view class="empty-cart" v-if="!loading && cartList.length === 0">
			<view class="empty-icon">🛒</view>
			<text class="empty-text">购物车是空的</text>
			<view class="empty-btn" @click="goShopping">
				<text>去逛逛</text>
			</view>
		</view>

		<!-- 加载中 -->
		<view class="loading" v-if="loading">
			<text>加载中...</text>
		</view>

		<!-- 购物车列表 -->
		<view class="cart-list" v-if="!loading && cartList.length > 0">
			<view class="cart-item" v-for="item in cartList" :key="item.skuId">
				<view class="item-checkbox" :class="{ checked: item.checked === 1 }" @click="onToggleCheck(item)">
					<text v-if="item.checked === 1">✓</text>
				</view>
				<image :src="item.image" class="item-image" mode="aspectFill" />
				<view class="item-info">
					<text class="item-name">{{ item.name }}</text>
					<text class="item-spec">{{ item.specs }}</text>
					<view class="item-bottom">
						<text class="item-price">¥{{ item.price }}</text>
						<view class="quantity-control">
							<view class="qty-btn" @click="onDecrease(item)">
								<text>-</text>
							</view>
							<text class="qty-num">{{ item.quantity }}</text>
							<view class="qty-btn" @click="onIncrease(item)">
								<text>+</text>
							</view>
						</view>
					</view>
				</view>
				<view class="item-delete" @click="onDelete(item)">
					<text>删除</text>
				</view>
			</view>
		</view>

		<!-- 底部结算栏 -->
		<view class="checkout-bar" v-if="!loading && cartList.length > 0">
			<view class="select-all" @click="onToggleSelectAll">
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
				<text>结算({{ checkedItems.length }})</text>
			</view>
		</view>
	</view>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useCartStore } from '@/store/modules/cart'

const cartStore = useCartStore()

const cartList = computed(() => cartStore.cartList)
const loading = computed(() => cartStore.loading)
const isAllChecked = computed(() => cartStore.isAllChecked)
const totalPrice = computed(() => cartStore.totalPrice)
const checkedItems = computed(() => cartStore.checkedItems)

onMounted(() => {
	cartStore.fetchCart()
})

const onToggleCheck = (item) => {
	cartStore.toggleCheck(item.skuId)
}

const onToggleSelectAll = () => {
	cartStore.toggleSelectAll()
}

const onIncrease = (item) => {
	cartStore.updateQuantity(item.skuId, item.quantity + 1)
}

const onDecrease = (item) => {
	if (item.quantity > 1) {
		cartStore.updateQuantity(item.skuId, item.quantity - 1)
	}
}

const onDelete = (item) => {
	uni.showModal({
		title: '提示',
		content: '确定要删除该商品吗？',
		success: (res) => {
			if (res.confirm) {
				cartStore.removeFromCart(item.skuId)
			}
		}
	})
}

const goShopping = () => {
	uni.switchTab({ url: '/pages/index/index' })
}

const goCheckout = () => {
	if (checkedItems.value.length === 0) {
		uni.showToast({ title: '请选择商品', icon: 'none' })
		return
	}
	uni.navigateTo({ url: '/pages/order/confirm' })
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.loading {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 200rpx 0;
}

.loading text {
	font-size: 28rpx;
	color: #999999;
}

.empty-cart {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 200rpx;
}

.empty-icon {
	font-size: 120rpx;
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
