package com.agent.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.MediaType;

import com.agent.service.AiCodeHelperService;
import com.agent.service.impl.MultimodalChatService;

import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * AI Controller
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AiCodeHelperService aiCodeHelperService;
    
    @Autowired
    private MultimodalChatService multimodalChatService;

    /**
     * 文本对话接口
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public Flux<ServerSentEvent<String>> chat(@RequestParam("memoryId") String memoryId,
                                              @RequestParam("message") String message) {
        return aiCodeHelperService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * 多模态聊天接口：支持文本 + 图片/PDF
     * 
     * @param memoryId 会话记忆 ID
     * @param message 文本消息（可选）
     * @param images 图片文件数组（可选）
     * @param pdfs PDF 文件数组（可选）
     * @return 流式响应
     */
    @PostMapping(value = "/chat/multimodal", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public Flux<ServerSentEvent<String>> chatMultimodal(
            @RequestParam("memoryId") String memoryId,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "pdfs", required = false) MultipartFile[] pdfs) {
        
        try {
            return multimodalChatService.chatStreamMultimodal(memoryId, message, images, pdfs)
                    .map(chunk -> ServerSentEvent.<String>builder()
                            .data(chunk)
                            .build())
                    .onErrorResume(error -> Flux.just(
                            ServerSentEvent.<String>builder()
                                    .data("错误: " + error.getMessage())
                                    .build()
                    ));
        } catch (IOException e) {
            return Flux.error(e);
        }
    }
}


