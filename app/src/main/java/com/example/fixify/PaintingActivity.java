package com.example.fixify;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.PaintingService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PaintingActivity extends AppCompatActivity {

    private ImageView backArrow;
    private FirebaseAuth mAuth; // FirebaseAuth instance

    private RecyclerView servicesRecyclerView;
    private PaintingServiceAdapter paintingServiceAdapter;
    private List<PaintingService> serviceList;

    private TextView noServicesText;
    private TextView serviceCountText; // Reference to the new TextView

    private Spinner ratingSpinner;
    private SeekBar distanceSeekBar;
    private TextView distanceValueText;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    // Define maximum distance in meters (e.g., 500 km)
    private final double MAX_DISTANCE_METERS = 500000; // 500 km

    // Sample service coordinates
    private final double SERVICE1_LATITUDE = 37.7749; // San Francisco
    private final double SERVICE1_LONGITUDE = -122.4194;

    //    private final double SERVICE2_LATITUDE = 34.0522; // Los Angeles
//    private final double SERVICE2_LONGITUDE = -118.2437;
    private final double SERVICE2_LATITUDE = 37.7749; // San Francisco
    private final double SERVICE2_LONGITUDE = -122.4194;
    // Add more as needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting); // Ensure this layout exists

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize back arrow and set click listener
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Initialize FusedLocationProviderClient first
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (fusedLocationClient != null) {
            Log.d("PaintingActivity", "FusedLocationProviderClient initialized successfully.");
        } else {
            Log.e("PaintingActivity", "Failed to initialize FusedLocationProviderClient.");
        }

        // Initialize service list
        serviceList = new ArrayList<>();

        // Load services first
        loadServices(); // Adds to serviceList

        // Initialize RecyclerView and Adapter after loading services
        servicesRecyclerView = findViewById(R.id.services_recyclerview);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        paintingServiceAdapter = new PaintingServiceAdapter(serviceList, this);
        servicesRecyclerView.setAdapter(paintingServiceAdapter);

        // Initialize filter controls
        ratingSpinner = findViewById(R.id.rating_spinner);
        distanceSeekBar = findViewById(R.id.distance_seekbar);
        distanceValueText = findViewById(R.id.distance_value_text);
        serviceCountText = findViewById(R.id.service_count_text); // Initialize service count TextView

        noServicesText = findViewById(R.id.no_services_text);

        // Handle filter changes
        setupFilters();

        // After setting up everything, calculate distances
        getLastLocationAndCalculateDistances();
    }

    private void getLastLocationAndCalculateDistances() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // fusedLocationClient should already be initialized in onCreate()
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (fusedLocationClient != null) {
                Log.d("PaintingActivity", "FusedLocationProviderClient initialized in getLastLocationAndCalculateDistances.");
            } else {
                Log.e("PaintingActivity", "Failed to initialize FusedLocationProviderClient in getLastLocationAndCalculateDistances.");
                Toast.makeText(this, "Location services not available.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLon = location.getLongitude();

                Log.d("PaintingActivity", "User Location - Latitude: " + userLat + ", Longitude: " + userLon);

                // Update adapter with user's location
                paintingServiceAdapter.updateUserLocation(userLat, userLon);

                // Re-apply filters based on updated distances
                applyFilters();
            } else {
                // Location is null
                Toast.makeText(PaintingActivity.this, "Unable to determine your location.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            // Handle failure in obtaining location
            Toast.makeText(PaintingActivity.this, "Failed to get location.", Toast.LENGTH_SHORT).show();
            Log.e("PaintingActivity", "getLastLocation failed: " + e.getMessage());
        });
    }

    /**
     * Load services data into the RecyclerView.
     * In a real application, this data might come from a database or API.
     */
    private void loadServices() {
        // Example services with latitude and longitude

        // Add more services as needed

        // Do NOT call notifyDataSetChanged() here since the adapter hasn't been initialized yet
    }

    /**
     * Set up filter controls and their listeners.
     */
    private void setupFilters() {
        // Initialize SeekBar
        distanceSeekBar.setMax(500); // 500 km
        distanceSeekBar.setProgress(250); // Default 250 km
        distanceValueText.setText("250 km");

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Ensure the minimum distance is at least 1 km to avoid 0 km
                int displayedProgress = Math.max(progress, 1);
                distanceValueText.setText(displayedProgress + " km");
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Handle start of touch
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional: Handle end of touch
            }
        });

        // Initialize Spinner
        ratingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle no selection
            }
        });
    }

    /**
     * Apply filters based on selected rating and distance.
     */
    private void applyFilters() {
        // Get selected rating
        String selectedRating = ratingSpinner.getSelectedItem().toString();
        float minRating = 0.0f; // Default to show all

        switch (selectedRating) {
            case "1 Star & Up":
                minRating = 1.0f;
                break;
            case "2 Stars & Up":
                minRating = 2.0f;
                break;
            case "3 Stars & Up":
                minRating = 3.0f;
                break;
            case "4 Stars & Up":
                minRating = 4.0f;
                break;
            case "5 Stars":
                minRating = 5.0f;
                break;
            case "All":
            default:
                minRating = 0.0f;
                break;
        }

        // Get selected distance
        int selectedDistanceKm = distanceSeekBar.getProgress();
        double selectedDistanceMeters = selectedDistanceKm * 1000.0;

        Log.d("PaintingActivity", "Applying filters - Rating: " + minRating + ", Distance: " + selectedDistanceMeters + " meters");

        // Filter the adapter
        paintingServiceAdapter.filter(minRating, selectedDistanceMeters);

        // Update service count
        serviceCountText.setText("Found " + serviceList.size() + " services");

        // Show or hide "No services found" message
        if (serviceList.isEmpty()) {
            noServicesText.setVisibility(View.VISIBLE);
        } else {
            noServicesText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh services and distances when activity resumes
        paintingServiceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastLocationAndCalculateDistances();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission is required to display distances.", Toast.LENGTH_SHORT).show();
                // Optionally, hide distance information or set default values
            }
        }
    }
}
