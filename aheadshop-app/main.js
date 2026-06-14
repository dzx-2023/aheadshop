import App from './App'
import pinia from './store'

// #ifdef VUE3
import { createSSRApp } from 'vue'

export function createApp() {
	const app = createSSRApp(App)
	app.use(pinia)
	return {
		app,
		Pinia // 此处必须将 Pinia 返回
	}
}
// #endif
