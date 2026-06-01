<template>
  <div class="home">
    <!-- 轮播图 -->
    <section class="hero-banner">
      <el-carousel height="420px" :interval="5000" arrow="hover" indicator-position="outside">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <div class="banner-slide" :style="{ background: banner.bg }">
            <div class="banner-content">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-desc">{{ banner.desc }}</p>
              <router-link to="/category" class="banner-btn">立即探索</router-link>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 分类导航 -->
    <section class="category-nav">
      <div class="section-inner">
        <h3 class="section-title">商品分类</h3>
        <div class="category-grid">
          <router-link
            v-for="cat in categories"
            :key="cat.id"
            :to="`/category?id=${cat.id}`"
            class="category-item"
          >
            <div class="category-icon">{{ cat.icon || '📦' }}</div>
            <span class="category-name">{{ cat.name }}</span>
          </router-link>
        </div>
        <div v-if="!categories.length && !loading" class="empty-hint">暂无分类数据</div>
      </div>
    </section>

    <!-- 推荐商品 -->
    <section class="recommend">
      <div class="section-inner">
        <div class="section-header">
          <h3 class="section-title">为你推荐</h3>
          <router-link to="/category" class="view-all">查看全部 →</router-link>
        </div>
        <div v-if="products.length" class="product-grid">
          <ProductCard v-for="item in products" :key="item.id" :product="item" />
        </div>
        <div v-else-if="loading" class="product-grid">
          <div v-for="i in 8" :key="i" class="skeleton-card">
            <div class="skeleton-img"></div>
            <div class="skeleton-text"></div>
            <div class="skeleton-text short"></div>
          </div>
        </div>
        <div v-else class="empty-state">
          <p>暂无商品数据</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSpuList, getCategoryTree } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import type { SpuPageItem, CategoryTree } from '@/types/product'

const loading = ref(true)
const products = ref<SpuPageItem[]>([])
const categories = ref<CategoryTree[]>([])

// 静态轮播图数据
const banners = [
  {
    id: 1,
    title: '新品首发 · 品质之选',
    desc: '精选全球好物，为你的生活增添色彩',
    bg: 'linear-gradient(135deg, #2d2926 0%, #4a4540 100%)',
  },
  {
    id: 2,
    title: '限时特惠 · 低至 5 折',
    desc: '海量爆款商品，限时抢购中',
    bg: 'linear-gradient(135deg, #8b6914 0%, #d4a574 100%)',
  },
  {
    id: 3,
    title: '会员专享 · 每周上新',
    desc: '注册会员即享专属优惠',
    bg: 'linear-gradient(135deg, #3a506b 0%, #5bc0be 100%)',
  },
]

onMounted(async () => {
  try {
    const [catRes, spuRes] = await Promise.allSettled([
      getCategoryTree(),
      getSpuList({ pageNum: 1, pageSize: 12 }),
    ])
    if (catRes.status === 'fulfilled') {
      categories.value = (catRes.value as any).data || []
    }
    if (spuRes.status === 'fulfilled') {
      const pageData = (spuRes.value as any).data
      products.value = pageData?.records || pageData?.records || []
    }
  } catch {
    // 接口未就绪时静默处理
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.home {
  background: var(--color-cream);
}

/* ── 轮播图 ── */
.hero-banner {
  margin-bottom: 40px;
}

:deep(.el-carousel__arrow) {
  background: rgba(45, 41, 38, 0.5);
  border: none;
  width: 40px;
  height: 40px;
}

:deep(.el-carousel__arrow:hover) {
  background: rgba(45, 41, 38, 0.8);
}

:deep(.el-carousel__indicator .el-carousel__button) {
  width: 24px;
  height: 3px;
  border-radius: 2px;
  background: #d0ccc7;
}

:deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background: var(--color-amber);
  width: 32px;
}

.banner-slide {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0;
}

.banner-content {
  text-align: center;
  color: #fdfbf7;
  animation: fadeInUp 0.8s ease-out;
}

.banner-title {
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 600;
  letter-spacing: 3px;
  margin-bottom: 12px;
}

.banner-desc {
  font-family: var(--font-body);
  font-size: 15px;
  opacity: 0.85;
  letter-spacing: 1px;
  margin-bottom: 28px;
}

.banner-btn {
  display: inline-block;
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 2px;
  color: var(--color-charcoal);
  background: #fdfbf7;
  padding: 10px 32px;
  border-radius: 6px;
  transition: all 0.3s;
}

.banner-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ── 公共区块 ── */
.section-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--color-charcoal);
  margin-bottom: 24px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.view-all {
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
  transition: color 0.2s;
}

.view-all:hover {
  color: var(--color-amber);
}

/* ── 分类导航 ── */
.category-nav {
  padding: 40px 0;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(232, 228, 223, 0.6);
  transition: all 0.3s;
}

.category-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(45, 41, 38, 0.08);
  border-color: var(--color-amber);
}

.category-icon {
  font-size: 28px;
  line-height: 1;
}

.category-name {
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-charcoal);
  text-align: center;
}

/* ── 推荐商品 ── */
.recommend {
  padding: 40px 0 60px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

/* 骨架屏 */
.skeleton-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  padding-bottom: 16px;
}

.skeleton-img {
  aspect-ratio: 1;
  background: linear-gradient(90deg, #f5f3ef 25%, #efecea 50%, #f5f3ef 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-text {
  height: 14px;
  margin: 14px 16px 0;
  background: #f5f3ef;
  border-radius: 4px;
}

.skeleton-text.short {
  width: 60%;
  height: 12px;
  margin-top: 8px;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.empty-state,
.empty-hint {
  text-align: center;
  padding: 60px 0;
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--color-text-muted);
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .banner-title { font-size: 24px; }
  .banner-desc { font-size: 13px; }
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
  .category-grid { grid-template-columns: repeat(4, 1fr); gap: 10px; }
  .category-item { padding: 14px 8px; }
  .category-icon { font-size: 24px; }
}
</style>
