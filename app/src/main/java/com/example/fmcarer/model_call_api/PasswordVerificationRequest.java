package com.example.fmcarer.model_call_api;

import com.google.gson.annotations.SerializedName;

public class PasswordVerificationRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("password")
    private String password;

    public PasswordVerificationRequest(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}