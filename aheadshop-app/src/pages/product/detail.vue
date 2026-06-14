<template>
	<view class="container">
		<!-- 顶部返回按钮 -->
		<view class="nav-bar" :style="{ top: statusBarHeight + 'px' }">
			<view class="nav-back" @click="goBack">
				<text class="back-icon">←</text>
			</view>
		</view>

		<!-- 商品图片轮播 -->
		<swiper class="banner" indicator-dots autoplay circular v-if="product.images.length > 0">
			<swiper-item v-for="(img, index) in product.images" :key="index">
				<image :src="img" mode="aspectFill" class="banner-image" @click="previewImage(index)" lazy-load />
			</swiper-item>
		</swiper>

		<!-- 商品信息 -->
		<view class="product-info">
			<view class="price-row">
				<text class="price">¥{{ currentPrice }}</text>
				<text class="original-price" v-if="currentOriginalPrice">¥{{ currentOriginalPrice }}</text>
			</view>
			<text class="name">{{ product.name }}</text>
			<text class="subtitle" v-if="product.subtitle">{{ product.subtitle }}</text>
			<view class="sales-row">
				<text class="sales">已售{{ product.sales }}件</text>
			</view>
		</view>

		<!-- 规格选择 -->
		<view class="section" @click="showSpecPopup = true" v-if="specGroups.length > 0">
			<text class="section-label">规格</text>
			<view class="section-content">
				<text>{{ selectedSpecText || '请选择规格' }}</text>
				<text class="arrow">></text>
			</view>
		</view>

		<!-- 评价区域 -->
		<view class="review-section" v-if="reviewList.length > 0 || reviewAvg > 0">
			<view class="review-header">
				<view class="review-summary">
					<text class="review-avg">{{ reviewAvg.toFixed(1) }}</text>
					<text class="review-label">分</text>
					<text class="review-count">({{ reviewTotal }}条评价)</text>
				</view>
				<view class="review-more" @click="goReviewList">
					<text>查看全部 ></text>
				</view>
			</view>
			<view class="review-list">
				<view class="review-item" v-for="item in reviewList" :key="item.id">
					<view class="review-user">
						<image :src="item.avatar || '/static/avatar-default.png'" class="review-avatar" mode="aspectFill" />
						<view class="review-user-info">
							<text class="review-nickname">{{ item.isAnonymous ? '匿名用户' : (item.nickname || '用户') }}</text>
							<view class="review-stars">
								<text class="star" v-for="i in 5" :key="i" :class="{ active: i <= item.score }">★</text>
							</view>
						</view>
						<text class="review-time">{{ formatTime(item.createTime) }}</text>
					</view>
					<text class="review-content">{{ item.content || '用户未填写评价内容' }}</text>
					<view class="review-images" v-if="item.images">
						<image
							v-for="(img, idx) in parseImages(item.images)"
							:key="idx"
							:src="img"
							class="review-image"
							mode="aspectFill"
							@click="previewReviewImage(item.images, idx)"
						/>
					</view>
					<view class="review-reply" v-if="item.reply">
						<text class="reply-label">商家回复：</text>
						<text class="reply-content">{{ item.reply }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 商品详情 -->
		<view class="detail-section">
			<view class="detail-tabs">
				<view class="tab-item" :class="{ active: activeTab === 'detail' }" @click="activeTab = 'detail'">
					<text>商品详情</text>
				</view>
				<view class="tab-item" :class="{ active: activeTab === 'params' }" @click="activeTab = 'params'">
					<text>规格参数</text>
				</view>
			</view>
			<view class="detail-content" v-if="activeTab === 'detail'">
				<rich-text :nodes="product.detail || '<p>暂无详情</p>'"></rich-text>
			</view>
			<view class="params-content" v-else>
				<view class="param-item" v-for="sku in product.skus" :key="sku.id">
					<text class="param-name">{{ sku.skuName }}</text>
					<text class="param-value">¥{{ sku.price }} / 库存{{ sku.stock }}</text>
				</view>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="action-icons">
				<view class="icon-item" @click="goHome">
					<text class="icon">🏠</text>
					<text class="icon-text">首页</text>
				</view>
				<view class="icon-item" @click="goCart">
					<text class="icon">🛒</text>
					<text class="icon-text">购物车</text>
					<view class="badge" v-if="cartCount > 0">{{ cartCount }}</view>
				</view>
			</view>
			<view class="action-buttons">
				<button class="btn-add-cart" @click="addToCart">加入购物车</button>
				<button class="btn-buy-now" @click="buyNow">立即购买</button>
			</view>
		</view>

		<!-- 规格弹窗 -->
		<view class="spec-popup-mask" v-if="showSpecPopup" @click="showSpecPopup = false">
			<view class="spec-popup" @click.stop>
				<view class="spec-header">
					<image :src="product.images[0]" class="spec-product-image" mode="aspectFill" v-if="product.images.length > 0" />
					<view class="spec-product-info">
						<text class="spec-price">¥{{ currentPrice }}</text>
						<text class="spec-stock">库存：{{ currentStock }}件</text>
						<text class="spec-selected">已选：{{ selectedSpecText || '请选择' }}</text>
					</view>
					<text class="spec-close" @click="showSpecPopup = false">✕</text>
				</view>
				<scroll-view scroll-y class="spec-body">
					<view class="spec-group" v-for="(group, gIndex) in specGroups" :key="gIndex">
						<text class="spec-group-name">{{ group.name }}</text>
						<view class="spec-options">
							<view
								class="spec-option"
								:class="{ active: selectedSpecs[gIndex] === option, disabled: !isOptionAvailable(gIndex, option) }"
								v-for="option in group.options"
								:key="option"
								@click="selectSpec(gIndex, option)"
							>
								<text>{{ option }}</text>
							</view>
						</view>
					</view>
					<view class="quantity-section">
						<text class="quantity-label">购买数量</text>
						<view class="quantity-control">
							<view class="qty-btn" @click="quantity > 1 && quantity--">
								<text>-</text>
							</view>
							<text class="qty-num">{{ quantity }}</text>
							<view class="qty-btn" @click="quantity < currentStock && quantity++">
								<text>+</text>
							</view>
						</view>
					</view>
				</scroll-view>
				<view class="spec-footer">
					<button class="btn-confirm" @click="confirmSpec">确定</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useCartStore } from '@/store/modules/cart'
