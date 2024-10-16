package com.example.fixify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class MyProfileActivity extends AppCompatActivity {

    private TextView username;
    private ImageView profileImageView; // Declare ImageView for profile picture
    private MaterialCardView waitingCard, inProgressCard, completedCard;
    private LinearLayout logoutButton, myprofileButton, addressButton, myOrderButton, settingButton, deleteAccButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialize UI components
        username = findViewById(R.id.username);
        profileImageView = findViewById(R.id.profileImageView); // Initialize the ImageView
        waitingCard = findViewById(R.id.waiting);
        inProgressCard = findViewById(R.id.inprogress);
        completedCard = findViewById(R.id.completed);
        myprofileButton = findViewById(R.id.myProfile);
        addressButton = findViewById(R.id.address);
        myOrderButton = findViewById(R.id.myOrder); // Initialize My Order TextView
        settingButton = findViewById(R.id.settings);
        deleteAccButton = findViewById(R.id.deleteAccount);
        logoutButton = findViewById(R.id.logout_layout);

        // Load the user's profile data from SharedPreferences
        loadUserProfile();
        loadProfilePicture(); // Load the profile picture

        // Check if both first name and picture are loaded
        checkUserProfileAndProceed();

        // Set click listeners for buttons
        myprofileButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        addressButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        myOrderButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, WaitingActivity.class);
            startActivity(intent);
        });

        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyProfileActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        deleteAccButton.setOnClickListener(v -> showDeleteAccountConfirmation());

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", "");

        if (!firstName.isEmpty()) {
            username.setText(firstName);
        } else {
            username.setText("User"); // Default text
        }
    }

    private void loadProfilePicture() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String savedPictureUri = sharedPreferences.getString("profilePictureUri", null); // Default to null

        if (savedPictureUri != null) {
            Uri uri = Uri.parse(savedPictureUri);
            profileImageView.setImageURI(uri); // Set the image from URI
        } else {
            profileImageView.setImageResource(R.drawable.profile); // Default picture
        }
    }

    private void checkUserProfileAndProceed() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        String firstName = sharedPreferences.getString("firstName", "");

        String savedPictureUri = sharedPreferences.getString("profilePictureUri", null);

        // Check if both first name and picture URI are available
        if (!firstName.isEmpty() && savedPictureUri != null) {
            showProceedConfirmation();
        }
    }

    private void showProceedConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Proceed")
                .setMessage("Fill the address details?")
                .setPositiveButton("Proceed", (dialog, which) -> {
                    Intent intent = new Intent(MyProfileActivity.this, EditAddressActivity.class); // Change this to your home page activity
                    startActivity(intent);
                    finish(); // Close this activity
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(MyProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    // Handle logout logic here (e.g., clear user data)
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clears all user data
        editor.apply(); // Apply the changes

        Toast.makeText(MyProfileActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MyProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
}