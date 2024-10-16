package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Service extends AppCompatActivity {

    private ImageView backArrow, service1, service2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        // Initialize back arrow and set click listener
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Initialize service cards
        service1 = findViewById(R.id.service1);
        service2 = findViewById(R.id.service2);

        // Set click listeners for the service cards
        service1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to a new activity for Service 1
                Intent intent = new Intent(Service.this, TimeSlot.class);
                startActivity(intent);
            }
        });

        service2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to a new activity for Service 2
                Intent intent = new Intent(Service.this, TimeSlot.class);
                startActivity(intent);
            }
        });
    }
}
