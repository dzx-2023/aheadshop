<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input">
				<text class="icon-search">🔍</text>
				<input type="text" placeholder="搜索商品" />
			</view>
		</view>

		<!-- 轮播图 -->
		<swiper class="banner" indicator-dots autoplay circular>
			<swiper-item v-for="(item, index) in banners" :key="index">
				<image :src="item.image" mode="aspectFill" class="banner-image" />
			</swiper-item>
		</swiper>

		<!-- 分类入口 -->
		<view class="category-grid">
			<view class="category-item" v-for="(item, index) in categoryIcons" :key="index">
				<image :src="item.icon" class="category-icon" />
				<text class="category-name">{{ item.name }}</text>
			</view>
		</view>

		<!-- 热门推荐 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">热门推荐</text>
				<text class="section-more">查看更多 ></text>
			</view>
			<scroll-view scroll-x class="hot-scroll">
				<view class="hot-item" v-for="(item, index) in hotProducts" :key="index">
					<image :src="item.image" class="hot-image" mode="aspectFill" />
					<text class="hot-name">{{ item.name }}</text>
					<text class="hot-price">¥{{ item.price }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 新品上市 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">新品上市</text>
				<text class="section-more">查看更多 ></text>
			</view>
			<view class="product-grid">
				<view class="product-item" v-for="(item, index) in newProducts" :key="index">
					<image :src="item.image" class="product-image" mode="aspectFill" />
					<view class="product-info">
						<text class="product-name">{{ item.name }}</text>
						<view class="product-bottom">
							<text class="product-price">¥{{ item.price }}</text>
							<text class="product-sales">已售{{ item.sales }}件</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

const banners = ref([
	{ image: '/static/banner/1.jpg' },
	{ image: '/static/banner/2.jpg' },
	{ image: '/static/banner/3.jpg' }
])

const categoryIcons = ref([
	{ name: '数码', icon: '/static/category/1.png' },
	{ name: '服装', icon: '/static/category/2.png' },
	{ name: '美妆', icon: '/static/category/3.png' },
	{ name: '食品', icon: '/static/category/4.png' },
	{ name: '家居', icon: '/static/category/5.png' },
	{ name: '运动', icon: '/static/category/6.png' },
	{ name: '图书', icon: '/static/category/7.png' },
	{ name: '更多', icon: '/static/category/8.png' }
])

const hotProducts = ref([
	{ name: '商品名称1', price: '99.00', image: '/static/product/1.jpg' },
	{ name: '商品名称2', price: '199.00', image: '/static/product/2.jpg' },
	{ name: '商品名称3', price: '299.00', image: '/static/product/3.jpg' },
	{ name: '商品名称4', price: '399.00', image: '/static/product/4.jpg' }
])

const newProducts = ref([
	{ name: '新品商品1', price: '128.00', sales: 100, image: '/static/product/5.jpg' },
	{ name: '新品商品2', price: '258.00', sales: 200, image: '/static/product/6.jpg' },
	{ name: '新品商品3', price: '388.00', sales: 150, image: '/static/product/7.jpg' },
	{ name: '新品商品4', price: '499.00', sales: 80, image: '/static/product/8.jpg' }
])
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

.search-input input {
	flex: 1;
	font-size: 28rpx;
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
