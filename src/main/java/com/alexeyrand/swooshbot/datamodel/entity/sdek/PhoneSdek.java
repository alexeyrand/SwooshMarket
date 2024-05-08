package com.alexeyrand.swooshbot.datamodel.entity.sdek;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "phone")
@AllArgsConstructor
@RequiredArgsConstructor
public class PhoneSdek {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;
    Long chatId;
    String number;
}
