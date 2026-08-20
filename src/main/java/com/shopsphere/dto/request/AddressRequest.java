package com.shopsphere.dto.request;

import com.shopsphere.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Invalid pincode"
    )
    private String pincode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    private boolean isDefault;
}
