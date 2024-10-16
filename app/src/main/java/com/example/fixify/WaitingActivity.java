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

public class WaitingActivity extends AppCompatActivity {

    private MaterialCardView waitingCard, activeCard, completedCard;
    private TextView waitingTextView;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        waitingCard = findViewById(R.id.waiting);
        activeCard = findViewById(R.id.active);
        completedCard = findViewById(R.id.completed);
        waitingTextView = findViewById(R.id.myProfile); // Assuming this is the TextView for booking details

        // Load booking details from Firestore
        loadBookings();

        // Set click listeners for cards
        activeCard.setOnClickListener(v -> {
            Intent intent = new Intent(WaitingActivity.this, ActiveActivity.class);
            startActivity(intent);
        });

        completedCard.setOnClickListener(v -> {
            Intent intent = new Intent(WaitingActivity.this, CompleteActivity.class);
            startActivity(intent);
        });
    }

    private void loadBookings() {
        // Fetch the bookings from Firestore
        db.collection("bookings").get().addOnCompleteListener(task -> {
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

                    // Display all the bookings at once
                    waitingTextView.setText(bookingsText.toString());
                } else {
                    waitingTextView.setText("No bookings found.");
                }
            } else {
                Toast.makeText(WaitingActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(WaitingActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}
