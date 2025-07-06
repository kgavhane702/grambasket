package com.grambasket.userservice.service.impl;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.exception.AddressNotFoundException;
import com.grambasket.userservice.exception.UserNotFoundException;
import com.grambasket.userservice.mapper.AddressMapper;
import com.grambasket.userservice.mapper.UserMapper;
import com.grambasket.userservice.model.Address;
import com.grambasket.userservice.model.UserProfile;
import com.grambasket.userservice.repository.UserRepository;
import com.grambasket.userservice.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public UserResponse addAddress(String authId, Address newAddress) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        List<Address> addresses = userProfile.getAddresses();
        newAddress.setId(UUID.randomUUID().toString());

        if (newAddress.isDefault() || addresses.isEmpty()) {
            addresses.forEach(addr -> addr.setDefault(false));
            newAddress.setDefault(true);
        }

        addresses.add(newAddress);
        userProfile.setUpdatedAt(userProfile.getUpdatedAt());
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    @Override
    @Transactional
    public UserResponse updateAddress(String authId, String addressId, Address updatedAddress) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        Address addressToUpdate = findAddressByIdInternal(userProfile, addressId);

        addressMapper.updateAddressFromModel(updatedAddress, addressToUpdate);

        if (updatedAddress.isDefault()) {
            userProfile.getAddresses().forEach(addr -> {
                if (!addr.getId().equals(addressId)) {
                    addr.setDefault(false);
                }
            });
            addressToUpdate.setDefault(true);
        }
        userProfile.setUpdatedAt(userProfile.getUpdatedAt());
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    @Override
    @Transactional
    public void deleteAddress(String authId, String addressId) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        Address addressToDelete = findAddressByIdInternal(userProfile, addressId);

        boolean wasDefault = addressToDelete.isDefault();
        userProfile.getAddresses().remove(addressToDelete);

        if (wasDefault && !userProfile.getAddresses().isEmpty()) {
            userProfile.getAddresses().get(0).setDefault(true);
        }
        userProfile.setUpdatedAt(userProfile.getUpdatedAt());
        userRepository.save(userProfile);
    }

    @Override
    @Transactional
    public UserResponse setDefaultAddress(String authId, String addressId) {
        UserProfile userProfile = findUserByAuthIdInternal(authId);
        Address newDefaultAddress = findAddressByIdInternal(userProfile, addressId);

        userProfile.getAddresses().forEach(addr -> addr.setDefault(false));
        newDefaultAddress.setDefault(true);
        userProfile.setUpdatedAt(userProfile.getUpdatedAt());
        UserProfile savedProfile = userRepository.save(userProfile);
        return userMapper.toUserResponse(savedProfile);
    }

    private UserProfile findUserByAuthIdInternal(String authId) {
        return userRepository.findByAuthId(authId)
                .orElseThrow(() -> new UserNotFoundException("User not found for authId: " + authId));
    }

    private Address findAddressByIdInternal(UserProfile userProfile, String addressId) {
        return userProfile.getAddresses().stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + addressId));
    }
}