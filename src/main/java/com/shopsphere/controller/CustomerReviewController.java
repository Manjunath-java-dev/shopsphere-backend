package com.shopsphere.controller;

import com.shopsphere.dto.request.ReviewRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.ReviewResponse;
import com.shopsphere.entity.User;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repositoy.UserRepository;
import com.shopsphere.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/reviews")
@SecurityRequirement(name = "bearerAuth")
public class CustomerReviewController {
    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public CustomerReviewController(
            ReviewService reviewService,
            UserRepository userRepository) {

        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @PostMapping("/product/{productId}")
    public ApiResponse<ReviewResponse> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        // 1. Get logged-in user's email
        String email = authentication.getName();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // 3. Add review
        ReviewResponse response =
                reviewService.addReview(user, productId, request);

        // 4. Return response
        return ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review added successfully")
                .data(response)
                .build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewResponse>> getProductReviews(
            @PathVariable Long productId) {

        List<ReviewResponse> responses =
                reviewService.getProductReviews(productId);

        return ApiResponse.<List<ReviewResponse>>builder()
                .success(true)
                .message("Reviews fetched successfully")
                .data(responses)
                .build();
    }


    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        ReviewResponse response =
                reviewService.updateReview(
                        user,
                        reviewId,
                        request);

        return ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<String> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        reviewService.deleteReview(user, reviewId);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Review deleted successfully")
                .data(null)
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<ReviewResponse>> getMyReviews(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        List<ReviewResponse> responses =
                reviewService.getMyReviews(user);

        return ApiResponse.<List<ReviewResponse>>builder()
                .success(true)
                .message("My reviews fetched successfully")
                .data(responses)
                .build();
    }
}
