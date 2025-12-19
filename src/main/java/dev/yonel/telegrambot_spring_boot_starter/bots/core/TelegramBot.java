package dev.yonel.telegrambot_spring_boot_starter.bots.core;

public interface TelegramBot {
    String getToken();
    String getPath();
    String getUsername();
    String getId();
}
