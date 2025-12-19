package dev.yonel.telegrambot_spring_boot_starter.messages.models;

import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.types.TypeMessage;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageBody {
    /*
     * Id del chat, puede ser un grupo o el chat como tal del usuario.
     */
    private Long chatid;
    /*
     * Id del mensaje
     */
    private Integer messageId;
    /*
     * Id del usuario
     */
    private Long userid;
    /*
     * Id del update que se está recibiendo
     */
    private int updateid;
    private String firstName;
    private String lastName;
    private String userName;
    private String message;
    private String telefono;
    @Builder.Default
    private boolean group = false;
    @Builder.Default
    private String groupTitle = "";
    @Builder.Default
    private boolean newUserInGroup = false;
    @Builder.Default
    private boolean isBot = false;
    private String callbackData;
    private TypeMessage typeMessage;
    private byte[] image;
    private String fileUrl;
    private TelegramBot telegramBot;

    public String getAlias() {

        if (this.userName != null && !this.userName.trim().isEmpty()) {
            return this.userName;
        }

        if (this.firstName != null && !this.firstName.trim().isEmpty()) {
            return this.firstName;
        }

        if (this.lastName != null && !this.firstName.trim().isEmpty()) {
            return this.lastName;
        }

        return "";
    }
}
