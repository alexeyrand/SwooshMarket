package com.alexeyrand.swooshbot.datamodel.repository;

import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import com.alexeyrand.swooshbot.datamodel.entity.sdek.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //Optional<Order> findByChatId(Long chatId);
}

