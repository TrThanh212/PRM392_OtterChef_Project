package com.app.cookbook.adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.databinding.ItemAdminRequireBinding;
import com.app.cookbook.listener.IOnManagerRequireListener;
import com.app.cookbook.model.RequestFood;

import java.util.List;

public class AdminRequireAdapter extends RecyclerView.Adapter<AdminRequireAdapter.AdminRequireViewHolder> {

    private Context mContext;
    private final List<RequestFood> mListRequire;
    private final IOnManagerRequireListener mListener;

    public AdminRequireAdapter(Context context, List<RequestFood> mListRequire, IOnManagerRequireListener mListener) {
        this.mContext = context;
        this.mListRequire = mListRequire;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AdminRequireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminRequireBinding binding = ItemAdminRequireBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdminRequireViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRequireViewHolder holder, int position) {
        RequestFood requestFood = mListRequire.get(position);
        if (requestFood == null) return;
        if (requestFood.isCompleted())
            holder.mBinding.layoutItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
        else {
            holder.mBinding.layoutItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
        holder.mBinding.chbStatus.setChecked(requestFood.isCompleted());
        holder.mBinding.tvRequire.setText(requestFood.getContent());

        holder.mBinding.chbStatus.setOnClickListener(
                v -> mListener.onClickUpdateRequire(requestFood));
        holder.mBinding.imgDelete.setOnClickListener(
                v -> mListener.onClickDeleteRequire(requestFood));
    }

    @Override
    public int getItemCount() {
        if (mListRequire != null) {
            return mListRequire.size();
        }
        return 0;
    }

    public void release() {
        mContext = null;
    }

    public static class AdminRequireViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminRequireBinding mBinding;

        public AdminRequireViewHolder(@NonNull ItemAdminRequireBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }
    }
}
