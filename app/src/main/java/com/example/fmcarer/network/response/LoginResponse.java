package com.example.fmcarer.network.response;

public class LoginResponse {
    private UserResponse user;
    private String token;

    public UserResponse getUser() { return user; }
    public String getToken() { return token; }
}
