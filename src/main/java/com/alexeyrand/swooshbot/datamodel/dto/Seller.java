package com.alexeyrand.swooshbot.datamodel.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Seller {
    String name;
    String phone;
    String address;
    Contact recipient;

}