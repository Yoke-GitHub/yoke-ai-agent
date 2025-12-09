<template>
  <div class="home">
    <div class="home-content">
      <div class="header">
        <h1>AI Agent 应用中心</h1>
        <p class="subtitle">选择您想要使用的AI应用</p>
      </div>
      <div class="app-list">
        <div class="app-card" @click="goToLoveApp">
          <div class="app-icon">💕</div>
          <h2>AI 恋爱大师</h2>
          <p>专业的恋爱咨询助手，为您提供情感建议</p>
          <div class="app-footer">点击进入 →</div>
        </div>
        <div class="app-card" @click="goToManusApp">
          <div class="app-icon">🤖</div>
          <h2>AI 超级智能体</h2>
          <p>强大的智能助手，帮您解决各种问题</p>
          <div class="app-footer">点击进入 →</div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import Footer from '../components/Footer.vue'

const router = useRouter()

const goToLoveApp = () => {
  router.push('/love-app')
}

const goToManusApp = () => {
  router.push('/manus-app')
}
</script>

<style scoped>
.home {
  width: 100%;
  min-height: 100vh;
  background: var(--gradient-primary);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow-x: hidden;
}

.home-content {
  flex: 1;
  width: 100%;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

/* 背景装饰 */
.home::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: float 20s infinite linear;
  pointer-events: none;
}

@keyframes float {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

.header {
  text-align: center;
  color: #fff;
  margin-bottom: 60px;
  position: relative;
  z-index: 1;
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 15px;
  background: linear-gradient(135deg, #fff 0%, #ffeef8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
  animation: slideInDown 0.6s ease-out;
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.subtitle {
  font-size: 20px;
  opacity: 0.95;
  font-weight: 300;
  animation: fadeIn 0.8s ease-out 0.2s backwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 0.95;
  }
}

.app-list {
  display: flex;
  gap: 30px;
  max-width: 1200px;
  width: 100%;
  justify-content: center;
  flex-wrap: wrap;
  position: relative;
  z-index: 1;
}

.app-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-large);
  padding: 40px 35px;
  width: 400px;
  box-shadow: var(--shadow-strong);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 2px solid rgba(255, 255, 255, 0.5);
  position: relative;
  overflow: hidden;
}

.app-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.app-card:hover::before {
  left: 100%;
}

.app-card:hover {
  transform: translateY(-15px) scale(1.02);
  box-shadow: 0 20px 60px rgba(255, 107, 157, 0.3);
  border-color: rgba(255, 107, 157, 0.3);
}

.app-card:nth-child(1) {
  animation: slideInLeft 0.6s ease-out 0.2s backwards;
}

.app-card:nth-child(2) {
  animation: slideInRight 0.6s ease-out 0.4s backwards;
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.app-icon {
  font-size: 80px;
  margin-bottom: 25px;
  filter: drop-shadow(0 5px 15px rgba(0, 0, 0, 0.1));
  transition: transform 0.3s;
  display: inline-block;
}

.app-card:hover .app-icon {
  transform: scale(1.1) rotate(5deg);
}

.app-card h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 15px;
  font-weight: 700;
  letter-spacing: 1px;
}

.app-card p {
  font-size: 16px;
  color: #666;
  text-align: center;
  line-height: 1.8;
  margin-bottom: 25px;
  flex-grow: 1;
}

.app-footer {
  color: var(--color-primary);
  font-weight: 600;
  font-size: 16px;
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.3s;
}

.app-card:hover .app-footer {
  transform: translateX(5px);
}

/* 平板适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .app-list {
    max-width: 900px;
  }
  
  .app-card {
    width: calc(50% - 15px);
    max-width: none;
  }
  
  .header h1 {
    font-size: 42px;
  }
}

/* 手机适配 */
@media (max-width: 768px) {
  .home {
    padding: 30px 15px;
  }
  
  .header {
    margin-bottom: 40px;
  }
  
  .header h1 {
    font-size: 32px;
    letter-spacing: 1px;
  }
  
  .subtitle {
    font-size: 16px;
  }
  
  .app-list {
    flex-direction: column;
    align-items: stretch;
    gap: 20px;
  }
  
  .app-card {
    width: 100%;
    max-width: none;
    padding: 30px 25px;
  }
  
  .app-icon {
    font-size: 64px;
    margin-bottom: 20px;
  }
  
  .app-card h2 {
    font-size: 24px;
  }
  
  .app-card p {
    font-size: 15px;
  }
}

/* 小屏手机 */
@media (max-width: 480px) {
  .home {
    padding: 20px 10px;
  }
  
  .header h1 {
    font-size: 28px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .app-card {
    padding: 25px 20px;
    border-radius: var(--radius-medium);
  }
  
  .app-icon {
    font-size: 56px;
  }
  
  .app-card h2 {
    font-size: 20px;
  }
  
  .app-card p {
    font-size: 14px;
  }
}
</style>

