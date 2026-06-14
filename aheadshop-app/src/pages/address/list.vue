<template>
	<view class="container">
		<!-- 地址列表 -->
		<view class="address-list">
			<view
				class="address-item"
				v-for="(item, index) in addressList"
				:key="item.id"
				@click="selectAddress(item)"
			>
				<view class="address-info">
					<view class="address-top">
						<text class="address-name">{{ item.receiverName }}</text>
						<text class="address-phone">{{ item.receiverPhone }}</text>
						<view class="default-tag" v-if="item.isDefault === 1">默认</view>
					</view>
					<text class="address-detail">{{ item.province }}{{ item.city }}{{ item.district }}{{ item.detailAddress }}</text>
				</view>
				<view class="address-actions">
					<view class="action-item" @click.stop="setDefault(item)">
						<view class="radio" :class="{ checked: item.isDefault === 1 }">
							<text v-if="item.isDefault === 1">✓</text>
						</view>
						<text>默认</text>
					</view>
					<view class="action-item" @click.stop="editAddress(item)">
						<text class="icon">✏️</text>
						<text>编辑</text>
					</view>
					<view class="action-item" @click.stop="deleteAddress(item, index)">
						<text class="icon">🗑️</text>
						<text>删除</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty" v-if="addressList.length === 0">
			<text class="empty-text">暂无收货地址</text>
		</view>

		<!-- 添加按钮 -->
		<view class="add-btn" @click="addAddress">
			<text>+ 新增收货地址</text>
		</view>
	</view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { addressApi } from '@/api/index'
import { showToast } from '@/utils/util'

const addressList = ref([])
const isSelectMode = ref(false)

onLoad((options) => {
	if (options && options.select === '1') {
		isSelectMode.value = true
	}
})

onShow(() => {
	loadAddressList()
})

const loadAddressList = async () => {
	try {
		const res = await addressApi.getList()
		addressList.value = res || []
	} catch (error) {
		console.error('加载地址列表失败', error)
	}
}

const selectAddress = (item) => {
	if (isSelectMode.value) {
		// 从订单确认页跳转来的，选择地址后返回
		const eventChannel = getOpenerEventChannel()
		if (eventChannel) {
			eventChannel.emit('selectAddress', {
				id: item.id,
				receiverName: item.receiverName,
				receiverPhone: item.receiverPhone,
				province: item.province,
				city: item.city,
				district: item.district,
				detailAddress: item.detailAddress
			})
		}
		uni.navigateBack()
	} else {
		editAddress(item)
	}
}

const getOpenerEventChannel = () => {
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	return currentPage.getOpenerEventChannel ? currentPage.getOpenerEventChannel() : null
}

const addAddress = () => {
	uni.navigateTo({
		url: '/pages/address/edit'
	})
}

const editAddress = (item) => {
	uni.navigateTo({
		url: `/pages/address/edit?id=${item.id}`
	})
}

const setDefault = async (item) => {
	try {
		await addressApi.setDefault(item.id)
		addressList.value.forEach(addr => {
			addr.isDefault = addr.id === item.id ? 1 : 0
		})
		showToast('设置成功')
	} catch (error) {
		console.error('设置默认地址失败', error)
	}
}

const deleteAddress = (item, index) => {
	uni.showModal({
		title: '提示',
		content: '确定要删除该地址吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					await addressApi.delete(item.id)
					addressList.value.splice(index, 1)
					showToast('删除成功')
				} catch (error) {
					console.error('删除地址失败', error)
				}
			}
		}
	})
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.address-list {
	padding: 16rpx;
}

.address-item {
	background-color: #ffffff;
	border-radius: 16rpx;
	margin-bottom: 16rpx;
	overflow: hidden;
}

.address-info {
	padding: 24rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.address-top {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;
}

.address-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	margin-right: 24rpx;
}

.address-phone {
	font-size: 28rpx;
	color: #666666;
}

.default-tag {
	padding: 2rpx 12rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 22rpx;
	border-radius: 4rpx;
	margin-left: 16rpx;
}

.address-detail {
	font-size: 26rpx;
	color: #666666;
	line-height: 1.5;
}

.address-actions {
	display: flex;
	padding: 16rpx 24rpx;
}

.action-item {
	display: flex;
	align-items: center;
	margin-right: 40rpx;
}

.radio {
	width: 32rpx;
	height: 32rpx;
	border: 2rpx solid #cccccc;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 8rpx;
}

.radio.checked {
	background-color: #ff6b35;
	border-color: #ff6b35;
}

.radio text {
	font-size: 20rpx;
	color: #ffffff;
}

.action-item .icon {
	margin-right: 8rpx;
}

.action-item text {
	font-size: 24rpx;
	color: #666666;
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

.add-btn {
	position: fixed;
	bottom: 40rpx;
	left: 40rpx;
	right: 40rpx;
	height: 88rpx;
	background-color: #ff6b35;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.add-btn text {
	font-size: 30rpx;
	color: #ffffff;
}
</style>
