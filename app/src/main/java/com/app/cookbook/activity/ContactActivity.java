package com.app.cookbook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import com.app.cookbook.R;
import com.app.cookbook.adapter.ContactAdapter;
import com.app.cookbook.constant.AboutUsConfig;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityContactBinding;
import com.app.cookbook.model.Contact;
import com.app.cookbook.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends BaseActivity {

    private ActivityContactBinding mBinding;
    private ContactAdapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initToolbar();
        initData();
        initListener();
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_contact));
    }

    private void initData() {
        mBinding.tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        mBinding.tvAboutUsContent.setText(AboutUsConfig.ABOUT_US_CONTENT);
        mBinding.tvAboutUsWebsite.setText(AboutUsConfig.ABOUT_US_WEBSITE_TITLE);

        mContactAdapter = new ContactAdapter(this, loadListContact(),
                () -> GlobalFunction.callPhoneNumber(this));
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mBinding.rcvData.setNestedScrollingEnabled(false);
        mBinding.rcvData.setFocusable(false);
        mBinding.rcvData.setLayoutManager(layoutManager);
        mBinding.rcvData.setAdapter(mContactAdapter);
    }

    private void initListener() {
        mBinding.layoutWebsite.setOnClickListener(view
                -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.WEBSITE))));
    }

    public List<Contact> loadListContact() {
        List<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new Contact(Contact.FACEBOOK, R.drawable.ic_facebook));
        contactArrayList.add(new Contact(Contact.HOTLINE, R.drawable.ic_hotline));
        contactArrayList.add(new Contact(Contact.GMAIL, R.drawable.ic_gmail));
        contactArrayList.add(new Contact(Contact.SKYPE, R.drawable.ic_skype));
        contactArrayList.add(new Contact(Contact.YOUTUBE, R.drawable.ic_youtube));
        contactArrayList.add(new Contact(Contact.ZALO, R.drawable.ic_zalo));

        return contactArrayList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactAdapter.release();
    }
}
