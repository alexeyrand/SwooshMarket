package com.alexeyrand.swooshbot.model.dto.calculator;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    DeliveryDateRange delivery_date_range;
}
