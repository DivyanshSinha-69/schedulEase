package com.amdocs.schedulease;

// import com.amdocs.schedulease.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EntityMappingTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testEntityManagerNotNull() {
        assertNotNull(entityManager, "EntityManager should not be null");
        System.out.println("✅ EntityManager is available");
    }

    @Test
    public void testAllEntitiesMapped() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        
        System.out.println("\n📋 Mapped Entities:");
        entities.forEach(entity -> {
            System.out.println("  - " + entity.getName());
        });

        assertTrue(entities.size() >= 8, "Should have at least 8 entities mapped");
        
        // Verify specific entities
        assertTrue(isEntityMapped("UserAccount"), "UserAccount should be mapped");
        assertTrue(isEntityMapped("Role"), "Role should be mapped");
        assertTrue(isEntityMapped("StaffProfile"), "StaffProfile should be mapped");
        assertTrue(isEntityMapped("Room"), "Room should be mapped");
        assertTrue(isEntityMapped("EquipmentType"), "EquipmentType should be mapped");
        assertTrue(isEntityMapped("EquipmentStock"), "EquipmentStock should be mapped");
        assertTrue(isEntityMapped("Booking"), "Booking should be mapped");
        assertTrue(isEntityMapped("BookingEquipment"), "BookingEquipment should be mapped");
        
        System.out.println("✅ All entities are properly mapped!");
    }

    private boolean isEntityMapped(String entityName) {
        return entityManager.getMetamodel().getEntities().stream()
                .anyMatch(entity -> entity.getName().equals(entityName));
    }
}
