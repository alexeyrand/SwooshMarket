package com.alexeyrand.swooshbot.telegram.inline;

import com.alexeyrand.swooshbot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuInline {

    @Autowired
    private BotConfig config;

    public InlineKeyboardMarkup getMenuInline(Long chatId) {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline6 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Публикация поста");
        inlineKeyboardButton1.setCallbackData("publish");

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Легит Чек");
        inlineKeyboardButton2.setCallbackData("legit");

        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Сдек");
        inlineKeyboardButton3.setCallbackData("sdek");

        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("Гарант");
        inlineKeyboardButton4.setCallbackData("garant");

        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton5.setText("Реклама");
        inlineKeyboardButton5.setCallbackData("adv");

        InlineKeyboardButton inlineKeyboardButton6 = new InlineKeyboardButton();
        inlineKeyboardButton6.setText("Настройки");
        inlineKeyboardButton6.setCallbackData("settings");

        //inlineKeyboardButton1.setCallbackData(shopSplit[4]);

        rowInline1.add(inlineKeyboardButton1);
        rowInline2.add(inlineKeyboardButton2);
        rowInline3.add(inlineKeyboardButton3);
        rowInline4.add(inlineKeyboardButton4);
        rowInline5.add(inlineKeyboardButton5);


        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);

        if (chatId.equals(658756678L)  || chatId.equals(430899374L)) {
            rowInline6.add(inlineKeyboardButton6);
            rowsInline.add(rowInline6);
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

}
