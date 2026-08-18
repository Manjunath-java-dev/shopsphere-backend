package com.shopsphere.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Double price;
}