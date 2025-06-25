package com.example.fmcarer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmcarer.R;
import com.example.fmcarer.network.ApiClient;
import com.example.fmcarer.network.ApiService;
import com.example.fmcarer.network.request.RegisterRequest;
import com.example.fmcarer.network.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText inputEmail, inputPassword, inputConfirmPassword;
    Button btnRegister;
    TextView textSignIn, textPasswordError;

    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textSignIn = findViewById(R.id.textSignIn);
        textPasswordError = findViewById(R.id.textPasswordError);

        apiService = ApiClient.getService();

        textSignIn.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirm = inputConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                textPasswordError.setVisibility(View.VISIBLE);
                return;
            } else {
                textPasswordError.setVisibility(View.GONE);
            }

            btnRegister.setEnabled(false);

            apiService.register(new RegisterRequest(email, password))
                    .enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            btnRegister.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Về lại LoginActivity
                            } else {
                                Toast.makeText(RegisterActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            btnRegister.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
