package com.shopsphere.service;

import com.shopsphere.dto.response.AdminCustomerResponse;
import com.shopsphere.entity.User;
import com.shopsphere.enums.Role;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCustomerService {


    private final UserRepository userRepository;

    public AdminCustomerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AdminCustomerResponse> getAllCustomers() {

        List<User> customers =
                userRepository.findByRole(Role.CUSTOMER);

        return customers.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public AdminCustomerResponse getCustomerById(Long id) {

        User customer = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Customer not found with id: " + id
                        ));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new UserNotFoundException(
                    "Customer not found with id: " + id
            );
        }

        return mapToResponse(customer);
    }

    public List<AdminCustomerResponse> searchCustomers(String name) {

        List<User> customers =
                userRepository
                        .findByRoleAndNameContainingIgnoreCase(
                                Role.CUSTOMER,
                                name
                        );

        return customers.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AdminCustomerResponse mapToResponse(User user) {

        return AdminCustomerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


}
