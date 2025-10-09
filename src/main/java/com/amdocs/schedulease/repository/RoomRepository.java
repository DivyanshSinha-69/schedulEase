package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    List<Room> findByStatus(Room.RoomStatus status);
    
    List<Room> findByFloor(String floor);
    
    List<Room> findByNameContainingIgnoreCaseOrFloorContainingIgnoreCase(String name, String floor);
}
