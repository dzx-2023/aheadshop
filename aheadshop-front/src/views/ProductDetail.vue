<template>
  <div class="product-detail">
    <h2>商品详情</h2>
    <p>商品 ID: {{ route.params.id }}</p>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>
