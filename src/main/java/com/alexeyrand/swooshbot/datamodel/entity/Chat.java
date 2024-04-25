package com.alexeyrand.swooshbot.datamodel.entity;

import jakarta.persistence.*;
import lombok.*;

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
    Boolean wait = false;
}
