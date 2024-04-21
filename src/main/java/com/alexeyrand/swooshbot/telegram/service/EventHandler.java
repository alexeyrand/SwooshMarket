package com.alexeyrand.swooshbot.telegram.service;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.MenuInline;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;

@Component
@RequiredArgsConstructor
public class EventHandler {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    private final MessageSender messageSender;
    private final MenuInline menuInline;
    private final BotConfig config;

    @SneakyThrows
    public void StartCommandReceived(String chatId, Integer messageId) {
        String answer = config.getHelpCommand();
        InlineKeyboardMarkup inline = menuInline.getMenuInline();
        File image = ResourceUtils.getFile("classpath:" + "static/images/menu.jpg");

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(inline);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        telegramBot.sendPhoto(photo, deleteMessage);

    }
}
