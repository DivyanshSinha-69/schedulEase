package com.amdocs.schedulease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "booking_room")
@IdClass(BookingRoomId.class)
public class BookingRoom {

    @Id
    @Column(name = "booking_id")
    private Long bookingId;

    @Id
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;

    // Constructors
    public BookingRoom() {
    }

    public BookingRoom(Long bookingId, Long roomId) {
        this.bookingId = bookingId;
        this.roomId = roomId;
    }

    // Getters and Setters
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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
