package com.shopsphere.service;

import com.shopsphere.dto.response.OrderItemResponse;
import com.shopsphere.dto.response.OrderResponse;
import com.shopsphere.entity.*;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.exception.CartNotFoundException;
import com.shopsphere.exception.InsufficientStockException;
import com.shopsphere.exception.InvalidOrderStatusException;
import com.shopsphere.exception.OrderNotFoundException;
import com.shopsphere.repositoy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        OrderItemRepository orderItemRepository,
                        OrderRepository orderRepository,
                        ProductRepository productRepository){
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse createOrder(User user){
        log.info("Order creation started for user: {}", user.getEmail());
        //find user's cart
      Cart cart =  cartRepository.findByUser(user)
                .orElseThrow(()->new CartNotFoundException("Cart not found"));

      //get all the items from cart
       List<CartItem> cartItems =  cartItemRepository.findByCart(cart);

       //check cart is empty
        if(cartItems.isEmpty()){
            log.warn("Order creation failed: cart is empty for user: {}", user.getEmail());
            throw new CartNotFoundException("Cart is empty");
        }

        //check stock
        for (CartItem cartItem : cartItems){
          Product product = cartItem.getProduct();
          if(product.getStock()<cartItem.getQuantity()){
              log.warn("Insufficient stock for product: {}, requested: {}, available: {}",
                      product.getName(),
                      cartItem.getQuantity(),
                      product.getStock());
              throw new InsufficientStockException("Not enough stock for product: "+product.getName());
          }
        }
        //create order
        Order order = Order.builder()
                .user(user)
                .totalAmount(0.0)
                .status(OrderStatus.PENDING)
                .build();
        order = orderRepository.save(order);

        // Calculate total + create OrderItems
        Double totalAmount = 0.0;
        for (CartItem cartItem : cartItems){
         Product product = cartItem.getProduct();
        Double itemTotal = product.getPrice()*cartItem.getQuantity();
        totalAmount = totalAmount + itemTotal;
         OrderItem orderItem = OrderItem.builder()
                 .order(order)
                 .product(product)
                 .quantity(cartItem.getQuantity())
                 .price(product.getPrice())
                 .build();
         orderItemRepository.save(orderItem);

            // Reduce stock
            product.setStock(
                    product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        log.info("Order created with id: {} for user: {}",
                order.getId(),
                user.getEmail());

// Clear cart
        cartItemRepository.deleteByCart(cart);

        log.info("Order {} created successfully. Total amount: {}",
                order.getId(),
                order.getTotalAmount());

// Convert Order → OrderResponse
        return convertToOrderResponse(order);
    }

    public List<OrderResponse> getAllOrders(User user) {
        log.info("Fetching orders for user: {}", user.getEmail());

        List<Order> orders = orderRepository.findByUser(user);
        log.info("Found {} orders for user: {}",
                orders.size(),
                user.getEmail());

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {

            OrderResponse orderResponse =
                    convertToOrderResponse(order);

            orderResponses.add(orderResponse);
        }

        return orderResponses;
    }


    public OrderResponse getOrderById(Long orderId, User user) {
        log.info("Fetching order {} for user: {}", orderId, user.getEmail());

        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> {
                    log.warn("Order {} not found for user: {}", orderId, user.getEmail());
                    return new OrderNotFoundException("Order not found");
                });

        return convertToOrderResponse(order);
    }
    @Transactional
    public OrderResponse cancelOrder(Long orderId, User user) {
        log.info("Order cancellation requested for orderId: {}", orderId);

        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING &&
                order.getStatus() != OrderStatus.CONFIRMED) {

            log.warn("Order {} cannot be cancelled. Current status: {}",
                    orderId,
                    order.getStatus());

            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled in status: "
                            + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        log.info("Order {} cancelled successfully", orderId);

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            product.setStock(
                    product.getStock() + orderItem.getQuantity());

            productRepository.save(product);
        }

        orderRepository.save(order);

        return convertToOrderResponse(order);
    }


    public List<OrderResponse> getAllOrdersForAdmin(){
      List<Order> orders =  orderRepository.findAll();
      List<OrderResponse> orderResponses = new ArrayList<>();
      for (Order order : orders){
          OrderResponse orderResponse = convertToOrderResponse(order);
          orderResponses.add(orderResponse);
      }

      return orderResponses;
    }

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);
        List<OrderItemResponse> orderItemResponses =
                new ArrayList<>();

        for (OrderItem orderItem : orderItems){
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setProductId(orderItem.getProduct().getId());
            orderItemResponse.setProductName(orderItem.getProduct().getName());
            orderItemResponse.setPrice(orderItem.getPrice());
            orderItemResponse.setQuantity(orderItem.getQuantity());

            orderItemResponses.add(orderItemResponse);
        }
        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setItems(orderItemResponses);

        return orderResponse;
    }
    public OrderResponse getOrderByIdForAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        return convertToOrderResponse(order);

    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus){
        log.info("Order status update requested for orderId: {}, newStatus: {}",
                orderId,
                newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new  OrderNotFoundException("Order not found"));

       OrderStatus currentStatus = order.getStatus();

       if(currentStatus==OrderStatus.DELIVERED || currentStatus==OrderStatus.CANCELLED){
           throw new InvalidOrderStatusException( "Order cannot be updated from status: "+currentStatus);
       }
        if (currentStatus == OrderStatus.PENDING &&
                newStatus != OrderStatus.CONFIRMED) {

            throw new InvalidOrderStatusException(
                    "PENDING order can only be CONFIRMED");
        }

        if (currentStatus == OrderStatus.CONFIRMED &&
                newStatus != OrderStatus.SHIPPED) {

            throw new InvalidOrderStatusException(
                    "CONFIRMED order can only be SHIPPED");
        }

        if (currentStatus == OrderStatus.SHIPPED &&
                newStatus != OrderStatus.DELIVERED) {

            throw new InvalidOrderStatusException(
                    "SHIPPED order can only be DELIVERED");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
        return convertToOrderResponse(order);
    }
}
