package dev.yonel.telegrambot_spring_boot_starter.bots;

import dev.yonel.telegrambot_spring_boot_starter.bots.annotations.TelegramBot;

@TelegramBot(
        prefix = "telegram.bot.test",
        id = "test")
public class TestBot extends AbstractTelegramBot {
}
