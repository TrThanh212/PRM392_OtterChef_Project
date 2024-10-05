package com.app.cookbook.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.cookbook.R;
import com.app.cookbook.activity.ChangePasswordActivity;
import com.app.cookbook.activity.LoginActivity;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.FragmentAdminAccountBinding;
import com.app.cookbook.prefs.DataStoreManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class AdminAccountFragment extends Fragment {

    private FragmentAdminAccountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminAccountBinding.inflate(inflater, container, false);
        initUi();
        return binding.getRoot();


    }

    private void initUi() {
        binding.tvEmail.setText(DataStoreManager.getUser().getEmail());
        binding.tvChangePassword.setOnClickListener(v -> onClickChangePassword());
        binding.tvSignOut.setOnClickListener(v -> onClickSignOut());


        // Khai báo Switch
        Switch switchLanguage = binding.switchLanguage;
        // Thiết lập OnCheckedChangeListener
        if (switchLanguage != null) {
            switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String languageCode = isChecked ? "vi" : "en";
                saveLanguagePreference(languageCode);
                changeLanguage(languageCode);

                // Change the icon based on the switch state
                int drawableId = isChecked ? R.drawable.ic_language_vn : R.drawable.ic_language_en;
                Drawable drawable = getResources().getDrawable(drawableId);
                int width = (int) (drawable.getIntrinsicWidth() * 0.25); // Scale to 25%
                int height = (int) (drawable.getIntrinsicHeight() * 0.25); // Scale to 25%
                drawable.setBounds(0, 0, width, height);
                switchLanguage.setCompoundDrawables(drawable, null, null, null);
            });
            // Set default to the saved language
            SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String savedLanguage = prefs.getString("language", "vi"); // Default to Vietnamese
            switchLanguage.setChecked(savedLanguage.equals("vi"));
            // Set the initial icon based on the saved language
            int initialDrawableId = savedLanguage.equals("vi") ? R.drawable.ic_language_vn : R.drawable.ic_language_en;
            Drawable initialDrawable = getResources().getDrawable(initialDrawableId);
            int initialWidth = (int) (initialDrawable.getIntrinsicWidth() * 0.25); // Scale to 25%
            int initialHeight = (int) (initialDrawable.getIntrinsicHeight() * 0.25); // Scale to 25%
            initialDrawable.setBounds(0, 0, initialWidth, initialHeight);
            switchLanguage.setCompoundDrawables(initialDrawable, null, null, null);
        } else {
            Log.e("AdminAccountFragment", "Switch is null");
        }
    }
    // Phương thức để thay đổi ngôn ngữ
    private void changeLanguage(String languageCode) {
        Locale currentLocale = getResources().getConfiguration().locale;
        if (!currentLocale.getLanguage().equals(languageCode)) {
            Locale locale = new Locale(languageCode);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.setLocale(locale);
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            if (getActivity() != null) {
                getActivity().recreate(); // Reload the activity to apply the new language
            }
        }
    }

    private void saveLanguagePreference(String languageCode) {
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageCode);
        editor.apply();
    }
    private void onClickChangePassword() {
        GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class);
    }

    private void onClickSignOut() {
        if (getActivity() == null) return;
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), LoginActivity.class);
        getActivity().finishAffinity();
    }
}
