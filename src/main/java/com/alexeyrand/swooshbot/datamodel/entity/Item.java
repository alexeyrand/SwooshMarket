package com.alexeyrand.swooshbot.datamodel.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Item {
    private final String name;
    private final String ware_key;
    private final Money payment;
    private final Float cost;
    private final Float weight;
    private Integer weight_gross;
    private final Integer amount;


}
