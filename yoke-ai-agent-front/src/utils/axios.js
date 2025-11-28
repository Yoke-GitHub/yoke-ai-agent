import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8123/api',
  timeout: 30000
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

export default instance

