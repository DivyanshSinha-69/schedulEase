package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.BookingEquipment;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.entity.EquipmentType;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.util.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

public interface BookingService {
    
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
    
    public List<Room> getAvailableRoomsFiltered(
            String date, String startTime, String endTime,
            String floor, String occupancy, String sort);
    
    List<TimeSlot> getFreeSlotsForRoom(Room room, LocalDate date, LocalTime dayStart, LocalTime dayEnd);
}
