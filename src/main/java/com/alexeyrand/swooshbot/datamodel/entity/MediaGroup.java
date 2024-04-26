package com.alexeyrand.swooshbot.datamodel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "media_group")
@AllArgsConstructor
@RequiredArgsConstructor
public class MediaGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long Id;

    Long chatId;

    @Builder.Default
    Boolean wait = false;
}