import { productApi, reviewApi } from '@/api/index'
import { showToast } from '@/utils/util'

const cartStore = useCartStore()

const productId = ref('')
const statusBarHeight = ref(0)

// 获取状态栏高度
const sysInfo = uni.getSystemInfoSync()
statusBarHeight.value = sysInfo.statusBarHeight || 0

onLoad((options) => {
	if (options && options.id) {
		productId.value = options.id
		loadProductDetail()
	}
})

const product = ref({
	id: '',
	name: '',
	subtitle: '',
	sales: 0,
	images: [],
	detail: '',
	skus: []
})

const activeTab = ref('detail')
const showSpecPopup = ref(false)
const selectedSpecs = ref([]) // 每个规格组选中的值
const quantity = ref(1)
const cartCount = computed(() => cartStore.cartCount)

// 评价相关
const reviewAvg = ref(0)
const reviewTotal = ref(0)
const reviewList = ref([])

// 从 SKU 列表解析规格组
const specGroups = computed(() => {
	const groups = {}
	product.value.skus.forEach(sku => {
		if (!sku.specs) return
		// specs 格式可能是 "颜色:黑色,尺码:XL" 或 JSON
		let specMap = {}
		if (typeof sku.specs === 'string') {
			sku.specs.split(',').forEach(pair => {
				const [key, val] = pair.split(':').map(s => s.trim())
				if (key && val) specMap[key] = val
			})
		} else if (typeof sku.specs === 'object') {
			specMap = sku.specs
		}
		Object.entries(specMap).forEach(([name, value]) => {
			if (!groups[name]) groups[name] = new Set()
			groups[name].add(value)
		})
	})
	return Object.entries(groups).map(([name, options]) => ({
		name,
		options: [...options]
	}))
})

