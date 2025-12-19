package dev.yonel.telegrambot_spring_boot_starter.bots;

import dev.yonel.telegrambot_spring_boot_starter.bots.config.TelegramBotAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = {
                TelegramBotAutoConfiguration.class,
                TestBot.class
        }
)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TelegramBotIntegrationTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private TestBot testBot;


    @Test
    void shouldRegisterTestBotAsBean() {
        // Verifica que Spring detectó y registró el bot
        assertTrue(context.containsBean("testBot"));
    }

    @Test
    void shouldInjectPropertiesIntoTestBot() {
        // Verifica que el BeanPostProcessor hizo su trabajo
        assertNotNull(testBot.getBotProps().getToken(), "El token no debería ser null");
        assertEquals("123456:ABC-DEF", testBot.getBotProps().getToken());
        assertEquals("test_bot", testBot.getBotProps().getUsername());
        assertEquals("/telegram/123456:ABC-DEF", testBot.getBotProps().getPath());
    }
}
