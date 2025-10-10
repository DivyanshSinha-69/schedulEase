package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Booking;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.entity.Role;
import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.StaffProfile;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.AdminService;
import com.amdocs.schedulease.service.AnalyticsService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private AnalyticsService analyticsService;

	// ========== DASHBOARD ==========

	@GetMapping("/dashboard")
	public String showDashboard(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");

		if (user == null) {
			return "redirect:/auth/login";
		}

		// Add analytics data for dashboard
		model.addAttribute("user", user);
		model.addAttribute("totalUsers", analyticsService.getTotalUserCount());
		model.addAttribute("activeUsers", analyticsService.getActiveUserCount());
		model.addAttribute("pendingUsers", analyticsService.getPendingUserCount());
		model.addAttribute("totalBookings", analyticsService.getTotalBookingCount());
		model.addAttribute("totalRooms", analyticsService.getTotalRoomCount());
		model.addAttribute("availableRooms", analyticsService.getAvailableRoomCount());

		return "admin/dashboard";
	}

	// ========== USER MANAGEMENT ==========

	@GetMapping("/user-management")
	public String showUserManagement(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");

		if (user == null) {
			return "redirect:/auth/login";
		}

		List<UserAccount> allUsers = adminService.getAllUsers();
		model.addAttribute("user", user);
		model.addAttribute("users", allUsers);

		return "admin/user-management";
	}

	// ========== USER APPROVAL ==========

	@GetMapping("/user-approval")
	public String showUserApproval(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");

		if (user == null) {
			return "redirect:/auth/login";
		}

		List<UserAccount> pendingUsers = adminService.getPendingUsers();
		model.addAttribute("user", user);
		model.addAttribute("pendingUsers", pendingUsers);

		return "admin/user-approval";
	}

	@PostMapping("/user-approval/approve/{userId}")
	public String approveUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
		try {
			adminService.approveUser(userId);
			redirectAttributes.addFlashAttribute("successMessage", "User approved successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user-approval";
	}

	@PostMapping("/user-approval/reject/{userId}")
	public String rejectUser(@PathVariable Long userId, @RequestParam(required = false) String reason,
			RedirectAttributes redirectAttributes) {
		try {
			adminService.rejectUser(userId, reason);
			redirectAttributes.addFlashAttribute("successMessage", "User registration rejected");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user-approval";
	}

	// ========== USER ACTIONS (SUSPEND/ACTIVATE) ==========

	@PostMapping("/users/suspend/{userId}")
	public String suspendUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
		try {
			adminService.suspendUser(userId);
			redirectAttributes.addFlashAttribute("successMessage", "User suspended successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user-management";
	}

	@PostMapping("/users/activate/{userId}")
	public String activateUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
		try {
			adminService.activateUser(userId);
			redirectAttributes.addFlashAttribute("successMessage", "User activated successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user-management";
	}

	// ========== STAFF MANAGEMENT ==========

	@GetMapping("/staff-management")
	public String showStaffManagement(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");

		if (user == null) {
			return "redirect:/auth/login";
		}

		List<StaffProfile> staffList = adminService.getAllStaff();
		model.addAttribute("user", user);
		model.addAttribute("staffList", staffList);

		return "admin/staff-management";
	}

	@PostMapping("/staff/create")
	public String createStaffAccount(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("confirmPassword") String confirmPassword, @RequestParam("fullName") String fullName,
			@RequestParam("phone") String phone, @RequestParam("department") String department,
			@RequestParam("dob") String dob, // NEW
			RedirectAttributes redirectAttributes) {

		// Validate passwords match
		if (!password.equals(confirmPassword)) {
			redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match");
			return "redirect:/admin/staff-management";
		}

		// Validate password strength
		if (password.length() < 8) {
			redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 8 characters");
			return "redirect:/admin/staff-management";
		}

		try {
			// Parse and validate DOB
			java.time.LocalDate parsedDob = java.time.LocalDate.parse(dob);

			// Validate DOB is not in future
			if (parsedDob.isAfter(java.time.LocalDate.now())) {
				redirectAttributes.addFlashAttribute("errorMessage", "Date of Birth cannot be in the future");
				return "redirect:/admin/staff-management";
			}

			// Call service with DOB
			adminService.createStaffAccount(email, password, fullName, phone, department, parsedDob);
			redirectAttributes.addFlashAttribute("successMessage", "Staff account created successfully");
		} catch (java.time.format.DateTimeParseException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/admin/staff-management";
	}

	// ========== ROLE MANAGEMENT ==========

	@PostMapping("/users/{userId}/assign-role")
	public String assignRole(@PathVariable Long userId, @RequestParam("roleName") String roleName,
			RedirectAttributes redirectAttributes) {
		try {
			Role.RoleName role = Role.RoleName.valueOf(roleName);
			adminService.assignRole(userId, role);
			redirectAttributes.addFlashAttribute("successMessage", "Role assigned successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user-management";
	}

	@PostMapping("/users/{userId}/remove-role")
	public String removeRole(@PathVariable Long userId, @RequestParam("roleName") String roleName,
			RedirectAttributes redirectAttributes) {
		try {
			Role.RoleName role = Role.RoleName.valueOf(roleName);
			adminService.removeRole(userId, role);
			redirectAttributes.addFlashAttribute("successMessage", "Role removed successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/user-management";
	}

	// ========== MONITOR BOOKINGS ==========

	@GetMapping("/monitor-bookings")
	public String monitorBookings(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}
		// Get all bookings, for ADMIN view
		List<Booking> bookings = analyticsService.getAllBookings(); // Add this to service!
		model.addAttribute("user", user);
		model.addAttribute("bookings", bookings);
		return "admin/monitor-bookings";
	}

	// ========== MONITOR RESOURCES ==========

	@GetMapping("/monitor-resources")
	public String monitorResources(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}
		List<EquipmentStock> resources = analyticsService.getAllEquipment(); // Add service method!
		model.addAttribute("user", user);
		model.addAttribute("resources", resources);
		return "admin/monitor-resources";
	}

	// ========== MONITOR ROOMS ==========

	@GetMapping("/monitor-rooms")
	public String monitorRooms(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}
		List<Room> rooms = analyticsService.getAllRooms(); // Add service method as below!
		model.addAttribute("user", user);
		model.addAttribute("rooms", rooms);
		return "admin/monitor-rooms";
	}

	// ========== SETTINGS ==========

	@GetMapping("/settings")
	public String showSettings(HttpSession session, Model model) {
		UserAccount user = (UserAccount) session.getAttribute("user");

		if (user == null) {
			return "redirect:/auth/login";
		}

		model.addAttribute("user", user);
		return "admin/settings";
	}

	@PostMapping("/reports/generate")
	public String generateReport(@RequestParam("reportType") String reportType,
			@RequestParam("startDate") String startDateStr, @RequestParam("endDate") String endDateStr, Model model,
			HttpSession session, RedirectAttributes redirectAttributes) {
		UserAccount user = (UserAccount) session.getAttribute("user");
		if (user == null) {
			return "redirect:/auth/login";
		}

		LocalDate startDate = LocalDate.parse(startDateStr);
		LocalDate endDate = LocalDate.parse(endDateStr);

		String reportData;

		if ("bookings".equals(reportType)) {
		    List<Booking> bookingsInRange = analyticsService.getAllBookings().stream()
		        .filter(booking -> {
		            LocalDateTime start = startDate.atStartOfDay();
		            LocalDateTime end = endDate.atTime(23,59,59);
		            return !booking.getStartDatetime().isBefore(start) && !booking.getStartDatetime().isAfter(end);
		        })
		        .collect(Collectors.toList());
		    model.addAttribute("bookingsInRange", bookingsInRange);
		    reportData = "Total bookings: " + bookingsInRange.size();
		} else if ("users".equals(reportType)) {
			long total = analyticsService.getTotalUserCount();
			long active = analyticsService.getActiveUserCount();
			reportData = "Total users: " + total + " (Active: " + active + ")";
		} else {
			reportData = "Report type not implemented yet.";
		}

		model.addAttribute("user", user);
		model.addAttribute("reportGenerated", true);
		model.addAttribute("reportType", reportType);
		model.addAttribute("startDate", startDateStr);
		model.addAttribute("endDate", endDateStr);
		model.addAttribute("reportData", reportData);

		return "admin/reports";
	}

	@GetMapping("/reports/export/excel")
	public void exportBookingsToExcel(HttpServletResponse response,
	                                  @RequestParam("startDate") String startDateStr,
	                                  @RequestParam("endDate") String endDateStr) throws IOException {
	    // Set response headers
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    String filename = "bookings_" + startDateStr + "_to_" + endDateStr + ".xlsx";
	    response.setHeader("Content-Disposition", "attachment; filename=" + filename);

	    LocalDate startDate = LocalDate.parse(startDateStr);
	    LocalDate endDate = LocalDate.parse(endDateStr);

	    List<Booking> bookingsInRange = analyticsService.getAllBookings().stream()
	        .filter(booking -> {
	            LocalDateTime start = startDate.atStartOfDay();
	            LocalDateTime end = endDate.atTime(23, 59, 59);
	            return !booking.getStartDatetime().isBefore(start) && !booking.getStartDatetime().isAfter(end);
	        })
	        .collect(Collectors.toList());

	    // Use Apache POI
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Bookings");
	    Row header = sheet.createRow(0);
	    header.createCell(0).setCellValue("ID");
	    header.createCell(1).setCellValue("User");
	    header.createCell(2).setCellValue("Status");
	    header.createCell(3).setCellValue("Start Date/Time");
	    header.createCell(4).setCellValue("End Date/Time");

	    int rowIdx = 1;
	    for (Booking booking : bookingsInRange) {
	        Row row = sheet.createRow(rowIdx++);
	        row.createCell(0).setCellValue(booking.getId());
	        row.createCell(1).setCellValue(booking.getUser() != null ? booking.getUser().getFullName() : "N/A");
	        row.createCell(2).setCellValue(String.valueOf(booking.getStatus()));
	        row.createCell(3).setCellValue(booking.getStartDatetime().toString());
	        row.createCell(4).setCellValue(booking.getEndDatetime().toString());
	    }

	    workbook.write(response.getOutputStream());
	    workbook.close();
	}

}
