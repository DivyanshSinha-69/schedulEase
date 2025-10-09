package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.entity.EquipmentType;
import com.amdocs.schedulease.exception.EquipmentNotFoundException;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.service.EquipmentService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    // View all equipment stock
    @GetMapping
    public String listEquipment(HttpSession session,Model model) {
    	if (session.getAttribute("user") == null) {
            return "redirect:/auth/login";
        }

    	
        List<EquipmentStock> equipmentStocks = equipmentService.getAllEquipmentStock();
        List<EquipmentType> equipmentTypes = equipmentService.getAllEquipmentTypes();
        
        model.addAttribute("equipmentStocks", equipmentStocks);
        model.addAttribute("equipmentTypes", equipmentTypes);
        model.addAttribute("pageTitle", "Equipment Management");
        model.addAttribute("userRole", "STAFF");
        
        return "staff/equipment-list";
    }

    // Show add equipment stock form
    @GetMapping("/add")
    public String showAddEquipmentForm(HttpSession session,Model model) {
    	if (session.getAttribute("user") == null) {
            return "redirect:/auth/login";
        }
        List<EquipmentType> equipmentTypes = equipmentService.getAllEquipmentTypes();
        model.addAttribute("equipmentTypes", equipmentTypes);
        model.addAttribute("equipmentStock", new EquipmentStock());
        model.addAttribute("pageTitle", "Add Equipment Stock");
        model.addAttribute("userRole", "STAFF");
        return "staff/equipment-add";
    }

    // Create equipment stock
    @PostMapping("/add")
    public String createEquipmentStock(HttpSession session,
            @RequestParam Long equipmentTypeId,
            @RequestParam Integer totalQuantity,
            RedirectAttributes redirectAttributes) {
    	if (session.getAttribute("user") == null) {
            return "redirect:/auth/login";
        }
        try {
            EquipmentType equipmentType = equipmentService.getEquipmentTypeById(equipmentTypeId);
            
            // Check if stock already exists for this equipment type
            try {
                EquipmentStock existingStock = equipmentService.getEquipmentStockByTypeId(equipmentTypeId);
                
                // If we reach here, stock already exists
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Stock for '" + equipmentType.getName() + "' already exists with " +
                    existingStock.getTotalQuantity() + " units. " +
                    "Please use 'Edit' button to update the quantity instead.");
                return "redirect:/staff/equipment";
                
            } catch (EquipmentNotFoundException e) {
                // Stock doesn't exist, proceed to create it
            }
            
            // Create new stock entry
            EquipmentStock stock = new EquipmentStock();
            stock.setEquipmentType(equipmentType);
            stock.setTotalQuantity(totalQuantity);
            
            equipmentService.createEquipmentStock(stock);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Equipment stock for '" + equipmentType.getName() + "' added successfully with " +
                totalQuantity + " units!");
            
            return "redirect:/staff/equipment";
            
        } catch (EquipmentNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Equipment type not found: " + e.getMessage());
            return "redirect:/staff/equipment/add";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to add equipment stock. " + e.getMessage());
            return "redirect:/staff/equipment/add";
        }
    }


    // Show edit equipment stock form
    @GetMapping("/edit/{id}")
    public String showEditEquipmentForm(HttpSession session,@PathVariable Long id, Model model) {
    	if (session.getAttribute("user") == null) {
            return "redirect:/auth/login";
        }
        EquipmentStock stock = equipmentService.getEquipmentStockById(id);
        model.addAttribute("equipmentStock", stock);
        model.addAttribute("pageTitle", "Edit Equipment Stock");
        model.addAttribute("userRole", "STAFF");
        return "staff/equipment-edit";
    }

    // Update equipment stock
    @PostMapping("/edit/{id}")
    public String updateEquipmentStock(HttpSession session,
            @PathVariable Long id,
            @RequestParam Integer totalQuantity,
            RedirectAttributes redirectAttributes) {
    	if (session.getAttribute("user") == null) {
            return "redirect:/auth/login";
        }
        
        try {
            EquipmentStock updatedStock = equipmentService.updateStockQuantity(id, totalQuantity);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Equipment stock updated successfully!");
            
            return "redirect:/staff/equipment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update equipment stock: " + e.getMessage());
            return "redirect:/staff/equipment/edit/" + id;
        }
    }

    // Check equipment availability (AJAX)
    @GetMapping("/check-availability/{equipmentTypeId}")
    @ResponseBody
    public String checkAvailability(HttpSession session,
            @PathVariable Long equipmentTypeId,
            @RequestParam Integer quantity) {
    	if (session.getAttribute("user") == null) {
            return "redirect:/auth/login";
        }
        
        try {
            boolean isAvailable = equipmentService.isEquipmentAvailable(equipmentTypeId, quantity);
            int availableQty = equipmentService.getAvailableQuantity(equipmentTypeId);
            
            if (isAvailable) {
                return "available:" + availableQty;
            } else {
                return "unavailable:" + availableQty;
            }
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
}
