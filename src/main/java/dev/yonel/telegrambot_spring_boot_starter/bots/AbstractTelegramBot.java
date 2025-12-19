package dev.yonel.telegrambot_spring_boot_starter.bots;

import dev.yonel.telegrambot_spring_boot_starter.messages.models.ResponseBody;
import dev.yonel.telegrambot_spring_boot_starter.services.BaseTelegramWebhookBot;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public abstract class AbstractTelegramBot extends BaseTelegramWebhookBot {

    public AbstractTelegramBot(){
        super();
    }

    public void handleMessage(List<ResponseBody> responses){
        try {
            List<Object> objectsResponses = convertToObject(telegramPlatform.onReceivedEventFromMessageRelay(responses));
            handleResponse(objectsResponses, botProps);
        }catch (Exception e){
            log.error("Error al manejar handleMessage: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hook de inicialización del bot
     */
    protected void onStart(){

    }
}
