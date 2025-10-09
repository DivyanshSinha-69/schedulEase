package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.BookingEquipment;
import com.amdocs.schedulease.entity.BookingEquipmentId;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.service.BookingService;
import com.amdocs.schedulease.service.UserService;
import com.amdocs.schedulease.util.RoomTimeline;
import com.amdocs.schedulease.util.TimeSlot;

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
import java.util.ArrayList;
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
//    @GetMapping("/create")
//    public String showCreateBookingForm(Model model) {
//        model.addAttribute("booking", new Booking());
//        model.addAttribute("availableRooms", bookingService.getAllAvailableRooms());
//        model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());
//        model.addAttribute("roomFloors", bookingService.getUniqueRoomFloors());
//        
//        return "user/booking-create-step1";
//    }

	@GetMapping("/create")
	public String showBookingPage(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String floor, @RequestParam(required = false) String occupancy,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime,
			@RequestParam(required = false, value = "rooms") List<Long> roomIds, Model model) {

		// Always provide floors and equipment for dropdowns
		model.addAttribute("floors", bookingService.getUniqueRoomFloors());
		model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());

		// Only show rooms if a date is selected
		List<Room> availableRooms = (startDate != null && !startDate.trim().isEmpty())
				? bookingService.getAvailableRoomsFiltered(startDate, startTime, endTime, floor, occupancy, sort)
				: List.of();
		model.addAttribute("availableRooms", availableRooms);

		// Preserve selections for the view
		model.addAttribute("selectedDate", startDate);
		model.addAttribute("selectedStartTime", startTime);
		model.addAttribute("selectedEndTime", endTime);
		model.addAttribute("selectedFloor", floor);
		model.addAttribute("selectedOccupancy", occupancy);
		model.addAttribute("selectedSort", sort);
		model.addAttribute("selectedRoomIds", roomIds);

		return "user/booking-create";
	}

	// ========== STEP 1: Handle form submission (proceed to step 2) ==========

//    @PostMapping("/create")
//    public String processStep1(
//        @ModelAttribute Booking booking,
//        @RequestParam(value = "rooms", required = false) List<Long> roomIds,
//        @RequestParam Map<String, String> allParams,
//        Model model
//    ) {
//        // Validate that at least one room is selected
//        if (roomIds == null || roomIds.isEmpty()) {
//            model.addAttribute("error", "Please select at least one room.");
//            model.addAttribute("availableRooms", bookingService.getAllAvailableRooms());
//            model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());
//            model.addAttribute("roomFloors", bookingService.getUniqueRoomFloors());
//            return "user/booking-create-step1";
//        }
//
//        // Set selected rooms
//        List<Room> selectedRooms = bookingService.getRoomsByIds(roomIds);
//        booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));
//
//
//        // Set selected equipment (parse from allParams, e.g. equipment_1=2)
//        List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
//        booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));
//
//        // Pass data to Step 2
//        model.addAttribute("booking", booking);
//        model.addAttribute("selectedRooms", selectedRooms);
//        model.addAttribute("selectedEquipments", selectedEquipments);
//
//        return "user/booking-create-step2";
//    }

	// Handle POST form submission to persist/filter state (optional, often same as
	// GET above)
	@PostMapping("/create")
	public String processBookingForm(@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime,
			@RequestParam(required = false) String floor, @RequestParam(required = false) String occupancy,
			@RequestParam(required = false) String sort,
			@RequestParam(required = false, value = "rooms") List<Long> roomIds,
			@RequestParam Map<String, String> allParams, Model model) {
//		System.out.println("Parsing date: '" + date + "'");

		String cleanDate = startDate != null ? startDate.trim().replaceAll(",$", "") : "";
		String cleanStartTime = (startTime != null) ? startTime.trim() : "";
		String cleanEndTime = (endTime != null) ? endTime.trim() : "";

		boolean hasError = false;
		if (cleanDate.isEmpty()) {
			model.addAttribute("error", "Please select a booking date.");
			hasError = true;
		}
		if (cleanStartTime.isEmpty()) {
			model.addAttribute("error", "Please select a start time.");
			hasError = true;
		}
		if (cleanEndTime.isEmpty()) {
			model.addAttribute("error", "Please select an end time.");
			hasError = true;
		}

		// Repopulate dropdowns and selections for the view
		List<Room> availableRooms = cleanDate.isEmpty() ? List.of()
	            : bookingService.getAvailableRoomsFiltered(cleanDate, cleanStartTime, cleanEndTime, floor, occupancy, sort);

	    // Always set attributes needed for the view
	    setCommonFormAttributes(model, startDate, startTime, endTime, floor, occupancy, sort, roomIds, availableRooms);

	    // Build timelines for UI
	    List<RoomTimeline> roomTimelines = buildRoomTimelines(availableRooms, cleanDate, LocalTime.of(0, 0), LocalTime.of(23, 59));
	    model.addAttribute("roomTimelines", roomTimelines);

		// If any required field is missing, show error and filtered rooms (if possible)
	    if (hasError) {
	        return "user/booking-create";
	    }

		// Parse date/time safely
	    try {
	        LocalDate parsedDate = LocalDate.parse(cleanDate);
	        LocalTime parsedStartTime = LocalTime.parse(cleanStartTime);
	        LocalTime parsedEndTime = LocalTime.parse(cleanEndTime);
	        // ...continue booking logic...
	    } catch (Exception ex) {
	        model.addAttribute("error", "Invalid date or time format. Please use the pickers.");
	        return "user/booking-create";
	    }

		// Repopulate rooms for the view
		availableRooms = bookingService.getAvailableRoomsFiltered(cleanDate, cleanStartTime, cleanEndTime,
				floor, occupancy, sort);
		model.addAttribute("availableRooms", availableRooms);
		
		roomTimelines = buildRoomTimelines(availableRooms, cleanDate, LocalTime.of(8, 0), LocalTime.of(23, 59));
	    model.addAttribute("roomTimelines", roomTimelines);

		// Continue with booking logic or redirect as needed
		return "user/booking-create";
	}

	// ========== STEP 2: Show date/time selection ==========

