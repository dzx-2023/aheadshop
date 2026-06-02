<template>
  <div class="home">
    <!-- Hero: 左侧分类 + 右侧轮播 -->
    <section class="hero-section">
      <div class="hero-inner">
        <!-- 左侧分类列表 -->
        <aside class="hero-category">
          <div class="category-list-title">全部分类</div>
          <nav class="category-list">
            <router-link
              v-for="cat in categories"
              :key="cat.id"
              :to="`/category?id=${cat.id}`"
              class="category-row"
            >
              <div class="category-icon" v-html="getCategoryIcon(cat.name)"></div>
              <span class="category-name">{{ cat.name }}</span>
              <span class="category-arrow">›</span>
            </router-link>
          </nav>
          <div v-if="!categories.length && !loading" class="category-empty">暂无分类</div>
        </aside>

        <!-- 右侧轮播图 -->
        <div class="hero-banner">
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
        </div>
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

/** 分类名称 → SVG 图标映射（手绘线条风格） */
function getCategoryIcon(name: string): string {
  const icons: Record<string, string> = {
    '手机数码': `<svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="14" y="4" width="20" height="40" rx="3" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
      <line x1="14" y1="10" x2="34" y2="10" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round"/>
      <line x1="14" y1="36" x2="34" y2="36" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round"/>
      <circle cx="24" cy="40" r="1.5" fill="#d4a574"/>
      <path d="M20 7h8" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
    </svg>`,
    '电脑办公': `<svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="6" y="8" width="36" height="24" rx="2.5" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
      <line x1="6" y1="28" x2="42" y2="28" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round"/>
      <path d="M18 32h12" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round"/>
      <path d="M20 36h8" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round"/>
      <path d="M12 14l4 4-4 4" stroke="#d4a574" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <line x1="20" y1="22" x2="30" y2="22" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
    </svg>`,
    '家用电器': `<svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect x="10" y="6" width="28" height="36" rx="3" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
      <circle cx="24" cy="28" r="8" stroke="#2d2926" stroke-width="2.2"/>
      <circle cx="24" cy="28" r="3" fill="#d4a574"/>
      <line x1="10" y1="14" x2="38" y2="14" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round"/>
      <line x1="16" y1="10" x2="16" y2="14" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
      <line x1="24" y1="10" x2="24" y2="14" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
      <line x1="32" y1="10" x2="32" y2="14" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
    </svg>`,
    '服饰鞋包': `<svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M16 6L8 14v4l6 2v22h20V20l6-2v-4L32 6" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
      <path d="M16 6c0 4 3.5 7 8 7s8-3 8-7" stroke="#2d2926" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>
      <line x1="24" y1="13" x2="24" y2="26" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
      <line x1="18" y1="20" x2="30" y2="20" stroke="#d4a574" stroke-width="2" stroke-linecap="round"/>
    </svg>`,
  }
  // 精确匹配
  if (icons[name]) return icons[name]
  // 模糊匹配
  if (name.includes('手机') || name.includes('数码')) return icons['手机数码']
  if (name.includes('电脑') || name.includes('办公')) return icons['电脑办公']
  if (name.includes('家电') || name.includes('电器')) return icons['家用电器']
  if (name.includes('服饰') || name.includes('鞋') || name.includes('包')) return icons['服饰鞋包']
  // 默认图标
  return `<svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
    <rect x="8" y="8" width="32" height="32" rx="4" stroke="#2d2926" stroke-width="2.2"/>
    <path d="M16 24h16M24 16v16" stroke="#d4a574" stroke-width="2.2" stroke-linecap="round"/>
  </svg>`
}

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
      getSpuList({ pageNum: 1, pageSize: 12, status: 1 }),
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

/* ── Hero: 分类 + 轮播 ── */
.hero-section {
  padding: 24px 0 40px;
}

.hero-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 16px;
  height: 420px;
}

/* 左侧分类栏 */
.hero-category {
  width: 220px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(232, 228, 223, 0.5);
  border-radius: 14px;
  padding: 16px 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.category-list-title {
  font-family: var(--font-body);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 2px;
  color: var(--color-text-muted);
  text-transform: uppercase;
  padding: 0 20px 12px;
  border-bottom: 1px solid rgba(232, 228, 223, 0.5);
  margin-bottom: 8px;
}

.category-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 0 8px;
}

.category-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  text-decoration: none;
  transition: all 0.25s;
  cursor: pointer;
}

.category-row:hover {
  background: rgba(212, 165, 116, 0.1);
}

.category-row .category-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.12) 0%, rgba(212, 165, 116, 0.04) 100%);
  border-radius: 10px;
  flex-shrink: 0;
  transition: all 0.3s;
}

.category-row .category-icon :deep(svg) {
  width: 22px;
  height: 22px;
}

.category-row:hover .category-icon {
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.25) 0%, rgba(212, 165, 116, 0.1) 100%);
  transform: scale(1.06);
}

.category-row .category-name {
  flex: 1;
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-row .category-arrow {
  font-size: 16px;
  color: var(--color-text-muted);
  opacity: 0;
  transform: translateX(-4px);
  transition: all 0.25s;
}

.category-row:hover .category-arrow {
  opacity: 1;
  transform: translateX(0);
  color: var(--color-amber);
}

.category-empty {
  text-align: center;
  padding: 40px 16px;
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--color-text-muted);
}

/* 右侧轮播 */
.hero-banner {
  flex: 1;
  min-width: 0;
  border-radius: 14px;
  overflow: hidden;
}

:deep(.el-carousel) {
  border-radius: 14px;
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
  background: rgba(255, 255, 255, 0.4);
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
  .hero-inner {
    flex-direction: column;
    height: auto;
  }

  .hero-category {
    width: 100%;
    max-height: 200px;
    border-radius: 12px;
    padding: 12px 0;
  }

  .category-list-title {
    padding: 0 16px 10px;
  }

  .category-list {
    padding: 0 8px;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 4px;
  }

  .category-row {
    padding: 8px 12px;
    flex: 0 0 auto;
  }

  .category-row .category-arrow {
    display: none;
  }

  .hero-banner {
    border-radius: 12px;
  }

  .hero-banner :deep(.el-carousel) {
    height: 200px !important;
  }

  .banner-title { font-size: 22px; letter-spacing: 1px; }
  .banner-desc { font-size: 13px; }
  .product-grid { grid-template-columns: repeat(2, 1fr); gap: 12px; }
}
</style>
