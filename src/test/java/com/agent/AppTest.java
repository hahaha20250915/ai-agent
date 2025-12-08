package com.agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for Spring Boot Application
 */
@SpringBootTest
@ActiveProfiles("test")
class AppTest {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void applicationStarts() {
        // Test that the application context loads successfully
        assertTrue(true);
    }
}

