package com.app.cookbook.activity.admin;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.databinding.ActivityAdminFoodDetailBinding;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;

public class AdminFoodDetailActivity extends BaseActivity {

    private ActivityAdminFoodDetailBinding mBinding;
    private Food mFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        mBinding = ActivityAdminFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadWebViewFoodDetail();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            mFood = (Food) bundleReceived.get(Constant.OBJECT_FOOD);
        }
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
            }
        });

    }

    private void loadWebViewFoodDetail() {
        if (mFood == null || StringUtil.isEmpty(mFood.getUrl())) return;
        mBinding.webView.loadUrl(mFood.getUrl());
    }
}
