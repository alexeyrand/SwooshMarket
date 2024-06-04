package com.alexeyrand.swooshbot.api.service;

import com.alexeyrand.swooshbot.api.repository.ChatRepository;
import com.alexeyrand.swooshbot.api.repository.PublishOrderInfoRepository;
import com.alexeyrand.swooshbot.model.entity.Chat;
import com.alexeyrand.swooshbot.model.entity.publish.PublishOrderInfo;
import com.alexeyrand.swooshbot.telegram.enums.State;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PublishOrderInfoService {
    private final PublishOrderInfoRepository publishOrderInfoRepository;
    public void save(PublishOrderInfo publishOrderInfo) {
        publishOrderInfoRepository.save(publishOrderInfo);
    }
}
