/**
 * SSE工具函数
 * @param {string} url - SSE接口地址
 * @param {Object} params - 请求参数
 * @param {Function} onMessage - 消息回调函数
 * @param {Function} onError - 错误回调函数
 * @param {Function} onComplete - 完成回调函数（可选）
 * @returns {Object} 包含close函数和eventSource的对象
 */
export function createSSEConnection(url, params, onMessage, onError, onComplete) {
  // 构建查询参数
  const queryString = new URLSearchParams(params).toString()
  const fullUrl = `${url}?${queryString}`

  console.log('创建SSE连接:', fullUrl)

  const eventSource = new EventSource(fullUrl)
  let isCompleted = false

  eventSource.onopen = () => {
    console.log('SSE连接已打开')
  }

  eventSource.onmessage = (event) => {
    console.log('收到SSE消息:', event.data)
    if (onMessage) {
      onMessage(event.data)
    }
  }

  eventSource.onerror = (error) => {
    const readyState = eventSource.readyState
    console.log('SSE onerror触发, readyState:', readyState, 'EventSource状态:', {
      0: 'CONNECTING',
      1: 'OPEN',
      2: 'CLOSED'
    }[readyState])
    
    // EventSource的readyState:
    // 0 = CONNECTING (连接中或重连中)
    // 1 = OPEN (已连接)
    // 2 = CLOSED (已关闭)
    
    if (readyState === EventSource.CLOSED) {
      // 连接已关闭 - 可能是正常关闭，也可能是错误关闭
      if (!isCompleted) {
        isCompleted = true
        console.log('SSE连接已关闭')
        // 延迟一点判断是正常关闭还是错误关闭
        setTimeout(() => {
          if (onComplete) {
            onComplete()
          }
        }, 100)
      }
    } else if (readyState === EventSource.CONNECTING) {
      // 连接失败或正在重连
      // 注意：EventSource会自动重连，所以这里可能是重连中
      // 只有在持续失败时才触发错误
      console.warn('SSE连接失败或正在重连, readyState:', readyState)
      // 不立即触发错误，等待重连结果
    } else {
      // 其他错误状态
      console.error('SSE连接错误:', error, 'readyState:', readyState)
      if (!isCompleted && onError) {
        isCompleted = true
        onError(new Error(`SSE连接错误，状态码: ${readyState}`))
      }
    }
  }

  // 返回关闭函数和eventSource对象
  return {
    close: () => {
      console.log('手动关闭SSE连接')
      isCompleted = true
      eventSource.close()
    },
    eventSource: eventSource
  }
}

