package com.example.fixify.Technician;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fixify.Data.PaintingService;
import com.example.fixify.LoginActivity;
import com.example.fixify.MainActivity;
import com.example.fixify.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddJobActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText jobTitleInput, jobLatitudeInput, jobLongitudeInput, jobRatingInput, jobPriceRangeInput;
    private Spinner jobCategorySpinner;
    private ImageView jobImageView;
    private Button chooseImageButton, submitJobButton;

    private Uri imageUri; // To store the selected image URI
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        // Initialize Firebase Storage and Firestore
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        jobTitleInput = findViewById(R.id.job_title);
        jobPriceRangeInput = findViewById(R.id.job_price_range);
        jobImageView = findViewById(R.id.job_image);
        chooseImageButton = findViewById(R.id.choose_image_button);
        submitJobButton = findViewById(R.id.submit_job_button);
        jobCategorySpinner = findViewById(R.id.job_category_spinner);

        // Set up the category spinner
        String[] categories = {"Painting Service", "Delivery Service", "Plumbing Service", "Electrical Service"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobCategorySpinner.setAdapter(adapter);

        // Handle image selection
        chooseImageButton.setOnClickListener(v -> openFileChooser());

        // Handle job submission
        submitJobButton.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageAndSaveJob();
            } else {
                Toast.makeText(AddJobActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            jobImageView.setImageURI(imageUri); // Show the selected image
        }
    }

    private void uploadImageAndSaveJob() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("job_images/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveJobToFirestore(imageUrl); // Save job details with image URL
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(AddJobActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveJobToFirestore(String imageUrl) {
        String jobTitle = jobTitleInput.getText().toString().trim();
        double jobLatitude = 0.0;
        double jobLongitude = 0.0;
        float jobRating = 0;
        String jobPriceRange = jobPriceRangeInput.getText().toString().trim();
        String selectedCategory = jobCategorySpinner.getSelectedItem().toString();

        // Create a new PaintingService object
        PaintingService newService = new PaintingService(
                0, // Dummy ID (Firestore will generate an ID for the document)
                jobTitle,
                jobLatitude,
                jobLongitude,
                jobRating,
                jobPriceRange,
                imageUrl, // Image URL from Firebase Storage
                selectedCategory
        );

        // Add the job to Firestore
        db.collection("services")
                .add(newService)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddJobActivity.this, "Job added successfully", Toast.LENGTH_SHORT).show();
//                    finish(); // Close the activity and return to previous screen
                    Intent intent = new Intent(AddJobActivity.this, TechnicianHomeActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddJobActivity.this, "Failed to add job", Toast.LENGTH_SHORT).show();
                });
    }
}
