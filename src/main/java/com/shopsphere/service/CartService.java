package com.shopsphere.service;

import com.shopsphere.dto.response.CartItemResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.exception.CartItemNotFoundException;
import com.shopsphere.exception.CartNotFoundException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.repositoy.CartItemRepository;
import com.shopsphere.repositoy.CartRepository;
import com.shopsphere.repositoy.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartItem addToCart(User user, Long productId, Integer quantity) {

        // STEP 1: Find or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(newCart);
                });

        // STEP 2: Find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId
                        ));

        // STEP 3: Find CartItem
        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseGet(() ->
                        CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(0)
                                .build()
                );

        // STEP 4: Increase quantity
        cartItem.setQuantity(
                cartItem.getQuantity() + quantity
        );

        // STEP 5: Save CartItem
        return cartItemRepository.save(cartItem);
    }

    public List<CartItemResponse> getMyCart(User user) {

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        List<CartItemResponse> responses = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            CartItemResponse response =
                    CartItemResponse.builder()
                            .id(cartItem.getId())
                            .productId(cartItem.getProduct().getId())
                            .productName(cartItem.getProduct().getName())
                            .price(cartItem.getProduct().getPrice())
                            .quantity(cartItem.getQuantity())
                            .build();

            responses.add(response);
        }

        return responses;
    }

    public CartItem updateCartItem(User user, Long cartItemId, Integer quantity) {

        // 1. Find user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        // 2. Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException(
                                "Cart item not found with id: " + cartItemId));

        // 3. Check cart item belongs to logged-in user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartItemNotFoundException(
                    "Cart item does not belong to this user's cart");
        }

        // 4. Update quantity
        cartItem.setQuantity(quantity);

        // 5. Save
        return cartItemRepository.save(cartItem);
    }

    public void removeCartItem(User user, Long cartItemId) {

        // 1. Find user's cart
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        // 2. Find cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException(
                                "Cart item not found with id: " + cartItemId));

        // 3. Check ownership
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartItemNotFoundException(
                    "Cart item does not belong to this user's cart");
        }

        // 4. Delete
        cartItemRepository.delete(cartItem);
    }
}