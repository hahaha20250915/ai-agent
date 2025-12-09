package com.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.agent.service.AiCodeHelperService;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Autowired
    private AiCodeHelperService aiCodeHelperService;

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
}
