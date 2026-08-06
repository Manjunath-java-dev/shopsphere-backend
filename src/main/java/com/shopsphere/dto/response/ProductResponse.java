package com.shopsphere.dto.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private String brand;

    private String color;

    private String variant;

    private Double rating;

    private String categoryName;
}
