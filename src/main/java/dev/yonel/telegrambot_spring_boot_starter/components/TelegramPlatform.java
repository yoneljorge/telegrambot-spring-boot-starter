
package dev.yonel.telegrambot_spring_boot_starter.components;

import dev.yonel.telegrambot_spring_boot_starter.bots.core.TelegramBot;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomEditMessageReplyMarkup;
import dev.yonel.telegrambot_spring_boot_starter.components.custom.CustomEditMessageText;
import dev.yonel.telegrambot_spring_boot_starter.messages.models.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

@Lazy
@Component
public class TelegramPlatform {

	@Autowired
	private TelegramMessageProcessor messageProcessor;

	@Autowired
	private TelegramButtonProcessor buttonProcessor;

	@Autowired
	private TelegramUserManager userManager;

	public List<Object> receivedRequestMessageFromPrivate(
            TelegramBot bot,
            Object updateObject,
			String fileUrl) throws Throwable {
		try {
			return messageProcessor.processRequestMessage(bot, (Update) updateObject, fileUrl);
		} catch (Exception e) {
			e.printStackTrace();
            return null;
		}
	}


	/*
	public void receivedMessageFromGroup() {

	}

	public void onNewUserJoinOnGroup(List<User> newUsers,TelegramBot bot) {
		userManager.handleNewGroupUsers(newUsers, bot);
	}
	 */

    /*FIXME: Cuando un usuario sale del grupo */
	public void onUserLeftOnGroup(User user) {

	}

	public List<Object> onReceivedEventFromMessageRelay(List<ResponseBody> responses) {
		try {
			return messageProcessor.processResponses(responses);
		} catch (Exception e) {
			return null;
		}
	}


	public List<CustomEditMessageText> editMessageText(TelegramBot bot, Object updateObject, String fileUrl) throws Throwable {
		try {
			return buttonProcessor.processEditMessageText(bot, (Update) updateObject, fileUrl);
		} catch (Exception e) {
			return null;
		}
	}

	public List<CustomEditMessageReplyMarkup> editButtons(Object updateObject, Object responseBody) {
		try {
			return buttonProcessor.processEditButtons((Update) updateObject, (ResponseBody) responseBody);
		} catch (Exception e) {
			return null;
		}
	}

	public List<CustomEditMessageReplyMarkup> removeButtons(Object updateObject, Object responseBody) {
		try {
			return buttonProcessor.processRemoveButtons((Update) updateObject, (ResponseBody) responseBody);
		} catch (Exception e) {
			return null;
		}
	}
}