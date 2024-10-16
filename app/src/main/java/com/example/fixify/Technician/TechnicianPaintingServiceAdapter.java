package com.example.fixify.Technician;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.PaintingService;
import com.example.fixify.PaintingServiceAdapter;
import com.example.fixify.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class TechnicianPaintingServiceAdapter extends RecyclerView.Adapter<TechnicianPaintingServiceAdapter.ServiceViewHolder> {

    private List<PaintingService> paintingServiceList;
    private List<PaintingService> originalServiceList; // To hold the original data for resetting filters
    private Context context;

    public TechnicianPaintingServiceAdapter(List<PaintingService> paintingServiceList, Context context) {
        this.paintingServiceList = paintingServiceList;
        this.originalServiceList = new ArrayList<>(paintingServiceList); // Keep a copy of the original list
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_painting_service_technician, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        PaintingService service = paintingServiceList.get(position);

        holder.serviceTitle.setText(service.getTitle());
        holder.servicePrice.setText(service.getPriceRange());
        holder.serviceDistanceText.setText(String.format("Distance: %.2f km", service.getDistance()));

        // Load the image resource using Glide or other methods
        Glide.with(context).load(service.getImageUrl()).into(holder.serviceImage);

        // Handle Details button click to navigate to DetailServiceActivity
        holder.detailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, TechnicianDetailServiceActivity.class);
            intent.putExtra("serviceTitle", service.getTitle());
            intent.putExtra("servicePrice", service.getPriceRange());
            intent.putExtra("serviceImageUrl", service.getImageUrl());
            intent.putExtra("serviceRating", service.getRating());
            intent.putExtra("serviceLatitude", service.getLatitude());
            intent.putExtra("serviceLongitude", service.getLongitude());
            intent.putExtra("serviceCategory", service.getCategory());
            context.startActivity(intent);
        });

        holder.bookButton.setOnClickListener(v -> {
            // Handle Book TimeSlot click
        });

        holder.feedbackButton.setOnClickListener(v -> {
            // Handle Feedback click
        });
    }


    @Override
    public int getItemCount() {
        return paintingServiceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceTitle;
        TextView servicePrice;
        TextView serviceDistanceText;
        Button detailsButton;
        Button bookButton;
        Button feedbackButton;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.service_image);
            serviceTitle = itemView.findViewById(R.id.service_title);
            servicePrice = itemView.findViewById(R.id.service_price);
            serviceDistanceText = itemView.findViewById(R.id.service_distance_text);
            detailsButton = itemView.findViewById(R.id.details_button);
            bookButton = itemView.findViewById(R.id.book_button);
            feedbackButton = itemView.findViewById(R.id.feedback_button);
        }
    }

    // Filter method to filter by rating and distance
    public void filter(float minRating, double maxDistance) {
        paintingServiceList.clear();

        for (PaintingService service : originalServiceList) {
            if (service.getRating() >= minRating && service.getDistance() <= maxDistance) {
                paintingServiceList.add(service);
            }
        }

        // Notify adapter to refresh the RecyclerView with filtered data
        notifyDataSetChanged();
    }

    // Method to update the user's location and calculate distances to services
    public void updateUserLocation(double userLat, double userLon) {
        for (PaintingService service : originalServiceList) {
            double distance = calculateDistance(userLat, userLon, service.getLatitude(), service.getLongitude());
            service.setDistance(distance); // Update the distance in the service model
        }

        // Notify adapter that data has changed
        notifyDataSetChanged();
    }

    // Method to calculate distance between two locations (in kilometers)
    private double calculateDistance(double userLat, double userLon, double serviceLat, double serviceLon) {
        double earthRadius = 6371.0; // Radius of Earth in kilometers

        double dLat = Math.toRadians(serviceLat - userLat);
        double dLon = Math.toRadians(serviceLon - userLon);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(serviceLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // Returns the distance in kilometers
    }
}


