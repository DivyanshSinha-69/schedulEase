package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.repository.BookingRepository;
import com.amdocs.schedulease.repository.EquipmentTypeRepository;
import com.amdocs.schedulease.repository.RoomRepository;
import com.amdocs.schedulease.repository.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private EquipmentTypeRepository equipmentTypeRepository;

	@Override
	public long getTotalUserCount() {
		return userAccountRepository.count();
	}

	@Override
	public long getActiveUserCount() {
		return userAccountRepository.findAll().stream().filter(user -> user.getStatus() == UserAccount.Status.ACTIVE)
				.count();
	}

	@Override
	public long getPendingUserCount() {
		return userAccountRepository.findAll().stream().filter(user -> user.getStatus() == UserAccount.Status.PENDING)
				.count();
	}

	@Override
	public long getTotalBookingCount() {
		return bookingRepository.count();
	}

	@Override
	public Map<String, Long> getBookingCountByStatus() {
		List<Booking> bookings = bookingRepository.findAll();

		Map<String, Long> statusCounts = new HashMap<>();
		statusCounts.put("PENDING",
				bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count());
		statusCounts.put("CONFIRMED",
				bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count());
		statusCounts.put("CANCELLED",
				bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.CANCELLED).count());
		
		return statusCounts;
	}

	@Override
	public long getTotalRoomCount() {
		return roomRepository.count();
	}

	@Override
	public long getAvailableRoomCount() {
		return roomRepository.findAll().stream().filter(room -> room.getStatus() == Room.RoomStatus.AVAILABLE).count();
	}

	@Override
	public long getTotalEquipmentCount() {
		return equipmentTypeRepository.count();
	}

	@Override
	public long getBookingsInDateRange(LocalDate startDate, LocalDate endDate) {
		LocalDateTime start = startDate.atStartOfDay();
		LocalDateTime end = endDate.atTime(23, 59, 59);

		return bookingRepository.findAll().stream().filter(booking -> {
			LocalDateTime bookingStart = booking.getStartDatetime();
			return !bookingStart.isBefore(start) && !bookingStart.isAfter(end);
		}).count();
	}

	@Override
	public Map<String, Long> getMostBookedRooms() {
		try {
			// Get all bookings
			List<Booking> bookings = bookingRepository.findAll();

			// Count bookings per room
			Map<String, Long> roomCounts = new HashMap<>();

			bookings.forEach(booking -> {
				// Access rooms through the Many-to-Many relationship
				booking.getRooms().forEach(room -> {
					String roomName = room.getName();
					roomCounts.put(roomName, roomCounts.getOrDefault(roomName, 0L) + 1);
				});
			});

			// Return top 5 most booked rooms, sorted by count (descending)
			return roomCounts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(5)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
							java.util.LinkedHashMap::new));

		} catch (Exception e) {
			// Return empty map if there's any issue
			return new HashMap<>();
		}
	}

	@Override
	public Map<LocalDate, Long> getBookingTrend() {
		LocalDate today = LocalDate.now();
		Map<LocalDate, Long> trend = new HashMap<>();

		// Initialize last 7 days with 0 bookings
		for (int i = 6; i >= 0; i--) {
			LocalDate date = today.minusDays(i);
			trend.put(date, 0L);
		}

		// Count bookings for each day
		List<Booking> bookings = bookingRepository.findAll();
		bookings.forEach(booking -> {
			LocalDate bookingDate = booking.getCreatedAt().toLocalDate();
			if (trend.containsKey(bookingDate)) {
				trend.put(bookingDate, trend.get(bookingDate) + 1);
			}
		});

		return trend.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(
				Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, java.util.LinkedHashMap::new));
	}
}
