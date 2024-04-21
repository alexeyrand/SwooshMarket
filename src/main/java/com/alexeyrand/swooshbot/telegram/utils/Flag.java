package com.alexeyrand.swooshbot.telegram.utils;

import com.alexeyrand.swooshbot.telegram.TelegramBot;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class Flag implements Runnable {


    private TelegramBot telegramBot;

    public Flag(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    @Setter
    private Long chatId;
    @Setter
    private Long chatIdChannel;
    @Setter
    private String text;

    @Override
    public void run() {
        try {

            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        telegramBot.ready(chatId, text);
        System.out.println(text);
        TelegramBot.flag = true;
        SendMessage message = new SendMessage();

        message.setChatId(chatIdChannel);
        message.setText("Готово! Публикация отправлена на модерацию.");
        telegramBot.justSendMessage(message);

    }
}
