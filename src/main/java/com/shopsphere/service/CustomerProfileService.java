package com.shopsphere.service;

import com.shopsphere.dto.request.CustomerUpdateRequest;
import com.shopsphere.dto.response.CustomerProfileResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.InvalidCredentialsException;
import com.shopsphere.repositoy.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public CustomerProfileService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerProfileResponse getMyProfile(User user) {

        CustomerProfileResponse response =
                new CustomerProfileResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public CustomerProfileResponse updateMyProfile(
            User user,
            CustomerUpdateRequest request) {

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        CustomerProfileResponse response =
                new CustomerProfileResponse();

        response.setId(updatedUser.getId());
        response.setName(updatedUser.getName());
        response.setEmail(updatedUser.getEmail());
        response.setPhone(updatedUser.getPhone());
        response.setRole(updatedUser.getRole());
        response.setCreatedAt(updatedUser.getCreatedAt());
        response.setUpdatedAt(updatedUser.getUpdatedAt());

        return response;
    }

    public void changePassword(
            User user,
            String currentPassword,
            String newPassword) {

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            throw new InvalidCredentialsException(
                    "Current password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }
}
