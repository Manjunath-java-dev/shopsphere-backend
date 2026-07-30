package com.shopsphere.service;

import com.shopsphere.dto.request.LoginRequest;
import com.shopsphere.dto.request.RegisterRequest;
import com.shopsphere.dto.response.LoginResponse;
import com.shopsphere.dto.response.RegisterResponse;
import com.shopsphere.entity.User;
import com.shopsphere.enums.Role;
import com.shopsphere.exception.EmailAlreadyExistsException;
import com.shopsphere.exception.InvalidCredentialsException;
import com.shopsphere.exception.PhoneAlreadyExistsException;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService){

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest registerRequest){
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .role(Role.CUSTOMER)
                .build();
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if(userRepository.existsByPhone(registerRequest.getPhone())){
            throw new PhoneAlreadyExistsException("Phone number already exists");
        }
        User savedUser = userRepository.save(user);
        return RegisterResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole())
                .build();
    }
    public LoginResponse login(LoginRequest loginRequest){
     User user =    userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()
        ->new UserNotFoundException("User with this email not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
       String token = jwtService.generateToken(user.getEmail());

        return LoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();

    }


}

