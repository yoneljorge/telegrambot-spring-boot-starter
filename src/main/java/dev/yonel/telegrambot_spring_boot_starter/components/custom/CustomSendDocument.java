package dev.yonel.telegrambot_spring_boot_starter.components.custom;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;

@Getter
@Setter
public class CustomSendDocument extends SendDocument{
    @Getter
    @Setter
    private boolean removable = false;
}
