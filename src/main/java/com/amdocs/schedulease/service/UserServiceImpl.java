package com.amdocs.schedulease.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.amdocs.schedulease.service.UserService;
import com.amdocs.schedulease.repository.UserAccountRepository;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.exception.UserNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserAccountRepository userRepo;

    @Override
    public UserAccount getUserByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public UserAccount updateUserProfile(UserAccount user) {
        if (!userRepo.existsById(user.getId())) {
            throw new UserNotFoundException("User not found");
        }
        return userRepo.save(user);
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        UserAccount user = getUserByEmail(email);
        if (!user.getPasswordHash().equals(oldPassword)) {
            throw new IllegalArgumentException("Old password does not match");
        }
        user.setPasswordHash(newPassword);
        userRepo.save(user);
    }
}
