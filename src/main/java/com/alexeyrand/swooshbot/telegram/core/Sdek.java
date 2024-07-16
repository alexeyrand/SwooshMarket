package com.alexeyrand.swooshbot.telegram.core;

import com.alexeyrand.swooshbot.api.client.RequestSender;
import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.model.dto.calculator.CalculateCostResponse;
import com.alexeyrand.swooshbot.model.dto.calculator.CostInfo;
import com.alexeyrand.swooshbot.model.dto.sdek.SdekOrderInfoResponse;
import com.alexeyrand.swooshbot.model.entity.sdek.CdekOrderInfo;
import com.alexeyrand.swooshbot.model.entity.sdek.SdekOrderInfo;
import com.alexeyrand.swooshbot.api.service.ChatService;
import com.alexeyrand.swooshbot.api.service.CdekOrderInfoService;
import com.alexeyrand.swooshbot.api.service.SdekOrderInfoService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.enums.State;
import com.alexeyrand.swooshbot.telegram.enums.TariffCode;
import com.alexeyrand.swooshbot.telegram.inline.*;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.invoices.CreateInvoiceLink;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;
import static com.alexeyrand.swooshbot.telegram.enums.State.WAIT_SDEK_SHIPMENT_PVZ;

@Component
@RequiredArgsConstructor
public class Sdek {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    private final SdekInline sdekInline;
    private final BotConfig config;
    private final SdekOrderInfoService sdekOrderInfoService;
    private final ChatService chatService;
    private final CdekOrderInfoService cdekOrderInfoService;
    private final RequestSender requestSender;

