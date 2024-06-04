package com.alexeyrand.swooshbot.telegram.inline;

import com.alexeyrand.swooshbot.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PublishPaidInline {

    private final ChatService chatService;

    public InlineKeyboardMarkup getPublishPaidInline(String response, Long  chatId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1;

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Назад");
        inlineKeyboardButton2.setCallbackData("publish/free/back");

        if (chatService.getPaidPublishStatus(chatId)) {
            inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Отправить публикацию");
            inlineKeyboardButton1.setCallbackData("publish/paid/publish");
        } else {
            inlineKeyboardButton1 = new InlineKeyboardButton();
            inlineKeyboardButton1.setText("Оплатить");
            inlineKeyboardButton1.setCallbackData("ppublish/paid/pay");
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
}
