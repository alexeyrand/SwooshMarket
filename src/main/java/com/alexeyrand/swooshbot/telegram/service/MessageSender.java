package com.alexeyrand.swooshbot.telegram.service;

import com.alexeyrand.swooshbot.telegram.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;

@Component
public class MessageSender {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    boolean wait = false;

    public void sendMessage(Long chatId, String answer) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(answer);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
    @SneakyThrows
    public void sendMessageWithInline(String chatId, String answer, InlineKeyboardMarkup inline) {

        SendPhoto photo = new SendPhoto();
        File image = ResourceUtils.getFile("classpath:" + "static/images/menu.jpg");
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));

        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(inline);


        try {
            telegramBot.execute(photo);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }

    }

}
