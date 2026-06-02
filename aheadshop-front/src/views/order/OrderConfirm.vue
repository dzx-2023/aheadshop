<template>
  <div class="order-confirm">
    <h2 class="page-title">确认订单</h2>

    <!-- 收货地址 -->
    <section class="section address-section">
      <div class="section-header">
        <h3>收货地址</h3>
        <el-button text type="primary" @click="showAddressDialog = true">管理地址</el-button>
      </div>
      <div v-if="selectedAddress" class="address-card selected" @click="showAddressDialog = true">
        <div class="address-info">
          <span class="address-name">{{ selectedAddress.receiverName }}</span>
          <span class="address-phone">{{ selectedAddress.receiverPhone }}</span>
          <el-tag v-if="selectedAddress.isDefault" size="small" type="warning">默认</el-tag>
        </div>
        <p class="address-detail">
          {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }} {{ selectedAddress.detailAddress }}
        </p>
        <el-icon class="address-arrow"><ArrowRight /></el-icon>
      </div>
      <div v-else class="address-card empty" @click="showAddressDialog = true">
        <el-icon><Plus /></el-icon>
        <span>请添加收货地址</span>
      </div>
    </section>

    <!-- 商品清单 -->
    <section class="section goods-section">
      <h3>商品清单</h3>
      <div class="goods-list">
        <div v-for="item in cartItems" :key="item.skuId" class="goods-item">
          <div class="goods-image">
            <img :src="item.image || placeholderImg" :alt="item.skuName" />
          </div>
          <div class="goods-info">
            <h4 class="goods-name">{{ item.skuName }}</h4>
            <p class="goods-specs">{{ formatSpecs(item.specs) }}</p>
          </div>
          <div class="goods-price">¥{{ formatPrice(item.price) }}</div>
          <div class="goods-qty">×{{ item.quantity }}</div>
          <div class="goods-subtotal">¥{{ formatPrice(Number(item.price) * item.quantity) }}</div>
        </div>
      </div>
    </section>

    <!-- 订单备注 -->
    <section class="section remark-section">
      <h3>订单备注</h3>
      <el-input v-model="remark" placeholder="选填，请先与商家协商一致" maxlength="200" show-word-limit type="textarea" :rows="2" />
    </section>

    <!-- 费用明细 -->
    <section class="section fee-section">
      <div class="fee-row">
        <span class="fee-label">商品总额</span>
        <span class="fee-value">¥{{ formatPrice(goodsTotal) }}</span>
      </div>
      <div class="fee-row">
        <span class="fee-label">运费</span>
        <span class="fee-value">¥{{ formatPrice(freight) }}</span>
      </div>
      <div v-if="discount > 0" class="fee-row">
        <span class="fee-label">优惠</span>
        <span class="fee-value discount">-¥{{ formatPrice(discount) }}</span>
      </div>
      <div class="fee-row total">
        <span class="fee-label">应付金额</span>
        <span class="fee-value">
          <span class="symbol">¥</span>
          <span class="amount">{{ formatPrice(payAmount) }}</span>
        </span>
      </div>
    </section>

    <!-- 提交按钮 -->
    <div class="submit-bar">
      <div class="submit-info">
        <span>应付：</span>
        <span class="submit-price">¥{{ formatPrice(payAmount) }}</span>
      </div>
      <el-button type="primary" size="large" class="submit-btn" :loading="submitting" :disabled="!selectedAddress" @click="handleSubmit">
        提交订单
      </el-button>
    </div>

    <!-- 地址选择弹窗 -->
    <el-dialog v-model="showAddressDialog" title="选择收货地址" width="560px" :close-on-click-modal="false">
      <div class="address-list">
        <div
          v-for="addr in addressList"
          :key="addr.id"
          class="address-item"
          :class="{ active: selectedAddress?.id === addr.id }"
          @click="selectAddress(addr)"
        >
          <div class="address-item-info">
            <span class="address-name">{{ addr.receiverName }}</span>
            <span class="address-phone">{{ addr.receiverPhone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="warning">默认</el-tag>
          </div>
          <p class="address-detail">
            {{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detailAddress }}
          </p>
        </div>
        <div v-if="!addressList.length" class="empty-address">暂无收货地址</div>
      </div>
      <template #footer>
        <el-button @click="showAddAddress = true">新增地址</el-button>
        <el-button type="primary" @click="showAddressDialog = false">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增地址弹窗 -->
    <el-dialog v-model="showAddAddress" title="新增收货地址" width="500px">
      <el-form ref="addressFormRef" :model="addressForm" :rules="addressRules" label-width="80px">
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
          <el-input v-model="addressForm.detailAddress" placeholder="街道/楼栋/门牌号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddAddress = false">取消</el-button>
        <el-button type="primary" :loading="addressSaving" @click="handleSaveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, ArrowRight } from '@element-plus/icons-vue'
import { getCartList } from '@/api/cart'
import { getSkuInfo } from '@/api/product'
import { getAddressList, addAddress } from '@/api/user'
import { createOrder } from '@/api/order'
import { useCartStore } from '@/store/modules/cart'
import type { CartItem } from '@/types/cart'
import type { AddressItem, AddressForm } from '@/types/address'
import type { FormInstance } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const placeholderImg = 'https://placehold.co/60x60/f5f3ef/d0ccc7?text=No+Image'
const submitting = ref(false)
const buyNowSkuId = ref<number | null>(null)
const buyNowQuantity = ref(1)
const remark = ref('')

// 购物车商品（已勾选）
const cartItems = ref<CartItem[]>([])

// 地址
const addressList = ref<AddressItem[]>([])
const selectedAddress = ref<AddressItem | null>(null)
const showAddressDialog = ref(false)
const showAddAddress = ref(false)
const addressSaving = ref(false)
const addressFormRef = ref<FormInstance>()
const addressForm = ref<AddressForm>({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
})
const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

// 费用计算
const goodsTotal = computed(() =>
  cartItems.value.reduce((sum, i) => sum + Number(i.price) * i.quantity, 0)
)
const freight = ref(0)
const discount = ref(0)
const payAmount = computed(() => Math.max(0, goodsTotal.value + freight.value - discount.value))

function formatPrice(price: number): string {
  return Number(price).toFixed(2)
}

function formatSpecs(specs: string): string {
  try {
    return Object.values(JSON.parse(specs)).join(' / ')
  } catch {
    return specs || ''
  }
}

function selectAddress(addr: AddressItem) {
  selectedAddress.value = addr
}

async function handleSaveAddress() {
  const valid = await addressFormRef.value?.validate().catch(() => false)
  if (!valid) return
  addressSaving.value = true
  try {
    await addAddress(addressForm.value)
    ElMessage.success('地址已保存')
    showAddAddress.value = false
    // 重置表单
    addressForm.value = { receiverName: '', receiverPhone: '', province: '', city: '', district: '', detailAddress: '' }
    // 刷新地址列表
    const res: any = await getAddressList()
    addressList.value = res.data || []
    // 自动选中新增的（或默认地址）
    const defaultAddr = addressList.value.find((a) => a.isDefault)
    if (defaultAddr) selectedAddress.value = defaultAddr
    else if (addressList.value.length) selectedAddress.value = addressList.value[addressList.value.length - 1]
  } catch {
    // 错误已在拦截器处理
  } finally {
    addressSaving.value = false
  }
}

async function handleSubmit() {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const orderData: Record<string, any> = {
      addressId: selectedAddress.value.id,
      remark: remark.value || undefined,
    }
    // 直接购买模式传递 skuId 和 quantity
    if (buyNowSkuId.value) {
      orderData.skuId = buyNowSkuId.value
      orderData.quantity = buyNowQuantity.value
    }
    const res: any = await createOrder(orderData)
    ElMessage.success('订单已创建')
    // 清空购物车已选
    cartStore.fetchCount().catch(() => {})
    // 跳转支付页
    const data = res.data
    router.replace(`/order/${data.orderNo}`)
  } catch {
    // 错误已在拦截器处理
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const skuId = Number(route.query.skuId)
  const buyQuantity = Number(route.query.quantity) || 1

  // 加载地址（两种模式都需要）
  const addrRes = await getAddressList().catch(() => null)
  if (addrRes) {
    addressList.value = (addrRes as any).data || []
    const defaultAddr = addressList.value.find((a) => a.isDefault)
    if (defaultAddr) selectedAddress.value = defaultAddr
    else if (addressList.value.length) selectedAddress.value = addressList.value[0]
  }

  if (skuId) {
    // 立即购买模式
    buyNowSkuId.value = skuId
    buyNowQuantity.value = buyQuantity
    try {
      const skuRes: any = await getSkuInfo(skuId)
      const sku = skuRes.data
      if (sku) {
        cartItems.value = [{
          skuId: sku.id,
          skuName: sku.skuName,
          specs: sku.specs,
          price: sku.price,
          quantity: buyQuantity,
          image: sku.image,
          checked: 1,
        } as CartItem]
      }
    } catch {
      ElMessage.error('商品信息加载失败')
      router.replace('/')
    }
  } else {
    // 购物车模式：加载已勾选商品
    try {
      const cartRes: any = await getCartList()
      const allItems: CartItem[] = cartRes.data || []
      cartItems.value = allItems.filter((i) => i.checked === 1)
      if (!cartItems.value.length) {
        ElMessage.warning('请先选择商品')
        router.replace('/cart')
      }
    } catch {
      // 静默处理
    }
  }
})
</script>

<style scoped>
.order-confirm {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px 40px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 24px;
}

.section {
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  padding: 24px;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section h3 {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 16px;
}

.section-header h3 {
  margin-bottom: 0;
}

/* ── 地址 ── */
.address-card {
  padding: 16px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.address-card.selected {
  background: rgba(212, 165, 116, 0.06);
  border: 1px solid var(--color-amber);
}

.address-card.selected:hover {
  background: rgba(212, 165, 116, 0.1);
}

.address-card.empty {
  border: 1px dashed var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--color-text-muted);
  font-family: var(--font-body);
  font-size: 14px;
  padding: 32px;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.address-name {
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  color: var(--color-charcoal);
}

.address-phone {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
}

.address-detail {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text);
  line-height: 1.5;
}

.address-arrow {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-muted);
}

/* ── 商品清单 ── */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  background: var(--color-surface);
  border-radius: 8px;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f3ef;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-charcoal);
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 4px;
}

