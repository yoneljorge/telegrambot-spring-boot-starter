package dev.yonel.telegrambot_spring_boot_starter.bots.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yonel.telegrambot_spring_boot_starter.BaseTest;
import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.TestBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.config.TelegramBotAutoConfiguration;
import dev.yonel.telegrambot_spring_boot_starter.bots.config.webhook.WebhookRegistrationService;
import dev.yonel.telegrambot_spring_boot_starter.services.queue.MessageQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TelegramWebhookControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestBot testBot; // Bot real inyectado con sus propiedades del YAML

    @MockitoBean
    private MessageQueueService messageQueueService;

    @MockitoBean
    private WebhookRegistrationService webhookRegistrationService;

    private Update update;
    private String jsonContent;

    @BeforeEach
    void setup() throws Exception{
        update = new Update();
        update.setUpdateId(12345);

        Message message = new Message();
        message.setMessageId(1);
        message.setText("Hola");
        update.setMessage(message);

        jsonContent = objectMapper.writeValueAsString(update);
    }
    @Test
    void whenValidToken_thenReturns200AndQueuesMessage() throws Exception {
        // Obtenemos el token directamente del bot configurado por el YAML
        String validToken = testBot.getBotToken();

        mockMvc.perform(post("/telegram/" + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andDo(print())
                .andExpect(status().isOk());

        // Verificamos que el controlador delegó el trabajo al servicio de cola
        verify(messageQueueService, times(1)).queueMessage(any());
    }

    @Test
    void whenInvalidToken_thenReturns200ButNoProcessing() throws Exception {
        String invalidToken = "wrong_token";
        String updateJson = "{\"update_id\": 12345}";

        mockMvc.perform(post("/telegram/" + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());

        verify(messageQueueService, never()).queueMessage(any());
    }
}
