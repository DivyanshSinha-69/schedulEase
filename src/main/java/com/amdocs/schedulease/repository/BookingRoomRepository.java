package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.BookingRoom;
import com.amdocs.schedulease.entity.BookingRoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRoomRepository extends JpaRepository<BookingRoom, BookingRoomId> {
    
    /**
     * Check if a room has any bookings
     * @param roomId the room ID
     * @return true if room has bookings, false otherwise
     */
    boolean existsByRoomId(Long roomId);
}
