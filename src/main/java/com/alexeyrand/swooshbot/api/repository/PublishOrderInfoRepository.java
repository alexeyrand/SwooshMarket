package com.alexeyrand.swooshbot.api.repository;

import com.alexeyrand.swooshbot.model.entity.Chat;
import com.alexeyrand.swooshbot.model.entity.publish.PublishOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublishOrderInfoRepository extends JpaRepository<PublishOrderInfo, Long> {

}