//	@GetMapping("/create/step2")
//	public String showStep2(@RequestParam List<Long> rooms, @RequestParam Map<String, String> allParams, Model model) {
//		// Reconstruct booking from hidden fields
//		Booking booking = new Booking();
//		// Set selected rooms
//		List<Room> selectedRooms = bookingService.getRoomsByIds(rooms);
//		booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));
//
//		// Set selected equipment (parse from allParams, e.g. equipment_1=2)
//		List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
//		booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));
//
//		model.addAttribute("booking", booking);
//		model.addAttribute("selectedRooms", selectedRooms);
//		model.addAttribute("selectedEquipments", selectedEquipments);
//		// Optionally, add empty/default date/time fields
//		model.addAttribute("startDate", "");
//		model.addAttribute("startTime", "");
//		model.addAttribute("endTime", "");
//		return "user/booking-create-step2";
//	}

	// ========== STEP 2: Check availability ==========

//	@PostMapping("/checkAvailability")
//	public String checkAvailability(@RequestParam String startDate, @RequestParam String startTime,
//			@RequestParam String endTime, @RequestParam(required = false) List<Long> rooms,
//			@RequestParam Map<String, String> allParams, Model model) {
//		// Defensive: Clean up date/time
//		String cleanStartDate = startDate != null ? startDate.trim().replaceAll(",$", "") : "";
//		String cleanStartTime = startTime != null ? startTime.trim().replaceAll(",$", "") : "";
//		String cleanEndTime = endTime != null ? endTime.trim().replaceAll(",$", "") : "";
//
//		// Reconstruct Booking
//		Booking booking = new Booking();
//		List<Room> selectedRooms = (rooms != null) ? bookingService.getRoomsByIds(rooms) : List.of();
//		booking.setRooms(selectedRooms.stream().collect(Collectors.toSet()));
//		List<BookingEquipment> selectedEquipments = bookingService.getEquipmentFromParams(allParams);
//		booking.setEquipmentAllocations(selectedEquipments.stream().collect(Collectors.toSet()));
//
//		// Parse date/time only if non-empty
//		if (!cleanStartDate.isEmpty() && !cleanStartTime.isEmpty() && !cleanEndTime.isEmpty()) {
//			LocalDate date = LocalDate.parse(cleanStartDate);
//			LocalTime start = LocalTime.parse(cleanStartTime);
//			LocalTime end = LocalTime.parse(cleanEndTime);
//			booking.setStartDatetime(LocalDateTime.of(date, start));
//			booking.setEndDatetime(LocalDateTime.of(date, end));
//		}
//
//		boolean isAvailable = false;
//		if (booking.getStartDatetime() != null && booking.getEndDatetime() != null && !selectedRooms.isEmpty()) {
//			List<Long> roomIds = selectedRooms.stream().map(Room::getId).toList();
//			isAvailable = !bookingService.hasConflict(roomIds, booking.getStartDatetime(), booking.getEndDatetime());
//			model.addAttribute("availabilityChecked", true);
//		}
//
//		// Always add all user inputs back to the model!
//		model.addAttribute("booking", booking);
//		model.addAttribute("selectedRooms", selectedRooms);
//		model.addAttribute("selectedEquipments", selectedEquipments);
//		model.addAttribute("isAvailable", isAvailable);
//		model.addAttribute("startDate", cleanStartDate);
//		model.addAttribute("startTime", cleanStartTime);
//		model.addAttribute("endTime", cleanEndTime);
//
//		return "user/booking-create-step2";
//	}

	// ========== STEP 3: Show confirmation page ==========

	@GetMapping("/confirm")
	public String showConfirmation(@RequestParam String startDate, @RequestParam String startTime,
			@RequestParam String endTime, @RequestParam List<Long> rooms, @RequestParam Map<String, String> allParams,
			Model model) {
		// Defensive: Clean up date/time
		String cleanStartDate = startDate!= null ? startDate.trim().replaceAll(",$", "") : "";
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

	@PostMapping("/confirm")
	public String confirmBooking(@RequestParam String startDate, @RequestParam String startTime,
			@RequestParam String endTime, @RequestParam List<Long> rooms, @RequestParam Map<String, String> allParams,
			@RequestParam(required = false) String purposeNotes, HttpSession session,
			RedirectAttributes redirectAttributes) {
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
		Booking booking = bookingService.createBookingWithAllocations(userAccount, selectedRooms, equipmentQuantities,
				startDateTime, endDateTime, purposeNotes);

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
	public String listUserBookings(HttpSession session, @RequestParam(defaultValue = "1") int page, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null)
			return "redirect:/login";

		int pageSize = 6;
		Page<Booking> bookingPage = bookingService.getPaginatedBookingsByUserId(userId, page - 1, pageSize);

		model.addAttribute("bookingList", bookingPage.getContent());
		model.addAttribute("page", page);
		model.addAttribute("totalPages", bookingPage.getTotalPages());
		model.addAttribute("userFullName", userService.getUserFullName(userId));
		return "user/my-bookings";
	}

	// ========== Cancel booking ==========
	@GetMapping("/cancel/{id}")
	public String openCancelModal(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
		Booking booking = bookingService.getBookingById(id);

		model.addAttribute("booking", booking);

		return "user/cancel-booking";
	}

	@PostMapping("/cancel/{id}")
	public String cancelBooking(@PathVariable Long id, @RequestParam String reason,
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

	
	
	
	
	// ============ Helper==================
	
	private void setCommonFormAttributes(Model model, String date, String startTime, String endTime, String floor,
			String occupancy, String sort, List<Long> roomIds, List<Room> availableRooms) {
		model.addAttribute("floors", bookingService.getUniqueRoomFloors());
		model.addAttribute("availableEquipments", bookingService.getAllAvailableEquipment());
		model.addAttribute("selectedDate", date);
		model.addAttribute("selectedStartTime", startTime);
		model.addAttribute("selectedEndTime", endTime);
		model.addAttribute("selectedFloor", floor);
		model.addAttribute("selectedOccupancy", occupancy);
		model.addAttribute("selectedSort", sort);
		model.addAttribute("selectedRoomIds", roomIds);
		model.addAttribute("availableRooms", availableRooms);
	}

	private List<RoomTimeline> buildRoomTimelines(List<Room> rooms, String date, LocalTime businessStart,
			LocalTime businessEnd) {
		LocalDate parsedDate;
		try {
			parsedDate = LocalDate.parse(date);
		} catch (Exception ex) {
			return List.of();
		}
		List<RoomTimeline> timelines = new ArrayList<>();
		for (Room room : rooms) {
			List<TimeSlot> freeSlots = bookingService.getFreeSlotsForRoom(room, parsedDate, businessStart, businessEnd);
			timelines.add(new RoomTimeline(room.getId(), room.getName(), freeSlots));
		}
		return timelines;
	}
}
