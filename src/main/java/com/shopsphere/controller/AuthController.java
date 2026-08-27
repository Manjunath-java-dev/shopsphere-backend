package com.shopsphere.controller;

import com.shopsphere.dto.request.ForgotPasswordRequest;
import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.request.RegisterRequest;
import com.shopsphere.dto.request.ResetPasswordRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.LoginResponse;
import com.shopsphere.dto.response.RegisterResponse;
import com.shopsphere.service.PasswordResetService;
import com.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            UserService userService,
            PasswordResetService passwordResetService) {

        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {

        RegisterResponse response =
                userService.register(registerRequest);

        return ApiResponse.<RegisterResponse>builder()
                .success(true)
                .message("User register successfully")
                .data(response)
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse =
                userService.login(loginRequest);

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.forgotPassword(
                request.getEmail()
        );

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Password reset link sent to your email")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Password reset successfully")
                .build();
    }
}