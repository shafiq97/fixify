package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditAddressActivity extends AppCompatActivity {

    private EditText state, addressLine1, postalCode;
    private Button saveAddressButton;
    private ImageView backArrow;  // Declare the back arrow ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        // Find views by ID
        state = findViewById(R.id.state);
        addressLine1 = findViewById(R.id.address_line1);
        postalCode = findViewById(R.id.postalCode);
        saveAddressButton = findViewById(R.id.save_address_button);
        backArrow = findViewById(R.id.backArrow);  // Initialize the back arrow ImageView

        // Set click listener for save button
        saveAddressButton.setOnClickListener(v -> {
            String addr1 = addressLine1.getText().toString();
            String addrState = state.getText().toString();
            String addrPostalCode = postalCode.getText().toString();

            if (addr1.isEmpty() || addrState.isEmpty() || addrPostalCode.isEmpty()) {
                Toast.makeText(EditAddressActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            } else {
                // Pass the address data to AddressActivity
                Intent intent = new Intent(EditAddressActivity.this, AddressActivity.class);
                intent.putExtra("address_line1", addr1);
                intent.putExtra("state", addrState);
                intent.putExtra("postal_code", addrPostalCode);
                startActivity(intent);

                // Finish the current activity
                finish();
            }
        });

        // Set click listener for the back arrow to go back to the previous activity
        backArrow.setOnClickListener(v -> {
            finish();  // Close this activity and return to the previous one
        });
    }
}
