<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input" @click="goSearch">
				<text class="icon-search">🔍</text>
				<text class="search-placeholder">搜索商品</text>
			</view>
		</view>

		<view class="category-content">
			<!-- 左侧分类导航 -->
			<scroll-view scroll-y class="category-nav">
				<view
					class="nav-item"
					:class="{ active: currentIndex === index }"
					v-for="(item, index) in categories"
					:key="index"
					@click="selectCategory(index)"
				>
					<text>{{ item.name }}</text>
				</view>
			</scroll-view>

			<!-- 右侧分类内容 -->
			<scroll-view scroll-y class="category-detail">
				<view class="detail-section" v-for="(item, index) in currentSubCategories" :key="index">
					<view class="detail-title">
						<text>{{ item.name }}</text>
					</view>
					<view class="detail-grid">
						<view class="detail-item" v-for="(sub, idx) in item.children" :key="idx" @click="goCategory(sub)">
							<image :src="sub.icon" class="detail-icon" mode="aspectFill" />
							<text class="detail-name">{{ sub.name }}</text>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue'

const currentIndex = ref(0)

const categories = ref([
	{
		name: '数码家电',
		children: [
			{
				name: '手机通讯',
				children: [
					{ name: '手机', icon: '/static/category/phone.png' },
					{ name: '游戏手机', icon: '/static/category/game-phone.png' },
					{ name: '老人机', icon: '/static/category/old-phone.png' }
				]
			},
			{
				name: '手机配件',
				children: [
					{ name: '手机壳', icon: '/static/category/case.png' },
					{ name: '充电器', icon: '/static/category/charger.png' },
					{ name: '耳机', icon: '/static/category/earphone.png' }
				]
			}
		]
	},
	{
		name: '服饰鞋包',
		children: [
			{
				name: '女装',
				children: [
					{ name: '连衣裙', icon: '/static/category/dress.png' },
					{ name: 'T恤', icon: '/static/category/tshirt.png' },
					{ name: '半身裙', icon: '/static/category/skirt.png' }
				]
			},
			{
				name: '男装',
				children: [
					{ name: '衬衫', icon: '/static/category/shirt.png' },
					{ name: '牛仔裤', icon: '/static/category/jeans.png' },
					{ name: '夹克', icon: '/static/category/jacket.png' }
				]
			}
		]
	},
	{
		name: '美妆护肤',
		children: [
			{
				name: '面部护肤',
				children: [
					{ name: '面膜', icon: '/static/category/mask.png' },
					{ name: '精华', icon: '/static/category/essence.png' },
					{ name: '面霜', icon: '/static/category/cream.png' }
				]
			}
		]
	},
	{
		name: '食品饮料',
		children: [
			{
				name: '休闲零食',
				children: [
					{ name: '坚果', icon: '/static/category/nut.png' },
					{ name: '饼干', icon: '/static/category/cookie.png' },
					{ name: '糖果', icon: '/static/category/candy.png' }
				]
			}
		]
	},
	{
		name: '家居日用',
		children: []
	},
	{
		name: '运动户外',
		children: []
	},
	{
		name: '图书音像',
		children: []
	},
	{
		name: '母婴玩具',
		children: []
	},
	{
		name: '家用电器',
		children: []
	}
])

const currentSubCategories = computed(() => {
	return categories.value[currentIndex.value]?.children || []
})

const selectCategory = (index) => {
	currentIndex.value = index
}

const goSearch = () => {
	uni.navigateTo({
		url: '/pages/search/search'
	})
}

const goCategory = (item) => {
	uni.navigateTo({
		url: `/pages/product/list?category=${item.name}`
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
	font-size: 28rpx;
	color: #999999;
}

.category-content {
	flex: 1;
	display: flex;
	overflow: hidden;
}

.category-nav {
	width: 200rpx;
	height: 100%;
	background-color: #ffffff;
}

.nav-item {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100rpx;
	font-size: 26rpx;
	color: #333333;
	border-left: 6rpx solid transparent;
}

.nav-item.active {
	background-color: #f5f5f5;
	color: #ff6b35;
	border-left-color: #ff6b35;
	font-weight: bold;
}

.category-detail {
	flex: 1;
	height: 100%;
	padding: 20rpx;
}

.detail-section {
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	padding: 24rpx;
}

.detail-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 20rpx;
	padding-bottom: 16rpx;
	border-bottom: 1rpx solid #eeeeee;
}

.detail-grid {
	display: flex;
	flex-wrap: wrap;
}

.detail-item {
	width: 33.33%;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 24rpx;
}

.detail-icon {
	width: 100rpx;
	height: 100rpx;
	margin-bottom: 12rpx;
}

.detail-name {
	font-size: 24rpx;
	color: #666666;
}
</style>
