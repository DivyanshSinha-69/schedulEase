package com.amdocs.schedulease.service;

public interface AuthService {
    void registerUser(String email, String password, String fullName);
    void login(String email, String password);
    void logout();
    void resetPassword(String email);
}
