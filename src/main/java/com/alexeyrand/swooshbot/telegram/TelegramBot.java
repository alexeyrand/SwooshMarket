package com.alexeyrand.swooshbot.telegram;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.telegram.inline.PublishInline;
import com.alexeyrand.swooshbot.telegram.inline.SdekInline;
import com.alexeyrand.swooshbot.telegram.service.EventHandler;
import com.alexeyrand.swooshbot.telegram.service.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final MessageSender messageSender;
    private final EventHandler eventHandler;
    private final SdekInline sdekInline;
    private final PublishInline publishInline;
    private boolean wait = false;


    public TelegramBot(@Value("${bot.token}") String botToken,
                       BotConfig config, MessageSender messageSender, EventHandler eventHandler,
                       SdekInline sdekInline,
                       PublishInline publishInline) throws TelegramApiException {
        super(botToken);
        this.config = config;
        this.messageSender = messageSender;
        this.eventHandler = eventHandler;
        this.sdekInline = sdekInline;
        this.publishInline = publishInline;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Меню"));
        this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), "ru"));
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }

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
                    case "/start" -> eventHandler.StartCommandReceived(chatId, messageId);//, InlineKeyboardMarkup inline);
                    default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery query = update.getCallbackQuery();
            Message message = query.getMessage();
            String s = message.getText();
            Integer i = message.getDate();
            String c = message.getCaption();

            Integer messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String data = query.getData();

            switch (data) {
                case "publish" -> sendMessageWithInlinePublish(chatId, messageId);
//                case "legit" -> sendMessageWithInlineSdek(chatId);
                case "sdek" -> sendMessageWithInlineSdek(chatId, messageId);
//                case "garant" -> sendMessageWithInlineSdek(chatId);
//                case "adv" -> sendMessageWithInlineSdek(chatId);

                case "publish/free" -> sendMessage(chatId,  messageId);
//                case "sdek/order" -> sendMessageWithInlineSdek(chatId);

                default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
            }
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
    public void sendMessage(String chatId, Integer messageId) {
        String answer = "hello";
        SendMessage message = new SendMessage();
        message.setText(answer);
        message.setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void sendMessageWithInlinePublish(String chatId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/publish.jpg");
        InlineKeyboardMarkup inline = publishInline.getPublishInline();
        String answer = config.getPublishAnswer();
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(inline);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        try {
            execute(deleteMessage);
            execute(photo);

        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void sendMessageWithInlinePublishFree(String chatId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/publish.jpg");
        InlineKeyboardMarkup inline = publishInline.getPublishInline();
        String answer = config.getPublishAnswer();
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(inline);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        try {
            execute(deleteMessage);
            execute(photo);

        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    @SneakyThrows
    public void sendMessageWithInlineSdek(String chatId, Integer messageId) {

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

        try {
            execute(deleteMessage);
            execute(photo);

        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
