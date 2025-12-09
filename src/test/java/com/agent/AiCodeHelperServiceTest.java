package com.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.agent.service.AiCodeHelperService;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Autowired
    @Qualifier("aiCodeHelperService")
    private AiCodeHelperService aiCodeHelperService;

    @Autowired
    @Qualifier("aiCodeHelperServiceWithJsonOutput")
    private AiCodeHelperService aiCodeHelperServiceWithJsonOutput;

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
    void chatWithJsonOutput() {
        // 使用带 JSON 格式化输出的服务
        String result = aiCodeHelperServiceWithJsonOutput.chat("你好，你是谁");
        System.out.println("JSON 格式化输出结果: " + result);
    }

}
