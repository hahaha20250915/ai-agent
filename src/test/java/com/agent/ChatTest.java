package com.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.agent.longchain.AiCodeHelper;

@SpringBootTest
class ChatTest {

    @Autowired
    private AiCodeHelper aiCodeHelper;

    @Test
    public void chat() {
        String result = aiCodeHelper.chat("你是谁");
        System.out.println(result);
    }
}