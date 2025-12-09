<template>
  <div class="chat-container">
    <div class="chat-header">
      <button class="back-btn" @click="goHome">← 返回</button>
      <h2>AI 恋爱大师</h2>
      <div class="chat-id">会话ID: {{ chatId }}</div>
    </div>
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(msg, index) in messages" 
        :key="index"
        :class="['message', msg.type]"
      >
        <div class="message-content">
          <div :class="['message-avatar', msg.type]">
            <span v-if="msg.type === 'user'">👤</span>
            <div v-else class="ai-avatar-love">💕</div>
          </div>
          <div class="message-bubble">
            <div class="message-text" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
      </div>
      <div v-if="isLoading" class="message ai">
        <div class="message-content">
          <div class="message-avatar ai">
            <div class="ai-avatar-love">💕</div>
          </div>
          <div class="message-bubble">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-input-container">
      <div class="input-wrapper">
        <textarea
          v-model="inputMessage"
          @keydown.enter.exact.prevent="sendMessage"
          @keydown.shift.enter.exact="inputMessage += '\n'"
          placeholder="输入您的消息..."
          class="chat-input"
          rows="1"
          ref="inputRef"
        ></textarea>
        <button 
          @click="sendMessage" 
          :disabled="!inputMessage.trim() || isLoading"
          class="send-btn"
        >
          发送
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { chatWithLoveAppSse } from '../api/chat'

const router = useRouter()
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatId = ref('')
const messagesContainer = ref(null)
const inputRef = ref(null)
let eventSource = null
let isStreamCompleted = false // 标记流是否已完成，防止重复处理

// 生成聊天室ID
const generateChatId = () => {
  return 'love_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 格式化消息（支持换行）
const formatMessage = (text) => {
  return text.replace(/\n/g, '<br>')
}

// 获取当前时间
const getCurrentTime = () => {
  const now = new Date()
  return now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: message,
    time: getCurrentTime()
  })

  inputMessage.value = ''
  isLoading.value = true
  isStreamCompleted = false // 重置完成标记
  scrollToBottom()

  // 初始化AI消息
  let aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    content: '',
    time: getCurrentTime()
  })

  try {
    // 关闭之前的连接
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }

    // 创建SSE连接
    eventSource = chatWithLoveAppSse(message, chatId.value)

    // 监听连接打开
    eventSource.onopen = () => {
      console.log('SSE连接已建立')
    }

    eventSource.onmessage = (event) => {
      // 检查是否已经完成，防止重复处理
      if (isStreamCompleted) {
        return
      }

      // 检查是否收到结束标记（空数据或特定标记）
      if (!event.data || event.data.trim() === '' || event.data === '[DONE]') {
        // 任务完成，立即关闭连接
        isStreamCompleted = true
        isLoading.value = false
        
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
        
        console.log('SSE流式消息接收完成（收到结束标记）')
        return
      }

      if (messages.value[aiMessageIndex]) {
        messages.value[aiMessageIndex].content += event.data
      } else {
        messages.value.push({
          type: 'ai',
          content: event.data,
          time: getCurrentTime()
        })
      }
      scrollToBottom()
    }

    eventSource.onerror = (error) => {
      // 如果已经完成，直接返回，防止重复处理
      if (isStreamCompleted) {
        return
      }

      // 检查连接状态
      if (eventSource && eventSource.readyState === EventSource.CLOSED) {
        // 标记为已完成，防止自动重试
        isStreamCompleted = true
        
        // 连接已关闭（可能是正常结束或错误）
        const hasContent = messages.value[aiMessageIndex] && 
                          messages.value[aiMessageIndex].content.trim().length > 0
        
        if (hasContent) {
          // 有内容，说明是正常结束
          console.log('SSE流式消息接收完成')
        } else {
          // 无内容，可能是错误
          console.warn('SSE连接关闭，但没有接收到内容')
          if (messages.value[aiMessageIndex]) {
            messages.value[aiMessageIndex].content = '抱歉，未接收到响应，请稍后重试。'
          }
        }
        
        // 清理状态
        isLoading.value = false
        
        // 确保连接已关闭
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
      } else if (eventSource && eventSource.readyState === EventSource.CONNECTING) {
        // 如果正在重连，立即关闭，防止自动重试
        console.log('检测到SSE自动重连，立即关闭连接')
        isStreamCompleted = true
        isLoading.value = false
        
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
      }
    }

  } catch (error) {
    console.error('发送消息失败:', error)
    isStreamCompleted = true // 标记为已完成，防止重试
    isLoading.value = false
    if (messages.value[aiMessageIndex]) {
      messages.value[aiMessageIndex].content = '发送消息失败，请稍后重试。'
    }
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
  }
}

// 返回主页
const goHome = () => {
  isStreamCompleted = true // 标记为已完成，防止重试
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  isLoading.value = false
  router.push('/')
}

onMounted(() => {
  chatId.value = generateChatId()
  // 自动聚焦输入框
  if (inputRef.value) {
    inputRef.value.focus()
  }
})

onUnmounted(() => {
  isStreamCompleted = true // 标记为已完成，防止重试
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  isLoading.value = false
})
</script>

<style scoped>
.chat-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--gradient-love);
  position: relative;
  overflow: hidden;
}

