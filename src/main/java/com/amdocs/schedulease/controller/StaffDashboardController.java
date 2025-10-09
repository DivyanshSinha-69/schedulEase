package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.service.RoomService;
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
    public String staffDashboard(Model model) {
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
