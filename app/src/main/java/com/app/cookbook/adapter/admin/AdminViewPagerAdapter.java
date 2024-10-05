package com.app.cookbook.adapter.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.cookbook.fragment.AdminAccountFragment;
import com.app.cookbook.fragment.AdminCategoryFragment;
import com.app.cookbook.fragment.AdminFeedbackFragment;
import com.app.cookbook.fragment.AdminFoodFragment;
import com.app.cookbook.fragment.AdminRequireFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AdminFoodFragment();

            case 2:
                return new AdminRequireFragment();

            case 3:
                return new AdminFeedbackFragment();

            case 4:
                return new AdminAccountFragment();

            default:
                return new AdminCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
