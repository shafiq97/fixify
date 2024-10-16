package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.Category;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up RecyclerView for categories
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Painting Service", "https://www.nipponpaint.co.in/wp-content/uploads/2021/07/shutterstock_376837156-min.jpg"));
        categories.add(new Category("Plumbing Service", "https://www.yhkrenovation.com/wp-content/uploads/2023/10/00-featured-2.jpg"));
        categories.add(new Category("Electrical Service", "https://jrpmservices.com.au/wp-content/uploads/2024/06/Electrical-service-commercial.jpg"));
        categories.add(new Category("Delivery Service", "https://daganghalal.blob.core.windows.net/42457/Product/delivery-service-furniture-1700475247675.jpg"));

        categoryRecyclerView = findViewById(R.id.recyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(categories, category -> {
            // Handle category click and navigate to the services of that category
            Intent intent = new Intent(MainActivity.this, CategoryServiceActivity.class);
            intent.putExtra("selectedCategory", category);
            startActivity(intent);
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
    }
}
