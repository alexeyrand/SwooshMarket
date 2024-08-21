package com.alexeyrand.swooshbot.telegram;

import com.alexeyrand.swooshbot.api.service.PublishOrderInfoService;
import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.model.entity.Chat;
import com.alexeyrand.swooshbot.model.entity.publish.Photo;
import com.alexeyrand.swooshbot.api.repository.ChatRepository;
import com.alexeyrand.swooshbot.api.service.ChatService;
import com.alexeyrand.swooshbot.api.service.PhotoService;
import com.alexeyrand.swooshbot.api.service.SdekOrderInfoService;
import com.alexeyrand.swooshbot.model.entity.publish.PublishOrderInfo;
import com.alexeyrand.swooshbot.telegram.core.Publish;
import com.alexeyrand.swooshbot.telegram.core.Sdek;
import com.alexeyrand.swooshbot.telegram.core.Settings;
import com.alexeyrand.swooshbot.telegram.enums.State;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.alexeyrand.swooshbot.telegram.service.MessageHandler;
import com.alexeyrand.swooshbot.telegram.service.MessageSender;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.payments.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.alexeyrand.swooshbot.telegram.constants.TelegramConstants.channelId;
import static com.alexeyrand.swooshbot.telegram.enums.State.*;


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
    private final SdekOrderInfoService sdekOrderInfoService;
    private final PublishOrderInfoService publishOrderInfoService;
    private final Publish publish;
    private final Sdek sdek;
    private final Settings setting;

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
                       Publish publish,
                       Sdek sdek,
                       Settings setting,
                       SdekOrderInfoService sdekOrderInfoService,
                       PublishOrderInfoService publishOrderInfoService) throws TelegramApiException {
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
        this.publish = publish;
        this.sdek = sdek;
        this.setting = setting;
        this.sdekOrderInfoService = sdekOrderInfoService;
        this.publishOrderInfoService = publishOrderInfoService;
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
            String username = message.getChat().getUserName();


            if (chatService.findWaitByChatId(chatId).isEmpty()) {
                chatService.save(
                        Chat.builder()
                                .chatId(chatId)
                                .state(NO_WAITING)
                                .username(username)
                                .build());
            }
            State state = chatService.getState(chatId);

            if (update.getMessage().hasSuccessfulPayment()) {
                Chat chat = chatService.findWaitByChatId(chatId).orElseThrow();
                if (state.equals(WAIT_SDEK_PAYMENT)) {
                    sdek.createOrder(chatId);
                } else if (state.equals(WAIT_PAID_PUBLISH_PAYMENT)) {
                    queryHandler.publishPaidReceived(chatId, messageId);
                }

            } else if (state.equals(NO_WAITING) && message.hasText()) {
                switch (message.getText()) {
                    case "/start" -> messageHandler.StartCommandReceived(chatId, messageId);
                    default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
                }
            } else if (state.equals(WAIT_FREE_PUBLISH)) {
                publish.PublishFree(message, chatId);
            } else if (state.equals(WAIT_PAID_PUBLISH)) {
                publish.PublishPaid(message, chatId);
//            } else if (state.equals(WAIT_SDEK_TARIFF)) {
//                sdek.setTariff(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_WEIGHT)) {
                sdek.setWeight(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_DIMENSIONS)) {
                sdek.setDimensions(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_ITEM_DESCRIPTION)) {
                sdek.setItemDescription(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_SENDER_FIO)) {
                sdek.setSenderFIO(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_SENDER_TELEPHONE)) {
                sdek.setSenderTelephone(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_FIO)) {
                sdek.setFIO(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_TELEPHONE)) {
                sdek.setTelephone(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_SHIPMENT_PVZ)) {
                sdek.setShipmentPVZ(message, chatId, message.getText());
            } else if (state.equals(WAIT_SDEK_DELIVERY_PVZ)) {
                sdek.setDeliveryPVZ(message, chatId, message.getText());
            } else if (!message.hasText() && message.hasPhoto()) {
                messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            } else if (state.equals(WAIT_EDIT_MAIN_MENU)) {
                setting.editMainMenu(chatId, messageId, message.getText(), "menu", state);
            } else if (state.equals(WAIT_EDIT_LEGIT)) {
                setting.editMainMenu(chatId, messageId, message.getText(), "legit", state);
            } else if (state.equals(WAIT_EDIT_ADV)) {
                setting.editMainMenu(chatId, messageId, message.getText(), "adv", state);
            } else if (state.equals(WAIT_EDIT_GARANT)) {
                setting.editMainMenu(chatId, messageId, message.getText(), "garant", state);
            } else if (state.equals(WAIT_EDIT_PUBLISH_1)) {
                setting.edit1(chatId, messageId, message.getText());
            } else if (state.equals(WAIT_EDIT_PUBLISH_2)) {
                setting.edit2(chatId, messageId, message.getText());
            } else if (state.equals(WAIT_EDIT_PUBLISH_3)) {
                setting.edit3(chatId, messageId, message.getText());
            } else if (state.equals(WAIT_EDIT_PUBLISH_4)) {
                setting.edit4(chatId, messageId, message.getText());
            } else if (state.equals(WAIT_EDIT_CDEK_1)) {
                setting.cdekEdit1(chatId, messageId, message.getText());
            } else if (state.equals(WAIT_EDIT_CDEK_2)) {
                setting.cdekEdit2(chatId, messageId, message.getText());
            }


        } else if (update.hasCallbackQuery()) {

            CallbackQuery query = update.getCallbackQuery();
            Message message = query.getMessage();
            Long chatId = message.getChatId();
            Integer messageId = message.getMessageId();
            String data = query.getData();
//            String text = query.ge

            if (chatService.findWaitByChatId(chatId).isEmpty()) {
                chatService.save(
                        Chat.builder()
                                .chatId(chatId)
                                .state(NO_WAITING)
                                .build());
            }
            State state = chatService.getState(chatId);

            switch (data) {
                case "menu" -> messageHandler.StartCommandReceived(chatId, messageId);
                case "publish" -> queryHandler.publishReceived(chatId, messageId, data);
                case "publishInChat" -> queryHandler.publishReceived(chatId, messageId, data);
                case "legit" -> queryHandler.legitReceived(chatId, messageId);
                case "sdek" -> queryHandler.sdekReceived(chatId, messageId);
                case "garant" -> queryHandler.garantReceived(chatId, messageId);
                case "adv" -> queryHandler.advertisingReceived(chatId, messageId);

                case "publish/chanel/free" -> queryHandler.publishFreeReceived(chatId, messageId);
                case "publish/chat/free" -> queryHandler.publishFreeReceived(chatId, messageId);
                case "publish/back" -> messageHandler.StartCommandReceived(chatId, messageId);

                case "publish/chanel/paid" -> queryHandler.publishCheckPaidReceived(chatId, messageId);
                case "publish/chanel/paid/publish" -> queryHandler.publishPaidReceived(chatId, messageId);
                case "publish/chat/paid" -> queryHandler.publishCheckPaidReceived(chatId, messageId);
                case "publish/chat/paid/publish" -> queryHandler.publishPaidReceived(chatId, messageId);

                case "publish/free/back" -> queryHandler.publishReceived(chatId, messageId, data);
                case "publish/free/success" -> messageHandler.StartCommandReceived(chatId, messageId);

                case "sdek/order" -> queryHandler.sdekOrderReceived(chatId, messageId);
                case "sdek/order/1" -> queryHandler.sdekOrder1Received(chatId, messageId);
                case "cdek/tariff/136", "cdek/tariff/482" -> sdek.setTariff(chatId, messageId, data);

                case "settings" -> queryHandler.settings1(chatId, messageId);
                case "settings/text" -> queryHandler.settings2(chatId, messageId);
                case "settings/text/publish" -> queryHandler.settings3(chatId, messageId);
                case "settings/text/cdek" -> queryHandler.settings4(chatId, messageId);

                case "settings/text/menu" -> setting.showTextMainMenu(chatId, messageId, data, "menu");
                case "settings/text/legit" -> setting.showTextMainMenu(chatId, messageId, data, "legit");
                case "settings/text/adv" -> setting.showTextMainMenu(chatId, messageId, data, "adv");
                case "settings/text/garant" -> setting.showTextMainMenu(chatId, messageId, data, "garant");

                case "settings/text/menu/edit" ->
                        setting.editMainMenu(chatId, messageId, data, "menu", WAIT_EDIT_MAIN_MENU);
                case "settings/text/legit/edit" ->
                        setting.editMainMenu(chatId, messageId, data, "legit", WAIT_EDIT_LEGIT);
                case "settings/text/adv/edit" -> setting.editMainMenu(chatId, messageId, data, "adv", WAIT_EDIT_ADV);
                case "settings/text/garant/edit" ->
                        setting.editMainMenu(chatId, messageId, data, "garant", WAIT_EDIT_GARANT);

                case "settings/text/publish/1",
                        "settings/text/publish/2",
                        "settings/text/publish/3",
                        "settings/text/publish/4" -> setting.showTextPublish(chatId, messageId, data);


                case "settings/text/publish/edit/1" -> setting.edit1(chatId, messageId, data);
                case "settings/text/publish/edit/2" -> setting.edit2(chatId, messageId, data);
                case "settings/text/publish/edit/3" -> setting.edit3(chatId, messageId, data);
                case "settings/text/publish/edit/4" -> setting.edit4(chatId, messageId, data);


                case "settings/text/cdek/1",
                        "settings/text/cdek/2" -> setting.showTextCdek(chatId, messageId, data);

                case "settings/text/cdek/edit/1" -> setting.cdekEdit1(chatId, messageId, data);
                case "settings/text/cdek/edit/2" -> setting.cdekEdit2(chatId, messageId, data);

                default -> messageSender.sendMessage(chatId, "Такой команды нет.\nВызов меню: /start");
            }
        } else if (update.hasPreCheckoutQuery()) {

            PreCheckoutQuery checkoutQuery = update.getPreCheckoutQuery();
            if (checkoutQuery.getInvoicePayload().equals("publish")) {
                chatService.updateState(update.getPreCheckoutQuery().getFrom().getId(), WAIT_PAID_PUBLISH_PAYMENT);
            } else if (checkoutQuery.getInvoicePayload().equals("sdek")) {
                chatService.updateState(update.getPreCheckoutQuery().getFrom().getId(), WAIT_SDEK_PAYMENT);
            }
            execute(new AnswerPreCheckoutQuery(checkoutQuery.getId(), true, "error ошбика"));

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
    public void sendMessageWithDelete(SendMessage message, DeleteMessage deleteMessage) {

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
    public void publishAlbum(Long chatId, String text, String username, Boolean paid, Message message) {
        String paidAnswer = paid ? "\n\n!!! Публикация вне очереди !!!" : "";
        List<InputMedia> inputsMedia = new ArrayList<>();
        SendMediaGroup mediaGroup = new SendMediaGroup();
        List<Photo> photos = photoService.findAllPhotosByChatId(chatId);
        if (photos.size() > 1) {
            for (Photo photo : photos) {
                String url = photo.getPhoto();
                InputMedia inputPhoto = new InputMediaPhoto();
                inputPhoto.setMedia(url);
                if (photos.get(0).getPhoto().equals(url)) {
                    inputPhoto.setCaption(text + "\n\nПродавец: @" + username + paidAnswer);
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
            sendPhoto.setCaption(text + "\n\nПродавец: @" + username + paidAnswer);
            execute(sendPhoto);
        }
        photoService.deleteAllByChatId(chatId);
        chatService.updateState(chatId, NO_WAITING);
        Long userId = chatService.getIdByChatId(chatId);
        PublishOrderInfo publishOrderInfo = PublishOrderInfo
                .builder()
                .chatId(chatId)
                .userId(userId)
                .username(message.getChat().getUserName())
                .orderType(paid ? "PAID" : "FREE")
                .date(new Date())
                .build();
        publishOrderInfoService.save(publishOrderInfo);
    }


    public String buy(CreateInvoiceLink invoiceLink) throws TelegramApiException {
        return execute(invoiceLink);
    }

    public void deleteMessage(Long chatId, Integer messageId) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        execute(deleteMessage);
    }

}
