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
import com.amdocs.schedulease.repository.StaffProfileRepository;
import com.amdocs.schedulease.repository.EquipmentStockRepository;
import com.amdocs.schedulease.repository.EquipmentTypeRepository;
import com.amdocs.schedulease.exception.BookingConflictException;
import com.amdocs.schedulease.util.TimeSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    // Logger for scheduling
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final EquipmentStockRepository equipmentStockRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;

    // NEW: Properties for reminders scheduler
    @Value("${staff.reminders.enabled:false}")
    private boolean remindersEnabled;

    @Value("${staff.reminders.email-enabled:false}")
    private boolean emailEnabled;

    @Value("${staff.reminders.hours-ahead:24}")
    private int hoursAhead;

    @Value("${staff.reminders.recipient-email:staff@schedulease.com}")
    private String staffEmail;

    // NEW: EmailService for sending reminders
    @Autowired
    private EmailService emailService;
    @Autowired
    private StaffProfileRepository staffProfileRepository;


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

        // Check if booking is already cancelled
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Users can cancel PENDING or CONFIRMED bookings
        if (booking.getStatus() != Booking.BookingStatus.PENDING &&
                booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Cannot cancel booking with status: " + booking.getStatus());
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

    @Override
    public List<Room> getAllAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE);
    }

    @Override
    public List<EquipmentStock> getAllAvailableEquipment() {
        return equipmentStockRepository.findAllAvailableEquipment();
    }

    @Override
    public List<String> getUniqueRoomFloors() {
        return roomRepository.findDistinctAvailableFloors();
    }

    @Override
    public List<Room> getRoomsByIds(List<Long> roomIds) {
        return roomRepository.findAllById(roomIds);
    }

    @Override
    public List<BookingEquipment> getEquipmentFromParams(Map<String, String> allParams) {
        List<BookingEquipment> equipmentList = new ArrayList<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("equipment_")) {
                try {
                    Long equipmentTypeId = Long.parseLong(key.substring("equipment_".length()));
                    int quantity = Integer.parseInt(entry.getValue());
                    if (quantity > 0) {
                        BookingEquipment stock = new BookingEquipment();
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
            Map<Long, Integer> equipmentQuantities,
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
        booking = bookingRepository.save(booking);

        return booking;
    }

    @Override
    public Page<Booking> getPaginatedBookingsByUserId(Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("startDatetime").descending());
        return bookingRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Room> getAvailableRoomsFiltered(
            String date, String startTime, String endTime,
            String floor, String occupancy, String sort) {

        List<Room> rooms = roomRepository.findByStatus(Room.RoomStatus.AVAILABLE);

        if (floor != null && !floor.isBlank()) {
            rooms = rooms.stream()
                    .filter(r -> r.getFloor().equalsIgnoreCase(floor))
                    .collect(Collectors.toList());
        }

        if (occupancy != null && !occupancy.isBlank()) {
            int minCap = 0, maxCap = Integer.MAX_VALUE;
            if (occupancy.equals("31+")) {
                minCap = 31;
            } else if (occupancy.contains("-")) {
                String[] parts = occupancy.split("-");
                minCap = Integer.parseInt(parts[0]);
                maxCap = Integer.parseInt(parts[1]);
            }

            final int min = minCap, max = maxCap;
            rooms = rooms.stream()
                    .filter(r -> r.getOccupancy() >= min && r.getOccupancy() <= max)
                    .collect(Collectors.toList());
        }

        if (date != null && !date.isBlank() && startTime != null && !startTime.isBlank() && endTime != null && !endTime.isBlank()) {
            LocalDate bookingDate = LocalDate.parse(date);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            LocalDateTime startDateTime = LocalDateTime.of(bookingDate, start);
            LocalDateTime endDateTime = LocalDateTime.of(bookingDate, end);

            List<Long> roomIds = rooms.stream().map(Room::getId).toList();
            List<Booking> conflicts = bookingRepository.findOverlappingBookings(roomIds, startDateTime, endDateTime);
            Set<Long> conflictedRoomIds = conflicts.stream()
                    .flatMap(b -> b.getRooms().stream())
                    .map(Room::getId)
                    .collect(Collectors.toSet());

            rooms = rooms.stream()
                    .filter(r -> !conflictedRoomIds.contains(r.getId()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case "name":
                    rooms.sort(Comparator.comparing(Room::getName));
                    break;
                case "occupancy":
                    rooms.sort(Comparator.comparing(Room::getOccupancy).reversed());
                    break;
                case "floor":
                    rooms.sort(Comparator.comparing(Room::getFloor));
                    break;
            }
        }

        return rooms;
    }

    @Override
    public List<TimeSlot> getFreeSlotsForRoom(
            Room room, LocalDate date, LocalTime dayStartTime, LocalTime dayEndTime) {

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.of(23, 59));

        List<Booking> bookings = bookingRepository.findBookingsByRoomAndTimeRange(room.getId(), dayStart, dayEnd);
        bookings.sort(Comparator.comparing(b -> b.getStartDatetime().toLocalTime()));

        List<TimeSlot> freeSlots = new ArrayList<>();
        LocalTime lastEnd = dayStartTime;

        for (Booking b : bookings) {
            LocalTime bookingStart = b.getStartDatetime().toLocalTime();
            if (lastEnd.isBefore(bookingStart)) {
                freeSlots.add(new TimeSlot(lastEnd, bookingStart));
            }

            lastEnd = b.getEndDatetime().toLocalTime();
        }

        if (lastEnd.isBefore(dayEndTime)) {
            freeSlots.add(new TimeSlot(lastEnd, dayEndTime));
        }

        return freeSlots;
    }

    @Override
    @Transactional
    public Booking approveBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking not found with ID: " + bookingId);
        }

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

    @Override
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // NEW: Feature 1 - Get expired pending bookings
    @Override
    public List<Booking> getExpiredPendingBookings(int thresholdHours) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(thresholdHours);
        return bookingRepository.findByStatusAndCreatedAtBefore(
                Booking.BookingStatus.PENDING,
                threshold
        );
    }

    // NEW: Feature 2 - Get upcoming confirmed bookings
    @Override
    public List<Booking> getUpcomingConfirmedBookings(int hoursAhead) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusHours(hoursAhead);
        return bookingRepository.findConfirmedBookingsBetween(now, end);
    }

    // NEW: Feature 2 - Daily scheduler for booking reminders
    /**
     * Daily scheduled task that runs at 8:00 AM to send reminders
     * Cron format: "0 0 8 * * *" = Every day at 8:00 AM
     */
    @Scheduled(cron = "${staff.reminders.cron:0 0 8 * * *}")
    @ConditionalOnProperty(name = "staff.reminders.enabled", havingValue = "true")
    public void sendDailyBookingReminders() {
        logger.info("===== Starting daily booking reminder scheduler at {} =====", LocalDateTime.now());

        try {
            List<Booking> upcomingBookings = getUpcomingConfirmedBookings(hoursAhead);

            if (upcomingBookings.isEmpty()) {
                logger.info("No upcoming bookings found for the next {} hours", hoursAhead);
                return;
            }

            logger.info("Found {} confirmed booking(s) starting in the next {} hours",
                    upcomingBookings.size(), hoursAhead);

            if (emailEnabled) {
                sendReminderEmail(upcomingBookings);  // CHANGED BACK
            } else {
                logger.info("Email reminders disabled. Bookings logged only.");
                logUpcomingBookings(upcomingBookings);
            }

        } catch (Exception e) {
            logger.error("Error occurred while processing booking reminders", e);
        }

        logger.info("===== Daily booking reminder scheduler completed at {} =====", LocalDateTime.now());
    }

    private void sendReminderEmail(List<Booking> bookings) {
        try {
            emailService.sendBookingRemindersEmail(staffEmail, bookings);
            logger.info("Reminder email sent successfully to {}", staffEmail);
        } catch (Exception e) {
            logger.error("Failed to send reminder email to {}", staffEmail, e);
        }
    }

    private void logUpcomingBookings(List<Booking> bookings) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        logger.info("========== Upcoming Bookings ==========");
        for (Booking booking : bookings) {
            String roomNames = booking.getRooms().stream()
                    .map(Room::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("N/A");

            logger.info("Booking #{}: {} - Start: {} | Room(s): {} | Capacity: {}",
                    booking.getId(),
                    booking.getUser().getEmail(),
                    booking.getStartDatetime().format(formatter),
                    roomNames,
                    booking.getTotalCapacityRequested());
        }
        logger.info("=======================================");
    }// Add this method for immediate testing
    public void testSchedulerManually() {
        logger.info("===== MANUAL TEST: Calling scheduler directly =====");
        sendDailyBookingReminders();
    }

}
