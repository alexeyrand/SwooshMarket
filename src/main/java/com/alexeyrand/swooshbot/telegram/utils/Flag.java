package com.alexeyrand.swooshbot.telegram.utils;

import com.alexeyrand.swooshbot.telegram.TelegramBot;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

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
    @Setter
    private SendPhoto sendPhoto;
    @Setter
    private String username;

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (telegramBot.medias.size() > 1) {
            telegramBot.publishAlbum(chatIdChannel, text, username);
            TelegramBot.flag = true;
        } else if (telegramBot.medias.size() == 1) {
            telegramBot.justSendPhoto(sendPhoto);
            TelegramBot.flag = true;
        }
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Готово! Публикация отправлена на модерацию.");
        telegramBot.justSendMessage(message);

    }
}
