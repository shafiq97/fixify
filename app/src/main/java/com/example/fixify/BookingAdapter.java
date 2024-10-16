package com.example.fixify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixify.Data.Booking;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.serviceTitleTextView.setText(booking.getServiceTitle());
        holder.preferredDateTextView.setText(booking.getPreferredDate());
        holder.preferredTimeTextView.setText(booking.getPreferredTime());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        public TextView serviceTitleTextView;
        public TextView preferredDateTextView;
        public TextView preferredTimeTextView;
        public MaterialCardView bookingCard;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitleTextView = itemView.findViewById(R.id.serviceTitleTextView);
            preferredDateTextView = itemView.findViewById(R.id.preferredDateTextView);
            preferredTimeTextView = itemView.findViewById(R.id.preferredTimeTextView);
            bookingCard = itemView.findViewById(R.id.bookingCard);
        }
    }
}
