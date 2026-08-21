package com.shopsphere.repositoy;


import com.shopsphere.entity.Product;
import com.shopsphere.entity.Review;
import com.shopsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Optional<Review> findByUserAndProduct(User user, Product product);
    List<Review> findByProduct(Product product);
    List<Review> findByUser(User user);

    Optional<Review> findByIdAndUser(Long reviewId, User user);
}
