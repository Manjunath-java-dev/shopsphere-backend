package com.shopsphere.controller;

import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.request.RegisterRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.LoginResponse;
import com.shopsphere.dto.response.RegisterResponse;
import com.shopsphere.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
   public AuthController(UserService userService){
       this.userService = userService;
   }
@PostMapping("/register")
public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest){

       RegisterResponse response = userService.register(registerRequest);
       return ApiResponse.<RegisterResponse>builder()
               .success(true)
               .message("User register succesfully")
               .data(response)
               .build();
}

@PostMapping("/login")
public  ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
       LoginResponse loginResponse = userService.login(loginRequest);
       return ApiResponse.<LoginResponse>builder()
               .success(true)
               .message("Login Sucsessful")
               .data(loginResponse)
               .build();
}



}
