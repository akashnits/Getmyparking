package com.example.akash.getmyparking.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;



public class GMPSharedPrefs {

    private static final Gson sGson = new Gson();
    private static final String GMP_PHOTOS_PREF_SET= "photos";

    public GMPSharedPrefs() {
    }

    public static void setGmpPhotosPrefSet(Context context, LinkedHashSet<String> stringSet){
        setStringPreference(context, GMP_PHOTOS_PREF_SET, sGson.toJson(stringSet));
    }

    public static LinkedHashSet<String> getGmpPhotosPrefSet(Context context){
        String selectedPrefs = getStringPreference(context, GMP_PHOTOS_PREF_SET, "");
        if (!selectedPrefs.equals("")) {
            Type type = new TypeToken<LinkedHashSet<String>>() {
            }.getType();
            return sGson.fromJson(selectedPrefs, type);
        } else {
            return new LinkedHashSet<>();
        }
    }


    private static void setStringPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }

    private static String getStringPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue);
    }
}
