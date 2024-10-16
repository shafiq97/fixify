package com.example.fixify.Data;

public class Booking {
    private String serviceTitle;
    private String preferredDate;
    private String preferredTime;

    public Booking(String serviceTitle, String preferredDate, String preferredTime) {
        this.serviceTitle = serviceTitle;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
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
}
