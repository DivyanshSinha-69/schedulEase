package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.UserAccount;

import java.util.Set;

import org.jspecify.annotations.Nullable;

public interface UserService {
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return UserAccount
     * @throws UserNotFoundException if user not found
     */
    UserAccount getUserById(Long userId);
    
    /**
     * Get user by email
     * @param email User email
     * @return UserAccount
     * @throws UserNotFoundException if user not found
     */
    UserAccount getUserByEmail(String email);
    
    /**
     * Update user profile
     * @param userId User ID
     * @param fullName New full name
     * @param phone New phone number
     * @return Updated UserAccount
     */
    UserAccount updateProfile(Long userId, String fullName, String phone);
    
    /**
     * Change user password
     * @param userId User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @throws InvalidCredentialsException if old password is incorrect
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * Get user roles
     * @param userId User ID
     * @return Set of roles
     */
    Set<Role> getUserRoles(Long userId);
    
    /**
     * Check if user has specific role
     * @param userId User ID
     * @param roleName Role name
     * @return true if user has the role
     */
    boolean hasRole(Long userId, Role.RoleName roleName);


	String getUserFullName(Long userId);
}
