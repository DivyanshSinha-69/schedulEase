package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.StaffProfile;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.AdminService;
import com.amdocs.schedulease.service.AnalyticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AnalyticsService analyticsService;

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Add analytics data for dashboard
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", analyticsService.getTotalUserCount());
        model.addAttribute("activeUsers", analyticsService.getActiveUserCount());
        model.addAttribute("pendingUsers", analyticsService.getPendingUserCount());
        model.addAttribute("totalBookings", analyticsService.getTotalBookingCount());
        model.addAttribute("totalRooms", analyticsService.getTotalRoomCount());
        model.addAttribute("availableRooms", analyticsService.getAvailableRoomCount());

        return "admin/dashboard";
    }

    // ========== USER MANAGEMENT ==========

    @GetMapping("/user-management")
    public String showUserManagement(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<UserAccount> allUsers = adminService.getAllUsers();
        model.addAttribute("user", user);
        model.addAttribute("users", allUsers);

        return "admin/user-management";
    }

    // ========== USER APPROVAL ==========

    @GetMapping("/user-approval")
    public String showUserApproval(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<UserAccount> pendingUsers = adminService.getPendingUsers();
        model.addAttribute("user", user);
        model.addAttribute("pendingUsers", pendingUsers);

        return "admin/user-approval";
    }

    @PostMapping("/user-approval/approve/{userId}")
    public String approveUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            adminService.approveUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User approved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user-approval";
    }

    @PostMapping("/user-approval/reject/{userId}")
    public String rejectUser(@PathVariable Long userId, 
                            @RequestParam(required = false) String reason,
                            RedirectAttributes redirectAttributes) {
        try {
            adminService.rejectUser(userId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "User registration rejected");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user-approval";
    }

    // ========== USER ACTIONS (SUSPEND/ACTIVATE) ==========

    @PostMapping("/users/suspend/{userId}")
    public String suspendUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            adminService.suspendUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User suspended successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user-management";
    }

    @PostMapping("/users/activate/{userId}")
    public String activateUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            adminService.activateUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User activated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user-management";
    }

    // ========== STAFF MANAGEMENT ==========

    @GetMapping("/staff-management")
    public String showStaffManagement(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        List<StaffProfile> staffList = adminService.getAllStaff();
        model.addAttribute("user", user);
        model.addAttribute("staffList", staffList);

        return "admin/staff-management";
    }

    @PostMapping("/staff/create")
    public String createStaffAccount(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("department") String department,
            @RequestParam("dob") String dob,  // NEW
            RedirectAttributes redirectAttributes) {

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match");
            return "redirect:/admin/staff-management";
        }

        // Validate password strength
        if (password.length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters");
            return "redirect:/admin/staff-management";
        }

        try {
            // Parse and validate DOB
            java.time.LocalDate parsedDob = java.time.LocalDate.parse(dob);
            
            // Validate DOB is not in future
            if (parsedDob.isAfter(java.time.LocalDate.now())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Date of Birth cannot be in the future");
                return "redirect:/admin/staff-management";
            }

            // Call service with DOB
            adminService.createStaffAccount(email, password, fullName, phone, department, parsedDob);
            redirectAttributes.addFlashAttribute("successMessage", "Staff account created successfully");
        } catch (java.time.format.DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/staff-management";
    }


    // ========== ROLE MANAGEMENT ==========

    @PostMapping("/users/{userId}/assign-role")
    public String assignRole(@PathVariable Long userId, 
                            @RequestParam("roleName") String roleName,
                            RedirectAttributes redirectAttributes) {
        try {
            Role.RoleName role = Role.RoleName.valueOf(roleName);
            adminService.assignRole(userId, role);
            redirectAttributes.addFlashAttribute("successMessage", "Role assigned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user-management";
    }

    @PostMapping("/users/{userId}/remove-role")
    public String removeRole(@PathVariable Long userId, 
                            @RequestParam("roleName") String roleName,
                            RedirectAttributes redirectAttributes) {
        try {
            Role.RoleName role = Role.RoleName.valueOf(roleName);
            adminService.removeRole(userId, role);
            redirectAttributes.addFlashAttribute("successMessage", "Role removed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/user-management";
    }

    // ========== MONITOR BOOKINGS ==========

    @GetMapping("/monitor-bookings")
    public String monitorBookings(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        // TODO: Add booking data when Module 2 is ready
        model.addAttribute("bookings", List.of());

        return "admin/monitor-bookings";
    }

    // ========== MONITOR RESOURCES ==========

    @GetMapping("/monitor-resources")
    public String monitorResources(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        // TODO: Add resource data when Module 3 is ready

        return "admin/monitor-resources";
    }

    // ========== MONITOR ROOMS ==========

    @GetMapping("/monitor-rooms")
    public String monitorRooms(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        // TODO: Add room data when Module 3 is ready

        return "admin/monitor-rooms";
    }

    // ========== SETTINGS ==========

    @GetMapping("/settings")
    public String showSettings(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        return "admin/settings";
    }
}
