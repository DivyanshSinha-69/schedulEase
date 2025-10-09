package com.amdocs.schedulease.service;

import com.amdocs.schedulease.entity.Booking;

public interface EmailService {
    void sendBookingCancellationEmail(Booking booking, String cancelReason);
    void sendBookingApprovalEmail(Booking booking);
    void sendBookingDeclinedEmail(Booking booking, String declineReason);
}
