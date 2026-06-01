<template>
  <div class="user-center">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="user-card">
        <el-avatar :size="64" :src="userInfo?.avatar" icon="User" />
        <div class="user-name">{{ userInfo?.nickname || userInfo?.username }}</div>
      </div>
      <el-menu :default-active="activeMenu" @select="handleMenuSelect">
        <el-menu-item index="profile">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
        <el-menu-item index="orders">
          <el-icon><List /></el-icon>
          <span>我的订单</span>
        </el-menu-item>
        <el-menu-item index="address">
          <el-icon><Location /></el-icon>
          <span>收货地址</span>
        </el-menu-item>
        <el-menu-item index="reviews">
          <el-icon><ChatDotRound /></el-icon>
          <span>我的评价</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 内容区 -->
    <main class="content">
      <!-- 个人信息 -->
      <section v-if="activeMenu === 'profile'" class="section">
        <h3>个人信息</h3>
        <el-form :model="profileForm" label-width="80px" class="profile-form">
          <el-form-item label="头像">
            <div class="avatar-upload">
              <el-avatar :size="80" :src="profileForm.avatar" icon="User" />
              <el-upload
                :show-file-list="false"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <el-button size="small" type="primary" plain>更换头像</el-button>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="用户名">
            <el-input :model-value="userInfo?.username" disabled />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="profileForm.gender">
              <el-radio :value="0">未设置</el-radio>
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存修改</el-button>
          </el-form-item>
        </el-form>
      </section>

      <!-- 我的订单 -->
      <section v-if="activeMenu === 'orders'" class="section">
        <div class="section-header">
          <h3>我的订单</h3>
          <el-button text type="primary" @click="router.push('/order')">查看全部</el-button>
        </div>
        <el-tabs v-model="orderTab" @tab-change="loadOrders">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="待支付" name="1" />
          <el-tab-pane label="已支付" name="2" />
          <el-tab-pane label="已发货" name="3" />
          <el-tab-pane label="已完成" name="4" />
        </el-tabs>
        <div v-if="orderLoading" v-loading="true" style="height: 200px" />
        <div v-else-if="orders.length === 0" class="empty">
          <el-empty description="暂无订单" />
        </div>
        <div v-else class="order-list">
          <div v-for="order in orders" :key="order.orderNo" class="order-card" @click="router.push(`/order/${order.orderNo}`)">
            <div class="order-header">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <el-tag :type="getStatusType(order.status)" size="small">{{ order.statusText }}</el-tag>
            </div>
            <div class="order-info">
              <span>{{ order.receiverName }} {{ order.receiverPhone }}</span>
              <span class="order-amount">¥{{ order.totalAmount?.toFixed(2) }}</span>
            </div>
            <div class="order-time">{{ order.createTime }}</div>
          </div>
          <el-pagination
            v-if="orderTotal > orderParams.pageSize"
            v-model:current-page="orderParams.pageNum"
            :page-size="orderParams.pageSize"
            :total="orderTotal"
            layout="prev, pager, next"
            @current-change="loadOrders"
          />
        </div>
      </section>

      <!-- 收货地址 -->
      <section v-if="activeMenu === 'address'" class="section">
        <div class="section-header">
          <h3>收货地址</h3>
          <el-button type="primary" size="small" @click="openAddressDialog()">
            <el-icon><Plus /></el-icon> 新增地址
          </el-button>
        </div>
        <div v-if="addresses.length === 0" class="empty">
          <el-empty description="暂无收货地址" />
        </div>
        <div v-else class="address-list">
          <div v-for="addr in addresses" :key="addr.id" class="address-card">
            <div class="address-info">
              <div class="address-top">
                <span class="receiver-name">{{ addr.receiverName }}</span>
                <span class="receiver-phone">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 1" type="danger" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
              </div>
            </div>
            <div class="address-actions">
              <el-button text size="small" @click="openAddressDialog(addr)">编辑</el-button>
              <el-button v-if="addr.isDefault !== 1" text size="small" @click="handleSetDefault(addr.id)">设为默认</el-button>
              <el-button text size="small" type="danger" @click="handleDeleteAddress(addr.id)">删除</el-button>
            </div>
          </div>
          <div v-if="addresses.length >= 10" class="address-limit-tip">
            <el-text type="info" size="small">最多保存 10 个收货地址</el-text>
          </div>
        </div>
      </section>

      <!-- 我的评价 -->
      <section v-if="activeMenu === 'reviews'" class="section">
        <div class="section-header">
          <h3>我的评价</h3>
        </div>

        <!-- 待评价订单 -->
        <div class="review-subsection">
          <h4>待评价订单</h4>
          <div v-if="pendingOrders.length === 0" class="empty-small">
            <el-text type="info">暂无待评价订单</el-text>
          </div>
          <div v-else class="pending-list">
            <div v-for="order in pendingOrders" :key="order.orderNo" class="pending-card">
              <div class="pending-info">
                <span class="pending-no">订单号：{{ order.orderNo }}</span>
                <span class="pending-amount">¥{{ order.totalAmount?.toFixed(2) }}</span>
              </div>
              <el-button type="primary" size="small" @click="openReviewDialog(order)">去评价</el-button>
            </div>
          </div>
        </div>

        <!-- 已评价列表 -->
        <div class="review-subsection">
          <h4>已发表评价</h4>
          <div v-if="reviewLoading" v-loading="true" style="height: 100px" />
          <div v-else-if="reviews.length === 0" class="empty-small">
            <el-text type="info">暂无评价</el-text>
          </div>
          <div v-else class="review-list">
            <div v-for="review in reviews" :key="review.id" class="review-card">
              <div class="review-header">
                <el-rate :model-value="review.score" disabled />
                <span class="review-time">{{ review.createTime }}</span>
              </div>
              <div class="review-content">{{ review.content || '用户未填写评价内容' }}</div>
              <div v-if="review.images" class="review-images">
                <el-image
                  v-for="(img, idx) in parseImages(review.images)"
                  :key="idx"
                  :src="img"
                  :preview-src-list="parseImages(review.images)"
                  fit="cover"
                  class="review-img"
                />
              </div>
              <div v-if="review.reply" class="review-reply">
                <span class="reply-label">商家回复：</span>{{ review.reply }}
              </div>
            </div>
            <el-pagination
              v-if="reviewTotal > reviewParams.pageSize"
              v-model:current-page="reviewParams.pageNum"
              :page-size="reviewParams.pageSize"
              :total="reviewTotal"
              layout="prev, pager, next"
              @current-change="loadReviews"
            />
          </div>
        </div>
      </section>
    </main>

    <!-- 地址编辑弹窗 -->
    <el-dialog
      v-model="addressDialogVisible"
      :title="addressForm.id ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" type="textarea" placeholder="请输入详细地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAddress" :loading="addressSaving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewDialogVisible" title="发表评价" width="500px">
      <el-form :model="reviewForm" ref="reviewFormRef" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.score" show-text :texts="['差', '较差', '一般', '好', '非常好']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="分享你的使用体验（选填）" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="reviewForm.images" placeholder="图片URL，多张用逗号分隔（选填）" />
        </el-form-item>
        <el-form-item label="匿名评价">
          <el-switch v-model="reviewForm.isAnonymous" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview" :loading="reviewSubmitting">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, List, Location, ChatDotRound, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { getUserInfo, updateUserInfo, getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/user'
