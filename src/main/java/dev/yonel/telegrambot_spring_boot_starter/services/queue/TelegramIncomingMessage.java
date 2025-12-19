
package dev.yonel.telegrambot_spring_boot_starter.services.queue;

import dev.yonel.telegrambot_spring_boot_starter.bots.AbstractTelegramBot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TelegramIncomingMessage<T extends AbstractTelegramBot> {

	private String userid;
	private int updateid;
	private Update updateMessage;
    private T bot;

	public TelegramIncomingMessage(Update update, T bot) {
		this.updateMessage = update;
        this.bot = bot;
		loadData();
	}

	private void loadData() {
		this.updateid = updateMessage.getUpdateId();
		if (updateMessage.hasCallbackQuery()) {
			this.userid = updateMessage.getCallbackQuery().getId();
		} else {
			this.userid = String.valueOf(updateMessage.getMessage().getChat().getId());
		}
	}
}
