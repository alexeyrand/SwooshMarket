package com.alexeyrand.swooshbot.model.dto.calculator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class DeliveryDateRange {
    Date min;
    Date max;
}
