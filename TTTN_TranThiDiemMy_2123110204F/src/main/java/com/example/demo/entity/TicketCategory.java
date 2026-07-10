package com.example.demo.entity;

public enum TicketCategory {
    AMUSEMENT_PARK("Khu vui chơi"),
    PARK("Công viên"),
    MUSEUM("Bảo tàng"),
    CABLE_CAR("Cáp treo");

    private final String displayName;

    TicketCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
