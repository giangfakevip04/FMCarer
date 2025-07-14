package com.example.fmcarer.response;

import com.example.fmcarer.model.CareSchedule;

import java.util.List;

public class CareScheduleResponse {
    private boolean success;
    private List<CareSchedule> data;
    private String message; // ⬅️ thêm message để dễ debug nếu cần

    public boolean isSuccess() {
        return success;
    }

    public List<CareSchedule> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
