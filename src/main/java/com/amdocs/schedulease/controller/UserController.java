package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Refresh user data from database
        user = userService.getUserById(user.getId());
        model.addAttribute("user", user);

        // TODO: Add booking statistics here
        // For now, just show basic info
        model.addAttribute("totalBookings", 0);
        model.addAttribute("upcomingBookings", 0);
        model.addAttribute("pastBookings", 0);

        return "user/dashboard";
    }

    // ========== PROFILE ==========

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Refresh user data
        user = userService.getUserById(user.getId());
        model.addAttribute("user", user);

        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        try {
            UserAccount updatedUser = userService.updateProfile(user.getId(), fullName, phone);
            
            // Update session
            session.setAttribute("user", updatedUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
            return "redirect:/user/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/profile";
        }
    }

    // ========== SETTINGS ==========

    @GetMapping("/settings")
    public String showSettings(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        return "user/settings";
    }

    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match");
            return "redirect:/user/settings";
        }

        // Validate password strength
        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters long");
            return "redirect:/user/settings";
        }

        try {
            userService.changePassword(user.getId(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
            return "redirect:/user/settings";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/settings";
        }
    }
}
