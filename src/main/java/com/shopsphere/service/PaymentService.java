package com.shopsphere.service;

import com.shopsphere.dto.response.PaymentResponse;
import com.shopsphere.entity.*;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.enums.PaymentMethod;
import com.shopsphere.enums.PaymentStatus;
import com.shopsphere.exception.*;
import com.shopsphere.repositoy.CartItemRepository;
import com.shopsphere.repositoy.CartRepository;
import com.shopsphere.repositoy.OrderItemRepository;
import com.shopsphere.repositoy.OrderRepository;
import com.shopsphere.repositoy.PaymentRepository;
import com.shopsphere.repositoy.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;


    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderItemRepository orderItemRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
    }


    @Transactional
    public PaymentResponse makePayment(
            Long orderId,
            User user,
            PaymentMethod paymentMethod) {

        log.info("Payment started for orderId: {}", orderId);

        // 1. Find order belonging to logged-in customer
        Order order = orderRepository
                .findByIdAndUser(orderId, user)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found"));

        // 2. Check whether payment already exists
        if (paymentRepository.findByOrder(order).isPresent()) {

            throw new PaymentAlreadyExistsException(
                    "Payment already exists for this order");
        }

        // 3. Payment is allowed only for PENDING order
        if (order.getStatus() != OrderStatus.PENDING) {

            throw new InvalidOrderStatusException(
                    "Payment can only be made for a PENDING order");
        }

        // 4. Get cart
        Cart cart = cartRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new CartNotFoundException(
                                "Cart not found"));

        // 5. Get cart items
        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        // 6. Find order items
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        // 7. Check stock again before payment
        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            if (product.getStock() < orderItem.getQuantity()) {

                throw new InsufficientStockException(
                        "Not enough stock for product: "
                                + product.getName());
            }
        }

        // 8. Create payment
        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.SUCCESS);

        payment = paymentRepository.save(payment);

        log.info(
                "Payment successful for orderId: {}, amount: {}",
                orderId,
                payment.getAmount());

        // 9. Reduce stock
        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            product.setStock(
                    product.getStock()
                            - orderItem.getQuantity());

            productRepository.save(product);
        }

        // 10. Remove ordered products from cart
        for (CartItem cartItem : cartItems) {

            for (OrderItem orderItem : orderItems) {

                if (cartItem.getProduct()
                        .getId()
                        .equals(orderItem.getProduct().getId())) {

                    cartItemRepository.delete(cartItem);

                    break;
                }
            }
        }

        // 11. Change order status
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);

        log.info(
                "Order {} status changed to CONFIRMED",
                orderId);

        // 12. Create response
        PaymentResponse response =
                new PaymentResponse();

        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(
                payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setOrderId(order.getId());

        return response;
    }
}