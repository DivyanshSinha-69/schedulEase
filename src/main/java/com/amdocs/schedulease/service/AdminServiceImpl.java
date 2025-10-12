package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.StaffProfile;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.exception.StaffCreationException;
import com.amdocs.schedulease.exception.UserAlreadyExistsException;
import com.amdocs.schedulease.exception.UserApprovalException;
import com.amdocs.schedulease.exception.UserNotFoundException;
import com.amdocs.schedulease.repository.RoleRepository;
import com.amdocs.schedulease.repository.StaffProfileRepository;
import com.amdocs.schedulease.repository.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StaffProfileRepository staffProfileRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;  // ADD THIS

    @Override
    public List<UserAccount> getPendingUsers() {
        return userAccountRepository.findAll().stream()
                .filter(user -> user.getStatus() == UserAccount.Status.PENDING)
                .toList();
    }

    @Override
    @Transactional
    public void approveUser(Long userId) {
        try {
            UserAccount user = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

            if (user.getStatus() != UserAccount.Status.PENDING) {
                throw new UserApprovalException("User is not in PENDING status");
            }

            user.setStatus(UserAccount.Status.ACTIVE);
            user.setApprovedAt(LocalDateTime.now());
            userAccountRepository.save(user);

        } catch (Exception e) {
            throw new UserApprovalException("Failed to approve user: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void rejectUser(Long userId, String reason) {
        try {
            UserAccount user = userAccountRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

            if (user.getStatus() != UserAccount.Status.PENDING) {
                throw new UserApprovalException("User is not in PENDING status");
            }

            userAccountRepository.delete(user);

        } catch (Exception e) {
            throw new UserApprovalException("Failed to reject user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UserAccount> getAllUsers() {
        return userAccountRepository.findAll();
    }

    @Override
    @Transactional
    public void suspendUser(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setStatus(UserAccount.Status.SUSPENDED);
        userAccountRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setStatus(UserAccount.Status.ACTIVE);
        if (user.getApprovedAt() == null) {
            user.setApprovedAt(LocalDateTime.now());
        }
        userAccountRepository.save(user);
    }

    @Override
    @Transactional
    public UserAccount createStaffAccount(String email, String password, String fullName, 
                                         String phone, String department, LocalDate dob) {
        try {
            if (authService.emailExists(email)) {
                throw new UserAlreadyExistsException("Email already registered: " + email);
            }

            // 1) Create and save user account
            UserAccount user = new UserAccount();
            user.setEmail(email);
            user.setPasswordHash(authService.hashPassword(password));
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setStatus(UserAccount.Status.ACTIVE);
            user.setCreatedAt(LocalDateTime.now());
            user.setApprovedAt(LocalDateTime.now());
            
            UserAccount savedUser = userAccountRepository.saveAndFlush(user);

            // 2) Assign STAFF role
            Role staffRole = roleRepository.findAll().stream()
                    .filter(r -> r.getName() == Role.RoleName.STAFF)
                    .findFirst()
                    .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.STAFF)));

            savedUser.getRoles().add(staffRole);
            savedUser = userAccountRepository.saveAndFlush(savedUser);

            // 3) Insert staff_profile using native SQL (bypasses @MapsId complexity)
            String sql = "INSERT INTO staff_profile (id, department, dob, created_at, version) " +
                         "VALUES (?, ?, ?, ?, 0)";
            
            jdbcTemplate.update(sql, 
                savedUser.getId(),          // id (same as user_account.id)
                department,                 // department
                dob,                       // dob
                LocalDateTime.now()        // created_at
            );

            return savedUser;

        } catch (Exception e) {
            throw new StaffCreationException("Failed to create staff account: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void assignRole(Long userId, Role.RoleName roleName) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Role role = roleRepository.findAll().stream()
                .filter(r -> r.getName() == roleName)
                .findFirst()
                .orElseGet(() -> {
                    Role newRole = new Role(roleName);
                    return roleRepository.save(newRole);
                });

        user.getRoles().add(role);
        userAccountRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Role.RoleName roleName) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.getRoles().removeIf(role -> role.getName() == roleName);
        userAccountRepository.save(user);
    }

    @Override
    public List<StaffProfile> getAllStaff() {
        return staffProfileRepository.findAll();
    }
    
    @Override
    public boolean isPasswordValid(String password) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";
        return password != null && password.matches(pattern);
    }

}
