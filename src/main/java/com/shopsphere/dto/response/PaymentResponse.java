package com.shopsphere.dto.response;

import com.shopsphere.enums.PaymentMethod;
import com.shopsphere.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {

    private Long id;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Long orderId;
}