import { getMyReviews, submitReview, getSpuDetail } from '@/api/product'
import { getOrderList } from '@/api/order'
import type { AddressItem, AddressForm } from '@/types/address'
import type { ReviewItem } from '@/types/product'
import type { OrderPageItem } from '@/types/order'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()

// ========== 侧边栏 ==========
const activeMenu = ref('profile')
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
  if (index === 'orders') loadOrders()
  if (index === 'address') loadAddresses()
  if (index === 'reviews') { loadPendingOrders(); loadReviews() }
}

// ========== 个人信息 ==========
const saving = ref(false)
const userInfo = ref<any>(null)
const profileForm = reactive({
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  gender: 0 as number,
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data
    profileForm.nickname = res.data.nickname || ''
    profileForm.phone = res.data.phone || ''
    profileForm.email = res.data.email || ''
    profileForm.avatar = res.data.avatar || ''
    profileForm.gender = res.data.gender ?? 0
  } catch {}
}

const handleAvatarUpload = async (options: any) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await request.post('/user/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    profileForm.avatar = res.data
    ElMessage.success('头像上传成功')
  } catch {
    ElMessage.error('头像上传失败')
  }
}

const handleSaveProfile = async () => {
  saving.value = true
  try {
    await updateUserInfo({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email,
      avatar: profileForm.avatar,
      gender: profileForm.gender,
    })
    ElMessage.success('保存成功')
    await loadUserInfo()
    userStore.fetchUserInfo()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// ========== 我的订单 ==========
const orderTab = ref('all')
const orderLoading = ref(false)
const orders = ref<OrderPageItem[]>([])
const orderTotal = ref(0)
const orderParams = reactive({ status: undefined as number | undefined, pageNum: 1, pageSize: 5 })

const loadOrders = async () => {
  orderLoading.value = true
  orderParams.status = orderTab.value === 'all' ? undefined : Number(orderTab.value)
  try {
    const res = await getOrderList(orderParams)
    orders.value = res.data.records || []
    orderTotal.value = res.data.total || 0
  } catch {
    orders.value = []
  } finally {
    orderLoading.value = false
  }
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 1: 'warning', 2: 'success', 3: 'primary', 4: '', 5: 'info', 6: 'danger', 7: 'danger' }
  return (map[status] || '') as any
}

// ========== 收货地址 ==========
const addresses = ref<AddressItem[]>([])
const addressDialogVisible = ref(false)
const addressSaving = ref(false)
const addressFormRef = ref<FormInstance>()
const addressForm = reactive<AddressForm>({
  id: undefined,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
})
const addressRules: FormRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

const loadAddresses = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch {}
}

