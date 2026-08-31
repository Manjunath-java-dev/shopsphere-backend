package com.shopsphere.repositoy;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.User;
import com.shopsphere.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);
    Optional<Order> findByIdAndUser(Long id,User user);
    long countByStatus(OrderStatus status);
    List<Order> findByStatus(OrderStatus status);
}
