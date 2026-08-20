package com.shopsphere.entity;

import com.shopsphere.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Shipping address snapshot
    @Column(length = 100)
    private String shippingAddressLine1;

    @Column(length = 100)
    private String shippingAddressLine2;

    @Column(length = 50)
    private String shippingCity;

    @Column(length = 50)
    private String shippingState;

    @Column(length = 10)
    private String shippingPincode;

    @Column(length = 50)
    private String shippingCountry;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
      createdAt =  LocalDateTime.now();
    }

}
