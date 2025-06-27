package com.example.fmcarer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fmcarer.R;
import com.example.fmcarer.model.CareSchedule;

import java.util.List;

public class CareScheduleAdapter extends RecyclerView.Adapter<CareScheduleAdapter.ViewHolder> {
    private List<CareSchedule> schedules;
    private Context context;
    private OnScheduleStatusChangeListener listener;

    public interface OnScheduleStatusChangeListener {
        void onScheduleStatusChanged(CareSchedule schedule, boolean isCompleted);
    }

    public CareScheduleAdapter(Context context, List<CareSchedule> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    public void setOnScheduleStatusChangeListener(OnScheduleStatusChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_care_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CareSchedule schedule = schedules.get(position);

        holder.childName.setText(schedule.getChildName());
        holder.timeRange.setText(schedule.getTimeRange());
        holder.duration.setText(schedule.getDuration());

        // Set avatar (có thể dùng Glide hoặc Picasso)
        // Glide.with(context).load(schedule.getChildAvatar()).into(holder.childAvatar);

        // Cập nhật trạng thái hoàn thành
        updateItemStatus(holder, schedule);

        holder.itemView.setOnClickListener(v -> {
            // Toggle trạng thái hoàn thành
            onScheduleClick(schedule, position);
        });
    }

    private void updateItemStatus(ViewHolder holder, CareSchedule schedule) {
        if (schedule.isCompleted()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_schedule_item_completed);
            holder.statusIcon.setVisibility(View.VISIBLE);
            holder.childName.setTextColor(Color.parseColor("#059669"));
            holder.timeRange.setTextColor(Color.parseColor("#047857"));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_schedule_item);
            holder.statusIcon.setVisibility(View.GONE);
            holder.childName.setTextColor(Color.parseColor("#1F2937"));
            holder.timeRange.setTextColor(Color.parseColor("#6B7280"));
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    private void onScheduleClick(CareSchedule schedule, int position) {
        // Toggle completed status
        boolean newStatus = !schedule.isCompleted();
        schedule.setCompleted(newStatus);
        notifyItemChanged(position);

        // Callback để cập nhật UI trong fragment
        if (listener != null) {
            listener.onScheduleStatusChanged(schedule, newStatus);
        }
    }

    public void updateSchedules(List<CareSchedule> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView childAvatar, statusIcon;
        TextView childName, timeRange, duration;

        ViewHolder(View itemView) {
            super(itemView);
            childAvatar = itemView.findViewById(R.id.childAvatar);
            childName = itemView.findViewById(R.id.childName);
            timeRange = itemView.findViewById(R.id.timeRange);
            duration = itemView.findViewById(R.id.duration);
            statusIcon = itemView.findViewById(R.id.statusIcon);
        }
    }
}