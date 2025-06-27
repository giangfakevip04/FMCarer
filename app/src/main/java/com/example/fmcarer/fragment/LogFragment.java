package com.example.fmcarer.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fmcarer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LogFragment extends Fragment {
    private RecyclerView recyclerLogs;
    private FloatingActionButton fabAddLog;

    public LogFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerLogs = view.findViewById(R.id.recyclerLogs);
        fabAddLog = view.findViewById(R.id.fabAddLog);

        recyclerLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Gắn adapter hiển thị nhật ký

        fabAddLog.setOnClickListener(v -> {
            // TODO: mở màn hình thêm nhật ký
        });
    }
}