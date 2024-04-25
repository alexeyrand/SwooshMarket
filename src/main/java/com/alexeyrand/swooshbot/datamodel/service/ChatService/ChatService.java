package com.alexeyrand.swooshbot.datamodel.service.ChatService;

import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import com.alexeyrand.swooshbot.datamodel.repository.ChatRepository;
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

    public void update(Long chatId, Boolean wait) {
        Chat chat = chatRepository.findByChatId(chatId).orElse(Chat.builder().chatId(chatId).wait(wait).build());
        chat.setWait(wait);
        chatRepository.save(chat);
    }


}
