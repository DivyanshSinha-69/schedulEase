package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff/bookings")
public class StaffBookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping
    public String listAllBookings(
            @RequestParam(required = false) String status,
            Model model) {
        
        List<Booking> bookings;
        
        if (status != null && !status.isEmpty()) {
            bookings = bookingService.getBookingsByStatus(Booking.BookingStatus.valueOf(status));
            model.addAttribute("filterStatus", status);
        } else {
            bookings = bookingService.getAllBookings();
        }
        
        model.addAttribute("bookings", bookings);
        model.addAttribute("pageTitle", "All Bookings");
        model.addAttribute("userRole", "STAFF");
        
        return "staff/bookings-list";
    }

    @GetMapping("/view/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);
        model.addAttribute("pageTitle", "Booking Details");
        model.addAttribute("userRole", "STAFF");
        return "staff/booking-details";
    }
    @PostMapping("/approve/{id}")
    public String approveBooking(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            Booking booking = bookingService.approveBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Booking #" + booking.getId() + " approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to approve booking: " + e.getMessage());
        }
        
        return "redirect:/staff/bookings/view/" + id;
    }

    // NEW: Decline booking
    @PostMapping("/decline/{id}")
    public String declineBooking(
            @PathVariable Long id,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Decline reason is required");
            }
            
            Booking booking = bookingService.declineBooking(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Booking #" + booking.getId() + " declined successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to decline booking: " + e.getMessage());
        }
        
        return "redirect:/staff/bookings/view/" + id;
    }

    // NEW: Cancel confirmed booking
    @PostMapping("/cancel/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Cancel reason is required");
            }
            
            Booking booking = bookingService.cancelBooking(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Booking #" + booking.getId() + " cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to cancel booking: " + e.getMessage());
        }
        
        return "redirect:/staff/bookings/view/" + id;
    }
}
