package com.example.fmcarer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.MainActivity;
import com.example.fmcarer.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kiểm tra khởi tạo Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            android.util.Log.e(TAG, "Firebase không khởi tạo! Kiểm tra google-services.json.");
        } else {
            android.util.Log.d(TAG, "Firebase khởi tạo thành công.");
        }

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Email không hợp lệ");
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError("Mật khẩu phải ít nhất 6 ký tự");
                    return;
                }

                loginUser(email, password);
            }
        });
    }

    public void goToRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void loginUser(String email, String password) {
        android.util.Log.d(TAG, "Bắt đầu đăng nhập với email: " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        android.util.Log.d(TAG, "Đăng nhập thành công");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        android.util.Log.w(TAG, "Đăng nhập thất bại", task.getException());
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        android.util.Log.w(TAG, "Chi tiết lỗi: " + errorMessage);
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}