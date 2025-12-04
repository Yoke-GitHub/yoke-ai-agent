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

    // 这里统一把 onerror 当作一次「失败结束」：
    // - 防止浏览器默认的无限自动重连把 Network 面板刷满
    // - 把控制权交还给上层（比如 LoveApp.vue 里会走 fetch 兜底逻辑）
    if (!isCompleted) {
      isCompleted = true
      console.error('SSE连接错误:', error, 'readyState:', readyState)

      // 主动关闭当前连接，阻止继续自动重连
      try {
        eventSource.close()
      } catch (e) {
        console.warn('关闭EventSource时出错:', e)
      }

      if (onError) {
        // 尽量构造一个可读性更好的错误对象
        let err
        if (error instanceof Error) {
          err = error
        } else {
          // 这里无法拿到具体的 HTTP 状态码，只能给出通用描述
          err = new Error('SSE连接失败，请检查后端 8123 端口或 /api/ai/love_app/chat/sse 是否已启动')
        }
        onError(err)
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

