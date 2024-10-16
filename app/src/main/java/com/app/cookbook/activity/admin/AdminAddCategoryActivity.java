package com.app.cookbook.activity.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityAdminAddCategoryBinding;
import com.app.cookbook.model.Category;
import com.app.cookbook.utils.CustomToast;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddCategoryActivity extends BaseActivity {

    private ActivityAdminAddCategoryBinding binding;
    private boolean isUpdate;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditCategory());
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mCategory = (Category) bundleReceived.get(Constant.OBJECT_CATEGORY);
        }
    }

    private void initToolbar() {
        binding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
    }

    private void initView() {
        if (isUpdate) {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_update_category));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mCategory.getName());
            binding.edtImage.setText(mCategory.getImage());
        } else {
            binding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_add_category));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditCategory() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            //Toast.makeText(this, getString(R.string.msg_input_name_require), Toast.LENGTH_SHORT).show();
            CustomToast.showToast(this, getString(R.string.msg_input_name_require));
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            CustomToast.showToast(this, getString(R.string.msg_input_image_require));
            return;
        }

        // Update category
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("image", strImage);

            MyApplication.get(this).categoryDatabaseReference()
                    .child(String.valueOf(mCategory.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false);
               // Toast.makeText(AdminAddCategoryActivity.this, getString(R.string.msg_edit_category_success), Toast.LENGTH_SHORT).show();
                CustomToast.showToast(AdminAddCategoryActivity.this, getString(R.string.msg_edit_category_success));
                GlobalFunction.hideSoftKeyboard(this);
            });
            return;
        }

        // Add category
        showProgressDialog(true);
        long categoryId = System.currentTimeMillis();
        Category category = new Category(categoryId, strName, strImage);
        MyApplication.get(this).categoryDatabaseReference()
                .child(String.valueOf(categoryId)).setValue(category, (error, ref) -> {
            showProgressDialog(false);
            binding.edtName.setText("");
            binding.edtImage.setText("");
            GlobalFunction.hideSoftKeyboard(this);
            //Toast.makeText(this, getString(R.string.msg_add_category_success), Toast.LENGTH_SHORT).show();
            CustomToast.showToast(this, getString(R.string.msg_add_category_success));
        });
    }
}