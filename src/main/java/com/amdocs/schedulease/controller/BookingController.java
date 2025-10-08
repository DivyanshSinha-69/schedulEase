package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.BookingEquipment;
import com.amdocs.schedulease.entity.BookingEquipmentId;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.service.BookingService;
import com.amdocs.schedulease.service.UserService;

import jakarta.servlet.http.HttpSession;

import com.amdocs.schedulease.exception.BookingConflictException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @Autowired
    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // ========== STEP 1: Show booking creation form ==========a
    @GetMapping("/create")
    public String showCreateBookingForm(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("availableRooms", bookingService.getAllAvailableRooms());
        model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());
        model.addAttribute("roomFloors", bookingService.getUniqueRoomFloors());
        
        return "user/booking-create-step1";
    }

    // ========== STEP 1: Handle form submission (proceed to step 2) ==========
//    @PostMapping("/create")
//    public String processStep1(@ModelAttribute Booking booking, Model model) {
//        // Validate that at least one room is selected
//        if (booking.getRooms() == null || booking.getRooms().isEmpty()) {
//            model.addAttribute("error", "Please select at least one room.");
//            model.addAttribute("availableRooms", bookingService.getAllAvailableRooms());
//            model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());
//            model.addAttribute("roomFloors", bookingService.getUniqueRoomFloors());
//            return "user/booking-create-step1";
//        }
//        
//        // Store booking data in session or pass to next step
//        // For now, redirect to step 2 with booking data
//        model.addAttribute("booking", booking);
//        model.addAttribute("selectedRooms", booking.getRooms());
//        
//        return "user/booking-create-step2";
//    }
    
    @PostMapping("/create")
    public String processStep1(
        @ModelAttribute Booking booking,
        @RequestParam(value = "rooms", required = false) List<Long> roomIds,
        @RequestParam Map<String, String> allParams,
        Model model
    ) {
        // Validate that at least one room is selected
        if (roomIds == null || roomIds.isEmpty()) {
            model.addAttribute("error", "Please select at least one room.");
            model.addAttribute("availableRooms", bookingService.getAllAvailableRooms());
            model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());
            model.addAttribute("roomFloors", bookingService.getUniqueRoomFloors());
            return "user/booking-create-step1";
        }

        // Set selected rooms
        List<Room> selectedRooms = bookingService.getRoomsByIds(roomIds);
        booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));


        // Set selected equipment (parse from allParams, e.g. equipment_1=2)
        List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
        booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));

        // Pass data to Step 2
        model.addAttribute("booking", booking);
        model.addAttribute("selectedRooms", selectedRooms);
        model.addAttribute("selectedEquipments", selectedEquipments);

        return "user/booking-create-step2";
    }


    // ========== STEP 2: Show date/time selection ==========
//    @GetMapping("/create/step2")
//    public String showStep2(Model model) {
//        // In a real app, retrieve booking from session
//        model.addAttribute("booking", new Booking());
//        return "user/booking-create-step2";
//    }
    
    @GetMapping("/create/step2")
    public String showStep2(
        @RequestParam List<Long> rooms,
        @RequestParam Map<String, String> allParams,
        Model model
    ) {
        // Reconstruct booking from hidden fields
        Booking booking = new Booking();
     // Set selected rooms
        List<Room> selectedRooms = bookingService.getRoomsByIds(rooms);
        booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));


        // Set selected equipment (parse from allParams, e.g. equipment_1=2)
        List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
        booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));


        model.addAttribute("booking", booking);
        model.addAttribute("selectedRooms", selectedRooms);
        model.addAttribute("selectedEquipments", selectedEquipments);
        // Optionally, add empty/default date/time fields
        model.addAttribute("startDate", "");
        model.addAttribute("startTime", "");
        model.addAttribute("endTime", "");
        return "user/booking-create-step2";
    }


    // ========== STEP 2: Check availability ==========
