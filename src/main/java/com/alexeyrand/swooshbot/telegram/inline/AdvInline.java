package com.alexeyrand.swooshbot.telegram.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdvInline {
    public InlineKeyboardMarkup getAdvInline() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Узнать расценки");
        inlineKeyboardButton1.setCallbackData("adv/price");
        inlineKeyboardButton1.setUrl("https://telegra.ph/Reklama-v-SVUSH-05-30");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Заказать рекламу");
        inlineKeyboardButton2.setCallbackData("adv/order");
        inlineKeyboardButton2.setUrl("https://t.me/skkwy");

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Назад");
        inlineKeyboardButton3.setCallbackData("menu");

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
