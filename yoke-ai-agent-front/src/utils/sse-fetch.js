/**
 * 使用fetch API处理SSE连接（更可靠的方式）
 * @param {string} url - SSE接口地址
 * @param {Object} params - 请求参数
 * @param {Function} onMessage - 消息回调函数
 * @param {Function} onError - 错误回调函数
 * @param {Function} onComplete - 完成回调函数（可选）
 * @returns {Object} 包含close函数和abortController的对象
 */
export function createSSEConnectionWithFetch(url, params, onMessage, onError, onComplete) {
  // 构建查询参数
  const queryString = new URLSearchParams(params).toString()
  const fullUrl = `${url}?${queryString}`

  console.log('创建SSE连接 (fetch):', fullUrl)

  const abortController = new AbortController()
  let isCompleted = false

  fetch(fullUrl, {
    method: 'GET',
    headers: {
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache'
    },
    signal: abortController.signal
  })
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }

      if (!response.body) {
        throw new Error('ReadableStream not supported')
      }

      console.log('SSE连接已建立，开始读取流')
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let buffer = ''

      const readStream = () => {
        reader.read().then(({ done, value }) => {
          if (done) {
            console.log('SSE流读取完成')
            isCompleted = true
            if (onComplete) {
              onComplete()
            }
            return
          }

          // 解码数据
          buffer += decoder.decode(value, { stream: true })
          
          // 处理SSE格式的数据
          const lines = buffer.split('\n')
          buffer = lines.pop() || '' // 保留最后一个不完整的行

          for (const line of lines) {
            if (line.startsWith('data: ')) {
              const data = line.slice(6) // 移除 'data: ' 前缀
              if (data.trim()) {
                console.log('收到SSE消息:', data)
                if (onMessage) {
                  onMessage(data)
                }
              }
            } else if (line.startsWith('event: ')) {
              // 处理事件类型
              const eventType = line.slice(7)
              console.log('SSE事件类型:', eventType)
            } else if (line === '') {
              // 空行表示消息结束
              continue
            }
          }

          // 继续读取
          readStream()
        }).catch(error => {
          if (error.name === 'AbortError') {
            console.log('SSE连接被手动取消')
            return
          }
          console.error('读取SSE流错误:', error)
          if (!isCompleted) {
            isCompleted = true
            if (onError) {
              onError(error)
            }
          }
        })
      }

      readStream()
    })
    .catch(error => {
      if (error.name === 'AbortError') {
        console.log('SSE连接被手动取消')
        return
      }
      console.error('SSE连接错误:', error)
      if (!isCompleted) {
        isCompleted = true
        if (onError) {
          onError(error)
        }
      }
    })

  // 返回关闭函数和abortController
  return {
    close: () => {
      console.log('手动关闭SSE连接')
      isCompleted = true
      abortController.abort()
    },
    abortController: abortController
  }
}

