package dev.yonel.telegrambot_spring_boot_starter.messages;

import dev.yonel.telegrambot_spring_boot_starter.messages.models.MessageBody;
import dev.yonel.telegrambot_spring_boot_starter.messages.models.ResponseBody;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultMessageRelayServiceImpl implements MessageRelayService{

    @Override
    public List<ResponseBody> handleMessage(MessageBody messageBody) throws Throwable {
        log.warn("MessageRelayService no ha sido implementado por el usuario. El mensaje no será procesado.");
        return new ArrayList<>();
    }
}
