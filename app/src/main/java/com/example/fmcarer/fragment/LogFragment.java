package com.example.fmcarer.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fmcarer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LogFragment extends Fragment {
    public LogFragment() {
        super(R.layout.fragment_log); // từ layout nhật ký bạn gửi
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = view.findViewById(R.id.fabAddLog);
        fab.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thêm nhật ký", Toast.LENGTH_SHORT).show();
        });
    }
}