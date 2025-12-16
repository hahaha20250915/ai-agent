package com.agent;

import com.agent.service.AiCodeHelperService;

import dev.langchain4j.service.Result;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Autowired
    @Qualifier("aiCodeHelperService")
    private AiCodeHelperService aiCodeHelperService;

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
}


