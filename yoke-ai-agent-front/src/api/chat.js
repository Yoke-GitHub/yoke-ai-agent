/**
 * AI聊天相关API
 */

// 根据环境变量设置 API 基础 URL
const API_BASE_URL = import.meta.env.PROD
  ? '/api' // 生产环境使用相对路径，适用于前后端部署在同一域名下
  : 'http://localhost:8123/api' // 开发环境指向本地后端服务

/**
 * AI恋爱大师SSE聊天
 * @param {string} message - 消息内容
 * @param {string} chatId - 聊天室ID
 * @returns {EventSource} EventSource对象
 */
export function chatWithLoveAppSse(message, chatId) {
  const params = new URLSearchParams({
    message,
    chatId
  })
  const url = `${API_BASE_URL}/ai/love_app/chat/sse?${params.toString()}`
  return new EventSource(url)
}

/**
 * AI超级智能体聊天（SSE）
 * @param {string} message - 消息内容
 * @returns {EventSource} EventSource对象
 */
export function chatWithManus(message) {
  const params = new URLSearchParams({
    message
  })
  const url = `${API_BASE_URL}/ai/manus/chat?${params.toString()}`
  return new EventSource(url)
}

