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

    @Value("${bot.payments_token}")
    private String paymentsToken;

    @Value("${answer.command.help}")
    private String helpCommand;

    @Value("${answer.command.sdek}")
    private String sdekAnswer;

    @Value("${answer.command.sdekOrder}")
    private String sdekOrderAnswer;

    @Value("${answer.command.sdekOrder1}")
    private String sdekOrder1Answer;

    @Value("${answer.command.sdekOrder2}")
    private String sdekOrder2Answer;

    @Value("${answer.command.sdekOrder3}")
    private String sdekOrder3Answer;

    @Value("${answer.command.sdekOrder4}")
    private String sdekOrder4Answer;

    @Value("${answer.command.sdekOrder5}")
    private String sdekOrder5Answer;

    @Value("${answer.command.sdekOrder6}")
    private String sdekOrder6Answer;

    @Value("${answer.command.sdekOrder7}")
    private String sdekOrder7Answer;

    @Value("${answer.command.sdekOrder8}")
    private String sdekOrder8Answer;

    @Value("${answer.command.sdekOrder9}")
    private String sdekOrder9Answer;

    @Value("${answer.command.sdekOrder10}")
    private String sdekOrder10Answer;

    @Value("${answer.command.publish}")
    private String publishAnswer;

    @Value("${answer.command.free}")
    private String publishFreeAnswer;

    @Value("${answer.command.paid}")
    private String publishPaidAnswer;

    @Value("${answer.command.check}")
    private String publishCheckPaidAnswer;

}
