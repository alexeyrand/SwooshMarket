package com.alexeyrand.swooshbot.datamodel.dto.calculator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Package {
    Integer height;
    Integer length;
    Integer weight;
    Integer width;
}
