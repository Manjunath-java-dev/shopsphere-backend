package com.shopsphere.repositoy;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {
    List<OrderItem> findByOrder(Order order);
    boolean existsByOrderUserAndProduct(User user, Product product);
}
