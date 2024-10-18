package com.app.cookbook.activity.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.adapter.IngredientsAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.databinding.ActivityAdminFoodRecipeBinding;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.LocaleHelper;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdminFoodRecipeActivity extends AppCompatActivity {
    private long mFoodId;
    private ImageView ivFoodImage;
    private TextView tvFoodName, tvFoodCalories, tvFoodCategory, tvFoodInstructionsContent, tvPreparationTime, tvCookingTime, tvServings;
    private RecyclerView recyclerViewIngredients;
    private IngredientsAdapter ingredientAdapter;
    private ActivityAdminFoodRecipeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        mBinding = ActivityAdminFoodRecipeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ivFoodImage = findViewById(R.id.iv_food_image);
        tvFoodName = findViewById(R.id.tv_food_name);
        tvFoodCalories = findViewById(R.id.tv_food_calories);
        tvFoodCategory = findViewById(R.id.tv_food_category);
        tvFoodInstructionsContent = findViewById(R.id.tv_food_instructions_content);
        tvPreparationTime = findViewById(R.id.tv_preparation_time);
        tvCookingTime = findViewById(R.id.tv_cooking_time);
        tvServings = findViewById(R.id.tv_servings);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);

        // Set RecyclerView to horizontal orientation
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Food food = (Food) getIntent().getSerializableExtra(Constant.OBJECT_FOOD);
        if (food != null) {
            displayFoodDetails(food);
        }
        initToolbar();
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
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_foodrecipe));
    }
}