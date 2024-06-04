package com.alexeyrand.swooshbot.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class City {
    private Integer code;
    private String city_uuid;
    private String city;
    private String fias_guid;
    private String kladr_code;
    private String country_code;
    private String country;
    private String region;
    private Integer region_code;
    private String sub_region;
    private Float longitude;
    private Float latitude;
    private String time_zone;
    private Float payment_limit;
}
