package com.agent.service;


import java.util.List;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
// import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.guardrail.InputGuardrails;
import dev.langchain4j.service.guardrail.OutputGuardrails;

import com.agent.longchain.SafeInputGuardrail;
import reactor.core.publisher.Flux;

// @AiService 注解 表示这是一个 AI 服务 ,看起来扩展性稍微差一点,但是比较简单
@InputGuardrails({SafeInputGuardrail.class})
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);

    /**
     * 结构化输出：方式1
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    // 学习报告,要求返回的格式为：{name: 学习报告的名称, suggestionList: 学习报告的建议列表}
    record Report(String name, List<String> suggestionList){}

    /**
     * 结构化输出：方式2
     * @return 学习报告的 JSON 字符串 依赖llm提示词：prompt-report.txt
     */
    @SystemMessage(fromResource = "prompt-report.txt")
    String chatForReportWithJsonOutput(String userMessage);

    /**
     * 使用 RAG 检索增强生成
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithRag(String userMessage);

    // 流式对话
    Flux<String> chatStream(@MemoryId String memoryId, @UserMessage String userMessage);
    
}


