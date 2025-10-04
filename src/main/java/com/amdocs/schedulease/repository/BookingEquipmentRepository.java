package com.amdocs.schedulease.repository;

import com.amdocs.schedulease.entity.BookingEquipment;
import com.amdocs.schedulease.entity.BookingEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingEquipmentRepository extends JpaRepository<BookingEquipment, BookingEquipmentId> {
}
