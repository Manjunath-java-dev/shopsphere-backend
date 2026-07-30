package com.shopsphere.dto.response;

import com.shopsphere.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long id;

    private String name;

    private String email;

    private String phone;

    private Role role;


}
