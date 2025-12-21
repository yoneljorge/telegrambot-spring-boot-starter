package dev.yonel.telegrambot_spring_boot_starter.bots;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Bots {
    private static List<AbstractTelegramBot> listBots;

    public Bots(List<AbstractTelegramBot> bots){
        listBots = bots;
    }

    public static AbstractTelegramBot getBot(String id){
        for (AbstractTelegramBot bot : listBots){
            if(bot.getBotProps().getId().equalsIgnoreCase(id)){
                return bot;
            }
        }
        return null;
    }
}
