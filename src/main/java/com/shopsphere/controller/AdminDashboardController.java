package com.shopsphere.controller;
import com.shopsphere.dto.response.AdminDashboardResponse;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService) {

        this.adminDashboardService =
                adminDashboardService;
    }

    @GetMapping
    public ApiResponse<AdminDashboardResponse> getDashboard() {

        AdminDashboardResponse dashboard =
                adminDashboardService.getDashboardData();

        return ApiResponse.<AdminDashboardResponse>builder()

                .success(true)

                .message(
                        "Admin dashboard data fetched successfully"
                )

                .data(dashboard)

                .build();
    }
}
