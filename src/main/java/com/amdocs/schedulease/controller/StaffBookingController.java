package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}
