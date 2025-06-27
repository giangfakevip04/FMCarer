package com.example.fmcarer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fmcarer.fragment.ChildFragment;
import com.example.fmcarer.fragment.DashboardFragment;
import com.example.fmcarer.fragment.LogFragment;
import com.example.fmcarer.fragment.NotificationFragment;
import com.example.fmcarer.fragment.PostListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        // Mặc định là DashboardFragment
        loadFragment(new DashboardFragment());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_visits) {
                fragment = new DashboardFragment();
            } else if (id == R.id.nav_children) {
                fragment = new ChildFragment();
            } else if (id == R.id.nav_logs) {
                fragment = new LogFragment();
            } else if (id == R.id.nav_posts) {
                fragment = new PostListFragment();
            } else if (id == R.id.nav_notifications) {
                fragment = new NotificationFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}

