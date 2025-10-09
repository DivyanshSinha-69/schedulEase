package com.amdocs.schedulease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_equipment")
public class BookingEquipment {

    @EmbeddedId
    private BookingEquipmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookingId")
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("equipmentTypeId")
    @JoinColumn(name = "equipment_type_id")
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer quantity;

    // Constructors
    public BookingEquipment() {}

    public BookingEquipment(Booking booking, EquipmentType equipmentType, Integer quantity) {
        this.booking = booking;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        if (booking.getId() != null && equipmentType.getId() != null) {
            this.id = new BookingEquipmentId(booking.getId(), equipmentType.getId());
        }
    }


    // Getters and Setters
    public BookingEquipmentId getId() {
        return id;
    }

    public void setId(BookingEquipmentId id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
