
package com.shopsphere.service;

import com.shopsphere.dto.request.ReviewRequest;
import com.shopsphere.dto.response.ReviewResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.Review;
import com.shopsphere.entity.User;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.exception.ProductNotPurchasedException;
import com.shopsphere.exception.ReviewAlreadyExistsException;
import com.shopsphere.exception.ReviewNotFoundException;
import com.shopsphere.repositoy.OrderItemRepository;
import com.shopsphere.repositoy.ProductRepository;
import com.shopsphere.repositoy.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;


    public ReviewService(
            ReviewRepository reviewRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {

        this.reviewRepository =
                reviewRepository;

        this.orderItemRepository =
                orderItemRepository;

        this.productRepository =
                productRepository;
    }


    // =====================================================
    // ADD REVIEW
    // =====================================================

    public ReviewResponse addReview(
            User user,
            Long productId,
            ReviewRequest reviewRequest) {


        // =================================================
        // FIND PRODUCT
        // =================================================

        Product product =
                productRepository.findById(productId)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );


        // =================================================
        // CHECK PURCHASE
        // =================================================
        //
        // Customer can review ONLY if:
        //
        // 1. The order belongs to the customer
        // 2. The order contains this product
        // 3. The order status is DELIVERED
        //
        // =================================================

        boolean purchased =
                orderItemRepository
                        .existsByOrderUserAndOrderStatusAndProduct(
                                user,
                                OrderStatus.DELIVERED,
                                product
                        );


        if (!purchased) {

            throw new ProductNotPurchasedException(
                    "You can review only products you have purchased and received."
            );

        }


        // =================================================
        // CHECK DUPLICATE REVIEW
        // =================================================

        if (reviewRepository
                .findByUserAndProduct(user, product)
                .isPresent()) {

            throw new ReviewAlreadyExistsException(
                    "You have already reviewed this product"
            );

        }


        // =================================================
        // CREATE REVIEW
        // =================================================

        Review review =
                Review.builder()

                        .user(user)

                        .product(product)

                        .rating(
                                reviewRequest.getRating()
                        )

                        .comment(
                                reviewRequest.getComment()
                        )

                        .build();


        review =
                reviewRepository.save(review);


        // =================================================
        // UPDATE PRODUCT RATING
        // =================================================

        updateProductRating(product);


        // =================================================
        // RETURN RESPONSE
        // =================================================

        return convertToResponse(review);
    }


    // =====================================================
    // GET PRODUCT REVIEWS
    // =====================================================

    public List<ReviewResponse> getProductReviews(
            Long productId) {


        Product product =
                productRepository.findById(productId)

                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with id: "
                                                + productId
                                )
                        );


        List<Review> reviews =
                reviewRepository.findByProduct(product);


        List<ReviewResponse> responses =
                new ArrayList<>();


        for (Review review : reviews) {

            responses.add(
                    convertToResponse(review)
            );

        }


        return responses;
    }


    // =====================================================
    // UPDATE PRODUCT RATING
    // =====================================================

    private void updateProductRating(
            Product product) {


        List<Review> reviews =
                reviewRepository.findByProduct(product);


        if (reviews.isEmpty()) {

            product.setRating(0.0);

        } else {

            double total = 0;


            for (Review review : reviews) {

                total +=
                        review.getRating();

            }


            double average =
                    total / reviews.size();


            product.setRating(average);
        }


        productRepository.save(product);
    }


    // =====================================================
    // CONVERT TO RESPONSE
    // =====================================================

    private ReviewResponse convertToResponse(
            Review review) {


        ReviewResponse response =
                new ReviewResponse();


        response.setId(
                review.getId()
        );


        response.setProductId(
                review.getProduct().getId()
        );


        response.setProductName(
                review.getProduct().getName()
        );


        response.setUserId(
                review.getUser().getId()
        );


        response.setUserName(
                review.getUser().getName()
        );


        response.setRating(
                review.getRating()
        );


        response.setComment(
                review.getComment()
        );


        response.setCreatedAt(
                review.getCreatedAt()
        );


        response.setUpdatedAt(
                review.getUpdatedAt()
        );


        return response;
    }


    // =====================================================
    // UPDATE REVIEW
    // =====================================================

    @Transactional
    public ReviewResponse updateReview(
            User user,
            Long reviewId,
            ReviewRequest request) {


        Review review =
                reviewRepository

                        .findByIdAndUser(
                                reviewId,
                                user
                        )

                        .orElseThrow(() ->
                                new ReviewNotFoundException(
                                        "Review not found"
                                )
                        );


        review.setRating(
                request.getRating()
        );


        review.setComment(
                request.getComment()
        );


        Review updatedReview =
                reviewRepository.save(review);


        updateProductRating(
                review.getProduct()
        );


        return convertToResponse(
                updatedReview
        );
    }


    // =====================================================
    // DELETE REVIEW
    // =====================================================

    @Transactional
    public void deleteReview(
            User user,
            Long reviewId) {


        Review review =
                reviewRepository

                        .findByIdAndUser(
                                reviewId,
                                user
                        )

                        .orElseThrow(() ->
                                new ReviewNotFoundException(
                                        "Review not found"
                                )
                        );


        Product product =
                review.getProduct();


        reviewRepository.delete(review);


        updateProductRating(product);
    }


    // =====================================================
    // GET MY REVIEWS
    // =====================================================

    public List<ReviewResponse> getMyReviews(
            User user) {


        List<Review> reviews =
                reviewRepository.findByUser(user);


        List<ReviewResponse> responses =
                new ArrayList<>();


        for (Review review : reviews) {

            responses.add(
                    convertToResponse(review)
            );

        }


        return responses;
    }
}
