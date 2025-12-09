<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-links">
        <a href="#" class="footer-link">关于我们</a>
        <span class="footer-divider">|</span>
        <a href="#" class="footer-link">帮助中心</a>
        <span class="footer-divider">|</span>
        <a href="#" class="footer-link">隐私政策</a>
        <span class="footer-divider">|</span>
        <a href="#" class="footer-link">使用条款</a>
      </div>
      <div class="footer-copyright">
        <p>© {{ currentYear }} AI Agent 应用中心 版权所有</p>
        <p class="footer-icp">ICP备案号：京ICP备XXXXXXXX号-X</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => {
  return new Date().getFullYear()
})
</script>

<style scoped>
.footer {
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  padding: 30px 20px;
  margin-top: auto;
  position: relative;
  z-index: 10;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.03);
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.footer-link {
  color: #666;
  text-decoration: none;
  font-size: 13px;
  transition: color 0.3s;
  padding: 0 5px;
  white-space: nowrap;
}

.footer-link:hover {
  color: var(--color-primary);
  text-decoration: underline;
}

.footer-divider {
  color: #ccc;
  font-size: 12px;
  padding: 0 5px;
}

.footer-copyright {
  color: #999;
  font-size: 12px;
  line-height: 1.8;
}

.footer-copyright p {
  margin: 5px 0;
}

.footer-icp {
  color: #999;
  font-size: 12px;
}

/* 平板适配 */
@media (min-width: 769px) and (max-width: 1024px) {
  .footer {
    padding: 25px 20px;
  }
  
  .footer-links {
    gap: 8px;
  }
  
  .footer-link {
    font-size: 12px;
  }
}

/* 手机适配 */
@media (max-width: 768px) {
  .footer {
    padding: 20px 15px;
  }
  
  .footer-links {
    gap: 6px;
    margin-bottom: 12px;
  }
  
  .footer-link {
    font-size: 11px;
    padding: 0 3px;
  }
  
  .footer-divider {
    font-size: 10px;
    padding: 0 3px;
  }
  
  .footer-copyright {
    font-size: 11px;
  }
  
  .footer-icp {
    font-size: 11px;
  }
}

/* 小屏手机 */
@media (max-width: 480px) {
  .footer {
    padding: 15px 10px;
  }
  
  .footer-links {
    flex-direction: column;
    gap: 8px;
    margin-bottom: 10px;
  }
  
  .footer-divider {
    display: none;
  }
  
  .footer-link {
    font-size: 10px;
  }
  
  .footer-copyright {
    font-size: 10px;
  }
  
  .footer-icp {
    font-size: 10px;
  }
}
</style>

