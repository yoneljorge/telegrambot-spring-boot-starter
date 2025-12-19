package dev.yonel.telegrambot_spring_boot_starter.bots.controller;

import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.services.queue.MessageQueueService;
import dev.yonel.telegrambot_spring_boot_starter.services.queue.TelegramIncomingMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${telegram.path:/telegram}")
// FIXME: poner para que se habilite si el usuario lo habilita en el .yaml
public class TelegramWebhookController {

    private final List<AbstractTelegramBot> bots;
    private final MessageQueueService messageQueueService;

    public TelegramWebhookController(
            @Lazy List<AbstractTelegramBot> bots,
            MessageQueueService messageQueueService){
        this.bots = bots;
        this.messageQueueService = messageQueueService;
    }

    @PostMapping("{secret}")
    public ResponseEntity<?> onUpdateReceived(
            @PathVariable String secret,
            @RequestBody Update update){
       for(AbstractTelegramBot bot : bots){
           if(secret.equals(bot.getBotToken())){
                try{
                    messageQueueService.queueMessage(new TelegramIncomingMessage<AbstractTelegramBot>(update, bot));
                    return ResponseEntity.ok(null);
                }catch (Exception e){
                    log.error("Error al procesar la actualización: {}", e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
           }
       }
       return ResponseEntity.ok(null);
    }
}
