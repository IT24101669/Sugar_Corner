package com.brownieshop.brownieshop.repository;

import com.brownieshop.brownieshop.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    // Find token by token string (CM23)
    Optional<PasswordResetToken> findByToken(String token);

    // Find token by customer id
    Optional<PasswordResetToken> findByCustomerId(Integer customerId);

}