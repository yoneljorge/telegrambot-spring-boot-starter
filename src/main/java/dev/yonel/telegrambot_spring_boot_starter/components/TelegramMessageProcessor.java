
package dev.yonel.telegrambot_spring_boot_starter.components;


import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.messages.MessageRelayService;
import dev.yonel.telegrambot_spring_boot_starter.messages.models.MessageBody;
import dev.yonel.telegrambot_spring_boot_starter.messages.models.ResponseBody;
import dev.yonel.telegrambot_spring_boot_starter.services.queue.MessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramMessageProcessor {

    @Autowired
    private MessageRelayService messageRelayService;
    @Autowired
    private TelegramResponsesBuilder messageBuilder;
    @Autowired
    private TelegramUserManager userManager;
    @Autowired
    private MessageTypeSelector messageTypeSelector;
    @Autowired
    private MessageQueueService mensajeColaService;

    public List<Object> processRequestMessage(
            TelegramBot bot,
            Update update,
            String fileUrl) throws Throwable {
        if ((update.getMessage() == null || update.getMessage().getText() == null) && update.getCallbackQuery() == null) {
            log.warn("Update no contiene mensaje de texto ni callbackQuery.");
            return null;
        }


        MessageBody messageBody = userManager.createMessageBody(bot, update, fileUrl);
        List<ResponseBody> responses = messageRelayService.handleMessage(messageBody);

        mensajeColaService.removeMensajesProcesados(messageBody.getUpdateid());
        return processResponses(responses);
    }

    public List<Object> processResponses(List<ResponseBody> responses) {
        if (responses == null || responses.isEmpty()) {
            log.warn("No se recibió respuesta válida para el mensaje.");
            return null;
        }
        
        List<Object> messages = new ArrayList<>();

        for (ResponseBody resp : responses) {
            Object message = messageTypeSelector.selectAndBuild(resp);
            if(message != null){
                messages.add(message);
            }
        }
        return messages;
    }
}