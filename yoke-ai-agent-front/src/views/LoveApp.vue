<template>
  <section class="chat-view">
    <header class="chat-view__header">
      <div class="chat-view__title">
        <button class="back-btn" @click="goBack">← 返回</button>
        <div>
          <p class="chat-view__label">恋爱场景智能体</p>
          <h2>AI 恋爱大师</h2>
        </div>
      </div>
      <div class="chat-view__meta">
        <div class="ai-profile">
          <div class="ai-profile__avatar">💗</div>
          <div>
            <p class="ai-profile__name">林深 · 情感陪伴</p>
            <small>会话 ID：{{ chatId }}</small>
          </div>
        </div>
      </div>
    </header>

    <div class="chat-panel">
      <div class="chat-messages" ref="messagesContainer">
        <div
          v-for="(message, index) in messages"
          :key="index"
          :class="['message-row', message.type]"
        >
          <div
            class="avatar"
            :class="message.type === 'ai' ? 'avatar--ai-love' : 'avatar--user'"
          >
            <span>{{ message.type === 'ai' ? 'AI' : '我' }}</span>
          </div>
          <div class="message-bubble">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>

        <div v-if="isLoading" class="message-row ai typing-row">
          <div class="avatar avatar--ai-love">
            <span>AI</span>
          </div>
          <div class="message-bubble">
            <div class="message-text typing">AI 正在思考中</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <textarea
          v-model="inputMessage"
          @keyup.enter.exact.prevent="sendMessage"
          placeholder="输入您的情感问题，Enter 发送"
          rows="2"
          :disabled="isLoading"
        />
        <button
          class="send-btn"
          @click="sendMessage"
          :disabled="!inputMessage.trim() || isLoading"
        >
          发送
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { createSSEConnection } from '../utils/sse'
import { createSSEConnectionWithFetch } from '../utils/sse-fetch'

const router = useRouter()
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
const chatId = ref('')
let sseConnection = null

// 生成聊天室ID
const generateChatId = () => {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substring(2, 11)
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
const sendMessage = () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage = {
    type: 'user',
    content: inputMessage.value,
    time: getCurrentTime()
  }
  messages.value.push(userMessage)
  const messageToSend = inputMessage.value
  inputMessage.value = ''
  scrollToBottom()

  // 创建SSE连接
  isLoading.value = true
  let aiMessageContent = ''
  let aiMessageIndex = -1

  // 如果已存在连接，先关闭
  if (sseConnection) {
    sseConnection.close()
  }

  // 先尝试使用EventSource，如果失败则使用fetch
  try {
    sseConnection = createSSEConnection(
      'http://localhost:8123/api/ai/love_app/chat/sse',
      {
        message: messageToSend,
        chatId: chatId.value
      },
      (data) => {
        if (aiMessageIndex === -1) {
          aiMessageContent = data
          aiMessageIndex = messages.value.length
          messages.value.push({
            type: 'ai',
            content: aiMessageContent,
            time: getCurrentTime()
          })
        } else {
          aiMessageContent += data
          messages.value[aiMessageIndex].content = aiMessageContent
        }
        scrollToBottom()
      },
      (error) => {
        console.error('EventSource SSE错误，尝试使用fetch:', error)
        // EventSource失败，尝试使用fetch
        if (sseConnection) {
          sseConnection.close()
        }
        // 使用fetch方式重试
        sseConnection = createSSEConnectionWithFetch(
          'http://localhost:8123/api/ai/love_app/chat/sse',
          {
            message: messageToSend,
            chatId: chatId.value
          },
          (data) => {
            if (aiMessageIndex === -1) {
              aiMessageContent = data
              aiMessageIndex = messages.value.length
              messages.value.push({
                type: 'ai',
                content: aiMessageContent,
                time: getCurrentTime()
              })
            } else {
              aiMessageContent += data
              messages.value[aiMessageIndex].content = aiMessageContent
            }
            scrollToBottom()
          },
          (error) => {
            console.error('Fetch SSE错误:', error)
            isLoading.value = false
            messages.value.push({
              type: 'ai',
              content: `连接错误: ${error.message || '请检查后端服务是否正常运行'}`,
              time: getCurrentTime()
            })
            scrollToBottom()
            if (sseConnection) {
              sseConnection.close()
              sseConnection = null
            }
          },
          () => {
            isLoading.value = false
            if (sseConnection) {
              sseConnection.close()
              sseConnection = null
            }
          }
        )
      },
      () => {
        // 连接正常完成
        isLoading.value = false
        if (sseConnection) {
          sseConnection.close()
          sseConnection = null
        }
      }
    )
  } catch (error) {
    console.error('创建SSE连接异常:', error)
    // 直接使用fetch方式
    sseConnection = createSSEConnectionWithFetch(
      'http://localhost:8123/api/ai/love_app/chat/sse',
      {
        message: messageToSend,
        chatId: chatId.value
      },
      (data) => {
        if (aiMessageIndex === -1) {
          aiMessageContent = data
          aiMessageIndex = messages.value.length
          messages.value.push({
            type: 'ai',
            content: aiMessageContent,
            time: getCurrentTime()
          })
        } else {
          aiMessageContent += data
          messages.value[aiMessageIndex].content = aiMessageContent
        }
        scrollToBottom()
      },
      (error) => {
        console.error('Fetch SSE错误:', error)
        isLoading.value = false
        messages.value.push({
          type: 'ai',
          content: `连接错误: ${error.message || '请检查后端服务是否正常运行'}`,
          time: getCurrentTime()
        })
        scrollToBottom()
        if (sseConnection) {
          sseConnection.close()
          sseConnection = null
        }
      },
      () => {
        isLoading.value = false
        if (sseConnection) {
          sseConnection.close()
          sseConnection = null
        }
      }
    )
  }
}

