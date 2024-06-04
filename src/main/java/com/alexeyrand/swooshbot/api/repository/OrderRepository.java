package com.alexeyrand.swooshbot.api.repository;

import com.alexeyrand.swooshbot.model.entity.sdek.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //Optional<Order> findByChatId(Long chatId);
}

