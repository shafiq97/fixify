package com.example.fixify;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide; // Import Glide
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 102;

    private ImageView profilePicture, calendarIcon;
    private TextView changePhoto;
    private EditText name, email, phoneNumber, icNumber, dateOfBirth;
    private Button saveButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Find views by ID
        profilePicture = findViewById(R.id.profile_picture);
        changePhoto = findViewById(R.id.change_photo);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phone_number);
        icNumber = findViewById(R.id.ic_number);
        dateOfBirth = findViewById(R.id.date_of_birth);
        calendarIcon = findViewById(R.id.calendar_icon);
        saveButton = findViewById(R.id.save_button);

        // Back button click listener
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Change photo click listener
        changePhoto.setOnClickListener(v -> showImagePickerDialog());

        // Calendar click listener for both dateOfBirth EditText and calendar icon
        View.OnClickListener showDatePicker = v -> showDatePickerDialog();
        dateOfBirth.setOnClickListener(showDatePicker);
        calendarIcon.setOnClickListener(showDatePicker);

        // Prefill user data if exists
        prefillUserData();

        // Save button click listener
        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void prefillUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Prefill email and name from FirebaseAuth
            email.setText(currentUser.getEmail());
            name.setText(currentUser.getDisplayName());

            // Prefill phone number if available
            String phone = currentUser.getPhoneNumber();
            if (phone != null && !phone.isEmpty()) {
                phoneNumber.setText(phone);
            } else {
                phoneNumber.setHint("Phone number not available");
            }

            // Load profile picture using Glide
            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.profile) // default image
                        .into(profilePicture);
            }

            // Fetch additional user data like IC Number and DOB from Firestore
            String userId = currentUser.getUid();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Populate additional fields if they exist
                            String existingPhone = documentSnapshot.getString("phoneNumber");
                            String existingIC = documentSnapshot.getString("icNumber");  // Retrieve IC Number
                            String existingDOB = documentSnapshot.getString("dateOfBirth");

                            if (existingPhone != null) {
                                phoneNumber.setText(existingPhone);
                            }

                            if (existingIC != null) {
                                icNumber.setText(existingIC);  // Prefill IC number field
                            }

                            if (existingDOB != null) {
                                dateOfBirth.setText(existingDOB);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
            finish();
        }
    }



    private void saveUserProfile() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPhone = phoneNumber.getText().toString().trim();
        String userIC = icNumber.getText().toString().trim();  // Get the IC number input
        String userDOB = dateOfBirth.getText().toString().trim();

        // Check if all fields are filled
        if (userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() ||
                userIC.isEmpty() || userDOB.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user ID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Prepare user data to be saved
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", userName);
            userData.put("email", userEmail);
            userData.put("phoneNumber", userPhone);
            userData.put("icNumber", userIC);  // Save the IC number in Firestore
            userData.put("dateOfBirth", userDOB);

            // Upload profile picture if a new one is selected
            if (imageUri != null) {
                uploadProfilePicture(userId, imageUri, downloadUrl -> {
                    userData.put("profilePictureUrl", downloadUrl);
                    saveUserDataToFirestore(userId, userData);
                });
            } else {
                saveUserDataToFirestore(userId, userData);
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveUserDataToFirestore(String userId, Map<String, Object> userData) {
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to the desired activity, e.g., MyProfileActivity
                    // Intent intent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                    // startActivity(intent);
                    finish(); // Close the EditProfileActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadProfilePicture(String userId, Uri imageUri, OnSuccessListener<String> onSuccessListener) {
        StorageReference profilePicRef = storageReference.child("profile_pictures/" + userId + ".jpg");

        profilePicRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    onSuccessListener.onSuccess(downloadUrl);
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                });
    }

    // Show dialog to choose between gallery and camera
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (checkCameraPermission()) {
                    openCamera();
                } else {
                    requestCameraPermission();
                }
            } else if (which == 1) {
                if (checkStoragePermission()) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
        builder.show();
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera application found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle image selection or capture
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                // Load image into ImageView using Glide
                Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.profile)
                        .into(profilePicture);
            } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    // Convert Bitmap to Uri if necessary or upload directly
                    // Here, we display the captured photo
                    profilePicture.setImageBitmap(photo);
                    // Optionally, you can convert Bitmap to Uri using a utility method
                    // and set imageUri for uploading
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateOfBirth.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select a photo", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
