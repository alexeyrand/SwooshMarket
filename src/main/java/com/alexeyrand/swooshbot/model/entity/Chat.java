package com.alexeyrand.swooshbot.model.entity;

import com.alexeyrand.swooshbot.telegram.enums.State;
import jakarta.persistence.*;
import lombok.*;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;

/** Таблица чатов в телеграм */
@Entity
@Getter
@Setter
@Builder
@Table(name = "chat")
@AllArgsConstructor
@RequiredArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** id чата в телеграме (чат == пользователь) */
    @Column(name = "chat_id")
    private Long chatId;

    /** username пользователя в телеграме */
    @Column(name = "username")
    private String username;
    /** Состояние блокировки потока */

    @Builder.Default
    private Boolean block = false;

    /** Состояние пользователя */
    @Builder.Default
    private State state = NO_WAITING;

    /** Оплачена ли услуга "публикация вне очереди" */
    @Builder.Default
    @Column(name = "paid_publish_status")
    private Boolean paidPublishStatus = false;

    /** Оплачена ли услуга "оформление накладной СДЕК" */
    @Builder.Default
    @Column(name = "cdek_status")
    private Boolean sdekStatus = false;
}
