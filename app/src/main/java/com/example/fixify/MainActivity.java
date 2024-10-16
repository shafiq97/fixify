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

import com.example.fixify.CategoryAdapter.OnCategoryClickListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;

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

        // Set up RecyclerView for categories
        List<String> categories = Arrays.asList("Painting Service", "Plumbing Service", "Electrical Service", "Delivery Service");
        categoryRecyclerView = findViewById(R.id.recyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(categories, this, category -> {
            // Handle category click and navigate to a new activity with selected category
            Intent intent = new Intent(MainActivity.this, CategoryServiceActivity.class);
            intent.putExtra("selectedCategory", category);
            startActivity(intent);
        });

        categoryRecyclerView.setAdapter(categoryAdapter);

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
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (id == R.id.nav_profile) {
            Log.d("NavigationDrawer", "Profile clicked");
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_login_logout) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in, perform logout
                firebaseAuth.signOut();
                Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                updateLoginLogoutMenuItem(); // Update menu item after logout
            } else {
                // User is not logged in, go to login activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateLoginLogoutMenuItem() {
        Menu menu = navigationView.getMenu();
        MenuItem loginLogoutMenuItem = menu.findItem(R.id.nav_login_logout);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            loginLogoutMenuItem.setTitle("Logout");
        } else {
            loginLogoutMenuItem.setTitle("Login");
        }
    }

    // Handle Bottom Navigation item clicks
    private boolean handleBottomNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            return true;
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
            return true;
        }
        return false;
    }
}
