package com.example.fixify.Data;

public class PaintingService {
    private int id;
    private String title;
    private double latitude;
    private double longitude;
    private float rating;
    private String priceRange;
    private String imageUrl;  // Updated to store image URL instead of image resource ID
    private double distance;   // Distance from user, to be calculated
    private String category;   // Field for category

    // Empty constructor required for Firestore
    public PaintingService() {
    }

    // Updated constructor to use imageUrl instead of imageResId
    public PaintingService(int id, String title, double latitude, double longitude, float rating, String priceRange, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.priceRange = priceRange;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getRating() {
        return rating;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
