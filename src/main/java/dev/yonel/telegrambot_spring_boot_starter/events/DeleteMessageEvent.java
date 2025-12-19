package dev.yonel.telegrambot_spring_boot_starter.events;

import lombok.Getter;

import java.util.EventObject;

@Getter
public class DeleteMessageEvent extends EventObject {
    private final String chatId;
    private final int messageId;
    
    public DeleteMessageEvent(Object source, String chatId, int messageId){
        super(source);
        this.chatId = chatId;
        this.messageId = messageId;
    }
}
