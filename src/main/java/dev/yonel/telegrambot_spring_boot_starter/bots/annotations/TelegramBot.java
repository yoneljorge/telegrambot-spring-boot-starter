package dev.yonel.telegrambot_spring_boot_starter.bots.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TelegramBot {
    /**
     * Prefijo de props:
     * telegram.bot.admin
     * telegram.bot.client
     */
    String prefix();

    /**
     * Identificador del bot
     */
    String id();
}
