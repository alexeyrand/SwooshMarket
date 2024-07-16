package com.alexeyrand.swooshbot.telegram.service;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.api.service.ChatService;
import com.alexeyrand.swooshbot.model.publish.Amount;
import com.alexeyrand.swooshbot.model.publish.Item;
import com.alexeyrand.swooshbot.model.publish.ProviderData;
import com.alexeyrand.swooshbot.model.publish.Receipt;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

import static com.alexeyrand.swooshbot.telegram.enums.State.*;

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
    private final SettingsInline settingsInline;
    private final ChatService chatService;
    private final PublishPaidInline publishPaidInline;
    private final AdvInline advInline;
    private final GarantInline garantInline;
    private final LegitInline legitInline;

    @SneakyThrows
    public void publishReceived(Long chatId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/publish.jpg");
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\menu.txt");
        String answer = Files.readString(path);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setCaption(answer);
        photo.setReplyMarkup(publishInline.getPublishInline());
        photo.setParseMode(ParseMode.MARKDOWNV2);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chatId, NO_WAITING);

        telegramBot.sendPhoto(photo, deleteMessage);
    }

    @SneakyThrows
    public void publishFreeReceived(Long chatId, Integer messageId) {

        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\free.txt");
        String answer = Files.readString(path);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(answer);
        message.setReplyMarkup(publishFreeInline.getPublishFreeInline());
        message.setParseMode(ParseMode.MARKDOWNV2);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        chatService.updateBlock(chatId, true);
        telegramBot.sendMessageAndWait(message, deleteMessage);
    }

    @SneakyThrows
    public void publishPaidReceived(Long chatId, Integer messageId) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\paid.txt");
        String answer = Files.readString(path);

        chatService.updateState(chatId, WAIT_PAID_PUBLISH);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(answer);
        message.setReplyMarkup(publishFreeInline.getPublishFreeInline());
        message.setParseMode(ParseMode.MARKDOWNV2);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(-22);
        chatService.updateBlock(chatId, true);
        telegramBot.sendMessageWithDelete(message, deleteMessage);

        chatService.updatePaidPublishStatus(chatId, true);

    }

    @SneakyThrows
    public void sdekReceived(Long chatId, Integer messageId) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\cdek\\cdek.txt");
        File image = ResourceUtils.getFile("classpath:" + "static/images/sdek.jpg");
        String answer = Files.readString(path);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setCaption(answer);
        photo.setReplyMarkup(sdekInline.getSdekInline());
        photo.setParseMode(ParseMode.MARKDOWNV2);
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chatId, NO_WAITING);

        telegramBot.sendPhoto(photo, deleteMessage);
    }

    @SneakyThrows
    public void sdekOrderReceived(Long chatId, Integer messageId) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\cdek\\cdekInfo.txt");
        String answer = Files.readString(path);
        InlineKeyboardMarkup inline = sdekInline.getSdekOrderInline();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText(answer);
        message.setReplyMarkup(inline);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        telegramBot.sendMessageWithDelete(message, deleteMessage);

        chatService.updateState(chatId, NO_WAITING);
    }

    @SneakyThrows
    public void sdekOrder1Received(Long chatId, Integer messageId) {

        String answer = config.getSdekOrder1Answer();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText(answer);
        message.setReplyMarkup(sdekInline.getCdekTariffInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

//        chatService.updateState(chatId, WAIT_SDEK_TARIFF);

        telegramBot.sendMessageWithDelete(message, deleteMessage);
    }

    @SneakyThrows
    public void cdekTariffReceived(Long chatId, Integer messageId) {

        String answer = config.getSdekOrder1Answer();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText(answer);
        message.setReplyMarkup(sdekInline.getSdekBackInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chatId, WAIT_SDEK_TARIFF);

        telegramBot.sendMessageWithDelete(message, deleteMessage);
    }


    @SneakyThrows
    public void publishCheckPaidReceived(Long chatId, Integer messageId) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\check.txt");
        String answer = Files.readString(path);
        if (chatService.getPaidPublishStatus(chatId)) {
            answer = answer + "\n\n*Статус оплаты*:\nОплачено, услуга доступна ✅";
        } else {
            answer = answer + "\n\nСтатус оплаты:\nНе оплачено ❌";
        }
        CreateInvoiceLink invoiceLink = new CreateInvoiceLink(
                "Публикация вне очереди",
                "Публикация размещается в канал * сразу после того, как пост пройдет проверку модератором",
                chatId.toString(),
                config.getPaymentsToken(),
                "RUB", List.of(new LabeledPrice("Цена", 150 * 100)));
        invoiceLink.setNeedEmail(true);
        invoiceLink.setSendEmailToProvider(true);
        invoiceLink.setNeedPhoneNumber(true);
        invoiceLink.setSendPhoneNumberToProvider(true);
        Receipt receipt = new Receipt();
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setDescription("Publication out of sequence");
        item.setQuantity("1");
        Amount amount = new Amount();
        amount.setValue("150.00");
        amount.setCurrency("RUB");
        item.setVat_code(1);
        item.setAmount(amount);
        items.add(item);
        receipt.setItems(items);
        ProviderData providerData = new ProviderData();
        providerData.setReceipt(receipt);
        final ObjectMapper mapper = new ObjectMapper();
        String jsonDataRequest = mapper.writeValueAsString(providerData);
        invoiceLink.setProviderData(jsonDataRequest);

        invoiceLink.setPayload("publish");
        String response = telegramBot.buy(invoiceLink);


        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText(answer);
        message.setReplyMarkup(publishPaidInline.getPublishPaidInline(response, chatId));

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);
        chatService.updateBlock(chatId, true);
        telegramBot.sendMessageAndWait(message, deleteMessage);
    }

    @SneakyThrows
    public void advertisingReceived(Long chatId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/advertising.jpg");
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\adv\\adv.txt");
        String answer = Files.readString(path);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWNV2);
        photo.setCaption(answer);
        photo.setReplyMarkup(advInline.getAdvInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chatId, NO_WAITING);

        telegramBot.sendPhoto(photo, deleteMessage);

    }

    @SneakyThrows
    public void garantReceived(Long chatId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/garant.jpg");
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\garant\\garant.txt");
        String answer = Files.readString(path);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
        photo.setParseMode(ParseMode.MARKDOWNV2);
        photo.setCaption(answer);
        photo.setReplyMarkup(garantInline.getGarantInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chatId, NO_WAITING);

        telegramBot.sendPhoto(photo, deleteMessage);

    }

    @SneakyThrows
    public void legitReceived(Long chatId, Integer messageId) {
        File image = ResourceUtils.getFile("classpath:" + "static/images/legit.jpg");
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\legit\\legit.txt");
        String answer = Files.readString(path);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(image));
