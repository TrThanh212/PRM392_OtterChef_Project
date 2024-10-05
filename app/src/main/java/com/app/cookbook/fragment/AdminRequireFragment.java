package com.app.cookbook.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.admin.AdminRequireAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.FragmentAdminRequireBinding;
import com.app.cookbook.listener.IOnManagerRequireListener;
import com.app.cookbook.model.RequestFood;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminRequireFragment extends Fragment {

    private FragmentAdminRequireBinding binding;
    private List<RequestFood> mListRequire;
    private AdminRequireAdapter mRequireAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminRequireBinding.inflate(inflater, container, false);

        initView();
        loadListRequire();
        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvData.setLayoutManager(linearLayoutManager);
        mListRequire = new ArrayList<>();
        mRequireAdapter = new AdminRequireAdapter(getActivity(), mListRequire, new IOnManagerRequireListener() {
            @Override
            public void onClickUpdateRequire(RequestFood requestFood) {
                handleUpdateStatusRequire(requestFood);
            }

            @Override
            public void onClickDeleteRequire(RequestFood requestFood) {
                handleDeleteRequire(requestFood);
            }
        });
        binding.rcvData.setAdapter(mRequireAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListRequire() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).requestFoodDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        clearListRequire();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            RequestFood requestFood = dataSnapshot.getValue(RequestFood.class);
                            if (requestFood != null) {
                                mListRequire.add(0, requestFood);
                            }
                        }
                        if (mRequireAdapter != null) mRequireAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void handleUpdateStatusRequire(RequestFood requestFood) {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).requestFoodDatabaseReference()
                .child(String.valueOf(requestFood.getId()))
                .child("completed")
                .setValue(!requestFood.isCompleted());
    }

    private void handleDeleteRequire(RequestFood requestFood) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) return;
                    MyApplication.get(getActivity()).requestFoodDatabaseReference()
                            .child(String.valueOf(requestFood.getId()))
                            .removeValue((error, ref) -> GlobalFunction.showToastMessage(getActivity(),
                                    getString(R.string.msg_delete_require_successfully)));
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void clearListRequire() {
        if (mListRequire != null) {
            mListRequire.clear();
        } else {
            mListRequire = new ArrayList<>();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRequireAdapter != null) mRequireAdapter.release();
    }
}
