package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.FoodAdapter;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivitySearchBinding;
import com.app.cookbook.listener.IOnClickFoodListener;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding mBinding;
    private List<Food> mListFood;
    private FoodAdapter mFoodAdapter;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initUi();
        initListener();
        getListFoodFromFirebase("");
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_search));
    }

    private void initUi() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mBinding.rcvSearchResult.setLayoutManager(linearLayoutManager);
        mListFood = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mListFood, new IOnClickFoodListener() {
            @Override
            public void onClickItemFood(Food food) {
                GlobalFunction.goToFoodDetail(SearchActivity.this, food.getId());
            }

            @Override
            public void onClickFavoriteFood(Food food, boolean favorite) {
                GlobalFunction.onClickFavoriteFood(SearchActivity.this, food, favorite);
            }

            @Override
            public void onClickMyDishFood(Food food, boolean dish) {
                GlobalFunction.onClickMyDishFood(SearchActivity.this, food, dish);
            }

            @Override
            public void onClickCategoryOfFood(Category category) {
                GlobalFunction.goToFoodByCategory(SearchActivity.this, category);
            }
        });
        mBinding.rcvSearchResult.setAdapter(mFoodAdapter);
    }

    private void initListener() {
        mBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    getListFoodFromFirebase("");
                }
            }
        });

        mBinding.imgSearch.setOnClickListener(view -> searchFood());

        mBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListFoodFromFirebase(String key) {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resetListFoodData();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Food food = dataSnapshot.getValue(Food.class);
                    if (food == null) return;
                    if (StringUtil.isEmpty(key)) {
                        mListFood.add(0, food);
                    } else {
                        if (GlobalFunction.getTextSearch(food.getName()).toLowerCase().trim()
                                .contains(GlobalFunction.getTextSearch(key).toLowerCase().trim())) {
                            mListFood.add(0, food);
                        }
                    }
                }
                if (mFoodAdapter != null) mFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(SearchActivity.this,
                        getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).foodDatabaseReference().addValueEventListener(mValueEventListener);
    }

    private void resetListFoodData() {
        if (mListFood == null) {
            mListFood = new ArrayList<>();
        } else {
            mListFood.clear();
        }
    }

    private void searchFood() {
        String strKey = mBinding.edtSearchName.getText().toString().trim();
        if (mValueEventListener != null) {
            MyApplication.get(this).foodDatabaseReference().removeEventListener(mValueEventListener);
        }
        getListFoodFromFirebase(strKey);
        GlobalFunction.hideSoftKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).foodDatabaseReference().removeEventListener(mValueEventListener);
        }
    }
}
