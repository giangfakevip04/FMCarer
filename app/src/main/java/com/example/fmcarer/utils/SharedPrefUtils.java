package com.example.fmcarer.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {
    private static final String PREF_NAME = "fmcarer";

    public static void saveToken(Context context, String token) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString("token", token).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("token", null);
    }

    public static void saveEmail(Context context, String email) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString("email", email).apply();
    }

    public static String getEmail(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("email", null);
    }

    public static void saveRole(Context context, String role) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putString("role", role).apply();
    }

    public static String getRole(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString("role", null);
    }

    public static void clear(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().clear().apply();
    }
}

