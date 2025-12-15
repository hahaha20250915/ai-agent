package com.agent.longchain;

import java.util.Set;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;

public class SafeOutputGuardrail implements OutputGuardrail {

    private static final Set<String> sensitiveWords = Set.of("编程助手", "问题","的");

    /**
     * 检测用户输入是否安全
     */
    @Override
    public OutputGuardrailResult validate(AiMessage aiMessage) {
        // 获取用户输入并转换为小写以确保大小写不敏感
        String outputText = aiMessage.text().toLowerCase();
        // 使用正则表达式分割输出文本为单词
        //String[] words = outputText.split("\\W+");
        // 遍历所有单词，检查是否存在敏感词,如果存在敏感词,则用**替换
        /* for (String word : words) {
            if (sensitiveWords.contains(word)) {
                outputText = outputText.replace(word, "**");
            }
        } */
        /* for (String word : sensitiveWords) {
            String regex = "(?i)" + java.util.regex.Pattern.quote(word); // 不区分大小写匹配
            outputText = outputText.replaceAll(regex, "**");
        } */
        return successWith(outputText);
    }
}
