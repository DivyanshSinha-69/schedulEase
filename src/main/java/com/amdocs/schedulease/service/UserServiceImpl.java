package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.exception.InvalidCredentialsException;
import com.amdocs.schedulease.exception.UserNotFoundException;
import com.amdocs.schedulease.repository.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private AuthService authService;

    @Override
    public UserAccount getUserById(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public UserAccount getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public UserAccount updateProfile(Long userId, String fullName, String phone) {
        UserAccount user = getUserById(userId);
        user.setFullName(fullName);
        user.setPhone(phone);
        return userAccountRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        UserAccount user = getUserById(userId);

        // Verify old password
        if (!authService.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        // Set new password
        user.setPasswordHash(authService.hashPassword(newPassword));
        userAccountRepository.save(user);
    }

    @Override
    public Set<Role> getUserRoles(Long userId) {
        UserAccount user = getUserById(userId);
        return user.getRoles();
    }

    @Override
    public boolean hasRole(Long userId, Role.RoleName roleName) {
        Set<Role> roles = getUserRoles(userId);
        return roles.stream().anyMatch(role -> role.getName() == roleName);
    }
}
