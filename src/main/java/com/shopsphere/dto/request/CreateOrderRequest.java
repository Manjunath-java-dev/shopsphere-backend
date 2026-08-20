package com.shopsphere.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull(message = "Address ID is required")
    private Long addressId;
}
