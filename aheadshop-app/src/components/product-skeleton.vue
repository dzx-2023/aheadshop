<template>
	<view class="skeleton-wrapper">
		<view class="skeleton-item" v-for="i in count" :key="i">
			<view class="skeleton-image"></view>
			<view class="skeleton-info">
				<view class="skeleton-line long"></view>
				<view class="skeleton-line short"></view>
				<view class="skeleton-price"></view>
			</view>
		</view>
	</view>
</template>

<script setup>
defineProps({
	count: {
		type: Number,
		default: 6
	}
})
</script>

<style scoped>
.skeleton-wrapper {
	display: flex;
	flex-wrap: wrap;
	padding: 16rpx;
	gap: 16rpx;
}

.skeleton-item {
	width: calc(50% - 8rpx);
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
}

.skeleton-image {
	width: 100%;
	height: 340rpx;
	background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 50%, #f5f5f5 75%);
	background-size: 200% 100%;
	animation: shimmer 1.5s infinite;
}

.skeleton-info {
	padding: 16rpx;
}

.skeleton-line {
	height: 28rpx;
	background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 50%, #f5f5f5 75%);
	background-size: 200% 100%;
	animation: shimmer 1.5s infinite;
	border-radius: 8rpx;
	margin-bottom: 12rpx;
}

.skeleton-line.long {
	width: 90%;
}

.skeleton-line.short {
	width: 60%;
}

.skeleton-price {
	width: 40%;
	height: 32rpx;
	background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 50%, #f5f5f5 75%);
	background-size: 200% 100%;
	animation: shimmer 1.5s infinite;
	border-radius: 8rpx;
}

@keyframes shimmer {
	0% {
		background-position: 200% 0;
	}
	100% {
		background-position: -200% 0;
	}
}
</style>
