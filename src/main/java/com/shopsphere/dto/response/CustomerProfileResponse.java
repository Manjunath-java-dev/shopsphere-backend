package com.shopsphere.dto.response;

import com.shopsphere.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileResponse {

    private String name;
    private String email;
    private String phone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