//    @PostMapping("/checkAvailability")
//    public String checkAvailability(@RequestParam String startDate,
//    	    @RequestParam String startTime,
//    	    @RequestParam String endTime,
//    	    @ModelAttribute Booking booking, Model model) {
//    	
//    	
//    	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//        LocalDate date = LocalDate.parse(startDate, dateFormatter);
//        LocalTime start = LocalTime.parse(startTime, timeFormatter);
//        LocalTime end = LocalTime.parse(endTime, timeFormatter);
//
//        booking.setStartDatetime(LocalDateTime.of(date, start));
//        booking.setEndDatetime(LocalDateTime.of(date, end));
//    	
//    	
//        // Extract room IDs for conflict check
//    	
//        List<Long> roomIds = booking.getRooms().stream()
//                                    .map(Room::getId)
//                                    .collect(java.util.stream.Collectors.toList());
//        
//        boolean isAvailable = !bookingService.hasConflict(
//            roomIds, 
//            booking.getStartDatetime(), 
//            booking.getEndDatetime()
//        );
//        
//        model.addAttribute("booking", booking);
//        model.addAttribute("availabilityChecked", true);
//        model.addAttribute("isAvailable", isAvailable);
//        model.addAttribute("selectedRooms", booking.getRooms());
//        model.addAttribute("startDate", startDate);
//        model.addAttribute("startTime", startTime);
//        model.addAttribute("endTime", endTime);
//        
//        return "user/booking-create-step2";
//    }
    
    
//    @PostMapping("/checkAvailability")
//    public String checkAvailability(
//        @RequestParam String startDate,
//        @RequestParam String startTime,
//        @RequestParam String endTime,
//        @RequestParam List<Long> rooms,
//        @RequestParam Map<String, String> allParams,
//        Model model
//    ) {
//        // Reconstruct booking
//        Booking booking = new Booking();
//     // Set selected rooms
//        List<Room> selectedRooms = bookingService.getRoomsByIds(rooms);
//        booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));
//
//
//        // Set selected equipment (parse from allParams, e.g. equipment_1=2)
//        List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
//        booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));
//        
//        
//        String cleanStartDate = startDate.trim().replaceAll(",$", "");
//        String cleanStartTime = startTime.trim().replaceAll(",$", "");
//        String cleanEndTime = endTime.trim().replaceAll(",$", "");
//
//        // Parse date/time
//        LocalDate date = LocalDate.parse(cleanStartDate);
//        LocalTime start = LocalTime.parse(cleanStartTime);
//        LocalTime end = LocalTime.parse(cleanEndTime);
//        booking.setStartDatetime(LocalDateTime.of(date, start));
//        booking.setEndDatetime(LocalDateTime.of(date, end));
//
//        // Check availability
//        List<Long> roomIds = selectedRooms.stream().map(Room::getId).toList();
//        boolean isAvailable = !bookingService.hasConflict(roomIds, booking.getStartDatetime(), booking.getEndDatetime());
//
//        // Add all data to model for view
//        model.addAttribute("booking", booking);
//        model.addAttribute("selectedRooms", selectedRooms);
//        model.addAttribute("selectedEquipments", selectedEquipments);
//        model.addAttribute("availabilityChecked", true);
//        model.addAttribute("isAvailable", isAvailable);
//        model.addAttribute("startDate", startDate);
//        model.addAttribute("startTime", startTime);
//        model.addAttribute("endTime", endTime);
//
//        return "user/booking-create-step2";
//    }
    
    @PostMapping("/checkAvailability")
    public String checkAvailability(
        @RequestParam String startDate,
        @RequestParam String startTime,
        @RequestParam String endTime,
        @RequestParam(required = false) List<Long> rooms,
        @RequestParam Map<String, String> allParams,
        Model model
    ) {
        // Defensive: Clean up date/time
        String cleanStartDate = startDate != null ? startDate.trim().replaceAll(",$", "") : "";
        String cleanStartTime = startTime != null ? startTime.trim().replaceAll(",$", "") : "";
        String cleanEndTime = endTime != null ? endTime.trim().replaceAll(",$", "") : "";

        // Reconstruct Booking
        Booking booking = new Booking();
        List<Room> selectedRooms = (rooms != null) ? bookingService.getRoomsByIds(rooms) : List.of();
        booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));
        List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
        booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));

        // Parse date/time only if non-empty
        if (!cleanStartDate.isEmpty() && !cleanStartTime.isEmpty() && !cleanEndTime.isEmpty()) {
            LocalDate date = LocalDate.parse(cleanStartDate);
            LocalTime start = LocalTime.parse(cleanStartTime);
            LocalTime end = LocalTime.parse(cleanEndTime);
            booking.setStartDatetime(LocalDateTime.of(date, start));
            booking.setEndDatetime(LocalDateTime.of(date, end));
        }

        boolean isAvailable = false;
        if (booking.getStartDatetime() != null && booking.getEndDatetime() != null && !selectedRooms.isEmpty()) {
            List<Long> roomIds = selectedRooms.stream().map(Room::getId).toList();
            isAvailable = !bookingService.hasConflict(roomIds, booking.getStartDatetime(), booking.getEndDatetime());
            model.addAttribute("availabilityChecked", true);
        }

        // Always add all user inputs back to the model!
        model.addAttribute("booking", booking);
        model.addAttribute("selectedRooms", selectedRooms);
        model.addAttribute("selectedEquipments", selectedEquipments);
        model.addAttribute("isAvailable", isAvailable);
        model.addAttribute("startDate", cleanStartDate);
        model.addAttribute("startTime", cleanStartTime);
        model.addAttribute("endTime", cleanEndTime);

        return "user/booking-create-step2";
    }


    // ========== STEP 3: Show confirmation page ==========
