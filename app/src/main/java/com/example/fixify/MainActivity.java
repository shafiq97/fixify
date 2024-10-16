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

import com.example.fixify.Data.Category;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sign out the user when the app runs (Optional)
        signOut();

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

        // Initialize RecyclerView for categories
        setupCategoryRecyclerView();

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::handleBottomNavigationItemSelected);
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // Optional: show a message if the user is signed out
        });
    }

    private void setupCategoryRecyclerView() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Painting Service", "https://www.nipponpaint.co.in/wp-content/uploads/2021/07/shutterstock_376837156-min.jpg"));
        categories.add(new Category("Plumbing Service", "https://www.yhkrenovation.com/wp-content/uploads/2023/10/00-featured-2.jpg"));
        categories.add(new Category("Electrical Service", "https://jrpmservices.com.au/wp-content/uploads/2024/06/Electrical-service-commercial.jpg"));
        categories.add(new Category("Delivery Service", "https://daganghalal.blob.core.windows.net/42457/Product/delivery-service-furniture-1700475247675.jpg"));

        categoryRecyclerView = findViewById(R.id.recyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryAdapter = new CategoryAdapter(categories, category -> {
            // Navigate to CategoryServiceActivity when a category is clicked
            Intent intent = new Intent(MainActivity.this, CategoryServiceActivity.class);
            intent.putExtra("selectedCategory", category);
            startActivity(intent);
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
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
        }
        else if (id == R.id.nav_waiting) {
            Intent intent = new Intent(MainActivity.this, WaitingActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_active) {
            Intent intent = new Intent(MainActivity.this, ActiveActivity.class);
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
            loginLogoutMenuItem.setTitle("Logout");
        } else {
            loginLogoutMenuItem.setTitle("Login");
        }
    }

    private boolean handleBottomNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Log.d("BottomNavigation", "Home clicked");
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_chatbot) {
            Log.d("BottomNavigation", "Chat clicked");
            Intent intent = new Intent(MainActivity.this, ChatBot.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_noti) {
            Log.d("BottomNavigation", "Notifications clicked");
            Intent intent = new Intent(MainActivity.this, NotiActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_profile) {
            Log.d("BottomNavigation", "Profile clicked");
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