// 根据选中规格找到匹配的 SKU
const matchedSku = computed(() => {
	if (specGroups.value.length === 0) return product.value.skus[0]
	if (selectedSpecs.value.length !== specGroups.value.length) return null
	if (selectedSpecs.value.some(s => !s)) return null

	return product.value.skus.find(sku => {
		if (!sku.specs) return false
		let specMap = {}
		if (typeof sku.specs === 'string') {
			sku.specs.split(',').forEach(pair => {
				const [key, val] = pair.split(':').map(s => s.trim())
				if (key && val) specMap[key] = val
			})
		} else if (typeof sku.specs === 'object') {
			specMap = sku.specs
		}
		return specGroups.value.every((group, i) => specMap[group.name] === selectedSpecs.value[i])
	})
})

const currentPrice = computed(() => matchedSku.value?.price || product.value.skus[0]?.price || '0.00')
const currentOriginalPrice = computed(() => matchedSku.value?.originalPrice || product.value.skus[0]?.originalPrice || '')
const currentStock = computed(() => matchedSku.value?.stock ?? product.value.skus[0]?.stock ?? 0)

const selectedSpecText = computed(() => {
	if (selectedSpecs.value.length === 0 || selectedSpecs.value.some(s => !s)) return ''
	return selectedSpecs.value.join('，')
})

onMounted(() => {
	loadProductDetail()
})

const loadProductDetail = async () => {
	try {
		const res = await productApi.getDetail(productId.value)
		product.value = {
			id: res.id,
			name: res.name,
			subtitle: res.subtitle || '',
			sales: res.sales || 0,
			images: parseImages(res.images),
			detail: res.detail || '',
			skus: res.skus || []
		}
		// 初始化规格选择数组
		selectedSpecs.value = new Array(specGroups.value.length).fill('')
		// 加载评价
		loadReviews()
	} catch (error) {
		console.error('加载商品详情失败', error)
	}
}

const loadReviews = async () => {
	try {
		const [avgRes, listRes] = await Promise.all([
			reviewApi.getAvgScore(productId.value).catch(() => 0),
			reviewApi.getList(productId.value, { pageNum: 1, pageSize: 3 }).catch(() => null)
		])
		reviewAvg.value = avgRes || 0
		if (listRes) {
			reviewList.value = listRes.records || []
			reviewTotal.value = listRes.total || 0
		}
	} catch (error) {
		console.error('加载评价失败', error)
	}
}

