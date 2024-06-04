package com.alexeyrand.swooshbot.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@RequiredArgsConstructor
public class Package {
    private final String number;
    private final Integer weight;
    private final Integer length;
    private final Integer width;
    private final Integer height;
    private final List<Item> items;
}
