package com.agent;

import com.agent.controller.AIController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatTest {

    @Autowired
    private AIController aiController;

    @Test
    void aiControllerShouldBeLoaded() {
        Assertions.assertNotNull(aiController);
    }
}


