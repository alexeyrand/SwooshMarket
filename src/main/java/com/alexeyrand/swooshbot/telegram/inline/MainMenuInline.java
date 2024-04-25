package com.alexeyrand.swooshbot.telegram.inline;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenuInline {

    public InlineKeyboardMarkup getMenuInline() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("В главное меню");
        inlineKeyboardButton1.setCallbackData("publish/free/success");

        rowInline1.add(inlineKeyboardButton1);

        rowsInline.add(rowInline1);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
