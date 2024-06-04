package com.alexeyrand.swooshbot.api.service;

import com.alexeyrand.swooshbot.model.entity.sdek.Order;
import com.alexeyrand.swooshbot.api.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

//    public Optional<Chat> findWaitByChatId(Long chatId) {
//        return chatRepository.findByChatId(chatId);
//    }

    public void save(Order order) {
        orderRepository.save(order);
    }



}
