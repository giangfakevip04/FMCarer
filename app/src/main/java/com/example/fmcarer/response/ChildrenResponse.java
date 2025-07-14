package com.example.fmcarer.response;

import com.example.fmcarer.model.Children;

import java.util.List;

public class ChildrenResponse {
    private boolean success;
    private List<Children> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Children> getData() {
        return data;
    }
}
