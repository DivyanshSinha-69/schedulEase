package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.Booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("SELECT DISTINCT b FROM Booking b " +
            "LEFT JOIN FETCH b.user " +
            "LEFT JOIN FETCH b.rooms " +
            "WHERE b.status = :status AND b.createdAt < :threshold " +
            "ORDER BY b.createdAt ASC")
     List<Booking> findByStatusAndCreatedAtBefore(
         @Param("status") Booking.BookingStatus status, 
         @Param("threshold") LocalDateTime threshold
     );
    
    @Query("SELECT DISTINCT b FROM Booking b " +
    	       "LEFT JOIN FETCH b.user " +
    	       "LEFT JOIN FETCH b.rooms " +
    	       "WHERE b.status = 'CONFIRMED' AND b.startDatetime BETWEEN :start AND :end " +
    	       "ORDER BY b.startDatetime ASC")
    	List<Booking> findConfirmedBookingsBetween(
    	    @Param("start") LocalDateTime start, 
    	    @Param("end") LocalDateTime end
    	);
    @Query("SELECT b FROM Booking b JOIN b.rooms r WHERE r.id = :roomId AND b.startDatetime < :dayEnd AND b.endDatetime > :dayStart")
    List<Booking> findBookingsByRoomAndTimeRange(@Param("roomId") Long roomId, 
                                                  @Param("dayStart") LocalDateTime dayStart, 
                                                  @Param("dayEnd") LocalDateTime dayEnd);

}
