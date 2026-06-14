<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input">
				<text class="icon">🔍</text>
				<input
					type="text"
					v-model="keyword"
					placeholder="搜索商品"
					focus
					@confirm="doSearch"
					@input="onInput"
					class="search-field"
				/>
				<text class="clear" v-if="keyword" @click="clearKeyword">✕</text>
			</view>
			<text class="cancel" @click="goBack">取消</text>
		</view>

		<!-- 搜索建议 -->
		<view class="suggestions" v-if="keyword && suggestions.length > 0">
			<view
				class="suggestion-item"
				v-for="(item, index) in suggestions"
				:key="index"
				@click="selectSuggestion(item)"
			>
				<text class="suggestion-text">{{ item }}</text>
			</view>
		</view>

		<!-- 搜索历史和热门搜索 -->
		<view class="search-sections" v-else>
			<!-- 搜索历史 -->
			<view class="section" v-if="searchHistory.length > 0">
				<view class="section-header">
					<text class="section-title">搜索历史</text>
					<text class="section-clear" @click="clearHistory">清除</text>
				</view>
				<view class="tag-list">
					<view
						class="tag-item"
						v-for="(item, index) in searchHistory"
						:key="index"
						@click="selectKeyword(item)"
					>
						<text>{{ item }}</text>
					</view>
				</view>
			</view>

			<!-- 热门搜索 -->
			<view class="section">
				<view class="section-header">
					<text class="section-title">热门搜索</text>
				</view>
				<view class="tag-list">
					<view
						class="tag-item"
						:class="{ hot: index < 3 }"
						v-for="(item, index) in hotKeywords"
						:key="index"
						@click="selectKeyword(item)"
					>
						<text>{{ item }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const keyword = ref('')
const suggestions = ref([])
const searchHistory = ref([])
const hotKeywords = ref(['手机', '电脑', '耳机', '衣服', '鞋子', '化妆品', '零食', '家电'])

onMounted(() => {
	loadSearchHistory()
})

const loadSearchHistory = () => {
	try {
		const history = uni.getStorageSync('searchHistory')
		if (history) {
			searchHistory.value = JSON.parse(history)
		}
	} catch (e) {
		searchHistory.value = []
	}
}

const onInput = () => {
	// 搜索建议功能（后端暂无此接口，留空）
	suggestions.value = []
}

const doSearch = () => {
	if (!keyword.value) return

	saveSearchHistory(keyword.value)

	uni.navigateTo({
		url: `/pages/product/list?keyword=${encodeURIComponent(keyword.value)}`
	})
}

const selectSuggestion = (item) => {
	keyword.value = item
	doSearch()
}

const selectKeyword = (item) => {
	keyword.value = item
	doSearch()
}

const clearKeyword = () => {
	keyword.value = ''
	suggestions.value = []
}

const saveSearchHistory = (kw) => {
	let history = [...searchHistory.value]
	const index = history.indexOf(kw)
	if (index > -1) {
		history.splice(index, 1)
	}
	history.unshift(kw)
	if (history.length > 20) {
		history = history.slice(0, 20)
	}
	searchHistory.value = history
	uni.setStorageSync('searchHistory', JSON.stringify(history))
}

const clearHistory = () => {
	uni.showModal({
		title: '提示',
		content: '确定要清除搜索历史吗？',
		success: (res) => {
			if (res.confirm) {
				searchHistory.value = []
				uni.removeStorageSync('searchHistory')
			}
		}
	})
}

const goBack = () => {
	uni.navigateBack()
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #ffffff;
}

.search-bar {
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #eeeeee;
}

.search-input {
	flex: 1;
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #f5f5f5;
	border-radius: 40rpx;
}

.icon {
	margin-right: 12rpx;
}

.search-field {
	flex: 1;
	font-size: 28rpx;
}

.clear {
	padding: 8rpx;
	color: #999999;
}

.cancel {
	padding: 16rpx;
	font-size: 28rpx;
	color: #666666;
}

.suggestions {
	background-color: #ffffff;
}

.suggestion-item {
	padding: 24rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.suggestion-text {
	font-size: 28rpx;
	color: #333333;
}

.search-sections {
	padding: 24rpx;
}

.section {
	margin-bottom: 40rpx;
}

.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 24rpx;
}

.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.section-clear {
	font-size: 26rpx;
	color: #999999;
}

.tag-list {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}

.tag-item {
	padding: 12rpx 24rpx;
	background-color: #f5f5f5;
	border-radius: 32rpx;
}

.tag-item text {
	font-size: 26rpx;
	color: #666666;
}

.tag-item.hot {
	background-color: #fff5f0;
}

.tag-item.hot text {
	color: #ff6b35;
}
</style>
