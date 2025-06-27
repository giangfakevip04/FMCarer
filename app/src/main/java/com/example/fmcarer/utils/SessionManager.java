package com.example.fmcarer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    // ✅ THỐNG NHẤT TÊN VỚI SharedPrefUtils
    private static final String PREF_NAME = "fmcarer"; // Đổi từ "user_session" thành "fmcarer"
    private static final String KEY_TOKEN = "token";    // Đổi từ "auth_token" thành "token"
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String token) {
        Log.d(TAG, "Saving token: " + token);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        Log.d(TAG, "Token saved and verified: " + getToken());
    }

    public String getToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        Log.d(TAG, "Getting token: " + token);
        return token;
    }

    public void saveEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void saveRole(String role) {
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public boolean isLoggedIn() {
        String token = getToken();
        boolean loggedIn = token != null && !token.isEmpty();
        Log.d(TAG, "Is logged in: " + loggedIn + " (token: " + token + ")");
        return loggedIn;
    }

    public void clearSession() {
        Log.d(TAG, "Clearing session");
        editor.clear();
        editor.apply();
    }

    // ✅ PHƯƠNG THỨC DEBUG
    public void debugSessionInfo() {
        Log.d(TAG, "=== SESSION DEBUG INFO ===");
        Log.d(TAG, "Pref name: " + PREF_NAME);
        Log.d(TAG, "Token key: " + KEY_TOKEN);
        Log.d(TAG, "Current token: " + getToken());
        Log.d(TAG, "Current email: " + getEmail());
        Log.d(TAG, "Current role: " + getRole());
        Log.d(TAG, "Is logged in: " + isLoggedIn());
        Log.d(TAG, "========================");
    }
}