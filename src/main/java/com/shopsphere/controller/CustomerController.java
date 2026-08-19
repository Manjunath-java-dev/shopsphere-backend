package com.shopsphere.controller;

import com.shopsphere.dto.request.ChangePasswordRequest;
import com.shopsphere.dto.request.CustomerUpdateRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.CustomerProfileResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.CustomerProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final UserRepository userRepository;
    private final CustomerProfileService customerProfileService;

    public CustomerController(
            UserRepository userRepository,
            CustomerProfileService customerProfileService) {

        this.userRepository = userRepository;
        this.customerProfileService = customerProfileService;
    }

    @GetMapping("/profile")
    public ApiResponse<CustomerProfileResponse> getMyProfile(
            Authentication authentication) {

        // 1. Get email from JWT
        String email = authentication.getName();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 3. Get profile
        CustomerProfileResponse response =
                customerProfileService.getMyProfile(user);

        return ApiResponse.<CustomerProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(response)
                .build();
    }
    @PutMapping("/profile")
    public ApiResponse<CustomerProfileResponse> updateMyProfile(
            @Valid @RequestBody CustomerUpdateRequest request,
            Authentication authentication) {

        // 1. Get email from JWT
        String email = authentication.getName();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 3. Update profile
        CustomerProfileResponse response =
                customerProfileService.updateMyProfile(user, request);

        return ApiResponse.<CustomerProfileResponse>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(response)
                .build();
    }

    @PutMapping("/profile/password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        customerProfileService.changePassword(
                user,
                request.getCurrentPassword(),
                request.getNewPassword());

        return ApiResponse.<String>builder()
                .success(true)
                .message("Password changed successfully")
                .data(null)
                .build();
    }
}