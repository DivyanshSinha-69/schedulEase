package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.StaffProfile;
import com.amdocs.schedulease.entity.UserAccount;
import java.time.LocalDate;

import java.util.List;

public interface AdminService {
    
    /**
     * Get all pending user registrations
     * @return List of users with PENDING status
     */
    List<UserAccount> getPendingUsers();
    
    /**
     * Approve user registration
     * @param userId User ID
     * @throws UserApprovalException if approval fails
     */
    void approveUser(Long userId);
    
    /**
     * Reject user registration
     * @param userId User ID
     * @param reason Rejection reason
     * @throws UserApprovalException if rejection fails
     */
    void rejectUser(Long userId, String reason);
    
    /**
     * Get all users (ACTIVE, PENDING, SUSPENDED)
     * @return List of all users
     */
    List<UserAccount> getAllUsers();
    
    /**
     * Suspend user account
     * @param userId User ID
     */
    void suspendUser(Long userId);
    
    /**
     * Activate user account
     * @param userId User ID
     */
    void activateUser(Long userId);
    
    /**
     * Create staff account
     * @param email Staff email
     * @param password Plain text password
     * @param fullName Staff full name
     * @param phone Staff phone
     * @param department Department name
     * @return Created UserAccount
     * @throws StaffCreationException if creation fails
     */
    UserAccount createStaffAccount(String email, String password, String fullName, 
            String phone, String department, LocalDate dob);
    
    /**
     * Assign role to user
     * @param userId User ID
     * @param roleName Role name
     */
    void assignRole(Long userId, Role.RoleName roleName);
    
    /**
     * Remove role from user
     * @param userId User ID
     * @param roleName Role name
     */
    void removeRole(Long userId, Role.RoleName roleName);
    
    /**
     * Get all staff profiles
     * @return List of staff profiles
     */
    List<StaffProfile> getAllStaff();
}
