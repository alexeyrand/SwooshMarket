package com.alexeyrand.swooshbot.datamodel.service;

import com.alexeyrand.swooshbot.datamodel.entity.Chat;
import com.alexeyrand.swooshbot.datamodel.entity.sdek.Order;
import com.alexeyrand.swooshbot.datamodel.repository.ChatRepository;
import com.alexeyrand.swooshbot.datamodel.repository.OrderRepository;
import com.alexeyrand.swooshbot.telegram.enums.State;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
