package com.alexeyrand.swooshbot.model.dto.sdek;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SdekOrderInfoResponse {
    /** uuid заказа */
    String uuid;

    /** Номер заказа в СДЕК */
    String orderNumber;

    /** статус заказа */
    String status;

    /** дата заказа */
    String date;

    /** Предупреждения заказа */
    List<String> warnings;

    //TODO: избавиться от хардкода
    /** Предупреждение заказа */
    String warn;

    /** Ошибки заказа */
    List<String> errors;

    //TODO: избавиться от хардкода
    /** Ошибки заказа */
    String err;









}