.goods-specs {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
}

.goods-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  color: var(--color-charcoal);
  width: 80px;
  text-align: center;
}

.goods-qty {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
  width: 40px;
  text-align: center;
}

.goods-subtotal {
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 600;
  color: #c44536;
  width: 90px;
  text-align: right;
}

/* ── 备注 ── */
:deep(.remark-section .el-textarea__inner) {
  font-family: var(--font-body);
  border-radius: 8px;
  border-color: var(--color-border);
}

/* ── 费用明细 ── */
.fee-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
  font-family: var(--font-body);
  font-size: 14px;
}

.fee-label {
  color: var(--color-text-muted);
}

.fee-value {
  color: var(--color-charcoal);
}

.fee-value.discount {
  color: #52c41a;
}

.fee-row.total {
  border-top: 1px solid var(--color-border);
  margin-top: 8px;
  padding-top: 16px;
}

.fee-row.total .fee-label {
  font-weight: 600;
  color: var(--color-charcoal);
  font-size: 15px;
}

.fee-row.total .fee-value {
  color: #c44536;
  font-weight: 700;
}

.fee-row.total .symbol {
  font-size: 14px;
}

.fee-row.total .amount {
  font-family: 'DM Sans', sans-serif;
  font-size: 24px;
  letter-spacing: -0.5px;
}

