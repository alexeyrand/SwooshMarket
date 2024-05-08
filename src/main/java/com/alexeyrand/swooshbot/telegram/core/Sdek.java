package com.alexeyrand.swooshbot.telegram.core;

import com.alexeyrand.swooshbot.api.client.RequestSender;
import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.datamodel.dto.SdekOrderRequest;
import com.alexeyrand.swooshbot.datamodel.entity.sdek.SdekOrderInfo;
import com.alexeyrand.swooshbot.datamodel.service.ChatService;
import com.alexeyrand.swooshbot.datamodel.service.SdekOrderRequestService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.enums.State;
import com.alexeyrand.swooshbot.telegram.enums.TariffCode;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Sdek {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    private final SdekInline sdekInline;
    private final BotConfig config;
    private final QueryHandler queryHandler;
    private final MenuInline menuInline;
    private final MainMenuInline mainMenuInline;
    private final SdekOrderRequestService sdekOrderRequestService;
    private final ChatService chatService;
    private final RequestSender requestSender;

    public void setTariff(Message message, Long chatId, String tariff) throws TelegramApiException {
        createSdekOrderRequestIfNotExist(chatId);
        try {
            TariffCode.valueOf("TARIFF_" + tariff);
            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setTariffCode(Integer.parseInt(tariff));
            sdekOrderInfo.setInfo("Информация о заказе:\n✅ Тариф: " + tariff);
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder2Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_WEIGHT);
        } catch (IllegalArgumentException IAE) {

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Неверный код тарифа. Укажите тариф из предложенных");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    public void setWeight(Message message, Long chatId, String weightStr) {
        try {
            Integer weight = Integer.parseInt(weightStr.trim());
            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setPackageWidth(weight);
            sdekOrderInfo.setItemWeight(Float.parseFloat(weight.toString()));
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Вес посылки: " + weight + " г.");
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder3Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_DIMENSIONS);
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Вес посылки указан неверно. Введите значение, равное весу посылки в граммах");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    public void setDimensions(Message message, Long chatId, String dimensions) {
        String pattern = "^[0-9]+ [0-9]+ [0-9]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(dimensions);
        if (m.matches()) {
            try {
                String[] dims = dimensions.split(" ");
                Integer l = Integer.parseInt(dims[0]);
                Integer w = Integer.parseInt(dims[1]);
                Integer h = Integer.parseInt(dims[2]);
                SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
                sdekOrderInfo.setPackageLength(l);
                sdekOrderInfo.setPackageWeight(w);
                sdekOrderInfo.setPackageHeight(h);
                sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Длина: " + l + " см.\n✅ Ширина: " + w + " см.\n✅ Высота: " + h + " см.");
                sdekOrderRequestService.save(sdekOrderInfo);

                telegramBot.deleteMessage(chatId, message.getMessageId());
                telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder4Answer());
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);

                chatService.updateState(chatId, State.WAIT_SDEK_ITEM_DESCRIPTION);

            } catch (Exception e) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Габариты посылки указаны неверно. Введите длину, ширину, высоту в граммах через пробел");
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Габариты посылки указаны неверно. Введите длину, ширину, высоту в граммах через пробел");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setItemDescription(Message message, Long chatId, String description) {

            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setItemName(description);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Описание товара: " + description);
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder5Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_FIO);
    }

    @SneakyThrows
    public void setFIO(Message message, Long chatId, String FIO) {

        if (FIO.split(" ").length == 2 || FIO.split(" ").length == 3) {

            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setRecipientName(FIO);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ ФИО отправителя: " + FIO);
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder6Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_TELEPHONE);

        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Фамилия, имя и отчество получателя указаны неверно. Введите ФИО получателя через пробел (отчество не обязательно).");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setTelephone(Message message, Long chatId, String number) {
        String pattern = "^\\+\\d[0-9]{10}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(number);
        if (m.matches()) {

            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setRecipientNumber(number);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Телефон: " + number);
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder7Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_SHIPMENT_PVZ);


        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Номер телефона получателя указан неверно. Введите номер телефона, соблюдая формат");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setShipmentPVZ(Message message, Long chatId, String PVZCode) {

        String URL = "https://api.cdek.ru/v2/deliverypoints";
        String result = requestSender.getPVZ(URI.create(URL + "?code=" + PVZCode.toUpperCase()), PVZCode);
        String name = result.split("\",")[1].split(":\"")[1];

        if (!result.isEmpty()) {

            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setShipmentPoint(PVZCode);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Адрес ПВЗ отправки: " + name);
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder8Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_DELIVERY_PVZ);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("По коду \"" + PVZCode + "\" не найдено ни одного офиса. Уточните код ПВЗ на сайте \"https://www.cdek.ru/ru/offices/\"");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setDeliveryPVZ(Message message, Long chatId, String PVZCode) {

        String URL = "https://api.cdek.ru/v2/deliverypoints";
        String result = requestSender.getPVZ(URI.create(URL + "?code=" + PVZCode.toUpperCase()), PVZCode);
        String name = result.split("\",")[1].split(":\"")[1];

        if (!result.isEmpty()) {

            SdekOrderInfo sdekOrderInfo = sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).orElseThrow();
            sdekOrderInfo.setDeliveryPoint(PVZCode);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Адрес ПВЗ получения: " + name);
            sdekOrderRequestService.save(sdekOrderInfo);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder8Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_TELEPHONE);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("По коду \"" + PVZCode + "\" не найдено ни одного офиса. Уточните код ПВЗ на сайте \"https://www.cdek.ru/ru/offices/\"");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }


    private void createSdekOrderRequestIfNotExist(Long chatId) {
        if (sdekOrderRequestService.findSdekOrderRequestByChatId(chatId).isEmpty()) {
            SdekOrderInfo sdekOrderInfo = SdekOrderInfo.builder().chatId(chatId).build();
            sdekOrderRequestService.save(sdekOrderInfo);
        }
    }
}
