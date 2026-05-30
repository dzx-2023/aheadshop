import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useCartStore = defineStore('cart', () => {
  const count = ref(0)

  /** 获取购物车数量 */
  async function fetchCount() {
    try {
      const res: any = await request.get('/cart/count')
      count.value = res.data ?? 0
    } catch {
      count.value = 0
    }
  }

  /** 设置数量（本地） */
  function setCount(val: number) {
    count.value = val
  }

  /** 重置 */
  function reset() {
    count.value = 0
  }

  return { count, fetchCount, setCount, reset }
})
