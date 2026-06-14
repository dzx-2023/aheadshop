import { createPinia } from 'pinia'
import { useUserStore } from './modules/user'
import { useCartStore } from './modules/cart'

const pinia = createPinia()

export { useUserStore, useCartStore }
export default pinia