//    @GetMapping("/confirm")
//    public String showConfirmation(@ModelAttribute Booking booking, Model model) {
//        model.addAttribute("booking", booking);
//        model.addAttribute("selectedRooms", booking.getRooms());
//        model.addAttribute("selectedEquipments", booking.getEquipmentAllocations());
//        
//        return "user/booking-create-step3";
//    }
    
    @GetMapping("/confirm")
    public String showConfirmation(
        @RequestParam String startDate,
        @RequestParam String startTime,
        @RequestParam String endTime,
        @RequestParam List<Long> rooms,
        @RequestParam Map<String, String> allParams,
        Model model
    ) {
        // Defensive: Clean up date/time
        String cleanStartDate = startDate != null ? startDate.trim().replaceAll(",$", "") : "";
        String cleanStartTime = startTime != null ? startTime.trim().replaceAll(",$", "") : "";
        String cleanEndTime = endTime != null ? endTime.trim().replaceAll(",$", "") : "";

        // Reconstruct Booking
        Booking booking = new Booking();
        List<Room> selectedRooms = (rooms != null) ? bookingService.getRoomsByIds(rooms) : List.of();
        booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));
        List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
        booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));

        if (!cleanStartDate.isEmpty() && !cleanStartTime.isEmpty() && !cleanEndTime.isEmpty()) {
            LocalDate date = LocalDate.parse(cleanStartDate);
            LocalTime start = LocalTime.parse(cleanStartTime);
            LocalTime end = LocalTime.parse(cleanEndTime);
            booking.setStartDatetime(LocalDateTime.of(date, start));
            booking.setEndDatetime(LocalDateTime.of(date, end));
        }

        // Add all data to model for view
        model.addAttribute("booking", booking);
        model.addAttribute("selectedRooms", selectedRooms);
        model.addAttribute("selectedEquipments", selectedEquipments);
        model.addAttribute("startDate", cleanStartDate);
        model.addAttribute("startTime", cleanStartTime);
        model.addAttribute("endTime", cleanEndTime);

        return "user/booking-create-step3";
    }


    // ========== STEP 3: Confirm and create booking ==========
