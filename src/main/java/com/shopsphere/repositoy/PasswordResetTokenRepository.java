package com.shopsphere.repositoy;

import com.shopsphere.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM PasswordResetToken p WHERE p.user.id = :userId")
    int deleteByUserId(@Param("userId") Long userId);
}