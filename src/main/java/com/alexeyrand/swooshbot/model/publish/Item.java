package com.alexeyrand.swooshbot.model.publish;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    String description;
    String quantity;
    Amount amount;
    Integer vat_code;
}
