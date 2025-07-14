package com.example.fmcarer.response;

import com.example.fmcarer.model.CareSchedule;

public class SingleCareScheduleResponse {
    private boolean success;
    private CareSchedule data;
    private String message; // Thêm trường message
    private Object error;   // Thêm trường error (có thể là Map<String, Object> hoặc Object tùy cấu trúc lỗi)

    public boolean isSuccess() {
        return success;
    }

    public CareSchedule getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public Object getError() { // Hoặc Map<String, Object> nếu lỗi là JSON object
        return error;
    }
}
