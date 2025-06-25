package com.example.fmcarer.network.request;

public class RegisterRequest {
    private String email;
    private String password;
    private String role;

    public RegisterRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.role = "main"; // mặc định
    }
}

