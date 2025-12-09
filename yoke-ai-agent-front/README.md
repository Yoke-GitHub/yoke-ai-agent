# AI Agent 前端应用

基于 Vue3 的 AI 聊天应用前端项目。

## 功能特性

1. **主页**：应用切换中心，可以选择不同的 AI 应用
2. **AI 恋爱大师**：专业的恋爱咨询聊天应用，支持实时 SSE 流式对话
3. **AI 超级智能体**：强大的智能助手应用，支持实时 SSE 流式对话

## 技术栈

- Vue 3
- Vue Router 4
- Axios
- Vite

## 项目结构

```
yoke-ai-agent-front/
├── src/
│   ├── api/           # API 接口
│   ├── router/        # 路由配置
│   ├── utils/         # 工具函数
│   ├── views/         # 页面组件
│   ├── App.vue        # 根组件
│   └── main.js        # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 安装和运行

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

项目将在 http://localhost:3000 启动

### 3. 构建生产版本

```bash
npm run build
```

## 后端接口配置

默认后端地址：`http://localhost:8123/api`

接口列表：
- `GET /ai/love_app/chat/sse` - AI 恋爱大师 SSE 聊天接口
  - 参数：`message`（消息内容）、`chatId`（聊天室ID）

- `GET /ai/manus/chat` - AI 超级智能体聊天接口
  - 参数：`message`（消息内容）

## 注意事项

1. 确保后端服务已启动并运行在 `http://localhost:8123`
2. 如果后端地址不同，请在 `src/api/chat.js` 中修改相应的 URL
3. 每个聊天页面会自动生成唯一的聊天室 ID

## 开发说明

- 使用 Vue 3 Composition API
- 支持 SSE (Server-Sent Events) 实时流式响应
- 响应式设计，支持移动端和桌面端
- 聊天消息支持换行显示

