package com.yoke.yokeaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 使用Http请求调用 AI
 */
public class HttpAliInvoke {
    
    public static void main(String[] args) {
        // 替换为你的实际API密钥
        String apiKey = TestApiKey.API_KEY;
        
        // 构建请求URL
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");
        
        // 构建消息数组
        JSONObject messages = new JSONObject();
        messages.set("role", "system");
        messages.set("content", "You are a helpful assistant.");
        
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");
        
        requestBody.set("messages", new Object[]{messages, userMessage});
        
        try {
            // 发送POST请求
            HttpResponse response = HttpRequest.post(url)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .execute();
            
            // 打印响应状态码
            System.out.println("Status: " + response.getStatus());
            
            // 打印响应内容
            System.out.println("Response: " + response.body());
            
            // 解析响应JSON
            JSONObject responseJson = JSONUtil.parseObj(response.body());
            System.out.println("Parsed response: " + responseJson);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
