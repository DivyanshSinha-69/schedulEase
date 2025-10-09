package com.amdocs.schedulease.entity;

import java.io.Serializable;
import java.util.Objects;

public class BookingRoomId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long bookingId;
    private Long roomId;

    public BookingRoomId() {
    }

    public BookingRoomId(Long bookingId, Long roomId) {
        this.bookingId = bookingId;
        this.roomId = roomId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRoomId that = (BookingRoomId) o;
        return Objects.equals(bookingId, that.bookingId) && 
               Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, roomId);
    }
}
