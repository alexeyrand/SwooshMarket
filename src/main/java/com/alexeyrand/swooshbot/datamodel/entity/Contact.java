package com.alexeyrand.swooshbot.datamodel.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@RequiredArgsConstructor
public class Contact {
    private final String name;
    private final List<Phone> phones;
}
