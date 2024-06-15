package com.alexeyrand.swooshbot.telegram.core;

import com.alexeyrand.swooshbot.api.service.ChatService;
import com.alexeyrand.swooshbot.telegram.TelegramBot;
import com.alexeyrand.swooshbot.telegram.enums.State;
import com.alexeyrand.swooshbot.telegram.inline.SettingsInline;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.alexeyrand.swooshbot.telegram.enums.State.*;

@Component
@RequiredArgsConstructor
public class Settings {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;

    private final SettingsInline settingsInline;

    private final ChatService chatService;

    @SneakyThrows
    public void showTextPublish(Long chatId, Integer messageId, String data) {
        String num = String.valueOf(data.charAt(data.length() - 1));
        String suffix = getPublishPath(num);

        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\" + suffix);
        String content = Files.readString(path);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Текст сейчас: \n\n" + content);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(settingsInline.getEditPublishTextInline(num));
        telegramBot.justSendMessage(sendMessage);
    }

    @SneakyThrows
    public void showTextMainMenu(Long chatId, Integer messageId, String data, String url) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\" + url + "\\" + url + ".txt");
        String content = Files.readString(path);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Текст сейчас: \n\n" + content);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(settingsInline.getEditInline(url));
        telegramBot.justSendMessage(sendMessage);
    }

    @SneakyThrows
    public void showTextLegit(Long chatId, Integer messageId, String data) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\legit\\legit.txt");
        String content = Files.readString(path);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Текст сейчас: \n\n" + content);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(settingsInline.getEditInline("legit"));
        telegramBot.justSendMessage(sendMessage);
    }

    @SneakyThrows
    public void showTextAdv(Long chatId, Integer messageId, String data) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\adv\\adv.txt");
        String content = Files.readString(path);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Текст сейчас: \n\n" + content);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(settingsInline.getEditInline("adv"));
        telegramBot.justSendMessage(sendMessage);
    }

    @SneakyThrows
    public void showTextGarant(Long chatId, Integer messageId, String data) {
        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\garant\\garant.txt");
        String content = Files.readString(path);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Текст сейчас: \n\n" + content);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(settingsInline.getEditInline("garant"));
        telegramBot.justSendMessage(sendMessage);
    }

    @SneakyThrows
    public void editMainMenu(Long chatId, Integer messageId, String text, String url, State state) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, state);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\" + url + "\\" + url + ".txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(settingsInline.getBackInline());
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SneakyThrows
    public void edit1(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_PUBLISH_1);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\menu.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(settingsInline.getBackInline());
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void edit2(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_PUBLISH_2);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\free.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Отправьте новый текст");
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(settingsInline.getBackInline());
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void edit3(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_PUBLISH_3);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\paid.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(settingsInline.getBackInline());
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void edit4(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_PUBLISH_4);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\publish\\check.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(settingsInline.getBackInline());
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SneakyThrows
    public void showTextCdek(Long chatId, Integer messageId, String data) {
        chatService.updateState(chatId, NO_WAITING);
        String num = String.valueOf(data.charAt(data.length() - 1));
        String suffix = getCdekPath(num);

        Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\cdek\\" + suffix);
        String content = Files.readString(path);
        telegramBot.deleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Текст сейчас: \n\n" + content);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(settingsInline.getEditCdekTextInline(num));
        telegramBot.justSendMessage(sendMessage);
    }

    @SneakyThrows
    public void cdekEdit1(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_PUBLISH_4);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\cdek\\cdek.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void cdekEdit2(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_PUBLISH_4);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\cdek\\cdekInfo.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void legitEdit(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_LEGIT);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\legit\\legit.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void advEdit(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_ADV);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\adv\\adv.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public void garantEdit(Long chatId, Integer messageId, String text) {
        if (chatService.getState(chatId).equals(NO_WAITING)) {
            chatService.updateState(chatId, WAIT_EDIT_GARANT);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Отправьте новый текст");
            sendMessage.setChatId(chatId);
            sendMessage.setReplyMarkup(settingsInline.getBackInline());
            telegramBot.justSendMessage(sendMessage);
        } else {
            try {
                Path path = Paths.get("D:\\jprojects\\SwooshBot\\src\\main\\resources\\text\\garant\\garant.txt");
                Files.writeString(path, "", StandardOpenOption.DELETE_ON_CLOSE);
                Files.writeString(path, text, StandardOpenOption.CREATE);
                chatService.updateState(chatId, NO_WAITING);
                telegramBot.deleteMessage(chatId, messageId);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Новый текст: \n\n" + text);
                sendMessage.setChatId(chatId);
                telegramBot.justSendMessage(sendMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPublishPath(String num) {
        switch (num) {
            case "1" -> {
                return "menu.txt";
            }
            case "2" -> {
                return "free.txt";
            }
            case "3" -> {
                return "paid.txt";
            }
            case "4" -> {
                return "check.txt";
            }
            default -> {
                return "menu.txt";
            }
        }
    }

    private String getCdekPath(String num) {
        switch (num) {
            case "1" -> {
                return "cdek.txt";
            }
            case "2" -> {
                return "cdekInfo.txt";
            }
            default -> {
                return "cdek.txt";
            }
        }
    }
}


