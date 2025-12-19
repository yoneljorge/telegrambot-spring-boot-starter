package dev.yonel.telegrambot_spring_boot_starter;

import dev.yonel.telegrambot_spring_boot_starter.bots.TestBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.config.TelegramBotAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({
        TelegramBotAutoConfiguration.class,
        TestBot.class
})
public class BaseTest {

}