// 返回主页
const goBack = () => {
  if (sseConnection) {
    sseConnection.close()
    sseConnection = null
  }
  router.push('/')
}

onMounted(() => {
  chatId.value = generateChatId()
})

onUnmounted(() => {
  if (sseConnection) {
    sseConnection.close()
    sseConnection = null
  }
})
</script>

<style scoped>
.chat-view {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: clamp(1.5rem, 3vw, 2.5rem);
  gap: 1.5rem;
  position: relative;
}

.chat-view::before {
  content: '';
  position: absolute;
  inset: 0;
  background: url('https://assets.codepen.io/4927073/stars.svg') center/400px repeat;
  opacity: 0.25;
  pointer-events: none;
}

.chat-view__header {
  background: var(--card-bg);
  border-radius: 28px;
  padding: clamp(1rem, 2vw, 1.9rem);
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 25px 60px rgba(255, 173, 211, 0.25);
}

.chat-view__title {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.chat-view__label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.chat-view__title h2 {
  margin-top: 0.35rem;
}

.back-btn {
  border: none;
  background: rgba(255, 143, 188, 0.2);
  color: #ff5f9d;
  padding: 0.55rem 1rem;
  border-radius: 14px;
  cursor: pointer;
  font-size: 0.95rem;
  box-shadow: inset 0 0 0 1px rgba(255, 95, 157, 0.2);
}

.chat-view__meta {
  display: flex;
  align-items: center;
}

.ai-profile {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  padding: 0.85rem 1.1rem;
  border-radius: 18px;
  background: rgba(255, 182, 218, 0.25);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6);
}

.ai-profile__avatar {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 1.7rem;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 15px 35px rgba(255, 157, 209, 0.45);
}

.ai-profile__name {
  font-weight: 600;
}

.chat-panel {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 36px;
  padding: clamp(1rem, 2vw, 1.6rem);
  border: 1px solid rgba(255, 255, 255, 0.7);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 220px);
  max-height: 920px;
  min-height: 480px;
  box-shadow: 0 45px 90px rgba(255, 173, 211, 0.3);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding-right: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message-row {
  display: flex;
  gap: 0.85rem;
  max-width: 85%;
  animation: fadeIn 0.35s ease;
}

.message-row.user {
  margin-left: auto;
  flex-direction: row-reverse;
}

.avatar {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 0.9rem;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 15px 30px rgba(255, 149, 206, 0.4);
}

.avatar--ai-love {
  background: var(--brand-gradient);
}

.avatar--user {
  background: var(--brand-gradient-alt);
}

.message-bubble {
  background: #ffffff;
  border-radius: 20px;
  padding: 1rem 1.15rem 0.7rem;
  box-shadow: 0 15px 45px rgba(255, 173, 211, 0.25);
  border: 1px solid rgba(255, 153, 204, 0.25);
  width: 100%;
  position: relative;
}

.message-row.user .message-bubble {
  background: var(--brand-gradient-alt);
  color: #fff;
  border: none;
  box-shadow: 0 18px 40px rgba(255, 153, 204, 0.35);
}

.message-text {
  font-size: 1rem;
  line-height: 1.8;
  text-align: left;
  white-space: pre-wrap;
}

.message-row.user .message-text {
  text-align: right;
}

.message-time {
  font-size: 0.75rem;
  opacity: 0.65;
  margin-top: 0.35rem;
  text-align: right;
}

.message-row.ai .message-time {
  text-align: left;
}

.typing-row .message-bubble {
  background: rgba(255, 189, 222, 0.25);
  border-style: dashed;
}

.typing {
  position: relative;
  padding-right: 1.6rem;
}

.typing::after {
  content: '...';
  position: absolute;
  right: 0.2rem;
  animation: dots 1.2s steps(4, end) infinite;
  color: #ff73b3;
}

.chat-input {
  margin-top: 1rem;
  display: flex;
  gap: 0.75rem;
  align-items: flex-end;
}

.chat-input textarea {
  flex: 1;
  resize: none;
  border: 1px solid rgba(255, 151, 208, 0.4);
  border-radius: 20px;
  padding: 1.1rem 1.3rem;
  font-size: 1rem;
  line-height: 1.7;
  min-height: 56px;
  max-height: 180px;
  box-shadow: inset 0 2px 10px rgba(255, 161, 209, 0.2);
  background: rgba(255, 255, 255, 0.85);
}

.chat-input textarea:focus {
  outline: 2px solid rgba(255, 151, 208, 0.6);
}

.chat-input textarea:disabled {
  background: #f7f7fb;
}

.send-btn {
  border: none;
  border-radius: 18px;
  padding: 1rem 2.1rem;
  background: linear-gradient(135deg, #ff9fd7 0%, #ff73b3 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, opacity 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 18px 35px rgba(255, 115, 179, 0.35);
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px) scale(1.01);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes dots {
  0%, 20% {
    content: '.';
  }
  40% {
    content: '..';
  }
  60%, 100% {
    content: '...';
  }
}

@media (max-width: 1024px) {
  .chat-panel {
    height: calc(100vh - 180px);
  }
}

@media (max-width: 768px) {
  .chat-view {
    padding: 1rem;
  }

  .chat-view__header {
    border-radius: 18px;
  }

  .chat-panel {
    border-radius: 24px;
    padding: 1rem;
  }

  .message-row {
    max-width: 100%;
  }
}

@media (max-width: 520px) {
  .chat-view__title {
    flex-direction: column;
    align-items: flex-start;
  }

  .ai-profile {
    width: 100%;
    justify-content: flex-start;
  }

  .chat-input {
    flex-direction: column;
  }

  .send-btn {
    width: 100%;
  }
}
</style>

