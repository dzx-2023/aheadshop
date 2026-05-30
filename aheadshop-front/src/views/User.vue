<template>
  <div class="user">
    <h2>个人中心</h2>
    <p v-if="userStore.userInfo">欢迎, {{ userStore.userInfo.nickname || userStore.userInfo.username }}</p>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
</script>
