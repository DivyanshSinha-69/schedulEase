package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.BookingService;
import com.amdocs.schedulease.service.RoomService;
import com.amdocs.schedulease.service.EquipmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private BookingService bookingService;
    
    @Value("${staff.booking.expiry.enabled:true}")
    private boolean expiryNotificationsEnabled;
    
    @Value("${staff.booking.expiry.threshold-hours:48}")
    private int expiryThresholdHours;
    @Value("${staff.reminders.enabled:false}")
    private boolean remindersEnabled;

    @Value("${staff.reminders.hours-ahead:24}")
    private int remindersHoursAhead;

    @GetMapping("/dashboard")
    public String staffDashboard(HttpSession session, Model model) {
        // Check if user is logged in
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Check if user is STAFF
        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equals("STAFF")) {
            return "redirect:/auth/login";
        }

        // Get room statistics
        List<Room> allRooms = roomService.getAllRooms();
        long totalRooms = allRooms.size();
        long availableRooms = allRooms.stream()
            .filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE)
            .count();
        long unavailableRooms = allRooms.stream()
            .filter(r -> r.getStatus() == Room.RoomStatus.UNAVAILABLE)
            .count();
        long maintenanceRooms = allRooms.stream()
            .filter(r -> r.getStatus() == Room.RoomStatus.MAINTENANCE)
            .count();
        long totalEquipmentTypes = equipmentService.getAllEquipmentTypes().size();

        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("unavailableRooms", unavailableRooms);
        model.addAttribute("maintenanceRooms", maintenanceRooms);
        model.addAttribute("totalEquipmentTypes", totalEquipmentTypes);
        
        // NEW: Add expiry notifications if enabled
        if (expiryNotificationsEnabled) {
            List<Booking> expiredBookings = bookingService.getExpiredPendingBookings(expiryThresholdHours);
            model.addAttribute("expiredBookings", expiredBookings);
            model.addAttribute("expiredBookingsCount", expiredBookings.size());
            model.addAttribute("expiryThresholdHours", expiryThresholdHours);
        }
        model.addAttribute("expiryNotificationsEnabled", expiryNotificationsEnabled);
        
        model.addAttribute("pageTitle", "Staff Dashboard");
        model.addAttribute("userRole", "STAFF");
        
        if (remindersEnabled) {
            List<Booking> upcomingBookings = bookingService.getUpcomingConfirmedBookings(remindersHoursAhead);
            model.addAttribute("upcomingBookings", upcomingBookings);
            model.addAttribute("upcomingBookingsCount", upcomingBookings.size());
        }
        model.addAttribute("remindersEnabled", remindersEnabled);
        return "staff/dashboard";
    }
}
