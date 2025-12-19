package dev.yonel.telegrambot_spring_boot_starter.bots.core;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramBotProperties implements TelegramBot {
    @NotBlank
    private String token;
    @NotBlank
    private String username;
    @NotBlank
    private String path;
    @NotBlank
    private String id;
}
