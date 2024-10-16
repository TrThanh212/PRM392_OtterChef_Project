package com.app.cookbook.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.listener.IOnAdminManagerIngredientListener;
import com.app.cookbook.utils.CustomToast;

import java.util.List;

public class AdminIngredientAdapter extends RecyclerView.Adapter<AdminIngredientAdapter.IngredientViewHolder> {

    private List<String> ingredients;
    private IOnAdminManagerIngredientListener listener;
    private boolean isAdmin;

    public AdminIngredientAdapter(List<String> ingredients, IOnAdminManagerIngredientListener listener , boolean isAdmin) {
        this.ingredients = ingredients;
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredient = ingredients.get(position);
        holder.tvIngredientName.setText(ingredient);
        holder.imgEdit.setOnClickListener(v -> listener.onClickEditIngredient(position, ingredient));
        holder.imgDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Delete Ingredient");
            builder.setMessage("Are you sure you want to delete this ingredient?");
            builder.setPositiveButton("Delete",(dialog, which)->{
                listener.onClickDeleteIngredient(ingredient);
                ingredients.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, ingredients.size());
                CustomToast.showToastLong(holder.itemView.getContext(), "Ingredient deleted successfully");
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Do nothing
            });

            builder.show();
        });

        // Set visibility based on isAdmin flag
        if (isAdmin) {
            holder.imgEdit.setVisibility(View.VISIBLE);
            holder.imgDelete.setVisibility(View.VISIBLE);
        } else {
            holder.imgEdit.setVisibility(View.GONE);
            holder.imgDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName;
        ImageView imgEdit, imgDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tvIngredient);
            imgEdit = itemView.findViewById(R.id.img_edit_ingredient);
            imgDelete = itemView.findViewById(R.id.img_delete_ingredient);
        }
    }
}