package dev.yonel.telegrambot_spring_boot_starter.components;

import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.context.UserSessionContext;
import dev.yonel.telegrambot_spring_boot_starter.events.DeleteMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DeleteMessageEventComponent {

    @Autowired
    private ApplicationEventPublisher publisher;

    public void deleteMessages(String chatId, UserSessionContext context, TelegramBot bot){
        if(context.getBotSession(bot).isPendingMessageToDelete()){
            for(int messageId : context.getBotSession(bot).getMessagesIdToDelete()){
              publisher.publishEvent(new DeleteMessageEvent(this,chatId, messageId));
            }
        }
    }
}
