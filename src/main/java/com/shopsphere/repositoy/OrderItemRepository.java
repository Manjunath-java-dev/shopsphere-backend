package com.shopsphere.repositoy;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {
    List<OrderItem> findByOrder(Order order);
}
