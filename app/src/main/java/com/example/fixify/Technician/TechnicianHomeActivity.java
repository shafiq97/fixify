package com.example.fixify.Technician;

import android.content.Intent;
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
        serviceAdapter = new PaintingServiceAdapter(serviceList, this);

        // Set up RecyclerView
        servicesRecyclerView = findViewById(R.id.jobsRecyclerView); // Use the correct ID for the RecyclerView
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Fetch and display services
        fetchServicesFromFirestore();

        // Handle the "Add New Job" button click
        ImageView addJobButton = findViewById(R.id.tambah);
        // Remove the showAddJobDialog() method and update the button click handler

        addJobButton.setOnClickListener(v -> {
            Intent intent = new Intent(TechnicianHomeActivity.this, AddJobActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch the updated services when returning to this activity
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
}
