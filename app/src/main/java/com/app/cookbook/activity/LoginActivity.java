package com.app.cookbook.activity;

import static com.app.cookbook.constant.GlobalFunction.showToastMessage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.admin.AdminMainActivity;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityLogInBinding;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {

    private ActivityLogInBinding mActivityLogInBinding;
    private boolean isEnableButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mActivityLogInBinding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(mActivityLogInBinding.getRoot());

        initListener();


    }

    private void initListener() {

        mActivityLogInBinding.edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityLogInBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityLogInBinding.edtEmail.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                String strPassword = mActivityLogInBinding.edtPassword.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strPassword)) {
                    isEnableButtonLogin = true;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonLogin = false;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityLogInBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityLogInBinding.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_main);
                } else {
                    mActivityLogInBinding.edtPassword.setBackgroundResource(R.drawable.bg_white_corner_30_border_gray);
                }

                String strEmail = mActivityLogInBinding.edtEmail.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strEmail)) {
                    isEnableButtonLogin = true;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_enable_corner_10);
                } else {
                    isEnableButtonLogin = false;
                    mActivityLogInBinding.btnLogin.setBackgroundResource(R.drawable.bg_button_disable_corner_10);
                }
            }
        });

        mActivityLogInBinding.layoutRegister.setOnClickListener(
                v -> GlobalFunction.startActivity(this, RegisterActivity.class));

        mActivityLogInBinding.btnLogin.setOnClickListener(v -> onClickValidateLogin());
        mActivityLogInBinding.tvForgotPassword.setOnClickListener(
                v -> GlobalFunction.startActivity(this, ForgotPasswordActivity.class));

        mActivityLogInBinding.btnChangeLanguage.setOnClickListener(v -> {
            // Toggle between languages (e.g., "en" and "vi")
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String currentLanguage = prefs.getString("language", "vi");
            String newLanguage = "vi".equals(currentLanguage) ? "en" : "vi";
            LocaleHelper.setLocale(this, newLanguage);
            recreate(); // Restart the activity to apply the new language
        });
    }

    private void onClickValidateLogin() {
        if (!isEnableButtonLogin) return;

        String strEmail = mActivityLogInBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivityLogInBinding.edtPassword.getText().toString().trim();

        if (StringUtil.isEmpty(strEmail)) {
            showToastMessage(this, getString(R.string.msg_email_require));
        } else if (StringUtil.isEmpty(strPassword)) {
            showToastMessage(this, getString(R.string.msg_password_require));
        } else {
            loginUserFirebase(strEmail, strPassword);
        }
    }


    private void loginUserFirebase(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            showProgressDialog(false);
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        // Email is verified, proceed with the login flow
                        retrieveUserData( email, password);
                    } else {
                        // Email is not verified, show a message and prompt the user to verify their email
                        showToastMessage(this, getString(R.string.msg_email_not_verified));
                        user.sendEmailVerification()
                                .addOnCompleteListener(verificationTask -> {
                                    if (verificationTask.isSuccessful()) {
                                        showToastMessage(this, getString(R.string.msg_verification_email_sent));
                                    } else {
                                        showToastMessage(this, getString(R.string.msg_verification_email_failed));
                                    }
                                });
                    }
                }  else {
                    showToastMessage(this, getString(R.string.msg_login_error));
                }
            } else {
                showToastMessage(this, getString(R.string.msg_login_error));
            }
        }).addOnFailureListener(e -> {
            showProgressDialog(false);
            showToastMessage(this, getString(R.string.msg_login_error));
            e.printStackTrace(); // Log the error
        });
    }
    private void retrieveUserData(String email, String password) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("user").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        Boolean isAdmin = userSnapshot.child("admin").getValue(Boolean.class);
                        if (isAdmin == null) {
                            isAdmin = false; // Default to false if admin status is not found
                        }

                        User userObject = new User(email, password);
                        userObject.setAdmin(isAdmin); // Set admin status

                        DataStoreManager.setUser(userObject);
                        goToMainActivity();
                        return;
                    }
                } else {
                    showToastMessage(LoginActivity.this, getString(R.string.msg_login_error));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToastMessage(LoginActivity.this, getString(R.string.msg_login_error));
                databaseError.toException().printStackTrace(); // Log the error
            }
        });
    }
    private void goToMainActivity() {
        if (DataStoreManager.getUser().isAdmin()) {
            GlobalFunction.startActivity(LoginActivity.this, AdminMainActivity.class);
        } else {
            GlobalFunction.startActivity(LoginActivity.this, MainActivity.class);
        }
        finishAffinity();
    }
}