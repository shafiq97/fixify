package com.example.fixify.Data;

public class Booking {
    private String id; // Unique booking ID from Firestore
    private String serviceTitle;
    private String preferredDate;
    private String preferredTime;
    private String status;  // Add status field

    public Booking(String id, String serviceTitle, String preferredDate, String preferredTime, String status) {
        this.id = id;
        this.serviceTitle = serviceTitle;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
