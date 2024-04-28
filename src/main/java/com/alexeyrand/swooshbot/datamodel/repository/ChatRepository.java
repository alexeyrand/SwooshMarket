package com.alexeyrand.swooshbot.datamodel.repository;

import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatId(Long chatId);
}

