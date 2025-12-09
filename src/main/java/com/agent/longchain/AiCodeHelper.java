package com.agent.longchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;

@Component
public class AiCodeHelper {

    private static final Logger log = LoggerFactory.getLogger(AiCodeHelper.class);
    
    @Autowired
    private QwenChatModel qwenChatModel;

    public String chat(String message) {
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        //logger.info("AI 输出：" + aiMessage.toString());
        return aiMessage.text();
    }

    //多模态聊天
    public String chatWithMessage(UserMessage userMessage) {
        ChatResponse chatResponse = qwenChatModel.chat(userMessage);
        AiMessage aiMessage = chatResponse.aiMessage();
        //log.info("AI 输出：" + aiMessage.toString());
        return aiMessage.text();    
    }
    
}
