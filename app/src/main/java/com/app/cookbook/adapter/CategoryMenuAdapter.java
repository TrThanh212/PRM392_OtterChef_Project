package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemCategoryMenuBinding;
import com.app.cookbook.listener.IOnClickCategoryListener;
import com.app.cookbook.model.Category;

import java.util.List;

public class CategoryMenuAdapter extends RecyclerView.Adapter<CategoryMenuAdapter.CategoryViewHolder> {

    private final List<Category> listCategory;
    private final IOnClickCategoryListener mListener;

    public CategoryMenuAdapter(List<Category> listCategory, IOnClickCategoryListener listener) {
        this.listCategory = listCategory;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryMenuBinding binding = ItemCategoryMenuBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryMenuAdapter.CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCategory.get(position);
        if (category == null) return;
        holder.mBinding.tvTitle.setText(category.getName());
        String strCount = category.getCount() + " món";
        holder.mBinding.tvCount.setText(strCount);
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickItemCategory(category));
    }

    @Override
    public int getItemCount() {
        return null == listCategory ? 0 : listCategory.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryMenuBinding mBinding;

        public CategoryViewHolder(ItemCategoryMenuBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
