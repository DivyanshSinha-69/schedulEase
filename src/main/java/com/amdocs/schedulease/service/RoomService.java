package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Room;
import java.util.List;

public interface RoomService {
    
    // Create
    Room createRoom(Room room, Long createdByUserId);
    
    // Read
    List<Room> getAllRooms();
    Room getRoomById(Long id);
    List<Room> getRoomsByStatus(Room.RoomStatus status);
    List<Room> getRoomsByFloor(String floor);
    
    // Update
    Room updateRoom(Long id, Room room);
    Room updateRoomStatus(Long id, Room.RoomStatus status);
    
    // Delete
    void deleteRoom(Long id);
    
    // Search
    List<Room> searchRooms(String keyword);
    
    // Availability check
    boolean isRoomAvailable(Long roomId);
}
