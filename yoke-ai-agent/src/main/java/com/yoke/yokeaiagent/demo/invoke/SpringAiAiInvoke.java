package com.yoke.yokeaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author H
 * @project_name yoke-ai-agent
 * @filename SpringAiAiInvoke
 * @created_date 2025/10/26 20:44
 * @description SpringAiAiInvoke springAI 框架调用 AI 大模型（阿里）
 */
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是Yoke"))
                .getResult()
                .getOutput();
        System.out.println(output.getText());
    }
}

