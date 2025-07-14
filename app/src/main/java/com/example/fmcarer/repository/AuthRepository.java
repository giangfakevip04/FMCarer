package com.example.fmcarer.repository;

import android.content.Context;

import com.example.fmcarer.model_call_api.UserRequest;
import com.example.fmcarer.network.ApiClient;
import com.example.fmcarer.network.ApiService;
import com.example.fmcarer.response.UserResponse;

import retrofit2.Call;

public class AuthRepository {
    private final ApiService apiService;

    public AuthRepository(Context context) {
        apiService = ApiClient.getInstance(context).create(ApiService.class);
    }

    public Call<UserResponse> registerUser(String email, String password) {
        return apiService.registerUser(new UserRequest(email, password));
    }
}
