package com.alexeyrand.swooshbot.model.publish;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class Receipt {
//    String email;
    List<Item> items;
}
