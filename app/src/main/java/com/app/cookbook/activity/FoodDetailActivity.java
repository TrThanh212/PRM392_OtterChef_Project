package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityFoodDetailBinding;
import com.app.cookbook.model.Food;
import com.app.cookbook.model.UserInfo;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodDetailActivity extends BaseActivity {

    private ActivityFoodDetailBinding mBinding;
    private long mFoodId;
    private Food mFood;
    private ValueEventListener mFoodDetailValueEventListener;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadFoodDetailFromFirebase();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mFoodId = bundle.getLong(Constant.FOOD_ID);
    }

    private void loadFoodDetailFromFirebase() {
        showProgressDialog(true);
        mFoodDetailValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                mFood = snapshot.getValue(Food.class);
                if (mFood == null) return;
                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(FoodDetailActivity.this, getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).foodDetailDatabaseReference(mFoodId).addValueEventListener(mFoodDetailValueEventListener);
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_food_detail));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        WebSettings webSettings = mBinding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setSupportZoom(false);
        webSettings.setSaveFormData(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setUseWideViewPort(true);
        mBinding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showProgressDialog(true);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showProgressDialog(false);
                isLoaded = true;
                addHistory();
                changeCountViewFood();
            }
        });

    }

    private void initData() {
        if (!isLoaded) {
            loadWebViewFoodDetail();
        }
        mBinding.tvRate.setText(String.valueOf(mFood.getRate()));
        String strCountReview = "(" + mFood.getCountReviews() + ")";
        mBinding.tvCountReview.setText(strCountReview);
        mBinding.layoutRatingAndReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.FOOD_ID, mFoodId);
            GlobalFunction.startActivity(FoodDetailActivity.this, RatingReviewActivity.class, bundle);
        });
    }


    private void loadWebViewFoodDetail() {
        if (mFood == null || StringUtil.isEmpty(mFood.getUrl())) return;
        mBinding.webView.loadUrl(mFood.getUrl());
    }

    private void addHistory() {
        if (mFood == null || isHistory(mFood)) return;
        String userEmail = DataStoreManager.getUser().getEmail();
        UserInfo userInfo = new UserInfo(System.currentTimeMillis(), userEmail);
        MyApplication.get(this).foodDatabaseReference()
                .child(String.valueOf(mFood.getId()))
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

    private void changeCountViewFood() {
        MyApplication.get(this).countFoodDatabaseReference(mFoodId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCount = snapshot.getValue(Integer.class);
                        int newCount = 1;
                        if (currentCount != null) {
                            newCount = currentCount + 1;
                        }
                        MyApplication.get(FoodDetailActivity.this)
                                .countFoodDatabaseReference(mFoodId).removeEventListener(this);
                        MyApplication.get(FoodDetailActivity.this)
                                .countFoodDatabaseReference(mFoodId).setValue(newCount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
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
