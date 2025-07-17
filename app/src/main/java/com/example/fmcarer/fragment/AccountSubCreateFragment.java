package com.example.fmcarer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fmcarer.R;
import com.example.fmcarer.change.FileUtils;
import com.example.fmcarer.model.User;
import com.example.fmcarer.model_call_api.SubUserRequest;
import com.example.fmcarer.network.ApiClient;
import com.example.fmcarer.network.ApiService;
import com.example.fmcarer.response.ApiResponse;
import com.example.fmcarer.response.UserResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AccountSubCreateFragment extends Fragment {

    private TextInputEditText editFullName, editPhone, editPassword, editConfirmPassword;
    private TextInputLayout layoutPassword, layoutConfirmPassword;
    private MaterialButton btnSave, btnCancel, btnChangeAvatar;
    private ImageView imgAvatar;
    private TextView tvTitle;

    private User subuserToEdit;
    private Uri selectedImageUri;
    private boolean isEditMode = false;
    private static final int REQUEST_PICK_IMAGE = 102;

    private SharedPreferences prefs;
    private String parentId;
    private String token;

    private static final String TAG = "SubUserFragment";
    private static final String PREF_USER_SESSION = "user_session";
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_AUTH_TOKEN = "token";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_subuser_form, container, false);
        initViews(view);
        loadSession();
        checkEditMode();

        btnChangeAvatar.setOnClickListener(v -> pickImage());
        btnSave.setOnClickListener(v -> handleSave());
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tvDialogTitle);
        imgAvatar = view.findViewById(R.id.imgSubuserAvatar);
        btnChangeAvatar = view.findViewById(R.id.btnChangeSubuserAvatar);
        editFullName = view.findViewById(R.id.editFullName);
        editPhone = view.findViewById(R.id.editSubPhone);
        editPassword = view.findViewById(R.id.editSubPassword);
        editConfirmPassword = view.findViewById(R.id.editConfirmSubPassword);
        layoutPassword = view.findViewById(R.id.layoutSubPassword);
        layoutConfirmPassword = view.findViewById(R.id.layoutConfirmSubPassword);
        btnSave = view.findViewById(R.id.btnSaveSubAccount);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void loadSession() {
        prefs = requireContext().getSharedPreferences(PREF_USER_SESSION, Context.MODE_PRIVATE);
        parentId = prefs.getString(KEY_USER_ID, null);
        token = prefs.getString(KEY_AUTH_TOKEN, null);

        Log.d(TAG, "Loaded parentId: " + parentId);
        Log.d(TAG, "Loaded token: " + token);

        if (TextUtils.isEmpty(parentId)) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy Parent ID. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy Token xác thực. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
        }
    }

    private void checkEditMode() {
        if (getArguments() != null) {
            subuserToEdit = (User) getArguments().getSerializable("subuser");
            isEditMode = subuserToEdit != null;
        }

        if (isEditMode) {
            tvTitle.setText("Chỉnh sửa tài khoản phụ");
            editFullName.setText(subuserToEdit.getFullname());
            editPhone.setText(subuserToEdit.getNumberphone());
            if (!TextUtils.isEmpty(subuserToEdit.getImage())) {
                Glide.with(this).load(subuserToEdit.getImage()).placeholder(R.drawable.taikhoan).into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.taikhoan);
            }
            layoutPassword.setVisibility(View.GONE);
            layoutConfirmPassword.setVisibility(View.GONE);
            btnSave.setText("Cập nhật");
        } else {
            tvTitle.setText("Thêm tài khoản phụ");
            imgAvatar.setImageResource(R.drawable.taikhoan);
            layoutPassword.setVisibility(View.VISIBLE);
            layoutConfirmPassword.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Glide.with(this).load(selectedImageUri).into(imgAvatar);
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void handleSave() {
        String fullname = Objects.requireNonNull(editFullName.getText()).toString().trim();
        String phone = Objects.requireNonNull(editPhone.getText()).toString().trim();
        String password = Objects.requireNonNull(editPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(editConfirmPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(fullname)) {
            editFullName.setError("Vui lòng nhập họ tên");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            editPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }

        if (!isEditMode) {
            if (TextUtils.isEmpty(password)) {
                editPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }
            if (!password.equals(confirmPassword)) {
                editConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                return;
            }
        } else {
            if (!TextUtils.isEmpty(password)) {
                if (!password.equals(confirmPassword)) {
                    editConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                    return;
                }
            }
        }

        if (TextUtils.isEmpty(parentId)) {
            Toast.makeText(requireContext(), "Lỗi: Parent ID không có sẵn. Vui lòng thử lại hoặc đăng nhập lại.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(requireContext(), "Lỗi: Token xác thực không có sẵn. Vui lòng thử lại hoặc đăng nhập lại.", Toast.LENGTH_LONG).show();
            return;
        }

        btnSave.setEnabled(false);

        if (selectedImageUri != null) {
            uploadImageThenSubmit(fullname, phone, password);
        } else {
            String imageUrlToSend = isEditMode && subuserToEdit != null ? subuserToEdit.getImage() : "";
            submitData(fullname, phone, password, imageUrlToSend);
        }
    }

    private void uploadImageThenSubmit(String fullname, String phone, String password) {
        String path = FileUtils.getPath(requireContext(), selectedImageUri);
        if (path == null) {
            Toast.makeText(requireContext(), "Không thể lấy đường dẫn file ảnh.", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(requireContext(), "File ảnh không tồn tại: " + path, Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(true);
            return;
        }

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("avatar", file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file));

        String uploadTargetId = isEditMode && subuserToEdit != null ? subuserToEdit.getId() : parentId;
        RequestBody targetIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), uploadTargetId);


        ApiService api = ApiClient.getInstance(requireContext()).create(ApiService.class);
        api.uploadImage(targetIdRequestBody, imagePart).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String uploadedImageUrl = response.body().getImageUrl();
                    if (uploadedImageUrl != null) {
                        submitData(fullname, phone, password, uploadedImageUrl);
                    } else {
                        Toast.makeText(requireContext(), "URL ảnh tải lên bị rỗng.", Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                    }
                } else {
                    showError("Tải ảnh thất bại", response);
                    btnSave.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối khi tải ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                Log.e(TAG, "Lỗi khi tải ảnh: " + t.getMessage(), t);
            }
        });
    }

    private void submitData(String fullname, String phone, String password, String imageUrl) {
        ApiService api = ApiClient.getInstance(requireContext()).create(ApiService.class);

        SubUserRequest request;

        if (isEditMode) {
            String finalPassword = password.isEmpty() ? null : password;

            if (subuserToEdit == null || TextUtils.isEmpty(subuserToEdit.getId())) {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID subuser để cập nhật.", Toast.LENGTH_LONG).show();
                btnSave.setEnabled(true);
                return;
            }

            request = new SubUserRequest(
                    subuserToEdit.getId(),
                    fullname,
                    phone,
                    finalPassword,
                    parentId,
                    imageUrl != null ? imageUrl : ""
            );
        } else {
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu cho tài khoản mới.", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                return;
            }

            request = new SubUserRequest(
                    fullname,
                    phone,
                    password,
                    parentId,
                    imageUrl != null ? imageUrl : ""
            );
        }

        Log.d(TAG, "Gửi SubUserRequest: " + new Gson().toJson(request));

        api.createOrUpdateSubUser("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    // THAY THẾ requireActivity().onBackPressed(); BẰNG ĐOẠN CODE DƯỚI ĐÂY
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Lỗi API: " + response.code() + " - " + errorBody);
                        JSONObject errorJson = new JSONObject(errorBody);
                        String errorMessage = errorJson.optString("message", "Lỗi không xác định từ server.");
                        Toast.makeText(requireContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi phân tích lỗi response hoặc IO:", e);
                        Toast.makeText(requireContext(), "Lỗi: " + response.code() + " - " + "Không thể đọc lỗi từ server.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối khi tạo/cập nhật subuser: " + t.getMessage(), t);
            }
        });
    }

    // --- PHƯƠNG THỨC MỚI ĐỂ CHUYỂN FRAGMENT ---


    private void showError(String defaultMsg, Response<?> response) {
        String error = defaultMsg;
        if (response.errorBody() != null) {
            try {
                JSONObject obj = new JSONObject(response.errorBody().string());
                if (obj.has("message")) error = obj.getString("message");
            } catch (Exception e) {
                Log.e(TAG, "Lỗi phân tích lỗi response", e);
            }
        }
        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Lỗi backend: " + response.code() + " - " + error);
    }
}