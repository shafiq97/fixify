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

public class WaitingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, null); // Passing null for listener since no status update here
        recyclerView.setAdapter(bookingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load waiting bookings from Firestore
        loadBookings();
    }

    private void loadBookings() {
        // Fetch the bookings with 'Waiting' status from Firestore
        db.collection("bookings")
                .whereEqualTo("status", "Waiting")  // Filter only bookings with status "Waiting"
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

                                // Create Booking object with updated constructor
                                Booking booking = new Booking(bookingId, serviceTitle, preferredDate, preferredTime, status);
                                bookingList.add(booking);  // Add booking to the list
                            }

                            // Notify adapter about data changes
                            bookingAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(WaitingActivity.this, "No bookings found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WaitingActivity.this, "Failed to load bookings.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(WaitingActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
