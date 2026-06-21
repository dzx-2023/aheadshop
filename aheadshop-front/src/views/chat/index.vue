<template>
  <div class="chat-page">
    <ChatWindow />
  </div>
</template>

<script setup lang="ts">
import ChatWindow from './ChatWindow.vue'
</script>

<style scoped>
.chat-page {
  height: 100dvh;
  overflow: hidden;
}
</style>
