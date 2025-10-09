package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.BookingEquipment;
import com.amdocs.schedulease.entity.BookingEquipmentId;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.Room.RoomStatus;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.entity.EquipmentType;
import com.amdocs.schedulease.repository.BookingRepository;
import com.amdocs.schedulease.repository.RoomRepository;
import com.amdocs.schedulease.repository.EquipmentStockRepository;
import com.amdocs.schedulease.repository.EquipmentTypeRepository;
import com.amdocs.schedulease.exception.BookingConflictException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final EquipmentStockRepository equipmentStockRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    
    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                             RoomRepository roomRepository,
                             EquipmentStockRepository equipmentStockRepository,
                             EquipmentTypeRepository equipmentTypeRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.equipmentStockRepository = equipmentStockRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        List<Long> roomIds = booking.getRooms().stream()
                                    .map(Room::getId)
                                    .collect(Collectors.toList());
        
        if (hasConflict(roomIds, booking.getStartDatetime(), booking.getEndDatetime())) {
            throw new BookingConflictException("Booking times conflict with existing bookings.");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        return booking.orElse(null);
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId, String cancelReason) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) return null;
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only CONFIRMED bookings can be cancelled. Current status: " + booking.getStatus());
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelReason(cancelReason);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public boolean hasConflict(List<Long> roomIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Booking> bookings = bookingRepository.findOverlappingBookings(roomIds, startDateTime, endDateTime);
        return !bookings.isEmpty();
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // NEW: Get all available rooms
    @Override
    public List<Room> getAllAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    // NEW: Get all available equipment
    @Override
    public List<EquipmentStock> getAllAvailableEquipment() {
        return equipmentStockRepository.findAllAvailableEquipment();
    }

        
    

    // NEW: Get unique room floors for filter dropdown
    @Override
    public List<String> getUniqueRoomFloors() {
        return roomRepository.findDistinctAvailableFloors();
    }
    
    @Override
    public List<Room> getRoomsByIds(List<Long> roomIds){
    	return roomRepository.findAllById(roomIds);    }
    
    @Override
    public List<BookingEquipment> getEquipmentFromParams(Map<String, String> allParams) {
        List<BookingEquipment> equipmentList = new ArrayList<BookingEquipment>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("equipment_")) {
                try {
                    Long equipmentTypeId = Long.parseLong(key.substring("equipment_".length()));
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        BookingEquipment stock = new BookingEquipment();
                        // Set the equipment type (fetch from DB/service if needed)
                        EquipmentType type = equipmentTypeRepository.findById(equipmentTypeId).orElse(null);
                        if (type != null) {
                            stock.setEquipmentType(type);
                            stock.setQuantity(quantity);
                            equipmentList.add(stock);
                        }
                    }
                } catch (NumberFormatException ex) {
                    // Ignore invalid input
                }
            }
        }
        return equipmentList;
    }
    
    @Override
    @Transactional
    public Booking createBookingWithAllocations(
        UserAccount userAccount,
        Set<Room> selectedRooms,
        Map<Long, Integer> equipmentQuantities, // equipmentTypeId -> quantity
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String purposeNotes
    ) {
        int totalCapacityRequested = selectedRooms.stream().mapToInt(Room::getOccupancy).sum();

        Booking booking = new Booking();
        booking.setUser(userAccount);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setStartDatetime(startDateTime);
        booking.setEndDatetime(endDateTime);
        booking.setTotalCapacityRequested(totalCapacityRequested);
        booking.setBookingReason(purposeNotes);
        booking.setRooms(selectedRooms);

        // Save booking first to get ID
        booking = bookingRepository.save(booking);

        Set<BookingEquipment> allocations = new HashSet<>();
        for (Map.Entry<Long, Integer> entry : equipmentQuantities.entrySet()) {
            EquipmentType equipmentType = equipmentTypeRepository.findById(entry.getKey()).orElseThrow();
            BookingEquipment allocation = new BookingEquipment();
            allocation.setBooking(booking);
            allocation.setEquipmentType(equipmentType);
            allocation.setQuantity(entry.getValue());
            allocation.setId(new BookingEquipmentId(booking.getId(), equipmentType.getId()));
            allocations.add(allocation);
        }
        booking.setEquipmentAllocations(allocations);

        // Save booking again to persist allocations
        booking = bookingRepository.save(booking);

        return booking;
    }
    
    
    @Override
    public Page<Booking> getPaginatedBookingsByUserId(Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("startDatetime").descending());
        return bookingRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Booking approveBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        
        if (booking == null) {
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }
        
        // Only PENDING bookings can be approved
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalStateException(
                "Only PENDING bookings can be approved. Current status: " + booking.getStatus());
        }
        
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking declineBooking(Long bookingId, String reason) {
        Booking booking = getBookingById(bookingId);
        
        if (booking == null) {
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }
        
        // Only PENDING bookings can be declined
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new IllegalStateException(
                "Only PENDING bookings can be declined. Current status: " + booking.getStatus());
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelReason(reason);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        return bookingRepository.save(booking);
    }


}
