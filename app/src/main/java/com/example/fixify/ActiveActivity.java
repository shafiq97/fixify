package com.example.fixify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.Booking;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActiveActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, null); // Since we are not updating status here, we can pass null for the listener
        recyclerView.setAdapter(bookingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load active bookings from Firestore
        loadActiveBookings();
    }

    private void loadActiveBookings() {
        // Fetch the bookings with active status from Firestore
        db.collection("bookings")
                .whereEqualTo("status", "Active")  // Filter only active bookings
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            bookingList.clear();  // Clear the list to avoid duplication
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String bookingId = document.getId();
                                String serviceTitle = document.getString("serviceTitle");
                                String preferredDate = document.getString("preferredDate");
                                String preferredTime = document.getString("preferredTime");
                                String status = document.getString("status");

                                // Create Booking object
                                Booking booking = new Booking(bookingId, serviceTitle, preferredDate, preferredTime, status);
                                bookingList.add(booking);  // Add booking to the list
                            }

                            // Notify adapter about data changes
                            bookingAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ActiveActivity.this, "No active bookings found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActiveActivity.this, "Failed to load active bookings.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ActiveActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
