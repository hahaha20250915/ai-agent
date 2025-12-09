package com.agent.service;


import java.util.List;

import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.spring.AiService;

// @AiService 注解 表示这是一个 AI 服务 ,看起来扩展性稍微差一点,但是比较简单
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);

    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    // 学习报告
    record Report(String name, List<String> suggestionList){}

    @SystemMessage(fromResource = "prompt-report.txt")
    String chatForReportWithJsonOutput(String userMessage);
    
}