const openAddressDialog = (addr?: AddressItem) => {
  if (addr) {
    Object.assign(addressForm, { id: addr.id, receiverName: addr.receiverName, receiverPhone: addr.receiverPhone, province: addr.province, city: addr.city, district: addr.district, detailAddress: addr.detailAddress })
  } else {
    Object.assign(addressForm, { id: undefined, receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '' })
  }
  addressDialogVisible.value = true
}

const handleSaveAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate()
  addressSaving.value = true
  try {
    if (addressForm.id) {
      await updateAddress(addressForm as any)
      ElMessage.success('修改成功')
    } else {
      if (addresses.value.length >= 10) {
        ElMessage.warning('最多保存 10 个收货地址')
        return
      }
      await addAddress(addressForm)
      ElMessage.success('新增成功')
    }
    addressDialogVisible.value = false
    await loadAddresses()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    addressSaving.value = false
  }
}

const handleSetDefault = async (id: number) => {
  try {
    await setDefaultAddress(id)
    ElMessage.success('设置成功')
    await loadAddresses()
  } catch {
    ElMessage.error('设置失败')
  }
}

const handleDeleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('删除成功')
    await loadAddresses()
  } catch {}
}

// ========== 我的评价 ==========
const reviewLoading = ref(false)
const reviews = ref<any[]>([])
const reviewTotal = ref(0)
const reviewParams = reactive({ pageNum: 1, pageSize: 10 })
const pendingOrders = ref<OrderPageItem[]>([])

const reviewDialogVisible = ref(false)
const reviewSubmitting = ref(false)
const reviewFormRef = ref<FormInstance>()
const reviewForm = reactive({
  spuId: 0,
  skuId: 0,
  orderId: 0,
  score: 5,
  content: '',
  images: '',
  isAnonymous: 0,
})

