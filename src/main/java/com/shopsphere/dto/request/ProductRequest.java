package com.shopsphere.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private String brand;

    private String color;

    private String variant;

    private Long categoryId;
}
