package com.alexeyrand.swooshbot.telegram.service;

import com.alexeyrand.swooshbot.telegram.TelegramBot;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    @SneakyThrows
    public void ready(Long chatId) {
        Thread.sleep(5000);
        TelegramBot.ready = true;
        //telegramBot.ready(chatId);

    }
}
