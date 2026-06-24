<template>
  <div class="dashboard-page">
    <div class="dashboard-container">
      <!-- 顶部欢迎区 -->
      <div class="welcome-section">
        <div class="welcome-left">
          <div class="avatar-wrapper">
            <el-avatar :size="72" :src="userInfo?.avatar" icon="User" />
            <div class="online-dot"></div>
          </div>
          <div class="welcome-text">
            <h1>{{ greeting }}，{{ userInfo?.nickname || userInfo?.username }}</h1>
            <p class="welcome-sub">欢迎回到 AheadShop 个人中心</p>
          </div>
        </div>
        <div class="welcome-right">
          <div class="quick-stat">
            <span class="stat-value">{{ distInfo?.totalTeamCount || 0 }}</span>
            <span class="stat-label">团队成员</span>
          </div>
          <div class="quick-stat">
            <span class="stat-value">{{ orderCount }}</span>
            <span class="stat-label">我的订单</span>
          </div>
        </div>
      </div>

      <!-- 核心数据卡片 -->
      <div class="stats-grid">
        <div class="stat-card accent">
          <div class="card-icon">💰</div>
          <div class="card-content">
            <div class="card-label">累计佣金</div>
            <div class="card-value">¥{{ distInfo?.totalCommission?.toFixed(2) || '0.00' }}</div>
          </div>
          <div class="card-trend" v-if="distInfo?.totalCommission">
            <span class="trend-icon">📈</span>
          </div>
        </div>
        <div class="stat-card primary">
          <div class="card-icon">✅</div>
          <div class="card-content">
            <div class="card-label">可提现</div>
            <div class="card-value">¥{{ distInfo?.availableCommission?.toFixed(2) || '0.00' }}</div>
          </div>
          <el-button
            v-if="distInfo?.availableCommission"
            size="small"
            class="withdraw-mini-btn"
            @click="showWithdrawDialog = true"
          >
            提现
          </el-button>
        </div>
        <div class="stat-card muted">
          <div class="card-icon">⏳</div>
          <div class="card-content">
            <div class="card-label">冻结中</div>
            <div class="card-value">¥{{ distInfo?.frozenCommission?.toFixed(2) || '0.00' }}</div>
          </div>
          <div class="card-hint">7天后自动结算</div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="main-grid">
        <!-- 左列 -->
        <div class="left-col">
          <!-- 我的邀请码 -->
          <div class="panel">
            <div class="panel-header">
              <h3>我的邀请码</h3>
              <span class="code-hint">分享邀请码，好友下单你赚佣金</span>
            </div>
            <div v-if="userInfo?.inviteCode" class="invite-code-area">
              <div class="code-display">
                <span class="code-text">{{ userInfo.inviteCode }}</span>
                <button class="copy-btn" @click="copyCode">
                  {{ codeCopied ? '✅ 已复制' : '📋 复制' }}
                </button>
              </div>
              <div class="invite-link-row">
                <span class="link-label">邀请链接：</span>
                <span class="link-url">{{ inviteLink }}</span>
                <button class="copy-btn mini" @click="copyLink">
                  {{ linkCopied ? '已复制' : '复制' }}
                </button>
              </div>
            </div>
            <div v-else class="empty-state small">
              <p>加载中...</p>
            </div>
          </div>

          <!-- 绑定的推荐人 -->
          <div class="panel">
            <div class="panel-header">
              <h3>我的推荐人</h3>
            </div>
            <div v-if="referrerInfo" class="referrer-card">
              <div class="referrer-info">
                <el-avatar :size="48" :src="referrerInfo.avatar" icon="User" />
                <div class="referrer-detail">
                  <div class="referrer-name">{{ referrerInfo.nickname }}</div>
                  <div class="referrer-meta">
                    <span>邀请码：{{ referrerInfo.inviteCode }}</span>
                    <span class="meta-sep">·</span>
                    <span>绑定时间：{{ formatTime(referrerInfo.bindTime) }}</span>
                  </div>
                </div>
              </div>
              <el-button
                type="danger"
                size="small"
                plain
                @click="handleUnbind"
                :loading="unbinding"
              >
                解除绑定
              </el-button>
            </div>
            <div v-else class="empty-state">
              <div class="empty-icon">🔗</div>
              <p>暂无推荐人</p>
              <p class="empty-hint">注册时填写邀请码即可绑定推荐关系</p>
            </div>
          </div>

          <!-- 最近佣金 -->
          <div class="panel">
            <div class="panel-header">
              <h3>最近佣金</h3>
              <router-link to="/distribution/commission" class="view-all">查看全部 →</router-link>
            </div>
            <div v-if="recentCommissions.length === 0" class="empty-state small">
              <p>暂无佣金记录</p>
            </div>
            <div v-else class="commission-list">
              <div v-for="item in recentCommissions" :key="item.id" class="commission-item">
                <div class="item-left">
                  <div class="item-order">订单 {{ item.orderNo }}</div>
                  <div class="item-time">{{ formatTime(item.createTime) }}</div>
                </div>
                <div class="item-right">
                  <span class="item-amount">+¥{{ item.commissionAmount?.toFixed(2) }}</span>
                  <el-tag :type="CommissionStatusMap[item.status]?.type as any" size="small">
                    {{ CommissionStatusMap[item.status]?.label }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右列 -->
        <div class="right-col">
          <!-- 快捷操作 -->
          <div class="panel">
            <div class="panel-header">
              <h3>快捷操作</h3>
            </div>
            <div class="quick-actions">
              <router-link to="/order" class="action-btn">
                <span class="action-icon">📦</span>
                <span class="action-text">我的订单</span>
              </router-link>
              <router-link to="/distribution/team" class="action-btn">
                <span class="action-icon">👥</span>
                <span class="action-text">我的团队</span>
              </router-link>
              <router-link to="/distribution/commission" class="action-btn">
                <span class="action-icon">📊</span>
                <span class="action-text">佣金明细</span>
              </router-link>
              <router-link to="/distribution/withdraw" class="action-btn">
                <span class="action-icon">🏦</span>
                <span class="action-text">提现记录</span>
              </router-link>
              <router-link to="/refund" class="action-btn">
                <span class="action-icon">↩️</span>
                <span class="action-text">退款/售后</span>
              </router-link>
              <div class="action-btn" @click="activeMenu = 'profile'">
                <span class="action-icon">⚙️</span>
                <span class="action-text">个人设置</span>
              </div>
            </div>
          </div>

          <!-- 个人信息编辑 -->
          <div class="panel">
            <div class="panel-header">
              <h3>个人信息</h3>
              <el-button text type="primary" size="small" @click="activeMenu = 'profile'" v-if="activeMenu !== 'profile'">
                编辑
              </el-button>
            </div>
            <div v-if="activeMenu !== 'profile'" class="profile-preview">
              <div class="preview-item">
                <span class="preview-label">用户名</span>
                <span class="preview-value">{{ userInfo?.username || '-' }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">昵称</span>
                <span class="preview-value">{{ userInfo?.nickname || '-' }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">手机号</span>
                <span class="preview-value">{{ profileForm.phone || '-' }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">邮箱</span>
                <span class="preview-value">{{ profileForm.email || '-' }}</span>
              </div>
            </div>
            <el-form v-else :model="profileForm" label-width="70px" class="profile-form" size="small">
              <el-form-item label="头像">
                <div class="avatar-upload">
                  <el-avatar :size="56" :src="profileForm.avatar" icon="User" />
                  <el-upload
                    :show-file-list="false"
                    :http-request="handleAvatarUpload"
                    accept="image/*"
                  >
                    <el-button size="small" plain>更换</el-button>
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
                <el-button type="primary" @click="handleSaveProfile" :loading="saving" size="small">保存</el-button>
                <el-button @click="activeMenu = 'preview'" size="small">取消</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 收货地址 -->
          <div class="panel">
            <div class="panel-header">
              <h3>收货地址</h3>
              <el-button text type="primary" size="small" @click="openAddressDialog()">
                + 新增
              </el-button>
            </div>
            <div v-if="addresses.length === 0" class="empty-state small">
              <p>暂无收货地址</p>
            </div>
            <div v-else class="address-list">
              <div v-for="addr in addresses.slice(0, 3)" :key="addr.id" class="address-item">
                <div class="address-main">
                  <div class="address-top">
                    <span class="address-name">{{ addr.receiverName }}</span>
                    <span class="address-phone">{{ addr.receiverPhone }}</span>
                    <el-tag v-if="addr.isDefault === 1" type="danger" size="small">默认</el-tag>
                  </div>
                  <div class="address-detail">
                    {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
                  </div>
                </div>
                <div class="address-actions">
                  <el-button text size="small" @click="openAddressDialog(addr)">编辑</el-button>
                  <el-button text size="small" type="danger" @click="handleDeleteAddress(addr.id)">删除</el-button>
                </div>
              </div>
              <div v-if="addresses.length > 3" class="address-more">
                <router-link to="/user" class="more-link">查看全部 {{ addresses.length }} 个地址 →</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 提现弹窗 -->
    <el-dialog v-model="showWithdrawDialog" title="申请提现" width="420px" :close-on-click-modal="false">
      <el-form :model="withdrawForm" label-width="80px">
        <el-form-item label="可提现">
          <span class="balance-text">¥{{ distInfo?.availableCommission?.toFixed(2) || '0.00' }}</span>
        </el-form-item>
        <el-form-item label="提现金额">
          <el-input-number
            v-model="withdrawForm.amount"
            :min="100"
            :max="distInfo?.availableCommission || 0"
            :step="100"
            :precision="2"
            style="width: 100%"
          />
          <div class="form-tip">最低提现金额 100 元</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showWithdrawDialog = false">取消</el-button>
        <el-button type="primary" @click="handleWithdraw" :loading="withdrawing">
          确认提现
        </el-button>
      </template>
    </el-dialog>

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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { getUserInfo, updateUserInfo, getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/user'
import { getDistributionInfo, getReferrerInfo, unbindReferral, getCommissionList, applyWithdraw } from '@/api/distribution'
import type { DistributionInfo, ReferrerInfo, CommissionRecord } from '@/types/distribution'
import { CommissionStatusMap } from '@/types/distribution'
import type { AddressItem, AddressForm } from '@/types/address'
import type { OrderPageItem } from '@/types/order'
import { getOrderList } from '@/api/order'
import request from '@/api/request'

const userStore = useUserStore()

// ========== 问候语 ==========
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// ========== 用户信息 ==========
const activeMenu = ref('preview')
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
    activeMenu.value = 'preview'
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// ========== 分销数据 ==========
const distInfo = ref<DistributionInfo | null>(null)
const referrerInfo = ref<ReferrerInfo | null>(null)
const recentCommissions = ref<CommissionRecord[]>([])
const orderCount = ref(0)
const codeCopied = ref(false)
const linkCopied = ref(false)
const unbinding = ref(false)

const inviteLink = computed(() => {
  if (!userInfo.value?.inviteCode) return ''
  return `${window.location.origin}/register?invite=${userInfo.value.inviteCode}`
})

const copyCode = async () => {
  if (!userInfo.value?.inviteCode) return
  try {
    await navigator.clipboard.writeText(userInfo.value.inviteCode)
    codeCopied.value = true
    ElMessage.success('邀请码已复制')
    setTimeout(() => (codeCopied.value = false), 2000)
  } catch {
    ElMessage.error('复制失败')
  }
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(inviteLink.value)
    linkCopied.value = true
    ElMessage.success('邀请链接已复制')
    setTimeout(() => (linkCopied.value = false), 2000)
  } catch {
    ElMessage.error('复制失败')
  }
}

const handleUnbind = async () => {
  try {
    await ElMessageBox.confirm(
      '解除绑定后将不再与该推荐人关联，确定继续？',
      '解除推荐关系',
      { type: 'warning', confirmButtonText: '确定解绑', cancelButtonText: '取消' }
    )
    unbinding.value = true
    await unbindReferral()
    ElMessage.success('已解除推荐关系')
    referrerInfo.value = null
  } catch {
    // error handled by interceptor
  } finally {
    unbinding.value = false
  }
}

// ========== 提现 ==========
const showWithdrawDialog = ref(false)
const withdrawing = ref(false)
const withdrawForm = ref({ amount: 100 })

const handleWithdraw = async () => {
  if (withdrawForm.value.amount < 100) {
    ElMessage.warning('最低提现金额为 100 元')
    return
  }
  withdrawing.value = true
  try {
    await applyWithdraw({ amount: withdrawForm.value.amount })
    ElMessage.success('提现申请已提交')
    showWithdrawDialog.value = false
    withdrawForm.value.amount = 100
    // 刷新分销数据
    const info = await getDistributionInfo()
    distInfo.value = info
  } catch {
    // error handled by interceptor
  } finally {
    withdrawing.value = false
  }
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
    Object.assign(addressForm, {
      id: addr.id,
      receiverName: addr.receiverName,
      receiverPhone: addr.receiverPhone,
      province: addr.province,
      city: addr.city,
      district: addr.district,
      detailAddress: addr.detailAddress,
    })
  } else {
    Object.assign(addressForm, {
      id: undefined,
      receiverName: '',
      receiverPhone: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
    })
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

const handleDeleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('删除成功')
    await loadAddresses()
  } catch {}
}

// ========== 工具函数 ==========
const formatTime = (time: string | null) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

// ========== 初始化 ==========
onMounted(async () => {
  await loadUserInfo()
  loadAddresses()

  // 加载分销数据
  try {
    const res = await getDistributionInfo()
    distInfo.value = res.data
  } catch {
    // 未开通分销商
  }

  try {
    const res = await getReferrerInfo()
    referrerInfo.value = res.data
  } catch {
    // ignore
  }

  try {
    const res = await getCommissionList({ pageNum: 1, pageSize: 5 })
    recentCommissions.value = res.data?.records || []
  } catch {
    // ignore
  }

  try {
    const res = await getOrderList({ pageNum: 1, pageSize: 1 })
    orderCount.value = res.data?.total || 0
  } catch {
    // ignore
  }
})
</script>

<style scoped>
.dashboard-page {
  min-height: 100vh;
  background: var(--color-cream, #fdfbf7);
  padding: 24px 0 80px;
}

.dashboard-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
}

/* ===== 欢迎区 ===== */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding: 28px 32px;
  background: linear-gradient(135deg, var(--color-charcoal, #2d2926) 0%, #4a4543 100%);
  border-radius: var(--radius-lg, 12px);
  color: #fff;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-wrapper {
  position: relative;
}

.online-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 14px;
  height: 14px;
  background: #52c41a;
  border: 3px solid var(--color-charcoal, #2d2926);
  border-radius: 50%;
}

.welcome-text h1 {
  font-family: var(--font-display, 'Playfair Display', serif);
  font-size: 24px;
  margin: 0 0 4px;
  font-weight: 600;
}

.welcome-sub {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

.welcome-right {
  display: flex;
  gap: 32px;
}

.quick-stat {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  font-family: var(--font-display, 'Playfair Display', serif);
  color: var(--color-amber, #d4a574);
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 2px;
}

/* ===== 数据卡片 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.06));
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card.accent::before {
  background: var(--color-amber, #d4a574);
}

.stat-card.primary::before {
  background: var(--color-charcoal, #2d2926);
}

.stat-card.muted::before {
  background: #ccc;
}

.card-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.card-content {
  flex: 1;
}

.card-label {
  font-size: 12px;
  color: var(--color-text-muted, #8a8580);
  margin-bottom: 4px;
}

.card-value {
  font-size: 24px;
  font-weight: 700;
  font-family: var(--font-display, 'Playfair Display', serif);
  color: var(--color-charcoal, #2d2926);
}

.card-trend {
  position: absolute;
  top: 12px;
  right: 12px;
}

.trend-icon {
  font-size: 16px;
}

.withdraw-mini-btn {
  position: absolute;
  bottom: 16px;
  right: 16px;
  background: var(--color-charcoal, #2d2926) !important;
  border-color: var(--color-charcoal, #2d2926) !important;
  color: #fff;
}

.card-hint {
  position: absolute;
  bottom: 12px;
  right: 16px;
  font-size: 11px;
  color: #bbb;
}

/* ===== 主内容网格 ===== */
.main-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
}

/* ===== 面板通用 ===== */
.panel {
  background: #fff;
  border-radius: var(--radius-lg, 12px);
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm, 0 1px 3px rgba(0, 0, 0, 0.06));
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.panel-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-charcoal, #2d2926);
  margin: 0;
}

.view-all {
  font-size: 13px;
  color: var(--color-amber, #d4a574);
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.code-hint {
  font-size: 12px;
  color: var(--color-text-muted, #8a8580);
}

/* ===== 邀请码 ===== */
.invite-code-area {
  background: var(--color-cream, #fdfbf7);
  border: 2px dashed var(--color-amber, #d4a574);
  border-radius: var(--radius-md, 8px);
  padding: 20px;
}

.code-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 12px;
}

.code-text {
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 6px;
  color: var(--color-charcoal, #2d2926);
  font-family: 'Courier New', monospace;
}

.copy-btn {
  background: var(--color-charcoal, #2d2926);
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: var(--radius-md, 8px);
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  transition: opacity 0.2s;
}

.copy-btn:hover {
  opacity: 0.85;
}

.copy-btn.mini {
  padding: 4px 12px;
  font-size: 12px;
}

.invite-link-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.link-label {
  white-space: nowrap;
}

.link-url {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--color-amber, #d4a574);
}

/* ===== 推荐人卡片 ===== */
.referrer-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: var(--color-cream, #fdfbf7);
  border-radius: var(--radius-md, 8px);
}

.referrer-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.referrer-name {
  font-weight: 600;
  color: var(--color-charcoal, #2d2926);
  font-size: 15px;
  margin-bottom: 4px;
}

.referrer-meta {
  font-size: 12px;
  color: var(--color-text-muted, #8a8580);
}

.meta-sep {
  margin: 0 6px;
}

/* ===== 佣金列表 ===== */
.commission-list {
  display: flex;
  flex-direction: column;
}

.commission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #f0eeea;
}

.commission-item:last-child {
  border-bottom: none;
}

.item-order {
  font-size: 14px;
  color: var(--color-charcoal, #2d2926);
  font-weight: 500;
}

.item-time {
  font-size: 12px;
  color: #999;
  margin-top: 3px;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-amount {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-amber, #d4a574);
}

/* ===== 快捷操作 ===== */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  background: var(--color-cream, #fdfbf7);
  border-radius: var(--radius-md, 8px);
  text-decoration: none;
  color: var(--color-charcoal, #2d2926);
  transition: all 0.2s;
  cursor: pointer;
}

.action-btn:hover {
  background: #f0eeea;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 24px;
}

.action-text {
  font-size: 13px;
  font-weight: 500;
}

/* ===== 个人信息预览 ===== */
.profile-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f3ef;
}

.preview-item:last-child {
  border-bottom: none;
}

.preview-label {
  font-size: 13px;
  color: var(--color-text-muted, #8a8580);
}

.preview-value {
  font-size: 14px;
  color: var(--color-charcoal, #2d2926);
  font-weight: 500;
}

/* ===== 个人信息表单 ===== */
.profile-form {
  margin-top: 4px;
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ===== 收货地址 ===== */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px;
  background: var(--color-cream, #fdfbf7);
  border-radius: var(--radius-md, 8px);
}

.address-top {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.address-name {
  font-weight: 600;
  color: var(--color-charcoal, #2d2926);
  font-size: 14px;
}

.address-phone {
  font-size: 13px;
  color: var(--color-text, #5a5652);
}

.address-detail {
  font-size: 13px;
  color: var(--color-text-muted, #8a8580);
  line-height: 1.5;
}

.address-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.address-more {
  text-align: center;
  padding-top: 4px;
}

.more-link {
  font-size: 13px;
  color: var(--color-amber, #d4a574);
  text-decoration: none;
}

.more-link:hover {
  text-decoration: underline;
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 32px 16px;
}

.empty-state.small {
  padding: 20px 16px;
}

.empty-icon {
  font-size: 36px;
  margin-bottom: 12px;
}

.empty-state p {
  color: var(--color-text-muted, #8a8580);
  font-size: 14px;
  margin: 4px 0;
}

.empty-hint {
  font-size: 12px !important;
  color: #bbb !important;
}

/* ===== 提现弹窗 ===== */
.balance-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-amber, #d4a574);
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .main-grid {
    grid-template-columns: 1fr;
  }

  .welcome-section {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .welcome-left {
    flex-direction: column;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 600px) {
  .dashboard-container {
    padding: 0 12px;
  }

  .welcome-section {
    padding: 20px;
  }

  .welcome-text h1 {
    font-size: 20px;
  }

  .stat-value {
    font-size: 22px;
  }

  .code-text {
    font-size: 24px;
    letter-spacing: 3px;
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
