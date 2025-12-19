package dev.yonel.telegrambot_spring_boot_starter.services;


import dev.yonel.telegrambot_spring_boot_starter.events.DeleteMessageEvent;

public interface MessageDeletionHandler {

    void handleDeleteMessage(DeleteMessageEvent event);
}
