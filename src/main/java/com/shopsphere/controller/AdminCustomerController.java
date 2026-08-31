package com.shopsphere.controller;

import com.shopsphere.dto.response.AdminCustomerResponse;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.service.AdminCustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/customers")
@SecurityRequirement(name = "bearerAuth")
public class AdminCustomerController {


    private final AdminCustomerService adminCustomerService;

    public AdminCustomerController(
            AdminCustomerService adminCustomerService) {

        this.adminCustomerService = adminCustomerService;
    }

    @GetMapping
    public ApiResponse<List<AdminCustomerResponse>> getAllCustomers() {

        List<AdminCustomerResponse> customers =
                adminCustomerService.getAllCustomers();

        return ApiResponse.<List<AdminCustomerResponse>>builder()
                .success(true)
                .message("Customers fetched successfully")
                .data(customers)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminCustomerResponse> getCustomerById(
            @PathVariable Long id) {

        AdminCustomerResponse customer =
                adminCustomerService.getCustomerById(id);

        return ApiResponse.<AdminCustomerResponse>builder()
                .success(true)
                .message("Customer fetched successfully")
                .data(customer)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<AdminCustomerResponse>> searchCustomers(
            @RequestParam String name) {

        List<AdminCustomerResponse> customers =
                adminCustomerService.searchCustomers(name);

        return ApiResponse.<List<AdminCustomerResponse>>builder()
                .success(true)
                .message("Customers fetched successfully")
                .data(customers)
                .build();
    }


}