/* ── 提交栏 ── */
.submit-bar {
  position: sticky;
  bottom: 0;
  z-index: 50;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(232, 228, 223, 0.6);
  border-radius: 12px;
  box-shadow: 0 -2px 20px rgba(45, 41, 38, 0.06);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 16px 24px;
  gap: 24px;
  margin-top: 16px;
}

.submit-info {
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
}

.submit-price {
  font-family: 'DM Sans', sans-serif;
  font-size: 26px;
  font-weight: 700;
  color: #c44536;
  letter-spacing: -0.5px;
}

.submit-btn {
  height: 46px;
  min-width: 160px;
  font-family: var(--font-body);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  background: var(--color-charcoal);
  border: none;
  border-radius: 10px;
  transition: all 0.25s;
}

.submit-btn:hover:not(:disabled) {
  background: var(--color-charcoal-light, #3d3935);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(45, 41, 38, 0.2);
}

/* ── 地址弹窗 ── */
.address-list {
  max-height: 400px;
  overflow-y: auto;
}

.address-item {
  padding: 14px 16px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.address-item:hover {
  border-color: var(--color-amber);
}

.address-item.active {
  border-color: var(--color-amber);
  background: rgba(212, 165, 116, 0.06);
}

.address-item-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.empty-address {
  text-align: center;
  padding: 40px 0;
  color: var(--color-text-muted);
  font-family: var(--font-body);
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .goods-price { display: none; }
  .goods-qty { display: none; }
  .submit-price { font-size: 22px; }
}
</style>
