package com.example.fmcarer.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CareSchedule {
    private String childName;
    private String childAvatar;
    private String startTime;
    private String endTime;
    private String activity;
    private String duration;
    private Date scheduleDate;
    private boolean isCompleted;

    // Constructor
    public CareSchedule(String childName, String childAvatar, String startTime,
                        String endTime, String activity, Date scheduleDate) {
        this.childName = childName;
        this.childAvatar = childAvatar;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
        this.scheduleDate = scheduleDate;
        this.isCompleted = false;

        // Tính duration
        this.duration = calculateDuration(startTime, endTime);
    }

    private String calculateDuration(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            long diffInMillis = endDate.getTime() - startDate.getTime();
            long hours = diffInMillis / (1000 * 60 * 60);
            long minutes = (diffInMillis % (1000 * 60 * 60)) / (1000 * 60);

            if (hours > 0) {
                return hours + " giờ" + (minutes > 0 ? " " + minutes + " phút" : "");
            } else {
                return minutes + " phút";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    // Getters and setters
    public String getChildName() { return childName; }
    public String getChildAvatar() { return childAvatar; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getActivity() { return activity; }
    public String getDuration() { return duration; }
    public Date getScheduleDate() { return scheduleDate; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public String getTimeRange() {
        return startTime + " - " + endTime + " (" + activity + ")";
    }
}
