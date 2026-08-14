package com.shopsphere.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {

    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
}
