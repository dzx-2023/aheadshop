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
					:key="item.id"
					@click="selectCategory(index)"
				>
					<text>{{ item.name }}</text>
				</view>
			</scroll-view>

			<!-- 右侧分类内容 -->
			<scroll-view scroll-y class="category-detail">
				<view class="detail-section" v-for="sub in currentSubCategories" :key="sub.id">
					<view class="detail-title">
						<text>{{ sub.name }}</text>
					</view>
					<view class="detail-grid">
						<view class="detail-item" v-for="child in (sub.children || [])" :key="child.id" @click="goProductList(child)">
							<image :src="child.icon" class="detail-icon" mode="aspectFill" v-if="child.icon" />
							<view class="detail-icon-placeholder" v-else>
								<text>{{ child.name.charAt(0) }}</text>
							</view>
							<text class="detail-name">{{ child.name }}</text>
						</view>
					</view>
				</view>

				<!-- 空状态 -->
				<view class="empty" v-if="currentSubCategories.length === 0">
					<text class="empty-text">暂无子分类</text>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { productApi } from '@/api/index'

const currentIndex = ref(0)
const categories = ref([])

const currentSubCategories = computed(() => {
	return categories.value[currentIndex.value]?.children || []
})

onMounted(() => {
	loadCategories()
})

const loadCategories = async () => {
	try {
		const res = await productApi.getCategoryTree()
		categories.value = res || []
	} catch (error) {
		console.error('加载分类失败', error)
	}
}

const selectCategory = (index) => {
	currentIndex.value = index
}

const goSearch = () => {
	uni.navigateTo({ url: '/pages/search/index' })
}

const goProductList = (item) => {
	uni.navigateTo({
		url: `/pages/product/list?categoryId=${item.id}`
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
	border-radius: 16rpx;
}

.detail-icon-placeholder {
	width: 100rpx;
	height: 100rpx;
	margin-bottom: 12rpx;
	border-radius: 16rpx;
	background-color: #f0f0f0;
	display: flex;
	align-items: center;
	justify-content: center;
}

.detail-icon-placeholder text {
	font-size: 36rpx;
	color: #999999;
}

.detail-name {
	font-size: 24rpx;
	color: #666666;
}

.empty {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 100rpx 0;
}

.empty-text {
	font-size: 28rpx;
	color: #999999;
}
</style>
