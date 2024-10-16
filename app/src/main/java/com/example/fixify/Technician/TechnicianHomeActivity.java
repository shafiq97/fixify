package com.example.fixify.Technician;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.PaintingService;
import com.example.fixify.PaintingServiceAdapter;
import com.example.fixify.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TechnicianHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFirestore db;
    private RecyclerView servicesRecyclerView;
    private TechnicianPaintingServiceAdapter serviceAdapter;
    private List<PaintingService> serviceList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_home);

        // Set up Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize service list and adapter
        serviceList = new ArrayList<>();
        serviceAdapter = new TechnicianPaintingServiceAdapter(serviceList, this);

        // Set up RecyclerView
        servicesRecyclerView = findViewById(R.id.jobsRecyclerView); // Use the correct ID for the RecyclerView
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Fetch and display services
        fetchServicesFromFirestore();

        // Handle the "Add New Job" button click
        ImageView addJobButton = findViewById(R.id.tambah);
        addJobButton.setOnClickListener(v -> {
            Intent intent = new Intent(TechnicianHomeActivity.this, AddJobActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchServicesFromFirestore();
    }

    private void fetchServicesFromFirestore() {
        db.collection("services").get().addOnCompleteListener(task -> {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TechnicianHomeActivity.this, TechnicianBookingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(TechnicianHomeActivity.this, TechnicianProfileActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Unknown item clicked", Toast.LENGTH_SHORT).show();
        }

        // Close the navigation drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
