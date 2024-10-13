package com.app.cookbook.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.app.cookbook.R;
import com.app.cookbook.databinding.ActivityForgotPasswordBinding;
import com.app.cookbook.utils.CustomToast;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends BaseActivity {

    private ActivityForgotPasswordBinding mActivityForgotPasswordBinding;
    private boolean isEnableButtonResetPassword;

    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mActivityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(mActivityForgotPasswordBinding.getRoot());

        initToolbar();
        initListener();
    }

    private void initToolbar() {
        mActivityForgotPasswordBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mActivityForgotPasswordBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_reset_password));
    }

    private void initListener() {
        mActivityForgotPasswordBinding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityForgotPasswordBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityForgotPasswordBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                if (!StringUtil.isEmpty(s.toString())) {
                    isEnableButtonResetPassword = true;
                    mActivityForgotPasswordBinding.btnResetPassword.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonResetPassword = false;
                    mActivityForgotPasswordBinding.btnResetPassword.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityForgotPasswordBinding.btnResetPassword.setOnClickListener(v -> onClickValidateResetPassword());
    }

    private void onClickValidateResetPassword() {
        if (!isEnableButtonResetPassword) return;
        String strEmail = mActivityForgotPasswordBinding.edtEmail.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            CustomToast.showToast(ForgotPasswordActivity.this, getString(R.string.msg_email_require));
        } else if (!StringUtil.isValidEmail(strEmail)) {

            CustomToast.showToast(ForgotPasswordActivity.this, getString(R.string.msg_email_invalid));
        } else {
            resetPassword(strEmail);
        }
    }

    private void resetPassword(String email) {
        showProgressDialog(true);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        CustomToast.showToast(ForgotPasswordActivity.this, getString(R.string.msg_reset_password_successfully));
                        mActivityForgotPasswordBinding.edtEmail.setText("");
                    } else {
                        CustomToast.showToast(ForgotPasswordActivity.this, getString(R.string.msg_reset_password_fail));
                    }
                }).addOnFailureListener(e -> {
                    showProgressDialog(false);
                    CustomToast.showToast(ForgotPasswordActivity.this, getString(R.string.msg_reset_password_fail));
                    e.printStackTrace(); // Log lỗi
                });
    }


}