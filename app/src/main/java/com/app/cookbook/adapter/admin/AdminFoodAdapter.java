package com.app.cookbook.adapter.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemAdminFoodBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.AdminFoodViewHolder> {

    private final List<Food> mListFoods;
    public final IOnAdminManagerFoodListener mListener;

    public AdminFoodAdapter(List<Food> list, IOnAdminManagerFoodListener listener) {
        this.mListFoods = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminFoodBinding binding = ItemAdminFoodBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminFoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFoodViewHolder holder, int position) {
        Food food = mListFoods.get(position);
        if (food == null) return;
        GlideUtils.loadUrl(food.getImage(), holder.mBinding.imgFood);
        holder.mBinding.tvName.setText(food.getName());
        holder.mBinding.tvCategory.setText(food.getCategoryName());
        if (food.isFeatured()) {
            holder.mBinding.tvFeatured.setText("Có");
        } else {
            holder.mBinding.tvFeatured.setText("Không");
        }

        holder.mBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateFood(food));
        holder.mBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteFood(food));
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailFood(food));
    }

    @Override
    public int getItemCount() {
        return null == mListFoods ? 0 : mListFoods.size();
    }

    public static class AdminFoodViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminFoodBinding mBinding;

        public AdminFoodViewHolder(ItemAdminFoodBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
