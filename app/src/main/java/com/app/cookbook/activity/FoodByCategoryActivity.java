package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.adapter.FoodAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityFoodByCategoryBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodByCategoryActivity extends BaseActivity {

    private ActivityFoodByCategoryBinding mBinding;
    private FoodAdapter mFoodAdapter;
    private List<Food> mListFood;
    private Category mCategory;
    private ValueEventListener mFoodValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityFoodByCategoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadListFoodFromFirebase();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCategory = (Category) bundle.get(Constant.OBJECT_CATEGORY);
        }
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(mCategory.getName());
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);
        mListFood = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mListFood, new IOnClickFoodListener() {
            @Override
            public void onClickItemFood(Food food) {
                GlobalFunction.goToFoodDetail(FoodByCategoryActivity.this, food.getId());
            }

            @Override
            public void onClickFavoriteFood(Food food, boolean favorite) {
                GlobalFunction.onClickFavoriteFood(FoodByCategoryActivity.this, food, favorite);
            }

            @Override
            public void onClickMyDishFood(Food food, boolean dish) {
                GlobalFunction.onClickMyDishFood(FoodByCategoryActivity.this, food, dish);
            }

            @Override
            public void onClickCategoryOfFood(Category category) {}
        });
        mBinding.rcvData.setAdapter(mFoodAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListFoodFromFirebase() {
        mFoodValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFood();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food == null) return;
                    mListFood.add(0, food);
                }
                if (mFoodAdapter != null) mFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        MyApplication.get(this).foodDatabaseReference()
                .orderByChild("categoryId").equalTo(mCategory.getId())
                .addValueEventListener(mFoodValueEventListener);
    }

    private void resetListFood() {
        if (mListFood == null) {
            mListFood = new ArrayList<>();
        } else {
            mListFood.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFoodValueEventListener != null) {
            MyApplication.get(this).foodDatabaseReference().removeEventListener(mFoodValueEventListener);
        }
    }
}
