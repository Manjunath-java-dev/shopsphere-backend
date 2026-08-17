package com.shopsphere.controller;

import com.shopsphere.dto.request.MakePaymentRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.PaymentResponse;
import com.shopsphere.entity.Payment;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.PaymentRepository;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    public PaymentController(PaymentService paymentService, UserRepository userRepository){
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{orderId}")
    public ApiResponse<PaymentResponse> makePayment(
            @PathVariable Long orderId,
            @Valid @RequestBody MakePaymentRequest makePaymentRequest,
            Authentication authentication){

        // 1. Get logged-in user's email from JWT
       String email =  authentication.getName();

        // 2. Find User
     User user = userRepository.findByEmail(email)
               .orElseThrow(()->new UserNotFoundException("User not found"));

       //3. Make payment
       PaymentResponse paymentResponse = paymentService.makePayment(orderId,
                user,
                makePaymentRequest.getPaymentMethod());

       return ApiResponse.<PaymentResponse
                       >builder()
               .success(true)
               .message("Payment successful")
               .data(paymentResponse)
               .build();

    }
}
