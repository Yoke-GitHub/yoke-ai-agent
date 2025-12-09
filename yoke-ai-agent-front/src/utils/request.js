import axios from 'axios'

// 根据环境变量设置 API 基础 URL
const API_BASE_URL = import.meta.env.PROD
  ? '/api' // 生产环境使用相对路径，适用于前后端部署在同一域名下
  : 'http://localhost:8123/api' // 开发环境指向本地后端服务

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default request

