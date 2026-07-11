// axios 实例：统一配置 baseURL 与请求拦截
// baseURL 设为 /api，开发环境由 vite proxy 转发到后端 8080 端口
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

export default request
