package com.example.fixify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.PaintingService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryServiceActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView servicesRecyclerView;
    private PaintingServiceAdapter serviceAdapter;
    private List<PaintingService> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_service);

        // Get the selected category from the intent
        String selectedCategory = getIntent().getStringExtra("selectedCategory");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        serviceList = new ArrayList<>();
        serviceAdapter = new PaintingServiceAdapter(serviceList, this);
        servicesRecyclerView.setAdapter(serviceAdapter);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch services from Firestore based on the selected category
        fetchServicesByCategory(selectedCategory);
    }

    private void fetchServicesByCategory(String category) {
        db.collection("services")
                .whereEqualTo("category", category)
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
                        Toast.makeText(CategoryServiceActivity.this, "Failed to load services for " + category, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CategoryServiceActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
