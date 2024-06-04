package com.alexeyrand.swooshbot.api.service;

import com.alexeyrand.swooshbot.model.entity.sdek.SdekOrderInfo;
import com.alexeyrand.swooshbot.api.repository.SdekOrderInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SdekOrderInfoService {
    private final SdekOrderInfoRepository sdekOrderInfoRepository;

    public Optional<SdekOrderInfo> findSdekOrderInfoByChatId(Long chatId) {
        return sdekOrderInfoRepository.findByChatId(chatId);
    }
    public void save(SdekOrderInfo sdekOrderInfo) {
        sdekOrderInfoRepository.save(sdekOrderInfo);
    }

    public void deleteByChatId(Long chatId) {
        sdekOrderInfoRepository.deleteByChatId(chatId);
    }
}
