package com.amdocs.schedulease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "equipment_type")
public class EquipmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private EquipmentName name;

    @Column(length = 255)
    private String description;

    public enum EquipmentName {
        LAPTOP, PROJECTOR, CHAIR
    }

    // Constructors
    public EquipmentType() {}

    public EquipmentType(EquipmentName name) {
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EquipmentName getName() {
        return name;
    }

    public void setName(EquipmentName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
