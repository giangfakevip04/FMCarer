package com.example.fmcarer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.MainActivity;
import com.example.fmcarer.R;
import com.example.fmcarer.network.ApiClient;
import com.example.fmcarer.network.ApiService;
import com.example.fmcarer.network.request.LoginRequest;
import com.example.fmcarer.network.response.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText inputEmail, inputPassword;
    Button btnSignIn;
    TextView textSignUp;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        textSignUp = findViewById(R.id.textSignUp);

        apiService = ApiClient.getService();

        textSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        btnSignIn.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSignIn.setEnabled(false);

            apiService.login(new LoginRequest(email, password))
                    .enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            btnSignIn.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null) {
                                SharedPreferences prefs = getSharedPreferences("fmcarer", MODE_PRIVATE);
                                prefs.edit()
                                        .putString("token", response.body().getToken())
                                        .putString("email", response.body().getUser().getEmail())
                                        .putString("role", response.body().getUser().getRole())
                                        .apply();

                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                // 👉 Đổi DashboardActivity thành MainActivity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            btnSignIn.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