//        photo.setParseMode(ParseMode.MARKDOWN);
        photo.setCaption(answer);
        photo.setReplyMarkup(legitInline.getLegitInline());

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        chatService.updateState(chatId, NO_WAITING);

        telegramBot.sendPhoto(photo, deleteMessage);

    }

    @SneakyThrows
    public void settings1(Long chatId, Integer messageId) {
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText("Тут будет текст с пояснениями где что настраивать");
        message.setReplyMarkup(settingsInline.getSettingsInline());
        telegramBot.justSendMessage(message);
    }

    @SneakyThrows
    public void settings2(Long chatId, Integer messageId) {
        chatService.updateState(chatId, NO_WAITING);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText(
                "1) Редактировать текст в главным меню\n" +
                "2) Редактировать текст в разделе \"Публикация постов\"\n" +
                "3) Редактировать текст в разделе \"Сдек\"");
        message.setReplyMarkup(settingsInline.getSettingsTextInline());
        telegramBot.justSendMessage(message);
    }

    @SneakyThrows
    public void settings3(Long chatId, Integer messageId) {
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText("Ниже перечислены все сообщения бота из раздела ПУБЛИКАЦИИ содержащие текст который можно редактировать");
        message.setReplyMarkup(settingsInline.getSettingsTextPublishInline());
        telegramBot.justSendMessage(message);
    }

    @SneakyThrows
    public void settings4(Long chatId, Integer messageId) {
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode(ParseMode.MARKDOWNV2);
        message.setText("Ниже перечислены все сообщения бота из раздела СДЕК содержащие текст который можно редактировать");
        message.setReplyMarkup(settingsInline.getSettingsTextCdekInline());
        telegramBot.justSendMessage(message);
    }



}
