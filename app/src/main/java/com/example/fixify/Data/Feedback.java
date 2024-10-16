package com.example.fixify.Data;

public class Feedback {
    private String username;
    private String comment;
    private float rating; // Optional: If users can rate within their feedback
    private String date; // Optional: Date of the feedback

    public Feedback(String username, String comment, float rating, String date) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
        this.date = date;
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }
}
