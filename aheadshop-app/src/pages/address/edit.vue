<template>
	<view class="container">
		<view class="form">
			<view class="form-item">
				<text class="label">收货人</text>
				<input
					type="text"
					v-model="form.receiverName"
					placeholder="请输入收货人姓名"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">手机号</text>
				<input
					type="number"
					v-model="form.receiverPhone"
					placeholder="请输入手机号"
					maxlength="11"
					class="input"
				/>
			</view>
			<view class="form-item">
				<text class="label">所在地区</text>
				<picker mode="region" @change="onRegionChange" :value="regionValue">
					<view class="region-input">
						<text :class="{ placeholder: !form.province }">
							{{ form.province ? `${form.province} ${form.city} ${form.district}` : '请选择地区' }}
						</text>
						<text class="arrow">></text>
					</view>
				</picker>
			</view>
			<view class="form-item">
				<text class="label">详细地址</text>
				<input
					type="text"
					v-model="form.detailAddress"
					placeholder="请输入详细地址"
					class="input"
				/>
			</view>
			<view class="form-item switch-item">
				<text class="label">设为默认地址</text>
				<switch
					:checked="form.isDefault === 1"
					@change="form.isDefault = $event.detail.value ? 1 : 0"
					color="#ff6b35"
				/>
			</view>
		</view>

		<button class="btn-save" @click="saveAddress" :disabled="isSaving">
			{{ isSaving ? '保存中...' : '保存' }}
		</button>
		<button class="btn-delete" v-if="isEdit" @click="deleteAddress">删除</button>
	</view>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { addressApi } from '@/api/index'
import { showToast, showLoading, hideLoading } from '@/utils/util'

const isEdit = ref(false)
const editId = ref(null)
const isSaving = ref(false)

const form = reactive({
	receiverName: '',
	receiverPhone: '',
	province: '',
	city: '',
	district: '',
	detailAddress: '',
	isDefault: 0
})

const regionValue = computed(() => {
	if (form.province && form.city && form.district) {
		return [form.province, form.city, form.district]
	}
	return []
})

onLoad((options) => {
	if (options && options.id) {
		isEdit.value = true
		editId.value = options.id
		loadAddress(options.id)
	}
})

const loadAddress = async (id) => {
	try {
		const res = await addressApi.getDetail(id)
		if (res) {
			Object.assign(form, {
				receiverName: res.receiverName || '',
				receiverPhone: res.receiverPhone || '',
				province: res.province || '',
				city: res.city || '',
				district: res.district || '',
				detailAddress: res.detailAddress || '',
				isDefault: res.isDefault || 0
			})
		}
	} catch (error) {
		console.error('加载地址失败', error)
	}
}

const onRegionChange = (e) => {
	const [province, city, district] = e.detail.value
	form.province = province
	form.city = city
	form.district = district
}

const saveAddress = async () => {
	if (!form.receiverName) {
		showToast('请输入收货人姓名')
		return
	}
	if (!form.receiverPhone) {
		showToast('请输入手机号')
		return
	}
	if (!/^1[3-9]\d{9}$/.test(form.receiverPhone)) {
		showToast('请输入正确的手机号')
		return
	}
	if (!form.province) {
		showToast('请选择地区')
		return
	}
	if (!form.detailAddress) {
		showToast('请输入详细地址')
		return
	}

	isSaving.value = true
	try {
		const data = { ...form }
		if (isEdit.value) {
			data.id = editId.value
			await addressApi.update(data)
		} else {
			await addressApi.add(data)
		}
		showToast('保存成功')
		setTimeout(() => {
			uni.navigateBack()
		}, 1500)
	} catch (error) {
		console.error('保存地址失败', error)
	} finally {
		isSaving.value = false
	}
}

const deleteAddress = () => {
	uni.showModal({
		title: '提示',
		content: '确定要删除该地址吗？',
		success: async (res) => {
			if (res.confirm) {
				try {
					await addressApi.delete(editId.value)
					showToast('删除成功')
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
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
	padding: 16rpx;
}

.form {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 0 24rpx;
	margin-bottom: 40rpx;
}

.form-item {
	display: flex;
	align-items: center;
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.form-item:last-child {
	border-bottom: none;
}

.label {
	width: 160rpx;
	font-size: 28rpx;
	color: #333333;
	flex-shrink: 0;
}

.input {
	flex: 1;
	font-size: 28rpx;
}

.region-input {
	flex: 1;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.region-input text {
	font-size: 28rpx;
	color: #333333;
}

.region-input .placeholder {
	color: #999999;
}

.arrow {
	color: #cccccc;
}

.switch-item {
	justify-content: space-between;
}

.btn-save {
	width: 100%;
	height: 88rpx;
	background-color: #ff6b35;
	color: #ffffff;
	font-size: 30rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 24rpx;
}

.btn-save[disabled] {
	opacity: 0.6;
}

.btn-delete {
	width: 100%;
	height: 88rpx;
	background-color: #ffffff;
	color: #ff4444;
	font-size: 30rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border: 2rpx solid #ff4444;
}
</style>
