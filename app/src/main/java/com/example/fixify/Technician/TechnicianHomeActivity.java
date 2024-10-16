package com.example.fixify.Technician;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.PaintingService;
import com.example.fixify.PaintingServiceAdapter;
import com.example.fixify.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TechnicianHomeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView servicesRecyclerView;
    private PaintingServiceAdapter serviceAdapter;
    private List<PaintingService> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_home);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize service list and adapter
        serviceList = new ArrayList<>();
        serviceAdapter = new PaintingServiceAdapter(serviceList,this);

        // Set up RecyclerView
        servicesRecyclerView = findViewById(R.id.jobsRecyclerView); // Use the correct ID for the RecyclerView
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Fetch and display services
        fetchServicesFromFirestore();

        // Handle the "Add New Job" button click
        ImageView addJobButton = findViewById(R.id.tambah);
        addJobButton.setOnClickListener(v -> showAddJobDialog());
    }

    private void fetchServicesFromFirestore() {
        db.collection("services")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        serviceList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PaintingService service = document.toObject(PaintingService.class);
                            serviceList.add(service);
                        }
                        serviceAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(TechnicianHomeActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Show a dialog to enter new job details
    private void showAddJobDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_job, null);
        builder.setView(dialogView);

        // Get references to the input fields
        EditText jobTitleInput = dialogView.findViewById(R.id.job_title);
        EditText jobLatitudeInput = dialogView.findViewById(R.id.job_latitude);
        EditText jobLongitudeInput = dialogView.findViewById(R.id.job_longitude);
        EditText jobRatingInput = dialogView.findViewById(R.id.job_rating);
        EditText jobPriceRangeInput = dialogView.findViewById(R.id.job_price_range);
        EditText jobImageUrlInput = dialogView.findViewById(R.id.job_image_url);  // Updated to image URL
        Spinner jobCategorySpinner = dialogView.findViewById(R.id.job_category_spinner);

        // Create a list of categories for the Spinner
        String[] categories = {"Painting Service", "Delivery Service", "Plumbing Service", "Electrical Service"};

        // Populate the Spinner with the categories
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobCategorySpinner.setAdapter(adapter);

        builder.setPositiveButton("Add Job", (dialog, which) -> {
            // Get the values entered by the user
            String jobTitle = jobTitleInput.getText().toString().trim();
            double jobLatitude = Double.parseDouble(jobLatitudeInput.getText().toString().trim());
            double jobLongitude = Double.parseDouble(jobLongitudeInput.getText().toString().trim());
            float jobRating = Float.parseFloat(jobRatingInput.getText().toString().trim());
            String jobPriceRange = jobPriceRangeInput.getText().toString().trim();
            String jobImageUrl = jobImageUrlInput.getText().toString().trim();  // Get image URL
            String selectedCategory = jobCategorySpinner.getSelectedItem().toString();

            // Create a new PaintingService object
            PaintingService newService = new PaintingService(
                    serviceList.size() + 1, // Assign a new ID
                    jobTitle,
                    jobLatitude,
                    jobLongitude,
                    jobRating,
                    jobPriceRange,
                    jobImageUrl,  // Use image URL instead of resource ID
                    selectedCategory  // Set category
            );

            // Add the job to Firestore
            db.collection("services")
                    .add(newService)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(TechnicianHomeActivity.this, "Job added successfully", Toast.LENGTH_SHORT).show();
                        fetchServicesFromFirestore(); // Refresh the list
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TechnicianHomeActivity.this, "Failed to add job", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



}
