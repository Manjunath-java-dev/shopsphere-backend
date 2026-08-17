package com.shopsphere.service;

import com.shopsphere.dto.response.PaymentResponse;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.Payment;
import com.shopsphere.entity.User;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.enums.PaymentMethod;
import com.shopsphere.enums.PaymentStatus;
import com.shopsphere.exception.InvalidOrderStatusException;
import com.shopsphere.exception.OrderNotFoundException;
import com.shopsphere.exception.PaymentAlreadyExistsException;
import com.shopsphere.repositoy.OrderRepository;
import com.shopsphere.repositoy.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository){
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }


    @Transactional
    public PaymentResponse makePayment(Long orderId, User user, PaymentMethod paymentMethod){
        // STEP 1: Find the order belonging to this customer
       Order order = orderRepository.findByIdAndUser(orderId,user)
                .orElseThrow(()-> new OrderNotFoundException("Order not found"));

        // STEP 2: Check whether payment already exists
        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new PaymentAlreadyExistsException(
                    "Payment already exists for this order");
        }

// STEP 3: Check order status
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Payment can only be made for a PENDING order");
        }
        // STEP 4: Create payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.SUCCESS);

        // STEP 5: Save payment
        payment = paymentRepository.save(payment);

        // STEP 6: Change order status
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);


        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setOrderId(order.getId());

        return response;
    }
}
