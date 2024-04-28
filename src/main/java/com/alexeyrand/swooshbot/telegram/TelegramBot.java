package com.alexeyrand.swooshbot.telegram;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import com.alexeyrand.swooshbot.datamodel.entity.Photo;
import com.alexeyrand.swooshbot.datamodel.repository.ChatRepository;
import com.alexeyrand.swooshbot.datamodel.service.ChatService.ChatService;
import com.alexeyrand.swooshbot.datamodel.service.ChatService.PhotoService;
import com.alexeyrand.swooshbot.telegram.constants.TelegramConstants;
import com.alexeyrand.swooshbot.telegram.enums.State;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.alexeyrand.swooshbot.telegram.service.MessageHandler;
import com.alexeyrand.swooshbot.telegram.service.MessageSender;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import com.alexeyrand.swooshbot.telegram.utils.Flag;
import com.pengrad.telegrambot.response.StringResponse;
import com.sun.tools.javac.Main;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
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
import org.telegram.telegrambots.meta.api.objects.payments.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.alexeyrand.swooshbot.telegram.constants.TelegramConstants.channelId;
import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;
import static com.alexeyrand.swooshbot.telegram.enums.State.WAIT_FREE_PUBLISH;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final MessageSender messageSender;
    private final MessageHandler messageHandler;
    private final Utils utils;
    private final SdekInline sdekInline;
    private final PublishInline publishInline;
    private final BuyInline buyInline;
    private final MenuInline menuInline;
    private final QueryHandler queryHandler;
    private final MainMenuInline mainMenuInline;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final PhotoService photoService;


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
                       PhotoService photoService,
                       BuyInline buyInline) throws TelegramApiException {
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
        this.buyInline = buyInline;
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

        if (update.hasMessage()) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();
            Integer messageId = message.getMessageId();


            if (chatService.findWaitByChatId(chatId).isEmpty()) {
                chatService.save(
                        Chat.builder()
                                .chatId(update.getMessage().getChatId())
                                .state(NO_WAITING)
                                .build());
            }
            State state = chatService.getState(chatId);
            if (update.getMessage().hasSuccessfulPayment()) {

                SuccessfulPayment successfulPayment = message.getSuccessfulPayment();
                queryHandler.publishPaidReceived(chatId, messageId);

            } else if (state.equals(NO_WAITING) && message.hasText()) {
                switch (message.getText()) {
                    case "/start" -> messageHandler.StartCommandReceived(chatId, messageId);
                    ///case "/bbb" -> buy(message);
                    default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
                }
            } else if (state.equals(WAIT_FREE_PUBLISH)) {
                String text = message.getCaption();
                System.out.println(Thread.currentThread().getName() + " :::::::::::: " + (chatService.findWaitByChatId(chatId).get().getState()));

                if (!message.hasPhoto()) {
                    SendMessage responseMessage = new SendMessage();
                    responseMessage.setChatId(chatId);
                    responseMessage.setText("Объявление не может быть без фотографий! Прикрепите фотографии товаров.");
                    justSendMessage(responseMessage);
                    queryHandler.publishFreeReceived(chatId, -1);

                } else {
                    String photo = message.getPhoto().get(0).getFileId();
                    String username = message.getChat().getUserName();


                    photoService.save(Photo.builder().chatId(message.getChatId()).photo(photo).build());

                    if (chatService.getBlock(chatId)) {
                        chatService.updateBlock(chatId, false);
                        Flag flag = new Flag(this, utils, queryHandler, menuInline, mainMenuInline, chatService, photoService);
                        flag.setChatIdChannel(-1002141489384L);
                        flag.setChatId(chatId);
                        flag.setText(text);
                        flag.setUsername(username);
                        flag.setMessage(message);
                        Thread thread = new Thread(flag);
                        thread.start();
                    }
                }
            } else if (!message.hasText() && message.hasPhoto()) {
                messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
            }
        } else if (update.hasCallbackQuery()) {

            CallbackQuery query = update.getCallbackQuery();
            Message message = query.getMessage();

            Integer messageId = message.getMessageId();
            Long chatId = message.getChatId();
            String data = query.getData();

            switch (data) {
                case "publish" -> queryHandler.publishReceived(chatId, messageId);
//                case "legit" -> sendMessageWithInlineSdek(chatId);
                case "sdek" -> queryHandler.sdekReceived(chatId, messageId);
//                case "garant" -> sendMessageWithInlineSdek(chatId);
//                case "adv" -> sendMessageWithInlineSdek(chatId);

                case "publish/free" -> queryHandler.publishFreeReceived(chatId, messageId);
                case "publish/back" -> messageHandler.StartCommandReceived(chatId, messageId);

                case "publish/paid" -> queryHandler.publishCheckPaidReceived(chatId, messageId);
                //case "publish/paid/pay" -> buy(message);
                //case "publish/free/1" -> queryHandler.publishFree1Received(chatId, messageId);
                case "publish/free/back" -> queryHandler.publishReceived(chatId, messageId);
//                case "sdek/order" -> sendMessageWithInlineSdek(chatId);
                case "publish/free/success" -> messageHandler.StartCommandReceived(chatId, messageId);

                default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
            }
        } else if (update.hasPreCheckoutQuery()) {
            PreCheckoutQuery checkoutQuery = update.getPreCheckoutQuery();
            execute(new AnswerPreCheckoutQuery(checkoutQuery.getId(), true, "error"));
            ;

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
        Long chatId = Long.parseLong(photo.getChatId());
        photoService.deleteAllByChatId(chatId);
        chatService.updateState(chatId, NO_WAITING);
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

        chatService.updateState(Long.parseLong(message.getChatId()), WAIT_FREE_PUBLISH);
        try {
            execute(message);
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }


    @SneakyThrows
    public void publishAlbum(Long chatId, String text, String username) {
        List<InputMedia> inputsMedia = new ArrayList<>();
        SendMediaGroup mediaGroup = new SendMediaGroup();
        List<Photo> photos = photoService.findAllPhotosByChatId(chatId);
        if (photos.size() > 1) {
            for (Photo photo : photos) {
                String url = photo.getPhoto();
                InputMedia inputPhoto = new InputMediaPhoto();
                inputPhoto.setMedia(url);
                if (photos.get(0).getPhoto().equals(url)) {
                    inputPhoto.setCaption(text + "\n\nПродавец - @" + username);
                    inputPhoto.setParseMode(ParseMode.MARKDOWN);
                }
                inputsMedia.add(inputPhoto);
            }
            mediaGroup.setMedias(inputsMedia);
            mediaGroup.setChatId(channelId);
            execute(mediaGroup);
        } else if (photos.size() == 1) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setPhoto(new InputFile(photos.get(0).getPhoto()));
            sendPhoto.setChatId(channelId);
            sendPhoto.setCaption(text + "\n\nПродавец - @" + username);
            sendPhoto.setParseMode(ParseMode.MARKDOWN);
            execute(sendPhoto);
        }
        photoService.deleteAllByChatId(chatId);
        chatService.updateState(chatId, NO_WAITING);
    }


    public String buy(CreateInvoiceLink invoiceLink) throws TelegramApiException {

        return execute(invoiceLink);
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(message.getChatId());
//        sendMessage.setText("Оплата");
//        sendMessage.setReplyMarkup(buyInline.getBuyInline(response));
//        execute(sendMessage);

//        Invoice invoice = new Invoice();
//        invoice.set
//        message.setInvoice();
    }


}
