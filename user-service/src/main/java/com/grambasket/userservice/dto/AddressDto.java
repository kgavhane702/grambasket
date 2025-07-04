package com.grambasket.userservice.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String addressType;
}