<template>
  <div class="product-card" @click="goDetail">
    <div class="card-image">
      <img :src="product.mainImage || placeholderImg" :alt="product.name" loading="lazy" />
      <div v-if="product.brandName" class="brand-tag">{{ product.brandName }}</div>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ product.name }}</h3>
      <p v-if="product.subtitle" class="card-subtitle">{{ product.subtitle }}</p>
      <div class="card-footer">
        <div class="price">
          <span class="price-symbol">¥</span>
          <span class="price-value">{{ formatPrice(product.minPrice) }}</span>
        </div>
        <div class="sales">已售 {{ formatSales(product.sales) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { SpuPageItem } from '@/types/product'

const props = defineProps<{
  product: SpuPageItem
}>()

const router = useRouter()
const placeholderImg = 'https://placehold.co/400x400/f5f3ef/d0ccc7?text=No+Image'

function goDetail() {
  router.push(`/product/${props.product.id}`)
}

function formatPrice(price: number): string {
  return Number(price).toFixed(2)
}

function formatSales(sales: number): string {
  if (sales >= 10000) return (sales / 10000).toFixed(1) + 'w'
  if (sales >= 1000) return (sales / 1000).toFixed(1) + 'k'
  return String(sales)
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  border: 1px solid rgba(232, 228, 223, 0.6);
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 40px rgba(45, 41, 38, 0.1);
  border-color: transparent;
}

.card-image {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f5f3ef;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.product-card:hover .card-image img {
  transform: scale(1.05);
}

.brand-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  font-family: var(--font-body);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
  color: #fdfbf7;
  background: rgba(45, 41, 38, 0.75);
  backdrop-filter: blur(4px);
  padding: 3px 10px;
  border-radius: 4px;
}

.card-body {
  padding: 14px 16px 16px;
}

.card-title {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-charcoal);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 4px;
}

.card-subtitle {
  font-family: var(--font-body);
  font-size: 12px;
  color: var(--color-text-muted);
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 10px;
}

.card-footer {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}

.price {
  color: #c44536;
  font-weight: 600;
}

.price-symbol {
  font-size: 12px;
}

.price-value {
  font-family: 'DM Sans', sans-serif;
  font-size: 20px;
  letter-spacing: -0.5px;
}

.sales {
  font-family: var(--font-body);
  font-size: 11px;
  color: var(--color-text-muted);
  letter-spacing: 0.3px;
}
</style>
