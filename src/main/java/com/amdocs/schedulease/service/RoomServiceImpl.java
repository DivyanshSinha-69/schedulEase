package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.exception.RoomNotFoundException;
import com.amdocs.schedulease.exception.ResourceUnavailableException;
import com.amdocs.schedulease.repository.BookingRoomRepository;
import com.amdocs.schedulease.repository.RoomRepository;
import com.amdocs.schedulease.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UserAccountRepository userAccountRepository;
    private final BookingRoomRepository bookingRoomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, 
                           UserAccountRepository userAccountRepository,
                           BookingRoomRepository bookingRoomRepository) {
        this.roomRepository = roomRepository;
        this.userAccountRepository = userAccountRepository;
        this.bookingRoomRepository = bookingRoomRepository;
    }

    @Override
    public Room createRoom(Room room, Long createdByUserId) {
        UserAccount createdBy = userAccountRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + createdByUserId));
        
        room.setCreatedBy(createdBy);
        room.setCreatedAt(LocalDateTime.now());
        room.setStatus(Room.RoomStatus.AVAILABLE);
        
        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getRoomsByFloor(String floor) {
        return roomRepository.findByFloor(floor);
    }

    @Override
    public Room updateRoom(Long id, Room room) {
        Room existingRoom = getRoomById(id);
        
        existingRoom.setName(room.getName());
        existingRoom.setFloor(room.getFloor());
        existingRoom.setOccupancy(room.getOccupancy());
        existingRoom.setStatus(room.getStatus());
        existingRoom.setUpdatedAt(LocalDateTime.now());
        
        return roomRepository.save(existingRoom);
    }

    @Override
    public Room updateRoomStatus(Long id, Room.RoomStatus status) {
        Room room = getRoomById(id);
        room.setStatus(status);
        room.setUpdatedAt(LocalDateTime.now());
        
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        
        // Check if room has any associated bookings
        boolean hasBookings = bookingRoomRepository.existsByRoomId(id);
        
        if (hasBookings) {
            throw new ResourceUnavailableException(
                "Cannot delete room '" + room.getName() + "' because it has existing bookings. " +
                "Please cancel or reassign all bookings before deleting this room."
            );
        }
        
        // If no bookings, delete the room
        roomRepository.delete(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> searchRooms(String keyword) {
        return roomRepository.findByNameContainingIgnoreCaseOrFloorContainingIgnoreCase(keyword, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoomAvailable(Long roomId) {
        Room room = getRoomById(roomId);
        return room.getStatus() == Room.RoomStatus.AVAILABLE;
    }
}
