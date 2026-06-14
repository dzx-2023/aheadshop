<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input" @click="goSearch">
				<text class="icon">🔍</text>
				<input
					v-if="isSearch"
					type="text"
					v-model="keyword"
					placeholder="搜索商品"
					focus
					@confirm="doSearch"
					class="search-field"
				/>
				<text v-else class="placeholder">{{ keyword || '搜索商品' }}</text>
			</view>
		</view>

		<!-- 排序栏 -->
		<view class="sort-bar">
			<view class="sort-item" :class="{ active: sortType === 'default' }" @click="changeSort('default')">
				<text>综合</text>
			</view>
			<view class="sort-item" :class="{ active: sortType === 'sales' }" @click="changeSort('sales')">
				<text>销量</text>
			</view>
			<view class="sort-item" :class="{ active: sortType === 'price' }" @click="changeSort('price')">
				<text>价格</text>
				<view class="sort-arrows">
					<text :class="{ active: sortType === 'price' && sortOrder === 'asc' }">↑</text>
					<text :class="{ active: sortType === 'price' && sortOrder === 'desc' }">↓</text>
				</view>
			</view>
			<view class="sort-item" @click="toggleViewMode">
				<text>{{ isGrid ? '☷' : '☰' }}</text>
			</view>
		</view>

		<!-- 骨架屏 -->
		<product-skeleton v-if="isLoading && productList.length === 0" :count="6" />

		<!-- 商品列表 -->
		<scroll-view
			v-else
			scroll-y
			class="product-list"
			@scrolltolower="loadMore"
			refresher-enabled
			@refresherrefresh="onRefresh"
			:refresher-triggered="isRefreshing"
		>
			<!-- 网格视图 -->
			<view class="grid-view" v-if="isGrid">
				<view class="grid-item" v-for="item in productList" :key="item.id" @click="goDetail(item)">
					<image :src="item.mainImage" class="grid-image" mode="aspectFill" lazy-load />
					<view class="grid-info">
						<text class="grid-name">{{ item.name }}</text>
						<view class="grid-bottom">
							<text class="grid-price">¥{{ item.minPrice }}</text>
							<text class="grid-sales">已售{{ item.sales }}件</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 列表视图 -->
			<view class="list-view" v-else>
				<view class="list-item" v-for="item in productList" :key="item.id" @click="goDetail(item)">
					<image :src="item.mainImage" class="list-image" mode="aspectFill" lazy-load />
					<view class="list-info">
						<text class="list-name">{{ item.name }}</text>
						<text class="list-desc">{{ item.subtitle }}</text>
						<view class="list-bottom">
							<text class="list-price">¥{{ item.minPrice }}</text>
							<text class="list-sales">已售{{ item.sales }}件</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载状态 -->
			<view class="loading-status">
				<text v-if="isLoading">加载中...</text>
				<text v-else-if="noMore">没有更多了</text>
			</view>

			<!-- 空状态 -->
			<view class="empty" v-if="!isLoading && productList.length === 0">
				<text class="empty-text">暂无商品</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { productApi } from '@/api/index'
import ProductSkeleton from '@/components/product-skeleton.vue'

const productList = ref([])
const sortType = ref('default')
const sortOrder = ref('desc')
const isGrid = ref(true)
const isSearch = ref(false)
const isLoading = ref(false)
const isRefreshing = ref(false)
const noMore = ref(false)
const keyword = ref('')
const categoryId = ref('')
const page = ref(1)
const pageSize = 20

onLoad((options) => {
	if (options && options.categoryId) categoryId.value = options.categoryId
	if (options && options.keyword) keyword.value = options.keyword
	loadProducts()
})

const loadProducts = async (isRefresh = false) => {
	if (isLoading.value) return

	isLoading.value = true
	try {
		if (isRefresh) {
			page.value = 1
			noMore.value = false
		}

		const params = {
			pageNum: page.value,
			pageSize: pageSize
		}
		if (categoryId.value) params.categoryId = categoryId.value
		if (keyword.value) params.keyword = keyword.value

		const res = await productApi.getList(params)
		const list = res?.records || []

		if (isRefresh) {
			productList.value = list
		} else {
			productList.value = [...productList.value, ...list]
		}

		if (list.length < pageSize) {
			noMore.value = true
		}

		page.value++
	} catch (error) {
		console.error('加载商品失败', error)
	} finally {
		isLoading.value = false
		isRefreshing.value = false
	}
}

const changeSort = (type) => {
	if (type === 'price' && sortType.value === 'price') {
		sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
	} else {
		sortType.value = type
		sortOrder.value = 'desc'
	}
	// 后端暂不支持排序参数，刷新即可
	loadProducts(true)
}

const toggleViewMode = () => {
	isGrid.value = !isGrid.value
}

const goSearch = () => {
	isSearch.value = true
}

const doSearch = () => {
	loadProducts(true)
	isSearch.value = false
}

const loadMore = () => {
	if (!noMore.value) {
		loadProducts()
	}
}

const onRefresh = () => {
	isRefreshing.value = true
	loadProducts(true)
}

const goDetail = (item) => {
	uni.navigateTo({
		url: `/pages/product/detail?id=${item.id}`
	})
}
</script>

<style scoped>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.search-bar {
	padding: 16rpx 24rpx;
	background-color: #ffffff;
}

.search-input {
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #f5f5f5;
	border-radius: 40rpx;
}

.icon {
	margin-right: 12rpx;
}

.placeholder {
	font-size: 28rpx;
	color: #999999;
}

.search-field {
	flex: 1;
	font-size: 28rpx;
}

.sort-bar {
	display: flex;
	background-color: #ffffff;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #eeeeee;
}

.sort-item {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 26rpx;
	color: #666666;
}

.sort-item.active {
	color: #ff6b35;
}

.sort-arrows {
	display: flex;
	flex-direction: column;
	margin-left: 8rpx;
	font-size: 20rpx;
	line-height: 1;
}

.sort-arrows text {
	color: #cccccc;
}

.sort-arrows text.active {
	color: #ff6b35;
}

.product-list {
	flex: 1;
}

.grid-view {
	display: flex;
	flex-wrap: wrap;
	padding: 16rpx;
	gap: 16rpx;
}

.grid-item {
	width: calc(50% - 8rpx);
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
}

.grid-image {
	width: 100%;
	height: 340rpx;
}

.grid-info {
	padding: 16rpx;
}

.grid-name {
	font-size: 26rpx;
	color: #333333;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	height: 72rpx;
}

.grid-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 12rpx;
}

.grid-price {
	font-size: 30rpx;
	color: #ff6b35;
	font-weight: bold;
}

.grid-sales {
	font-size: 22rpx;
	color: #999999;
}

.list-view {
	padding: 16rpx;
}

.list-item {
	display: flex;
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
	margin-bottom: 16rpx;
}

.list-image {
	width: 240rpx;
	height: 240rpx;
	flex-shrink: 0;
}

.list-info {
	flex: 1;
	padding: 20rpx;
	display: flex;
	flex-direction: column;
}

.list-name {
	font-size: 28rpx;
	color: #333333;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	margin-bottom: 12rpx;
}

.list-desc {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 12rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.list-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: auto;
}

.list-price {
	font-size: 32rpx;
	color: #ff6b35;
	font-weight: bold;
}

.list-sales {
	font-size: 22rpx;
	color: #999999;
}

.loading-status {
	text-align: center;
	padding: 24rpx;
	font-size: 24rpx;
	color: #999999;
}

.empty {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 200rpx 0;
}

.empty-text {
	font-size: 28rpx;
	color: #999999;
}
</style>
