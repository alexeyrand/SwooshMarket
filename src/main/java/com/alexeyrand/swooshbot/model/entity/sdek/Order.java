package com.alexeyrand.swooshbot.model.entity.sdek;

import jakarta.persistence.*;
import lombok.*;

/** Таблица заказов накладной СДЭК */
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "sdek_order")
@AllArgsConstructor
@RequiredArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "username")
    private String username;
    @Column(name = "date_time")
    private String dateTime;
    @Column(name = "status")
    private String state;
    @Column(name = "uuid")
    private String uuid;

}
