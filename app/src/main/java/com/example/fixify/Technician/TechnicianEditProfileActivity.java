package com.example.fixify.Technician;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fixify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TechnicianEditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profilePicture, calendarIcon;
    private TextView changePhoto;
    private EditText name, email, icNumber, phoneNumber, countryCode, dateOfBirth;
    private Button saveButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_edit_profile);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Find views by ID
        profilePicture = findViewById(R.id.profile_picture);
        changePhoto = findViewById(R.id.change_photo);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        icNumber = findViewById(R.id.ic_number);
        phoneNumber = findViewById(R.id.phone_number);
        countryCode = findViewById(R.id.country_code);
        dateOfBirth = findViewById(R.id.date_of_birth);
        calendarIcon = findViewById(R.id.calendar_icon);
        saveButton = findViewById(R.id.save_button);

        // Prefill user data if exists
        prefillUserData();

        // Handle the "Change Photo" action
        changePhoto.setOnClickListener(view -> openImageChooser());

        // Handle calendar icon click for selecting Date of Birth
        calendarIcon.setOnClickListener(v -> showDatePickerDialog());

        // Save button click listener
        saveButton.setOnClickListener(view -> saveUserProfile());
    }

    private void prefillUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            email.setText(currentUser.getEmail());
            name.setText(currentUser.getDisplayName());

            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.profile)
                        .into(profilePicture);
            }

            // Fetch additional user data from Firestore
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String ic = documentSnapshot.getString("icNumber");
                            String phone = documentSnapshot.getString("phoneNumber");
                            String dob = documentSnapshot.getString("dateOfBirth");

                            if (ic != null) icNumber.setText(ic);
                            if (phone != null) phoneNumber.setText(phone);
                            if (dob != null) dateOfBirth.setText(dob);
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(TechnicianEditProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void saveUserProfile() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userIC = icNumber.getText().toString().trim();
        String userPhone = phoneNumber.getText().toString().trim();
        String userDOB = dateOfBirth.getText().toString().trim();

        if (userName.isEmpty() || userEmail.isEmpty() || userIC.isEmpty() || userPhone.isEmpty() || userDOB.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", userName);
            userData.put("email", userEmail);
            userData.put("icNumber", userIC);
            userData.put("phoneNumber", userPhone);
            userData.put("dateOfBirth", userDOB);

            // Save profile picture if a new one is selected
            if (imageUri != null) {
                uploadProfilePicture(userId, userData);
            } else {
                saveUserDataToFirestore(userId, userData);
            }
        }
    }

    private void uploadProfilePicture(String userId, Map<String, Object> userData) {
        StorageReference profilePicRef = storageReference.child("profile_pictures/" + userId + ".jpg");
        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    userData.put("profilePictureUrl", uri.toString());
                    saveUserDataToFirestore(userId, userData);
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show());
    }

    private void saveUserDataToFirestore(String userId, Map<String, Object> userData) {
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to TechnicianHomeActivity after successful save
                    Intent intent = new Intent(TechnicianEditProfileActivity.this, TechnicianHomeActivity.class);
                    startActivity(intent);
                    finish();  // Close the current activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profilePicture);
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            dateOfBirth.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }
}
