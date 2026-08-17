package com.shopsphere.dto.request;

import com.shopsphere.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MakePaymentRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
