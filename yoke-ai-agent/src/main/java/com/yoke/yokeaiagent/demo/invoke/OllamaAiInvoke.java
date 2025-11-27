package com.yoke.yokeaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename OllamaAiInvoke
 * @created_date 2025/10/27 0:12
 * @description OllamaAiInvoke Spring AI 框架调用 AI 大模型（Ollama）
 */

// 取消注释后，项目启动时会执行
//@Component
public class OllamaAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel ollamaChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("你好，我是Yoke"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());
    }
}