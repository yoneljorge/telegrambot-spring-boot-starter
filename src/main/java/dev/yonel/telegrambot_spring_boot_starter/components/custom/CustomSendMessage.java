package dev.yonel.telegrambot_spring_boot_starter.components.custom;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class CustomSendMessage extends SendMessage {
    @Getter
    @Setter
    private boolean removable = false;
}
