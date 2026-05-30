<template>
  <div class="order-detail">
    <h2>订单详情</h2>
    <p>订单号: {{ route.params.orderNo }}</p>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>
