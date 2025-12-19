
package dev.yonel.telegrambot_spring_boot_starter.components;

import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomEditMessageReplyMarkup;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomEditMessageText;
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
public class TelegramButtonProcessor {

    @Autowired
    private MessageRelayService messageRelayService;
    @Autowired
    private TelegramResponsesBuilder responsesBuilder;
    @Autowired
    private TelegramUserManager userManager;
    @Autowired
    private MessageQueueService mensajeColaService;

    public List<CustomEditMessageText> processEditMessageText(TelegramBot bot, Update update, String fileUrl) throws Throwable {
        if (update.getCallbackQuery() == null) {
            log.warn("No se encontró callbackQuery.");
            return null;
        }

        MessageBody messageBody = userManager.createMessageBody(bot, update, fileUrl);
        List<ResponseBody> responses = messageRelayService.handleMessage(messageBody);

        if (responses == null || responses.isEmpty()) {
            log.warn("Respuesta vacía para edición de texto.");
            return null;
        }

        List<CustomEditMessageText> messages = new ArrayList<>();

        for (ResponseBody resp : responses) {
            messages.add(
                    responsesBuilder.buildEditMessageText(resp, update.getCallbackQuery().getMessage().getMessageId()));
        }

        mensajeColaService.removeMensajesProcesados(messageBody.getUpdateid());

        return messages;
    }

    public List<CustomEditMessageReplyMarkup> processEditButtons(Update update, ResponseBody responseBody) {
        try {
            return List.of(responsesBuilder.buildEditMessageReplyMarkup(responseBody,
                    update.getCallbackQuery().getMessage().getMessageId(),
                    update.getCallbackQuery().getMessage().getChatId()));
        } catch (Exception e) {
            log.error("Error al editar los botones: ", e);
            return null;
        }
    }

    public List<CustomEditMessageReplyMarkup> processRemoveButtons(Update update, ResponseBody responseBody) {
        try {
            return List.of(responsesBuilder.buildEditMessageReplyMarkup(responseBody,
                    update.getCallbackQuery().getMessage().getMessageId(),
                    update.getCallbackQuery().getMessage().getChatId()));
        } catch (Exception e) {
            log.error("Error al eliminar los botones: ", e);
            return null;
        }
    }
}