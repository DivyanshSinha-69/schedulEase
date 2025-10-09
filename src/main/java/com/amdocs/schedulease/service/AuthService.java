package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.UserAccount;

public interface AuthService {
    
    /**
     * Register a new user
     * @param email User email
     * @param password Plain text password
     * @param fullName User's full name
     * @param phone User's phone number
     * @return Created UserAccount
     * @throws UserAlreadyExistsException if email already exists
     */
    UserAccount registerUser(String email, String password, String fullName, String phone);
    
    /**
     * Authenticate user and create session
     * @param email User email
     * @param password Plain text password
     * @return Authenticated UserAccount
     * @throws InvalidCredentialsException if credentials are invalid
     * @throws UserNotFoundException if user doesn't exist
     */
    UserAccount login(String email, String password);
    
    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists
     */
    boolean emailExists(String email);
    
    /**
     * Hash password using BCrypt
     * @param plainPassword Plain text password
     * @return Hashed password
     */
    String hashPassword(String plainPassword);
    
    /**
     * Verify password against hash
     * @param plainPassword Plain text password
     * @param hashedPassword Hashed password from database
     * @return true if password matches
     */
    boolean verifyPassword(String plainPassword, String hashedPassword);
    
    void initiateForgotPassword(String email);
    boolean validateOtp(String email, String otp);
    void resetPasswordWithOtp(String email, String newPassword);
}
