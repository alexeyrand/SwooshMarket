package com.alexeyrand.swooshbot.datamodel.dto.calculator;

import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateCostResponse {
    List<CostInfo> tariff_codes;
}
