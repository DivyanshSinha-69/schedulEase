package com.amdocs.schedulease.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.entity.Room;

public interface AnalyticsService {
    
    /**
     * Get total user count
     * @return Total number of users
     */
    long getTotalUserCount();
    
    /**
     * Get active user count
     * @return Number of active users
     */
    long getActiveUserCount();
    
    /**
     * Get pending user count
     * @return Number of pending users
     */
    long getPendingUserCount();
    
    /**
     * Get total booking count
     * @return Total number of bookings
     */
    long getTotalBookingCount();
    
    /**
     * Get booking count by status
     * @return Map of status to count
     */
    Map<String, Long> getBookingCountByStatus();
    
    /**
     * Get total room count
     * @return Total number of rooms
     */
    long getTotalRoomCount();
    
    /**
     * Get available room count
     * @return Number of available rooms
     */
    long getAvailableRoomCount();
    
    /**
     * Get total equipment count
     * @return Total number of equipment types
     */
    long getTotalEquipmentCount();
    
    /**
     * Get bookings for date range
     * @param startDate Start date
     * @param endDate End date
     * @return Number of bookings in range
     */
    long getBookingsInDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get most booked rooms (top 5)
     * @return Map of room name to booking count
     */
    Map<String, Long> getMostBookedRooms();
    
    /**
     * Get booking trend (last 7 days)
     * @return Map of date to booking count
     */
    Map<LocalDate, Long> getBookingTrend();
    List<Booking> getAllBookings();
    List<Room> getAllRooms();
    List<EquipmentStock> getAllEquipment();

}
