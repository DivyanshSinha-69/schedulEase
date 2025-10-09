package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.Room;
import com.amdocs.schedulease.exception.ResourceUnavailableException;
import com.amdocs.schedulease.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // View all rooms
    @GetMapping
    public String listRooms(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String floor,
            @RequestParam(required = false) String search,
            Model model) {
        
        List<Room> rooms;
        
        if (search != null && !search.trim().isEmpty()) {
            rooms = roomService.searchRooms(search);
            model.addAttribute("searchKeyword", search);
        } else if (status != null && !status.isEmpty()) {
            rooms = roomService.getRoomsByStatus(Room.RoomStatus.valueOf(status));
            model.addAttribute("filterStatus", status);
        } else if (floor != null && !floor.isEmpty()) {
            rooms = roomService.getRoomsByFloor(floor);
            model.addAttribute("filterFloor", floor);
        } else {
            rooms = roomService.getAllRooms();
        }
        
        model.addAttribute("rooms", rooms);
        model.addAttribute("pageTitle", "Room Management");
        model.addAttribute("userRole", "STAFF"); // TODO: Get from session
        
        return "staff/resource-list";
    }

    // Show add room form
    @GetMapping("/add")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("pageTitle", "Add New Room");
        model.addAttribute("userRole", "STAFF");
        return "staff/resource-add";
    }

    // Create new room
    @PostMapping("/add")
    public String createRoom(
            @ModelAttribute Room room,
            RedirectAttributes redirectAttributes) {
        
        try {
            // TODO: Get current staff user ID from session
            Long currentUserId = 2L; // Hardcoded for now (rajesh.kumar@amdocs.com)
            
            Room savedRoom = roomService.createRoom(room, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Room '" + savedRoom.getName() + "' created successfully!");
            
            return "redirect:/staff/rooms";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to create room: " + e.getMessage());
            return "redirect:/staff/rooms/add";
        }
    }

    // Show edit room form
    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable Long id, Model model) {
        Room room = roomService.getRoomById(id);
        model.addAttribute("room", room);
        model.addAttribute("pageTitle", "Edit Room");
        model.addAttribute("userRole", "STAFF");
        return "staff/resource-edit";
    }

    // Update room
    @PostMapping("/edit/{id}")
    public String updateRoom(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String floor,
            @RequestParam Integer occupancy,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        
        try {
            Room existingRoom = roomService.getRoomById(id);
            existingRoom.setName(name);
            existingRoom.setFloor(floor);
            existingRoom.setOccupancy(occupancy);
            existingRoom.setStatus(Room.RoomStatus.valueOf(status));
            existingRoom.setUpdatedAt(java.time.LocalDateTime.now());
            
            Room updatedRoom = roomService.updateRoom(id, existingRoom);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Room '" + updatedRoom.getName() + "' updated successfully!");
            
            return "redirect:/staff/rooms";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update room: " + e.getMessage());
            return "redirect:/staff/rooms/edit/" + id;
        }
    }

    // Update room status (AJAX endpoint)
    @PostMapping("/status/{id}")
    @ResponseBody
    public String updateRoomStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        try {
            Room.RoomStatus newStatus = Room.RoomStatus.valueOf(status);
            roomService.updateRoomStatus(id, newStatus);
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    // Delete room
    @GetMapping("/delete/{id}")
    public String deleteRoom(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            Room room = roomService.getRoomById(id);
            String roomName = room.getName();
            
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Room '" + roomName + "' deleted successfully!");
            
        } catch (ResourceUnavailableException e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to delete room: " + e.getMessage());
        }
        
        return "redirect:/staff/rooms";
    }




    // View room details
    @GetMapping("/view/{id}")
    public String viewRoom(@PathVariable Long id, Model model) {
        Room room = roomService.getRoomById(id);
        model.addAttribute("room", room);
        model.addAttribute("pageTitle", "Room Details");
        model.addAttribute("userRole", "STAFF");
        return "staff/room-details";
    }
}
