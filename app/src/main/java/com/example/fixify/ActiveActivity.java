package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ActiveActivity extends AppCompatActivity {

    private MaterialCardView waitingCard, activeCard, completedCard;
    private TextView activeTextView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        waitingCard = findViewById(R.id.waiting);
        activeCard = findViewById(R.id.active);
        completedCard = findViewById(R.id.completed);
        activeTextView = findViewById(R.id.myProfile); // Assuming this is the TextView for active booking details

        // Load active booking details from Firestore
        loadActiveBookings();

        // Set click listeners for cards
        waitingCard.setOnClickListener(v -> {
            Intent intent = new Intent(ActiveActivity.this, WaitingActivity.class);
            startActivity(intent);
        });

        completedCard.setOnClickListener(v -> {
            Intent intent = new Intent(ActiveActivity.this, CompleteActivity.class);
            startActivity(intent);
        });
    }

    private void loadActiveBookings() {
        // Fetch the bookings with active status from Firestore
        db.collection("bookings").whereEqualTo("status", "Active")  // Fetch only bookings with "active" status
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            StringBuilder bookingsText = new StringBuilder();  // Use a StringBuilder to append text
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String serviceTitle = document.getString("serviceTitle");
                                String preferredDate = document.getString("preferredDate");
                                String preferredTime = document.getString("preferredTime");

                                // Append each booking's information
                                bookingsText.append("Service: ").append(serviceTitle).append("\n").append("Preferred Date: ").append(preferredDate).append("\n").append("Preferred Time: ").append(preferredTime).append("\n\n");
                            }

                            // Display all the active bookings at once
                            activeTextView.setText(bookingsText.toString());
                        } else {
                            activeTextView.setText("No active bookings found.");
                        }
                    } else {
                        Toast.makeText(ActiveActivity.this, "Failed to load active bookings", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(ActiveActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
