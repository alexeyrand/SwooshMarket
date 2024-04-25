package com.alexeyrand.swooshbot.telegram.utils;

import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.MainMenuInline;
import com.alexeyrand.swooshbot.telegram.inline.MenuInline;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;

@Component
public class Flag implements Runnable {


    private final TelegramBot telegramBot;
    private final Utils utils;
    private final QueryHandler queryHandler;
    private final MenuInline menuInline;
    private final MainMenuInline mainMenuInline;

    public Flag(TelegramBot telegramBot, Utils utils, QueryHandler queryHandler, MenuInline menuInline, MainMenuInline mainMenuInline) {
        this.telegramBot = telegramBot;
        this.utils = utils;
        this.queryHandler = queryHandler;
        this.menuInline = menuInline;
        this.mainMenuInline = mainMenuInline;
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
    @Setter
    private Message message;

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (text == null || text.isBlank()) {

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Объявление не может быть пустым! Заполните информацию о товарах.");
            sendMessage.setChatId(chatId);
            telegramBot.medias.clear();
            telegramBot.inputsMedia.clear();
            telegramBot.justSendMessage(sendMessage);
            queryHandler.publishFreeReceived(chatId.toString(), -1);
            TelegramBot.flag = true;
            TelegramBot.wait = true;
        } else {

            if (telegramBot.medias.size() > 1) {
                telegramBot.publishAlbum(chatIdChannel, text, username);
                TelegramBot.flag = true;
            } else if (telegramBot.medias.size() == 1) {
                telegramBot.justSendPhoto(sendPhoto);
                TelegramBot.flag = true;
            }
            SendMessage message = new SendMessage();

            message.setChatId(chatId);
            message.setReplyMarkup(mainMenuInline.getMenuInline());
            message.setText("Готово! Публикация отправлена на модерацию.");
            telegramBot.justSendMessage(message);
        }
    }
}
