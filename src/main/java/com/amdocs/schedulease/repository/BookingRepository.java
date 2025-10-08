package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.Booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    // Find overlapping bookings for conflict detection
    @Query("SELECT b FROM Booking b JOIN b.rooms r WHERE r.id IN :roomIds " +
           "AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND b.startDatetime < :endDatetime AND b.endDatetime > :startDatetime")
    List<Booking> findOverlappingBookings(@Param("roomIds") List<Long> roomIds,
                                          @Param("startDatetime") LocalDateTime startDatetime,
                                          @Param("endDatetime") LocalDateTime endDatetime);
    
    Page<Booking> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);
}
