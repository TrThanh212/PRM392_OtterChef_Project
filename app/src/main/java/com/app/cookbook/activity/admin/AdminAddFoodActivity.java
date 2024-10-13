package com.app.cookbook.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.adapter.admin.AdminSelectAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminAddFoodBinding;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.model.SelectObject;
import com.app.cookbook.utils.CustomToast;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddFoodActivity extends BaseActivity {

    private ActivityAdminAddFoodBinding binding;
    private boolean isUpdate;
    private Food mFood;
    private SelectObject mCategorySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditFood());
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
        } else {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_add_food));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
        loadListCategory();
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
            map.put("calories", Double.parseDouble(strlCalories)); // Add calories

            MyApplication.get(this).foodDatabaseReference()
                    .child(String.valueOf(mFood.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        CustomToast.showToast(AdminAddFoodActivity.this, getString(R.string.msg_edit_food_success));
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add food
        showProgressDialog(true);
        long foodId = System.currentTimeMillis();
        Food food = new Food(foodId, strName, strImage, strUrl, binding.chbFeatured.isChecked(),
                mCategorySelected.getId(), mCategorySelected.getName(), Double.parseDouble(strlCalories)); // Add calories
        MyApplication.get(this).foodDatabaseReference()
                .child(String.valueOf(foodId)).setValue(food, (error, ref) -> {
                    showProgressDialog(false);
                    binding.edtName.setText("");
                    binding.edtImage.setText("");
                    binding.edtLink.setText("");
                    binding.edtCalo.setText("");
                    binding.chbFeatured.setChecked(false);
                    binding.spnCategory.setSelection(0);
                    GlobalFunction.hideSoftKeyboard(this);
                    CustomToast.showToast(this, getString(R.string.msg_add_food_success));
                });
    }
}