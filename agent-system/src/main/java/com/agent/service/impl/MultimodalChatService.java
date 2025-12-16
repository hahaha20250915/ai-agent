package com.agent.service.impl;

import com.agent.util.MultimodalFileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 多模态聊天服务实现
 */
@Service
public class MultimodalChatService {
    
    @Autowired
    private QwenChatModel qwenChatModel;
    
    private final ChatMemoryProvider chatMemoryProvider = (memoryId) ->
            MessageWindowChatMemory.withMaxMessages(10);
    
    /**
     * 多模态流式对话
     * 
     * @param memoryId 会话记忆 ID
     * @param message 文本消息（可选）
     * @param images 图片文件数组（可选）
     * @param pdfs PDF 文件数组（可选）
     * @return 流式响应
     * @throws IOException 处理文件时的 IO 异常
     */
    public Flux<String> chatStreamMultimodal(
            String memoryId,
            String message,
            MultipartFile[] images,
            MultipartFile[] pdfs) throws IOException {
        
        // 处理图片
        List<String> imageBase64List = MultimodalFileProcessor.processImages(images);
        
        // 处理 PDF（转为图片）
        List<String> pdfImageList = MultimodalFileProcessor.processPdfs(pdfs);
        imageBase64List.addAll(pdfImageList);
        
        // 构建消息内容
        List<Content> contents = new ArrayList<>();
        
        // 添加文本内容
        if (message != null && !message.trim().isEmpty()) {
            contents.add(TextContent.from(message));
        }
        
        // 添加图片内容
        for (String base64ImageDataUri : imageBase64List) {
            // LangChain4j 的 ImageContent.from() 支持 Base64 数据 URI 格式
            // 格式: data:image/png;base64,xxxxx
            contents.add(ImageContent.from(base64ImageDataUri));
        }
        
        // 如果没有任何内容，返回错误
        if (contents.isEmpty()) {
            return Flux.error(new IllegalArgumentException("消息内容不能为空，请提供文本、图片或PDF文件"));
        }
        
        UserMessage userMessage = new UserMessage(contents);

        // 获取或创建对话记忆
        var chatMemory = chatMemoryProvider.get(memoryId);
        chatMemory.add(userMessage);
        
        // 获取历史消息
        List<dev.langchain4j.data.message.ChatMessage> messages = chatMemory.messages();
        
        // 使用 QwenChatModel 生成完整响应，然后分块发送以模拟流式输出
        // 注意：qwen-vl-plus-latest 支持多模态，但不支持流式输出
        // 因此我们生成完整响应后分块发送
        try {
            // 直接使用 QwenChatModel 的 chat 方法
            // QwenChatModel 实现了 ChatModel 接口，支持多模态输入
            var response = qwenChatModel.chat(messages);
            AiMessage aiMessage = response.aiMessage();
            
            // 将 AI 响应添加到记忆
            chatMemory.add(aiMessage);
            
            String fullText = aiMessage.text();
            
            // 如果响应为空，返回错误
            if (fullText == null || fullText.isEmpty()) {
                return Flux.error(new RuntimeException("AI 响应为空"));
            }
            
            // 将完整响应分块发送以模拟流式输出
            // 每 3 个字符作为一个块发送，延迟 50ms
            return Flux.fromArray(fullText.split(""))
                    .buffer(3)
                    .map(chars -> String.join("", chars))
                    .delayElements(java.time.Duration.ofMillis(50))
                    .doOnComplete(() -> {
                        // 响应完成
                    });
        } catch (Exception e) {
            return Flux.error(new RuntimeException("生成响应时发生错误: " + e.getMessage(), e));
        }
    }
}

