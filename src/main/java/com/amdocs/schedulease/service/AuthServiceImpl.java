package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.exception.InvalidCredentialsException;
import com.amdocs.schedulease.exception.UserAlreadyExistsException;
import com.amdocs.schedulease.exception.UserNotFoundException;
import com.amdocs.schedulease.repository.RoleRepository;
import com.amdocs.schedulease.repository.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amdocs.schedulease.entity.PasswordResetOtp;
import com.amdocs.schedulease.repository.PasswordResetOtpRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Random;
import java.util.Optional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordResetOtpRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public UserAccount registerUser(String email, String password, String fullName, String phone) {
        // Check if email already exists
        if (emailExists(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        // Create new user
        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setStatus(UserAccount.Status.PENDING); // Pending approval
        user.setCreatedAt(LocalDateTime.now());

        // Save user
        UserAccount savedUser = userAccountRepository.save(user);

        // Assign USER role by default
        Role userRole = roleRepository.findAll().stream()
                .filter(r -> r.getName() == Role.RoleName.USER)
                .findFirst()
                .orElseGet(() -> {
                    Role newRole = new Role(Role.RoleName.USER);
                    return roleRepository.save(newRole);
                });

        savedUser.getRoles().add(userRole);
        return userAccountRepository.save(savedUser);
    }

    @Override
    public UserAccount login(String email, String password) {
        // Find user by email
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Verify password
        if (!verifyPassword(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Check if user is active
        if (user.getStatus() == UserAccount.Status.PENDING) {
            throw new InvalidCredentialsException("Account pending approval. Please contact administrator.");
        }

        if (user.getStatus() == UserAccount.Status.SUSPENDED) {
            throw new InvalidCredentialsException("Account suspended. Please contact administrator.");
        }

        return user;
    }

    @Override
    public boolean emailExists(String email) {
        return userAccountRepository.findByEmail(email).isPresent();
    }

    @Override
    public String hashPassword(String plainPassword) {
        // Using BCrypt for password hashing
        return org.springframework.security.crypto.bcrypt.BCrypt.hashpw(
                plainPassword, 
                org.springframework.security.crypto.bcrypt.BCrypt.gensalt(12)
        );
    }

    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public void initiateForgotPassword(String email) {
        // Verify user exists
        UserAccount user = userAccountRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        // Delete any existing OTPs for this email
        otpRepository.deleteByEmail(email);
        
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Create OTP record with 10 minutes expiry
        PasswordResetOtp resetOtp = new PasswordResetOtp(
            email,
            otp,
            LocalDateTime.now().plusMinutes(10)
        );
        
        otpRepository.save(resetOtp);
        
        // Send OTP via email
        sendOtpEmail(email, otp, user.getFullName());
    }

    private void sendOtpEmail(String email, String otp, String fullName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("harshsinha190@gmail.com"); // ADD THIS LINE
        message.setTo(email);
        message.setSubject("Forgot Password OTP - schedulEase");
        message.setText("Hello " + fullName + ",\n\n" +
                        "Your password reset OTP is: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes only.\n" +
                        "Do not share this OTP with anyone.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "schedulEase Team");
        
        mailSender.send(message);
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        // Find OTP record
        Optional<PasswordResetOtp> otpRecordOpt = otpRepository.findByEmailAndOtpAndIsUsedFalse(email, otp);
        
        if (otpRecordOpt.isEmpty()) {
            return false;
        }
        
        PasswordResetOtp otpRecord = otpRecordOpt.get();
        
        // Check if expired
        if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }

    @Override
    @Transactional
    public void resetPasswordWithOtp(String email, String newPassword) {
        // Verify user exists
        UserAccount user = userAccountRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        
        // Update password using existing hashing method
        user.setPasswordHash(hashPassword(newPassword));
        userAccountRepository.save(user);
        
        // Delete all OTPs for this email
        otpRepository.deleteByEmail(email);
    }

}
