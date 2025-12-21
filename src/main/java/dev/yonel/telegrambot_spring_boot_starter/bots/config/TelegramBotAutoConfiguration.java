package dev.yonel.telegrambot_spring_boot_starter.bots.config;

import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.config.webhook.WebhookRegistrationService;
import dev.yonel.telegrambot_spring_boot_starter.bots.controller.TelegramWebhookController;
import dev.yonel.telegrambot_spring_boot_starter.components.*;
import dev.yonel.telegrambot_spring_boot_starter.messages.DefaultMessageRelayServiceImpl;
import dev.yonel.telegrambot_spring_boot_starter.messages.MessageRelayService;
import dev.yonel.telegrambot_spring_boot_starter.services.queue.MessageQueueService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "dev.yonel.telegrambot_spring_boot_starter")
public class TelegramBotAutoConfiguration {

    // --- COMPONENTES CORE ---

    @Bean
    public TelegramPlatform telegramPlatform() {
        return new TelegramPlatform();
    }

    @Bean
    public DeleteMessageEventComponent deleteMessageEventComponent() {
        return new DeleteMessageEventComponent();
    }

    @Bean
    public TelegramUserManager telegramUserManager() {
        return new TelegramUserManager();
    }

    // --- PROCESAMIENTO Y MENSAJERÍA ---

    /**
     * Si el usuario no crea automaticamente el servicio
     */
    @Bean
    @ConditionalOnMissingBean(MessageRelayService.class)
    public MessageRelayService messageRelayService() {
        return new DefaultMessageRelayServiceImpl();
    }

    @Bean
    public TelegramMessageProcessor telegramMessageProcessor() {
        return new TelegramMessageProcessor();
    }

    @Bean
    public TelegramButtonProcessor telegramButtonProcessor() {
        return new TelegramButtonProcessor();
    }

    @Bean
    public TelegramButtons telegramButtons() {
        return new TelegramButtons();
    }

    @Bean
    public MessageTypeSelector messageTypeSelector() {
        return new MessageTypeSelector();
    }

    @Bean
    public TelegramResponsesBuilder telegramResponsesBuilder() {
        return new TelegramResponsesBuilder();
    }

    // --- SERVICIOS DE COLA Y WEBHOOK ---

    @Bean
    public MessageQueueService mensajeColaService() {
        return new MessageQueueService();
    }

    @Bean
    @Primary
    public WebhookRegistrationService webhookRegistrationService() {
        return new WebhookRegistrationService();
    }


    // --- CONTROLADOR ---

    @Bean
    @ConditionalOnProperty(name = "telegram.webhook.enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnMissingBean
    public TelegramWebhookController telegramWebhookController(
            @Lazy List<AbstractTelegramBot> bots,
            MessageQueueService messageQueueService){
        return new TelegramWebhookController(bots, messageQueueService);
    }
    // --- INFRAESTRUCTURA DE SPRING ---

    @Bean
    public static TelegramBotBeanPostProcessor telegramBotBeanPostProcessor(Environment environment) {
        return new TelegramBotBeanPostProcessor(environment);
    }

    @Bean
    public TelegramBotRegistrationListener telegramBotRegistrationListener(List<AbstractTelegramBot> bots, WebhookRegistrationService service) {
        return new TelegramBotRegistrationListener(bots, service);
    }
}
