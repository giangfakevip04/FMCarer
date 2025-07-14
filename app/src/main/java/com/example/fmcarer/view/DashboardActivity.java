package com.example.fmcarer.view;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.fmcarer.R;
import com.example.fmcarer.fragment.AccountSubCreateFragment;
import com.example.fmcarer.fragment.CareScheduleFragment;
import com.example.fmcarer.fragment.ChildrenListFragment;
import com.example.fmcarer.fragment.HomeFragment;
import com.example.fmcarer.fragment.ProfileFragment;
import com.example.fmcarer.fragment.TopUpFragment;
import com.example.fmcarer.model_call_api.UserUpdateRequest;
import com.example.fmcarer.network.ApiClient;
import com.example.fmcarer.network.ApiService;
import com.example.fmcarer.response.UserResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private Uri selectedImageUri;
    private ImageView imgAvatar; // Used in the dialog
    private TextView toolbarTitle;

    private ImageView navAvatar;
    private TextView navUsername, navEmail;

    // Declare ApiService at class level if it's used across multiple methods
    private ApiService apiService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        navAvatar = headerView.findViewById(R.id.imgAvatar);
        navUsername = headerView.findViewById(R.id.txtUsername);
        navEmail = headerView.findViewById(R.id.txtEmail);

        // Initialize ApiService for authenticated calls
        // This is done once for the activity.
        this.apiService = ApiClient.getInstance(this).create(ApiService.class);

        loadHeaderData();

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment(), "Trang chủ");
            } else if (id == R.id.account_sub_create) {
                replaceFragment(new AccountSubCreateFragment(), "Tạo tài khoản phụ");
            } else if (id == R.id.nav_schedule) {
                replaceFragment(new CareScheduleFragment(), "Lịch chăm sóc");
            } else if (id == R.id.nav_profile) {
                replaceFragment(new ProfileFragment(), "Thông tin cá nhân");
            }
            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_top_up) {
                replaceFragment(new TopUpFragment(), "Nạp tiền");
            } else if (id == R.id.nav_schedule) {
                replaceFragment(new CareScheduleFragment(), "Lịch chăm sóc");
            } else if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment(), "Trang chủ");
            } else if (id == R.id.account_sub_create) {
                replaceFragment(new AccountSubCreateFragment(), "Tạo tài khoản phụ");
            } else if (id == R.id.nav_account) {
                replaceFragment(new ProfileFragment(), "Thông tin cá nhân");
            } else if (id == R.id.nav_children) {
                replaceFragment(new ChildrenListFragment(), "Trẻ đang theo dõi");
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment(), "Trang chủ");
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);

        // 🛡️ Kiểm tra token
        String token = prefs.getString("token", "");
        if (token == null || token.isEmpty()) {
            // If token is missing, redirect to Login_Activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return; // Important: return after starting new activity to prevent further execution
        }

        String fullname = prefs.getString("fullname", "");
        String phone = prefs.getString("numberphone", "");
        String image = prefs.getString("image", "");

        // Show update dialog if user info is incomplete
        if (fullname.isEmpty() || phone.isEmpty() || image.isEmpty()) {
            new Handler().postDelayed(this::showUpdateDialog, 1000);
        }
    }

    /**
     * Loads user data into the navigation drawer header.
     */
    private void loadHeaderData() {
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String name = prefs.getString("fullname", "Tên chưa cập nhật");
        String email = prefs.getString("email", "Email chưa cập nhật");
        String image = prefs.getString("image", "");

        navUsername.setText(name);
        navEmail.setText(email);
        if (!image.isEmpty()) {
            Glide.with(this).load(image).placeholder(R.drawable.taikhoan).error(R.drawable.taikhoan).into(navAvatar);
        } else {
            // Set a default image if currentImage is empty
            Glide.with(this).load(R.drawable.taikhoan).into(navAvatar);
        }
    }

    /**
     * Replaces the current fragment in the container and updates the toolbar title.
     * @param fragment The new fragment to display.
     * @param title The title for the toolbar.
     */
    private void replaceFragment(androidx.fragment.app.Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    /**
     * Displays a dialog for users to update their profile information.
     */
    private void showUpdateDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_information_update);
        dialog.setCancelable(true); // Allow dialog to be dismissed by back button or outside touch

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_background));
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }

        MaterialButton btnUpdate = dialog.findViewById(R.id.btnUpdate);
        MaterialButton btnBack = dialog.findViewById(R.id.btnBack);
        MaterialButton btnChangeAvatar = dialog.findViewById(R.id.btnChangeAvatar);
        TextInputEditText edtFullname = dialog.findViewById(R.id.edtFullname);
        TextInputEditText edtPhone = dialog.findViewById(R.id.edtPhone);
        imgAvatar = dialog.findViewById(R.id.imgAvatar); // Assign to class-level variable

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = prefs.getString("_id", null);
        String currentName = prefs.getString("fullname", "");
        String currentPhone = prefs.getString("numberphone", "");
        String currentImage = prefs.getString("image", "");
        // String token = prefs.getString("token", ""); // Token is already available via class-level apiService

        edtFullname.setText(currentName);
        edtPhone.setText(currentPhone);
        if (!currentImage.isEmpty()) {
            Glide.with(this).load(currentImage).into(imgAvatar);
        } else {
            // Set a default image if currentImage is empty
            Glide.with(this).load(R.drawable.taikhoan).into(imgAvatar);
        }

        btnBack.setOnClickListener(v -> dialog.dismiss());

        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnUpdate.setOnClickListener(v -> {
            String name = edtFullname.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId == null) {
                Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
                return;
            }

            // If a new image was selected, use its URI; otherwise, use the current image URL
            String imageUrl = selectedImageUri != null ? selectedImageUri.toString() : currentImage;
            UserUpdateRequest request = new UserUpdateRequest(userId, name, phone, imageUrl);

            // ✅ Use the class-level apiService instance which is already authenticated
            // ApiService apiService = RetrofitClient.getInstance(this).create(ApiService.class); // REMOVE THIS LINE
            Call<UserResponse> call = apiService.updateUser(request); // Use the existing apiService

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(DashboardActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                        // Update SharedPreferences with new data
                        prefs.edit()
                                .putString("fullname", name)
                                .putString("numberphone", phone)
                                .putString("image", imageUrl)
                                .apply();

                        loadHeaderData(); // Reload header data to reflect changes
                        dialog.dismiss();
                    } else {
                        String errorMessage = "Lỗi cập nhật.";
                        try {
                            if (response.errorBody() != null) {
                                // Attempt to parse error message from errorBody
                                String errorBodyString = response.errorBody().string();
                                JSONObject jObjError = new JSONObject(errorBodyString);
                                if (jObjError.has("message")) {
                                    errorMessage += " " + jObjError.getString("message");
                                }
                            }
                        } catch (Exception e) {
                            Log.e("DashboardActivity", "Error parsing error body: " + e.getMessage());
                        }
                        Toast.makeText(DashboardActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(DashboardActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("DashboardActivity", "API call failed: " + t.getMessage(), t);
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Load selected image into the dialog's ImageView
            if (imgAvatar != null) { // Ensure imgAvatar is initialized
                Glide.with(this).load(selectedImageUri).into(imgAvatar);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}