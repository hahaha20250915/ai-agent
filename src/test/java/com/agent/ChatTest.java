package com.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.agent.longchain.AiCodeHelper;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.UserMessage;

import java.nio.file.Paths;

@SpringBootTest
class ChatTest {

    @Autowired
    private AiCodeHelper aiCodeHelper;

    @Test
    public void chat() {
        String result = aiCodeHelper.chat("你是谁");
        System.out.println(result);
    }

    @Test
    public void chatWithMessage() {
        ImageContent imageContent = ImageContent.from("https://image-cdn.poizon.com/app/2024/community/1974046945_byte236056byte_7f18c40fbfb676c641b3b142917e7d73_iOS_w1440h1920.jpg");
        //ImageContent imageContent2 = ImageContent.from("D://ai_test.jpg");
        UserMessage userMessage = UserMessage.from("分析这张图片",imageContent);
        String result = aiCodeHelper.chatWithMessage(userMessage);
        System.out.println(result);
    }
}