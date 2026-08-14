package com.shopsphere.dto.response;

import com.shopsphere.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderResponse {

    private Long id;
    private Double totalAmount;
    private OrderStatus status;
    private List<OrderItemResponse> items;
}
