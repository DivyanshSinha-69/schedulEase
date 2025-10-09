package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;
import java.util.List;

public interface BookingService {
    
    List<Booking> getAllBookings();
    Booking getBookingById(Long id);
    List<Booking> getBookingsByStatus(Booking.BookingStatus status);
    List<Booking> getBookingsByUserId(Long userId);
}
