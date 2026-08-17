package com.shopsphere.controller;

import com.shopsphere.dto.request.AddToCartRequest;
import com.shopsphere.dto.request.UpdateCartItemRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.CartItemResponse;
import com.shopsphere.dto.response.CartResponse;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/cart")
@SecurityRequirement(name = "bearerAuth")
public class CustomerCartController {
    private final CartService cartService;
    private final UserRepository userRepository;

    public CustomerCartController(CartService cartService, UserRepository userRepository){
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ApiResponse<CartItem> addToCart(
            @RequestBody AddToCartRequest addToCartRequest,
            Authentication authentication){
        //1 Get logged-in user's email from JWT
     String email = authentication.getName();
     //2 Find User from database
     User user = userRepository.findByEmail(email).
             orElseThrow(()-> new UserNotFoundException("User not found"));

     //3 Send User + productId + quantity to service
        cartService.addToCart(user,
                addToCartRequest.getProductId(),
                addToCartRequest.getQuantity()
        );

        //4 Return response
        return ApiResponse.<CartItem>builder()
                .success(true)
                .message("Product added to cart successfully")
                .build();

    }

    @GetMapping
    public ApiResponse<CartResponse> getMyCart( Authentication authentication){
       String email = authentication.getName();
      User user = userRepository.findByEmail(email).orElseThrow(()->
               new UserNotFoundException("User not found"));

     CartResponse cartResponse = cartService.getMyCart(user);
     return ApiResponse.<CartResponse>builder()
             .success(true)
             .message("Cart fetched successfully")
             .data(cartResponse)
             .build();

    }

    @PutMapping("/{cartItemId}")
    public ApiResponse<CartItemResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        CartItem cartItem = cartService.updateCartItem(
                user,
                cartItemId,
                request.getQuantity()
        );

        CartItemResponse response = CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .price(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .build();

        return ApiResponse.<CartItemResponse>builder()
                .success(true)
                .message("Cart item updated successfully")
                .data(response)
                .build();
    }
    @DeleteMapping("/{cartItemId}")
    public ApiResponse<String> removeCartItem(
            @PathVariable Long cartItemId,
            Authentication authentication) {

        // 1. Get logged-in user's email
        String email = authentication.getName();

        // 2. Find User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 3. Delete cart item
        cartService.removeCartItem(user, cartItemId);

        // 4. Return response
        return ApiResponse.<String>builder()
                .success(true)
                .message("Cart item removed successfully")
                .data(null)
                .build();
    }




}
