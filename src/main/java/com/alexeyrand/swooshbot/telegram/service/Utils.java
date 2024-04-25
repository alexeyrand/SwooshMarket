package com.alexeyrand.swooshbot.telegram.service;


import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.PublishFreeInline;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class Utils {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    private final PublishFreeInline publishFreeInline;

    public void isBlank(String text, Long chatId) {
        if (text == null || text.isEmpty()) {
            SendMessage message = new SendMessage();
            message.setText("Объявление не может быть пустым! Заполните информацию о товарах.");
            message.setChatId(chatId);

            telegramBot.justSendMessage(message);
            TelegramBot.flag = false;
            TelegramBot.wait = false;
        }
    }
}
