package com.example.fixify;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.fixify.Data.Feedback;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDialogFragment extends DialogFragment {

    private static final String ARG_SERVICE_ID = "service_id";
    private int serviceId; // Identifier for the service

    private RecyclerView feedbackRecyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;

    private Button closeButton;

    public FeedbackDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serviceId Service identifier.
     * @return A new instance of fragment FeedbackDialogFragment.
     */
    public static FeedbackDialogFragment newInstance(int serviceId) {
        FeedbackDialogFragment fragment = new FeedbackDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve serviceId from arguments
        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_SERVICE_ID);
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.feedback_modal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize views
        TextView modalTitle = view.findViewById(R.id.modal_title);
        feedbackRecyclerView = view.findViewById(R.id.feedback_recyclerview);
        closeButton = view.findViewById(R.id.close_button);

        modalTitle.setText("Feedback");

        // Initialize feedback list and adapter
        feedbackList = getFeedbackForService(serviceId); // Implement this method to fetch feedback
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        feedbackRecyclerView.setAdapter(feedbackAdapter);

        // Handle close button
        closeButton.setOnClickListener(v -> dismiss());
    }

    /**
     * Fetch feedback for the given service ID.
     * In a real application, this data would come from a database or API.
     *
     * @param serviceId The ID of the service.
     * @return A list of Feedback objects.
     */
    private List<Feedback> getFeedbackForService(int serviceId) {
        // Placeholder: Return static feedback data
        List<Feedback> feedbacks = new ArrayList<>();

        // Example feedbacks for demonstration purposes
        if (serviceId == 1) { // Indoor Painting Service
            feedbacks.add(new Feedback("Alice", "Great service! Highly recommend.", 5.0f, "2023-09-15"));
            feedbacks.add(new Feedback("Bob", "Satisfied with the quality.", 4.5f, "2023-09-12"));
            feedbacks.add(new Feedback("Charlie", "Could have been better.", 3.0f, "2023-09-10"));
        } else if (serviceId == 2) { // Interior Painting
            feedbacks.add(new Feedback("David", "Excellent work!", 5.0f, "2023-09-14"));
            feedbacks.add(new Feedback("Eve", "Very professional team.", 4.8f, "2023-09-11"));
        }
        // Add more feedback based on serviceId as needed

        return feedbacks;
    }
}
