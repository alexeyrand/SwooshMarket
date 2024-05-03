package com.alexeyrand.swooshbot.datamodel.service;

import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import com.alexeyrand.swooshbot.datamodel.repository.ChatRepository;
import com.alexeyrand.swooshbot.telegram.enums.State;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public Optional<Chat> findWaitByChatId(Long chatId) {
        return chatRepository.findByChatId(chatId);
    }

    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    public State getState(Long chatId) {
        Chat chat = chatRepository.findByChatId(chatId).orElseThrow();
        return chat.getState();
    }

    public Boolean getPaidPublishStatus(Long chatId) {
        Chat chat = chatRepository.findByChatId(chatId).orElseThrow();
        return chat.getPaidPublishStatus();
    }

    public Boolean isBlock(Long chatId) {
        Chat chat = chatRepository.findByChatId(chatId).orElseThrow();
        return chat.getBlock();
    }

    public void updateState(Long chatId, State state) {
        Chat chat = chatRepository.findByChatId(chatId).orElse(Chat.builder().chatId(chatId).state(state).build());
        chat.setState(state);
        chatRepository.save(chat);
    }

    public void updateBlock(Long chatId, Boolean block) {
        Chat chat = chatRepository.findByChatId(chatId).orElse(Chat.builder().chatId(chatId).build());
        chat.setBlock(block);
        chatRepository.save(chat);
    }

    public void updatePaidPublishStatus(Long chatId, Boolean status) {
        Chat chat = chatRepository.findByChatId(chatId).orElse(Chat.builder().chatId(chatId).build());
        chat.setPaidPublishStatus(status);
        chatRepository.save(chat);
    }

}
