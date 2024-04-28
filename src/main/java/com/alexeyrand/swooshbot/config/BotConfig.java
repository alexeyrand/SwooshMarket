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

    @Value("${answer.command.publish}")
    private String publishAnswer;

    @Value("${answer.command.free}")
    private String publishFreeAnswer;

    @Value("${answer.command.paid}")
    private String publishPaidAnswer;

    @Value("${answer.command.check}")
    private String publishCheckPaidAnswer;

}
