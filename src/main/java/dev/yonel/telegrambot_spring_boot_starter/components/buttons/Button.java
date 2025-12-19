package dev.yonel.telegrambot_spring_boot_starter.components.buttons;

import dev.yonel.telegrambot_spring_boot_starter.types.TypeCustomButton;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Button {
    private String text;
    private String url;
    private String callbackData;
    private TypeCustomButton typeButton;
}
