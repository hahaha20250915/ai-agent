package com.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.agent.longchain.AiCodeHelper;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import java.nio.file.Paths;
import java.util.Arrays;

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
        //ImageContent imageContent = ImageContent.from("https://image-cdn.poizon.com/app/2024/community/1974046945_byte236056byte_7f18c40fbfb676c641b3b142917e7d73_iOS_w1440h1920.jpg");
        // 将 Windows 路径转换为正确的 URI 格式
        String imagePath = "D://ai_test.jpg";
        String imageUri = Paths.get(imagePath).toUri().toString();
        ImageContent imageContent2 = ImageContent.from(imageUri);
        
        // 使用系统消息明确要求中文回答
        SystemMessage systemMessage = SystemMessage.from("请用中文回答所有问题。");
        UserMessage userMessage = UserMessage.from("请详细分析这张图片", imageContent2);
        
        // 将系统消息和用户消息一起发送
        String result = aiCodeHelper.chatWithMessages(Arrays.asList(systemMessage, userMessage));
        System.out.println(result);
    }
}