package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.model.Rating;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

    private List<Rating> ratingList;

    public RatingAdapter(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        holder.tvUserName.setText(rating.getUserEmail());
        holder.tvReview.setText(rating.getReview());
        holder.tvRating.setRating((float) rating.getRate()); // Cast to float
        holder.tvTimestamp.setText(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(rating.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvReview, tvTimestamp;
        RatingBar tvRating;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_email);
            tvReview = itemView.findViewById(R.id.tv_review);
            tvRating = itemView.findViewById(R.id.tv_rating); // Change type to RatingBar
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }


}