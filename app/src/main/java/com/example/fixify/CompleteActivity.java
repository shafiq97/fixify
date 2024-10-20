package com.example.fixify;

import android.content.Intent;
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

public class CompleteActivity extends AppCompatActivity implements BookingAdapter.OnBookingStatusUpdateListener {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, this); // Pass 'this' as the listener
        recyclerView.setAdapter(bookingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load bookings
        loadBookings();
    }

    private void loadBookings() {
        db.collection("bookings")
                .whereEqualTo("status", "Complete")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            bookingList.clear();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String bookingId = document.getId();
                                String serviceTitle = document.getString("serviceTitle");
                                String preferredDate = document.getString("preferredDate");
                                String preferredTime = document.getString("preferredTime");
                                String status = document.getString("status");

                                Booking booking = new Booking(bookingId, serviceTitle, preferredDate, preferredTime, status);
                                bookingList.add(booking);
                            }

                            bookingAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CompleteActivity.this, "No bookings found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CompleteActivity.this, "Failed to load bookings.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CompleteActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBookingStatusUpdate(Booking booking) {
        // Navigate to FeedbackActivity and pass the booking ID
        Intent intent = new Intent(CompleteActivity.this, FeedbackActivity.class);
        intent.putExtra("bookingId", booking.getId());
        startActivity(intent);
    }
}
