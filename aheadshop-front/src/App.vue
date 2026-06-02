<template>
  <!-- 全局背景轮播（根级别，不受 layout 层叠上下文影响） -->
  <BackgroundCarousel />
  <router-view />
</template>

<script setup lang="ts">
import BackgroundCarousel from '@/components/BackgroundCarousel.vue'
</script>
