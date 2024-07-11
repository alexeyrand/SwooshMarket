package com.alexeyrand.swooshbot.telegram.inline;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SdekInline {
    private final BotConfig config;
private final ChatService chatService;



    public InlineKeyboardMarkup getSdekInline() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Оформить заказ");
        inlineKeyboardButton1.setCallbackData("sdek/order");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Назад");
        inlineKeyboardButton2.setCallbackData("menu");

        rowInline1.add(inlineKeyboardButton1);
        rowInline2.add(inlineKeyboardButton2);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public InlineKeyboardMarkup getSdekOrderInline() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Указать информацию о заказе");
        inlineKeyboardButton1.setCallbackData("sdek/order/1");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Назад");
        inlineKeyboardButton2.setCallbackData("sdek");

        rowInline1.add(inlineKeyboardButton1);
        rowInline2.add(inlineKeyboardButton2);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public InlineKeyboardMarkup getSdekBackInline() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Назад");
        inlineKeyboardButton.setCallbackData("sdek/order");

        rowInline.add(inlineKeyboardButton);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public InlineKeyboardMarkup getCdekPayInline(String response, Long  chatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1;

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Назад");
        inlineKeyboardButton2.setCallbackData("sdek");

        if (chatService.getSdekStatus(chatId)) {
            inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Оформить заказ");
            inlineKeyboardButton1.setCallbackData("sdek/order/create");
        } else {
            inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Оплатить");
            inlineKeyboardButton1.setCallbackData("pppublish/paid/pay");
            inlineKeyboardButton1.setUrl(response);
            inlineKeyboardButton1.setPay(true);
        }

        rowInline1.add(inlineKeyboardButton1);
        rowInline2.add(inlineKeyboardButton2);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }



    public InlineKeyboardMarkup getCdekTariffInline() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();


        inlineKeyboardButton1.setText("склад-склад");
        inlineKeyboardButton1.setCallbackData("cdek/tariff/136");
        inlineKeyboardButton2.setText("склад-склад экспресс");
        inlineKeyboardButton2.setCallbackData("cdek/tariff/482");

        inlineKeyboardButton3.setText("Назад");
        inlineKeyboardButton3.setCallbackData("sdek/order");

        rowInline1.add(inlineKeyboardButton1);
        rowInline2.add(inlineKeyboardButton2);
        rowInline3.add(inlineKeyboardButton3);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);


        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

}
