package com.amdocs.schedulease.controller;

import com.amdocs.schedulease.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerTestController {
    
    @Autowired
    private BookingService bookingService;
    
    @GetMapping("/test-scheduler")
    public String testScheduler() {
        ((com.amdocs.schedulease.service.BookingServiceImpl) bookingService)
            .sendDailyBookingReminders();
        return "Scheduler triggered manually! Check console logs.";
    }
}
