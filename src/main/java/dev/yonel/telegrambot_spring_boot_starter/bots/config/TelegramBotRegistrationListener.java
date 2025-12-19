package dev.yonel.telegrambot_spring_boot_starter.bots.config;

import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.bots.config.webhook.WebhookRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

@Slf4j
public class TelegramBotRegistrationListener {

    private final List<AbstractTelegramBot> registeredBots;
    private final WebhookRegistrationService registerWebhookService;

    public TelegramBotRegistrationListener(
            List<AbstractTelegramBot> registeredBots,
            WebhookRegistrationService registerWebhookService){
        this.registeredBots = registeredBots;
        this.registerWebhookService = registerWebhookService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(){
        log.info("Iniciando registro de webhooks para {} bots ...", registeredBots.size());
        for(AbstractTelegramBot bot : registeredBots){
            if(bot.getBotProps().getToken() != null && bot.getBotProps().getPath() != null){
                registerWebhookService.registerWebhook(
                        bot.getBotProps().getToken(),
                        bot.getBotProps().getPath());
            }else{
                log.warn("El bot {} no tiene token o path configurado correctamente", bot.getClass().getSimpleName());
            }

        }
    }
}
