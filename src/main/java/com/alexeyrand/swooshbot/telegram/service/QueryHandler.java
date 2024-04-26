package com.alexeyrand.swooshbot.telegram.service;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.datamodel.service.ChatService.ChatService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.enums.State;
import com.alexeyrand.swooshbot.telegram.inline.PublishFreeInline;
import com.alexeyrand.swooshbot.telegram.inline.PublishInline;
import com.alexeyrand.swooshbot.telegram.inline.SdekInline;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;

@Component
@RequiredArgsConstructor
public class QueryHandler {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    private final SdekInline sdekInline;
    private final PublishInline publishInline;
    private final PublishFreeInline publishFreeInline;
    private final BotConfig config;
    private final Utils utils;
    private final ChatService chatService;

    @SneakyThrows
    public void publishReceived(Long chadId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/publish.jpg");
        String answer = config.getPublishAnswer();
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chadId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(publishInline.getPublishInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chadId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chadId, NO_WAITING);

        telegramBot.sendPhoto(photo, deleteMessage);
    }

    @SneakyThrows
    public void publishFreeReceived(Long chatId, Integer messageId) {

        String answer = config.getPublishFree1Answer();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWN);
        message.setText(answer);
        message.setReplyMarkup(publishFreeInline.getPublishFreeInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        TelegramBot.flag = true;
        telegramBot.sendMessageAndWait(message, deleteMessage);
    }

    @SneakyThrows
    public void publishFree1Received(String chatId, Integer messageId) {

        //telegramBot.inputsMedia = new ArrayList<>();

        String answer = config.getPublishFree1Answer();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWN);
        message.setText(answer);
        message.setReplyMarkup(publishFreeInline.getPublishFreeInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        TelegramBot.flag = true;
        telegramBot.sendMessageAndWait(message, deleteMessage);
    }



    @SneakyThrows
    public void sdekReceived(Long chatId, Integer messageId) {

        File image = ResourceUtils.getFile("classpath:" + "static/images/sdek.jpg");
        InlineKeyboardMarkup inline = sdekInline.getSdekInline();
        String answer = config.getSdekAnswer();
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
