package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.app.cookbook.R;
import com.app.cookbook.databinding.ActivityChangePasswordBinding;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initListener();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_change_password));
    }

    private void initListener() {
        mBinding.edtOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mBinding.edtOldPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mBinding.edtOldPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }
            }
        });
        mBinding.edtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mBinding.edtNewPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mBinding.edtNewPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }
            }
        });
        mBinding.edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mBinding.edtConfirmPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mBinding.edtConfirmPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }
            }
        });

        mBinding.btnChangePassword.setOnClickListener(v -> onClickValidateChangePassword());
    }

    private void onClickValidateChangePassword() {
        String strOldPassword = mBinding.edtOldPassword.getText().toString().trim();
        String strNewPassword = mBinding.edtNewPassword.getText().toString().trim();
        String strConfirmPassword = mBinding.edtConfirmPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strOldPassword)) {
            showToastMessage(this, getString(R.string.msg_old_password_require));
        } else if (StringUtil.isEmpty(strNewPassword)) {
            showToastMessage(this, getString(R.string.msg_new_password_require));
        } else if (StringUtil.isEmpty(strConfirmPassword)) {
            showToastMessage(this, getString(R.string.msg_confirm_password_require));
        } else if (!DataStoreManager.getUser().getPassword().equals(strOldPassword)) {
            showToastMessage(this, getString(R.string.msg_old_password_invalid));
        } else if (!strNewPassword.equals(strConfirmPassword)) {
            showToastMessage(this, getString(R.string.msg_confirm_password_invalid));
        } else if (strOldPassword.equals(strNewPassword)) {
            showToastMessage(this, getString(R.string.msg_new_password_invalid));
        } else {
            changePassword(strNewPassword);
        }
    }

    private void changePassword(String newPassword) {
        showProgressDialog(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        showToastMessage(this, getString(R.string.msg_change_password_successfully));
                        User userLogin = DataStoreManager.getUser();
                        userLogin.setPassword(newPassword);
                        DataStoreManager.setUser(userLogin);
                        mBinding.edtOldPassword.setText("");
                        mBinding.edtNewPassword.setText("");
                        mBinding.edtConfirmPassword.setText("");
                    } else {
                        showToastMessage(this, getString(R.string.msg_change_password_fail));
                    }
                });
    }
}