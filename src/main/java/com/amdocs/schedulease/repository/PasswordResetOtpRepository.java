package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    
    Optional<PasswordResetOtp> findByEmailAndOtpAndIsUsedFalse(String email, String otp);
    
    void deleteByExpiryTimeBefore(LocalDateTime now);
    
    void deleteByEmail(String email);
}
