package com.shopsphere.service;

import com.shopsphere.dto.response.WishlistResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.Wishlist;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.repositoy.ProductRepository;
import com.shopsphere.repositoy.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistRepository wishlistRepository,
                           ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    public WishlistResponse addToWishlist(User user, Long productId) {
        // 1. Check whether product already exists in wishlist
        Optional<Wishlist> existingWishlist = wishlistRepository.findByUserAndProductId(user, productId);
        if (existingWishlist.isPresent()) {
            throw new RuntimeException("Product already exists in wishlist");
        }


        // 2. Find product
        Product product = productRepository.findById(productId).
                orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        // 3. Create Wishlist entity
        Wishlist wishlist = new Wishlist();

        wishlist.setUser(user);
        wishlist.setProduct(product);

        // 4. Save Wishlist
        wishlist = wishlistRepository.save(wishlist);

        // 5. Convert Wishlist → WishlistResponse
        WishlistResponse wishlistResponse = new WishlistResponse();

        wishlistResponse.setId(wishlist.getId());
        wishlistResponse.setProductId(product.getId());
        wishlistResponse.setProductName(product.getName());
        wishlistResponse.setPrice(product.getPrice());

        // 6. Return response
        return wishlistResponse;
    }

    public List<WishlistResponse> getMyWishlist(User user) {

        // 1. Find all wishlist items of this user
        List<Wishlist> wishlists =
                wishlistRepository.findByUser(user);

        // 2. Create response list
        List<WishlistResponse> responses = new ArrayList<>();

        // 3. Convert Wishlist → WishlistResponse
        for (Wishlist wishlist : wishlists) {

            WishlistResponse response = new WishlistResponse();

            response.setId(wishlist.getId());
            response.setProductId(wishlist.getProduct().getId());
            response.setProductName(wishlist.getProduct().getName());
            response.setPrice(wishlist.getProduct().getPrice());

            responses.add(response);
        }

        return responses;
    }
    public void removeFromWishlist(User user, Long productId) {

        // 1. Find wishlist item belonging to this user
        Wishlist wishlist = wishlistRepository
                .findByUserAndProductId(user, productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found in wishlist"));

        // 2. Delete wishlist item
        wishlistRepository.delete(wishlist);
    }
}