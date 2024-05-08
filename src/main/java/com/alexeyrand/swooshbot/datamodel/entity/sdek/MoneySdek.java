package com.alexeyrand.swooshbot.datamodel.entity.sdek;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "money")
@AllArgsConstructor
@RequiredArgsConstructor
public class MoneySdek {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long id;
    Long chatId;
    Float value;

}
