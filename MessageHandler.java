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
        String answer = config.getHelpCommand();
        InlineKeyboardMarkup inline = menuInline.getMenuInline(chatId);
//        File image = ResourceUtils.getFile("classpath:" + "static/images/menu.jpg");
        File image = new File("/root/swooshbot/SwooshBot/src/main/resources/static/images");
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(inline);

        chatService.updateState(chatId, NO_WAITING);
        telegramBot.justSendPhoto(photo);
    }
}
