package com.amdocs.schedulease.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.amdocs.schedulease.service.AuthService;
import com.amdocs.schedulease.repository.UserAccountRepository;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.exception.UserAlreadyExistsException;
import com.amdocs.schedulease.exception.InvalidCredentialsException;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserAccountRepository userRepo;

    @Override
    public void registerUser(String email, String password, String fullName) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }
        UserAccount user = new UserAccount();
        user.setEmail(email);
        // You should hash the password before saving in production!
        user.setPasswordHash(password);
        user.setFullName(fullName);
        user.setStatus(UserAccount.Status.PENDING);
        user.setCreatedAt(java.time.LocalDateTime.now());
        userRepo.save(user);
    }

    @Override
    public void login(String email, String password) {
        UserAccount user = userRepo.findByEmail(email)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.getPasswordHash().equals(password)) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        // Proceed with setting login session in controller layer
    }

    @Override
    public void logout() {
        // logout logic handled in controller/session
    }

    @Override
    public void resetPassword(String email) {
        // Implement password reset email and token generation here
    }
}
