package com.alexeyrand.swooshbot.datamodel.dto.calculator;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostInfo {
    Integer tariff_code;
    String tariff_name;
    String tariff_description;
    Integer delivery_mode;
    Float delivery_sum;
    String period_min;
    String period_max;
    String calendar_min;
    String calendar_max;
}
