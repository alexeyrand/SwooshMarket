package com.alexeyrand.swooshbot.telegram.utils;

import com.alexeyrand.swooshbot.datamodel.service.ChatService;
import com.alexeyrand.swooshbot.datamodel.service.PhotoService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.inline.MainMenuInline;
import com.alexeyrand.swooshbot.telegram.inline.MenuInline;
import com.alexeyrand.swooshbot.telegram.service.QueryHandler;
import com.alexeyrand.swooshbot.telegram.service.Utils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;

@Component
@RequiredArgsConstructor
public class Flag implements Runnable {


    private final TelegramBot telegramBot;
    private final Utils utils;
    private final QueryHandler queryHandler;
    private final MenuInline menuInline;
    private final MainMenuInline mainMenuInline;
    private final ChatService chatService;
    private final PhotoService photoService;

    @Setter
    private Long chatId;
    @Setter
    private Long chatIdChannel;
    @Setter
    private String text;
    @Setter
    private String username;
    @Setter
    private Message message;

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (text == null || text.isBlank()) {

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Объявление не может быть пустым! Заполните информацию о товарах.");
            sendMessage.setChatId(chatId);
            photoService.deleteAllByChatId(chatId);
            //chatService.updateState(chatId, NO_WAITING);
            telegramBot.justSendMessage(sendMessage);
            queryHandler.publishFreeReceived(chatId, -1);
            chatService.updateBlock(chatId, true);

        } else {
            telegramBot.publishAlbum(chatId, text, username, false);
            chatService.updateBlock(chatId, true);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setReplyMarkup(mainMenuInline.getMenuInline());
            message.setText("Готово! Публикация отправлена на модерацию.");
            telegramBot.justSendMessage(message);
        }
    }
}
