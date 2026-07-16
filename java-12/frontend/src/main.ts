import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'

// 创建 Vue 应用实例
const app = createApp(App)

// 使用 Pinia 状态管理与路由
app.use(createPinia())
app.use(router)

// 挂载应用
app.mount('#app')
