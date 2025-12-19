package dev.yonel.telegrambot_spring_boot_starter.messages.models;

import dev.yonel.telegrambot_spring_boot_starter.components.buttons.Button;
import dev.yonel.telegrambot_spring_boot_starter.types.TypeCustomKeyboardMarkup;
import dev.yonel.telegrambot_spring_boot_starter.types.TypeParseMode;
import dev.yonel.telegrambot_spring_boot_starter.types.TypeSendExecution;
import lombok.*;

import java.io.File;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseBody {

    private Integer messageId;
    private Long chatid;
    private Long userid;
    private String response;
    private int updateid;
    private List<Button> buttons;
    @Builder.Default
    private int rows = 0;
    @Builder.Default
    private boolean removable = false;
    private String fileDocument;
    private File file;

    private TypeParseMode parseMode;
    private TypeSendExecution typeSendExecution;
    private TypeCustomKeyboardMarkup typeKeyboard;
}