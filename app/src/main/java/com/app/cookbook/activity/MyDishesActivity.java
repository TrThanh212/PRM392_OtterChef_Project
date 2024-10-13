package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.FoodAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityFavoriteBinding;
import com.app.cookbook.databinding.ActivityMydishesBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.CustomToast;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.List;

public class MyDishesActivity extends BaseActivity {

    private ActivityMydishesBinding mBinding;
    private List<Food> mListFood;
    private FoodAdapter mFoodAdapter;
    private ValueEventListener mValueEventListener;
    private TextView tvTotalCalories,tvPersonTotalCalories;

   // private TextView tvCalorieResult;
    private EditText etHeight, etWeight, etAge; // Thêm trường nhập tuổi
    private Button btnCalculate;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityMydishesBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        sharedPreferences = getSharedPreferences("DataDishesTDEE", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initToolbar();
        initUi();
        loadDataMyDishes();

        loadSavedData();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_mydishes));

    }

    private void initUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvData.setLayoutManager(linearLayoutManager);

        mListFood = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mListFood, new IOnClickFoodListener() {
            @Override
            public void onClickItemFood(Food food) {
                GlobalFunction.goToFoodDetail(MyDishesActivity.this, food.getId());
            }

            @Override
            public void onClickFavoriteFood(Food food, boolean favorite) {
                GlobalFunction.onClickFavoriteFood(MyDishesActivity.this, food, favorite);
            }
            @Override
            public void onClickMyDishFood(Food food, boolean dish) {
                GlobalFunction.onClickMyDishFood(MyDishesActivity.this, food, dish);
            }

            @Override
            public void onClickCategoryOfFood(Category category) {
                GlobalFunction.goToFoodByCategory(MyDishesActivity.this, category);
            }
        });
        mBinding.rcvData.setAdapter(mFoodAdapter);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvPersonTotalCalories = findViewById(R.id.tvPersonTotalCalories);

        // Khởi tạo các View
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etAge = findViewById(R.id.et_age); // Khởi tạo trường tuổi
        btnCalculate = findViewById(R.id.btn_calculate_bmi);
        //tvCalorieResult = findViewById(R.id.tv_calorie_result);

        // Thiết lập sự kiện cho nút tính toán
        btnCalculate.setOnClickListener(view -> calculateCaloriesOfPerson());

        tvPersonTotalCalories = findViewById(R.id.tvPersonTotalCalories);
        tvPersonTotalCalories.setText(getString(R.string.label_person_total_calories, 0.00f));

        // Add TextWatchers to save data
        addTextWatchers();
    }
// DataDishesTDEE
    private void addTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                editor.putString("age", etAge.getText().toString());
                editor.putString("height", etHeight.getText().toString());
                editor.putString("weight", etWeight.getText().toString());
                editor.apply();
            }
        };

        etAge.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);
    }
    private void loadSavedData() {
        String savedAge = sharedPreferences.getString("age", "");
        String savedHeight = sharedPreferences.getString("height", "");
        String savedWeight = sharedPreferences.getString("weight", "");

        etAge.setText(savedAge);
        etHeight.setText(savedHeight);
        etWeight.setText(savedWeight);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadDataMyDishes() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListData();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food == null) return;
                    if (GlobalFunction.isMyDish(food)) {
                        mListFood.add(0, food);
                    }
                }
                if (mListFood.isEmpty()) {
                    hideInputFields(); // Ẩn các trường nhập
                } else {
                    showInputFields(); // Hiện các trường nhập
                }
                if (mFoodAdapter != null) mFoodAdapter.notifyDataSetChanged();
                calculateTotalCaloriesOfDishes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(MyDishesActivity.this,
                        getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).foodDatabaseReference().addValueEventListener(mValueEventListener);
    }
    private void hideInputFields() {
        etHeight.setVisibility(View.GONE);
        etWeight.setVisibility(View.GONE);
        etAge.setVisibility(View.GONE);
        btnCalculate.setVisibility(View.GONE);
        //tvCalorieResult.setVisibility(View.GONE);
        tvTotalCalories.setVisibility(View.GONE);
        tvPersonTotalCalories.setVisibility(View.GONE);

    }

    private void showInputFields() {
        etHeight.setVisibility(View.VISIBLE);
        etWeight.setVisibility(View.VISIBLE);
        etAge.setVisibility(View.VISIBLE);
        btnCalculate.setVisibility(View.VISIBLE);

        tvTotalCalories.setVisibility(View.VISIBLE);
        tvPersonTotalCalories.setVisibility(View.VISIBLE);
    }

    private void resetListData() {
        if (mListFood == null) {
            mListFood = new ArrayList<>();
        } else {
            mListFood.clear();
        }
    }
    private void calculateTotalCaloriesOfDishes() {
        float totalCalories = 0;
        for (Food food : mListFood) {
            totalCalories += food.getCalories();
        }
        String totalCaloriesText = getString(R.string.label_total_calories, totalCalories);
        tvTotalCalories.setText(totalCaloriesText);
    }

    private void calculateCaloriesOfPerson() {
        String heightStr = etHeight.getText().toString();
        String weightStr = etWeight.getText().toString();
        String ageStr = etAge.getText().toString(); // Lấy tuổi

        if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
           // Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            CustomToast.showToast(this,"Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        float height;
        float weight;
        int age;

        try {
            height = Float.parseFloat(heightStr);
            weight = Float.parseFloat(weightStr);
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            CustomToast.showToast(this,"Vui lòng nhập giá trị hợp lệ.");
            return;
        }

        if (height <= 0 || height > 300) {
            CustomToast.showToast(this,"Chiều cao không hợp lệ.");
            return;
        }

        if (weight <= 0 || weight > 500) {
            CustomToast.showToast(this,"Cân nặng không hợp lệ.");
            return;
        }

        if (age <= 0 || age > 120) {
            CustomToast.showToast(this,"Tuổi không hợp lệ.");
            return;
        }

        float bmr = (10 * weight) + (6.25f * height) - (5 * age) + 5;
        float averageCalories = bmr * 1.2f;

        float totalCalories = 0;
        for (Food food : mListFood) {
            totalCalories += food.getCalories();
        }
        // Inflate the popup layout
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_calorie_result, null);
        TextView tvPopupCalorieResult = popupView.findViewById(R.id.tv_calorie_result);
        // So sánh và hiển thị kết quả
        if (totalCalories < averageCalories) {
            tvPopupCalorieResult.setText("Bạn cần nhiều calo hơn hôm nay.");
        } else if (totalCalories > averageCalories) {
            tvPopupCalorieResult.setText("Bạn đã vượt quá lượng calo cho hôm nay.");
        } else {
            tvPopupCalorieResult.setText("Bạn đang theo đúng lượng calo cho hôm nay.");
        }
        //tvPersonTotalCalories.setText("Calories per day: " + averageCalories);

        String totalPersonTotalCaloriesText = getString(R.string.label_person_total_calories, averageCalories);
        tvPersonTotalCalories.setText(totalPersonTotalCaloriesText);


        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss());

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
        //tvCalorieResult.setVisibility(View.VISIBLE); // Hiện TextView sau khi tính toán
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).foodDatabaseReference().removeEventListener(mValueEventListener);
        }
    }

}
