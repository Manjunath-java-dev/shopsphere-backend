package com.shopsphere.controller;

import com.shopsphere.dto.request.CreateOrderRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.OrderResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/customer/orders")
@SecurityRequirement(name = "bearerAuth")
public class CustomerOrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public CustomerOrderController(
            OrderService orderService,
            UserRepository userRepository) {

        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest orderRequest,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        OrderResponse orderResponse =
                orderService.createOrder(user,orderRequest.getAddressId());

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order created successfully")
                .data(orderResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        List<OrderResponse> orderResponses =
                orderService.getAllOrders(user);

        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("Orders fetched successfully")
                .data(orderResponses)
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getMyOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        OrderResponse orderResponse =
                orderService.getOrderById(orderId, user);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order fetched successfully")
                .data(orderResponse)
                .build();
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        OrderResponse orderResponse =
                orderService.cancelOrder(orderId, user);

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order cancelled successfully")
                .data(orderResponse)
                .build();
    }
}