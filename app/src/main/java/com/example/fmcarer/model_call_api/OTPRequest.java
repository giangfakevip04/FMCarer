package com.example.fmcarer.model_call_api;

public class OTPRequest {
    private String email;

    public OTPRequest(String email) {
        this.email = email;
    }

    // Getter
    public String getEmail() {
        return email;
    }
}