const formatTime = (time) => {
	if (!time) return ''
	const date = new Date(time)
	return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const previewReviewImage = (images, index) => {
	const arr = parseImages(images)
	uni.previewImage({ current: index, urls: arr })
}

const goReviewList = () => {
	uni.navigateTo({
		url: `/pages/product/review?spuId=${productId.value}`
	})
}

// 解析图片列表（可能是逗号分隔字符串或数组）
const parseImages = (images) => {
	if (!images) return []
	if (Array.isArray(images)) return images
	if (typeof images === 'string') return images.split(',').map(s => s.trim()).filter(Boolean)
	return []
}

const isOptionAvailable = (groupIndex, option) => {
	// 简单实现：所有选项都可用
	return true
}

const selectSpec = (groupIndex, option) => {
	selectedSpecs.value[groupIndex] = selectedSpecs.value[groupIndex] === option ? '' : option
}

const previewImage = (index) => {
	uni.previewImage({
		current: index,
		urls: product.value.images
	})
}

const confirmSpec = () => {
	if (!selectedSpecText.value) {
		showToast('请选择规格')
		return
	}
	showSpecPopup.value = false
}

const addToCart = () => {
	if (specGroups.value.length > 0 && !selectedSpecText.value) {
		showSpecPopup.value = true
		return
	}
	const sku = matchedSku.value || product.value.skus[0]
	if (!sku) {
		showToast('商品信息异常')
		return
	}
	cartStore.addToCart({ skuId: sku.id, quantity: quantity.value })
}

const buyNow = () => {
	if (specGroups.value.length > 0 && !selectedSpecText.value) {
		showSpecPopup.value = true
		return
	}
	const sku = matchedSku.value || product.value.skus[0]
	if (!sku) {
		showToast('商品信息异常')
		return
	}
	uni.navigateTo({
		url: `/pages/order/confirm?spuId=${productId.value}&skuId=${sku.id}&quantity=${quantity.value}`
	})
}

const goBack = () => {
	const pages = getCurrentPages()
	if (pages.length > 1) {
		uni.navigateBack()
	} else {
		uni.switchTab({ url: '/pages/index/index' })
	}
}

const goHome = () => {
	uni.switchTab({ url: '/pages/index/index' })
}

const goCart = () => {
	uni.switchTab({ url: '/pages/cart/cart' })
}
</script>

<style scoped>
.container {
	padding-bottom: 120rpx;
	background-color: #f5f5f5;
}

.nav-bar {
	position: fixed;
	left: 0;
	right: 0;
	z-index: 100;
	padding: 20rpx 24rpx;
	display: flex;
	align-items: center;
}

.nav-back {
	width: 64rpx;
	height: 64rpx;
	background-color: rgba(0, 0, 0, 0.4);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.back-icon {
	font-size: 36rpx;
	color: #ffffff;
	font-weight: bold;
}

.banner {
	width: 100%;
	height: 750rpx;
}

.banner-image {
	width: 100%;
	height: 100%;
}

.product-info {
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.price-row {
	display: flex;
	align-items: baseline;
	margin-bottom: 16rpx;
}

.price {
	font-size: 44rpx;
	color: #ff6b35;
	font-weight: bold;
}

.original-price {
	font-size: 26rpx;
	color: #999999;
	text-decoration: line-through;
	margin-left: 16rpx;
}

.name {
	font-size: 30rpx;
	color: #333333;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	margin-bottom: 8rpx;
}

.subtitle {
	font-size: 26rpx;
	color: #999999;
	margin-bottom: 16rpx;
}

.sales-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.sales {
	font-size: 24rpx;
	color: #999999;
}

.section {
	display: flex;
	align-items: center;
	background-color: #ffffff;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.section-label {
	font-size: 26rpx;
	color: #999999;
	margin-right: 24rpx;
}

.section-content {
	flex: 1;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.section-content text {
	font-size: 28rpx;
	color: #333333;
}

.arrow {
	color: #cccccc;
}

.detail-section {
	background-color: #ffffff;
}

.detail-tabs {
	display: flex;
	border-bottom: 1rpx solid #eeeeee;
}

.tab-item {
	flex: 1;
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 24rpx 0;
}

.tab-item text {
	font-size: 28rpx;
	color: #666666;
}

.tab-item.active text {
	color: #ff6b35;
	font-weight: bold;
}

.detail-content {
	padding: 24rpx;
}

.params-content {
	padding: 24rpx;
}

.param-item {
	display: flex;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.param-name {
	width: 200rpx;
	font-size: 26rpx;
	color: #999999;
}

.param-value {
	flex: 1;
	font-size: 26rpx;
	color: #333333;
}

.bottom-bar {
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
	z-index: 100;
}

.action-icons {
	display: flex;
	margin-right: 24rpx;
}

.icon-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin: 0 16rpx;
	position: relative;
}

.icon {
	font-size: 40rpx;
}

.icon-text {
	font-size: 20rpx;
	color: #666666;
}

.badge {
	position: absolute;
	top: -8rpx;
	right: -8rpx;
	min-width: 32rpx;
	height: 32rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 20rpx;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 8rpx;
}

.action-buttons {
	flex: 1;
	display: flex;
}

.btn-add-cart {
	flex: 1;
	height: 72rpx;
	background-color: #ff9500;
	color: #ffffff;
	font-size: 28rpx;
	border-radius: 36rpx 0 0 36rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-buy-now {
	flex: 1;
	height: 72rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 28rpx;
	border-radius: 0 36rpx 36rpx 0;
	display: flex;
	align-items: center;
	justify-content: center;
}

.spec-popup-mask {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 200;
	display: flex;
	align-items: flex-end;
}

.spec-popup {
	width: 100%;
	max-height: 80vh;
	background-color: #ffffff;
	border-radius: 24rpx 24rpx 0 0;
}

.spec-header {
	display: flex;
	padding: 24rpx;
	border-bottom: 1rpx solid #eeeeee;
	position: relative;
}

.spec-product-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 12rpx;
	margin-right: 24rpx;
}

.spec-product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.spec-price {
	font-size: 40rpx;
	color: #ff6b35;
	font-weight: bold;
	margin-bottom: 8rpx;
}

.spec-stock {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 8rpx;
}

.spec-selected {
	font-size: 26rpx;
	color: #333333;
}

.spec-close {
	position: absolute;
	top: 24rpx;
	right: 24rpx;
	font-size: 36rpx;
	color: #999999;
}

.spec-body {
	max-height: 60vh;
	padding: 24rpx;
}

.spec-group {
	margin-bottom: 32rpx;
}

.spec-group-name {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 16rpx;
	display: block;
}

.spec-options {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}

.spec-option {
	padding: 12rpx 32rpx;
	background-color: #f5f5f5;
	border-radius: 8rpx;
	border: 2rpx solid transparent;
}

.spec-option.active {
	background-color: #fff5f0;
	border-color: #ff6b35;
	color: #ff6b35;
}

.spec-option.disabled {
	opacity: 0.4;
}

.spec-option text {
	font-size: 26rpx;
	color: #333333;
}

.spec-option.active text {
	color: #ff6b35;
}

.quantity-section {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 24rpx;
	border-top: 1rpx solid #eeeeee;
}

.quantity-label {
	font-size: 28rpx;
	color: #333333;
}

.quantity-control {
	display: flex;
	align-items: center;
}

.qty-btn {
	width: 56rpx;
	height: 56rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #f5f5f5;
	border-radius: 8rpx;
}

.qty-btn text {
	font-size: 32rpx;
	color: #333333;
}

.qty-num {
	width: 80rpx;
	text-align: center;
	font-size: 28rpx;
	color: #333333;
}

.spec-footer {
	padding: 24rpx;
	border-top: 1rpx solid #eeeeee;
}

.btn-confirm {
	width: 100%;
	height: 80rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 30rpx;
	border-radius: 40rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 评价区域 */
.review-section {
	background-color: #ffffff;
	margin-bottom: 16rpx;
	padding: 24rpx;
}

.review-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.review-summary {
	display: flex;
	align-items: baseline;
}

.review-avg {
	font-size: 40rpx;
	color: #ff6b35;
	font-weight: bold;
	margin-right: 8rpx;
}

.review-label {
	font-size: 26rpx;
	color: #999999;
	margin-right: 12rpx;
}

.review-count {
	font-size: 24rpx;
	color: #999999;
}

.review-more text {
	font-size: 26rpx;
	color: #999999;
}

.review-item {
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.review-item:last-child {
	border-bottom: none;
}

.review-user {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.review-avatar {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	margin-right: 16rpx;
}

.review-user-info {
	flex: 1;
}

.review-nickname {
	font-size: 26rpx;
	color: #333333;
	margin-bottom: 4rpx;
}

.review-stars {
	display: flex;
}

.star {
	font-size: 24rpx;
	color: #cccccc;
}

.star.active {
	color: #ff9500;
}

.review-time {
	font-size: 22rpx;
	color: #cccccc;
}

.review-content {
	font-size: 26rpx;
	color: #333333;
	line-height: 1.6;
	margin-bottom: 12rpx;
}

.review-images {
	display: flex;
	flex-wrap: wrap;
	gap: 12rpx;
	margin-bottom: 12rpx;
}

.review-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 8rpx;
}

.review-reply {
	background-color: #f8f8f8;
	padding: 16rpx;
	border-radius: 8rpx;
}

.reply-label {
	font-size: 24rpx;
	color: #ff6b35;
	margin-right: 8rpx;
}

.reply-content {
	font-size: 24rpx;
	color: #666666;
}
</style>
