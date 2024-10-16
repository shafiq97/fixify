package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import Button
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AddressActivity extends AppCompatActivity {

    // Declare UI components
    private TextView addressLine1TextView;
    private ImageView mapImage, tambahImage, backArrow;  // Declare back arrow ImageView
    private TextView noAddressFound;
    private CardView cardView;
    private Button nextButton; // Declare Next Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address); // Link to the XML layout

        // Initialize UI components
        addressLine1TextView = findViewById(R.id.address_line1_text_view);
        mapImage = findViewById(R.id.mapImage);
        tambahImage = findViewById(R.id.tambah);  // Initialize "Tambah" ImageView
        backArrow = findViewById(R.id.backArrow);  // Initialize back arrow ImageView
        noAddressFound = findViewById(R.id.noAddressFound);
        cardView = findViewById(R.id.cardView);
        nextButton = findViewById(R.id.nextButton); // Initialize Next Button

        // Set OnClickListener for "Tambah" ImageView to navigate to EditAddressActivity
        tambahImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
                startActivity(intent);  // Start the EditAddressActivity
            }
        });

        // Set OnClickListener for back arrow to return to previous activity
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Close the current activity and go back to the previous one
            }
        });

        // Set OnClickListener for Next Button to navigate to TimeSlotActivity
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressActivity.this, TimeSlot.class); // Correct class name here
                startActivity(intent);  // Start TimeSlotActivity
            }
        });

        // Get data from the Intent
        Intent intent = getIntent();
        String addr1 = intent.getStringExtra("address_line1");
        String addrState = intent.getStringExtra("state");
        String addrPostalCode = intent.getStringExtra("postal_code");

        // Check if any address was passed
        if (addr1 != null && addrState != null && addrPostalCode != null) {
            // Combine the address information into a single string
            String fullAddress = addr1 + ", " + addrState + " " + addrPostalCode;

            // Update the address TextView with the actual address
            addressLine1TextView.setText(fullAddress);

            // Hide the "no address found" message if an address exists
            noAddressFound.setVisibility(View.GONE);
        }
    }
}