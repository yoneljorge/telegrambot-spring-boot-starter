package dev.yonel.telegrambot_spring_boot_starter.services;

import dev.yonel.telegrambot_spring_boot_starter.bots.config.webhook.WebhookRegistrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterWebhookServiceTest {
    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        System.out.println("INICIANDO PRUEBA: testRegisterWebhook");
    }

    @AfterEach
    void tearDown() {
        System.out.println("PRUEBA FINALIZADA CORRECTAMENTE");
    }

    @Test
    public void testRegisterWebhook() {
        doReturn(requestBodyUriSpec).when(restClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(anyString());
        doReturn(requestBodySpec).when(requestBodySpec).body(any(Object.class));
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(ResponseEntity.ok().build()).when(responseSpec).toBodilessEntity();

        WebhookRegistrationService service = new WebhookRegistrationService(restClient);
        ReflectionTestUtils.setField(service, "appUrl", "https://miwebhook.com");

        service.registerWebhook("-dummy-token", "/webhook");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("https://api.telegram.org/bot-dummy-token/setWebhook");
        verify(requestBodySpec).body(any(Object.class));
        verify(responseSpec).toBodilessEntity();
    }
}
