package com.example.fixify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize the About Us TextView
        TextView aboutUsTextView = findViewById(R.id.aboutUs);
        TextView termsTextView = findViewById(R.id.terms);
        TextView privacyTextView = findViewById(R.id.privacy);
        TextView contacUsTextView = findViewById(R.id.contact);
        TextView HelpCenterTextView = findViewById(R.id.helpCenter);
        TextView shareActivityTextView = findViewById(R.id.share);


        // Check if TextViews are found
        if (aboutUsTextView != null) {
            // Set an OnClickListener on the About Us TextView
            aboutUsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start AboutUsActivity
                    Intent intent = new Intent(SettingActivity.this, AboutUsActivity.class);
                    startActivity(intent); // Start About Us Activity
                }
            });
        } else {
            // Log error or handle accordingly
            System.err.println("About Us TextView not found");
        }

        if (termsTextView != null) {
            termsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start TermsActivity
                    Intent intent = new Intent(SettingActivity.this, TermsActivity.class);
                    startActivity(intent); // Start Terms Activity
                }
            });
        } else {
            // Log error or handle accordingly
            System.err.println("Terms TextView not found");
        }

        privacyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start TermsActivity
                Intent intent = new Intent(SettingActivity.this, PrivacyActivity.class);
                startActivity(intent); // Start Terms Activity
            }
        });

        contacUsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start TermsActivity
                Intent intent = new Intent(SettingActivity.this, ContactUsActivity.class);
                startActivity(intent); // Start Terms Activity
            }
        });

        HelpCenterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start TermsActivity
                Intent intent = new Intent(SettingActivity.this, HelpCenterActivity.class);
                startActivity(intent); // Start Terms Activity
            }
        });

        shareActivityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start TermsActivity
                Intent intent = new Intent(SettingActivity.this, ShareActivity.class);
                startActivity(intent); // Start Terms Activity
            }
        });

        // Initialize other settings UI components here if needed.
    }
}