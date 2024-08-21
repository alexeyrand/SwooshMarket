package com.alexeyrand.swooshbot.telegram.core;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.model.entity.publish.Photo;
import com.alexeyrand.swooshbot.api.service.ChatService;
import com.alexeyrand.swooshbot.api.service.PhotoService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import com.alexeyrand.swooshbot.telegram.utils.Flag;
import com.alexeyrand.swooshbot.telegram.utils.PaidPublishThread;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class Publish {

    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    private final SdekInline sdekInline;
    private final PublishInline publishInline;
    private final PublishFreeInline publishFreeInline;
    private final BotConfig config;
    private final Utils utils;
    private final ChatService chatService;
    private final PhotoService photoService;
    private final PublishPaidInline publishPaidInline;
    private final QueryHandler queryHandler;
    private final MenuInline menuInline;
    private final MainMenuInline mainMenuInline;

    public void PublishFree(Message message, Long chatId) {
        String text = message.getCaption();
        String chanel = chatService.getChanel(chatId);
        Long chanelId = chanel.equals("chanel") ? -1002141489384L : -1002141489384L;

        if (!message.hasPhoto()) {
            SendMessage responseMessage = new SendMessage();
            responseMessage.setChatId(chatId);
            responseMessage.setText("Объявление не может быть без фотографий! Прикрепите фотографии товаров.");
            telegramBot.justSendMessage(responseMessage);
            queryHandler.publishFreeReceived(chatId, -1);

        } else {
            String photo = message.getPhoto().get(0).getFileId();
            String username = message.getChat().getUserName();

            photoService.save(Photo.builder().chatId(message.getChatId()).photo(photo).build());

            if (chatService.isBlock(chatId)) {
                chatService.updateBlock(chatId, false);
                Flag flag = new Flag(telegramBot, utils, queryHandler, menuInline, mainMenuInline, chatService, photoService);
                flag.setChatIdChannel(chanelId);
                flag.setChatId(chatId);
                flag.setText(text);
                flag.setUsername(username);
                flag.setMessage(message);
                Thread thread = new Thread(flag);
                thread.start();
            }
        }
    }

    public void PublishPaid(Message message, Long chatId) {
        String text = message.getCaption();
        if (!message.hasPhoto()) {
            SendMessage responseMessage = new SendMessage();
            responseMessage.setChatId(chatId);
            responseMessage.setText("Объявление не может быть без фотографий! Прикрепите фотографии товаров.");
            telegramBot.justSendMessage(responseMessage);
            queryHandler.publishPaidReceived(chatId, -1);

        } else {
            String photo = message.getPhoto().get(0).getFileId();
            String username = message.getChat().getUserName();

            photoService.save(Photo.builder().chatId(message.getChatId()).photo(photo).build());

            if (chatService.isBlock(chatId)) {
                chatService.updateBlock(chatId, false);
                PaidPublishThread paidPublishThread = new PaidPublishThread(telegramBot, utils, queryHandler, menuInline, mainMenuInline, chatService, photoService);
                paidPublishThread.setChatIdChannel(-1002141489384L);
                paidPublishThread.setChatId(chatId);
                paidPublishThread.setText(text);
                paidPublishThread.setUsername(username);
                paidPublishThread.setMessage(message);
                Thread thread = new Thread(paidPublishThread);
                thread.start();
            }
        }
    }
}
