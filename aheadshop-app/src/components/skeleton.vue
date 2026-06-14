<template>
	<view class="skeleton" :style="{ width: width, height: height }">
		<view class="skeleton-content" :class="type"></view>
	</view>
</template>

<script setup>
defineProps({
	type: {
		type: String,
		default: 'line' // line, rect, circle, avatar, card
	},
	width: {
		type: String,
		default: '100%'
	},
	height: {
		type: String,
		default: '32rpx'
	}
})
</script>

<style scoped>
.skeleton {
	background-color: #f5f5f5;
	border-radius: 8rpx;
	overflow: hidden;
	position: relative;
}

.skeleton-content {
	width: 100%;
	height: 100%;
	background: linear-gradient(90deg, #f5f5f5 25%, #e8e8e8 50%, #f5f5f5 75%);
	background-size: 200% 100%;
	animation: shimmer 1.5s infinite;
}

.skeleton.line {
	height: 32rpx;
	border-radius: 8rpx;
}

.skeleton.rect {
	border-radius: 12rpx;
}

.skeleton.circle {
	border-radius: 50%;
}

.skeleton.avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
}

.skeleton.card {
	height: 200rpx;
	border-radius: 16rpx;
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
