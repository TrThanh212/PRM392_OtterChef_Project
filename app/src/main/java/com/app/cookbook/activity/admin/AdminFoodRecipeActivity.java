package com.app.cookbook.activity.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.R;
import com.app.cookbook.adapter.IngredientsAdapter;
import com.app.cookbook.adapter.admin.AdminIngredientAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.model.Food;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class AdminFoodRecipeActivity extends AppCompatActivity {

    private ImageView ivFoodImage;
    private TextView tvFoodName, tvFoodCalories, tvFoodCategory, tvFoodInstructionsContent;
    private RecyclerView recyclerViewIngredients;
    private IngredientsAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_food_recipe);

        ivFoodImage = findViewById(R.id.iv_food_image);
        tvFoodName = findViewById(R.id.tv_food_name);
        tvFoodCalories = findViewById(R.id.tv_food_calories);
        tvFoodCategory = findViewById(R.id.tv_food_category);
        tvFoodInstructionsContent = findViewById(R.id.tv_food_instructions_content);
        recyclerViewIngredients = findViewById(R.id.recycler_view_ingredients);


        // Set RecyclerView to horizontal orientation
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Food food = (Food) getIntent().getSerializableExtra(Constant.OBJECT_FOOD);
        if (food != null) {
            displayFoodDetails(food);
        }


    }

    private void displayFoodDetails(Food food) {
        Glide.with(this).load(food.getImage()).into(ivFoodImage);
        tvFoodName.setText(food.getName());
        tvFoodCalories.setText(getString(R.string.label_calories_number, food.getCalories()));
        tvFoodCategory.setText(getString(R.string.label_category_name, food.getCategoryName()));
        tvFoodInstructionsContent.setText(food.getFoodDetail().getInstructions());

        List<String> ingredients = food.getFoodDetail().getIngredients();
        ingredientAdapter = new IngredientsAdapter(ingredients);
        recyclerViewIngredients.setAdapter(ingredientAdapter);
    }
}
