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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
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
        //find user's cart
      Cart cart =  cartRepository.findByUser(user)
                .orElseThrow(()->new CartNotFoundException("Cart not found"));

      //get all the items from cart
       List<CartItem> cartItems =  cartItemRepository.findByCart(cart);

       //check cart is empty
        if(cartItems.isEmpty()){
            throw new CartNotFoundException("Cart is empty");
        }

        //check stock
        for (CartItem cartItem : cartItems){
          Product product = cartItem.getProduct();
          if(product.getStock()<cartItem.getQuantity()){
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

       List<OrderItem> orderItems =  orderItemRepository.findByOrder(order);

       List<OrderItemResponse> orderItemResponses = new ArrayList<>();

       for(OrderItem orderItem : orderItems){
        Product product = orderItem.getProduct();
           OrderItemResponse response = new OrderItemResponse();
           response.setProductId(product.getId());
           response.setProductName(product.getName());
           response.setPrice(orderItem.getPrice());
           response.setQuantity(orderItem.getQuantity());

           orderItemResponses.add(response);
       }


        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setItems(orderItemResponses);



        // Clear cart after successful order
        cartItemRepository.deleteByCart(cart);

       // Return order resposne
        return orderResponse;
    }

    public List<OrderResponse> getAllOrders(User user){
        List<Order> orders = orderRepository.findByUser(user);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

            List<OrderItemResponse> responses = new ArrayList<>();
            for(OrderItem orderItem : orderItems){
                OrderItemResponse orderItemResponse = new OrderItemResponse();
                orderItemResponse.setProductId(orderItem.getProduct().getId());
                orderItemResponse.setProductName(orderItem.getProduct().getName());
                orderItemResponse.setPrice(orderItem.getPrice());
                orderItemResponse.setQuantity(orderItem.getQuantity());
                responses.add(orderItemResponse);
            }

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setTotalAmount(order.getTotalAmount());
            orderResponse.setStatus(order.getStatus());
            orderResponse.setItems(responses);

            orderResponses.add(orderResponse);
        }

        return orderResponses;

    }


    public OrderResponse getOrderById(Long orderId,User user){
      Order order = orderRepository.findByIdAndUser(orderId,user).
              orElseThrow(()-> new OrderNotFoundException("Order not found"));

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems){
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setProductId(orderItem.getProduct().getId());
            orderItemResponse.setProductName(orderItem.getProduct().getName());
            orderItemResponse.setPrice(orderItem.getPrice());
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponses.add(orderItemResponse);
        }

        OrderResponse orderResponse =  new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setItems(orderItemResponses);

        return orderResponse;

    }
    @Transactional
    public OrderResponse cancelOrder(Long orderId, User user){
       Order order = orderRepository.findByIdAndUser(orderId,user).
               orElseThrow(()->new OrderNotFoundException("Order not found"));

        if(order.getStatus()!=OrderStatus.PENDING &&
                order.getStatus()!=OrderStatus.CONFIRMED){
            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled in status: "+order.getStatus());
        }

           order.setStatus(OrderStatus.CANCELLED);

       List<OrderItem> orderItems =  orderItemRepository.findByOrder(order);

       for(OrderItem orderItem : orderItems){
          Product product = orderItem.getProduct();
          product.setStock(product.getStock()+orderItem.getQuantity());
           productRepository.save(product);
       }
       orderRepository.save(order);
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems){
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setProductId(orderItem.getProduct().getId());
            orderItemResponse.setProductName(orderItem.getProduct().getName());
            orderItemResponse.setPrice(orderItem.getPrice());
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponses.add(orderItemResponse);
        }

        OrderResponse orderResponse =  new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setItems(orderItemResponses);

        return orderResponse;
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
