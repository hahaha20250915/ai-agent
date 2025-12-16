package com.agent.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;

/**
 * 自定义创建实体类，用于创建AI模型实例
 */
@Component
public class AiCodeHelper {

    @Autowired
    private QwenChatModel qwenChatModel;

    /**
     * 普通文本对话
     * @param message
     * @return
     */
    public String chat(String message) {
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        return aiMessage.text();
    }

    /**
     * 多模态对话
     * userMessage: 用户消息封装了文本、图片、PDF文件
     * @return
     */
    public String chatWithMessage(UserMessage userMessage) {
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        return aiMessage.text();
    }
    
}
