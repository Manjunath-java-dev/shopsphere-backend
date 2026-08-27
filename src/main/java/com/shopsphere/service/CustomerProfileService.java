package com.shopsphere.service;

import com.shopsphere.dto.request.CustomerUpdateRequest;
import com.shopsphere.dto.response.CustomerProfileResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.InvalidCredentialsException;
import com.shopsphere.exception.PhoneAlreadyExistsException;
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


        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }

    public CustomerProfileResponse updateMyProfile(
            User user,
            CustomerUpdateRequest request) {

        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {

            throw new PhoneAlreadyExistsException(
                    "Phone number already exists");
        }

        user.setName(request.getName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        CustomerProfileResponse response =
                new CustomerProfileResponse();

        response.setName(updatedUser.getName());
        response.setEmail(updatedUser.getEmail());
        response.setPhone(updatedUser.getPhone());

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
