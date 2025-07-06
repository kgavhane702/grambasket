package com.grambasket.userservice.service;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.model.Address;

public interface AddressService {
    UserResponse addAddress(String authId, Address newAddress);
    UserResponse updateAddress(String authId, String addressId, Address updatedAddress);
    void deleteAddress(String authId, String addressId);
    UserResponse setDefaultAddress(String authId, String addressId);
}