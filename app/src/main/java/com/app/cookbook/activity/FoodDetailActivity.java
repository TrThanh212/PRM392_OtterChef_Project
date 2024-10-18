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
import com.app.cookbook.databinding.ActivityFoodDetailBinding;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FoodDetailActivity extends BaseActivity {

    private ActivityFoodDetailBinding mBinding;
    private long mFoodId;
    private Food mFood;
    private ValueEventListener mFoodDetailValueEventListener;

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
                loadWebViewFoodDetail();
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showProgressDialog(false);
            }
        });
    }

    private void loadWebViewFoodDetail() {
        if (mFood == null || StringUtil.isEmpty(mFood.getUrl())) return;
        mBinding.webView.loadUrl(mFood.getUrl());
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