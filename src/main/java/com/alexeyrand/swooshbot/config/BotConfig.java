package com.alexeyrand.swooshbot.config;

import com.alexeyrand.swooshbot.telegram.TelegramBot;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Data
@PropertySource("application.yml")
public class BotConfig {

    @Value("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Value("${answer.command.help}")
    private String helpCommand;

    @Value("${answer.command.sdek}")
    private String sdekAnswer;

    @Value("${answer.command.publish}")
    private String publishAnswer;

    @Value("${answer.command.free1}")
    private String publishFree1Answer;

//    @Bean
//    public TelegramBotsApi telegramBotsApi(TelegramBot bot) throws TelegramApiException {
//        var api = new TelegramBotsApi(DefaultBotSession.class);
//        api.registerBot(bot);
//        return api;
//    }
}
