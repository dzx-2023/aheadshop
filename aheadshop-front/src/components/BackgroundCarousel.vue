<template>
  <div class="bg-carousel">
    <!-- 双层图片用于交叉渐变 -->
    <div
      class="bg-layer"
      :class="{ active: activeLayer === 0 }"
      :style="layerStyle(0)"
    />
    <div
      class="bg-layer"
      :class="{ active: activeLayer === 1 }"
      :style="layerStyle(1)"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getActiveBackgrounds } from '@/api/product'

const images = ref<string[]>([])
const activeLayer = ref(0)
const currentIndex = ref(0)
const layerImages = ref(['', ''])


let timer: ReturnType<typeof setInterval> | null = null

// 从 localStorage 读取轮播间隔，默认 30 秒
const INTERVAL = () => (Number(localStorage.getItem('bg_interval') || '30')) * 1000

// 从 localStorage 读取透明度，默认 0.15（很淡的氛围感）
const opacity = ref(Number(localStorage.getItem('bg_opacity') || '0.15'))

function layerStyle(layer: number) {
  return {
    backgroundImage: layerImages.value[layer] ? `url(${layerImages.value[layer]})` : 'none',
    opacity: layer === activeLayer.value ? opacity.value : 0,
  }
}

async function fetchImages() {
  try {
    const res: any = await getActiveBackgrounds()
    const list = res?.data || []
    images.value = list.map((img: any) => img.imageUrl)
    console.log('[BackgroundCarousel] 加载到', images.value.length, '张背景图')
    if (images.value.length > 0) {
      layerImages.value[0] = images.value[0]
      currentIndex.value = 0
      startCarousel()
    }
  } catch (e) {
    console.error('[BackgroundCarousel] 加载失败:', e)
  }
}

function startCarousel() {
  stopCarousel()
  if (images.value.length <= 1) return
  timer = setInterval(() => nextSlide(), INTERVAL())
}

// 间隔变化时重启轮播
function handleIntervalChange() {
  if (images.value.length > 1) startCarousel()
}

function stopCarousel() {
  if (timer) { clearInterval(timer); timer = null }
}

function nextSlide() {
  if (images.value.length <= 1) return

  const nextIndex = (currentIndex.value + 1) % images.value.length
  const inactiveLayer = activeLayer.value === 0 ? 1 : 0

  layerImages.value[inactiveLayer] = images.value[nextIndex]

  const img = new Image()
  img.onload = () => {
    activeLayer.value = inactiveLayer
    currentIndex.value = nextIndex
  }
  img.onerror = () => { currentIndex.value = nextIndex }
  img.src = images.value[nextIndex]
}

function handleOpacityChange(e: Event) {
  const detail = (e as CustomEvent).detail
  if (detail?.opacity !== undefined) opacity.value = detail.opacity
}

function handleStorage(e: StorageEvent) {
  if (e.key === 'bg_opacity' && e.newValue) opacity.value = Number(e.newValue)
}

onMounted(() => {
  fetchImages()
  window.addEventListener('storage', handleStorage)
  window.addEventListener('bg-opacity-change', handleOpacityChange as EventListener)
  window.addEventListener('bg-interval-change', handleIntervalChange as EventListener)
})

onUnmounted(() => {
  stopCarousel()
  window.removeEventListener('storage', handleStorage)
  window.removeEventListener('bg-opacity-change', handleOpacityChange as EventListener)
  window.removeEventListener('bg-interval-change', handleIntervalChange as EventListener)
})
</script>

<style scoped>
.bg-carousel {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  /* 最顶层，覆盖一切 */
  z-index: 99999;
  /* 不拦截任何鼠标事件 */
  pointer-events: none;
  overflow: hidden;
}

.bg-layer {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 2s ease-in-out;
  will-change: opacity;
}

.bg-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
</style>
