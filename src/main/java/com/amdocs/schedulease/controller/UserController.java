package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.service.BookingService;  // ADD THIS

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingService bookingService;  // ADD THIS FIELD

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

        // Get user's bookings and calculate stats
        List<Booking> allBookings = bookingService.getUserBookings(user.getId());
        LocalDateTime now = LocalDateTime.now();

        long totalBookings = allBookings.size();
        
        // Upcoming bookings = future bookings that are not cancelled
        long upcomingBookings = allBookings.stream()
            .filter(b -> b.getStartDatetime().isAfter(now) 
                      && b.getStatus() != Booking.BookingStatus.CANCELLED)
            .count();
        
        // Past bookings = bookings that already ended
        long pastBookings = allBookings.stream()
            .filter(b -> b.getEndDatetime().isBefore(now))
            .count();

        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("upcomingBookings", upcomingBookings);
        model.addAttribute("pastBookings", pastBookings);

        return "user/dashboard";
    }



    // ========== PROFILE ==========

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Set dashboard link based on user's role
        String dashboardLink;
        if (user.hasRole("ADMIN")) {
            dashboardLink = "/admin/dashboard";
        } else if (user.hasRole("STAFF")) {
            dashboardLink = "/staff/dashboard";
        } else {
            dashboardLink = "/user/dashboard";
        }
        model.addAttribute("dashboardLink", dashboardLink);

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

        // Set dashboard link based on user's role
        String dashboardLink;
        if (user.hasRole("ADMIN")) {
            dashboardLink = "/admin/dashboard";
        } else if (user.hasRole("STAFF")) {
            dashboardLink = "/staff/dashboard";
        } else {
            dashboardLink = "/user/dashboard";
        }
        model.addAttribute("dashboardLink", dashboardLink);

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
