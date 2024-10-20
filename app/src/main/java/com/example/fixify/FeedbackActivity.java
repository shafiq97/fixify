package com.example.fixify;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private EditText feedbackEditText;
    private Button submitFeedbackButton;
    private FirebaseFirestore db;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get booking ID from intent
        bookingId = getIntent().getStringExtra("bookingId");

        // Initialize feedback input and button
        feedbackEditText = findViewById(R.id.feedbackEditText);
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // Set up button click listener to submit feedback
        submitFeedbackButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedbackText = feedbackEditText.getText().toString().trim();

        if (TextUtils.isEmpty(feedbackText)) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a feedback map
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("bookingId", bookingId);
        feedback.put("feedback", feedbackText);
        feedback.put("timestamp", System.currentTimeMillis());

        // Add feedback to Firestore
        db.collection("feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                    feedbackEditText.setText(""); // Clear the input field
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedbackActivity.this, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
