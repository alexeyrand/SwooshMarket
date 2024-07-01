package com.alexeyrand.swooshbot.api.service;

import com.alexeyrand.swooshbot.model.entity.sdek.CdekOrderInfo;
import com.alexeyrand.swooshbot.api.repository.CdekOrderInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CdekOrderInfoService {
    private final CdekOrderInfoRepository cdekOrderInfoRepository;

    public void save(CdekOrderInfo cdekOrderInfo) {
        cdekOrderInfoRepository.save(cdekOrderInfo);
    }



}
