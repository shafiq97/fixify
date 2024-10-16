package com.example.fixify.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fixify.R;
import com.example.fixify.TimeSlot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TechnicianDetailServiceActivity extends AppCompatActivity {

    private TextView serviceTitleTextView, servicePriceTextView, serviceDistanceTextView;
    private ViewFlipper bannerViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_service);

        // Retrieve the passed data from the Intent
        Intent intent = getIntent();
        String serviceTitle = intent.getStringExtra("serviceTitle");
        String servicePrice = intent.getStringExtra("servicePrice");
        String serviceImageUrl = intent.getStringExtra("serviceImageUrl");
        float serviceRating = intent.getFloatExtra("serviceRating", 0);
        double serviceLatitude = intent.getDoubleExtra("serviceLatitude", 0);
        double serviceLongitude = intent.getDoubleExtra("serviceLongitude", 0);
        String serviceCategory = intent.getStringExtra("serviceCategory");
        String serviceDistance = intent.getStringExtra("serviceDistance");

        // Find the views
        serviceTitleTextView = findViewById(R.id.serviceTitleTextView);
        servicePriceTextView = findViewById(R.id.servicePriceTextView);
        serviceDistanceTextView = findViewById(R.id.serviceDistanceTextView);
        bannerViewFlipper = findViewById(R.id.bannerViewFlipper);

        // Set the data to the views
        serviceTitleTextView.setText(serviceTitle);
        servicePriceTextView.setText(servicePrice);
        serviceDistanceTextView.setText(serviceDistance);

        // Load images using Glide into the ImageViews inside the ViewFlipper
        ImageView firstImageView = (ImageView) bannerViewFlipper.getChildAt(0);
        ImageView secondImageView = (ImageView) bannerViewFlipper.getChildAt(1);

        Glide.with(this).load(serviceImageUrl).into(firstImageView);
        Glide.with(this).load(serviceImageUrl).into(secondImageView);

        // Handle the Next button click
        findViewById(R.id.nextButton).setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                // User is not logged in, show toast
                Toast.makeText(TechnicianDetailServiceActivity.this, "Need to login before booking service", Toast.LENGTH_SHORT).show();
            } else {
                // User is logged in, proceed to the next activity
                Intent nextIntent = new Intent(TechnicianDetailServiceActivity.this, TimeSlot.class);

                // Pass the service details to the TimeSlot activity
                nextIntent.putExtra("serviceTitle", serviceTitle);
                nextIntent.putExtra("servicePrice", servicePrice);
                nextIntent.putExtra("serviceImageUrl", serviceImageUrl);
                nextIntent.putExtra("serviceRating", serviceRating);
                nextIntent.putExtra("serviceLatitude", serviceLatitude);
                nextIntent.putExtra("serviceLongitude", serviceLongitude);
                nextIntent.putExtra("serviceCategory", serviceCategory);

                startActivity(nextIntent);
            }
        });
    }
}
