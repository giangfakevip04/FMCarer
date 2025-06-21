package com.example.fmcarer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Kiểm tra khởi tạo Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            android.util.Log.e(TAG, "Firebase không khởi tạo! Kiểm tra google-services.json.");
        } else {
            android.util.Log.d(TAG, "Firebase khởi tạo thành công.");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                android.util.Log.d(TAG, "Đăng ký với: Email=" + email + ", Password=" + password + ", Confirm=" + confirmPassword);

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Email không hợp lệ");
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError("Mật khẩu phải ít nhất 6 ký tự");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Mật khẩu không khớp");
                    return;
                }

                createAccount(email, password);
            }
        });
    }

    private void createAccount(String email, String password) {
        android.util.Log.d(TAG, "Bắt đầu tạo tài khoản với email: " + email);
        android.util.Log.d(TAG, "Firebase Auth instance: " + (mAuth != null ? "Initialized" : "Not Initialized"));
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        android.util.Log.d(TAG, "Tạo tài khoản thành công");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            android.util.Log.d(TAG, "UID của người dùng: " + user.getUid());
                            saveUserDataToFirestore();
                            // Chuyển về LoginActivity sau khi đăng ký thành công
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        android.util.Log.w(TAG, "Tạo tài khoản thất bại", task.getException());
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                        android.util.Log.w(TAG, "Chi tiết lỗi: " + errorMessage);
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUid());
            userData.put("email", user.getEmail());
            userData.put("role", "parent");
            userData.put("status", "active");
            userData.put("createdAt", new Date());

            android.util.Log.d(TAG, "Lưu dữ liệu Firestore cho UID: " + user.getUid());
            db.collection("users").document(user.getUid()).set(userData)
                    .addOnSuccessListener(aVoid -> android.util.Log.d(TAG, "Dữ liệu người dùng đã lưu"))
                    .addOnFailureListener(e -> android.util.Log.w(TAG, "Lỗi lưu dữ liệu Firestore", e));
        }
    }
}