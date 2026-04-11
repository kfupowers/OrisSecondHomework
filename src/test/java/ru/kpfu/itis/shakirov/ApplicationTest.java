package ru.kpfu.itis.shakirov;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ApplicationTest {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts successfully
    }

    @Test
    void mainMethodRunsWithoutException() {
        assertDoesNotThrow(() -> Application.main(new String[]{}));
    }
}