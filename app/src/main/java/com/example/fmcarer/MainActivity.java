package com.example.fmcarer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.activity.LoginActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kiểm tra khởi tạo Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            android.util.Log.e(TAG, "Firebase không khởi tạo! Kiểm tra google-services.json.");
        } else {
            android.util.Log.d(TAG, "Firebase khởi tạo thành công.");
        }

        mAuth = FirebaseAuth.getInstance();

//        Button logoutButton = findViewById(R.id.logoutButton); // Chưa có trong layout hiện tại
        Button addChildButton = findViewById(R.id.addChildButton);

//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                android.util.Log.d(TAG, "Đăng xuất thành công");
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                finish();
//            }
//        });

        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d(TAG, "Nhấn nút Thêm mới");
                // Thêm logic cho việc thêm trẻ em (ví dụ: chuyển sang Activity khác)
                // startActivity(new Intent(MainActivity.this, AddChildActivity.class));
            }
        });
    }
}