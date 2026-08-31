package com.shopsphere.service;
import com.shopsphere.dto.response.AdminDashboardResponse;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.repositoy.OrderRepository;
import com.shopsphere.repositoy.ProductRepository;
import com.shopsphere.repositoy.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminDashboardService(
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {

        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public AdminDashboardResponse getDashboardData() {

        // Total customers
        long totalCustomers =
                userRepository.countByRole(
                        com.shopsphere.enums.Role.CUSTOMER
                );

        // Total products
        long totalProducts =
                productRepository.count();

        // Total orders
        long totalOrders =
                orderRepository.count();

        // Orders by status
        long pendingOrders =
                orderRepository.countByStatus(
                        OrderStatus.PENDING
                );

        long confirmedOrders =
                orderRepository.countByStatus(
                        OrderStatus.CONFIRMED
                );

        long shippedOrders =
                orderRepository.countByStatus(
                        OrderStatus.SHIPPED
                );

        long deliveredOrders =
                orderRepository.countByStatus(
                        OrderStatus.DELIVERED
                );

        long cancelledOrders =
                orderRepository.countByStatus(
                        OrderStatus.CANCELLED
                );

        /*
         * Total sales
         *
         * For now we calculate sales from DELIVERED orders.
         *
         * PENDING / CONFIRMED / SHIPPED orders are not counted
         * as completed sales yet.
         */
        double totalSales =
                orderRepository
                        .findByStatus(OrderStatus.DELIVERED)
                        .stream()
                        .mapToDouble(order ->
                                order.getTotalAmount() != null
                                        ? order.getTotalAmount()
                                        : 0.0
                        )
                        .sum();

        return AdminDashboardResponse.builder()

                .totalCustomers(totalCustomers)

                .totalProducts(totalProducts)

                .totalOrders(totalOrders)

                .pendingOrders(pendingOrders)

                .confirmedOrders(confirmedOrders)

                .shippedOrders(shippedOrders)

                .deliveredOrders(deliveredOrders)

                .cancelledOrders(cancelledOrders)

                .totalSales(totalSales)

                .build();
    }
}
