package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class ActiveActivity extends AppCompatActivity {

    private MaterialCardView waitingCard, activeCard, completedCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active); // Replace with your actual XML layout file name

        // Initialize UI components
        waitingCard = findViewById(R.id.waiting);
        activeCard = findViewById(R.id.active);
        completedCard = findViewById(R.id.completed);


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
}
