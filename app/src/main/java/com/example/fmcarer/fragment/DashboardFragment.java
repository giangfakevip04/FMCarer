package com.example.fmcarer.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.R;
import com.example.fmcarer.adapter.CareScheduleAdapter;
import com.example.fmcarer.model.CareSchedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {
    private TextView textCurrentDate;
    private Button btnToday;
    private LinearLayout weeklyCalendarContainer;
    private RecyclerView recyclerSchedules;
    private TextView warningText;
    private LinearLayout warningContainer;

    private CareScheduleAdapter scheduleAdapter;
    private List<CareSchedule> allSchedules;
    private int selectedDayPosition = -1;
    private Calendar selectedDate;

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupData();
        updateCurrentDate();
        setupWeeklyCalendar();
        setupRecyclerView();
        loadTodaySchedule();
        checkScheduleConflicts();
        updateTodayStatistics();
    }

    private void initViews(View view) {
        textCurrentDate = view.findViewById(R.id.textCurrentDate);
        btnToday = view.findViewById(R.id.btnToday);
        weeklyCalendarContainer = view.findViewById(R.id.weeklyCalendarContainer);
        recyclerSchedules = view.findViewById(R.id.recyclerSchedules);
        warningContainer = view.findViewById(R.id.warningContainer);
        warningText = view.findViewById(R.id.warningText);

        btnToday.setOnClickListener(v -> scrollToToday());
    }

    private void setupData() {
        allSchedules = new ArrayList<>();
        selectedDate = Calendar.getInstance();

        // Dữ liệu mẫu - sau này sẽ lấy từ database
        Calendar today = Calendar.getInstance();

        // Thêm lịch cho hôm nay
        allSchedules.add(new CareSchedule("Bé Ngọc Lan", "", "08:00", "10:00", "Ăn sáng", today.getTime()));
        allSchedules.add(new CareSchedule("Bé Minh Khang", "", "09:30", "11:00", "Tắm rửa", today.getTime()));
        allSchedules.add(new CareSchedule("Bé Ngọc Lan", "", "12:00", "13:30", "Ăn trưa", today.getTime()));

        // Thêm lịch cho ngày mai
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        allSchedules.add(new CareSchedule("Bé Minh Khang", "", "08:30", "10:00", "Học tập", tomorrow.getTime()));
        allSchedules.add(new CareSchedule("Bé Ngọc Lan", "", "14:00", "15:00", "Ngủ trưa", tomorrow.getTime()));
    }

    private void updateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        String currentDate = dateFormat.format(new Date());
        textCurrentDate.setText(currentDate);
    }

    private void setupWeeklyCalendar() {
        Calendar calendar = Calendar.getInstance();

        // Tìm thứ 2 của tuần hiện tại
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        String[] daysOfWeek = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

        weeklyCalendarContainer.removeAllViews();

        Calendar today = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            LinearLayout dayItem = createDayItem(
                    daysOfWeek[i],
                    dayFormat.format(calendar.getTime()),
                    i,
                    isSameDay(calendar, today),
                    (Calendar) calendar.clone()
            );
            weeklyCalendarContainer.addView(dayItem);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private LinearLayout createDayItem(String dayName, String dayNumber, int position, boolean isToday, Calendar itemDate) {
        LinearLayout dayItem = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) (56 * getResources().getDisplayMetrics().density),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);
        dayItem.setLayoutParams(params);
        dayItem.setOrientation(LinearLayout.VERTICAL);
        dayItem.setGravity(Gravity.CENTER);
        dayItem.setPadding(12, 12, 12, 12);

        // Highlight ngày hôm nay
        if (isToday) {
            dayItem.setBackgroundResource(R.drawable.bg_selected_day);
            selectedDayPosition = position;
        } else {
            dayItem.setBackgroundResource(R.drawable.bg_unselected_day);
        }

        dayItem.setOnClickListener(v -> {
            selectDay(position, itemDate);
        });

        TextView dayNameText = new TextView(getContext());
        dayNameText.setText(dayName);
        dayNameText.setTextColor(isToday ? Color.WHITE : Color.parseColor("#888888"));
        dayNameText.setTextSize(12);

        TextView dayNumberText = new TextView(getContext());
        dayNumberText.setText(dayNumber);
        dayNumberText.setTextColor(isToday ? Color.WHITE : Color.BLACK);
        dayNumberText.setTextSize(18);
        dayNumberText.setTypeface(null, Typeface.BOLD);

        dayItem.addView(dayNameText);
        dayItem.addView(dayNumberText);

        return dayItem;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void selectDay(int position, Calendar date) {
        // Update UI
        updateDaySelection(position);

        // Update selected date
        selectedDate = date;

        // Load schedule for selected day
        loadScheduleForDay(date);
    }

    private void updateDaySelection(int newPosition) {
        if (selectedDayPosition != -1 && selectedDayPosition < weeklyCalendarContainer.getChildCount()) {
            LinearLayout oldItem = (LinearLayout) weeklyCalendarContainer.getChildAt(selectedDayPosition);
            oldItem.setBackgroundResource(R.drawable.bg_unselected_day);

            TextView oldDayName = (TextView) oldItem.getChildAt(0);
            TextView oldDayNumber = (TextView) oldItem.getChildAt(1);
            oldDayName.setTextColor(Color.parseColor("#888888"));
            oldDayNumber.setTextColor(Color.BLACK);
        }

        // Highlight new selection
        LinearLayout newItem = (LinearLayout) weeklyCalendarContainer.getChildAt(newPosition);
        newItem.setBackgroundResource(R.drawable.bg_selected_day);

        TextView newDayName = (TextView) newItem.getChildAt(0);
        TextView newDayNumber = (TextView) newItem.getChildAt(1);
        newDayName.setTextColor(Color.WHITE);
        newDayNumber.setTextColor(Color.WHITE);

        selectedDayPosition = newPosition;
    }

    private void setupRecyclerView() {
        scheduleAdapter = new CareScheduleAdapter(getContext(), new ArrayList<>());
        scheduleAdapter.setOnScheduleStatusChangeListener(this::onScheduleStatusChanged);
        recyclerSchedules.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSchedules.setAdapter(scheduleAdapter);
    }

    private void onScheduleStatusChanged(CareSchedule schedule, boolean isCompleted) {
        // Cập nhật thống kê
        updateTodayStatistics();

        // Kiểm tra lại conflicts
        checkScheduleConflicts();

        // Lưu vào database nếu cần
        // saveScheduleStatus(schedule);
    }

    private void updateTodayStatistics() {
        Calendar today = Calendar.getInstance();
        int totalToday = 0;
        int completedToday = 0;

        for (CareSchedule schedule : allSchedules) {
            Calendar scheduleDate = Calendar.getInstance();
            scheduleDate.setTime(schedule.getScheduleDate());

            if (isSameDay(today, scheduleDate)) {
                totalToday++;
                if (schedule.isCompleted()) {
                    completedToday++;
                }
            }
        }

        TextView totalSchedulesToday = getView().findViewById(R.id.totalSchedulesToday);
        TextView completedSchedulesToday = getView().findViewById(R.id.completedSchedulesToday);

        if (totalSchedulesToday != null) {
            totalSchedulesToday.setText(String.valueOf(totalToday));
        }
        if (completedSchedulesToday != null) {
            completedSchedulesToday.setText(String.valueOf(completedToday));
        }
    }

    private void loadTodaySchedule() {
        Calendar today = Calendar.getInstance();
        loadScheduleForDay(today);
    }

    private void loadScheduleForDay(Calendar date) {
        List<CareSchedule> daySchedules = new ArrayList<>();

        for (CareSchedule schedule : allSchedules) {
            Calendar scheduleDate = Calendar.getInstance();
            scheduleDate.setTime(schedule.getScheduleDate());

            if (isSameDay(date, scheduleDate)) {
                daySchedules.add(schedule);
            }
        }

        scheduleAdapter.updateSchedules(daySchedules);

        // Cập nhật tiêu đề
        updateScheduleTitle(date);
    }

    private void updateScheduleTitle(Calendar date) {
        TextView titleSchedule = getView().findViewById(R.id.titleSchedule);
        Calendar today = Calendar.getInstance();

        if (isSameDay(date, today)) {
            titleSchedule.setText("Hôm nay");
        } else {
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);

            if (isSameDay(date, tomorrow)) {
                titleSchedule.setText("Ngày mai");
            } else {
                SimpleDateFormat titleFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                titleSchedule.setText(titleFormat.format(date.getTime()));
            }
        }
    }

    private void scrollToToday() {
        Calendar today = Calendar.getInstance();

        // Tìm vị trí của hôm nay trong tuần
        Calendar weekStart = Calendar.getInstance();
        int dayOfWeek = weekStart.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - Calendar.MONDAY;
        weekStart.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        for (int i = 0; i < 7; i++) {
            if (isSameDay(weekStart, today)) {
                selectDay(i, today);
                break;
            }
            weekStart.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void checkScheduleConflicts() {
        // Kiểm tra lịch trùng giờ
        Calendar today = Calendar.getInstance();
        List<CareSchedule> todaySchedules = new ArrayList<>();

        for (CareSchedule schedule : allSchedules) {
            Calendar scheduleDate = Calendar.getInstance();
            scheduleDate.setTime(schedule.getScheduleDate());

            if (isSameDay(today, scheduleDate)) {
                todaySchedules.add(schedule);
            }
        }

        boolean hasConflict = checkTimeConflicts(todaySchedules);

        if (hasConflict) {
            warningContainer.setVisibility(View.VISIBLE);
            warningText.setText("Lịch hôm nay bị trùng giờ. Hãy kiểm tra lại!");
        } else {
            warningContainer.setVisibility(View.GONE);
        }
    }

    private boolean checkTimeConflicts(List<CareSchedule> schedules) {
        if (schedules.size() < 2) return false;

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i < schedules.size() - 1; i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                try {
                    CareSchedule schedule1 = schedules.get(i);
                    CareSchedule schedule2 = schedules.get(j);

                    Date start1 = timeFormat.parse(schedule1.getStartTime());
                    Date end1 = timeFormat.parse(schedule1.getEndTime());
                    Date start2 = timeFormat.parse(schedule2.getStartTime());
                    Date end2 = timeFormat.parse(schedule2.getEndTime());

                    // Kiểm tra trùng lặp thời gian
                    if (!(end1.before(start2) || start1.after(end2))) {
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}