package com.alexeyrand.swooshbot.telegram;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import com.alexeyrand.swooshbot.datamodel.entity.Photo;
import com.alexeyrand.swooshbot.datamodel.repository.ChatRepository;
import com.alexeyrand.swooshbot.datamodel.service.ChatService.ChatService;
import com.alexeyrand.swooshbot.datamodel.service.ChatService.PhotoService;
import com.alexeyrand.swooshbot.telegram.inline.MainMenuInline;
import com.alexeyrand.swooshbot.telegram.inline.MenuInline;
import com.alexeyrand.swooshbot.telegram.inline.PublishInline;
import com.alexeyrand.swooshbot.telegram.inline.SdekInline;
import com.alexeyrand.swooshbot.telegram.service.MessageHandler;
import com.alexeyrand.swooshbot.telegram.service.MessageSender;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import com.alexeyrand.swooshbot.telegram.utils.Flag;
import com.sun.tools.javac.Main;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final MessageSender messageSender;
    private final MessageHandler messageHandler;
    private final Utils utils;
    private final SdekInline sdekInline;
    private final PublishInline publishInline;
    private final MenuInline menuInline;
    private final QueryHandler queryHandler;
    private final MainMenuInline mainMenuInline;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final PhotoService photoService;
    public static boolean wait = false;
    public static boolean ready = false;
    public static boolean flag = true;


    public List<String> medias = new ArrayList<>();
    //public List<InputMedia> inputsMedia = new ArrayList<>();

    public TelegramBot(@Value("${bot.token}") String botToken,
                       BotConfig config, MessageSender messageSender, MessageHandler messageHandler,
                       SdekInline sdekInline,
                       PublishInline publishInline,
                       QueryHandler queryHandler,
                       Utils utils,
                       MenuInline menuInline,
                       MainMenuInline mainMenuInline,
                       ChatService chatService,
                       ChatRepository chatRepository,
                       PhotoService photoService) throws TelegramApiException {
        super(botToken);
        this.config = config;
        this.messageSender = messageSender;
        this.messageHandler = messageHandler;
        this.sdekInline = sdekInline;
        this.publishInline = publishInline;
        this.queryHandler = queryHandler;
        this.utils = utils;
        this.menuInline = menuInline;
        this.mainMenuInline = mainMenuInline;
        this.chatService = chatService;
        this.chatRepository = chatRepository;
        this.photoService = photoService;
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
        if (update.hasMessage() && update.getMessage().hasText() && !wait) {

            Message message = update.getMessage();

            if (chatService.findWaitByChatId(message.getChatId()).isEmpty()) {
                chatService.save(
                        Chat.builder()
                                .chatId(update.getMessage().getChatId())
                                .wait(false)
                                .build());
                Optional<Chat> waitRest = chatService.findWaitByChatId(update.getMessage().getChatId());
                System.out.println(waitRest.get());
            }

            Integer messageId = message.getMessageId();
            String chatId = message.getChatId().toString();
            String messageText = message.getText();
            System.out.println(messageText);


            if (wait) {
                System.out.println("Жду сообщение");

                wait = false;
            } else {
                switch (messageText) {
                    case "/start" -> messageHandler.StartCommandReceived(chatId, messageId);
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

                case "publish/free" -> queryHandler.publishFreeReceived(chatId, messageId);
                case "publish/back" -> messageHandler.StartCommandReceived(chatId, messageId);

                //case "publish/free/1" -> queryHandler.publishFree1Received(chatId, messageId);
                case "publish/free/back" -> queryHandler.publishReceived(chatId, messageId);
//                case "sdek/order" -> sendMessageWithInlineSdek(chatId);
                case "publish/free/success" -> queryHandler.publishReceived(chatId, messageId);

                default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
            }
        } else if (update.hasMessage() && chatService.findWaitByChatId(update.getMessage().getChatId()).get().getWait()) {

            Message message = update.getMessage();
            String text = message.getCaption();
            Long chatId = message.getChatId();
            System.out.println(Thread.currentThread().getName() + " :::::::::::: " + (chatService.findWaitByChatId(chatId).get().getWait()));
            Integer messageId = message.getMessageId();

            if (!update.getMessage().hasPhoto()) {
                //medias.clear();                               /////////////////////////////////
                SendMessage responseMessage = new SendMessage();
                responseMessage.setChatId(chatId);
                responseMessage.setText("Объявление не может быть без фотографий! Прикрепите фотографии товаров.");
                justSendMessage(responseMessage);
                queryHandler.publishFreeReceived(chatId.toString(), -1);

            } else {

                String photo = message.getPhoto().get(0).getFileId();
                String username = message.getChat().getUserName();

                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(new InputFile(photo));
                sendPhoto.setChatId(-1002141489384L);
                sendPhoto.setCaption(text);

                medias.add(photo);
                photoService.save(Photo.builder().chatId(message.getChatId()).photo(photo).build());

                if (flag) {
                    flag = false;
                    Flag flag = new Flag(this, utils, queryHandler, menuInline, mainMenuInline, chatService, photoService);
                    flag.setChatIdChannel(-1002141489384L);
                    flag.setChatId(chatId);
                    flag.setText(text);
                    flag.setSendPhoto(sendPhoto);
                    flag.setUsername(username);
                    flag.setMessage(message);
                    Thread thread = new Thread(flag);
                    thread.start();
                }
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
        wait = false;
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
        chatService.update(Long.parseLong(message.getChatId()), true);
        try {
            execute(message);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }


    @SneakyThrows
    public void publishAlbum(Long chatId, Long chatChannel, String text, String username) {
        List<InputMedia> inputsMedia = new ArrayList<>();

        SendMediaGroup mediaGroup = new SendMediaGroup();
        List<Photo> photos = photoService.findAllPhotosByChatId(chatId);
        for (Photo photo : photos) {
            String url = photo.getPhoto();
            InputMedia inputPhoto = new InputMediaPhoto();
            inputPhoto.setMedia(url);
            if (medias.get(0).equals(url)) {
                inputPhoto.setCaption(text + "\n\nПродавец - @" + username);
            }
            inputsMedia.add(inputPhoto);
        }
        mediaGroup.setMedias(inputsMedia);
        mediaGroup.setChatId(chatChannel);
        wait = false;
        execute(mediaGroup);
        photoService.deleteAllByChatId(chatId);
        chatService.update(chatId, false);
    }
}
