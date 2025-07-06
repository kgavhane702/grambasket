package com.grambasket.userservice.controller;

import com.grambasket.userservice.dto.UserResponse;
import com.grambasket.userservice.model.Address;
import com.grambasket.userservice.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-service/users/me/addresses")
@RequiredArgsConstructor
@Tag(name = "User Addresses", description = "Endpoints for managing a user's shipping and billing addresses")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Add a new address", description = "Adds a new address to the authenticated user's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid address data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponse> addAddress(Authentication authentication, @Valid @RequestBody Address newAddress) {
        String authId = authentication.getName();
        return ResponseEntity.ok(addressService.addAddress(authId, newAddress));
    }

    @Operation(summary = "Update an existing address", description = "Updates a specific address by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid address data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<UserResponse> updateAddress(Authentication authentication, @PathVariable String addressId, @Valid @RequestBody Address updatedAddress) {
        String authId = authentication.getName();
        return ResponseEntity.ok(addressService.updateAddress(authId, addressId, updatedAddress));
    }

    @Operation(summary = "Delete an address", description = "Permanently deletes a specific address by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(Authentication authentication, @PathVariable String addressId) {
        String authId = authentication.getName();
        addressService.deleteAddress(authId, addressId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Set a default address", description = "Marks a specific address as the user's default address for shipping.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Default address set successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found", content = @Content)
    })
    @PutMapping("/{addressId}/default") // Changed from POST to PUT and refined the path
    public ResponseEntity<UserResponse> setDefaultAddress(Authentication authentication, @PathVariable String addressId) {
        String authId = authentication.getName();
        return ResponseEntity.ok(addressService.setDefaultAddress(authId, addressId));
    }
}