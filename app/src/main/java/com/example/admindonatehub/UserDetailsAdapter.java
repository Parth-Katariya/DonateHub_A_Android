package com.example.admindonatehub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserDetailsAdapter extends RecyclerView.Adapter<UserDetailsAdapter.UserViewHolder> {
    List<UserDetail> userDetailList;
    private Context context;

    public UserDetailsAdapter(Context context, List<UserDetail> userDetailList) {
        this.context = context;
        this.userDetailList = userDetailList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_cardview, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDetail userDetail = userDetailList.get(position);
        holder.userNameTextView.setText(userDetail.getUserName());
        holder.userEmailTextView.setText(userDetail.getUserEmail());
        holder.donationAmountTextView.setText(formatDonationAmount(userDetail.getDonationAmount())); // Format the amount

        // Load the user image using Glide
        Glide.with(context)
                .load(userDetail.getImageUrl())
                .into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        return userDetailList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView userNameTextView;
        TextView userEmailTextView;
        TextView donationAmountTextView;

        UserViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
            donationAmountTextView = itemView.findViewById(R.id.donationAmountTextView);
        }
    }

    // Helper method to format the donation amount
    private String formatDonationAmount(double amount) {
        // Check if it's an integer (no decimal part)
        if (amount == (int) amount) {
            return String.format("₹%,.0f", amount); // No decimal point
        } else {
            return String.format("₹%,.2f", amount); // With two decimal places
        }
    }
}
