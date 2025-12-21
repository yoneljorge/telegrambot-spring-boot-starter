
package dev.yonel.telegrambot_spring_boot_starter.services.queue;
import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Lazy
@Slf4j
@Service
public class MessageQueueService {

    private static final ConcurrentLinkedQueue<TelegramIncomingMessage<AbstractTelegramBot>> colaDeMensajes = new ConcurrentLinkedQueue<>();
    private static final ConcurrentHashMap<Integer, Boolean> mensajesProcesados = new ConcurrentHashMap<>();

    // FIXME: Agregar funcionalidad para procesar stickers
    public void queueMessage(TelegramIncomingMessage<AbstractTelegramBot> message) {
        String text;
        if (message.getUpdateMessage().getMessage() != null) {
            text = message.getUpdateMessage().getMessage().getText();
        } else {
            text = message.getUpdateMessage().getCallbackQuery().getData();
        }

        if (text != null) {
            //FIXME quitar esto en produccion
            log.debug("Nuevo message: {}", text);
            if (!mensajesProcesados.containsKey(message.getUpdateid())) {

                //FIXME quitar esto en produccion
                log.debug("Mensaje encolado: {}", text);
                colaDeMensajes.offer(message);
                mensajesProcesados.put(message.getUpdateid(), true);
                procesarMensajesEnCola(); // Iniciar el procesamiento si es necesario
            } else {
                log.debug("Mensaje no encolado: {}", text);
            }
        }
    }

    @Async
    public void procesarMensajesEnCola() {
        TelegramIncomingMessage<AbstractTelegramBot> message;
        while ((message = colaDeMensajes.poll()) != null) {
            try {
                AbstractTelegramBot botInstance = message.getBot();
                botInstance.onWebhookUpdateReceived(message.getUpdateMessage());
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                // Considerar reenviar a una cola de errores local si es necesario
                log.error("Error al procesar el mensaje: {} - {}", message.getUpdateid(), e.getMessage());
            }
        }
    }


    public void removeMensajesProcesados(int updateid) {
        try {
            mensajesProcesados.remove(updateid);
        } catch (Exception e) {
            log.error("Error eliminando mensaje procesado.");
        }
    }
}
