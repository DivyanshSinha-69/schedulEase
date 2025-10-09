package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    
    @Query("SELECT u.fullName FROM UserAccount u WHERE u.id = :userId")
    String findUserFullNameById(@Param("userId") Long userId);
}