//    @PostMapping("/confirm")
//    public String confirmBooking(@ModelAttribute Booking booking, 
//                                 @RequestParam(required = false) String purposeNotes,
//                                 @RequestParam(required = false) Boolean reminder,
//                                 RedirectAttributes redirectAttributes) {
//        try {
//            // Set optional fields
//            booking.setBookingReason(purposeNotes);
//            // Handle reminder logic if needed
//            
//            Booking savedBooking = bookingService.createBooking(booking);
//            
//            redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
//            return "redirect:/booking/details/" + savedBooking.getId();
//            
//        } catch (BookingConflictException ex) {
//            redirectAttributes.addFlashAttribute("error", ex.getMessage());
//            return "redirect:/booking/create";
//        }
//    }
    
    @PostMapping("/confirm")
    public String confirmBooking(
        @RequestParam String startDate,
        @RequestParam String startTime,
        @RequestParam String endTime,
        @RequestParam List<Long> rooms,
        @RequestParam Map<String, String> allParams,
        @RequestParam(required = false) String purposeNotes,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        // 1. Get the current user from session
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "User not logged in.");
            return "redirect:/login";
        }
        UserAccount userAccount = userService.getUserById(userId);

        // 2. Get selected rooms
        Set<Room> selectedRooms = new HashSet<>(bookingService.getRoomsByIds(rooms));

        // 3. Parse date/time
        LocalDate date = LocalDate.parse(startDate.trim());
        LocalTime start = LocalTime.parse(startTime.trim());
        LocalTime end = LocalTime.parse(endTime.trim());
        LocalDateTime startDateTime = LocalDateTime.of(date, start);
        LocalDateTime endDateTime = LocalDateTime.of(date, end);

        // 4. Get equipment quantities from params
        Map<Long, Integer> equipmentQuantities = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("equipment_")) {
                Long equipmentTypeId = Long.valueOf(entry.getKey().substring("equipment_".length()));
                Integer quantity = Integer.valueOf(entry.getValue());
                if (quantity > 0) {
                    equipmentQuantities.put(equipmentTypeId, quantity);
                }
            }
        }

        // 5. Call the service method
        Booking booking = bookingService.createBookingWithAllocations(
            userAccount,
            selectedRooms,
            equipmentQuantities,
            startDateTime,
            endDateTime,
            purposeNotes
        );

        redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
        return "redirect:/booking/details/" + booking.getId();
    }


    // ========== View booking details ==========
    @GetMapping("/details/{id}")
    public String viewBookingDetails(@PathVariable Long id, Model model) {
        Booking booking = bookingService.getBookingById(id);
        
        if (booking == null) {
            model.addAttribute("error", "Booking not found.");
            return "error/404";
        }
        
        model.addAttribute("booking", booking);
        return "user/booking-details";
    }

    // ========== List user's bookings ==========
    @GetMapping("/my-bookings")
    public String listUserBookings(HttpSession session,
                                   @RequestParam(defaultValue = "1") int page,
                                   Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        int pageSize = 6;
        Page<Booking> bookingPage = bookingService.getPaginatedBookingsByUserId(userId, page - 1, pageSize);

        model.addAttribute("bookingList", bookingPage.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", bookingPage.getTotalPages());
        model.addAttribute("userFullName", userService.getUserFullName(userId));
        return "user/my-bookings";
    }


    // ========== Cancel booking ==========
    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id, 
                               @RequestParam String reason,
                               RedirectAttributes redirectAttributes) {
        Booking cancelled = bookingService.cancelBooking(id, reason);
        
        if (cancelled != null) {
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel booking.");
        }
        
        return "redirect:/booking/my-bookings";
    }

    // ========== Admin: View all bookings ==========
    @GetMapping("/admin/all")
    public String viewAllBookings(Model model) {
        List<Booking> allBookings = bookingService.getAllBookings();
        model.addAttribute("bookings", allBookings);
        return "admin/monitor-bookings";
    }
}
