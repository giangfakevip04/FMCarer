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


public class ChildFragment extends Fragment {
    public ChildFragment() {
        super(R.layout.fragment_child); // từ layout bạn mới cung cấp
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = view.findViewById(R.id.fabAddChild);
        fab.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Thêm trẻ mới", Toast.LENGTH_SHORT).show();
        });
    }
}