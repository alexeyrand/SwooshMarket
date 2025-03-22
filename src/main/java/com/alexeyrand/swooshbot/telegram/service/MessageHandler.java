package com.alexeyrand.swooshbot.telegram.service;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.api.service.ChatService;
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
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;

@Component
@RequiredArgsConstructor
public class MessageHandler {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    private final ChatService chatService;
    private final MessageSender messageSender;
    private final MenuInline menuInline;
    private final BotConfig config;

    @SneakyThrows
    public void StartCommandReceived(Long chatId, Integer messageId) {

        Path path = Paths.get("C:\\Java projects\\SwooshMarket\\src\\main\\resources\\text\\menu\\menu.txt");
        String answer = Files.readString(path);
        InlineKeyboardMarkup inline = menuInline.getMenuInline(chatId);
        File image = ResourceUtils.getFile("classpath:" + "static/images/menu.jpg");

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setCaption(answer);
        photo.setReplyMarkup(inline);

        chatService.updateState(chatId, NO_WAITING);
        telegramBot.justSendPhoto(photo);
    }
}
