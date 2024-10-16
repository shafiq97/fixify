package com.example.fixify;

public class ServiceItem {
    private String name;
    private int imageResId;

    public ServiceItem(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}

