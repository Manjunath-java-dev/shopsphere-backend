package com.shopsphere.repositoy;

import com.shopsphere.entity.User;
import com.shopsphere.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
Optional<User>  findByEmail(String email);
boolean existsByEmail(String email);
boolean existsByPhone(String phone);
    long countByRole(Role role);

    List<User> findByRole(Role role);

    List<User> findByRoleAndNameContainingIgnoreCase(Role role, String name);
}
