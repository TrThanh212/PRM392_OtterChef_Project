package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ItemFoodBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<Food> listFood;
    private final IOnClickFoodListener mListener;

    public FoodAdapter(List<Food> listFood, IOnClickFoodListener mListener) {
        this.listFood = listFood;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodAdapter.FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = listFood.get(position);
        if (food == null) return;

        GlideUtils.loadUrl(food.getImage(), holder.mBinding.imgFood);
        holder.mBinding.tvName.setText(food.getName());
        holder.mBinding.tvRate.setText(String.valueOf(food.getRate()));
        holder.mBinding.tvCategory.setText(food.getCategoryName());
        holder.mBinding.tvCalories.setText(String.format("Calories: %.2f", food.getCalories())); // Bind calories


        boolean isFavorite = GlobalFunction.isFavoriteFood(food);
        boolean isMyDish = GlobalFunction.isMyDish(food);

        holder.mBinding.tvCountHistory.setText(String.valueOf(food.getCount()));
        holder.mBinding.tvCountFavorite.setText(food.countFavorites());

        if (isFavorite) {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        if (isMyDish) {
            holder.mBinding.imgDishes.setImageResource(R.drawable.ic_dish);
        } else {
            holder.mBinding.imgDishes.setImageResource(R.drawable.ic_undish);
        }

        holder.mBinding.imgFavorite.setOnClickListener(v -> mListener.onClickFavoriteFood(food, !isFavorite));
        holder.mBinding.imgDishes.setOnClickListener(v -> mListener.onClickMyDishFood(food, !isMyDish));
        holder.mBinding.layoutImage.setOnClickListener(v -> mListener.onClickItemFood(food));
        holder.mBinding.layoutInfo.setOnClickListener(v -> mListener.onClickItemFood(food));
        holder.mBinding.tvCategory.setOnClickListener(v -> mListener.onClickCategoryOfFood(
                new Category(food.getCategoryId(), food.getCategoryName())));
    }

    @Override
    public int getItemCount() {
        return null == listFood ? 0 : listFood.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodBinding mBinding;

        public FoodViewHolder(ItemFoodBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
