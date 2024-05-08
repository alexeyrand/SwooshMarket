package com.alexeyrand.swooshbot.datamodel.entity.publish;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "photo")
@AllArgsConstructor
@RequiredArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long Id;

    Long chatId;

    String photo;
}
