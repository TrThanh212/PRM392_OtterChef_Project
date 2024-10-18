package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.cookbook.model.UserInfo;
import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.IngredientsAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminFoodRecipeBinding;
import com.app.cookbook.databinding.ActivityFoodRecipeCustomerBinding;
import com.app.cookbook.model.Food;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodRecipeActivity extends BaseActivity {

    private long mFoodId;
    private ImageView ivFoodImage;
    private TextView tvFoodName, tvFoodCalories, tvFoodCategory, tvFoodInstructionsContent, tvPreparationTime, tvCookingTime, tvServings, tvRate, tvCountReview;
    private RecyclerView recyclerViewIngredients;
    private IngredientsAdapter ingredientAdapter;
    private ActivityFoodRecipeCustomerBinding mBinding;
    private ValueEventListener mFoodDetailValueEventListener;
    private boolean isLoaded = false;
    private boolean isCountUpdated = false; // Add this flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        mBinding = ActivityFoodRecipeCustomerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ivFoodImage = findViewById(R.id.iv_food_image);
        tvFoodName = findViewById(R.id.tv_food_name);
        tvFoodCalories = findViewById(R.id.tv_food_calories);
        tvFoodCategory = findViewById(R.id.tv_food_category);
        tvFoodInstructionsContent = findViewById(R.id.tv_food_instructions_content);
        tvPreparationTime = findViewById(R.id.tv_preparation_time);
        tvCookingTime = findViewById(R.id.tv_cooking_time);
        tvServings = findViewById(R.id.tv_servings);
        tvRate = findViewById(R.id.tv_rate);
        tvCountReview = findViewById(R.id.tv_count_review);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);

        // Set RecyclerView to horizontal orientation
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Initialize the adapter with an empty list
        ingredientAdapter = new IngredientsAdapter(new ArrayList<>());
        recyclerViewIngredients.setAdapter(ingredientAdapter);

        // Retrieve the foodId from the intent
        mFoodId = getIntent().getLongExtra(Constant.FOOD_ID, -1);
        if (mFoodId != -1) {
            // Fetch the Food object using the foodId
            loadFoodDetailFromFirebase(mFoodId);
        }
        Button btnGoToFoodDetail = findViewById(R.id.btn_go_to_food_detail);
        btnGoToFoodDetail.setOnClickListener(v -> {
            Intent intent = new Intent(FoodRecipeActivity.this, FoodDetailActivity.class);
            intent.putExtra(Constant.FOOD_ID, mFoodId);
            startActivity(intent);
        });
        initToolbar();
    }

    private void loadFoodDetailFromFirebase(long foodId) {
        showProgressDialog(true);
        if (mFoodDetailValueEventListener == null) {
            mFoodDetailValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    showProgressDialog(false);
                    Food food = snapshot.getValue(Food.class);
                    if (food != null) {
                        displayFoodDetails(food);
                        addHistory(food);
                        if (!isCountUpdated) { // Check if the count has already been updated
                            changeCountViewFood(foodId);
                            isCountUpdated = true; // Set the flag to true
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showProgressDialog(false);
                    showToastMessage(FoodRecipeActivity.this, getString(R.string.msg_get_date_error));
                }
            };
            MyApplication.get(this).foodDetailDatabaseReference(foodId).addValueEventListener(mFoodDetailValueEventListener);
        }
    }

    private void displayFoodDetails(Food food) {
        Glide.with(this).load(food.getImage()).into(ivFoodImage);
        tvFoodName.setText(food.getName());
        tvFoodCalories.setText(getString(R.string.label_calories_number, food.getCalories()));
        tvFoodCategory.setText(food.getCategoryName());

        if (food.getFoodDetail() != null) {
            tvFoodInstructionsContent.setText(food.getFoodDetail().getInstructions());
            tvPreparationTime.setText(getString(R.string.label_preparation_time, food.getFoodDetail().getPreparationTime()));
            tvCookingTime.setText(getString(R.string.label_time, food.getFoodDetail().getCookingTime()));
            tvServings.setText(getString(R.string.label_number, food.getFoodDetail().getServings()));

            List<String> ingredients = food.getFoodDetail().getIngredients();
            ingredientAdapter = new IngredientsAdapter(ingredients);
            recyclerViewIngredients.setAdapter(ingredientAdapter);
        } else {
            tvFoodInstructionsContent.setText(R.string.label_add_food);
            tvPreparationTime.setText(R.string.label_add_food);
            tvCookingTime.setText(R.string.label_add_food);
            tvServings.setText(R.string.label_add_food);
        }

        tvRate.setText(String.valueOf(food.getRate()));
        String strCountReview = "(" + food.getCountReviews() + ")";
        tvCountReview.setText(strCountReview);
        mBinding.layoutRatingAndReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.FOOD_ID, mFoodId);
            GlobalFunction.startActivity(FoodRecipeActivity.this, RatingReviewActivity.class, bundle);
        });
    }

    private void addHistory(Food food) {
        if (food == null || isHistory(food)) return;
        String userEmail = DataStoreManager.getUser().getEmail();
        UserInfo userInfo = new UserInfo(System.currentTimeMillis(), userEmail);
        MyApplication.get(this).foodDatabaseReference()
                .child(String.valueOf(food.getId()))
                .child("history")
                .child(String.valueOf(userInfo.getId()))
                .setValue(userInfo);
    }

    private boolean isHistory(Food food) {
        if (food.getHistory() == null || food.getHistory().isEmpty()) {
            return false;
        }
        List<UserInfo> listHistory = new ArrayList<>(food.getHistory().values());
        if (listHistory.isEmpty()) {
            return false;
        }
        for (UserInfo userInfo : listHistory) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }

    private void changeCountViewFood(long foodId) {
        MyApplication.get(this).countFoodDatabaseReference(foodId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCount = snapshot.getValue(Integer.class);
                        int newCount = 1;
                        if (currentCount != null) {
                            newCount = currentCount + 1;
                        }
                        MyApplication.get(FoodRecipeActivity.this)
                                .countFoodDatabaseReference(foodId).removeEventListener(this);
                        MyApplication.get(FoodRecipeActivity.this)
                                .countFoodDatabaseReference(foodId).setValue(newCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_food_detail));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFoodDetailValueEventListener != null) {
            MyApplication.get(this).foodDetailDatabaseReference(mFoodId)
                    .removeEventListener(mFoodDetailValueEventListener);
        }
    }
}