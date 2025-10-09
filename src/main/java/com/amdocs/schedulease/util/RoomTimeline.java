package com.amdocs.schedulease.util;

import java.util.List;

public class RoomTimeline {
    private Long id;
    private String name;
    private List<TimeSlot> freeSlots;

    // Constructor
    public RoomTimeline(Long id, String name, List<TimeSlot> freeSlots) {
        this.id = id;
        this.name = name;
        this.freeSlots = freeSlots;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<TimeSlot> getFreeSlots() { return freeSlots; }
    public void setFreeSlots(List<TimeSlot> freeSlots) { this.freeSlots = freeSlots; }
}
