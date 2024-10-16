package com.app.cookbook.activity.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.adapter.admin.AdminFoodAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminFoodOfCategoryBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodOfCategoryActivity extends BaseActivity {

    private ActivityAdminFoodOfCategoryBinding binding;
    private List<Food> mListFood;
    private AdminFoodAdapter mAdminFoodAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminFoodOfCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadListFood();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            mCategory = (Category) bundleReceived.get(Constant.OBJECT_CATEGORY);
        }
    }

    private void initToolbar() {
        binding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        binding.layoutToolbar.tvToolbarTitle.setText(mCategory.getName());
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvFood.setLayoutManager(linearLayoutManager);
        mListFood = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(mListFood, new IOnAdminManagerFoodListener() {
            @Override
            public void onClickUpdateFood(Food food) {
                onClickEditFood(food);
            }

            @Override
            public void onClickDeleteFood(Food food) {
                deleteFoodItem(food);
            }

            @Override
            public void onClickDetailFood(Food food) {
                goToFoodDetail(food);
            }

        });
        binding.rcvFood.setAdapter(mAdminFoodAdapter);
    }

    private void goToFoodDetail(@NonNull Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, food);
        GlobalFunction.startActivity(this, AdminFoodDetailActivity.class, bundle);
    }

    private void onClickEditFood(Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, food);
        GlobalFunction.startActivity(this, AdminAddFoodActivity.class, bundle);
    }

    private void deleteFoodItem(Food food) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) ->
                        MyApplication.get(this).foodDatabaseReference()
                                .child(String.valueOf(food.getId())).removeValue((error, ref) ->
                                        CustomToast.showToast(this,
                                                getString(R.string.msg_delete_food_successfully)
                                        )
                                )
                )
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void resetListFood() {
        if (mListFood != null) {
            mListFood.clear();
        } else {
            mListFood = new ArrayList<>();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListFood() {
        MyApplication.get(this).foodDatabaseReference()
                .orderByChild("categoryId").equalTo(mCategory.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        resetListFood();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Food food = dataSnapshot.getValue(Food.class);
                            if (food == null) return;
                            mListFood.add(0, food);
                        }
                        if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}