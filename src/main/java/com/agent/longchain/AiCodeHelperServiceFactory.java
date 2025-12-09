package com.agent.longchain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.agent.service.AiCodeHelperService;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

/**
 * AiCodeHelperService 工厂类 用于创建 AiCodeHelperService 实例
 * 使用 @Configuration 注解 表示这是一个配置类
 * 使用 @Bean 注解 表示这是一个 Bean 实例
 * 使用 @Autowired 注解 表示这是一个自动注入的 Bean
 */
@Configuration
public class AiCodeHelperServiceFactory {

    @Autowired
    private QwenChatModel qwenChatModel;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        
        //不加入会话记忆
        //return AiServices.create(AiCodeHelperService.class, qwenChatModel);

        //加入会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
            .chatModel(qwenChatModel)
            .chatMemory(chatMemory)
            .build();
        return aiCodeHelperService;
    }

    @Bean
    public AiCodeHelperService aiCodeHelperServiceWithJsonOutput() {
        
        // 注意：qwen-max 模型不支持 ResponseFormat.JSON
        // 如果需要 JSON 输出，可以通过以下方式实现：
        // 1. 在 system prompt 中要求模型返回 JSON 格式
        // 2. 使用支持 JSON 格式的其他模型（如 qwen-plus）
        // 3. 在后处理中解析和格式化响应
        
        // 目前使用与第一个 Bean 相同的配置
        // 如果需要 JSON 输出，可以在 system-prompt.txt 中添加要求返回 JSON 的指令
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        AiCodeHelperService aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory)
                .build();
        
        return aiCodeHelperService;
    }
}
