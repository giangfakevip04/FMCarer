package com.example.fmcarer.model_call_api;

import com.google.gson.annotations.SerializedName;

public class TopUpInitiateRequest {
    @SerializedName("amount")
    private long amount; // Số tiền nạp
    @SerializedName("payment_method")
    private String paymentMethod; // "Momo" hoặc "ZaloPay"

    public TopUpInitiateRequest(long amount, String paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
