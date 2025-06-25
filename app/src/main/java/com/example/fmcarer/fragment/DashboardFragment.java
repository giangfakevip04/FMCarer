package com.example.fmcarer.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fmcarer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DashboardFragment extends Fragment {
    public DashboardFragment() {
        super(R.layout.fragment_dashboard); // XML từ activity_dashboard.xml cũ
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView dateText = view.findViewById(R.id.textCurrentDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        dateText.setText(sdf.format(new Date()));
    }
}
