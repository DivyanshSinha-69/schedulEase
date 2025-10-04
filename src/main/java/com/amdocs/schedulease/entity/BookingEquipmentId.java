package com.amdocs.schedulease.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BookingEquipmentId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bookingId;
    private Long equipmentTypeId;

    // Constructors
    public BookingEquipmentId() {}

    public BookingEquipmentId(Long bookingId, Long equipmentTypeId) {
        this.bookingId = bookingId;
        this.equipmentTypeId = equipmentTypeId;
    }

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getEquipmentTypeId() {
        return equipmentTypeId;
    }

    public void setEquipmentTypeId(Long equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingEquipmentId that = (BookingEquipmentId) o;
        return Objects.equals(bookingId, that.bookingId) &&
               Objects.equals(equipmentTypeId, that.equipmentTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, equipmentTypeId);
    }
}
