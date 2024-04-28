package com.alexeyrand.swooshbot.datamodel.entity;

import com.alexeyrand.swooshbot.telegram.enums.State;
import jakarta.persistence.*;
import lombok.*;

import static com.alexeyrand.swooshbot.telegram.enums.State.NO_WAITING;
import static com.alexeyrand.swooshbot.telegram.enums.State.WAIT_FREE_PUBLISH;

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
    @Column(name="id")
    Long Id;

    Long chatId;

    @Builder.Default
    Boolean block = true;

    @Builder.Default
    State state = NO_WAITING;
}
