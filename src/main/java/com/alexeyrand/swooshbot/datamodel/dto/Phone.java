package com.alexeyrand.swooshbot.datamodel.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Phone {
    private final String number;
}