package com.example.fmcarer.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

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

        // Thiết lập chức năng ẩn/hiện mật khẩu
        setupPasswordToggle();
        setupConfirmPasswordToggle();

        textSignIn.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirm = inputConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                textPasswordError.setVisibility(View.VISIBLE);
                // Đổi background của confirm password thành màu đỏ
                inputConfirmPassword.setBackgroundResource(R.drawable.bg_edittext_error);
                return;
            } else {
                textPasswordError.setVisibility(View.GONE);
                // Đổi lại background bình thường
                inputConfirmPassword.setBackgroundResource(R.drawable.bg_edittext_outline);
            }

            btnRegister.setEnabled(false);
            btnRegister.setText("Đang đăng ký...");

            apiService.register(new RegisterRequest(email, password))
                    .enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Đăng ký");

                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Về lại LoginActivity
                            } else {
                                Toast.makeText(RegisterActivity.this, "Email đã tồn tại hoặc có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Đăng ký");
                            Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setupPasswordToggle() {
        inputPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputPassword.getRight() - inputPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void setupConfirmPasswordToggle() {
        inputConfirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (inputConfirmPassword.getRight() - inputConfirmPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    toggleConfirmPasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ẩn mật khẩu
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputPassword.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.lock),
                    null,
                    ContextCompat.getDrawable(this, R.drawable.eye),
                    null
            );
            isPasswordVisible = false;
        } else {
            // Hiện mật khẩu
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            inputPassword.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.lock),
                    null,
                    ContextCompat.getDrawable(this, R.drawable.eye_off), // Bạn cần thêm icon này
                    null
            );
            isPasswordVisible = true;
        }

        // Đặt con trỏ chuột về cuối text
        inputPassword.setSelection(inputPassword.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            // Ẩn mật khẩu
            inputConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.key),
                    null,
                    ContextCompat.getDrawable(this, R.drawable.eye),
                    null
            );
            isConfirmPasswordVisible = false;
        } else {
            // Hiện mật khẩu
            inputConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            inputConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this, R.drawable.key),
                    null,
                    ContextCompat.getDrawable(this, R.drawable.eye_off), // Bạn cần thêm icon này
                    null
            );
            isConfirmPasswordVisible = true;
        }

        // Đặt con trỏ chuột về cuối text
        inputConfirmPassword.setSelection(inputConfirmPassword.getText().length());
    }
}