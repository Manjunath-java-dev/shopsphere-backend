
        package com.shopsphere.repositoy;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    /*
     * A customer can review a product only when
     * the product belongs to a DELIVERED order
     * placed by that customer.
     */
    boolean existsByOrderUserAndOrderStatusAndProduct(
            User user,
            OrderStatus status,
            Product product
    );
}
