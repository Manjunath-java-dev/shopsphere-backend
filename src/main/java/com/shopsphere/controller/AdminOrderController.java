package com.shopsphere.controller;

import com.shopsphere.dto.request.UpdateOrderStatusRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.OrderResponse;
import com.shopsphere.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin/orders")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {
    private final OrderService orderService;
    public AdminOrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(){
        List<OrderResponse> orderResponses = orderService.getAllOrdersForAdmin();
        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("All orders fetched successfully")
                .data(orderResponses)
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderByIdForAdmin(@PathVariable Long orderId){
        OrderResponse orderResponse = orderService.getOrderByIdForAdmin(orderId);
        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order fetched successfully")
                .data(orderResponse)
                .build();
    }

    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request){

      OrderResponse orderResponse = orderService.updateOrderStatus(orderId , request.getOrderStatus());
        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order status updated successfully")
                .data(orderResponse)
                .build();
    }
}
