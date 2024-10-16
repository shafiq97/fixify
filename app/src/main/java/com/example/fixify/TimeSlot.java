package com.example.fixify;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TimeSlot extends AppCompatActivity {

    EditText preferredDate, preferredTime, alternativeDate, alternativeTime;
    ImageButton calendarButton1, timeButton1, calendarButton2, timeButton2;
    ImageView backArrow;
    Button btnNext;

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserLoggedIn()) {
            Intent loginIntent = new Intent(TimeSlot.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        } else {
            setContentView(R.layout.activity_time_slot);

            db = FirebaseFirestore.getInstance();  // Initialize Firestore

            // Retrieve the service details passed from DetailServiceActivity
            Intent intent = getIntent();
            String serviceTitle = intent.getStringExtra("serviceTitle");
            String servicePrice = intent.getStringExtra("servicePrice");
            String serviceImageUrl = intent.getStringExtra("serviceImageUrl");
            float serviceRating = intent.getFloatExtra("serviceRating", 0);
            double serviceLatitude = intent.getDoubleExtra("serviceLatitude", 0);
            double serviceLongitude = intent.getDoubleExtra("serviceLongitude", 0);
            String serviceCategory = intent.getStringExtra("serviceCategory");

            preferredDate = findViewById(R.id.preferredDate);
            preferredTime = findViewById(R.id.preferredTime);
            alternativeDate = findViewById(R.id.alternativeDate);
            alternativeTime = findViewById(R.id.alternativeTime);
            calendarButton1 = findViewById(R.id.calendarButton1);
            timeButton1 = findViewById(R.id.timeButton1);
            calendarButton2 = findViewById(R.id.calendarButton2);
            timeButton2 = findViewById(R.id.timeButton2);
            btnNext = findViewById(R.id.nextButton);
            backArrow = findViewById(R.id.backArrow);

            // Preferred Date Picker
            calendarButton1.setOnClickListener(v -> showDatePickerDialog(preferredDate));

            // Preferred Time Picker
            timeButton1.setOnClickListener(v -> showTimePickerDialog(preferredTime));

            // Alternative Date Picker
            calendarButton2.setOnClickListener(v -> showDatePickerDialog(alternativeDate));

            // Alternative Time Picker
            timeButton2.setOnClickListener(v -> showTimePickerDialog(alternativeTime));

            // Next Button Click Listener
            btnNext.setOnClickListener(v -> {
                if (validateForm()) {
                    saveBookingDetails(serviceTitle, servicePrice, serviceImageUrl, serviceCategory, serviceRating, serviceLatitude, serviceLongitude);
                } else {
                    Toast.makeText(this, "Sila isi semua medan", Toast.LENGTH_SHORT).show();
                }
            });

            backArrow.setOnClickListener(v -> onBackPressed());
        }
    }

    private void saveBookingDetails(String serviceTitle, String servicePrice, String serviceImageUrl, String serviceCategory, float serviceRating, double serviceLatitude, double serviceLongitude) {
        // Get the current logged-in user's email
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String userEmail = (account != null) ? account.getEmail() : "anonymous";

        // Collect booking details
        String preferredDateString = preferredDate.getText().toString().trim();
        String preferredTimeString = preferredTime.getText().toString().trim();
        String alternativeDateString = alternativeDate.getText().toString().trim();
        String alternativeTimeString = alternativeTime.getText().toString().trim();

        // Create a booking object to save to Firestore
        Map<String, Object> booking = new HashMap<>();
        booking.put("userEmail", userEmail);
        booking.put("serviceTitle", serviceTitle);
        booking.put("servicePrice", servicePrice);
        booking.put("serviceImageUrl", serviceImageUrl);
        booking.put("serviceCategory", serviceCategory);
        booking.put("serviceRating", serviceRating);
        booking.put("serviceLatitude", serviceLatitude);
        booking.put("serviceLongitude", serviceLongitude);
        booking.put("preferredDate", preferredDateString);
        booking.put("preferredTime", preferredTimeString);
        booking.put("alternativeDate", alternativeDateString);
        booking.put("alternativeTime", alternativeTimeString);
        booking.put("status", "Waiting");


        // Save to Firestore
        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TimeSlot.this, "Booking saved successfully", Toast.LENGTH_SHORT).show();
                    Intent nextIntent = new Intent(TimeSlot.this, WaitingActivity.class);  // Proceed to the next activity
                    startActivity(nextIntent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TimeSlot.this, "Failed to save booking", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private boolean validateForm() {
        return !(preferredDate.getText().toString().trim().isEmpty() || preferredTime.getText().toString().trim().isEmpty() || alternativeDate.getText().toString().trim().isEmpty() || alternativeTime.getText().toString().trim().isEmpty());
    }

    private void showDatePickerDialog(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dateField.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText timeField) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1);
            timeField.setText(selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }
}
