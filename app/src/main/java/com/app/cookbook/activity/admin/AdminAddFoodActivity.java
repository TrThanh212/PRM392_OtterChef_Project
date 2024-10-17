package com.app.cookbook.activity.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.adapter.IngredientsAdapter;
import com.app.cookbook.adapter.admin.AdminFoodAdapter;
import com.app.cookbook.adapter.admin.AdminIngredientAdapter;
import com.app.cookbook.adapter.admin.AdminSelectAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminAddFoodBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.listener.IOnAdminManagerIngredientListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.model.FoodDetails;
import com.app.cookbook.model.SelectObject;
import com.app.cookbook.utils.CustomToast;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddFoodActivity extends BaseActivity implements IOnAdminManagerIngredientListener {

    private ActivityAdminAddFoodBinding binding;
    private boolean isUpdate;
    private Food mFood;
    private SelectObject mCategorySelected;
    private List<String> ingredientsList;
    private AdminIngredientAdapter adminIngredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditFood());

        adminIngredientAdapter = new AdminIngredientAdapter(ingredientsList, this, true);
        binding.rvIngredients.setAdapter(adminIngredientAdapter);
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mFood = (Food) bundleReceived.get(Constant.OBJECT_FOOD);
        }
    }

    private void initToolbar() {
        binding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
    }

    private void initView() {
        if (isUpdate) {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_update_food));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mFood.getName());
            binding.edtCalo.setText(String.valueOf(mFood.getCalories()));
            binding.edtImage.setText(mFood.getImage());
            binding.edtLink.setText(mFood.getUrl());
            binding.chbFeatured.setChecked(mFood.isFeatured());

            // Get food details
            FoodDetails foodDetails = mFood.getFoodDetail();
            if (foodDetails != null) {
                ingredientsList = new ArrayList<>(foodDetails.getIngredients());
                setupIngredientsList(); // Initialize the ingredients list
                adminIngredientAdapter.notifyDataSetChanged();

                binding.edtInstructions.setText(foodDetails.getInstructions());
                binding.edtPreparationTime.setText(String.valueOf(foodDetails.getPreparationTime()));
                binding.edtCookingTime.setText(String.valueOf(foodDetails.getCookingTime()));
                binding.edtServings.setText(String.valueOf(foodDetails.getServings()));
            }
        } else {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_add_food));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
            setupIngredientsList(); // Initialize the ingredients list
        }
        loadListCategory();
    }

    private void setupIngredientsList() {
        if (ingredientsList == null) {
            ingredientsList = new ArrayList<>();
        }
        adminIngredientAdapter = new AdminIngredientAdapter(ingredientsList, this, true);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.rvIngredients.setAdapter(adminIngredientAdapter);

        EditText edtIngredient = findViewById(R.id.edtIngredient);
        ImageView btnAddIngredient = findViewById(R.id.btnAddIngredient);

        btnAddIngredient.setOnClickListener(v -> {
            String ingredient = edtIngredient.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                ingredientsList.add(ingredient);
                adminIngredientAdapter.notifyDataSetChanged();
                edtIngredient.setText("");
            }
        });
    }

    private void loadListCategory() {
        MyApplication.get(this).categoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<SelectObject> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Category category = dataSnapshot.getValue(Category.class);
                            if (category == null) return;
                            list.add(0, new SelectObject(category.getId(), category.getName()));
                        }
                        AdminSelectAdapter adapter = new AdminSelectAdapter(AdminAddFoodActivity.this,
                                R.layout.item_choose_option, list);
                        binding.spnCategory.setAdapter(adapter);
                        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mCategorySelected = adapter.getItem(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                        if (mFood != null && mFood.getCategoryId() > 0) {
                            binding.spnCategory.setSelection(getPositionSelected(list, mFood.getCategoryId()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private int getPositionSelected(List<SelectObject> list, long id) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void addOrEditFood() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();
        String strlCalories = binding.edtCalo.getText().toString().trim();
        String strUrl = binding.edtLink.getText().toString().trim();

        // Get ingredients
        List<String> ingredients = ingredientsList;
        String instruction = binding.edtInstructions.getText().toString().trim();
        String preparationTime = binding.edtPreparationTime.getText().toString().trim();
        String cookingTime = binding.edtCookingTime.getText().toString().trim();
        String servings = binding.edtServings.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            CustomToast.showToast(this, getString(R.string.msg_input_name_require));
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            CustomToast.showToast(this, getString(R.string.msg_input_image_require));
            return;
        }
        if (StringUtil.isEmpty(strlCalories)) {
            CustomToast.showToast(this, getString(R.string.msg_input_calo_require));
            return;
        }

        if (StringUtil.isEmpty(strUrl)) {
            CustomToast.showToast(this, getString(R.string.msg_input_url_require));
            return;
        }

        // Update food
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("image", strImage);
            map.put("url", strUrl);
            map.put("featured", binding.chbFeatured.isChecked());
            map.put("categoryId", mCategorySelected.getId());
            map.put("categoryName", mCategorySelected.getName());
            map.put("calories", Double.parseDouble(strlCalories));

            Map<String, Object> foodDetailMap = new HashMap<>();
            foodDetailMap.put("ingredients", ingredients);
            foodDetailMap.put("instructions", instruction);
            foodDetailMap.put("preparationTime", Integer.parseInt(preparationTime));
            foodDetailMap.put("cookingTime", Integer.parseInt(cookingTime));
            foodDetailMap.put("servings", Integer.parseInt(servings));

            map.put("foodDetail", foodDetailMap);

            MyApplication.get(this).foodDatabaseReference()
                    .child(String.valueOf(mFood.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        if (error != null) {
                            CustomToast.showToast(AdminAddFoodActivity.this, getString(R.string.msg_login_error));
                        } else {
                            CustomToast.showToast(AdminAddFoodActivity.this, getString(R.string.msg_edit_food_success));
                            GlobalFunction.hideSoftKeyboard(this);
                        }
                    });
            return;
        }
        FoodDetails foodDetails = new FoodDetails(ingredients, instruction, Integer.parseInt(preparationTime), Integer.parseInt(cookingTime), Integer.parseInt(servings));
        // Add food
        showProgressDialog(true);
        long foodId = System.currentTimeMillis();
        Food food = new Food(foodId, strName, strImage, strUrl, binding.chbFeatured.isChecked(),
                mCategorySelected.getId(), mCategorySelected.getName(), Double.parseDouble(strlCalories), foodDetails);
        MyApplication.get(this).foodDatabaseReference()
                .child(String.valueOf(foodId)).setValue(food, (error, ref) -> {
                    showProgressDialog(false);
                    binding.edtName.setText("");
                    binding.edtImage.setText("");
                    binding.edtLink.setText("");
                    binding.edtCalo.setText("");
                    binding.edtIngredient.setText("");
                    binding.edtInstructions.setText("");
                    binding.edtPreparationTime.setText("");
                    binding.edtCookingTime.setText("");
                    binding.edtServings.setText("");
                    binding.chbFeatured.setChecked(false);
                    binding.spnCategory.setSelection(0);
                    GlobalFunction.hideSoftKeyboard(this);
                    CustomToast.showToast(this, getString(R.string.msg_add_food_success));
                });
    }

    @Override
    public void onClickDeleteIngredient(String ingredient) {
        ingredientsList.remove(ingredient);
        adminIngredientAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickEditIngredient(int position, String ingredient) {
        showEditIngredientDialog(position, ingredient);
    }

    private void showEditIngredientDialog(int position, String ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Ingredient");

        final EditText input = new EditText(this);
        input.setText(ingredient);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newIngredient = input.getText().toString().trim();
            if (!newIngredient.isEmpty()) {
                ingredientsList.set(position, newIngredient);
                adminIngredientAdapter.notifyItemChanged(position);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}