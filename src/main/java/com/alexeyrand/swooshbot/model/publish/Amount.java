package com.alexeyrand.swooshbot.model.publish;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Getter
@Setter
public class Amount {
    String value;
    String currency;
}
