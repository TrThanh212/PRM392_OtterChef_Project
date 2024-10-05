package com.app.cookbook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.databinding.ItemCategoryListBinding;
import com.app.cookbook.listener.IOnClickCategoryListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.utils.GlideUtils;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder> {

    private final List<Category> listCategory;
    private final IOnClickCategoryListener mListener;

    public CategoryListAdapter(List<Category> listCategory, IOnClickCategoryListener listener) {
        this.listCategory = listCategory;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryListBinding binding = ItemCategoryListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryListAdapter.CategoryListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {
        Category category = listCategory.get(position);
        if (category == null) return;
        GlideUtils.loadUrl(category.getImage(), holder.mBinding.imgCategory);
        holder.mBinding.tvName.setText(category.getName());
        holder.mBinding.layoutItem.setOnClickListener(v -> mListener.onClickItemCategory(category));
    }

    @Override
    public int getItemCount() {
        return null == listCategory ? 0 : listCategory.size();
    }

    public static class CategoryListViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryListBinding mBinding;

        public CategoryListViewHolder(ItemCategoryListBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
