<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  /* 动漫风格配色 */
  --color-primary: #ff6b9d;
  --color-secondary: #c084fc;
  --color-accent: #a78bfa;
  --color-success: #34d399;
  --color-warning: #fbbf24;
  --color-error: #f87171;
  
  /* 背景渐变 */
  --gradient-primary: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  --gradient-secondary: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  --gradient-love: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  --gradient-manus: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  
  /* 阴影 */
  --shadow-soft: 0 4px 20px rgba(0, 0, 0, 0.08);
  --shadow-medium: 0 8px 30px rgba(0, 0, 0, 0.12);
  --shadow-strong: 0 15px 50px rgba(0, 0, 0, 0.2);
  
  /* 圆角 */
  --radius-small: 12px;
  --radius-medium: 20px;
  --radius-large: 30px;
  --radius-full: 999px;
}

#app {
  width: 100%;
  height: 100vh;
  font-family: 'Noto Sans SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow: hidden;
}

body {
  margin: 0;
  padding: 0;
  background: var(--gradient-primary);
}

/* 滚动条样式（动漫风格） */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 107, 157, 0.5);
  border-radius: 10px;
  transition: background 0.3s;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 107, 157, 0.7);
}

/* 选择文本颜色 */
::selection {
  background: rgba(255, 107, 157, 0.3);
  color: #fff;
}
</style>

