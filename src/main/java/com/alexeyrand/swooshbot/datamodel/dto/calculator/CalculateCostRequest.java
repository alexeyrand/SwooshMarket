package com.alexeyrand.swooshbot.datamodel.dto.calculator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CalculateCostRequest {
    Integer currency;
    Integer tariff_code;
    Location from_location;
    Location to_location;
    List<Package> packages;
}
