package com.alexeyrand.swooshbot.datamodel.service;

import com.alexeyrand.swooshbot.datamodel.entity.sdek.SdekOrderInfo;
import com.alexeyrand.swooshbot.datamodel.repository.SdekOrderRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SdekOrderRequestService {
    private final SdekOrderRequestRepository sdekOrderRequestRepository;

    public Optional<SdekOrderInfo> findSdekOrderRequestByChatId(Long chatId) {
        return sdekOrderRequestRepository.findByChatId(chatId);
    }
    public void save(SdekOrderInfo sdekOrderInfo) {
        sdekOrderRequestRepository.save(sdekOrderInfo);
    }

}
