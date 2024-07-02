package com.alexeyrand.swooshbot.model.entity.sdek;

import jakarta.persistence.*;
import lombok.*;

/** Таблица заказов накладной СДЭК */
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "cdek_order_info")
@AllArgsConstructor
@RequiredArgsConstructor
public class CdekOrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "username")
    private String username;
    @Column(name = "date")
    private String date;
    @Column(name = "status")
    private String state;
    @Column(name = "uuid")
    private String uuid;

}
