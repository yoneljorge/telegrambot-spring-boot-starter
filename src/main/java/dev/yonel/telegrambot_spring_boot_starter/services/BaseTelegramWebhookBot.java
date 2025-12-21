package dev.yonel.telegrambot_spring_boot_starter.services;

import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.components.DeleteMessageEventComponent;
import dev.yonel.telegrambot_spring_boot_starter.components.EmptyResponse;
import dev.yonel.telegrambot_spring_boot_starter.components.TelegramBotCustomOptions;
import dev.yonel.telegrambot_spring_boot_starter.components.TelegramPlatform;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomEditMessageReplyMarkup;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomEditMessageText;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomSendDocument;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomSendMessage;
import dev.yonel.telegrambot_spring_boot_starter.context.SessionManager;
import dev.yonel.telegrambot_spring_boot_starter.context.UserSessionContext;
import dev.yonel.telegrambot_spring_boot_starter.events.DeleteMessageEvent;
import dev.yonel.telegrambot_spring_boot_starter.messages.models.ResponseBody;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class BaseTelegramWebhookBot extends TelegramWebhookBot implements MessageDeletionHandler {

    @Autowired
    protected TelegramPlatform telegramPlatform;

    @Autowired
    private DeleteMessageEventComponent deleteMessageEventComponent;

    @Getter @Setter
    protected TelegramBot botProps;

    /**
     * Usamos el constructor deprecado para permitir que los hijos
     * no tengan que declarar constructores.
     */
    @SuppressWarnings("deprecation")
    public BaseTelegramWebhookBot() {
        super(new TelegramBotCustomOptions());
    }

    @Override
    public String getBotPath() {
        return botProps.getPath();
    }

    @Override
    public String getBotUsername() {return botProps.getUsername();}

    /**
     * Usamos el metodo deprecado para permitir que los hijos
     * no tengan que declarar constructores.
     */
    @SuppressWarnings("deprecation")
    @Override
    public String getBotToken(){return botProps.getToken(); }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            try {
                handleMessageUpdate(update, this.botProps);
            } catch (Throwable e) {
                log.error("Error en handleMessageUpdate: {}", e.getMessage());
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQueryUpdate(update, this.botProps);
        }
        return new EmptyResponse();
    }

    private void handleMessageUpdate(Update update, TelegramBot bot) throws Throwable {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();
        String chatType = update.getMessage().getChat().getType();

        log.debug("Mensaje recibido del chat ID: {}, tipo: {}, usuario ID: {}", chatId, chatType,
                userId);
        processPrivateMessage(update, bot);

    }

    private void processPrivateMessage(Update update, TelegramBot bot) throws Throwable {

        String messageText = update.getMessage().getText();

        if (update.getMessage().hasPhoto()) {
            try {
                List<Object> response = convertToObject(telegramPlatform.receivedRequestMessageFromPrivate(bot, update, getFileUrl(update)));
                handleResponse(response, bot);
                return;
            } catch (Exception e) {
                log.error("Error en telegramPlatform.receivedImageMessageFromPrivate: {}", e.getMessage());
            }
        }

        if (messageText != null) {
            try {
                List<Object> response = convertToObject(
                        telegramPlatform.receivedRequestMessageFromPrivate(bot, update, null));
                handleResponse(response, bot);
                return;
            } catch (Throwable e) {
                log.error("Error en telegramPlatform.receivedFromMessage (privado): {}", e.getMessage(), e);
                handleResponse(null, bot);
                e.printStackTrace();
                return;
            }
        }
    }

    private void handleCallbackQueryUpdate(Update update, TelegramBot bot) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        Long chatId = null;
        String chatType = null;

        if (update.getCallbackQuery().getMessage() != null) {
            Message message = (Message) update.getCallbackQuery().getMessage();
            chatId = message.getChatId();
            chatType = message.getChat().getType();
        } else if (update.getCallbackQuery().getChatInstance() != null) {
            /*
             * Esto puede indicar que el callback se originó de un mensaje inline
             * En este caso, no hay un 'message' asociado directamente
             * 
             * El 'chatInstance' podría ser útil en algunos contextos, pero no proporciona
             * el tipo de chat de la misma manera.
             */
            log.warn("CallbackQuery sin mensaje acociado directamente (chatInstance: {})",
                    update.getCallbackQuery().getChatInstance());
            /*
             * Dependiendo de la lógica, se pudiera necesitar manejar esto de forma
             * diferente.
             * Por ejemplo, podrías no tener el tipo de chat disponible aquí.
             */
            return; // No se puede obtener la información del chat
        }
        try {
            handleResponse(convertToObject(telegramPlatform.removeButtons(update, new ResponseBody())), bot);
            handleResponse(convertToObject(telegramPlatform.receivedRequestMessageFromPrivate(bot, update, null)), bot);
        } catch (TelegramApiException e) {
            log.error("Error al procesar CallbackQuery: {}", e.getMessage(), e);
            e.printStackTrace();
        } catch (Throwable e) {
            log.error("Error en telegramPlatform.receivedFromButtons (callback): {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    protected void handleResponse(List<Object> responses, TelegramBot bot) {
        if (responses == null || responses.isEmpty()) {
            returnResponseEmpty();
            return;
        }

        /**
         * Lista en la cual vamos a agregar los mensajes enviados que tiene que ser
         * eliminados.
         */
        List<Message> returnedMessages = new ArrayList<>();

        for (Object response : responses) {

            switch (response) {
                case CustomSendMessage sendMessage -> {
                    try {
                        deleteMessageEventComponent.deleteMessages(
                                sendMessage.getChatId(),
                               SessionManager.getContext(Long.parseLong(sendMessage.getChatId())),
                                bot);
                        if (sendMessage.isRemovable()) {
                            Message message = execute(sendMessage);
                            returnedMessages.add(message);
                        } else {
                            execute(sendMessage);
                        }

                    } catch (TelegramApiException e) {
                        log.error("Error enviando SendMessage: {}", e.getMessage());
                    }
                }
                case CustomEditMessageText editMessageText -> {
                    try {
                        deleteMessageEventComponent.deleteMessages(
                                editMessageText.getChatId(),
                                SessionManager.getContext(Long.parseLong(editMessageText.getChatId())),
                                bot);
                        execute(editMessageText);
                    } catch (TelegramApiException e) {
                        log.error("Error enviando EditMessageText: {}", e.getMessage());
                    }
                }
                case CustomEditMessageReplyMarkup editMessageReplyMarkup -> {
                    try {
                        deleteMessageEventComponent.deleteMessages(
                                editMessageReplyMarkup.getChatId(),
                                SessionManager.getContext(Long.parseLong(editMessageReplyMarkup.getChatId())),
                                bot);
                        execute(editMessageReplyMarkup);
                    } catch (TelegramApiException e) {
                        log.error("Error enviando EditMessageReplyMarkup: {}", e.getMessage());
                    }
                }

                case CustomSendDocument sendDocument -> {
                    try{
                        deleteMessageEventComponent.deleteMessages(
                                sendDocument.getChatId(),
                                SessionManager.getContext(Long.parseLong(sendDocument.getChatId())),
                                bot);
                        execute(sendDocument);
                    }catch(TelegramApiException e){
                        log.error("Error enviando CustomSendDocument: {}", e.getMessage());
                    }
                }

                case SendDocument sendDocument -> {
                    try {
                        deleteMessageEventComponent.deleteMessages(
                                sendDocument.getChatId(),
                                SessionManager.getContext(Long.parseLong(sendDocument.getChatId())),
                                bot);
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        log.error("Error enviando SendDocument: {}", e.getMessage());
                    }
                }
                
                case null -> {
                    returnResponseEmpty();
                }
                default -> {
                    log.warn("Tipo de respuesta no soportado: {}", response.getClass().getName());
                }
            }
        }

        if (!returnedMessages.isEmpty()) {
            for (Message message : returnedMessages) {
                Long userId = message.getChatId();
                int messageId = message.getMessageId();

                UserSessionContext context = SessionManager.getContext(userId);
                context.getBotSession(bot).setMessageIdToDelete(messageId);
            }
        }
    }

    public void returnResponseEmpty() {
        try {
            execute(new EmptyResponse());
        } catch (Exception e) {
            log.error("Hubo un error enviando un mensaje vacío: {}", e.getMessage());
        }
    }

    private String getFileUrl(Update update) {
        try {
            PhotoSize largestPhoto = update.getMessage().getPhoto().stream()
                    .max(Comparator.comparing(PhotoSize::getFileSize))
                    .orElse(null);

            if (largestPhoto != null) {
                GetFile getFile = new GetFile();
                getFile.setFileId(largestPhoto.getFileId());
                File file = execute(getFile);

                if (file != null && file.getFilePath() != null) {
                    return "https://api.telegram.org/file/bot" + botProps.getToken() + "/" + file.getFilePath();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (TelegramApiException e) {
            log.error("Error al interactuar con la API de Telegram para obtener la imagen: {}", e.getMessage());
            throw new RuntimeException("Error al procesar la imagen.");
        } catch (Throwable e) {
            log.error("Error al procesar la imagen con Gemini: {}", e.getMessage());
            throw new RuntimeException("Error al analizar la imagen.");
        }
    }

    protected <T> List<Object> convertToObject(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<Object> objectList = new ArrayList<>();
        for (T item : list) {
            objectList.add(item);
        }
        return objectList;
    }

    @Async
    @EventListener
    public void handleDeleteMessage(DeleteMessageEvent event) {

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(event.getChatId());
        deleteMessage.setMessageId(event.getMessageId());

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("error eliminando mensaje marcado para eliminar");
        }
    }
}