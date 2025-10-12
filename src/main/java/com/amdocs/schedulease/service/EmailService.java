package com.amdocs.schedulease.service;

import java.util.List;

import com.amdocs.schedulease.entity.Booking;



public interface EmailService {
    void sendBookingCancellationEmail(Booking booking, String cancelReason);
    void sendBookingApprovalEmail(Booking booking);
    void sendBookingDeclinedEmail(Booking booking, String declineReason);
    void sendBookingRemindersEmail(String recipientEmail, List<Booking> bookings);
}
