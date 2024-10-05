package com.app.cookbook.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.app.cookbook.R;
import com.app.cookbook.activity.admin.AdminMainActivity;
import com.app.cookbook.constant.AboutUsConfig;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivitySplashBinding;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding mActivitySplashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mActivitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mActivitySplashBinding.getRoot());

        initUi();

        Handler handler = new Handler();
        handler.postDelayed(this::goToActivity, 3500);
    }

    private void initUi() {
        mActivitySplashBinding.tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        mActivitySplashBinding.tvAboutUsSlogan.setText(AboutUsConfig.ABOUT_US_SLOGAN);

        // Load animations
        Animation imageAnimation = AnimationUtils.loadAnimation(this, R.anim.image_animation);
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_animation);

        // Start animations
        mActivitySplashBinding.imageView.startAnimation(imageAnimation);
        mActivitySplashBinding.layoutText.startAnimation(textAnimation);
    }

    private void goToActivity() {
        if (DataStoreManager.getUser() != null
                && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            if (DataStoreManager.getUser().isAdmin()) {
                GlobalFunction.startActivity(this, AdminMainActivity.class);
            } else {
                GlobalFunction.startActivity(this, MainActivity.class);
            }
        } else {
            GlobalFunction.startActivity(this, LoginActivity.class);
        }
        finish();
    }
}
