package com.yoke.yokeaiagent.controller.AiController;

import com.yoke.yokeaiagent.agent.YokeManus;
import com.yoke.yokeaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 love app 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用 love app 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSse(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 love app 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 love app 应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // 获取 Flux 响应式数据并且直接通过订阅推送给 SseEmitter
        loveApp.doChatByStream(message, chatId).subscribe(chunk -> {
            try {
                sseEmitter.send(chunk);
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
    }

    /**
     * 流式调用 yokeManus 应用
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithYokeManus(String message){
        YokeManus yokeManus = new YokeManus(allTools, dashscopeChatModel);
        return yokeManus.runStream(message);
    }
}
