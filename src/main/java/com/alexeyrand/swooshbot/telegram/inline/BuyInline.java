package com.alexeyrand.swooshbot.telegram.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class BuyInline {
    public InlineKeyboardMarkup getBuyInline(String response) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
//
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Оплата");
        inlineKeyboardButton1.setCallbackData("buy");
        inlineKeyboardButton1.setUrl(response);
        inlineKeyboardButton1.setPay(true);

        rowInline1.add(inlineKeyboardButton1);

        rowsInline.add(rowInline1);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
