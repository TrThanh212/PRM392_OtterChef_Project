package com.app.cookbook.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save the language preference
        SharedPreferences.Editor editor = context.getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
        editor.putString("language", languageCode);
        editor.apply();
    }
    public static void applySavedLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Default to Vietnamese
        LocaleHelper.setLocale(context, languageCode);
    }
}