package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.EquipmentStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentStockRepository extends JpaRepository<EquipmentStock, Long> {
}
