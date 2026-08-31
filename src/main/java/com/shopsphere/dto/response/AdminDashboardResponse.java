
        package com.shopsphere.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private long totalCustomers;

    private long totalProducts;

    private long totalOrders;

    private long pendingOrders;

    private long confirmedOrders;

    private long shippedOrders;

    private long deliveredOrders;

    private long cancelledOrders;

    private double totalSales;
}

