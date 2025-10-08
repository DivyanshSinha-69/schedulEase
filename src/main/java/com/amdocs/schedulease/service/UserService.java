package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.UserAccount;

public interface UserService {
    UserAccount getUserByEmail(String email);
    UserAccount updateUserProfile(UserAccount user);
    void changePassword(String email, String oldPassword, String newPassword);
}
