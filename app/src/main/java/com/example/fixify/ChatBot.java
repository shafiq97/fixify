// MainActivity.java
package com.example.fixify;  // Change this to match your manifest package name

import static com.example.fixify.R.id.tvAnswer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChatBot extends AppCompatActivity {

    // Sample questions and answers
    private String[][] qaPairs = {
            {"What is your name?", "I am a Chatbot."},
            {"How are you?", "I am doing great!"},
            {"What can you do?", "I can chat with you."},
            {"Tell me a joke.", "Why did the chicken join a band? Because it had the drumsticks!"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        // Reference to the TextView and the LinearLayout
        TextView tvAnswer = findViewById(R.id.tvAnswer);
        LinearLayout questionsContainer = findViewById(R.id.questionsContainer);

        // Dynamically add buttons for each question
        for (String[] qa : qaPairs) {
            String question = qa[0];
            String answer = qa[1];

            // Create a new Button
            Button btnQuestion = new Button(this);
            btnQuestion.setText(question);
            btnQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set the answer when a question is clicked
                    tvAnswer.setText(answer);
                }
            });

            // Add the button to the container
            questionsContainer.addView(btnQuestion);
        }
    }
}
