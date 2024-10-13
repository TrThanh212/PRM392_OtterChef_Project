package com.app.cookbook.constant;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.app.cookbook.MyApplication;
import com.app.cookbook.activity.FoodByCategoryActivity;
import com.app.cookbook.activity.FoodDetailActivity;
import com.app.cookbook.model.Category;
import com.app.cookbook.model.Food;
import com.app.cookbook.model.UserInfo;
import com.app.cookbook.prefs.DataStoreManager;
import com.app.cookbook.utils.CustomToast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GlobalFunction {

    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static void onClickOpenGmail(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", AboutUsConfig.GMAIL, null));
        context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    public static void onClickOpenSkype(Context context) {
        try {
            Uri skypeUri = Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat");
            context.getPackageManager().getPackageInfo("com.skype.raider", 0);
            Intent skypeIntent = new Intent(Intent.ACTION_VIEW, skypeUri);
            skypeIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
            context.startActivity(skypeIntent);
        } catch (Exception e) {
            openSkypeWebView(context);
        }
    }

    private static void openSkypeWebView(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")));
        } catch (Exception exception) {
            String skypePackageName = "com.skype.raider";
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + skypePackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + skypePackageName)));
            }
        }
    }

    public static void onClickOpenFacebook(Context context) {
        Intent intent;
        try {
            String urlFacebook = AboutUsConfig.PAGE_FACEBOOK;
            PackageManager packageManager = context.getPackageManager();
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlFacebook = "fb://facewebmodal/f?href=" + AboutUsConfig.LINK_FACEBOOK;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_FACEBOOK));
        }
        context.startActivity(intent);
    }

    public static void onClickOpenYoutubeChannel(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_YOUTUBE)));
    }

    public static void onClickOpenZalo(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.ZALO_LINK)));
    }

    public static void callPhoneNumber(Activity activity) {
        try {
            if (ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CALL_PHONE}, 101);
                return;
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER));
            activity.startActivity(callIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showToastMessage(Context context, String message) {
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        CustomToast.showToast(context, message);
    }

    public static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void goToFoodDetail(Context context, long foodId) {
        Bundle bundle = new Bundle();
        bundle.putLong(Constant.FOOD_ID, foodId);
        startActivity(context, FoodDetailActivity.class, bundle);
    }

    public static void goToFoodByCategory(Context context, Category category) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_CATEGORY, category);
        startActivity(context, FoodByCategoryActivity.class, bundle);
    }

    public static void onClickFavoriteFood(Context context, Food food, boolean isFavorite) {
        if (context == null) return;
        if (isFavorite) {
            String userEmail = DataStoreManager.getUser().getEmail();
            UserInfo userInfo = new UserInfo(System.currentTimeMillis(), userEmail);
            MyApplication.get(context).foodDatabaseReference()
                    .child(String.valueOf(food.getId()))
                    .child("favorite")
                    .child(String.valueOf(userInfo.getId()))
                    .setValue(userInfo);
        } else {
            UserInfo userInfo = getUserFavoriteFood(food);
            if (userInfo != null) {
                MyApplication.get(context).foodDatabaseReference()
                        .child(String.valueOf(food.getId()))
                        .child("favorite")
                        .child(String.valueOf(userInfo.getId()))
                        .removeValue();
            }
        }
    }

    private static UserInfo getUserFavoriteFood(Food food) {
        UserInfo userInfo = null;
        if (food.getFavorite() == null || food.getFavorite().isEmpty()) return null;
        List<UserInfo> listUsersFavorite = new ArrayList<>(food.getFavorite().values());
        for (UserInfo userObject : listUsersFavorite) {
            if (DataStoreManager.getUser().getEmail().equals(userObject.getEmailUser())) {
                userInfo = userObject;
                break;
            }
        }
        return userInfo;
    }

    public static boolean isFavoriteFood(Food food) {
        if (food.getFavorite() == null || food.getFavorite().isEmpty()) return false;
        //tạo ds user từ value của favorite
        List<UserInfo> listUsersFavorite = new ArrayList<>(food.getFavorite().values());
        if (listUsersFavorite.isEmpty()) return false;
        for (UserInfo userInfo : listUsersFavorite) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }
    public static void onClickMyDishFood(Context context, Food food, boolean isMyDish) {
        if (context == null) return;
        if (isMyDish) {
            String userEmail = DataStoreManager.getUser().getEmail();
            UserInfo userInfo = new UserInfo(System.currentTimeMillis(), userEmail);
            MyApplication.get(context).foodDatabaseReference()
                    .child(String.valueOf(food.getId()))
                    .child("mydish")
                    .child(String.valueOf(userInfo.getId()))
                    .setValue(userInfo);
        } else {
            UserInfo userInfo = getUserMyDishFood(food);
            if (userInfo != null) {
                MyApplication.get(context).foodDatabaseReference()
                        .child(String.valueOf(food.getId()))
                        .child("mydish")
                        .child(String.valueOf(userInfo.getId()))
                        .removeValue();
            }
        }
    }
    private static UserInfo getUserMyDishFood(Food food) {
        UserInfo userInfo = null;
        if (food.getMydish() == null || food.getMydish().isEmpty()) return null;
        List<UserInfo> listUsersMyDish = new ArrayList<>(food.getMydish().values());
        for (UserInfo userObject : listUsersMyDish) {
            if (DataStoreManager.getUser().getEmail().equals(userObject.getEmailUser())) {
                userInfo = userObject;
                break;
            }
        }
        return userInfo;
    }
    //check xem món ăn có phải món ăn của mình không
    public static boolean isMyDish(Food food) {
        if (food.getMydish() == null || food.getMydish().isEmpty()) return false;
        // Create a list of users from the values of mydish
        List<UserInfo> listUsersMyDish = new ArrayList<>(food.getMydish().values());
        if (listUsersMyDish.isEmpty()) return false;
        for (UserInfo userInfo : listUsersMyDish) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isHistoryFood(Food food) {
        if (food.getHistory() == null || food.getHistory().isEmpty()) return false;
        List<UserInfo> listUsersHistory = new ArrayList<>(food.getHistory().values());
        if (listUsersHistory.isEmpty()) return false;
        for (UserInfo userInfo : listUsersHistory) {
            if (DataStoreManager.getUser().getEmail().equals(userInfo.getEmailUser())) {
                return true;
            }
        }
        return false;
    }

    public static int encodeEmailUser() {
        int hashCode = DataStoreManager.getUser().getEmail().hashCode();
        if (hashCode < 0) {
            hashCode = hashCode * -1;
        }
        return hashCode;
    }
}
