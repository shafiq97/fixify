package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class CompleteActivity extends AppCompatActivity {

    private MaterialCardView waitingCard, activeCard, completedCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete); // Replace with your actual XML layout file name

        // Initialize UI components
        waitingCard = findViewById(R.id.waiting);
        activeCard = findViewById(R.id.active);
        completedCard = findViewById(R.id.completed);


        // Set click listeners for cards
        activeCard.setOnClickListener(v -> {
            Intent intent = new Intent(CompleteActivity.this, ActiveActivity.class);
            startActivity(intent);
        });

        waitingCard.setOnClickListener(v -> {
            Intent intent = new Intent(CompleteActivity.this, WaitingActivity.class);
            startActivity(intent);
        });
    }
}
