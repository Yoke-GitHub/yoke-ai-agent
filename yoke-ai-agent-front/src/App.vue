<template>
  <div class="app-shell">
    <main class="app-main">
      <router-view />
    </main>
    <footer class="app-footer">
      <div class="footer-links">
        <a href="https://www.baidu.com/" target="_blank" rel="noopener noreferrer">关于我们</a>
        <span>|</span>
        <a href="https://www.baidu.com/" target="_blank" rel="noopener noreferrer">联系我们</a>
        <span>|</span>
        <a href="https://www.baidu.com/" target="_blank" rel="noopener noreferrer">隐私政策</a>
      </div>
      <p>© {{ currentYear }} Yoke AI Agent · All Rights Reserved</p>
      <p>京公网安备 11000002000001 号 · 京ICP证030173号</p>
    </footer>
  </div>
</template>

<script setup>
const currentYear = new Date().getFullYear()
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Baloo+2:wght@400;500;600&family=Inter:wght@400;500;600&display=swap');

:root {
  --brand-gradient: linear-gradient(135deg, #8f8bff 0%, #ff9fd7 100%);
  --brand-gradient-alt: linear-gradient(135deg, #ffc778 0%, #ff92c2 100%);
  --bg-soft: #f7f1ff;
  --bg-soft-alt: #fef5ff;
  --border-light: rgba(203, 187, 255, 0.5);
  --text-primary: #402147;
  --text-secondary: #7a5f87;
  --card-bg: rgba(255, 255, 255, 0.96);
  --glow-1: rgba(143, 139, 255, 0.35);
  --glow-2: rgba(255, 159, 215, 0.35);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Baloo 2', 'Inter', 'Segoe UI', system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--bg-soft);
  color: var(--text-primary);
  min-height: 100vh;
  background-image: radial-gradient(circle at 10% 20%, rgba(255, 212, 236, 0.8) 0, transparent 35%),
    radial-gradient(circle at 70% 0%, rgba(183, 210, 255, 0.9) 0, transparent 42%),
    radial-gradient(circle at 20% 80%, rgba(255, 236, 185, 0.8) 0, transparent 40%);
}

#app {
  min-height: 100vh;
}

.app-shell {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
}

.app-shell::before,
.app-shell::after {
  content: '';
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  background: var(--glow-1);
  filter: blur(60px);
  z-index: -1;
  opacity: 0.6;
}

.app-shell::after {
  background: var(--glow-2);
  bottom: -120px;
  right: -80px;
}

.app-shell::before {
  top: -140px;
  left: -80px;
}

.app-footer {
  text-align: center;
  padding: 1.5rem 1rem 2rem;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  color: var(--text-secondary);
  font-size: 0.92rem;
  border-top: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 -10px 30px rgba(64, 33, 71, 0.08);
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 0.6rem;
  margin-bottom: 0.35rem;
  flex-wrap: wrap;
}

.footer-links a {
  color: var(--text-secondary);
  text-decoration: none;
  font-weight: 600;
}

.footer-links span {
  color: rgba(90, 70, 98, 0.4);
}
</style>

