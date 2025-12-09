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
}
