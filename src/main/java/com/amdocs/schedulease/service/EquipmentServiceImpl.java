package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.EquipmentType;
import com.amdocs.schedulease.entity.EquipmentStock;
import com.amdocs.schedulease.exception.EquipmentNotFoundException;
import com.amdocs.schedulease.exception.ResourceUnavailableException;
import com.amdocs.schedulease.repository.EquipmentTypeRepository;
import com.amdocs.schedulease.repository.EquipmentStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    private EquipmentStockRepository equipmentStockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentType getEquipmentTypeById(Long id) {
        return equipmentTypeRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException(id));
    }

    @Override
    public EquipmentType createEquipmentType(EquipmentType equipmentType) {
        return equipmentTypeRepository.save(equipmentType);
    }

    @Override
    public EquipmentType updateEquipmentType(Long id, EquipmentType equipmentType) {
        EquipmentType existing = getEquipmentTypeById(id);
        existing.setDescription(equipmentType.getDescription());
        return equipmentTypeRepository.save(existing);
    }

    @Override
    public void deleteEquipmentType(Long id) {
        EquipmentType equipmentType = getEquipmentTypeById(id);
        equipmentTypeRepository.delete(equipmentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentStock> getAllEquipmentStock() {
        return equipmentStockRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentStock getEquipmentStockById(Long id) {
        return equipmentStockRepository.findById(id)
                .orElseThrow(() -> new EquipmentNotFoundException("Equipment stock not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentStock getEquipmentStockByTypeId(Long equipmentTypeId) {
        return equipmentStockRepository.findByEquipmentTypeId(equipmentTypeId)
                .orElseThrow(() -> new EquipmentNotFoundException("No stock found for equipment type ID: " + equipmentTypeId));
    }

    @Override
    public EquipmentStock createEquipmentStock(EquipmentStock equipmentStock) {
        equipmentStock.setCreatedAt(LocalDateTime.now());
        equipmentStock.setAllocatedQuantity(0);
        return equipmentStockRepository.save(equipmentStock);
    }

    @Override
    public EquipmentStock updateEquipmentStock(Long id, EquipmentStock equipmentStock) {
        EquipmentStock existing = getEquipmentStockById(id);
        existing.setTotalQuantity(equipmentStock.getTotalQuantity());
        existing.setUpdatedAt(LocalDateTime.now());
        return equipmentStockRepository.save(existing);
    }

    @Override
    public EquipmentStock updateStockQuantity(Long id, Integer totalQuantity) {
        EquipmentStock stock = getEquipmentStockById(id);
        
        if (totalQuantity < stock.getAllocatedQuantity()) {
            throw new ResourceUnavailableException(
                "Cannot reduce total quantity below allocated quantity. " +
                "Current allocated: " + stock.getAllocatedQuantity()
            );
        }
        
        stock.setTotalQuantity(totalQuantity);
        stock.setUpdatedAt(LocalDateTime.now());
        return equipmentStockRepository.save(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public int getAvailableQuantity(Long equipmentTypeId) {
        EquipmentStock stock = getEquipmentStockByTypeId(equipmentTypeId);
        return stock.getAvailableQuantity();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEquipmentAvailable(Long equipmentTypeId, Integer requestedQuantity) {
        int available = getAvailableQuantity(equipmentTypeId);
        return available >= requestedQuantity;
    }
}
