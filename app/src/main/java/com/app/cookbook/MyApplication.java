package com.app.cookbook;

import android.app.Application;
import android.content.Context;

import com.app.cookbook.prefs.DataStoreManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    public static final String FIREBASE_URL = "https://prm392-cookbook-database-default-rtdb.firebaseio.com";
    private FirebaseDatabase mFirebaseDatabase;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        DataStoreManager.init(getApplicationContext());
    }

    public DatabaseReference categoryDatabaseReference() {
        return mFirebaseDatabase.getReference("/category");
    }

    public DatabaseReference foodDatabaseReference() {
        return mFirebaseDatabase.getReference("/food");
    }

    public DatabaseReference feedbackDatabaseReference() {
        return mFirebaseDatabase.getReference("/feedback");
    }

    public DatabaseReference requestFoodDatabaseReference() {
        return mFirebaseDatabase.getReference("/request");
    }

    public DatabaseReference foodDetailDatabaseReference(long foodId) {
        return mFirebaseDatabase.getReference("food/" + foodId);
    }

    public DatabaseReference ratingFoodDatabaseReference(long foodId) {
        return mFirebaseDatabase.getReference("/food/" + foodId + "/rating");
    }

    public DatabaseReference countFoodDatabaseReference(long foodId) {
        return mFirebaseDatabase.getReference("/food/" + foodId + "/count");
    }
    public DatabaseReference userDatabaseReference() {
        return mFirebaseDatabase.getReference("/user");
    }
}
