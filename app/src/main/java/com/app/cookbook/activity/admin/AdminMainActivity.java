package com.app.cookbook.activity.admin;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.cookbook.R;
import com.app.cookbook.activity.BaseActivity;
import com.app.cookbook.adapter.admin.AdminViewPagerAdapter;
import com.app.cookbook.databinding.ActivityAdminMainBinding;
import com.app.cookbook.utils.LocaleHelper;

import java.util.Locale;

public class AdminMainActivity extends BaseActivity {

    private ActivityAdminMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);
        super.onCreate(savedInstanceState);
        mBinding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setToolBar();

        mBinding.viewpager2.setUserInputEnabled(false);
        mBinding.viewpager2.setOffscreenPageLimit(5);
        AdminViewPagerAdapter adminViewPagerAdapter = new AdminViewPagerAdapter(this);
        mBinding.viewpager2.setAdapter(adminViewPagerAdapter);

        mBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_category).setChecked(true);
                        break;

                    case 1:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_food).setChecked(true);
                        break;

                    case 2:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_require).setChecked(true);
                        break;

                    case 3:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_feedback).setChecked(true);
                        break;

                    case 4:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_account).setChecked(true);
                        break;
                }
            }
        });

        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_category) {
                mBinding.viewpager2.setCurrentItem(0);
            } else if (id == R.id.nav_food) {
                mBinding.viewpager2.setCurrentItem(1);
            } else if (id == R.id.nav_require) {
                mBinding.viewpager2.setCurrentItem(2);
            }  else if (id == R.id.nav_feedback) {
                mBinding.viewpager2.setCurrentItem(3);
            } else if (id == R.id.nav_account) {
                mBinding.viewpager2.setCurrentItem(4);
            }
            return true;
        });

    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finishAffinity())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    public void setToolBar() {
        mBinding.layoutToolbar.imgToolbar.setVisibility(View.GONE);
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.app_name));
    }
}