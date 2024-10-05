package com.app.cookbook.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityFeedbackBinding;
import com.app.cookbook.model.Feedback;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.app.cookbook.utils.StringUtil;

public class FeedbackActivity extends BaseActivity {

    private ActivityFeedbackBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initData();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_feedback));
    }

    private void initData() {
        mBinding.edtEmail.setText(DataStoreManager.getUser().getEmail());
        mBinding.tvSendFeedback.setOnClickListener(v -> onClickSendFeedback());
    }

    private void onClickSendFeedback() {
        String strName = mBinding.edtName.getText().toString();
        String strPhone = mBinding.edtPhone.getText().toString();
        String strEmail = mBinding.edtEmail.getText().toString();
        String strComment = mBinding.edtComment.getText().toString();

        if (StringUtil.isEmpty(strName)) {
            GlobalFunction.showToastMessage(this, getString(R.string.msg_name_require));
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFunction.showToastMessage(this, getString(R.string.msg_comment_require));
        } else {
            showProgressDialog(true);
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment);
            MyApplication.get(this).feedbackDatabaseReference()
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(feedback, (databaseError, databaseReference) -> {
                        showProgressDialog(false);
                        sendFeedbackSuccess();
                    });
        }
    }

    public void sendFeedbackSuccess() {
        GlobalFunction.hideSoftKeyboard(this);
        GlobalFunction.showToastMessage(this, getString(R.string.msg_send_feedback_success));
        mBinding.edtName.setText("");
        mBinding.edtPhone.setText("");
        mBinding.edtComment.setText("");
    }
}
