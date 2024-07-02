package com.alexeyrand.swooshbot.api.repository;

import com.alexeyrand.swooshbot.model.entity.sdek.CdekOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdekOrderInfoRepository extends JpaRepository<CdekOrderInfo, Long> {
}

