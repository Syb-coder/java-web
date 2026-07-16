// axios 实例：统一配置 baseURL、请求拦截器（自动携带 Token）、响应拦截器（统一错误处理）
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器：自动在请求头添加 Authorization Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一处理 401/403 错误
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      // 401 未登录：清除 Token 并跳转登录页
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        // 避免在登录页重复跳转
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
      // 统一抛出后端返回的错误信息
      return Promise.reject(new Error(data?.message || `请求失败(${status})`))
    }
    return Promise.reject(new Error('网络异常，请稍后重试'))
  },
)

export default request
