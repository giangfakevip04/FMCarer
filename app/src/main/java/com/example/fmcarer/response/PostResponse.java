package com.example.fmcarer.response;

import com.example.fmcarer.model.Post;
import com.google.gson.annotations.SerializedName;

public class PostResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("post") // Khớp với key JSON server trả về: "post": {...}
    private Post post;

    // Getter cho success
    public boolean isSuccess() {
        return success;
    }

    // Getter cho message
    public String getMessage() {
        return message;
    }

    // Getter cho post
    public Post getPost() {
        return post;
    }
}
