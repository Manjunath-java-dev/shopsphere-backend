package com.shopsphere.controller;


import com.shopsphere.dto.request.AddressRequest;
import com.shopsphere.dto.response.AddressResponse;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {
    private final AddressService addressService;
    private final UserRepository userRepository;

    public AddressController(AddressService addressService , UserRepository userRepository){
        this.addressService = addressService;
        this.userRepository = userRepository;
    }

    // 1 add address
    @PostMapping
    public ApiResponse<AddressResponse> addAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication){
       String email =  authentication.getName();
     User user = userRepository.findByEmail(email)
               .orElseThrow(()->new UserNotFoundException("User not found"));
     AddressResponse addressResponse = addressService.addAddress(user,request);

     return ApiResponse.<AddressResponse>builder()
             .success(true)
             .message("Address added successfulyy")
             .data(addressResponse)
             .build();
    }

    // 2. Get all my addresses
    @GetMapping
    public ApiResponse<List<AddressResponse>> getMyAddresses(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        List<AddressResponse> responses =
                addressService.getMyAddresses(user);

        return ApiResponse.<List<AddressResponse>>builder()
                .success(true)
                .message("Addresses fetched successfully")
                .data(responses)
                .build();
    }

    // 3. Get one address
    @GetMapping("/{addressId}")
    public ApiResponse<AddressResponse> getAddress(
            @PathVariable Long addressId,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        AddressResponse response =
                addressService.getAddress(user, addressId);

        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address fetched successfully")
                .data(response)
                .build();
    }

    // 4. Update address
    @PutMapping("/{addressId}")
    public ApiResponse<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        AddressResponse response =
                addressService.updateAddress(
                        user,
                        addressId,
                        request
                );

        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address updated successfully")
                .data(response)
                .build();
    }

    // 5. Delete address
    @DeleteMapping("/{addressId}")
    public ApiResponse<Void> deleteAddress(
            @PathVariable Long addressId,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        addressService.deleteAddress(user, addressId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Address deleted successfully")
                .data(null)
                .build();
    }
}
