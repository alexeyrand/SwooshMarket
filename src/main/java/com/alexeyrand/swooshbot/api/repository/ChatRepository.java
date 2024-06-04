package com.alexeyrand.swooshbot.api.repository;

import com.alexeyrand.swooshbot.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatId(Long chatId);
}

