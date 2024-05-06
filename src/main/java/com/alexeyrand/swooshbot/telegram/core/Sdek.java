package com.alexeyrand.swooshbot.telegram.core;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.datamodel.service.ChatService;
import com.alexeyrand.swooshbot.datamodel.service.PhotoService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Sdek {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    private final SdekInline sdekInline;
    private final BotConfig config;
    private final QueryHandler queryHandler;
    private final MenuInline menuInline;
    private final MainMenuInline mainMenuInline;

    public void sdek(String message) {
        System.out.println(message);
    }

}
