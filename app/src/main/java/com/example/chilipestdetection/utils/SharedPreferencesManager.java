package com.example.chilipestdetection.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TYPE_USER = "type_user";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserData(String username, String password, String typeUser) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_TYPE_USER, typeUser);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
//        editor.apply();
        editor.commit();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    public String getTypeUser() {
        return sharedPreferences.getString(KEY_TYPE_USER, "");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearUserData() {
        editor.clear();
        editor.apply();
    }
}