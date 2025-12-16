package com.agent;

import com.agent.service.AiCodeHelperService;
import com.agent.util.AiCodeHelper;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.Result;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Autowired
    @Qualifier("aiCodeHelperService")
    private AiCodeHelperService aiCodeHelperService;

    @Autowired
    @Qualifier("aiCodeHelperServiceNoRag")
    private AiCodeHelperService aiCodeHelperServiceNoRag;

    @Autowired
    private AiCodeHelper aiCodeHelper;

    //@Autowired
    //@Qualifier("aiCodeHelperServiceWithJsonOutput")
    //private AiCodeHelperService aiCodeHelperServiceWithJsonOutput;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat("你好，你是谁");
        System.out.println(result);
    }

    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat("你好，我的姓名是叫练");
        System.out.println(result);
        result = aiCodeHelperService.chat("我是谁来的,记得回答我");
        System.out.println(result);
    }


    @Test
    void chatForReport() {
        String userMessage = "你好，我的姓名是叫练，学编程两年半，请帮我制定AI学习计划";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }

    @Test
    void chatForReportWithJsonOutput() {
        String userMessage = "你好，我的姓名是叫练，学编程两年半，请帮我制定AI学习计划";
        String report = aiCodeHelperService.chatForReportWithJsonOutput(userMessage);
        System.out.println(report);
    }


    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("如果你是第一次找工作，会遇到哪些问题？简单回答一下");
        System.out.println(result.content());
        System.out.println(result.sources());
    }


    @Test
    void chatStreamMultimodal() throws IOException {
        // 读取图片文件
        File imageFile = new File("C:\\Users\\jiaolian\\Pictures\\ai_test.jpg");
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        
        // 转换为 Base64
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        
        // 根据文件扩展名确定 MIME 类型
        String fileName = imageFile.getName().toLowerCase();
        String mimeType;
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.endsWith(".gif")) {
            mimeType = "image/gif";
        } else if (fileName.endsWith(".webp")) {
            mimeType = "image/webp";
        } else {
            mimeType = "image/jpeg"; // 默认使用 jpeg
        }
        
        // 构建 Base64 数据 URI 格式: data:image/jpeg;base64,xxxxx
        String base64DataUri = "data:" + mimeType + ";base64," + base64;
        
        UserMessage userMessage = UserMessage.from(
            TextContent.from("描述图片"),
            ImageContent.from(base64DataUri)
        );
        String result = aiCodeHelper.chatWithMessage(userMessage);
        System.out.println(result);
    }
}


