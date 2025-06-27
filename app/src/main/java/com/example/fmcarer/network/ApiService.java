package com.example.fmcarer.network;


import com.example.fmcarer.model.Child;
import com.example.fmcarer.network.request.LoginRequest;
import com.example.fmcarer.network.request.RegisterRequest;
import com.example.fmcarer.network.request.SubUserRequest;
import com.example.fmcarer.network.response.LoginResponse;
import com.example.fmcarer.network.response.RegisterResponse;
import com.example.fmcarer.network.response.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @GET("users/profile")
    Call<UserResponse> getProfile(@Header("Authorization") String token);

    @POST("users/sub")
    Call<UserResponse> createSubUser(
            @Header("Authorization") String token,
            @Body SubUserRequest request
    );

    @GET("children")
    Call<List<Child>> getChildren(@Header("Authorization") String token);

    @POST("children")
    Call<Child> createChild(@Header("Authorization") String token, @Body Child child);

    @PUT("children/{id}")
    Call<Child> updateChild(@Header("Authorization") String token, @Path("id") String id, @Body Child child);

    @DELETE("children/{id}")
    Call<Void> deleteChild(@Header("Authorization") String token, @Path("id") String id);



}

