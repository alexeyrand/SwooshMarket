package com.alexeyrand.swooshbot.datamodel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class SdekOrderRequest {
    Integer tariff_code;   ///
    String comment;         ///
    String shipment_point;  ///
    String delivery_point;  ///
    Date date_invoice;      ///
    String shipper_name;    ///
    String shipper_address; ///
    Money delivery_recipient_cost;
    Seller seller;
    Contact recipient;
    Location from_location;
    Location to_location;
    List<Package> packages;
}
