package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.AnalyticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // ========== ANALYTICS DASHBOARD ==========

    @GetMapping("/analytics")
    public String showAnalytics(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Get analytics data
        model.addAttribute("user", user);
        model.addAttribute("totalUsers", analyticsService.getTotalUserCount());
        model.addAttribute("activeUsers", analyticsService.getActiveUserCount());
        model.addAttribute("pendingUsers", analyticsService.getPendingUserCount());
        model.addAttribute("totalBookings", analyticsService.getTotalBookingCount());
        model.addAttribute("bookingsByStatus", analyticsService.getBookingCountByStatus());
        model.addAttribute("totalRooms", analyticsService.getTotalRoomCount());
        model.addAttribute("availableRooms", analyticsService.getAvailableRoomCount());
        model.addAttribute("totalEquipment", analyticsService.getTotalEquipmentCount());
        model.addAttribute("mostBookedRooms", analyticsService.getMostBookedRooms());
        model.addAttribute("bookingTrend", analyticsService.getBookingTrend());

        return "admin/analytics";
    }

    // ========== REPORTS ==========

    @GetMapping("/reports")
    public String showReports(HttpSession session, Model model) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("reportGenerated", false); // <-- Add this line
        return "admin/reports";
    }


    // ========== API ENDPOINTS FOR AJAX CALLS ==========

    @GetMapping("/api/booking-trend")
    @ResponseBody
    public Map<LocalDate, Long> getBookingTrend() {
        return analyticsService.getBookingTrend();
    }

    @GetMapping("/api/most-booked-rooms")
    @ResponseBody
    public Map<String, Long> getMostBookedRooms() {
        return analyticsService.getMostBookedRooms();
    }

    @GetMapping("/api/booking-status")
    @ResponseBody
    public Map<String, Long> getBookingsByStatus() {
        return analyticsService.getBookingCountByStatus();
    }
}
