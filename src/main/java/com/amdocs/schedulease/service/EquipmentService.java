package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.EquipmentType;
import com.amdocs.schedulease.entity.EquipmentStock;
import java.util.List;

public interface EquipmentService {
    
    // Equipment Type operations
    List<EquipmentType> getAllEquipmentTypes();
    EquipmentType getEquipmentTypeById(Long id);
    EquipmentType createEquipmentType(EquipmentType equipmentType);
    EquipmentType updateEquipmentType(Long id, EquipmentType equipmentType);
    void deleteEquipmentType(Long id);
    
    // Equipment Stock operations
    List<EquipmentStock> getAllEquipmentStock();
    EquipmentStock getEquipmentStockById(Long id);
    EquipmentStock getEquipmentStockByTypeId(Long equipmentTypeId);
    EquipmentStock createEquipmentStock(EquipmentStock equipmentStock);
    EquipmentStock updateEquipmentStock(Long id, EquipmentStock equipmentStock);
    EquipmentStock updateStockQuantity(Long id, Integer totalQuantity);
    
    // Availability check
    int getAvailableQuantity(Long equipmentTypeId);
    boolean isEquipmentAvailable(Long equipmentTypeId, Integer requestedQuantity);
}
