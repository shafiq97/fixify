package com.example.fixify.Technician;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.BookingAdapter;
import com.example.fixify.Data.Booking;
import com.example.fixify.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TechnicianBookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_booking);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerView);
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingList, this::showConfirmationDialog); // Pass confirmation callback
        recyclerView.setAdapter(bookingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load booking details from Firestore
        loadBookings();
    }

    private void loadBookings() {
        // Fetch the bookings from Firestore
        db.collection("bookings").get().addOnCompleteListener(task -> {
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
                    Toast.makeText(TechnicianBookingActivity.this, "No bookings found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TechnicianBookingActivity.this, "Failed to load bookings.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(TechnicianBookingActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showConfirmationDialog(Booking booking) {
        String nextStatus = booking.getStatus().equals("Waiting") ? "Active" : "Complete";
        new AlertDialog.Builder(this)
                .setTitle("Confirm Status Update")
                .setMessage("Do you want to update the booking status to " + nextStatus + "?")
                .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus(booking, nextStatus))
                .setNegativeButton("No", null)
                .show();
    }

    private void updateBookingStatus(Booking booking, String newStatus) {
        db.collection("bookings").document(booking.getId())
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TechnicianBookingActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                    loadBookings(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TechnicianBookingActivity.this, "Failed to update status.", Toast.LENGTH_SHORT).show();
                });
    }
}
