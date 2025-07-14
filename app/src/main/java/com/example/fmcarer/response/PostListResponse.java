package com.example.fmcarer.response;

import com.example.fmcarer.model.Post;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostListResponse {
    private boolean success;
    private String message;

    @SerializedName("posts") // ⚠️ Đảm bảo backend trả về key này
    private List<Post> posts;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