const loadReviews = async () => {
  reviewLoading.value = true
  try {
    const res = await getMyReviews(reviewParams)
    reviews.value = res.data?.records || []
    reviewTotal.value = res.data?.total || 0
  } catch {
    reviews.value = []
  } finally {
    reviewLoading.value = false
  }
}

const loadPendingOrders = async () => {
  try {
    const res = await getOrderList({ status: 4, pageNum: 1, pageSize: 50 })
    const allOrders = res.data?.records || []
    // 过滤掉已评价的订单（简化处理，实际可在后端过滤）
    pendingOrders.value = allOrders.slice(0, 5)
  } catch {
    pendingOrders.value = []
  }
}

const openReviewDialog = (order: OrderPageItem) => {
  // 从订单中取第一个商品的 spuId/skuId（简化处理）
  Object.assign(reviewForm, {
    spuId: (order as any).spuId || 0,
    skuId: (order as any).skuId || 0,
    orderId: (order as any).id || 0,
    score: 5,
    content: '',
    images: '',
    isAnonymous: 0,
  })
  reviewDialogVisible.value = true
}

const handleSubmitReview = async () => {
  if (reviewForm.score < 1) {
    ElMessage.warning('请选择评分')
    return
  }
  reviewSubmitting.value = true
  try {
    await submitReview(reviewForm)
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    await loadReviews()
    await loadPendingOrders()
  } catch {
    ElMessage.error('评价失败')
  } finally {
    reviewSubmitting.value = false
  }
}

const parseImages = (images: string): string[] => {
  if (!images) return []
  try {
    return JSON.parse(images)
  } catch {
    return images.split(',').map(s => s.trim()).filter(Boolean)
  }
}

// ========== 初始化 ==========
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.user-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  gap: 24px;
  min-height: calc(100vh - 160px);
}

.sidebar {
  width: 220px;
  flex-shrink: 0;
}

.user-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.user-name {
  margin-top: 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.sidebar :deep(.el-menu) {
  border-radius: 12px;
  border-right: none;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.content {
  flex: 1;
  min-width: 0;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.section h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-header h3 {
  margin-bottom: 0;
}

/* 个人信息 */
.profile-form {
  max-width: 480px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 订单 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.order-card:hover {
  border-color: var(--color-amber);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.order-no {
  font-size: 13px;
  color: var(--color-text-muted);
}

.order-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.order-amount {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.order-time {
  font-size: 12px;
  color: var(--color-text-muted);
}

/* 收货地址 */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-card {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 16px;
}

.address-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.receiver-name {
  font-weight: 600;
  color: var(--color-charcoal);
}

.receiver-phone {
  color: var(--color-text);
}

.address-detail {
  font-size: 14px;
  color: var(--color-text);
}

.address-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.address-limit-tip {
  text-align: center;
  padding: 8px 0;
}

/* 评价 */
.review-subsection {
  margin-bottom: 24px;
}

.review-subsection h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 12px;
}

.pending-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pending-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 12px 16px;
}

.pending-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.pending-no {
  font-size: 13px;
  color: var(--color-text-muted);
}

.pending-amount {
  font-weight: 600;
  color: var(--color-charcoal);
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-card {
  border: 1px solid var(--color-border);
  border-radius: 8px;
  padding: 16px;
}

.review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.review-time {
  font-size: 12px;
  color: var(--color-text-muted);
}

.review-content {
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.6;
  margin-bottom: 8px;
}

.review-images {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.review-img {
  width: 80px;
  height: 80px;
  border-radius: 6px;
}

.review-reply {
  background: var(--color-surface);
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 13px;
  color: var(--color-text);
}

.reply-label {
  color: var(--color-amber);
  font-weight: 600;
}

.empty, .empty-small {
  padding: 40px 0;
  text-align: center;
}

.empty-small {
  padding: 16px 0;
}
</style>
