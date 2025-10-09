package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.entity.UserAccount;
import com.amdocs.schedulease.service.RoomService;

import jakarta.servlet.http.HttpSession;

import com.amdocs.schedulease.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping("/dashboard")
    public String staffDashboard(HttpSession session, Model model) {
    	// Check if user is logged in
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        // Check if user is STAFF
        String role = (String) session.getAttribute("userRole");
        if (role == null || !role.equals("STAFF")) {
            return "redirect:/auth/login";
        }
        
        // Your existing dashboard code...
        model.addAttribute("totalRooms", roomService.getAllRooms().size());
        // Get statistics
        List<Room> allRooms = roomService.getAllRooms();
        long totalRooms = allRooms.size();
        long availableRooms = allRooms.stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE)
                .count();
        long unavailableRooms = allRooms.stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.UNAVAILABLE)
                .count();
        long maintenanceRooms = allRooms.stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.MAINTENANCE)
                .count();
        
        long totalEquipmentTypes = equipmentService.getAllEquipmentTypes().size();
        
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("unavailableRooms", unavailableRooms);
        model.addAttribute("maintenanceRooms", maintenanceRooms);
        model.addAttribute("totalEquipmentTypes", totalEquipmentTypes);
        model.addAttribute("pageTitle", "Staff Dashboard");
        model.addAttribute("userRole", "STAFF");
        
        return "staff/dashboard";
    }
    

}
