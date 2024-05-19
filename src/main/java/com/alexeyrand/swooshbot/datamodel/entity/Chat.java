package com.alexeyrand.swooshbot.datamodel.entity;

import com.alexeyrand.swooshbot.telegram.enums.State;
import jakarta.persistence.*;
import lombok.*;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;
import static com.alexeyrand.swooshbot.telegram.enums.State.WAIT_FREE_PUBLISH;

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
    private Long Id;

    /** id чата в телеграме (чат == пользователь) */
    private Long chatId;

    /** username пользователя в телеграме */
    private String username;
    /** Состояние блокировки потока */

    @Builder.Default
    private Boolean block = false;

    /** Состояние пользователя */
    @Builder.Default
    private State state = NO_WAITING;

    /** Оплачена ли услуга "публикация вне очереди" */
    @Builder.Default
    private Boolean paidPublishStatus = false;

    /** Оплачена ли услуга "оформление накладной СДЕК" */
    @Builder.Default
    private Boolean sdekStatus = false;
}