    public void setTariff(Long chatId, Integer messageId, String tariff) throws TelegramApiException {
        createSdekOrderRequestIfNotExist(chatId);
        try {
            tariff = tariff.split("/")[2];
            TariffCode.valueOf("TARIFF_" + tariff);
            SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
            sdekOrderInfo.setTariffCode(Integer.parseInt(tariff));
            sdekOrderInfo.setInfo("Информация о заказе:\n✅ Тариф: " + tariff);
            sdekOrderInfoService.save(sdekOrderInfo);
            chatService.updateState(chatId, State.WAIT_SDEK_WEIGHT);

            telegramBot.deleteMessage(chatId, messageId);
//            telegramBot.deleteMessage(chatId, messageId - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder2Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);


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
            SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
            sdekOrderInfo.setPackageWeight(weight);
            sdekOrderInfo.setItemWeight(Float.parseFloat(weight.toString()));
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Вес посылки: " + weight + " г.");
            sdekOrderInfoService.save(sdekOrderInfo);
            chatService.updateState(chatId, State.WAIT_SDEK_DIMENSIONS);

            telegramBot.deleteMessage(chatId, message.getMessageId());
//            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder3Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);


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
        try {
            Matcher m = r.matcher(dimensions);
            if (m.matches()) {

                String[] dims = dimensions.split(" ");
                Integer l = Integer.parseInt(dims[0]);
                Integer w = Integer.parseInt(dims[1]);
                Integer h = Integer.parseInt(dims[2]);
                SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
                sdekOrderInfo.setPackageLength(l);
                sdekOrderInfo.setPackageWidth(w);
                sdekOrderInfo.setPackageHeight(h);
                sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Длина: " + l + " см.\n✅ Ширина: " + w + " см.\n✅ Высота: " + h + " см.");
                sdekOrderInfoService.save(sdekOrderInfo);
                chatService.updateState(chatId, State.WAIT_SDEK_ITEM_DESCRIPTION);

                telegramBot.deleteMessage(chatId, message.getMessageId());
//                telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder4Answer());
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);



            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Габариты посылки указаны неверно. Введите длину, ширину, высоту в граммах через пробел");
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);
            }
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Габариты посылки указаны неверно. Введите длину, ширину, высоту в граммах через пробел");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setItemDescription(Message message, Long chatId, String description) {
        try {
            if (description == null) {
                throw new IOException();
            }
            SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
            sdekOrderInfo.setItemName(description);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Описание товара: " + description);
            sdekOrderInfoService.save(sdekOrderInfo);
            chatService.updateState(chatId, State.WAIT_SDEK_SENDER_FIO);

            telegramBot.deleteMessage(chatId, message.getMessageId());
//            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder5Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);


        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Описание указано неверно.");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setSenderFIO(Message message, Long chatId, String FIO) {

        if (FIO != null && (FIO.split(" ").length == 2 || FIO.split(" ").length == 3)) {
            //TODO: setUpperCase FIO
            SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
            sdekOrderInfo.setSenderName(FIO);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ ФИО отправителя: " + FIO);
            sdekOrderInfoService.save(sdekOrderInfo);
            chatService.updateState(chatId, State.WAIT_SDEK_SENDER_TELEPHONE);

            telegramBot.deleteMessage(chatId, message.getMessageId());
//            telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder6Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);

            chatService.updateState(chatId, State.WAIT_SDEK_SENDER_TELEPHONE);

        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Фамилия, имя и отчество отправителя указаны неверно. Введите ФИО получателя через пробел (отчество не обязательно).");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setSenderTelephone(Message message, Long chatId, String number) {
        String pattern = "^\\+\\d[0-9]{10}";
        Pattern r = Pattern.compile(pattern);
        try {
            Matcher m = r.matcher(number);
            if (m.matches()) {

                SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
                sdekOrderInfo.setSenderNumber(number);
                sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Телефон отправителя: " + number);
                sdekOrderInfoService.save(sdekOrderInfo);
                chatService.updateState(chatId, State.WAIT_SDEK_FIO);

                telegramBot.deleteMessage(chatId, message.getMessageId());
                //telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder7Answer());
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);




            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Номер телефона отправителя указан неверно. Введите номер телефона, соблюдая формат");
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);
            }
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Номер телефона получателя указан неверно. Введите номер телефона, соблюдая формат");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setFIO(Message message, Long chatId, String FIO) {

        if (FIO != null && (FIO.split(" ").length == 2 || FIO.split(" ").length == 3)) {
            //TODO: setUpperCase FIO
            SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
            sdekOrderInfo.setRecipientName(FIO);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ ФИО получателя: " + FIO);
            sdekOrderInfoService.save(sdekOrderInfo);
            chatService.updateState(chatId, State.WAIT_SDEK_TELEPHONE);

            telegramBot.deleteMessage(chatId, message.getMessageId());
            //telegramBot.deleteMessage(chatId, message.getMessageId() - 1);


            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder8Answer());
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);



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
        try {
            Matcher m = r.matcher(number);
            if (m.matches()) {

                SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
                sdekOrderInfo.setRecipientNumber(number);
                sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Телефон получателя: " + number);
                sdekOrderInfoService.save(sdekOrderInfo);
                chatService.updateState(chatId, WAIT_SDEK_SHIPMENT_PVZ);

                telegramBot.deleteMessage(chatId, message.getMessageId());
                //telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder9Answer());
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);




            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Номер телефона получателя указан неверно. Введите номер телефона, соблюдая формат");
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);
            }
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Номер телефона получателя указан неверно. Введите номер телефона, соблюдая формат");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void setShipmentPVZ(Message message, Long chatId, String PVZCode) {
        String result;
        if (PVZCode != null) {
            result = requestSender.getPVZ(PVZCode);
            if (!result.equals("[]") && !result.isEmpty()) {
                String name = result.split("\",")[1].split(":\"")[1];
                SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
                sdekOrderInfo.setShipmentPoint(PVZCode.toUpperCase());
                sdekOrderInfo.setShipmentCity(name.split(", ")[1]);
                sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Адрес ПВЗ отправки: " + name);
                sdekOrderInfoService.save(sdekOrderInfo);
                chatService.updateState(chatId, State.WAIT_SDEK_DELIVERY_PVZ);

                telegramBot.deleteMessage(chatId, message.getMessageId());
                //telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(sdekOrderInfo.getInfo() + "\n\n" + config.getSdekOrder10Answer());
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);


            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("По коду \"" + PVZCode.toUpperCase() + "\" не найдено ни одного офиса. Уточните код ПВЗ на сайте \"https://www.cdek.ru/ru/offices/\"");
                sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
                telegramBot.justSendMessage(sendMessage);
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Некорректный код ПВЗ. Уточните код ПВЗ на сайте \"https://www.cdek.ru/ru/offices/\"");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }


    }

    @SneakyThrows
    public void setDeliveryPVZ(Message message, Long chatId, String PVZCode) {

        String result = requestSender.getPVZ(PVZCode);

        if (!result.equals("[]") && !result.isEmpty()) {
            String name = result.split("\",")[1].split(":\"")[1];
            SdekOrderInfo sdekOrderInfo = sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow();
            sdekOrderInfo.setDeliveryPoint(PVZCode.toUpperCase());
            sdekOrderInfo.setDeliveryCity(name.split(", ")[1]);
            sdekOrderInfo.setInfo(sdekOrderInfo.getInfo() + "\n✅ Адрес ПВЗ получения: " + name);
            sdekOrderInfoService.save(sdekOrderInfo);

            chatService.updateState(chatId, NO_WAITING);

            postCreateSdekOrder(chatId, message, sdekOrderInfo);

        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("По коду \"" + PVZCode.toUpperCase() + "\" не найдено ни одного офиса. Уточните код ПВЗ на сайте \"https://www.cdek.ru/ru/offices/\"");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    @SneakyThrows
    public void createOrder(Long chatId) {
        String uuid = requestSender.createOrder(chatId);
        Thread.sleep(3000);
        SdekOrderInfoResponse sdekOrderInfoResponse = requestSender.getOrderInfo(uuid);
        sdekOrderInfoResponse.setUuid(uuid);
        handlerSdekOrderInfoResponse(chatId, sdekOrderInfoResponse);

        String answer;
        if (sdekOrderInfoResponse != null) {
            answer = "Информаци о заказе:\n" +
                    "Статус: " + sdekOrderInfoResponse.getStatus() +
                    "\nДата создания: " + sdekOrderInfoResponse.getDate() +
                    "\n✅Трек-номер для отслеживания: " + sdekOrderInfoResponse.getOrderNumber();
        } else {
            answer = "Во время создания заказа произошла ошибка. Обратитесь к администратору.";
        }
        if (sdekOrderInfoResponse.getWarn() != null) {
            answer = answer + "\n\nПредупреждения: \n" + sdekOrderInfoResponse.getWarn();
        }
        if (sdekOrderInfoResponse.getErr() != null) {
            answer = answer + "\n\nОшибки: \n" + sdekOrderInfoResponse.getErr();
        }
        answer = answer + "\n\nuuid: " + uuid;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
        telegramBot.justSendMessage(sendMessage);
        chatService.updateState(chatId, NO_WAITING);

        if (Objects.equals(sdekOrderInfoResponse.getStatus(), "Заказ успешно создан")) {
            CdekOrderInfo cdekOrderInfo = CdekOrderInfo.builder()
                    .chatId(chatId)
                    .username(chatService.findWaitByChatId(chatId).orElseThrow().getUsername())
                    .state(sdekOrderInfoResponse.getStatus())
                    .date(sdekOrderInfoResponse.getDate())
                    .uuid(sdekOrderInfoResponse.getUuid())
                    .build();
            cdekOrderInfoService.save(cdekOrderInfo);
        }
    }

    @SneakyThrows
    private void postCreateSdekOrder(Long chatId, Message message, SdekOrderInfo sdekOrderInfo) {
        Integer shipmentCityCode = requestSender.getCityCode(chatId, sdekOrderInfo.getShipmentCity());
        Integer deliveryCityCode = requestSender.getCityCode(chatId, sdekOrderInfo.getDeliveryCity());

        CalculateCostResponse calculateCostResponse = requestSender.calculateTheCostOrder(
                chatId,
                shipmentCityCode,
                deliveryCityCode
        );

        if (calculateCostResponse != null && !calculateCostResponse.getTariff_codes().isEmpty()) {
            List<CostInfo> costInfoList = calculateCostResponse.getTariff_codes();
            Float cost = costInfoList.stream().filter(
                    c -> c.getTariff_code().equals(sdekOrderInfo.getTariffCode())
            ).findFirst().orElseThrow().getDelivery_sum();
            cost = cost * 1.5f;

            telegramBot.deleteMessage(chatId, message.getMessageId());
            //telegramBot.deleteMessage(chatId, message.getMessageId() - 1);

            CreateInvoiceLink invoiceLink = new CreateInvoiceLink(
                    "Оплата накладной СДЭК",
                    "После оплаты вы получите трек-номер для отправки и отслеживания посылки",
                    chatId.toString(),
                    config.getPaymentsToken(),
                    "RUB", List.of(new LabeledPrice("Цена", Math.round(cost) * 100)));
            invoiceLink.setNeedEmail(true);
            invoiceLink.setNeedName(true);
            invoiceLink.setPayload("sdek");
            String response = telegramBot.buy(invoiceLink);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(sdekOrderInfo.getInfo() + "\n\nЦена доставки: " + cost + "руб.");
            sendMessage.setReplyMarkup(sdekInline.getCdekPayInline(response, chatId));
            telegramBot.justSendMessage(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Выбранный тариф не поддерживается на маршруте:\n" + sdekOrderInfo.getShipmentCity() + " \n   ---> \n" + sdekOrderInfo.getDeliveryCity() + "\n\nВоспользуйтесь другим тарифом или уточните адреса ПВЗ.");
            sendMessage.setReplyMarkup(sdekInline.getSdekBackInline());
            telegramBot.justSendMessage(sendMessage);
        }
    }

    private void createSdekOrderRequestIfNotExist(Long chatId) {
        if (sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).isEmpty()) {
            SdekOrderInfo sdekOrderInfo = SdekOrderInfo.builder().chatId(chatId).build();
            sdekOrderInfoService.save(sdekOrderInfo);
        }
    }

    private void handlerSdekOrderInfoResponse(Long chatId, SdekOrderInfoResponse sdekOrderInfoResponse) throws JsonProcessingException {
        if (sdekOrderInfoResponse.getStatus().equals("SUCCESSFUL")) {
            sdekOrderInfoResponse.setStatus("Заказ успешно создан");
        } else if (sdekOrderInfoResponse.getStatus().equals("INVALID")) {
            sdekOrderInfoResponse.setStatus("Ошибка создания заказа");
        }
        List<String> ws = sdekOrderInfoResponse.getWarnings();
        if (!ws.isEmpty() && ws.get(0).contains("Delivery point has been changed to")) {
            String result1 = requestSender.getPVZ(sdekOrderInfoService.findSdekOrderInfoByChatId(chatId).orElseThrow().getDeliveryPoint());
            String oldAddressPVZ = result1.split("\"address_full\"")[1].split("\"")[1];
            String codePVZ = ws.get(0).split("changed to ")[1].replace("\"", "");
            String result2 = requestSender.getPVZ(codePVZ);
            String newAddressPVZ = result2.split("\"address_full\"")[1].split("\"")[1];
            sdekOrderInfoResponse.setWarn("Адрес ПВЗ доставки был изменен!\nВыбранный ПВЗ: \n\"" + oldAddressPVZ
                    + "\"\nнедоступен. Произошла переадресация на ближайший ПВЗ по адресу:\n\"" + newAddressPVZ + ".\"");
        }
        List<String> ers = sdekOrderInfoResponse.getErrors();
        if (!ers.isEmpty() && ers.get(0).contains("The pick-up point")) {
            sdekOrderInfoResponse.setErr("Некорректный заказ!\nВыбранный ПВЗ:\n \"" + sdekOrderInfoResponse.getErrors().get(0).split("\"")[1]
                    + "\"\nнедоступен или не существует. Уточните информацию на сайте и укажите корректный код ПВЗ.");
        }
        sdekOrderInfoService.deleteByChatId(chatId);
    }



}
