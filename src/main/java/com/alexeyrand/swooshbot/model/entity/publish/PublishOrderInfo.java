package com.alexeyrand.swooshbot.model.entity.publish;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/** Таблица публикаций в телеграме */
@Entity
@Getter
@Setter
@Builder
@Table(name = "publish_order_info")
@AllArgsConstructor
@RequiredArgsConstructor
public class PublishOrderInfo {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    private String username;

    private Date date;

    @Column(name = "order_type")
    private String orderType;    // тип заказа: paid - платная публикация, free - бесплатная публикация
}
