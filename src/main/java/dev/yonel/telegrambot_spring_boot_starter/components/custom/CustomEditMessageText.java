package dev.yonel.telegrambot_spring_boot_starter.components.custom;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public class CustomEditMessageText extends EditMessageText{

    @Getter
    @Setter
    private boolean removable = false;
}
