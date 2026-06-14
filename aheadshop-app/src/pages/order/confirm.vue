<template>
	<view class="container">
		<!-- 收货地址 -->
		<view class="address-section" @click="goAddressList">
			<view class="address-info" v-if="address">
				<view class="address-top">
					<text class="address-name">{{ address.receiverName }}</text>
					<text class="address-phone">{{ address.receiverPhone }}</text>
				</view>
				<text class="address-detail">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}</text>
			</view>
			<view class="no-address" v-else>
				<text>请选择收货地址</text>
			</view>
			<text class="arrow">></text>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="goods-item" v-for="item in orderGoods" :key="item.skuId">
				<image :src="item.image" class="goods-image" mode="aspectFill" />
				<view class="goods-info">
					<text class="goods-name">{{ item.name }}</text>
					<text class="goods-spec">{{ item.specs }}</text>
					<view class="goods-bottom">
						<text class="goods-price">¥{{ item.price }}</text>
						<text class="goods-quantity">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单信息 -->
		<view class="info-section">
			<view class="info-item">
				<text class="info-label">商品金额</text>
				<text class="info-value">¥{{ goodsTotal }}</text>
			</view>
			<view class="info-item">
				<text class="info-label">运费</text>
				<text class="info-value">¥{{ freight }}</text>
			</view>
		</view>

		<!-- 备注 -->
		<view class="remark-section">
			<text class="remark-label">订单备注</text>
			<input
				type="text"
				v-model="remark"
				placeholder="选填，请先和商家协商一致"
				maxlength="100"
				class="remark-input"
			/>
		</view>

		<!-- 底部提交栏 -->
		<view class="submit-bar">
			<view class="total-info">
				<text class="total-label">合计：</text>
				<text class="total-price">¥{{ totalAmount }}</text>
			</view>
			<button class="btn-submit" @click="submitOrder" :disabled="submitting">
				{{ submitting ? '提交中...' : '提交订单' }}
			</button>
		</view>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useCartStore } from '@/store/modules/cart'
import { orderApi, addressApi, productApi } from '@/api/index'
import { showToast, showLoading, hideLoading } from '@/utils/util'

const cartStore = useCartStore()

// 页面参数：立即购买模式有 spuId + skuId + quantity
const spuId = ref('')
const skuId = ref('')
const quantity = ref('1')

onLoad((options) => {
	if (options) {
		if (options.spuId) spuId.value = options.spuId
		if (options.skuId) skuId.value = options.skuId
		if (options.quantity) quantity.value = options.quantity
	}
	loadDefaultAddress()
	if (skuId.value) {
		loadDirectBuyGoods()
	} else {
		loadCartGoods()
	}
})

const address = ref(null)
const orderGoods = ref([])
const freight = ref('0.00')
const remark = ref('')
const submitting = ref(false)

const goodsTotal = computed(() => {
	return orderGoods.value
		.reduce((sum, item) => sum + parseFloat(item.price) * item.quantity, 0)
		.toFixed(2)
})

const totalAmount = computed(() => {
	return (parseFloat(goodsTotal.value) + parseFloat(freight.value)).toFixed(2)
})

const isDirectBuy = computed(() => !!skuId.value)

// 加载默认地址
const loadDefaultAddress = async () => {
	try {
		const res = await addressApi.getList()
		const list = res || []
		address.value = list.find(a => a.isDefault === 1) || list[0] || null
	} catch (error) {
		console.error('加载地址失败', error)
	}
}

// 立即购买模式：加载 SKU 信息
const loadDirectBuyGoods = async () => {
	try {
		const res = await productApi.getDetail(spuId.value)
		const sku = res.skus?.find(s => s.id == skuId.value) || res.skus?.[0]
		if (sku) {
			orderGoods.value = [{
				skuId: sku.id,
				name: sku.skuName || res.name,
				image: sku.image || res.mainImage,
				price: sku.price,
				specs: sku.specs || '',
				quantity: parseInt(quantity.value) || 1
			}]
		}
	} catch (error) {
		console.error('加载商品信息失败', error)
	}
}

// 购物车结算模式：从 store 取选中商品
const loadCartGoods = () => {
	orderGoods.value = cartStore.checkedItems.map(item => ({
		skuId: item.skuId,
		name: item.name,
		image: item.image,
		price: item.price,
		specs: item.specs,
		quantity: item.quantity
	}))
}

const goAddressList = () => {
	uni.navigateTo({
		url: '/pages/address/list?select=1',
		events: {
			selectAddress: (data) => {
				address.value = data
			}
		}
	})
}

const submitOrder = async () => {
	if (!address.value) {
		showToast('请选择收货地址')
		return
	}
	if (orderGoods.value.length === 0) {
		showToast('商品信息异常')
		return
	}

	submitting.value = true
	showLoading('提交中...')
	try {
		let res
		if (isDirectBuy.value) {
			// 立即购买
			res = await orderApi.create({
				addressId: address.value.id,
				skuId: parseInt(skuId.value),
				quantity: parseInt(quantity.value) || 1,
				remark: remark.value
			})
		} else {
			// 购物车结算
			res = await orderApi.create({
				addressId: address.value.id,
				remark: remark.value
			})
			// 清除购物车中已下单的商品
			await cartStore.fetchCart()
		}

		uni.redirectTo({
			url: `/pages/order/detail?orderNo=${res.orderNo}`
		})
	} catch (error) {
		console.error('提交订单失败', error)
	} finally {
		hideLoading()
		submitting.value = false
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.address-section {
	display: flex;
	align-items: center;
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.address-info {
	flex: 1;
}

.address-top {
	display: flex;
	margin-bottom: 8rpx;
}

.address-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	margin-right: 24rpx;
}

.address-phone {
	font-size: 28rpx;
	color: #666666;
}

.address-detail {
	font-size: 26rpx;
	color: #999999;
}

.no-address {
	flex: 1;
}

.no-address text {
	font-size: 28rpx;
	color: #999999;
}

.arrow {
	font-size: 28rpx;
	color: #cccccc;
}

.goods-section {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.goods-item {
	display: flex;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.goods-item:last-child {
	border-bottom: none;
}

.goods-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.goods-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.goods-name {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 8rpx;
}

.goods-spec {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 16rpx;
}

.goods-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.goods-price {
	font-size: 30rpx;
	color: #ff6b35;
	font-weight: bold;
}

.goods-quantity {
	font-size: 26rpx;
	color: #999999;
}

.info-section {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.info-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12rpx 0;
}

.info-label {
	font-size: 28rpx;
	color: #666666;
}

.info-value {
	font-size: 28rpx;
	color: #333333;
}

.remark-section {
	display: flex;
	align-items: center;
	background-color: #ffffff;
	padding: 24rpx;
}

.remark-label {
	font-size: 28rpx;
	color: #333333;
	margin-right: 24rpx;
	white-space: nowrap;
}

.remark-input {
	flex: 1;
	font-size: 28rpx;
}

.submit-bar {
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

.total-info {
	flex: 1;
	display: flex;
	align-items: baseline;
}

.total-label {
	font-size: 28rpx;
	color: #333333;
}

.total-price {
	font-size: 36rpx;
	color: #ff6b35;
	font-weight: bold;
}

.btn-submit {
	width: 240rpx;
	height: 72rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 30rpx;
	border-radius: 36rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-submit[disabled] {
	opacity: 0.6;
}
</style>
