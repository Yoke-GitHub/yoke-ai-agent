<template>
<section class="home">
  <div class="home__halo home__halo--pink"></div>
  <div class="home__halo home__halo--blue"></div>
  <div class="home__content">
      <header class="home__hero">
        <p class="home__badge">Yoke AI Suite</p>
        <h1>一站式智能体中心</h1>
        <p class="home__sub">
          精选垂直智能体，覆盖情感陪伴与超级效率两大场景，随时切换，多端自适应。
        </p>
      </header>

      <div class="home__cards">
        <article class="app-card love" @click="goToLoveApp">
          <div class="app-card__head">
            <div class="app-card__icon">💕</div>
            <span class="app-card__tag">情感陪伴</span>
          </div>
          <h3>AI 恋爱大师</h3>
          <p>12 年情感咨询经验的虚拟导师，洞悉恋爱心理，随时提供暖心建议。</p>
          <ul>
            <li>即时情绪疏导</li>
            <li>恋爱关系诊断</li>
            <li>亲密沟通建议</li>
          </ul>
        </article>

        <article class="app-card manus" @click="goToManusApp">
          <div class="app-card__head">
            <div class="app-card__icon">🤖</div>
            <span class="app-card__tag">效率助手</span>
          </div>
          <h3>AI 超级智能体</h3>
          <p>多工具协同的执行专家，处理复杂链式任务，分步骤输出可追踪结果。</p>
          <ul>
            <li>长流程任务拆解</li>
            <li>实时进度播报</li>
            <li>工具链自动编排</li>
          </ul>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

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
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(2rem, 4vw, 4rem);
  overflow: hidden;
}

.home__halo {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  filter: blur(50px);
  opacity: 0.5;
  z-index: 0;
}

.home__halo--pink {
  background: rgba(255, 163, 221, 0.8);
  top: -80px;
  left: -120px;
}

.home__halo--blue {
  background: rgba(166, 196, 255, 0.85);
  bottom: -100px;
  right: -140px;
}

.home__content {
  position: relative;
  z-index: 1;
  width: min(1200px, 100%);
  display: flex;
  flex-direction: column;
  gap: 3rem;
  padding: 2rem;
  border-radius: 36px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 25px 80px rgba(54, 26, 73, 0.18);
}

.home__hero {
  text-align: center;
  max-width: 720px;
  margin: 0 auto;
}

.home__badge {
  display: inline-flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.5rem 1rem;
  border-radius: 999px;
  font-size: 0.9rem;
  letter-spacing: 0.06em;
  background: rgba(255, 189, 222, 0.5);
  color: #ff4f9d;
  font-weight: 600;
  margin-bottom: 1rem;
  box-shadow: inset 0 0 0 1px rgba(255, 132, 194, 0.4);
}

.home__hero h1 {
  font-size: clamp(2.2rem, 4.5vw, 3.65rem);
  margin-bottom: 1rem;
  text-shadow: 0 6px 18px rgba(255, 159, 215, 0.35);
}

.home__sub {
  color: var(--text-secondary);
  line-height: 1.9;
}

.home__cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: clamp(1.5rem, 2vw, 2.5rem);
}

.app-card {
  position: relative;
  overflow: hidden;
  background: var(--card-bg);
  border-radius: 32px;
  padding: 2.2rem;
  border: 1px solid var(--border-light);
  box-shadow: 0 25px 60px rgba(117, 73, 148, 0.18);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 1.3rem;
  cursor: pointer;
}

.app-card::after {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.35), transparent 60%);
  transition: opacity 0.3s ease;
}

.app-card:hover {
  transform: translateY(-10px) scale(1.01);
  box-shadow: 0 35px 90px rgba(117, 73, 148, 0.25);
}

.app-card:hover::after {
  opacity: 1;
}

.app-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-card__icon {
  width: 72px;
  height: 72px;
  border-radius: 22px;
  display: grid;
  place-items: center;
  font-size: 2.15rem;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 10px 25px rgba(255, 255, 255, 0.5);
}

.app-card__tag {
  padding: 0.4rem 1.1rem;
  border-radius: 999px;
  font-size: 0.9rem;
  background: rgba(255, 255, 255, 0.6);
  color: var(--text-secondary);
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.6);
}

.app-card h3 {
  font-size: 1.7rem;
}

.app-card p {
  color: var(--text-secondary);
  line-height: 1.75;
}

.app-card ul {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  color: var(--text-secondary);
  font-size: 1rem;
}

.app-card ul li {
  position: relative;
  padding-left: 1.6rem;
}

.app-card ul li::before {
  content: '✦';
  position: absolute;
  left: 0;
  color: #ff87c5;
  font-size: 0.95rem;
}

.app-card.love {
  background: linear-gradient(160deg, rgba(255, 209, 233, 0.95), rgba(255, 255, 255, 0.9));
}

.app-card.manus {
  background: linear-gradient(160deg, rgba(215, 227, 255, 0.95), rgba(255, 255, 255, 0.9));
}

@media (max-width: 768px) {
  .home__content {
    gap: 2rem;
    padding: 1.5rem;
  }

  .app-card {
    padding: 1.8rem;
  }
}

@media (max-width: 576px) {
  .home {
    padding: 1.5rem 1rem 3rem;
  }

  .app-card__head {
    flex-direction: column;
    gap: 0.75rem;
    align-items: flex-start;
  }

  .home__hero h1 {
    font-size: 2.2rem;
  }
}
</style>

