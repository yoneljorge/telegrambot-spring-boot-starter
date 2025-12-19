package dev.yonel.telegrambot_spring_boot_starter.messages;

import dev.yonel.telegrambot_spring_boot_starter.messages.models.MessageBody;
import dev.yonel.telegrambot_spring_boot_starter.messages.models.ResponseBody;

import java.util.List;

public interface MessageRelayService {
    List<ResponseBody> handleMessage(MessageBody messageBody) throws Throwable;
}
