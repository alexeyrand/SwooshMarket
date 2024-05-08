package com.alexeyrand.swooshbot.telegram.inline;

import com.alexeyrand.swooshbot.config.BotConfig;
import com.alexeyrand.swooshbot.datamodel.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;

@Component
@RequiredArgsConstructor
public class SdekInline {
    private final BotConfig config;




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

}
