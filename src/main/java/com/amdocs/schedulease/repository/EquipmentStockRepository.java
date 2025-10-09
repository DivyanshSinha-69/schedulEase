package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.EquipmentStock;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentStockRepository extends JpaRepository<EquipmentStock, Long> {
	@Query("SELECT es FROM EquipmentStock es WHERE (es.totalQuantity - es.allocatedQuantity) > 0")
    List<EquipmentStock> findAllAvailableEquipment();
    
    Optional<EquipmentStock> findByEquipmentTypeId(Long equipmentTypeId);
}
