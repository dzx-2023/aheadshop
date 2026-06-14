<template>
	<view class="container">
		<!-- 评分概览 -->
		<view class="score-overview">
			<view class="score-main">
				<text class="score-num">{{ avgScore.toFixed(1) }}</text>
				<text class="score-label">综合评分</text>
			</view>
			<view class="score-stars">
				<text class="star" v-for="i in 5" :key="i" :class="{ active: i <= Math.round(avgScore) }">★</text>
			</view>
			<text class="score-count">共 {{ total }} 条评价</text>
		</view>

		<!-- 评价列表 -->
		<scroll-view
			scroll-y
			class="review-list"
			@scrolltolower="loadMore"
			refresher-enabled
			@refresherrefresh="onRefresh"
			:refresher-triggered="isRefreshing"
		>
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
						@click="previewImage(item.images, idx)"
					/>
				</view>
				<view class="review-reply" v-if="item.reply">
					<text class="reply-label">商家回复：</text>
					<text class="reply-content">{{ item.reply }}</text>
				</view>
			</view>

			<!-- 加载状态 -->
			<view class="loading-status">
				<text v-if="isLoading">加载中...</text>
				<text v-else-if="noMore">没有更多了</text>
			</view>

			<!-- 空状态 -->
			<view class="empty" v-if="!isLoading && reviewList.length === 0">
				<text class="empty-text">暂无评价</text>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { reviewApi } from '@/api/index'

const spuId = ref('')
const avgScore = ref(0)
const total = ref(0)
const reviewList = ref([])
const page = ref(1)
const pageSize = 20
const isLoading = ref(false)
const isRefreshing = ref(false)
const noMore = ref(false)

onLoad((options) => {
	if (options && options.spuId) {
		spuId.value = options.spuId
		loadAvgScore()
		loadReviews(true)
	}
})

const loadAvgScore = async () => {
	try {
		const res = await reviewApi.getAvgScore(spuId.value)
		avgScore.value = res || 0
	} catch (error) {
		console.error('加载评分失败', error)
	}
}

const loadReviews = async (isRefresh = false) => {
	if (isLoading.value) return
	isLoading.value = true

	try {
		if (isRefresh) {
			page.value = 1
			noMore.value = false
		}

		const res = await reviewApi.getList(spuId.value, {
			pageNum: page.value,
			pageSize: pageSize
		})

		const list = res?.records || []
		total.value = res?.total || 0

		if (isRefresh) {
			reviewList.value = list
		} else {
			reviewList.value = [...reviewList.value, ...list]
		}

		if (list.length < pageSize) {
			noMore.value = true
		}
		page.value++
	} catch (error) {
		console.error('加载评价失败', error)
	} finally {
		isLoading.value = false
		isRefreshing.value = false
	}
}

const loadMore = () => {
	if (!noMore.value) {
		loadReviews()
	}
}

const onRefresh = () => {
	isRefreshing.value = true
	loadReviews(true)
}

const formatTime = (time) => {
	if (!time) return ''
	const date = new Date(time)
	return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const parseImages = (images) => {
	if (!images) return []
	if (Array.isArray(images)) return images
	if (typeof images === 'string') return images.split(',').map(s => s.trim()).filter(Boolean)
	return []
}

const previewImage = (images, index) => {
	const arr = parseImages(images)
	uni.previewImage({ current: index, urls: arr })
}
</script>

<style scoped>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.score-overview {
	background-color: #ffffff;
	padding: 40rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 16rpx;
}

.score-main {
	display: flex;
	align-items: baseline;
	margin-bottom: 12rpx;
}

.score-num {
	font-size: 60rpx;
	color: #ff6b35;
	font-weight: bold;
	margin-right: 8rpx;
}

.score-label {
	font-size: 28rpx;
	color: #999999;
}

.score-stars {
	margin-bottom: 12rpx;
}

.star {
	font-size: 32rpx;
	color: #cccccc;
}

.star.active {
	color: #ff9500;
}

.score-count {
	font-size: 24rpx;
	color: #999999;
}

.review-list {
	flex: 1;
	padding: 0 16rpx;
}

.review-item {
	background-color: #ffffff;
	border-radius: 12rpx;
	padding: 24rpx;
	margin-bottom: 16rpx;
}

.review-user {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.review-avatar {
	width: 64rpx;
	height: 64rpx;
	border-radius: 50%;
	margin-right: 16rpx;
}

.review-user-info {
	flex: 1;
}

.review-nickname {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 4rpx;
}

.review-stars {
	display: flex;
}

.review-time {
	font-size: 22rpx;
	color: #cccccc;
}

.review-content {
	font-size: 28rpx;
	color: #333333;
	line-height: 1.6;
	margin-bottom: 16rpx;
}

.review-images {
	display: flex;
	flex-wrap: wrap;
	gap: 12rpx;
	margin-bottom: 16rpx;
}

.review-image {
	width: 180rpx;
	height: 180rpx;
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
