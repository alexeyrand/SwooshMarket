package com.alexeyrand.swooshbot.telegram;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.telegram.inline.PublishInline;
import com.alexeyrand.swooshbot.telegram.inline.SdekInline;
import com.alexeyrand.swooshbot.telegram.service.MessageHandler;
import com.alexeyrand.swooshbot.telegram.service.MessageSender;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import com.alexeyrand.swooshbot.telegram.utils.Flag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final MessageSender messageSender;
    private final MessageHandler messageHandler;
    private final Utils utils;
    private final SdekInline sdekInline;
    private final PublishInline publishInline;
    private final QueryHandler queryHandler;
    private boolean wait = false;
    public static boolean ready = false;
    public static boolean flag = true;

    public List<String> medias;
    List<InputMedia> inputsMedia;

    public TelegramBot(@Value("${bot.token}") String botToken,
                       BotConfig config, MessageSender messageSender, MessageHandler messageHandler,
                       SdekInline sdekInline,
                       PublishInline publishInline,
                       QueryHandler queryHandler,
                       Utils utils) throws TelegramApiException {
        super(botToken);
        this.config = config;
        this.messageSender = messageSender;
        this.messageHandler = messageHandler;
        this.sdekInline = sdekInline;
        this.publishInline = publishInline;
        this.queryHandler = queryHandler;
        this.utils = utils;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Меню"));
        this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), "ru"));
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Integer messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String messageText = message.getText();
            System.out.println(messageText);

            if (wait) {
                System.out.println("Жду сообщение");

                wait = false;
            } else {
                switch (messageText) {
//                    case "ready" -> ready(chatId);
                    case "/start" ->
                            messageHandler.StartCommandReceived(chatId, messageId);//, InlineKeyboardMarkup inline);
                    default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            Message message = query.getMessage();

            Integer messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String data = query.getData();
            switch (data) {

                case "publish" -> queryHandler.publishReceived(chatId, messageId);
//                case "legit" -> sendMessageWithInlineSdek(chatId);
                case "sdek" -> queryHandler.sdekReceived(chatId, messageId);
//                case "garant" -> sendMessageWithInlineSdek(chatId);
//                case "adv" -> sendMessageWithInlineSdek(chatId);

                case "publish/free" -> queryHandler.publishFree1Received(chatId, messageId);
                case "publish/free/1" -> queryHandler.publishFree1Received(chatId, messageId);
//                case "sdek/order" -> sendMessageWithInlineSdek(chatId);

                default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {

            Message message = update.getMessage();
            String text = message.getCaption();
            Long chatId = message.getChatId();
            String photo = message.getPhoto().get(0).getFileId();
            String username = message.getChat().getUserName();

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(new InputFile(photo));
            sendPhoto.setChatId(-1002141489384L);
            sendPhoto.setCaption(text);

            medias.add(photo);
            if (flag) {
                flag = false;
                Flag flag = new Flag(this);
                flag.setChatIdChannel(-1002141489384L);
                flag.setChatId(chatId);
                flag.setText(text);
                flag.setSendPhoto(sendPhoto);
                flag.setUsername(username);
                Thread thread = new Thread(flag);
                thread.start();
            }
            wait = false;
        }

    }

    @SneakyThrows
    public void sendPhoto(SendPhoto photo, DeleteMessage deleteMessage) {

        try {
            execute(deleteMessage);
            execute(photo);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void justSendPhoto(SendPhoto photo) {

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void sendMessage(SendMessage message, DeleteMessage deleteMessage) {

        try {
            execute(message);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void justSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void sendMessageAndWait(SendMessage message, DeleteMessage deleteMessage) {
        wait = true;
        medias = new ArrayList<>();
        inputsMedia = new ArrayList<>();
        try {
            execute(message);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }


    @SneakyThrows
    public void publishAlbum(Long chatId, String text, String username) {
        ready = true;

        SendMediaGroup mediaGroup = new SendMediaGroup();
        for (String s : medias) {
            InputMedia photo = new InputMediaPhoto();
            photo.setMedia(s);
            if (medias.get(0).equals(s)) {
                photo.setCaption(text + "\n\nПродавец - " + username);
            }
            inputsMedia.add(photo);
        }
        mediaGroup.setMedias(inputsMedia);
        mediaGroup.setChatId(chatId);

        if (medias.size() > 1) {
            execute(mediaGroup);
        } else {

        }
    }
}
