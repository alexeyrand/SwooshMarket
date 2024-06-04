package com.alexeyrand.swooshbot.api.repository;

import com.alexeyrand.swooshbot.model.entity.sdek.SdekOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SdekOrderInfoRepository extends JpaRepository<SdekOrderInfo, Long> {
    Optional<SdekOrderInfo> findByChatId(Long chatId);
    void deleteByChatId(Long chatId);
}
