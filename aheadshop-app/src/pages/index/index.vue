<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input" @click="goSearch">
				<text class="icon-search">🔍</text>
				<text class="search-placeholder">搜索商品</text>
			</view>
		</view>

		<!-- 轮播图 -->
		<swiper class="banner" indicator-dots autoplay circular v-if="banners.length > 0">
			<swiper-item v-for="(item, index) in banners" :key="index">
				<image :src="item.imageUrl" mode="aspectFill" class="banner-image" />
			</swiper-item>
		</swiper>

		<!-- 分类入口 -->
		<view class="category-grid">
			<view class="category-item" v-for="(item, index) in categoryIcons" :key="item.id" @click="goCategory(item)">
				<image :src="item.icon" class="category-icon" v-if="item.icon" />
				<view class="category-icon-placeholder" v-else>
					<text>{{ item.name.charAt(0) }}</text>
				</view>
				<text class="category-name">{{ item.name }}</text>
			</view>
		</view>

		<!-- 热门推荐 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">热门推荐</text>
				<text class="section-more" @click="goProductList()">查看更多 ></text>
			</view>
			<scroll-view scroll-x class="hot-scroll">
				<view class="hot-item" v-for="item in hotProducts" :key="item.id" @click="goDetail(item)">
					<image :src="item.mainImage" class="hot-image" mode="aspectFill" />
					<text class="hot-name">{{ item.name }}</text>
					<text class="hot-price">¥{{ item.minPrice }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 新品上市 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">新品上市</text>
				<text class="section-more" @click="goProductList()">查看更多 ></text>
			</view>
			<view class="product-grid">
				<view class="product-item" v-for="item in newProducts" :key="item.id" @click="goDetail(item)">
					<image :src="item.mainImage" class="product-image" mode="aspectFill" />
					<view class="product-info">
						<text class="product-name">{{ item.name }}</text>
						<view class="product-bottom">
							<text class="product-price">¥{{ item.minPrice }}</text>
							<text class="product-sales">已售{{ item.sales }}件</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { productApi, backgroundApi } from '@/api/index'

const banners = ref([])
const categoryIcons = ref([])
const hotProducts = ref([])
const newProducts = ref([])

onMounted(() => {
	loadBanners()
	loadCategories()
	loadHotProducts()
	loadNewProducts()
})

// 加载轮播图
const loadBanners = async () => {
	try {
		const res = await backgroundApi.getActive()
		banners.value = res || []
	} catch (error) {
		console.error('加载轮播图失败', error)
	}
}

// 加载分类（取前 8 个一级分类）
const loadCategories = async () => {
	try {
		const res = await productApi.getCategoryTree()
		const list = (res || []).slice(0, 8)
		categoryIcons.value = list.map(item => ({
			id: item.id,
			name: item.name,
			icon: item.icon
		}))
	} catch (error) {
		console.error('加载分类失败', error)
	}
}

// 加载热门推荐（按销量排序）
const loadHotProducts = async () => {
	try {
		const res = await productApi.getList({ pageSize: 10, sort: 'sales', order: 'desc' })
		hotProducts.value = res?.records || []
	} catch (error) {
		console.error('加载热门商品失败', error)
	}
}

// 加载新品上市
const loadNewProducts = async () => {
	try {
		const res = await productApi.getList({ pageSize: 10 })
		newProducts.value = res?.records || []
	} catch (error) {
		console.error('加载新品失败', error)
	}
}

const goSearch = () => {
	uni.navigateTo({ url: '/pages/search/index' })
}

const goCategory = (item) => {
	uni.switchTab({ url: '/pages/category/category' })
}

const goProductList = () => {
	uni.navigateTo({ url: '/pages/product/list' })
}

const goDetail = (item) => {
	uni.navigateTo({ url: `/pages/product/detail?id=${item.id}` })
}
</script>

<style scoped>
.container {
	padding-bottom: 20rpx;
	background-color: #f5f5f5;
}

.search-bar {
	padding: 20rpx;
	background-color: #ff6b35;
}

.search-input {
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #ffffff;
	border-radius: 40rpx;
}

.icon-search {
	margin-right: 16rpx;
}

.search-placeholder {
	flex: 1;
	font-size: 28rpx;
	color: #999999;
}

.banner {
	width: 100%;
	height: 360rpx;
}

.banner-image {
	width: 100%;
	height: 100%;
}

.category-grid {
	display: flex;
	flex-wrap: wrap;
	padding: 30rpx 20rpx;
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.category-item {
	width: 25%;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 20rpx;
}

.category-icon {
	width: 80rpx;
	height: 80rpx;
	margin-bottom: 10rpx;
	border-radius: 16rpx;
}

.category-icon-placeholder {
	width: 80rpx;
	height: 80rpx;
	margin-bottom: 10rpx;
	border-radius: 16rpx;
	background-color: #ff6b35;
	display: flex;
	align-items: center;
	justify-content: center;
}

.category-icon-placeholder text {
	font-size: 32rpx;
	color: #ffffff;
}

.category-name {
	font-size: 24rpx;
	color: #333333;
}

.section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
	padding: 30rpx;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
}

.section-more {
	font-size: 24rpx;
	color: #999999;
}

.hot-scroll {
	white-space: nowrap;
}

.hot-item {
	display: inline-block;
	width: 200rpx;
	margin-right: 20rpx;
}

.hot-image {
	width: 200rpx;
	height: 200rpx;
	border-radius: 16rpx;
}

.hot-name {
	display: block;
	font-size: 24rpx;
	color: #333333;
	margin-top: 10rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.hot-price {
	display: block;
	font-size: 28rpx;
	color: #ff6b35;
	font-weight: bold;
	margin-top: 8rpx;
}

.product-grid {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}

.product-item {
	width: 48%;
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
	margin-bottom: 20rpx;
}

.product-image {
	width: 100%;
	height: 320rpx;
}

.product-info {
	padding: 16rpx;
}

.product-name {
	font-size: 26rpx;
	color: #333333;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	height: 72rpx;
}

.product-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 12rpx;
}

.product-price {
	font-size: 30rpx;
	color: #ff6b35;
	font-weight: bold;
}

.product-sales {
	font-size: 22rpx;
	color: #999999;
}
</style>
