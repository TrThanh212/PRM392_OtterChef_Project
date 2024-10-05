package com.app.cookbook.activity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.adapter.RatingAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.ActivityRatingReviewBinding;
import com.app.cookbook.model.Rating;
import com.app.cookbook.model.User;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RatingReviewActivity extends BaseActivity {

    private ActivityRatingReviewBinding mBinding;
    private long mFoodId;
    private List<Rating> ratingList;
    private RatingAdapter ratingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve the saved language preference
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(this, languageCode);

        super.onCreate(savedInstanceState);
        mBinding = ActivityRatingReviewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        initRecyclerView();
        loadExistingRatings();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mFoodId = bundle.getLong(Constant.FOOD_ID);
    }

    private void initToolbar() {
        mBinding.layoutToolbar.imgToolbar.setOnClickListener(view -> finish());
        mBinding.layoutToolbar.tvToolbarTitle.setText(getString(R.string.label_rate_review));
    }

    private void initView() {
        mBinding.ratingbar.setRating(5f);
        mBinding.tvSendReview.setOnClickListener(v -> {
            float rate = mBinding.ratingbar.getRating();
            String review = mBinding.edtReview.getText().toString().trim();
            User user = DataStoreManager.getUser();
            String userEmail = user != null ? user.getEmail() : "Unknown";
            long timestamp = System.currentTimeMillis();
            Rating rating = new Rating(userEmail, review, Double.parseDouble(String.valueOf(rate)), timestamp);
            sendRatingFood(rating);
        });
    }

    private void initRecyclerView() {
        ratingList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(ratingList);
        mBinding.recyclerViewRatings.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerViewRatings.setAdapter(ratingAdapter);
    }
    private void loadExistingRatings() {
        showProgressDialog(true);
        MyApplication.get(this).ratingFoodDatabaseReference(mFoodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ratingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rating rating = snapshot.getValue(Rating.class);
                    if (rating != null) {
                        ratingList.add(rating);
                    }
                }
                ratingAdapter.notifyDataSetChanged();
                showProgressDialog(false);
                // Sort the list by timestamp in descending order
                Collections.sort(ratingList, new Comparator<Rating>() {
                    @Override
                    public int compare(Rating r1, Rating r2) {
                        return Long.compare(r2.getTimestamp(), r1.getTimestamp());
                    }
                });
                ratingAdapter.notifyDataSetChanged();
                showProgressDialog(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showProgressDialog(false);
                GlobalFunction.showToastMessage(RatingReviewActivity.this, getString(R.string.msg_login_error));
            }
        });
      
    }
    private void sendRatingFood(Rating rating) {
        User user = DataStoreManager.getUser();
        if (user != null) {
            rating.setUserEmail(user.getEmail());
        }
        rating.setTimestamp(System.currentTimeMillis());

        // Generate a unique key for each review
        String reviewKey = MyApplication.get(this).ratingFoodDatabaseReference(mFoodId).push().getKey();
        if (reviewKey != null) {
            MyApplication.get(this).ratingFoodDatabaseReference(mFoodId)
                    .child(reviewKey)
                    .setValue(rating, (error, ref) -> {
                        GlobalFunction.showToastMessage(RatingReviewActivity.this,
                                getString(R.string.msg_send_review_success));
                        mBinding.ratingbar.setRating(5f);
                        mBinding.edtReview.setText("");
                        GlobalFunction.hideSoftKeyboard(RatingReviewActivity.this);
                        updateRatingList(rating);
                    });
        }
    }

    private void updateRatingList(Rating rating) {
        ratingList.add(rating);
        // Sort the list by timestamp in descending order
        Collections.sort(ratingList, (r2, r1) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));
        ratingAdapter.notifyDataSetChanged();
    }

}