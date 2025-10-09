package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.BookingEquipment;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.entity.EquipmentType;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

public interface BookingService {
	
	// Staff approval/decline methods
	Booking approveBooking(Long bookingId);
	Booking declineBooking(Long bookingId, String reason);

    
    // Create a new booking
    Booking createBooking(Booking booking);
    
    // Get all bookings for a specific user
    List<Booking> getBookingsByUserId(Long userId);
    
    // Get booking by ID
    Booking getBookingById(Long bookingId);
    
    // Cancel a booking
    Booking cancelBooking(Long bookingId, String cancelReason);
    
    // Get all bookings with a specific status
    List<Booking> getBookingsByStatus(Booking.BookingStatus status);
    
    // Check if booking has conflicts with existing bookings
    boolean hasConflict(List<Long> roomIds, LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    // Get all bookings (for admin/staff)
    List<Booking> getAllBookings();
    
    //Get all available rooms
    List<Room> getAllAvailableRooms();
    
    //Get all available equipment
    List<EquipmentStock> getAllAvailableEquipment();
    
    //Get unique room floors
    List<String> getUniqueRoomFloors();
    
    List<Room> getRoomsByIds(List<Long> roomIds);
    
    List<BookingEquipment> getEquipmentFromParams(Map<String, String> allParams);
    
    Booking createBookingWithAllocations(
    	    UserAccount userAccount,
    	    Set<Room> selectedRooms,
    	    Map<Long, Integer> equipmentQuantities, // equipmentTypeId -> quantity
    	    LocalDateTime startDateTime,
    	    LocalDateTime endDateTime,
    	    String purposeNotes
    	);
    
    Page<Booking> getPaginatedBookingsByUserId(Long userId, int page, int pageSize);
}
