package com.shopsphere.controller;

import com.shopsphere.dto.request.AddToWishlistRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.WishlistResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.WishlistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/wishlist")
@SecurityRequirement(name = "bearerAuth")
public class CustomerWishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    public CustomerWishlistController(WishlistService wishlistService,
                                      UserRepository userRepository){
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ApiResponse<WishlistResponse> addToWishlist(@Valid
                                                       @RequestBody AddToWishlistRequest addToWishlistRequest,
                                                       Authentication authentication){
        // 1. Get email from JWT
        String email = authentication.getName();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email).
                orElseThrow(()->new UserNotFoundException("User not found"));

        // 3. Add product to wishlist
        WishlistResponse wishlistResponse = wishlistService.addToWishlist(user,
                addToWishlistRequest.getProductId());

        // 4. Return response
        return ApiResponse.<WishlistResponse>builder()
                .success(true)
                .message("Product added to wishlist successfully")
                .data(wishlistResponse)
                .build();
    }


    @GetMapping
    public ApiResponse<List<WishlistResponse>> getMyWishlist(
            Authentication authentication) {

        // 1. Get email from JWT
        String email = authentication.getName();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 3. Get wishlist
        List<WishlistResponse> responses =
                wishlistService.getMyWishlist(user);

        // 4. Return response
        return ApiResponse.<List<WishlistResponse>>builder()
                .success(true)
                .message("Wishlist fetched successfully")
                .data(responses)
                .build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<String> removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        // 1. Get email from JWT
        String email = authentication.getName();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 3. Remove product
        wishlistService.removeFromWishlist(
                user,
                productId
        );

        // 4. Return response
        return ApiResponse.<String>builder()
                .success(true)
                .message("Product removed from wishlist successfully")
                .data(null)
                .build();
    }
}
