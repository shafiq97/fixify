package com.example.fixify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fixify.Data.Feedback;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);
        holder.usernameTextView.setText(feedback.getUsername());
        holder.commentTextView.setText(feedback.getComment());
        holder.ratingTextView.setText(String.valueOf(feedback.getRating()));
        holder.dateTextView.setText(feedback.getDate());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;
        TextView ratingTextView;
        TextView dateTextView;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.feedback_username);
            commentTextView = itemView.findViewById(R.id.feedback_comment);
            ratingTextView = itemView.findViewById(R.id.feedback_rating);
            dateTextView = itemView.findViewById(R.id.feedback_date);
        }
    }
}
