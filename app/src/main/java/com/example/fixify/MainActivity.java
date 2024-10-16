package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private PaintingServiceAdapter serviceAdapter;
    private List<PaintingService> serviceList;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

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

        // Set navigation view listener
        navigationView.setNavigationItemSelectedListener(this);

        // Update the login/logout menu item based on user's login status
        updateLoginLogoutMenuItem();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerView);
        serviceList = new ArrayList<>();
        serviceAdapter = new PaintingServiceAdapter(serviceList, this);
        recyclerView.setAdapter(serviceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch services from Firestore
        fetchServicesFromFirestore();

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::handleBottomNavigationItemSelected);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Handle Navigation Drawer item clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.d("NavigationDrawer", "Home clicked");
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Log.d("NavigationDrawer", "Profile clicked");
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_login_logout) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in, perform logout
                firebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                updateLoginLogoutMenuItem(); // Update menu item after logout
            } else {
                // User is not logged in, go to login activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateLoginLogoutMenuItem() {
        Menu menu = navigationView.getMenu();
        MenuItem loginLogoutMenuItem = menu.findItem(R.id.nav_login_logout);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, show "Logout"
            loginLogoutMenuItem.setTitle("Logout");
        } else {
            // User is not logged in, show "Login"
            loginLogoutMenuItem.setTitle("Login");
        }
    }

    // Handle Bottom Navigation item clicks
    private boolean handleBottomNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.d("BottomNavigation", "Home clicked");
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_chatbot) {
            Log.d("BottomNavigation", "Chat clicked");
            Toast.makeText(this, "Chat clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, ChatBot.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_noti) {
            Log.d("BottomNavigation", "Notifications clicked");
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, NotiActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_profile) {
            Log.d("BottomNavigation", "Profile clicked");
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        } else {
            return false;
        }
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
                        Toast.makeText(MainActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error fetching services: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