.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(255, 154, 158, 0.2) 0%, transparent 50%),
              radial-gradient(circle at 80% 80%, rgba(250, 208, 196, 0.2) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

.chat-header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  color: white;
  padding: 18px 25px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-medium);
  position: relative;
  z-index: 10;
  border-bottom: 2px solid rgba(255, 255, 255, 0.3);
}

.back-btn {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 10px 18px;
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateX(-3px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.chat-header h2 {
  font-size: 22px;
  font-weight: 700;
  flex: 1;
  text-align: center;
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  letter-spacing: 1px;
}

.chat-id {
  font-size: 11px;
  opacity: 0.85;
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 10px;
  border-radius: var(--radius-full);
  backdrop-filter: blur(5px);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 25px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.message {
  display: flex;
  width: 100%;
  animation: fadeInMessage 0.3s ease-out;
}

@keyframes fadeInMessage {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  justify-content: flex-end;
}

.message.ai {
  justify-content: flex-start;
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 75%;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: var(--shadow-soft);
  border: 3px solid rgba(255, 255, 255, 0.8);
  transition: transform 0.3s;
}

.message-avatar:hover {
  transform: scale(1.1);
}

.message-avatar.user {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.message-avatar.ai {
  background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%);
}

.ai-avatar-love {
  font-size: 28px;
  filter: drop-shadow(0 2px 5px rgba(0, 0, 0, 0.1));
  animation: heartbeat 2s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.message-bubble {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-medium);
  padding: 14px 18px;
  box-shadow: var(--shadow-soft);
  position: relative;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.message.user .message-bubble {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
}

.message.ai .message-bubble {
  background: rgba(255, 255, 255, 0.95);
  color: #333;
}

.message-text {
  word-wrap: break-word;
  line-height: 1.7;
  font-size: 15px;
  font-weight: 400;
}

.message.user .message-text {
  color: white;
}

.message-time {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 8px;
  font-weight: 300;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  padding: 5px 0;
  align-items: center;
}

.typing-indicator span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  animation: typing 1.4s infinite;
  box-shadow: 0 2px 5px rgba(255, 154, 158, 0.3);
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.7;
  }
  30% {
    transform: translateY(-12px) scale(1.1);
    opacity: 1;
  }
}

.chat-input-container {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 18px 25px;
  border-top: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 10;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  max-width: 1200px;
  margin: 0 auto;
}

.chat-input {
  flex: 1;
  border: 2px solid rgba(255, 154, 158, 0.3);
  border-radius: var(--radius-large);
  padding: 14px 18px;
  font-size: 15px;
  resize: none;
  max-height: 120px;
  font-family: inherit;
  outline: none;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 2px 5px rgba(0, 0, 0, 0.05);
}

.chat-input:focus {
  border-color: #ff9a9e;
  background: white;
  box-shadow: 0 0 0 4px rgba(255, 154, 158, 0.1), inset 0 2px 5px rgba(0, 0, 0, 0.05);
}

.chat-input::placeholder {
  color: #999;
}

.send-btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: white;
  border: none;
  border-radius: var(--radius-large);
  padding: 14px 35px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  white-space: nowrap;
  box-shadow: var(--shadow-soft);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-medium);
}

.send-btn:active:not(:disabled) {
  transform: translateY(0);
}

.send-btn:disabled {
  background: #ddd;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
  opacity: 0.6;
}

/* 平板适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .message-content {
    max-width: 80%;
  }
  
  .chat-header {
    padding: 16px 22px;
  }
  
  .chat-header h2 {
    font-size: 20px;
  }
  
  .chat-messages {
    padding: 22px 18px;
  }
}

/* 手机适配 */
@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }
  
  .chat-header {
    padding: 14px 18px;
  }
  
  .chat-header h2 {
    font-size: 18px;
  }
  
  .chat-id {
    display: none;
  }
  
  .chat-messages {
    padding: 20px 15px;
    gap: 16px;
  }
  
  .message-avatar {
    width: 42px;
    height: 42px;
    font-size: 20px;
  }
  
  .ai-avatar-love {
    font-size: 24px;
  }
  
  .message-bubble {
    padding: 12px 16px;
  }
  
  .message-text {
    font-size: 14px;
  }
  
  .chat-input-container {
    padding: 15px 18px;
  }
  
  .chat-input {
    padding: 12px 16px;
    font-size: 14px;
  }
  
  .send-btn {
    padding: 12px 25px;
    font-size: 14px;
  }
}

/* 小屏手机 */
@media (max-width: 480px) {
  .chat-header {
    padding: 12px 15px;
  }
  
  .chat-header h2 {
    font-size: 16px;
  }
  
  .back-btn {
    padding: 8px 14px;
    font-size: 12px;
  }
  
  .message-content {
    max-width: 90%;
  }
  
  .message-avatar {
    width: 38px;
    height: 38px;
    font-size: 18px;
  }
  
  .ai-avatar-love {
    font-size: 22px;
  }
  
  .message-bubble {
    padding: 10px 14px;
    border-radius: var(--radius-small);
  }
  
  .message-text {
    font-size: 13px;
    line-height: 1.6;
  }
  
  .chat-messages {
    padding: 15px 12px;
    gap: 14px;
  }
  
  .chat-input-container {
    padding: 12px 15px;
  }
  
  .input-wrapper {
    gap: 10px;
  }
  
  .chat-input {
    padding: 10px 14px;
    font-size: 13px;
  }
  
  .send-btn {
    padding: 10px 20px;
    font-size: 13px;
  }
}
</style>

