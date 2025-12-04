<template>
  <section class="chat-view">
    <header class="chat-view__header">
      <div class="chat-view__title">
        <button class="back-btn" @click="goBack">← 返回</button>
        <div>
          <p class="chat-view__label">多工具执行体</p>
          <h2>AI 超级智能体</h2>
        </div>
      </div>
      <div class="chat-view__meta">
        <div class="ai-profile ai-profile--manus">
          <div class="ai-profile__avatar">🤖</div>
          <div>
            <p class="ai-profile__name">Manus · Workflow</p>
            <small>逐步执行，每一步均有播报</small>
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
            :class="message.type === 'ai' ? 'avatar--ai-manus' : 'avatar--user'"
          >
            <span>{{ message.type === 'ai' ? 'AI' : '我' }}</span>
          </div>
          <div class="message-bubble">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ message.time }}</div>
          </div>
        </div>

        <div v-if="isLoading" class="message-row ai typing-row">
          <div class="avatar avatar--ai-manus">
            <span>AI</span>
          </div>
          <div class="message-bubble">
            <div class="message-text typing">正在执行多步任务...</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <textarea
          v-model="inputMessage"
          @keyup.enter.exact.prevent="sendMessage"
          placeholder="输入需要执行的任务，Enter 发送"
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
import { ref, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
let eventSource = null

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

  isLoading.value = true
  let aiMessageContent = ''
  let aiMessageIndex = -1

  try {
    const url = `http://localhost:8123/api/ai/manus/chat?message=${encodeURIComponent(messageToSend)}`
    eventSource = new EventSource(url)

    eventSource.onmessage = (event) => {
      const chunk = event.data ? `${event.data}\n\n` : '\n'
      if (aiMessageIndex === -1) {
        aiMessageContent = chunk
        aiMessageIndex = messages.value.length
        messages.value.push({
          type: 'ai',
          content: aiMessageContent,
          time: getCurrentTime()
        })
      } else {
        aiMessageContent += chunk
        messages.value[aiMessageIndex].content = aiMessageContent
      }
      scrollToBottom()
    }

    eventSource.onopen = () => {
      console.log('SSE连接已打开')
    }

    eventSource.onerror = (error) => {
      console.log('SSE onerror触发, readyState:', eventSource.readyState)

      // 如果已经收到过 AI 返回内容（aiMessageIndex !== -1），
      // 那么大概率是任务完成或正常结束后的连接关闭，不再显示“连接失败”的错误。
      if (aiMessageIndex !== -1) {
        console.log('SSE连接结束（任务已产生输出，视为正常结束）')
        isLoading.value = false
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
        return
      }

      if (eventSource.readyState === EventSource.CLOSED) {
        console.log('SSE连接正常关闭')
        isLoading.value = false
        eventSource = null
      } else if (eventSource.readyState === EventSource.CONNECTING) {
        // 只有在“尚未收到任何任务输出”的情况下，才认为是真正的连接失败
        console.error('SSE连接失败:', error)
        isLoading.value = false
        messages.value.push({
          type: 'ai',
          content: '连接失败，请稍后再试。',
          time: getCurrentTime()
        })
        scrollToBottom()
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
      } else {
        console.error('SSE其他错误:', error)
        isLoading.value = false
        if (eventSource) {
          eventSource.close()
          eventSource = null
        }
      }
    }
  } catch (error) {
    console.error('发送消息错误:', error)
    isLoading.value = false
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
  }
}

// 返回主页
const goBack = () => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
  router.push('/')
}

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
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
  overflow: hidden;
}

.chat-view::before {
  content: '';
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  background: rgba(145, 189, 255, 0.35);
  filter: blur(70px);
  left: -120px;
  top: -150px;
  z-index: 0;
}

.chat-view::after {
  content: '';
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  background: rgba(153, 255, 241, 0.3);
  filter: blur(80px);
  right: -140px;
  bottom: -160px;
  z-index: 0;
}

.chat-view__header {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 28px;
  padding: clamp(1rem, 2vw, 1.9rem);
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  border: 1px solid rgba(188, 205, 255, 0.6);
  box-shadow: 0 25px 60px rgba(125, 154, 255, 0.25);
  position: relative;
  z-index: 1;
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
  background: rgba(139, 173, 255, 0.2);
  color: #4a6fff;
  padding: 0.55rem 1rem;
  border-radius: 14px;
  cursor: pointer;
  font-size: 0.95rem;
  box-shadow: inset 0 0 0 1px rgba(84, 129, 255, 0.2);
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
}

.ai-profile--manus {
  background: rgba(188, 205, 255, 0.25);
  box-shadow: inset 0 0 0 1px rgba(111, 154, 255, 0.25);
}

.ai-profile__avatar {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 1.6rem;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 15px 35px rgba(115, 151, 255, 0.35);
}

.ai-profile__name {
  font-weight: 600;
}

.chat-panel {
  background: rgba(255, 255, 255, 0.92);
  border-radius: 36px;
  padding: clamp(1rem, 2vw, 1.6rem);
  border: 1px solid rgba(188, 205, 255, 0.45);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 220px);
  max-height: 920px;
  min-height: 480px;
  box-shadow: 0 45px 90px rgba(120, 149, 255, 0.25);
  position: relative;
  z-index: 1;
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
  width: 46px;
  height: 46px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 0.9rem;
  font-weight: 600;
  color: #fff;
}

.avatar--ai-manus {
  background: linear-gradient(145deg, #7ecbff, #5a8bff);
  box-shadow: 0 18px 35px rgba(122, 168, 255, 0.35);
}

.avatar--user {
  background: linear-gradient(145deg, #ffc778, #ff9f7e);
  box-shadow: 0 18px 35px rgba(255, 159, 128, 0.35);
}

.message-bubble {
  background: #ffffff;
  border-radius: 20px;
  padding: 1rem 1.15rem 0.7rem;
  box-shadow: 0 15px 40px rgba(124, 156, 255, 0.2);
  border: 1px solid rgba(124, 156, 255, 0.2);
  width: 100%;
  position: relative;
}

.message-row.user .message-bubble {
  background: linear-gradient(135deg, #8f8bff, #7db9ff);
  color: #fff;
  border: none;
  box-shadow: 0 18px 45px rgba(127, 158, 255, 0.35);
}

.message-text {
  font-size: 0.98rem;
  line-height: 1.65;
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
  background: rgba(137, 176, 255, 0.2);
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
  border: 1px solid rgba(120, 158, 255, 0.45);
  border-radius: 20px;
  padding: 1.1rem 1.3rem;
  font-size: 1rem;
  line-height: 1.7;
  min-height: 56px;
  max-height: 180px;
  box-shadow: inset 0 2px 10px rgba(120, 158, 255, 0.2);
  background: rgba(255, 255, 255, 0.9);
}

.chat-input textarea:focus {
  outline: 2px solid rgba(120, 158, 255, 0.65);
}

.chat-input textarea:disabled {
  background: #f7f7fb;
}

.send-btn {
  border: none;
  border-radius: 18px;
  padding: 1rem 2.1rem;
  background: linear-gradient(135deg, #7db9ff, #5a8bff);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, opacity 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 18px 40px rgba(125, 185, 255, 0.35);
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
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

